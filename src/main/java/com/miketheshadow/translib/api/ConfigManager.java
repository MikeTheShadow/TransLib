package com.miketheshadow.translib.api;

import com.miketheshadow.translib.util.ConfigRecord;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple impl for loading a folder of configs
 */
public class ConfigManager {

    private final HashMap<String, YamlConfiguration> configMap = new HashMap<>();

    /**
     * @param configFolder   The folder containing the configs. Null check here in case a global instance is made as well
     * @param baseConfigName The name of the base configuration file to use
     */
    public ConfigManager(@Nonnull File configFolder, @Nonnull String baseConfigName) {

        File[] files = configFolder.listFiles();
        if (files == null) {
            throw new IllegalStateException("Config folder " + configFolder.getAbsolutePath() + " is not a folder!");
        }

        YamlConfiguration primaryConfig = loadConfiguration(new File(configFolder, baseConfigName));

        for (File file : files) {
            YamlConfiguration yamlConfiguration = loadConfiguration(file);
            ConfigRecord record = new ConfigRecord(yamlConfiguration, file);
            validateConfiguration(record, primaryConfig);
            configMap.put(file.getName(), yamlConfiguration);
        }
    }

    /**
     * @param configName  Name of the config file
     * @param configValue Value to fetch from the config
     * @return A string from the config
     */
    public String getValue(String configName, String configValue) {
        YamlConfiguration configuration = configMap.get(configName);
        String value = configuration.getString(configValue);
        return value != null ? value : configName + "." + configValue;
    }

    /**
     * This is used only to validate a secondary config to the primary configuration file
     *
     * @param record        A quick pair of config and file.
     * @param primaryConfig the primary configuration file to pull the keys from
     */
    private void validateConfiguration(ConfigRecord record, YamlConfiguration primaryConfig) {
        Set<String> keys = primaryConfig.getKeys(false);
        YamlConfiguration yamlConfiguration = record.configuration();
        File configFile = record.configLocation();

        // Marking as dirty prevents unnecessary saving
        boolean isDirty = false;

        for (String key : keys) {
            if (!yamlConfiguration.contains(key)) {
                yamlConfiguration.set(key, primaryConfig.getString(key));
                yamlConfiguration.setComments(key, Collections.singletonList("Please update this field!"));
                isDirty = true;
            }
        }
        if (isDirty) {
            try {
                yamlConfiguration.save(configFile);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to update config " + configFile.getAbsolutePath() + " " + e.getMessage());
            }
        }
    }

    private YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.load(file);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load config! " + file.getAbsolutePath() + " " + e.getMessage());
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException("bad configuration file! " + file.getAbsolutePath() + " " + e.getMessage());
        }
        return yamlConfiguration;
    }

    /**
     * This may become private at some point.
     * From a safety/security standpoint exposing configs is a bad idea.
     * Possibly allowing an update feature that will reflect across all configs.
     * Until then this method will be marked deprecated and be considered unsafe to use.
     */
    @Deprecated
    public HashMap<String, YamlConfiguration> getConfigMap() {
        return configMap;
    }
}
