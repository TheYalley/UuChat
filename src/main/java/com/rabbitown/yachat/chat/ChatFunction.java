package com.rabbitown.yachat.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import lombok.Getter;

/**
 * Represents a chat function.
 * 
 * @author Yoooooory
 */
public abstract class ChatFunction {

    /**
     * The function's name (prefer ID Namespace).<p>
     * <b>Example:</b> foo:bar
     */
    @Getter
    protected String name;

    /**
     * The function's configuration ('{@code functions.[name]}' section).<p>
     */
    @Getter
    protected ConfigurationSection config;

    /**
     * Construct a ChatFunction object.
     * 
     * @param name   The function's name (prefer ID Namespace).
     * @param config The function's configuration ('{@code functions.[name]}' section).
     */
    protected ChatFunction(String name, ConfigurationSection config) {
        this.name = name;
        this.config = config;
    }

    /**
     * This method will be called when loading the function (usually for format reloading).
     * 
     * @return The Load result. If return false, the function won't be enabled.
     */
    public abstract boolean loadFunction();

    /**
     * This method will be called when player sent message, and the message will be modified to the final return result.
     * 
     * @param message The message that might be already modified by other functions.
     * @param player  The message sender.
     * @return A {@link JsonArray} object of <a href="https://minecraft.gamepedia.com/Raw_JSON_text_format">raw JSON text
     *         format</a>.
     */
    public abstract JsonArray parseMessage(JsonArray message, Player player);

    /**
     * This method will be called to decide whether the message can be sent when player trying to send message.
     * 
     * @param message Original message.
     * @param sender  The message sender.
     * @return If return false, the message won't be sent.
     */
    public boolean checkLimit(String message, Player sender) {
        return true;
    }

}