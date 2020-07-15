package com.rabbitown.yachat.util;

import org.bukkit.Bukkit;

/**
 * YaChat logger.
 * 
 * @author Yoooooory
 */
public class Logger {

    public static String prefix = "§8[§7YaChat§8] §e";

    /**
     * Log a INFO message.
     * 
     * @param message The message to log.
     */
    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + message);
    }

    /**
     * Log a SEVERE message.
     * 
     * @param message The message to log.
     */
    public static void severe(String message) {
        Bukkit.getLogger().severe(message);
    }

    /**
     * Log a WARNING message.
     * 
     * @param message The message to log.
     */
    public static void warning(String message) {
        Bukkit.getLogger().warning(message);
    }

}
