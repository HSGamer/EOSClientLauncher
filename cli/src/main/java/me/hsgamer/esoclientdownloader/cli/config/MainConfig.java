package me.hsgamer.esoclientdownloader.cli.config;

import me.hsgamer.hscore.config.annotated.AnnotatedConfig;
import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.hscore.config.simpleconfiguration.SimpleConfig;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MainConfig extends AnnotatedConfig {
    @ConfigPath("client.id")
    @Comment("The client's ID")
    public final String clientId;

    @ConfigPath("client.secret")
    @Comment("The client's secret key")
    public final String clientSecret;

    @ConfigPath("file.delete-existed-files")
    @Comment("Should the existed files be deleted before uncompressing ?")
    public final boolean deleteExistedFiles;

    @ConfigPath("auto-connect-wifi")
    @Comment("Should the launcher automatically connect to the wifi network ?")
    public final boolean autoConnectWifi;

    @ConfigPath("execute-file-after-download")
    @Comment("Should the launcher execute the file after downloading ?")
    public final boolean executeFileAfterDownload;

    public MainConfig() {
        super(new SimpleConfig<>(new File(".", "config.yml"), new YamlFile(), (file, yamlFile) -> {
            yamlFile.setConfigurationFile(file);
            try {
                yamlFile.loadWithComments();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage(), e);
            }
        }));
        clientId = "";
        clientSecret = "";
        deleteExistedFiles = false;
        autoConnectWifi = false;
        executeFileAfterDownload = true;
    }
}
