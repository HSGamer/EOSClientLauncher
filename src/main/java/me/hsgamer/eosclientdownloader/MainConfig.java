package me.hsgamer.eosclientdownloader;

import me.hsgamer.hscore.config.CommentablePath;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BooleanConfigPath;
import me.hsgamer.hscore.config.path.StringConfigPath;
import me.hsgamer.hscore.config.simpleconfiguration.SimpleConfig;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MainConfig extends PathableConfig {
    public static final ConfigPath<String> CLIENT_ID = new CommentablePath<>(
            new StringConfigPath("client.id", ""),
            "The client's ID"
    );
    public static final ConfigPath<String> CLIENT_SECRET = new CommentablePath<>(
            new StringConfigPath("client.secret", ""),
            "The client's secret key"
    );
    public static final ConfigPath<String> FILE_NAME = new CommentablePath<>(
            new StringConfigPath("file.name", "EOS-Client.zip"),
            "The file's name"
    );
    public static final ConfigPath<String> FILE_DRIVE_ID = new CommentablePath<>(
            new StringConfigPath("file.drive-id", "15orxKbmGm4foV5Lpc0RYnz9Imvp3IbWF"),
            "The file's drive ID"
    );
    public static final ConfigPath<String> FILE_UNCOMPRESSED_PATH = new CommentablePath<>(
            new StringConfigPath("file.uncompressed-path", "Uncompressed"),
            "The uncompressed folder"
    );
    public static final ConfigPath<Boolean> FILE_DELETE_AFTER_UNCOMPRESSED = new CommentablePath<>(
            new BooleanConfigPath("file.delete-after-uncompressed", true),
            "Should the downloaded file be deleted after uncompressed ?"
    );
    public static final ConfigPath<Boolean> FILE_DELETE_EXISTED_UNCOMPRESSED = new CommentablePath<>(
            new BooleanConfigPath("file.delete-existed-files", false),
            "Should the existed files be deleted before uncompressing ?"
    );

    public MainConfig() {
        super(new SimpleConfig<>(new File(".", "config.yml"), new YamlFile(), (file, yamlFile) -> {
            yamlFile.setConfigurationFile(file);
            try {
                yamlFile.loadWithComments();
            } catch (InvalidConfigurationException | IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }));
    }
}
