package com.rabbitown.yachat.chat.function;

import com.google.gson.JsonArray;
import com.rabbitown.yachat.chat.ChatFunction;
import com.rabbitown.yachat.chat.MessageHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@MessageHandler
public class ShowItemFunction extends ChatFunction {

    public ShowItemFunction(ConfigurationSection config) {
        super("builtin:show_item", config);
    }

    @Override
    public boolean loadFunction() {
        return false;
    }

    @Override
    public JsonArray parseMessage(JsonArray message, Player player) {
        return null;
    }
}
