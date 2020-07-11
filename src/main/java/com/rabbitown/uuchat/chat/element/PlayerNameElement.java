package com.rabbitown.uuchat.chat.element;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbitown.uuchat.chat.ChatElement;
import com.rabbitown.uuchat.util.ParseUtil;

public class PlayerNameElement extends ChatElement {

    String display;

    public PlayerNameElement(FileConfiguration config) {
        super("builtin:player_name", config);
    }

    @Override
    public boolean loadElement() {
        String display = config.getString("display");
        if (display != null) {
            if (display.equals("displayname") || display.equals("listname") || display.equals("name")) {
                this.display = display;
            }
        } else {
            this.display = "displayname";
        }
        if (config.getString("pattern") == null) {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": Cannot find the pattern.");
            return false;
        }
        try {
            ParseUtil.addJSONEvents(new JsonObject(), config, null);
        } catch (ConfigurationException e) {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public JsonElement parseMessage(String message, Player sender) {
        JsonObject object = new JsonObject();
        String name;
        switch (display) {
        case "displayname":
            name = sender.getDisplayName();
            break;
        case "listname":
            name = sender.getPlayerListName();
            break;
        case "name":
            name = sender.getName();
            break;
        default:
            return null;
        }
        object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")).replace("$name$", name));
        try {
            return ParseUtil.addJSONEvents(object, config, sender);
        } catch (ConfigurationException e) {
            return null;
        }
    }

}
