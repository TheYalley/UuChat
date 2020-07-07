package com.rabbitown.uuchat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.rabbitown.uuchat.UuChat;

public class CommandMain implements CommandExecutor {

    UuChat plugin;

    public CommandMain(UuChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("uuchat")) {
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
        if (sender.hasPermission("uuchat.admin.reload")) {
            sender.sendMessage("§7Reloading plugin...");
            plugin.loadPlugin();
            sender.sendMessage("§aReloaded successfully!");
        } else {
            sender.sendMessage("§cPermission needing: §7uuchat.admin.reload");
        }
    }

}