package com.rocoo.magix;

import com.rocoo.magix.command.CommandHandler;
import com.rocoo.magix.config.ConfigManager;
import com.rocoo.magix.listener.WandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Magix extends JavaPlugin {

    private ConfigManager manager;
    private ScriptTank engine;
    private CommandHandler handler;
    private ImageHandler imageHandler;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.manager = new ConfigManager(this);
        this.engine = new ScriptTank(this);

        Bukkit.getPluginManager().registerEvents(new WandListener(this), this);

        this.handler = new CommandHandler(this);
        this.getCommand("wand").setExecutor(this.handler);
        try {
            this.imageHandler = new ImageHandler(this);
            this.handler.initImage();
        } catch (IOException e) {
            getLogger().info("An error occurred!");
        }
    }

    @Override
    public void onDisable() {
        this.manager = null;
        this.engine = null;
    }

    public ConfigManager getManager() {
        return this.manager;
    }

    public ScriptTank getEngine() {
        return this.engine;
    }

    public ImageHandler getImageHandler() {
        return this.imageHandler;
    }

    public void setImageToken(char[] token) {
        this.handler.setImage(token);
    }
}
