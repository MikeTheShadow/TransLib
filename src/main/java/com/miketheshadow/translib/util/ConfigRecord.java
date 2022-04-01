package com.miketheshadow.translib.util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public record ConfigRecord(YamlConfiguration configuration, File configLocation) {
}
