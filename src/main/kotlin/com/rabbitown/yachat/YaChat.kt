package com.rabbitown.yachat

import com.rabbitown.yachat.common.Config
import com.rabbitown.yachat.common.Logger
import com.rabbitown.yalib.YaLibCentral
import com.rabbitown.yalib.module.locale.I18NPlugin
import com.rabbitown.yalib.module.locale.impl.PrefixedLocale
import org.bukkit.plugin.java.JavaPlugin

/**
 * The main class of YaChat.
 *
 * @author Yoooooory
 */
class YaChat : JavaPlugin(), I18NPlugin {

    init {
        instance = this
    }

    override fun getNewLocale() = PrefixedLocale(this)

    override fun onLoad() {
        YaLibCentral.registerPlugin(this)
    }

    override fun onEnable() {
        reload()
        Config.finalLoad()
    }

    override fun onDisable() {
        Logger.info("POP TEAM EPIC")
    }

    fun reload() {
        Config.reload()
    }

    companion object {
        lateinit var instance: YaChat private set
    }
}