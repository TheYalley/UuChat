package com.rabbitown.uuchat.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;

public abstract class ChatElement implements Cloneable {

    /**
     * The element's name that defines in config.yml by users.
     */
    @Getter
    @Setter
    protected String name;

    /**
     * The element's type (prefer ID Namespace).<p>
     * <b>Example:</b> foo:bar
     */
    @Getter
    protected String type;

    /**
     * The element's configuration.<p>
     * Before reloading format, the config is the root of element.yml (or your config).<br>
     * After that, the config will change to the config's {@code elements.[name]} section.
     */
    @Getter
    @Setter
    protected ConfigurationSection config;

    /**
     * Construct a ChatElement object.
     * 
     * @param type   The element's type (prefer ID Namespace).
     * @param config The element's configuration (need a 'elements' section).
     * @see ChatElement#type
     * @see ChatElement#config
     */
    protected ChatElement(String type, FileConfiguration config) {
        this.type = type;
        this.config = config;
    }

    /**
     * This method will be called when loading the element (usually for format reloading).
     * 
     * @return The Load result. If return {@code false}, the element won't be enabled.
     */
    public abstract boolean loadElement();

    /**
     * This method will be called when player sent message. After all elements returned a {@link JsonElement}, the formatter
     * will connect them together to make up the complete message.
     * 
     * @param message Original message.
     * @param player  The message sender.
     * @return A {@link JsonElement} object of <a href="https://minecraft.gamepedia.com/Raw_JSON_text_format">raw JSON text
     *         format</a>.
     */
    public abstract JsonElement parseMessage(String message, Player sender);

    /**
     * This method will be called to decide whether the message can be sent when player trying to send message.
     * 
     * @param message Original message.
     * @param player  The message sender.
     * @return If return false, the message won't be sent.
     */
    public boolean checkLimit(String message, Player player) {
        return true;
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