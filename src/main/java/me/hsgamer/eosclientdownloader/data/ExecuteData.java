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
            "1Yxp0nua82PHc0UShAss4mxDpRngP2Bbt",
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
