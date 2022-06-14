package me.hsgamer.eosclientlauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class WifiConnector {
    private static final String profileFileName = "Wi-Fi-FU-Exam.xml";
    private static final String profileName = "FU-Exam";

    public static void connect() throws IOException, InterruptedException {
        File wifiProfile = new File(profileName);
        if (!wifiProfile.exists()) {
            wifiProfile.createNewFile();
            InputStream inputStream = WifiConnector.class.getClassLoader().getResourceAsStream(profileFileName);
            Files.copy(inputStream, wifiProfile.toPath());
            Process process = Runtime.getRuntime().exec("netsh wlan add profile filename=\"" + wifiProfile.getAbsolutePath() + "\"");
            process.waitFor();
        }
        Process process = Runtime.getRuntime().exec("netsh wlan connect name=\"" + profileName + "\"");
        process.waitFor();
    }
}
