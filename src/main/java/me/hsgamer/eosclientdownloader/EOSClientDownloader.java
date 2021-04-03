package me.hsgamer.eosclientdownloader;

import com.google.api.client.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.logging.*;
import java.util.stream.Stream;

public class EOSClientDownloader {
    static final Logger LOGGER = Logger.getLogger("Downloader");

    private static final MainConfig MAIN_CONFIG = new MainConfig();

    static {
        MAIN_CONFIG.setup();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                return "[" + logRecord.getLevel() + "] " + logRecord.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    public static void main(String... args) {
        String filename = MainConfig.FILE_NAME.getValue();
        String driveId = MainConfig.FILE_DRIVE_ID.getValue();
        String uncompressedPath = MainConfig.FILE_UNCOMPRESSED_PATH.getValue();

        boolean deleteExistedFiles = MainConfig.FILE_DELETE_EXISTED_UNCOMPRESSED.getValue();
        boolean deleteAfterUncompressed = MainConfig.FILE_DELETE_AFTER_UNCOMPRESSED.getValue();

        try {
            File downloadFile = new File(filename);
            if (!downloadFile.exists() && downloadFile.createNewFile()) {
                LOGGER.info("Created '" + downloadFile.getCanonicalPath() + "'");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
            InputStream downloadStream = DriveUtils.getFileAsInputStream(driveId);
            IOUtils.copy(downloadStream, fileOutputStream);
            LOGGER.info("Downloaded to '" + downloadFile.getCanonicalPath() + "'");

            File uncompressedFolder = new File(".", uncompressedPath);
            if (deleteExistedFiles && uncompressedFolder.exists()) {
                try (Stream<Path> stream = Files.walk(uncompressedFolder.toPath())) {
                    stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                }
                LOGGER.info("Deleted existed folder");
            }

            ZipUtils.unzip(downloadFile, uncompressedFolder);
            if (deleteAfterUncompressed && Files.deleteIfExists(downloadFile.toPath())) {
                LOGGER.info("Deleted '" + downloadFile.getAbsolutePath() + "'");
            }
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }
}