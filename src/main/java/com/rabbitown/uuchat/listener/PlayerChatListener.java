package com.rabbitown.uuchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.gson.JsonArray;
import com.rabbitown.uuchat.UuChat;

public class PlayerChatListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        JsonArray message = UuChat.formatter.parseMessage(event.getMessage(), event.getPlayer());
        if (message != null) {
            UuChat.NMS.sendJSONMessage(message.toString());
        }
    }

}