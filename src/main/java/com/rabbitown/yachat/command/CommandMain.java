package com.rabbitown.yachat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rabbitown.yachat.YaChat;

public class CommandMain implements CommandExecutor {

    YaChat plugin;

    public CommandMain(YaChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("yachat")) {
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    onReloadCommand(sender);
                } else {
                    sender.sendMessage("§cUnknown command.");
                }
            } else {
                sender.sendMessage("§cUnknown command.");
            }
        }
        return true;
    }

    void onReloadCommand(CommandSender sender) {
        if (sender.hasPermission("yachat.admin.reload")) {
            sender.sendMessage("§7Reloading plugin...");
            plugin.loadPlugin();
            sender.sendMessage("§aReloaded successfully!");
        } else {
            sender.sendMessage("§cPermission needing: §7yachat.admin.reload");
        }
    }

}