package com.rabbitown.yachat.util;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.clip.placeholderapi.PlaceholderAPI;

public class ParseUtil {

    /**
     * Automatically convert string.<p>
     * Replace '{@code $player$}' and PlaceholderAPI varibles (if enabled).
     * 
     * @param player The player. If got null then return the original string back.
     * @param str    The source string.
     * @return The parsed string.
     */
    public static String parseGeneral(Player player, String str) {
        if (player == null) {
            return str;
        }
        if (Bukkit.getPluginManager().getPlugin("YaChat").getConfig().getBoolean("general.chat.placeholder")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str.replace("$player$", player.getName());
    }

    /**
     * Automatically add clickEvent and hoverEvent read from the config section to the {@link JSONObject}.
     * 
     * @param object A {@link JSONObject} with raw JSON text such as '<code>{"text":"foo"}</code>'.
     * @param config A specified config section, such as '{@code elements.world_name}' section in element.yml.
     * @param player A reference player. If got null then won't parse string with
     *               {@link ParseUtil#parseGeneral(Player, String)}
     * @return The {@link JsonObject} that added clickEvent and hoverEvent.
     * @throws ConfigurationException If config has the '{@code clickEvent.action}' section but missing
     *                                '{@code clickEvent.value}' section.
     */

    public static JsonObject addJSONEvents(JsonObject object, ConfigurationSection config, Player player) throws ConfigurationException {
        if (config.getString("style.clickEvent.action") != null) {
            if (config.getString("style.clickEvent.value") != null) {
                JsonObject clickobj = new JsonObject();
                String action;
                switch (config.getString("style.clickEvent.action")) {
                case "run":
                    action = "run_command";
                    break;
                case "suggest":
                    action = "suggest_command";
                    break;
                case "open":
                    action = "open_url";
                    break;
                default:
                    throw new ConfigurationException("Unknown action \"" + config.getString("style.clickEvent.action") + "\"");
                }
                clickobj.addProperty("action", action);
                clickobj.addProperty("value", ParseUtil.parseGeneral(player, config.getString("style.clickEvent.value")));
                object.add("clickEvent", clickobj);
            } else {
                throw new ConfigurationException("ClickEvent value is null");
            }
        }
        if (!config.getStringList("style.hoverEvent").isEmpty()) {
            StringBuilder sb = new StringBuilder();
            config.getStringList("style.hoverEvent").forEach(s -> sb.append("§r" + ParseUtil.parseGeneral(player, s) + "\n"));
            JsonObject hoverobj = new JsonObject();
            hoverobj.addProperty("action", "show_text");
            hoverobj.addProperty("value", sb.toString().substring(0, sb.length() - 1));
            object.add("hoverEvent", hoverobj);
        }
        return object;
    }

}