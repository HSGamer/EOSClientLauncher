package me.hsgamer.eosclientlauncher.core;

import lombok.Builder;
import me.hsgamer.eosclientlauncher.core.data.ExecuteData;
import me.hsgamer.eosclientlauncher.core.data.FileData;
import me.hsgamer.eosclientlauncher.core.executor.Executor;
import me.hsgamer.eosclientlauncher.core.executor.Executors;
import me.hsgamer.eosclientlauncher.core.utils.DriveUtils;
import me.hsgamer.eosclientlauncher.core.utils.Utils;
import me.hsgamer.eosclientlauncher.core.utils.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
public class Launcher {
    private String clientId;
    private String clientSecret;
    @Builder.Default
    private boolean connectWifi = false;
    @Builder.Default
    private File uncompressedFolder = new File("Uncompressed");
    @Builder.Default
    private boolean deleteExistedFiles = false;
    @Builder.Default
    private BiConsumer<Long, Long> progressConsumer = (total, current) -> {
        // EMPTY
    };
    @Builder.Default
    private Consumer<Path> onStartDownload = path -> {
        // EMPTY
    };
    @Builder.Default
    private Consumer<Path> onFinishDownload = path -> {
        // EMPTY
    };
    @Builder.Default
    private Consumer<Path> onAlreadyDownload = path -> {
        // EMPTY
    };
    @Builder.Default
    private Consumer<Path> onCreateFolder = path -> {
        // EMPTY
    };
    @Builder.Default
    private Function<List<FileData>, FileData> onChooseFileData = list -> list.stream().max(Comparator.comparingLong(FileData::getSize)).orElse(null);
    @Builder.Default
    private Function<List<ExecuteData>, ExecuteData> onChooseExecuteData = list -> list.stream().findAny().orElse(null);
    @Builder.Default
    private Function<List<Path>, Path> onChooseExecutePath = list -> list.stream().findFirst().orElse(null);
    @Builder.Default
    private boolean executeAfterDownload = false;
    @Builder.Default
    private Consumer<Throwable> onError = throwable -> {
        // EMPTY
    };

    public CompletableFuture<Void> launch() {
        return CompletableFuture.runAsync(() -> {
            try {
                DriveUtils driveUtils = new DriveUtils(clientId, clientSecret);
                ExecuteData executeData = onChooseExecuteData.apply(Arrays.asList(ExecuteData.values()));
                File downloadFile = new File(executeData.fileName);
                if (!downloadFile.exists() && downloadFile.createNewFile()) {
                    onCreateFolder.accept(downloadFile.toPath());
                }
                FileData fileData = onChooseFileData.apply(driveUtils.getFiles(executeData.clientId));
                String currentMd5 = Utils.getFileChecksum(downloadFile);
                if (currentMd5.equalsIgnoreCase(fileData.getMd5())) {
                    onAlreadyDownload.accept(downloadFile.toPath());
                } else {
                    onStartDownload.accept(downloadFile.toPath());
                    InputStream downloadStream = driveUtils.getFileAsInputStream(fileData.getId());
                    FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
                    long progress = 0;
                    long size = fileData.getSize();
                    byte[] buffer = new byte[1024];
                    int read;
                    progressConsumer.accept(size, progress);
                    while ((read = downloadStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, read);
                        progress += read;
                        progressConsumer.accept(size, progress);
                    }
                    fileOutputStream.close();
                    downloadStream.close();
                    onFinishDownload.accept(downloadFile.toPath());
                }

                if (deleteExistedFiles && uncompressedFolder.exists()) {
                    try (Stream<Path> stream = Files.walk(uncompressedFolder.toPath())) {
                        stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                    }
                }
                List<Path> files = ZipUtils.unzip(downloadFile, uncompressedFolder);

                if (executeAfterDownload) {
                    List<Path> clientPaths = files.stream().filter(executeData.executeFileCheck).collect(Collectors.toList());
                    Path clientPath = onChooseExecutePath.apply(clientPaths);
                    Optional<Executor> executor = Executors.getBestExecutor();
                    if (executor.isPresent() && clientPath != null) {
                        executor.get().execute(clientPath, connectWifi).join();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (Throwable throwable) {
                onError.accept(throwable);
            }
        });
    }
}
