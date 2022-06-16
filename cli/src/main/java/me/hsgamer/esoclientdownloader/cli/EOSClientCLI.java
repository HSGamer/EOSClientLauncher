package me.hsgamer.esoclientdownloader.cli;

import me.hsgamer.eosclientlauncher.core.Launcher;
import me.hsgamer.eosclientlauncher.core.data.ExecuteData;
import me.hsgamer.eosclientlauncher.core.data.FileData;
import me.hsgamer.esoclientdownloader.cli.config.MainConfig;

import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
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

        new MainConfig().setup();
    }

    public static void main(String... args) {
        ExecuteData executeData = askAndGetExecute();
        Launcher.builder()
                .clientId(MainConfig.CLIENT_ID.getValue())
                .clientSecret(MainConfig.CLIENT_SECRET.getValue())
                .executeData(executeData)
                .connectWifi(MainConfig.AUTO_CONNECT_WIFI.getValue())
                .deleteExistedFiles(MainConfig.FILE_DELETE_EXISTED_UNCOMPRESSED.getValue())
                .executeAfterDownload(MainConfig.EXECUTE_FILE_AFTER_DOWNLOAD.getValue())
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
                .onAlreadyDownload(path -> LOGGER.info(() -> "File " + path + " already downloaded"))
                .onStartDownload(path -> LOGGER.info(() -> "Start downloading " + path))
                .onFinishDownload(path -> LOGGER.info(() -> "Finish downloading " + path))
                .onChooseFile(EOSClientCLI::askAndGet)
                .onError(throwable -> LOGGER.log(Level.SEVERE, throwable.getMessage(), throwable))
                .onCreateFolder(path -> LOGGER.info(() -> "Create folder " + path))
                .build()
                .launch().join();
    }

    private static FileData askAndGet(List<FileData> list) {
        if (list.isEmpty()) {
            throw new IllegalStateException("Can't find any file");
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        LOGGER.info(() -> "Available file data:");
        Scanner scanner = new Scanner(System.in);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            FileData fileData = list.get(i);
            int finalI = i;
            LOGGER.info(() -> "  " + finalI + ": " + fileData.getName());
        }
        System.out.print("Please enter the number of the file you want to download: ");
        do {
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {
                LOGGER.info("Will download the first file");
            }
        } while (index < 0 || index >= list.size());
        return list.get(index);
    }

    private static ExecuteData askAndGetExecute() {
        ExecuteData[] values = ExecuteData.values();
        LOGGER.info(() -> "Available execute data:");
        for (int i = 0; i < values.length; i++) {
            ExecuteData executeData = values[i];
            int finalI = i;
            LOGGER.info(() -> "  " + finalI + ": " + executeData.name());
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the number of the execute data you want to use: ");
        int index = 0;
        do {
            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (Exception ignored) {
                LOGGER.info("Will choose the first execute data");
            }
        } while (index < 0 || index >= values.length);
        return values[index];
    }
}