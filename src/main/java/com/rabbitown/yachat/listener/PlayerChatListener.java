package com.rabbitown.yachat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.gson.JsonArray;
import com.rabbitown.yachat.YaChat;

public class PlayerChatListener implements Listener {

    /**
     * Handle player chatting event.<p>
     * 
     * @param event An AsyncPlayerChatEvent.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        JsonArray message = YaChat.formatter.parseMessage(event.getMessage(), event.getPlayer());
        if (message != null) {
            YaChat.NMS.sendJSONMessage(message.toString());
        }
    }

}