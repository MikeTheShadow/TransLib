package com.miketheshadow.translib;

import com.miketheshadow.translib.api.ConfigManager;

import javax.annotation.Nonnull;
import java.io.File;

public class TransLib {

    private final ConfigManager configManager;
    private String activeConfig;

    /**
     * The base config must contain all possible keys. Lacking all keys will cause severe stability issues
     *
     * @param baseConfigName The config to use as a template for all of them
     * @param configFolder   The folder that contains your configurations
     * @param activeConfig   The currently active configuration
     */
    public TransLib(@Nonnull String baseConfigName, @Nonnull File configFolder, @Nonnull String activeConfig) {
        if (!configFolder.isDirectory()) {
            throw new IllegalStateException(configFolder.getAbsolutePath() + " is not a folder!");
        }
        this.configManager = new ConfigManager(configFolder, baseConfigName);
        this.activeConfig = activeConfig;
    }

    /**
     * Using this constructor assumes the primary configuration file is the base configuration file
     *
     * @param baseConfigName The config to use as a template for all of them
     * @param configFolder   The folder that contains your configurations
     */
    public TransLib(@Nonnull String baseConfigName, @Nonnull File configFolder) {
        if (!configFolder.isDirectory()) {
            throw new IllegalStateException(configFolder.getAbsolutePath() + " is not a folder!");
        }
        this.configManager = new ConfigManager(configFolder, baseConfigName);
        this.activeConfig = baseConfigName;
    }

    /**
     * Use this to allow for user-specific translations. Using a global {@see ConfigManager}
     * This allows you to create a new TransLib instance per user which can be as simple as
     * PlayerMap#get#getTranslation to get translations per player rather than globally
     *
     * @param manager      a pre-loaded manager
     * @param activeConfig The currently active configuration
     */
    public TransLib(@Nonnull ConfigManager manager, @Nonnull String activeConfig) {
        this.configManager = manager;
        this.activeConfig = activeConfig;
    }

    /**
     * @param key the key from the yml file.
     * @return The key pulled from the current active configuration
     */
    public String getTranslation(String key) {
        return this.configManager.getValue(this.activeConfig, key);
    }

    public String getActiveConfig() {
        return this.activeConfig;
    }

    public void setActiveConfig(String activeConfig) {
        this.activeConfig = activeConfig;
    }

    public String getTranslationFromConfig(String config, String key) {
        return this.configManager.getValue(config, key);
    }
}
