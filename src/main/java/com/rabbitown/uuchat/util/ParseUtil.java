package com.rabbitown.uuchat.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class ParseUtil {

    /**
     * 自动转换字符串，包括修改 $player$ 变量以及 PlaceholderAPI 变量等。
     * 
     * @param player 玩家
     * @param str    源字符串
     * @return 转换完毕后的字符串
     */
    public static String parseGeneral(Player player, String str) {
        if (player == null) {
            return str;
        }
        if (Bukkit.getPluginManager().getPlugin("UuChat").getConfig().getBoolean("general.chat.placeholder")) {
            str = PlaceholderAPI.setPlaceholders(player, str);
        }
        return str.replace("$player$", player.getName());
    }

}