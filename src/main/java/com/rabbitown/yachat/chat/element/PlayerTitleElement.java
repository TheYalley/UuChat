package com.rabbitown.yachat.chat.element;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.handy.constants.BaseConstants;
import com.rabbitown.yachat.chat.ChatElement;
import com.rabbitown.yachat.util.Logger;
import com.rabbitown.yachat.util.ParseUtil;

public class PlayerTitleElement extends ChatElement {

    String plugin;

    public PlayerTitleElement(FileConfiguration config) {
        super("builtin:player_title", config);
    }

    @Override
    public boolean loadElement() {
        String plugin = config.getString("plugin");
        if (plugin == null) {
            plugin = "PlayerTitle";
        }
        if (plugin.equals("PlayerTitle")) {
            if (Bukkit.getPluginManager().getPlugin("PlayerTitle") == null) {
                Logger.warning("Can't load element \"" + name + "\": Unable to find PlayerTitle.");
                return false;
            }
            this.plugin = plugin;
        } else {
            Logger.warning("Can't load element \"" + name + "\": Unknown setting \"" + plugin + "\".");
            return false;
        }
        if (config.getString("pattern") == null) {
            Logger.warning("Can't load element \"" + name + "\": Cannot find the pattern.");
            return false;
        }
        try {
            ParseUtil.addJSONEvents(new JsonObject(), config, null);
        } catch (ConfigurationException e) {
            Logger.warning("Can't load element \"" + name + "\": " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public JsonObject parseMessage(String message, Player sender) {
        JsonObject object = new JsonObject();
        if (plugin.equals("PlayerTitle")) {
            String title = BaseConstants.playerTitleMap.get(sender.getUniqueId());
            if (title == null) {
                title = Bukkit.getPluginManager().getPlugin("PlayerTitle").getConfig().getString("default");
            }
            object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")).replace("$title$", title));
        }
        try {
            ParseUtil.addJSONEvents(object, config, sender);
        } catch (ConfigurationException e) {
        }
        return object;
    }

}