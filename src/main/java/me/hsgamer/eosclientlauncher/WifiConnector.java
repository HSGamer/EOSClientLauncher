package me.hsgamer.eosclientlauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class WifiConnector {
    private static final String PROFILE_FILE_NAME = "Wi-Fi-FU-Exam.xml";
    private static final String PROFILE_NAME = "FU-Exam";

    public static void connect() throws IOException, InterruptedException {
        File wifiProfile = new File(PROFILE_NAME);
        if (!wifiProfile.exists()) {
            wifiProfile.createNewFile();
            InputStream inputStream = WifiConnector.class.getClassLoader().getResourceAsStream(PROFILE_FILE_NAME);
            Files.copy(inputStream, wifiProfile.toPath());
            Process process = Runtime.getRuntime().exec("netsh wlan add profile filename=\"" + wifiProfile.getAbsolutePath() + "\"");
            process.waitFor();
        }
        Process process = Runtime.getRuntime().exec("netsh wlan connect name=\"" + PROFILE_NAME + "\"");
        process.waitFor();
    }
}
