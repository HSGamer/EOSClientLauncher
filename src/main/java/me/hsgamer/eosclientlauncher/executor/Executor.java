package me.hsgamer.eosclientlauncher.executor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public interface Executor {
    boolean canExecute();

    CompletableFuture<Void> execute(Path path, boolean connectWifi) throws IOException, InterruptedException;
}
