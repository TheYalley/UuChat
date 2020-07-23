// TODO rewrite FIXME
package com.rabbitown.yachat.chat.function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rabbitown.yachat.chat.FunctionPriority;
import com.rabbitown.yachat.chat.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.rabbitown.yachat.YaChat;
import com.rabbitown.yachat.chat.ChatFunction;
import com.rabbitown.yachat.util.Logger;
import com.rabbitown.yachat.util.ParseUtil;

@MessageHandler(priority = FunctionPriority.LOWEST)
public class AtPlayerFunction extends ChatFunction {

    int cooldown;
    String pattern;
    String patternSelf;
    List<String> inputs;

    Map<String, Long> data = new HashMap<String, Long>();

    public AtPlayerFunction(ConfigurationSection config) {
        super("builtin:at_player", config);
    }

    @Override
    public boolean loadFunction() {
        if (config.getInt("cooldown") <= 0) {
            Logger.warning("Can't load function \"atplayer\": Invalid cooldown value.");
            return false;
        }
        cooldown = config.getInt("cooldown");
        if (config.getString("pattern") == null) {
            Logger.warning("Can't load function \"atplayer\": Invalid pattern.");
            return false;
        }
        pattern = config.getString("pattern");
        if (config.getString("pattern-self") == null) {
            patternSelf = pattern;
        } else {
            patternSelf = config.getString("pattern-self");
        }
        if (config.getStringList("input").size() < 1) {
            Logger.warning("Can't load function \"atplayer\": Empty input list.");
            return false;
        }
        if (config.getConfigurationSection("mention") == null) {
            Logger.warning("Can't load function \"atplayer\": No mention ways found.");
            return false;
        }
        inputs = config.getStringList("input");
        return true;
    }

    @Override
    public JsonArray parseMessage(JsonArray message, Player sender) {
        String raw = message.get(0).getAsString();
        ArrayList<Player> foundPlayer = new ArrayList<Player>();
        for (Player player : Bukkit.getOnlinePlayers().stream().sorted(Comparator.comparing(Player::getName).reversed()).collect(Collectors.toList())) {
            for (String input : inputs) {
                String result = ParseUtil.replaceIgnoreCase(raw, input.replace("$player$", player.getName()), (player.getUniqueId().equals(sender.getUniqueId()) ? patternSelf : pattern).replace("$player$", player.getName() + "§r"));
                if (!result.equals(raw)) {
                    // means the player has been mentioned
                    foundPlayer.add(player);
                    raw = result;
                    break;
                }
            }
        }
        if (!foundPlayer.isEmpty() && checkLimit(sender)) {
            message = new JsonArray();
            message.add(raw);
            data.put(sender.getName(), new Date().getTime());
            String title = null;
            String subtitle = null;
            String actionbar = null;
            if (config.getString("mention.title") != null) {
                title = ParseUtil.parseGeneral(sender, config.getString("mention.title"));
            } else if (config.getString("mention.subtitle") != null) {
                title = "";
            }
            if (config.getString("mention.subtitle") != null) {
                subtitle = ParseUtil.parseGeneral(sender, config.getString("mention.subtitle"));
            }
            if (config.getString("mention.actionbar") != null) {
                actionbar = ParseUtil.parseGeneral(sender, config.getString("mention.actionbar"));
            }
            JsonArray action = new JsonArray();
            action.add(actionbar);
            for (Player player : foundPlayer) {
                // Mentioned player
                player.sendTitle(title, subtitle, config.getInt("mention.fadeIn"), config.getInt("mention.stay"), config.getInt("mention.fadeOut"));
                YaChat.NMS.sendActionbar(player, action.toString());
                player.playSound(sender.getLocation(), config.getString("mention.sound.name"), (float) config.getDouble("mention.sound.volume"), (float) config.getDouble("mention.sound.pitch"));
            }
        }
        return message;
    }

    public boolean checkLimit(Player player) {
        if (!player.hasPermission("yachat.chat.atplayer")) {
            YaChat.NMS.sendActionbar(player, "\"§c你没有 @ 他人的权限..\"");
            return false;
        } else if (data.get(player.getName()) == null) {
            return true;
        } else if (data.get(player.getName()) + cooldown > new Date().getTime() && !player.hasPermission("uuchat.chat.atplayer.bypass")) {
            YaChat.NMS.sendActionbar(player, "\"§c@ 功能冷却中..\"");
            return false;
        }
        return true;
    }

}