package me.hsgamer.eosclientdownloader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.hsgamer.eosclientdownloader.utils.LoggerUtils.LOGGER;

public final class ZipUtils {
    private ZipUtils() {
        // EMPTY
    }

    public static void unzip(File zipFile, File destination) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile); ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destination, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                LOGGER.info("Uncompressed to '" + newFile.getCanonicalPath() + "'");
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }
}
