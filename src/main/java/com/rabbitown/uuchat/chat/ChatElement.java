package com.rabbitown.uuchat.chat;

import javax.naming.ConfigurationException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;

public abstract class ChatElement implements Cloneable {

    @Getter
    @Setter
    protected String name;

    @Getter
    protected String type;

    @Getter
    @Setter
    protected ConfigurationSection config;

    /**
     * 构造一个聊天元素对象。
     * 
     * @param name   聊天元素的名称
     * @param config 聊天元素的配置项
     */
    protected ChatElement(String type, FileConfiguration config) {
        this.type = type;
        this.config = config;
    }

    /**
     * 加载该聊天元素时会调用此方法。
     * 
     * @return 聊天元素是否加载成功，若此处返回为 false 则不会在 ChatFormat 中注册该元素。
     */
    public abstract boolean loadElement();

    /**
     * 自动从配置节中读取 clickEvent 和 hoverEvent 并添加到 JSON 对象中。
     * 
     * @param object JSON 文本对象，格式如“{"text":"foo"}”，不允许列表
     * @param config 指定的配置节，如默认配置 element.yml 中的 elements.world_name 节
     * @param player 参考玩家，可为 null。
     * @return 添加了 clickEvent 和 hoverEvent 后的 JSON 对象
     * @throws ConfigurationException 配置节中存在 clickEvent.action 却不存在 clickEvent.value
     */
    final protected JsonObject addJSONEvents(JsonObject object, ConfigurationSection config, Player player) throws ConfigurationException {
        object = addJSONClickEvent(object, config, player);
        object = addJSONHoverEvent(object, config, player);
        return object;
    }

    protected JsonObject addJSONClickEvent(JsonObject object, ConfigurationSection config, Player player) throws ConfigurationException {
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
                clickobj.addProperty("value", parseGeneral(player, config.getString("style.clickEvent.value")));
                object.add("clickEvent", clickobj);
            } else {
                throw new ConfigurationException("ClickEvent value is null");
            }
        }
        return object;
    }

    protected JsonObject addJSONHoverEvent(JsonObject object, ConfigurationSection config, Player player) {
        if (!config.getStringList("style.hoverEvent").isEmpty()) {
            StringBuilder sb = new StringBuilder();
            config.getStringList("style.hoverEvent").forEach(s -> sb.append("§r" + parseGeneral(player, s) + "\n"));
            JsonObject hoverobj = new JsonObject();
            hoverobj.addProperty("action", "show_text");
            hoverobj.addProperty("value", sb.toString().substring(0, sb.length() - 1));
            object.add("hoverEvent", hoverobj);
        }
        return object;
    }

    /**
     * 插件监听到聊天事件时会逐个调用已注册聊天元素的这个方法，并将获取到的 JSON 文本按配置中的设置拼接在一起。
     * 
     * @param message 聊天消息
     * @param player  发送该聊天的玩家
     * @return 原始 JSON 文本对象
     */
    public abstract JsonElement parseMessage(String message, Player sender);

    /**
     * 插件监听到聊天事件时会逐个调用已注册聊天元素的这个方法，用以检查聊天限制，决定这条消息能否被发送。
     * 
     * @param message 聊天消息
     * @param player  发送该聊天的玩家
     * @return 若为 true，则消息能够发送；若为 false，该消息将被拦截并无法发送。
     */
    public boolean checkLimit(String message, Player player) {
        return true;
    }

    /**
     * 自动转换字符串，包括修改 $player$ 变量以及 PlaceholderAPI 变量等。
     * 
     * @param player 玩家
     * @param str    源字符串
     * @return 转换完毕后的字符串
     */
    protected String parseGeneral(Player player, String str) {
        if (player == null) {
            return str;
        }
        if (Bukkit.getPluginManager().getPlugin("UuChat").getConfig().getBoolean("general.chat.placeholder")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str.replace("$player$", player.getName());
    }

    @Override
    public ChatElement clone() {
        try {
            return (ChatElement) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}