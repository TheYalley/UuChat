package com.rabbitown.yachat.common

import com.rabbitown.yachat.YaChat
import org.bukkit.configuration.file.YamlConfiguration

/**
 * @author Yoooooory
 */
object Config {

    private val plugin = YaChat.instance
    private val dataFile = plugin.dataFolder.resolve("data.yml")

    internal fun finalLoad() {
        dataFile.createNewFile()
        val data = YamlConfiguration.loadConfiguration(dataFile)
        val star = data["pop-team-epic"].let { if (it == null || it !is Int) 1 else it }
        data.set("pop-team-epic", if (star + 1 > 13) 1 else star + 1)
        data.save(dataFile)
        Logger.logPopTeamEpic(star)
    }

    fun reload() {
    }

}