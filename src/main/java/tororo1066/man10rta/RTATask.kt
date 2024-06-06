package tororo1066.man10rta

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerKickEvent
import tororo1066.man10rta.Man10RTA.Companion.sendPrefixMsg
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import java.time.Duration
import java.util.*
import java.util.function.Consumer

class RTATask: Thread() {

    val sEvent = SEvent(SJavaPlugin.plugin)

    override fun run() {
        val startDate = Date()
        var lock = true
        var finishedPlayer: String? = null
        var finishedAdvancement: Component? = null
        sEvent.register(BlockBreakEvent::class.java) { e ->
            if (e.block.type == Material.SPAWNER){
                e.player.sendPrefixMsg(SStr("&cスポナーは壊せません！"))
                e.isCancelled = true
                return@register
            }
        }
        sEvent.register(PlayerAdvancementDoneEvent::class.java){ e ->
            if (e.player.hasPermission("rta.exclude"))return@register
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
            if (data.isWriteAll){
                val list = SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.getStringList("clearTime")?:return@register
                list.add("${e.player.uniqueId}:${e.player.name}:${time}")
                SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime",list)
                SJavaPlugin.plugin.saveConfig()
            } else {
                SJavaPlugin.plugin.config.getConfigurationSection(data.name)?.set("clearTime","${e.player.uniqueId}:${e.player.name}:${time}")
                SJavaPlugin.plugin.saveConfig()
            }
            Bukkit.broadcast(Component.text(Man10RTA.prefix.toString() + "§e§l${e.player.name}§fが§r")
                .color(TextColor.color(0x00FF00))
                .append(Component.text("["))
                .append(data.displayName)
                .append(Component.text("]"))
                .append(Component.text("§fを達成した！\n§d§l達成時間: $time")),Server.BROADCAST_CHANNEL_USERS)

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
            sleep(1000)
        }

        if (SJavaPlugin.isFolia){
            Bukkit.getGlobalRegionScheduler().run(SJavaPlugin.plugin) {
                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
                    it.playSound(it.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.5f)
                    it.showTitle(
                        Title.title(
                            finishedAdvancement?.let { it1 ->
                                Component.text("進捗")
                                    .color(TextColor.color(0x00FF00))
                                    .append(Component.text("["))
                                    .append(it1)
                                    .append(Component.text("]"))
                                    .color(null)
                                    .append(Component.text("を達成した"))
                            } ?: return@forEach,
                            Component.text("§d§l達成者：${finishedPlayer}"),
                            Title.Times.of(Duration.ZERO, Duration.ofSeconds(5), Duration.ofSeconds(1))
                        )
                    )
                }
            }
        } else {
            Bukkit.getScheduler().runTask(SJavaPlugin.plugin, Runnable {
                Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f)
                    it.playSound(it.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.5f)
                    it.showTitle(Title.title(finishedAdvancement?.let { it1 ->
                        Component.text("進捗")
                            .color(TextColor.color(0x00FF00))
                            .append(Component.text("["))
                            .append(it1)
                            .append(Component.text("]"))
                            .color(null)
                            .append(Component.text("を達成した")) }?:return@forEach,
                        Component.text("§d§l達成者：${finishedPlayer}"), Title.Times.of(Duration.ZERO, Duration.ofSeconds(5), Duration.ofSeconds(1))))
                }
            })
        }

        interrupt()
    }

    override fun interrupt() {
        sEvent.unregisterAll()
        super.interrupt()
    }

    private fun between(firstDate: Date,minusDate: Date): String {
        val time = firstDate.time - minusDate.time
        val hour = time / 3600000
        val minute = (time - hour * 3600000) / 60000
        val second = (time - hour * 3600000 - minute * 60000) / 1000
//        val millisecond = time - hour * 3600000 - minute * 60000 - second * 1000
        return "${hour}:${if (minute.toString().length == 1) "0${minute}" else minute}" +
                ":${if (second.toString().length == 1) "0${second}" else second}"
//        return "${hour}:${if (minute.toString().length == 1) "0${minute}" else minute}" +
//                ":${if (second.toString().length == 1) "0${second}" else second}" +
//                ".${when(millisecond.toString().length) {
//                    1 -> "00${millisecond}"
//                    2 -> "0${millisecond}"
//                    else -> millisecond
//                }}"
    }
}