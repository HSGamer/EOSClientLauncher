package me.hsgamer.eosclientlauncher.core.data;

import java.nio.file.Path;
import java.util.function.Predicate;

public enum ExecuteData {
    EOS_CLIENT(
            "EOSClient.zip",
            "1akRf3fgQkEu9DcXAdGHwk0BXYuqG31Am",
            "EOSClient.exe"
    ),
    PEA_CLIENT(
            "PEAClient.zip",
            "1ynjfBepM3JoYjMsi4ijwJ1hj6FP0mZhF",
            "PEALogin.exe"
    );

    public final String fileName;
    public final String clientId;
    public final Predicate<Path> executeFileCheck;

    ExecuteData(String fileName, String clientId, Predicate<Path> executeFileCheck) {
        this.fileName = fileName;
        this.clientId = clientId;
        this.executeFileCheck = executeFileCheck;
    }

    ExecuteData(String fileName, String clientId, String executeFile) {
        this(fileName, clientId, path -> path.endsWith(executeFile));
    }
}
