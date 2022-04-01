package me.hsgamer.eosclientdownloader;

import com.google.common.io.ByteStreams;
import me.hsgamer.eosclientdownloader.config.MainConfig;
import me.hsgamer.eosclientdownloader.data.FileData;
import me.hsgamer.eosclientdownloader.utils.DriveUtils;
import me.hsgamer.eosclientdownloader.utils.FileDataUtils;
import me.hsgamer.eosclientdownloader.utils.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Comparator;
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
        boolean deleteAfterUncompressed = MainConfig.FILE_DELETE_AFTER_UNCOMPRESSED.getValue();

        try {
            File downloadFile = new File(filename);
            if (!downloadFile.exists() && downloadFile.createNewFile()) {
                LOGGER.info("Created '" + downloadFile.getCanonicalPath() + "'");
            }
            FileDataUtils.runFuture();
            DriveUtils.initService();

            FileData fileData = FileDataUtils.getAvailableData().join().get(0);
            InputStream downloadStream = DriveUtils.getFileAsInputStream(fileData.getId());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
            ByteStreams.copy(downloadStream, fileOutputStream);
            LOGGER.info("Downloaded to '" + downloadFile.getCanonicalPath() + "'");

            File uncompressedFolder = new File(".", uncompressedPath);
            if (deleteExistedFiles && uncompressedFolder.exists()) {
                try (Stream<Path> stream = Files.walk(uncompressedFolder.toPath())) {
                    stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
                LOGGER.info("Deleted existed folder");
            }

            ZipUtils.unzip(downloadFile, uncompressedFolder);
            fileOutputStream.close();
            if (deleteAfterUncompressed && Files.deleteIfExists(downloadFile.toPath())) {
                LOGGER.info("Deleted '" + downloadFile.getCanonicalPath() + "'");
            }
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }
}