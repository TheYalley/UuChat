package com.rabbitown.yachat.nms;

import org.bukkit.entity.Player;

public interface NMSBase {

    /**
     * Get the NMS' version.
     * 
     * @return The NMS' version.
     */
    public String getVersion();

    /**
     * Send a raw JSON text message to the whole server including online players and server console.
     * 
     * @param message The raw message to be sent.
     */
    public void sendJSONMessage(String message);

    /**
     * Send an actionbar message to a player.
     * 
     * @param player  The player.
     * @param message The message to be sent.
     */
    public void sendActionbar(Player player, String message);

}