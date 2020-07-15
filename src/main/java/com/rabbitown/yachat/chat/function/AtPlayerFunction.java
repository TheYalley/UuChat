// TODO rewrite FIXME
package com.rabbitown.yachat.chat.function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitown.yachat.YaChat;
import com.rabbitown.yachat.chat.ChatFunction;
import com.rabbitown.yachat.chat.FunctionHandle;
import com.rabbitown.yachat.util.Logger;
import com.rabbitown.yachat.util.ParseUtil;

@FunctionHandle
public class AtPlayerFunction extends ChatFunction {

    int cooldown;
    String pattern;
    String patternSelf;
    List<String> inputs;

    Map<String, Long> data = new HashMap<String, Long>();

    public AtPlayerFunction(ConfigurationSection config) {
        super("atplayer", config);
    }

    @Override
    public boolean loadFunction() {
        if (config.getInt("cooldown") <= 0) {
            Logger.warning("Can't load function \"atplayer\": Invaild cooldown value.");
            return false;
        }
        cooldown = config.getInt("cooldown");
        if (config.getString("pattern") == null) {
            Logger.warning("Can't load function \"atplayer\": Invaild pattern.");
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
        // Check permission
        if (!sender.hasPermission("uuchat.chat.atplayer")) {
            return message;
        }
        ArrayList<Player> foundPlayer = new ArrayList<Player>();
        for (Player player : Bukkit.getOnlinePlayers().stream().sorted(Comparator.comparing(Player::getName).reversed()).collect(Collectors.toList())) {
            if (parser(message, sender, player)) {
                foundPlayer.add(player);
            }
        }
        if (foundPlayer.isEmpty() && !checkLimit(sender)) {
            return message;
        }
        for (Player player : foundPlayer) {
            // Mentioned player
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
            player.sendTitle(title, subtitle, config.getInt("mention.fadeIn"), config.getInt("mention.stay"), config.getInt("mention.fadeOut"));
            JsonArray action = new JsonArray();
            action.add(actionbar);
            YaChat.NMS.sendActionbar(player, action.toString());
            player.playSound(sender.getLocation(), config.getString("mention.sound.name"), (float) config.getDouble("mention.sound.volume"), (float) config.getDouble("mention.sound.pitch"));
        }
        return new JsonParser().parse(message.toString().replace("\u200a", "")).getAsJsonArray();

    }

    private boolean parser(JsonArray message, Player sender, Player player) {
        boolean isFound = false;
        for (int i = 0; i < message.size(); i++) {
            JsonElement element = message.get(i);
            if (element.isJsonArray()) {
                // Array
                isFound = parser(element.getAsJsonArray(), sender, player);
                message.set(i, element);
            } else if (element.isJsonObject()) {
                // Object
                JsonElement textJson = element.getAsJsonObject().get("text");
                String text = null;
                if (textJson != null) {
                    try {
                        text = textJson.getAsString();
                    } catch (Exception e) {
                        continue;
                    }
                }
                String parsed = parseMention(text, sender, player.getName());
                if (!parsed.equals(text)) {
                    JsonObject jsonObj = element.getAsJsonObject();
                    jsonObj.remove("text");
                    jsonObj.addProperty("text", parsed);
                    message.set(i, jsonObj);
                    isFound = true;
                }
            } else {
                String text;
                try {
                    text = element.getAsString();
                } catch (Exception e) {
                    continue;
                }
                // String
                String parsed = parseMention(text, sender, player.getName());
                if (!parsed.equals(text)) {
                    JsonArray array = new JsonArray();
                    array.add(parsed);
                    message.set(i, array);
                    isFound = true;
                }
            }
        }
        return isFound;
    }

    private String parseMention(String message, Player sender, String player) {
        for (String input : inputs) {
            input = input.replace("$player$", player);
            int index = message.toLowerCase().indexOf(input.toLowerCase());
            boolean foundPlayer = false;
            while (index != -1) {
                if (index - 1 >= 0) {
                    if (message.charAt(index - 1) == '\u0007' || message.charAt(index - 1) == '\u200a') {
                        index = message.toLowerCase().indexOf(input.toLowerCase(), index + player.length());
                        continue;
                    }
                }
                foundPlayer = true;
                data.put(sender.getName(), new Date().getTime());
                String pattern = (sender.getName().equals(player) ? this.patternSelf : this.pattern).replace("$player$", '\u200a' + player);
                StringBuilder sb = new StringBuilder(message);
                sb.replace(index, index + input.length() - 1, pattern + "§r");
                message = sb.toString();
                index = message.toLowerCase().indexOf(input.toLowerCase(), index + pattern.length());
            }
            if (foundPlayer) {
                return message;
            }
        }
        return message;
    }

    public boolean checkLimit(Player player) {
        if (!player.hasPermission("uuchat.chat.atplayer")) {
            YaChat.NMS.sendJSONMessage("\"§c你没有 @ 他人的权限..\"");
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