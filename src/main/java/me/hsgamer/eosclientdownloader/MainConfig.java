package me.hsgamer.eosclientdownloader;

import me.hsgamer.hscore.config.CommentablePath;
import me.hsgamer.hscore.config.ConfigPath;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.StringConfigPath;
import me.hsgamer.hscore.config.simpleconfiguration.SimpleConfig;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;

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

    public MainConfig() {
        super(new SimpleConfig(new File(".", "config.yml"), YamlFile::loadConfiguration));
    }
}
