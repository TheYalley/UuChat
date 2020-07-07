package com.rabbitown.uuchat;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.rabbitown.uuchat.chat.ChatFormat;
import com.rabbitown.uuchat.chat.element.CustomElement;
import com.rabbitown.uuchat.chat.element.PlayerLevelElement;
import com.rabbitown.uuchat.chat.element.PlayerNameElement;
import com.rabbitown.uuchat.chat.element.PlayerTitleElement;
import com.rabbitown.uuchat.chat.element.WorldNameElement;
import com.rabbitown.uuchat.chat.function.AtPlayerFunction;
import com.rabbitown.uuchat.command.CommandMain;
import com.rabbitown.uuchat.listener.PlayerChatListener;
import com.rabbitown.uuchat.nms.NMSBase;

public class UuChat extends JavaPlugin {

    FileConfiguration elementConfig = new YamlConfiguration();
    FileConfiguration functionConfig = new YamlConfiguration();

    public static ChatFormat formater;
    public static NMSBase NMS;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eなんか静かですね。街の中にはギャラルホルンもいないし本部とはえらい違いだ。");
        if (!loadNMS()) {
            getLogger().severe("Oops..!! Cannot use UuChat in this server. Please report this to the plugin maker.");
            getPluginLoader().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginCommand("uuchat").setExecutor(new CommandMain(this));
    }

    @Override
    public void onLoad() {
        loadPlugin();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §r止まる§7んじゃ§8ねぇぞ§0……。");
    }

    public void loadPlugin() {
        // Load configs
        /// config.yml
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading configs...");
        saveDefaultConfig();
        reloadConfig();
        if (getConfig().getBoolean("general.chat.placeholder")) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
                getLogger().warning("Could not find PlaceholderAPI! Some feature may not work.");
                getConfig().set("general.chat.placeholder", false);
            } else {
                Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §aFound PlaceholderAPI!");
            }
        }
        /// element.yml
        File elementFile = new File(getDataFolder(), "element.yml");
        if (!elementFile.exists()) {
            saveResource("element.yml", false);
        }
        try {
            elementConfig.load(elementFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            getLogger().severe("Cannot load element.yml! This plugin maybe doesn't work.");
            return;
        }
        /// function.yml
        File functionFile = new File(getDataFolder(), "function.yml");
        if (!functionFile.exists()) {
            saveResource("function.yml", false);
        }
        try {
            functionConfig.load(functionFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            getLogger().severe("Cannot load function.yml! This plugin maybe doesn't work.");
            return;
        }
        // Register chat elements
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading elements...");
        registerChatElements();
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading functions...");
        registerChatFunctions();
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §aEverything is ready!");
    }

    public void registerChatElements() {
        formater = new ChatFormat(getConfig());
        formater.registerElement("builtin:custom", CustomElement.class, elementConfig);
        formater.registerElement("builtin:world_name", WorldNameElement.class, elementConfig);
        formater.registerElement("builtin:player_name", PlayerNameElement.class, elementConfig);
        formater.registerElement("builtin:player_title", PlayerTitleElement.class, elementConfig);
        formater.registerElement("builtin:player_level", PlayerLevelElement.class, elementConfig);
    }

    public void registerChatFunctions() {
        formater.registerFunction(new AtPlayerFunction(functionConfig.getConfigurationSection("functions.atplayer")));
    }

    private boolean loadNMS() {
        try {
            NMS = (NMSBase) Class.forName("com.rabbitown.uuchat.nms." + getServer().getClass().getPackage().getName().substring(23)).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
