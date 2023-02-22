package tororo1066.man10rta

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerKickEvent
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import java.text.SimpleDateFormat
import java.util.*

class RTATask: Thread() {

    override fun start() {
        val dateFormat = SimpleDateFormat("HH:mm:ss.SSS")
        val startDate = Date()
        sleep(10)
        val debugDate = Date()
        Bukkit.broadcastMessage((debugDate.time-startDate.time).toString())
        var lock = true
        var finishedPlayer: String? = null
        var finishedAdvancement: Component? = null
        SEvent(SJavaPlugin.plugin).register(PlayerAdvancementDoneEvent::class.java){ e ->
            if (e.player.isOp)return@register
            val data = Man10RTA.data[e.advancement.key.key]?:return@register
            if (!data.isEnabled)return@register
            val nowDate = Date()
            if (data.count){
                e.player.sendMessage(Component.text(Man10RTA.prefix.toString()).append(data.displayName).append(Component.text("§fを達成しました！ §d§l達成時間: ${dateFormat.format(Date(nowDate.time-startDate.time))}")))
                if (data.isWriteAll){
                    val list = SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.getStringList("clearTime")?:return@register
                    list.add("${e.player.uniqueId}:${e.player.name}:${dateFormat.format(Date(nowDate.time-startDate.time))}")
                    SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime",list)
                    return@register
                }
            }
            data.count = true
            SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime","${e.player.uniqueId}:${e.player.name}:${dateFormat.format(Date(nowDate.time-startDate.time))}")
            SJavaPlugin.plugin.saveConfig()
            Bukkit.broadcast(Component.text(Man10RTA.prefix.toString() + "§e§l${e.player.name}§fが§r").append(data.displayName).append(Component.text("§fを達成しました！ §d§l達成時間: ${dateFormat.format(Date(nowDate.time-startDate.time))}")),Server.BROADCAST_CHANNEL_USERS)
            (Man10RTA.prefix + SStr("&e&l${e.player.name}&fが&a${data.displayName}&fを達成しました！ &d&l達成時間: ${dateFormat.format(Date(nowDate.time-startDate.time))}")).broadcast()

            if (data.isKillServer){
                finishedPlayer = e.player.name
                finishedAdvancement = data.displayName
                lock = false
            }
        }

        while (lock) {
            Bukkit.getOnlinePlayers().forEach {
                it.sendActionBar(SStr("&a&l経過時間: &d&l${dateFormat.format(Date(Date().time - startDate.time))}").toPaperComponent())
            }
            sleep(1000)
        }

        Bukkit.getOnlinePlayers().forEach {
            it.kick(finishedAdvancement?.let { it1 -> Component.text("${finishedPlayer}が").append(it1).append(Component.text("を達成しました")) },PlayerKickEvent.Cause.TIMEOUT)
        }
    }
}