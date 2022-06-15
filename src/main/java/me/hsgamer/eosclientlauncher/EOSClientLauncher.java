package me.hsgamer.eosclientlauncher;

import me.hsgamer.eosclientlauncher.config.MainConfig;
import me.hsgamer.eosclientlauncher.data.ExecuteData;
import me.hsgamer.eosclientlauncher.data.FileData;
import me.hsgamer.eosclientlauncher.executor.Executor;
import me.hsgamer.eosclientlauncher.executor.WindowsExecutor;
import me.hsgamer.eosclientlauncher.utils.DriveUtils;
import me.hsgamer.eosclientlauncher.utils.Utils;
import me.hsgamer.eosclientlauncher.utils.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

import static me.hsgamer.eosclientlauncher.utils.LoggerUtils.LOGGER;

public class EOSClientLauncher {
    private static final MainConfig MAIN_CONFIG = new MainConfig();
    private static final List<Executor> EXECUTORS = new ArrayList<>();

    static {
        MAIN_CONFIG.setup();
        EXECUTORS.add(new WindowsExecutor());
    }

    public static void main(String... args) {
        ExecuteData executeData = askAndGetExecute();
        String uncompressedPath = "Uncompressed";

        boolean deleteExistedFiles = MainConfig.FILE_DELETE_EXISTED_UNCOMPRESSED.getValue();

        try {
            File downloadFile = new File(executeData.fileName);
            if (!downloadFile.exists() && downloadFile.createNewFile()) {
                LOGGER.info("Created '" + downloadFile.getCanonicalPath() + "'");
            }
            FileData fileData = askAndGet(executeData.clientId);
            String currentMd5 = Utils.getFileChecksum(downloadFile);
            if (currentMd5.equalsIgnoreCase(fileData.getMd5())) {
                LOGGER.info("The file is already downloaded");
            } else {
                LOGGER.info("Downloading file...");
                InputStream downloadStream = DriveUtils.getFileAsInputStream(fileData.getId());
                FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
                float progress = 0;
                long size = fileData.getSize();
                byte[] buffer = new byte[1024];
                int read;
                LOGGER.info("Downloading: 0%");
                int times = 0;
                while ((read = downloadStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                    progress += read;
                    if (progress / size >= times * 0.1) {
                        float finalProgress = progress;
                        LOGGER.info(() -> "Downloading: " + (int) (finalProgress / size * 100) + "%");
                        times++;
                    }
                }
                fileOutputStream.close();
                LOGGER.info("Downloaded to '" + downloadFile.getCanonicalPath() + "'");
            }

            File uncompressedFolder = new File(uncompressedPath);
            if (deleteExistedFiles && uncompressedFolder.exists()) {
                try (Stream<Path> stream = Files.walk(uncompressedFolder.toPath())) {
                    stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
                LOGGER.info("Deleted existed folder");
            }
            List<Path> files = ZipUtils.unzip(downloadFile, uncompressedFolder);

            Path clientPath = files.stream()
                    .filter(executeData.executeFileCheck)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Can't find Client folder"));
            if (MainConfig.EXECUTE_FILE_AFTER_DOWNLOAD.getValue()) {
                Optional<Executor> executor = EXECUTORS.stream()
                        .filter(Executor::canExecute)
                        .findFirst();
                if (executor.isPresent()) {
                    executor.get().execute(clientPath, Boolean.TRUE.equals(MainConfig.AUTO_CONNECT_WIFI.getValue())).join();
                } else {
                    LOGGER.warning("Can't find any executor");
                }
            }
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static FileData askAndGet(String clientId) throws GeneralSecurityException, IOException {
        List<FileData> list = DriveUtils.getFiles(clientId);
        if (list.isEmpty()) {
            throw new IllegalStateException("Can't find any file");
        }
        if (list.size() == 1) {
            return list.get(0);
        }
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

    private static ExecuteData askAndGetExecute() {
        ExecuteData[] values = ExecuteData.values();
        for (int i = 0; i < values.length; i++) {
            ExecuteData executeData = values[i];
            int finalI = i;
            LOGGER.info(() -> finalI + ": " + executeData.name());
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the number of the execute data you want to use: ");
        int index = 0;
        do {
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {
                LOGGER.info("Will choose the first execute data");
            }
        } while (index < 0 || index >= values.length);
        return values[index];
    }
}