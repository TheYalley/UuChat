package com.rabbitown.uuchat.chat;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatFormat {

    FileConfiguration config;

    ArrayList<ChatElement> elements = new ArrayList<ChatElement>();
    ArrayList<ChatFunction> functions = new ArrayList<ChatFunction>();

    public ChatFormat(FileConfiguration config) {
        this.config = config;
    }

    /**
     * 注册一个聊天元素，只能注册 config.yml 里 general.chat.pattern 中存在的聊天元素对象。
     * 
     * @param type    要注册的聊天元素类型名称
     * @param element 要注册的聊天元素类
     * @param config  要注册的聊天元素配置（element.yml）
     * @return 注册成功的次数
     */
    public int registerElement(String type, Class<? extends ChatElement> element, FileConfiguration config) {
        int count = 0;
        int index = 0;
        String pattern = this.config.getString("general.chat.pattern");
        while (true) {
            int indexStart = pattern.indexOf("$", index);
            if (indexStart != -1) {
                int indexEnd = pattern.indexOf("$", indexStart + 1);
                String elementName = pattern.substring(indexStart + 1, indexEnd);
                String elementType = config.getString("elements." + elementName + ".type");
                if (elementType != null && elementType.equals(type)) {
                    ChatElement celement;
                    try {
                        celement = element.getConstructor(String.class, ConfigurationSection.class).newInstance(elementName, config.getConfigurationSection("elements." + elementName));
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                        Bukkit.getLogger().severe("Unable to register element type \"" + type + "\".");
                        return 0;
                    }
                    if (celement.loadElement()) {
                        elements.add(celement);
                        count++;
                    }
                }
                index = indexEnd + 1;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 注册一个聊天函数，只有已被启用的函数才能被注册。
     * 
     * @param function 要注册的聊天函数实例
     * @param config   要注册的聊天函数配置项
     * @return 注册结果，成功则返回 true，失败则返回 false。
     */
    public boolean registerFunction(ChatFunction function) {
        if (functions.stream().anyMatch(s -> s.getName().equals(function.getName()))) {
            Bukkit.getLogger().severe("Unable to register function \"" + function.getName() + "\": An identical function has already been registered.");
            return false;
        }
        if (!this.config.getStringList("general.chat.functions").contains(function.getName())) {
            return false;
        }
        try {
            if (!function.loadFunction()) {
                Bukkit.getLogger().severe("Unable to register function \"" + function.getName() + "\".");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Unable to register function \"" + function.getName() + "\".");
            return false;
        }
        functions.add(function);
        return true;
    }

    @Nullable
    public JsonArray parseMessage(String message, Player player) {
        JsonArray json = new JsonArray();
        JsonArray jsonMsg = new JsonArray();
        jsonMsg.add(message);
        for (ChatFunction function : functions) {
            if (function.checkLimit(message, player)) {
                jsonMsg = function.parseMessage(jsonMsg, player);
            } else {
                return null;
            }
        }
        String pattern = config.getString("general.chat.pattern");
        int index = 0;
        while (true) {
            int indexStart = pattern.indexOf("$", index);
            if (indexStart != -1) {
                json.add("§r" + parseGeneral(player, pattern.substring(index, indexStart)));
                int indexEnd = pattern.indexOf("$", indexStart + 1);
                String elementName = pattern.substring(indexStart + 1, indexEnd);
                if (elementName.equals("message")) {
                    json.add(jsonMsg);
                } else {
                    for (ChatElement element : elements) {
                        if (element.getName().equals(elementName)) {
                            if (element.checkLimit(message, player)) {
                                JsonElement pMessage = element.parseMessage(message, player);
                                if (pMessage != null) {
                                    json.add(pMessage);
                                }
                            } else {
                                return null;
                            }
                            break;
                        }
                    }
                }
                index = indexEnd + 1;
            } else {
                json.add("§r" + parseGeneral(player, pattern.substring(index)));
                break;
            }
        }
        return json;
    }

    protected String parseGeneral(Player player, String str) {
        if (player == null) {
            return str;
        }
        if (Bukkit.getPluginManager().getPlugin("UuChat").getConfig().getBoolean("general.chat.placeholder")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str.replace("$player$", player.getName());
    }

}