package com.rabbitown.yachat.chat;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.rabbitown.yachat.util.Logger;
import com.rabbitown.yachat.util.ParseUtil;

import lombok.Getter;

/**
 * The Yachat formatter.
 * 
 * @author Yoooooory
 */
public class ChatFormat {

    FileConfiguration config;

    @Getter
    ArrayList<ChatElement> elements = new ArrayList<ChatElement>();
    @Getter
    ArrayList<ChatFunction> functions = new ArrayList<ChatFunction>();
    @Getter
    ArrayList<ChatElement> activeElements = new ArrayList<ChatElement>();
    @Getter
    ArrayList<ChatFunction> activeFunctions = new ArrayList<ChatFunction>();

    /**
     * Construct a ChatFormat object.
     * 
     * @param config The config.yml file.
     */
    public ChatFormat(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Register a chat element.
     * 
     * @param element The element need to register.
     * @return If register succeed, it will return true.
     * @see ChatFormat#unregisterElement(String)
     */
    public boolean registerElement(ChatElement element) {
        if (elements.stream().anyMatch(s -> s.getType().equals(element.getType()))) {
            Logger.severe("Unable to register element \"" + element.getName() + "\": An element with the same name has already been registered.");
            return false;
        }
        elements.add(element);
        return true;
    }

    /**
     * Register a chat function.
     * 
     * @param function The function need to register.
     * @return If register succeed, it will return true.
     * @see ChatFormat#unregisterFunction(String)
     */
    public boolean registerFunction(ChatFunction function) {
        if (!function.getClass().isAnnotationPresent(FunctionHandle.class)) {
            Logger.severe("Unable to register function \"" + function.getName() + "\": The class should be added @FunctionHandle to tell YaChat this is a function.");
            return false;
        }
        if (functions.stream().anyMatch(s -> s.getName().equals(function.getName()))) {
            Logger.severe("Unable to register function \"" + function.getName() + "\": A function with the same name has already been registered.");
            return false;
        }
        functions.add(function);
        functions.sort((s1, s2) -> s1.getClass().getAnnotation(FunctionHandle.class).priority().compareTo(s2.getClass().getAnnotation(FunctionHandle.class).priority()));
        return true;
    }

    /**
     * Unregister a chat element.
     * 
     * @param type The type of the element need to unregister.
     * @return If no element found, it will return false.
     * @see ChatFormat#registerElement(ChatElement)
     */
    public boolean unregisterElement(String type) {
        return elements.removeIf(s -> s.getType().equals(type));
    }

    /**
     * Unregister a chat function.
     * 
     * @param name The name of the function need to unregister.
     * @return If no function found, it will return false.
     * @see ChatFormat#unregisterFunction(String)
     */
    public boolean unregisterFunction(String name) {
        return functions.removeIf(s -> s.getName().equals(name));
    }

    /**
     * Load chat formats.
     */
    public void loadFormat() {
        Logger.info("Loading chat formats...");
        activeElements = new ArrayList<ChatElement>();
        activeFunctions = new ArrayList<ChatFunction>();
        // Load elements
        {
            Logger.info("Loading elements...");
            String pattern = config.getString("general.chat.pattern");
            for (ChatElement celement : elements) {
                ChatElement element = celement.clone();
                ConfigurationSection elementConfig = element.getConfig().getConfigurationSection("elements");
                if (elementConfig == null) {
                    Logger.severe("Unable to load element type \"" + element.getType() + "\".");
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
                                Logger.severe("Unable to load element \"" + element.getName() + "\".");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Logger.severe("Unable to load element \"" + element.getName() + "\".");
                        }
                    }
                }
            }
        }
        // Load functions
        {
            Logger.info("Loading functions...");
            List<String> enableFunctions = config.getStringList("general.chat.functions");
            for (ChatFunction function : functions) {
                if (enableFunctions.contains(function.getName())) {
                    try {
                        if (function.loadFunction()) {
                            activeFunctions.add(function);
                        } else {
                            Logger.severe("Unable to load function \"" + function.getName() + "\".");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.severe("Unable to load function \"" + function.getName() + "\".");
                    }
                }
            }
        }
        Logger.info("§aFormats loaded successfully!");
    }

    /**
     * Parse message by using format.
     * 
     * @param message Original message.
     * @param player  The message sender.
     * @return A {@link JsonArray} object of <a href="https://minecraft.gamepedia.com/Raw_JSON_text_format">raw JSON text
     *         format</a>. If any active elements or functions refuse the message's sending, the method will return null.
     */
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