package com.rabbitown.uuchat.chat;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.rabbitown.uuchat.util.ParseUtil;

public class ChatFormat {

    FileConfiguration config;

    ArrayList<ChatElement> elements = new ArrayList<ChatElement>();
    ArrayList<ChatFunction> functions = new ArrayList<ChatFunction>();
    ArrayList<ChatElement> activeElements = new ArrayList<ChatElement>();
    ArrayList<ChatFunction> activeFunctions = new ArrayList<ChatFunction>();

    public ChatFormat(FileConfiguration config) {
        this.config = config;
    }

    /**
     * 注册一个聊天元素。
     * 
     * @param element 要注册的聊天元素实例
     * @return 注册结果，成功返回 true，失败则返回 false。
     */
    public boolean registerElement(ChatElement element) {
        if (elements.stream().anyMatch(s -> s.getType().equals(element.getType()))) {
            Bukkit.getLogger().severe("Unable to register element \"" + element.getName() + "\": An element with the same name has already been registered.");
            return false;
        }
        elements.add(element);
        return true;
    }

    /**
     * 注册一个聊天函数。
     * 
     * @param function 要注册的聊天函数实例
     * @return 注册结果，成功返回 true，失败则返回 false。
     */
    public boolean registerFunction(ChatFunction function) {
        if (!function.getClass().isAnnotationPresent(FunctionHandle.class)) {
            Bukkit.getLogger().severe("Unable to register function \"" + function.getName() + "\": The class should be added @FunctionHandle to tell UuChat this is a function.");
            return false;
        }
        if (functions.stream().anyMatch(s -> s.getName().equals(function.getName()))) {
            Bukkit.getLogger().severe("Unable to register function \"" + function.getName() + "\": A function with the same name has already been registered.");
            return false;
        }
        functions.add(function);
        functions.sort((s1, s2) -> s1.getClass().getAnnotation(FunctionHandle.class).priority().compareTo(s2.getClass().getAnnotation(FunctionHandle.class).priority()));
        return true;
    }

    public boolean unregisterElement(String type) {
        return elements.removeIf(s -> s.getType().equals(type));
    }

    public boolean unregisterFunction(String name) {
        return functions.removeIf(s -> s.getName().equals(name));
    }

    public void loadFormat() {
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading chat formats...");
        activeElements = new ArrayList<ChatElement>();
        activeFunctions = new ArrayList<ChatFunction>();
        // Load elements
        {
            Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading elements...");
            String pattern = config.getString("general.chat.pattern");
            for (ChatElement celement : elements) {
                ChatElement element = celement.clone();
                ConfigurationSection elementConfig = element.getConfig().getConfigurationSection("elements");
                if (elementConfig == null) {
                    Bukkit.getLogger().severe("Unable to load element type \"" + element.getType() + "\".");
                    continue;
                }
                for (String key : elementConfig.getKeys(false)) {
                    if (pattern.contains("$" + key + "$") && elementConfig.getString(key + ".type").equals(element.getType())) {
                        element.setConfig(elementConfig.getConfigurationSection(key));
                        element.setName(key);
                        try {
                            if (element.loadElement()) {
                                activeElements.add(element);
                            } else {
                                Bukkit.getLogger().severe("Unable to load element \"" + element.getName() + "\".");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Bukkit.getLogger().severe("Unable to load element \"" + element.getName() + "\".");
                        }
                    }
                }
            }
        }
        // Load functions
        {
            Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §eLoading functions...");
            List<String> enableFunctions = config.getStringList("general.chat.functions");
            for (ChatFunction function : functions) {
                if (enableFunctions.contains(function.getName())) {
                    try {
                        if (function.loadFunction()) {
                            activeFunctions.add(function);
                        } else {
                            Bukkit.getLogger().severe("Unable to load function \"" + function.getName() + "\".");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Bukkit.getLogger().severe("Unable to load function \"" + function.getName() + "\".");
                    }
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage("§8[§7UuChat§8] §aFormats loaded successfully!");
    }

    @Nullable
    public JsonArray parseMessage(String message, Player player) {
        JsonArray json = new JsonArray();
        JsonArray jsonMsg = new JsonArray();
        jsonMsg.add(message);
        for (ChatFunction function : activeFunctions) {
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
                json.add("§r" + ParseUtil.parseGeneral(player, pattern.substring(index, indexStart)));
                int indexEnd = pattern.indexOf("$", indexStart + 1);
                String elementName = pattern.substring(indexStart + 1, indexEnd);
                if (elementName.equals("message")) {
                    json.add(jsonMsg);
                } else {
                    for (ChatElement element : activeElements) {
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
                json.add("§r" + ParseUtil.parseGeneral(player, pattern.substring(index)));
                break;
            }
        }
        return json;
    }

}