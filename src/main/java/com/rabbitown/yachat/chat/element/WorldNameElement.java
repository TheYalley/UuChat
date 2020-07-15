package com.rabbitown.yachat.chat.element;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.rabbitown.yachat.chat.ChatElement;
import com.rabbitown.yachat.util.Logger;
import com.rabbitown.yachat.util.ParseUtil;

public class WorldNameElement extends ChatElement {

    String plugin;
    String display;

    public WorldNameElement(FileConfiguration config) {
        super("builtin:world_name", config);
    }

    @Override
    public boolean loadElement() {
        String plugin = config.getString("plugin");
        if (plugin == null) {
            plugin = "Vanilla";
        }
        if (plugin.equals("Multiverse-Core")) {
            if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") == null) {
                Logger.warning("Can't load element \"world_name\": Unable to find Multiverse-Core.");
                return false;
            }
            this.plugin = plugin;
            String display = config.getString("display");
            if (display == null) {
                display = "alias";
            }
            if (display.equals("alias") || display.equals("name")) {
                this.display = display;
            } else {
                Logger.warning("Can't load element \"world_name\": Unknown setting \"" + display + "\".");
                return false;
            }
        } else if (plugin.equals("Vanilla")) {
            this.plugin = plugin;
        } else {
            Logger.warning("Can't load element \"world_name\": Unknown setting \"" + plugin + "\".");
            return false;
        }
        if (config.getString("pattern") == null) {
            Logger.warning("Can't load element \"world_name\": Cannot find the pattern.");
            return false;
        }
        try {
            ParseUtil.addJSONEvents(new JsonObject(), config, null);
        } catch (ConfigurationException e) {
            Logger.warning("Can't load element \"world_name\": " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public JsonObject parseMessage(String message, Player sender) {
        JsonObject object = new JsonObject();
        if (plugin.equals("Vanilla")) {
            object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")).replace("$world$", sender.getWorld().getName()));
        } else if (plugin.equals("Multiverse-Core")) {
            MultiverseWorld world = ((MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core")).getMVWorldManager().getMVWorld(sender.getWorld());
            if (display.equals("alias")) {
                object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")).replace("$world$", world.getAlias()).replace('&', '§'));
            } else if (display.equals("name")) {
                object.addProperty("text", ParseUtil.parseGeneral(sender, config.getString("pattern")).replace("$world$", world.getName()));
            }
        }
        try {
            ParseUtil.addJSONEvents(object, config, sender);
        } catch (ConfigurationException e) {
        }
        return object;
    }

}