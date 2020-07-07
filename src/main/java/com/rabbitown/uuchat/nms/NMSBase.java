package com.rabbitown.uuchat.nms;

import org.bukkit.entity.Player;

public interface NMSBase {

    /**
     * 获取该 NMS 的版本。
     * 
     * @return NMS 的版本
     */
    public String getVersion();

    public void sendJSONMessage(String message);

    public void sendActionbar(Player player, String message);

}