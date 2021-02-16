package com.rabbitown.yachat.common

import com.rabbitown.yachat.YaChat
import com.rabbitown.yalib.module.locale.YLocale
import org.bukkit.Bukkit

/**
 * @author Yoooooory
 */
object Logger {

    private val prefix = YLocale.getConsoleMessage("prefix")

    fun info(message: String) = Bukkit.getConsoleSender().sendMessage(prefix + message)
    fun severe(message: String?) = YaChat.instance.logger.severe(message)
    fun warning(message: String?) = YaChat.instance.logger.warning(message)

    /** Mysterious function written when I'm bored.
     * @param star The starial number. */
    @JvmName("{ POP TEAM EPIC }")
    internal fun logPopTeamEpic(star: Int) {
        val message = when (star) {
            1 -> "第 1 星  君だけに教えるよ！"
            2 -> "第 2 星  ヘルプ、そそぐはアイドル！"
            3 -> "第 3 星  大地くんが新しいマネージャー？"
            4 -> "第 4 星  デビルボルケー登場！二人だけのライブ！"
            5 -> "第 5 星  しずくのライバルハート炎上中！"
            6 -> "第 6 星  三角関係！？強敵はころなパイセン"
            7 -> "第 7 星  部屋が一緒なら、戸籍も一緒に♪"
            8 -> "第 8 星  ドロップスターズ、解散の危機！？"
            9 -> "第 9 星  あなたに届け、私たちの新曲！"
            10 -> "第 10 星  満天のキス"
            11 -> "第 11 星  突然の別れ"
            12 -> "第 12 星  星降る大地、大切な約束"
            13 -> "第 1 星  アイドルか花嫁！選ぶのはどっち？"
            else -> "第 20 星  Bilibiliアカウント、20人のフォロワー"
        }
        info(message)
    }

}