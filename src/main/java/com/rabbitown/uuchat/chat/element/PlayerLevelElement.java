package com.rabbitown.uuchat.chat.element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import com.rabbitown.uuchat.chat.ChatElement;
import com.rabbitown.uuchat.util.ParseUtil;

public class PlayerLevelElement extends ChatElement {

    Map<String, String> patterns = new HashMap<String, String>();

    public PlayerLevelElement(FileConfiguration config) {
        super("builtin:player_level", config);
    }

    @Override
    public boolean loadElement() {
        for (String pattern : config.getStringList("patterns")) {
            String[] splited = pattern.split("\\|", 2);
            if (splited.length < 2) {
                Bukkit.getLogger().warning("Can't load element \"" + name + "\" pattern \"" + pattern + "§e\": Missing a pattern.");
                continue;
            }
            if (splited[0].equals("{ALL}") || Pattern.matches("\\d+\\.\\.\\d*", splited[0]) || Pattern.matches("\\d*\\.\\.\\d+", splited[0]) || Pattern.matches("\\d", splited[0])) {
                patterns.put(splited[0], splited[1]);
            } else {
                Bukkit.getLogger().warning("Can't load element \"" + name + "\" pattern \"" + pattern + "§e\": Unknown setting \"" + splited[0] + "§e\".");
                continue;
            }
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
        int level = sender.getLevel();
        for (String key : patterns.keySet()) {
            if (key.startsWith("..")) {
                // ..X
                if (level > Integer.valueOf(key.substring(2))) {
                    continue;
                }
            } else if (key.endsWith("..")) {
                // X..
                if (level < Integer.valueOf(key.substring(0, key.length() - 2))) {
                    continue;
                }
            } else {
                String[] splited = key.split("\\.\\.");
                if (splited.length == 1) {
                    // X
                    if (level != Integer.valueOf(splited[0])) {
                        continue;
                    }
                } else {
                    // X..X
                    if (level < Integer.valueOf(splited[0]) || level > Integer.valueOf(splited[1])) {
                        continue;
                    }
                }
            }
            JsonObject object = new JsonObject();
            object.addProperty("text", ParseUtil.parseGeneral(sender, patterns.get(key)).replace("$level$", String.valueOf(level)));
            try {
                return addJSONEvents(object, config, sender);
            } catch (ConfigurationException e) {
            }
        }
        return null;
    }

}
