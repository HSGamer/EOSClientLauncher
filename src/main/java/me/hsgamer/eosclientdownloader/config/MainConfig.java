package me.hsgamer.eosclientdownloader.config;

import me.hsgamer.eosclientdownloader.data.ExecuteData;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.hscore.config.path.CommentablePath;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.hscore.config.path.impl.BooleanConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import me.hsgamer.hscore.config.simpleconfiguration.SimpleConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
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
    public static final ConfigPath<ExecuteData> FILE_MODE = new CommentablePath<>(
            new AdvancedConfigPath<String, ExecuteData>("file.mode", ExecuteData.EOS_CLIENT) {
                @Override
                public @Nullable String getFromConfig(@NotNull Config config) {
                    return Objects.toString(config.get(getPath()), null);
                }

                @Override
                public @Nullable ExecuteData convert(@NotNull String rawValue) {
                    try {
                        return ExecuteData.valueOf(rawValue.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }

                @Override
                public @NotNull String convertToRaw(@NotNull ExecuteData value) {
                    return value.name();
                }
            },
            "The file mode. Currently: EOS_CLIENT or PEA_CLIENT"
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
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }));
    }
}
