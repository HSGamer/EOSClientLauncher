package me.hsgamer.eosclientdownloader;

import com.google.api.services.drive.Drive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.*;

public class EOSClientDownloader {
    private static final String FILE_NAME = "EOS-Client.zip";
    private static final String FILE_DRIVE_ID = "15orxKbmGm4foV5Lpc0RYnz9Imvp3IbWF";
    private static final File UNCOMPRESSED_FOLDER = new File("Uncompressed");
    private static final Logger LOGGER = Logger.getLogger("Downloader");

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                return "[" + logRecord.getLoggerName() + "-" + logRecord.getLevel() + "] " + logRecord.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    public static void main(String... args) {
        try {
            Drive drive = DriveUtils.getService();
            File downloadFile = new File(FILE_NAME);
            if (!downloadFile.exists() && downloadFile.createNewFile()) {
                LOGGER.info("Created '" + downloadFile.getCanonicalPath() + "'");
            }
            drive.files().get(FILE_DRIVE_ID).setSupportsTeamDrives(true).executeMediaAndDownloadTo(new FileOutputStream(downloadFile));
            LOGGER.info("Downloaded to '" + downloadFile.getCanonicalPath() + "'");

            if (UNCOMPRESSED_FOLDER.exists() && UNCOMPRESSED_FOLDER.delete()) {
                LOGGER.info("Deleted existed folder");
            }
            ZipUtils.unzip(downloadFile, UNCOMPRESSED_FOLDER, LOGGER);
            if (downloadFile.exists() && downloadFile.delete()) {
                LOGGER.info("Deleted '" + downloadFile.getAbsolutePath() + "'");
            }
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }
}