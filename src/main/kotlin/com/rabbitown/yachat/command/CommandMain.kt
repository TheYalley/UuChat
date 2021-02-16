package com.rabbitown.yachat.command

import com.rabbitown.yachat.YaChat
import com.rabbitown.yalib.module.command.SimpleCommandRemote
import com.rabbitown.yalib.module.command.annotation.Access
import com.rabbitown.yalib.module.command.annotation.Action
import com.rabbitown.yalib.module.locale.YLocale.Companion.sendLocale
import org.bukkit.command.CommandSender

/**
 * @author Yoooooory
 */
class CommandMain : SimpleCommandRemote("yachat", YaChat.instance, listOf("yc", "chat")) {

    @Action("reload")
    @Access(["yachat.command.reload"])
    fun reload(sender: CommandSender) {
        YaChat.instance.reload()
        sender.sendLocale("command.reload.succeed")
    }

}