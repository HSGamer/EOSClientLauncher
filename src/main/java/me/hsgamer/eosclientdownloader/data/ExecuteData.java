package me.hsgamer.eosclientdownloader.data;

import java.nio.file.Path;
import java.util.function.Predicate;

public enum ExecuteData {
    EOS_CLIENT(
            "EOSClient.zip",
            "10sGYKJCvNNra6WAnAsILFLd2qBbd9043",
            "EOSClient.exe"
    ),
    PEA_CLIENT(
            "PEAClient.zip",
            "1rx0t_fSFqc1IJ4NSIQqjVjWmd3t5r1sC",
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
