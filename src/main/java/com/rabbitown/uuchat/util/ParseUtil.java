package com.rabbitown.uuchat.util;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.clip.placeholderapi.PlaceholderAPI;

public class ParseUtil {

    /**
     * 自动转换字符串，包括修改 $player$ 变量以及 PlaceholderAPI 变量等。
     * 
     * @param player 玩家
     * @param str    源字符串
     * @return 转换完毕后的字符串
     */
    public static String parseGeneral(Player player, String str) {
        if (player == null) {
            return str;
        }
        if (Bukkit.getPluginManager().getPlugin("UuChat").getConfig().getBoolean("general.chat.placeholder")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str.replace("$player$", player.getName());
    }


    /**
     * 自动从配置节中读取 clickEvent 和 hoverEvent 并添加到 JSON 对象中。
     * 
     * @param object JSON 文本对象，格式如“{"text":"foo"}”，不允许列表
     * @param config 指定的配置节，如默认配置 element.yml 中的 elements.world_name 节
     * @param player 参考玩家，可为 null。
     * @return 添加了 clickEvent 和 hoverEvent 后的 JSON 对象
     * @throws ConfigurationException 配置节中存在 clickEvent.action 却不存在 clickEvent.value
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