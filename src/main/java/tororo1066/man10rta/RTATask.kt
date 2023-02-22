package tororo1066.man10rta

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerKickEvent
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.*

class RTATask: Thread() {

    override fun run() {
        val startDate = Date()
        var lock = true
        var finishedPlayer: String? = null
        var finishedAdvancement: Component? = null
        SEvent(SJavaPlugin.plugin).register(PlayerAdvancementDoneEvent::class.java){ e ->
            if (e.player.isOp)return@register
            val data = Man10RTA.data[e.advancement.key.key]?:return@register
            if (!data.isEnabled)return@register
            val nowDate = Date()
            val time = between(nowDate,startDate)
            if (data.count){
                e.player.sendMessage(Component.text(Man10RTA.prefix.toString()).append(data.displayName).append(Component.text("§fを達成しました！ §d§l達成時間: $time")))
                if (data.isWriteAll){
                    val list = SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.getStringList("clearTime")?:return@register
                    list.add("${e.player.uniqueId}:${e.player.name}:${between(Date(),startDate)}")
                    SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime",list)
                    SJavaPlugin.plugin.saveConfig()
                    return@register
                }
            }
            data.count = true
            SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime","${e.player.uniqueId}:${e.player.name}:${time}")
            SJavaPlugin.plugin.saveConfig()
            Bukkit.broadcast(Component.text(Man10RTA.prefix.toString() + "§e§l${e.player.name}§fが§r").append(data.displayName).append(Component.text("§fを達成しました！ §d§l達成時間: $time")),Server.BROADCAST_CHANNEL_USERS)

            if (data.isKillServer){
                finishedPlayer = e.player.name
                finishedAdvancement = data.displayName
                lock = false
            }
        }

        while (lock) {
            Bukkit.getOnlinePlayers().forEach {
                it.sendActionBar(SStr("&a&l経過時間: &d&l${between(Date(),startDate)}").toPaperComponent())
            }
            sleep(50)
        }

        Bukkit.getScheduler().runTask(SJavaPlugin.plugin, Runnable {
            Bukkit.getOnlinePlayers().forEach {
                it.kick(finishedAdvancement?.let { it1 -> Component.text("${finishedPlayer}が").append(it1).append(Component.text("を達成しました")) },PlayerKickEvent.Cause.TIMEOUT)
            }
        })
    }

    private fun between(firstDate: Date,minusDate: Date): String {
        val time = firstDate.time - minusDate.time
        val hour = time / 3600000
        val minute = (time - hour * 3600000) / 6000
        val second = (time - hour * 36000000 - minute * 6000) / 1000
        val millisecond = time - hour * 36000000 - minute * 6000 - second * 1000
        return "${hour}:${minute}:${second}.${millisecond}"
    }
}