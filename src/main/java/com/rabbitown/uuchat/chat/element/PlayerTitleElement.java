package com.rabbitown.uuchat.chat.element;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.handy.constants.BaseConstants;
import com.rabbitown.uuchat.chat.ChatElement;

public class PlayerTitleElement extends ChatElement {

    String plugin;

    public PlayerTitleElement() {
        super("builtin:player_title");
    }

    @Override
    public boolean loadElement() {
        String plugin = config.getString("plugin");
        if (plugin == null) {
            plugin = "PlayerTitle";
        }
        if (plugin.equals("PlayerTitle")) {
            if (Bukkit.getPluginManager().getPlugin("PlayerTitle") == null) {
                Bukkit.getLogger().warning("Can't load element \"" + name + "\": Unable to find PlayerTitle.");
                return false;
            }
            this.plugin = plugin;
        } else {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": Unknown setting \"" + plugin + "\".");
            return false;
        }
        if (config.getString("pattern") == null) {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": Cannot find the pattern.");
            return false;
        }
        try {
            addJSONEvents(new JsonObject(), config, null);
        } catch (ConfigurationException e) {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": " + e.getMessage());
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
            object.addProperty("text", parseGeneral(sender, config.getString("pattern")).replace("$title$", title));
        }
        try {
            addJSONEvents(object, config, sender);
        } catch (ConfigurationException e) {
        }
        return object;
    }

}