package com.rabbitown.yachat.nms;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;

public class v1_12_R1 implements NMSBase {

    @Override
    public String getVersion() {
        return "v1_12_R1";
    }

    @Override
    public void sendJSONMessage(String message) {
        IChatBaseComponent iMessage = IChatBaseComponent.ChatSerializer.a(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().sendMessage(IChatBaseComponent.ChatSerializer.a(message));
        }
        Bukkit.getConsoleSender().sendMessage(iMessage.toPlainText());
    }

    @Override
    public void sendActionbar(Player player, String message) {
        try {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, ChatSerializer.a(message)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}