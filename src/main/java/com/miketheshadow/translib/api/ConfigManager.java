package com.miketheshadow.translib.api;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

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
            validateConfiguration(file, yamlConfiguration, primaryConfig);
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
     * @param configFile        the file location
     * @param yamlConfiguration the config to validate
     * @param primaryConfig     the primary configuration file to pull the keys from
     */
    private void validateConfiguration(File configFile, YamlConfiguration yamlConfiguration, YamlConfiguration primaryConfig) {
        Set<String> keys = primaryConfig.getKeys(false);

        // Marking as dirty prevents unnecessary saving
        boolean isDirty = false;

        int missing = 0;
        for (String key : keys) {
            if (!yamlConfiguration.contains(key)) {
                yamlConfiguration.set(key, primaryConfig.getString(key));
                yamlConfiguration.setComments(key, Collections.singletonList("Please update this field!"));
                isDirty = true;
                missing++;
            }
        }
        if (isDirty) {
            try {
                String message = "[TransLib] " + ChatColor.YELLOW + " Warning: config "
                        + configFile.getAbsolutePath() + " is missing " + missing +
                        " key(s). If you use this configuration please update it with the missing values. Otherwise ignore this message.";

                if (Bukkit.getServer() == null) Logger.getLogger(ConfigManager.class.getName()).warning(message);
                else Bukkit.getServer().getLogger().warning(message);

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
