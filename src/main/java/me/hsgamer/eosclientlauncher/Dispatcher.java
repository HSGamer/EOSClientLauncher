package me.hsgamer.eosclientlauncher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Dispatcher {
    public static void dispatch(Path path) throws IOException, InterruptedException {
        Path parent = path.getParent();
        List<String> scriptList = Arrays.asList(
                "Set UAC = CreateObject(\"Shell.Application\")",
                "UAC.ShellExecute \"" + path.toAbsolutePath() + "\", \"\", \"" + parent.toAbsolutePath() + "\", \"runas\", 1"
        );
        File tempVbs = Files.createTempFile("eosclientlauncher", ".vbs").toFile();
        Files.write(tempVbs.toPath(), scriptList);
        Process process = Runtime.getRuntime().exec("cscript " + tempVbs.getAbsolutePath());
        process.waitFor();
    }
}
