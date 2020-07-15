package com.rabbitown.yachat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.rabbitown.yachat.chat.ChatFormat;
import com.rabbitown.yachat.chat.element.*;
import com.rabbitown.yachat.chat.function.*;
import com.rabbitown.yachat.command.CommandMain;
import com.rabbitown.yachat.listener.PlayerChatListener;
import com.rabbitown.yachat.nms.NMSBase;
import com.rabbitown.yachat.util.Logger;

/**
 * The main class of YaChat.
 * 
 * @author Yoooooory
 */
public class YaChat extends JavaPlugin {

    FileConfiguration elementConfig = new YamlConfiguration();
    FileConfiguration functionConfig = new YamlConfiguration();

    /**
     * The active chat formatter.<p>
     * You can register (or unregister) an element (or a function) there.
     * 
     * @see ChatFormat
     */
    public static ChatFormat formatter;

    /**
     * The active NMS util class.<p>
     * 
     * @see NMSBase
     */
    public static NMSBase NMS;

    @Override
    public void onEnable() {
        formatter.loadFormat();
        Logger.info("なんか静かですね。街の中にはギャラルホルンもいないし本部とはえらい違いだ。");
        if (!loadNMS()) {
            Logger.severe("Oops..!! Cannot use YaChat in this server. Please report this to the plugin maker.");
            getPluginLoader().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginCommand("yachat").setExecutor(new CommandMain(this));
        Logger.info("§aEverything got ready! YaChat is now enabled.");
    }

    @Override
    public void onLoad() {
        formatter = new ChatFormat(getConfig());
        // Load configs
        loadConfig();
        // Register chat elements
        registerChatElements();
        registerChatFunctions();
        Logger.info("§aEverything is ready!");
    }

    @Override
    public void onDisable() {
        Logger.info("§r止まる§7んじゃ§8ねぇぞ§0……。");
    }

    /**
     * Reload YaChat.
     */
    public void loadPlugin() {
        loadConfig();
        formatter.loadFormat();
        Logger.info("§aLoaded successfully!");
    }

    /**
     * Load YaChat configs.
     */
    public void loadConfig() {
        Logger.info("Loading configs...");
        // config.yml
        saveDefaultConfig();
        reloadConfig();
        if (getConfig().getBoolean("general.chat.placeholder")) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                Logger.warning("Could not find PlaceholderAPI! Some feature may not work.");
                getConfig().set("general.chat.placeholder", false);
            } else {
                Logger.info("§aFound PlaceholderAPI!");
            }
        }
        // element.yml
        File elementFile = new File(getDataFolder(), "element.yml");
        if (!elementFile.exists()) {
            saveResource("element.yml", false);
        }
        try {
            elementConfig.load(elementFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Logger.severe("Cannot load element.yml! This plugin maybe doesn't work.");
            return;
        }
        // function.yml
        File functionFile = new File(getDataFolder(), "function.yml");
        if (!functionFile.exists()) {
            saveResource("function.yml", false);
        }
        try {
            functionConfig.load(functionFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            Logger.severe("Cannot load function.yml! This plugin maybe doesn't work.");
            return;
        }
    }

    /**
     * Register default chat elements.
     */
    public void registerChatElements() {
        Logger.info("Registering elements and functions...");
        formatter.registerElement(new CustomElement(elementConfig));
        formatter.registerElement(new WorldNameElement(elementConfig));
        formatter.registerElement(new PlayerNameElement(elementConfig));
        formatter.registerElement(new PlayerTitleElement(elementConfig));
        formatter.registerElement(new PlayerLevelElement(elementConfig));
    }

    /**
     * Register default chat functions.
     */
    public void registerChatFunctions() {
        formatter.registerFunction(new AtPlayerFunction(functionConfig.getConfigurationSection("functions.atplayer")));
    }

    /**
     * Load NMS.
     * 
     * @return Whether the NMS loaded successfully.
     */
    private boolean loadNMS() {
        try {
            NMS = (NMSBase) Class.forName("com.rabbitown.yachat.nms." + getServer().getClass().getPackage().getName().substring(23)).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
