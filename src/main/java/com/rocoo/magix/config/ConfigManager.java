package com.rocoo.magix.config;

import com.rocoo.magix.Magix;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private Magix magix;
    private FileConfiguration configuration;

    public ConfigManager(Magix magix) {
        this.magix = magix;
        this.configuration = magix.getConfig();
    }

    public String getDefaultCommand() {
        return this.configuration.getString("default-script");
    }
}
