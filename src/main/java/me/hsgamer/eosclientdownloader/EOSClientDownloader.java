package me.hsgamer.eosclientdownloader;

import com.google.common.io.ByteStreams;
import me.hsgamer.eosclientdownloader.config.MainConfig;
import me.hsgamer.eosclientdownloader.data.FileData;
import me.hsgamer.eosclientdownloader.utils.DriveUtils;
import me.hsgamer.eosclientdownloader.utils.Utils;
import me.hsgamer.eosclientdownloader.utils.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.stream.Stream;

import static me.hsgamer.eosclientdownloader.utils.LoggerUtils.LOGGER;

public class EOSClientDownloader {
    private static final MainConfig MAIN_CONFIG = new MainConfig();

    static {
        MAIN_CONFIG.setup();
    }

    public static void main(String... args) {
        String filename = "EOSClient.zip";
        String uncompressedPath = "Uncompressed";

        boolean deleteExistedFiles = MainConfig.FILE_DELETE_EXISTED_UNCOMPRESSED.getValue();

        try {
            File downloadFile = new File(filename);
            if (!downloadFile.exists() && downloadFile.createNewFile()) {
                LOGGER.info("Created '" + downloadFile.getCanonicalPath() + "'");
            }
            FileData fileData = askAndGet();
            String currentMd5 = Utils.getFileChecksum(downloadFile);
            if (currentMd5.equalsIgnoreCase(fileData.getMd5())) {
                LOGGER.info("The file is already downloaded");
                return;
            }
            LOGGER.info("Downloading file...");
            InputStream downloadStream = DriveUtils.getFileAsInputStream(fileData.getId());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
            float progress = 0;
            long size = fileData.getSize();
            byte[] buffer = new byte[1024];
            int read;
            System.out.print("Downloading: 0%");
            while ((read = downloadStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
                progress += read;
                System.out.print("\rDownloading: " + (progress / size * 100) + "%");
            }
            System.out.println();
            LOGGER.info("Downloaded to '" + downloadFile.getCanonicalPath() + "'");

            File uncompressedFolder = new File(uncompressedPath);
            if (deleteExistedFiles && uncompressedFolder.exists()) {
                try (Stream<Path> stream = Files.walk(uncompressedFolder.toPath())) {
                    stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
                LOGGER.info("Deleted existed folder");
            }

            List<Path> files = ZipUtils.unzip(downloadFile, uncompressedFolder);
            fileOutputStream.close();

            Path eosClient = files.stream()
                    .filter(path -> path.endsWith("EOSClient.exe"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can't find EOSClient folder"));
            System.out.println("EOSClient path: " + eosClient.toAbsolutePath());
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private static FileData askAndGet() throws GeneralSecurityException, IOException {
        List<FileData> list = DriveUtils.getFiles(MainConfig.CLIENT_FOLDER_ID.getValue());
        Scanner scanner = new Scanner(System.in);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            FileData fileData = list.get(i);
            int finalI = i;
            LOGGER.info(() -> finalI + ": " + fileData.getName());
        }
        System.out.print("Please enter the number of the file you want to download: ");
        do {
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {
                LOGGER.info("Will download the first file");
            }
        } while (index < 0 || index >= list.size());
        return list.get(index);
    }
}