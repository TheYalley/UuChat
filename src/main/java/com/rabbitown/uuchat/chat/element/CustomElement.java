package com.rabbitown.uuchat.chat.element;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.rabbitown.uuchat.chat.ChatElement;
import com.rabbitown.uuchat.util.ParseUtil;

public class CustomElement extends ChatElement {

    public CustomElement(FileConfiguration config) {
        super("builtin:custom", config);
    }

    @Override
    public JsonElement parseMessage(String message, Player sender) {
        JsonElement pattern = new JsonObject();
        if (config.getString("pattern") != null) {
            // {"text":"pattern","hoverEvent":{"action":"show_text","value":"hover"}}
            JsonObject object = new JsonObject();
            object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")) + "§r");
            try {
                pattern = ParseUtil.addJSONEvents(object, config, sender);
            } catch (ConfigurationException e) {
            }
        } else if (config.getString("json") != null) {
            try {
                pattern = new JsonParser().parse(ParseUtil.parseGeneral(sender, config.getString("json")));
            } catch (JsonSyntaxException e) {
            }
        }
        return pattern;
    }

    @Override
    public boolean loadElement() {
        if (config.getString("pattern") != null) {
            try {
                ParseUtil.addJSONEvents(new JsonObject(), config, null);
            } catch (ConfigurationException e) {
                Bukkit.getLogger().warning("Can't load element \"" + name + "\": " + e.getMessage());
                return false;
            }
        } else if (config.getString("json") != null) {
            try {
                new JsonParser().parse(config.getString("json"));
            } catch (JsonSyntaxException e) {
                Bukkit.getLogger().warning("Can't load element \"" + name + "\": " + e.getMessage());
                return false;
            }
        } else {
            Bukkit.getLogger().warning("Can't load element \"" + name + "\": Cannot find a pattern or a json in config.yml.");
            return false;
        }
        return true;
    }

}
