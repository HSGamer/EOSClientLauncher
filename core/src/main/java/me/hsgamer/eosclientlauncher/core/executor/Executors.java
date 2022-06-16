package me.hsgamer.eosclientlauncher.core.executor;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public final class Executors {
    private static final List<Executor> executorList = new ArrayList<>();

    static {
        register(new WindowsExecutor());
    }

    public static void register(Executor executor) {
        executorList.add(executor);
    }

    public static Optional<Executor> getBestExecutor() {
        return executorList.stream().filter(Executor::canExecute).findFirst();
    }
}
