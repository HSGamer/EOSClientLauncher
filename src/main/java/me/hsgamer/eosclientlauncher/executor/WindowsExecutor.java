package me.hsgamer.eosclientlauncher.executor;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WindowsExecutor implements Executor {
    @Override
    public boolean canExecute() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @Override
    public CompletableFuture<Void> execute(Path path, boolean connectWifi) throws IOException, InterruptedException {
        if (connectWifi) {
            connectWifi();
        }
        Process process = dispatch(path);
        return CompletableFuture.runAsync(() -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private Process dispatch(Path path) throws IOException {
        Path parent = path.getParent();
        List<String> scriptList = Arrays.asList(
                "Set UAC = CreateObject(\"Shell.Application\")",
                "UAC.ShellExecute \"" + path.toAbsolutePath() + "\", \"\", \"" + parent.toAbsolutePath() + "\", \"runas\", 1"
        );
        File tempVbs = Files.createTempFile("eosclientlauncher", ".vbs").toFile();
        Files.write(tempVbs.toPath(), scriptList);
        return Runtime.getRuntime().exec("cscript " + tempVbs.getAbsolutePath());
    }

    private void connectWifi() throws IOException, InterruptedException {
        String PROFILE_NAME = "FU-Exam";
        File wifiProfile = new File(PROFILE_NAME);
        if (!wifiProfile.exists()) {
            wifiProfile.createNewFile();
            String PROFILE_FILE_NAME = "Wi-Fi-FU-Exam.xml";
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROFILE_FILE_NAME);
            Files.copy(inputStream, wifiProfile.toPath());
            Process process = Runtime.getRuntime().exec("netsh wlan add profile filename=\"" + wifiProfile.getAbsolutePath() + "\"");
            process.waitFor();
        }
        Process process = Runtime.getRuntime().exec("netsh wlan connect name=\"" + PROFILE_NAME + "\"");
        process.waitFor();
    }
}