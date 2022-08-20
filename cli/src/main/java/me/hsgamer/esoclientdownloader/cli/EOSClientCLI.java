package me.hsgamer.esoclientdownloader.cli;

import me.hsgamer.eosclientlauncher.core.Launcher;
import me.hsgamer.eosclientlauncher.core.data.ExecuteData;
import me.hsgamer.eosclientlauncher.core.data.FileData;
import me.hsgamer.esoclientdownloader.cli.config.MainConfig;

import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.*;

public class EOSClientCLI {
    public static final Logger LOGGER = Logger.getLogger("Downloader");

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                StringBuilder builder = new StringBuilder();
                builder.append("[").append(logRecord.getLevel()).append("] ").append(logRecord.getMessage());
                builder.append("\n");
                Throwable throwable = logRecord.getThrown();
                if (throwable != null) {
                    builder.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
                    for (StackTraceElement element : throwable.getStackTrace()) {
                        builder.append("  at ")
                                .append(element.getClassName())
                                .append(" ")
                                .append(element.getFileName())
                                .append(":")
                                .append(element.getLineNumber())
                                .append("\n");
                    }
                }
                return builder.toString();
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    public static void main(String... args) {
        MainConfig config = new MainConfig();
        config.setup();
        Launcher.builder()
                .clientId(config.clientId)
                .clientSecret(config.clientSecret)
                .connectWifi(config.autoConnectWifi)
                .deleteExistedFiles(config.deleteExistedFiles)
                .executeAfterDownload(config.executeFileAfterDownload)
                .progressConsumer(new BiConsumer<Long, Long>() {
                    int times = 0;

                    @Override
                    public void accept(Long total, Long current) {
                        float percent = (float) current / total;
                        if (percent >= times * 0.1) {
                            LOGGER.info(() -> "Downloading: " + (int) (percent * 100) + "%");
                            times++;
                        }
                    }
                })
                .onChooseExecuteData(EOSClientCLI::askAndGetExecuteData)
                .onChooseFileData(EOSClientCLI::askAndGetFileData)
                .onAlreadyDownload(path -> LOGGER.info(() -> "File " + path + " already downloaded"))
                .onStartDownload(path -> LOGGER.info(() -> "Start downloading " + path))
                .onFinishDownload(path -> LOGGER.info(() -> "Finish downloading " + path))
                .onError(throwable -> LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable))
                .onCreateFolder(path -> LOGGER.info(() -> "Create folder " + path))
                .build()
                .launch().join();
    }

    private static <T> T askAndGet(List<T> list, String message, String prompt, Function<T, String> displayFunction) {
        if (list.isEmpty()) {
            throw new IllegalStateException("Can't find any file");
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        LOGGER.info(() -> message + ":");
        Scanner scanner = new Scanner(System.in);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            T data = list.get(i);
            int finalI = i;
            LOGGER.info(() -> "  " + finalI + ": " + displayFunction.apply(data));
        }
        System.out.print(prompt + ": ");
        do {
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {
                index = 0;
                LOGGER.info("Will choose the first one");
            }
        } while (index < 0 || index >= list.size());
        return list.get(index);
    }

    private static FileData askAndGetFileData(List<FileData> list) {
        return askAndGet(list, "Available file data", "Please enter the number of the file you want to download", FileData::getName);
    }

    private static ExecuteData askAndGetExecuteData(List<ExecuteData> list) {
        return askAndGet(list, "Available execute data", "Please enter the number of the execute data you want to use", Enum::name);
    }
}