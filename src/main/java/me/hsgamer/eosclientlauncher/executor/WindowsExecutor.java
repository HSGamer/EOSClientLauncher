package me.hsgamer.eosclientlauncher.executor;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        String profileName = "FU-Exam";
        String profileFileName = "Wi-Fi-FU-Exam.xml";
        File wifiProfile = new File(profileFileName);
        if (!wifiProfile.exists()) {
            wifiProfile.createNewFile();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(profileFileName);
            assert inputStream != null;
            Files.copy(inputStream, wifiProfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Process process = Runtime.getRuntime().exec("netsh wlan add profile filename=\"" + wifiProfile.getAbsolutePath() + "\"");
            process.waitFor();
        }
        Process process = Runtime.getRuntime().exec("netsh wlan connect name=\"" + profileName + "\"");
        process.waitFor();
    }
}
