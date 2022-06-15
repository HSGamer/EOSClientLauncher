package me.hsgamer.eosclientlauncher.utils;

import java.util.logging.*;

public final class LoggerUtils {
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

    private LoggerUtils() {
        // EMPTY
    }
}
