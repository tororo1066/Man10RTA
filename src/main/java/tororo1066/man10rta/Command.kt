package tororo1066.man10rta

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Sound
import tororo1066.man10rta.Man10RTA.Companion.sendPrefixMsg
import tororo1066.man10rta.menu.SettingMenu
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandArgType
import java.time.Duration

@Suppress("UNUSED")
class Command: SCommand("rta",Man10RTA.prefix.toString(),"rta.op") {

    @SCommandBody
    val setting = command().addArg(SCommandArg("setting")).setPlayerExecutor {
        SettingMenu().open(it.sender)
    }

    @SCommandBody
    val start = command().addArg(SCommandArg("start")).setNormalExecutor {
        Bukkit.getOnlinePlayers().forEach {
            it.showTitle(Title.title(Component.text("§0§kaaa §l§eSTART!! §0§kaaa"),Component.text(""),Title.Times.of(
                Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1))))
            it.playSound(it.location, Sound.ENTITY_WITHER_SPAWN,1f,1f)
        }
        Man10RTA.nowTask = RTATask()
        Man10RTA.nowTask?.start()
    }

    @SCommandBody
    val forceStop = command().addArg(SCommandArg("forceStop")).setNormalExecutor {
        Man10RTA.nowTask?.interrupt()
    }

    @SCommandBody
    val clearResults = command().addArg(SCommandArg("clearResults")).addArg(SCommandArg(SCommandArgType.INT).addAlias("pasuwa-do"))
        .setNormalExecutor {
            if (it.args[1].toInt() != 10661066)return@setNormalExecutor
            SJavaPlugin.plugin.config.getKeys(false).forEach { key ->
                val section = SJavaPlugin.plugin.config.getConfigurationSection(key)?:return@forEach
                section.set("clearTime",null)
            }
            SJavaPlugin.plugin.saveConfig()
            it.sender.sendMessage("cleared")
        }

    @SCommandBody
    val hideGiveAllAdvancements = command().addArg(SCommandArg("hideGiveAllAdv")).setPlayerExecutor {
        if (SJavaPlugin.isFolia){
            Bukkit.getGlobalRegionScheduler().execute(SJavaPlugin.plugin) {
                it.sender.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            }
        } else {
            it.sender.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        }

        it.sender.performCommand("minecraft:advancement grant ${it.sender.name} everything")
        if (SJavaPlugin.isFolia){
            Bukkit.getGlobalRegionScheduler().runDelayed(SJavaPlugin.plugin, { _ ->
                it.sender.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
                it.sender.sendPrefixMsg(SStr("&a付与済み"))
            },20)

        } else {
            Bukkit.getScheduler().runTaskLater(SJavaPlugin.plugin, Runnable {
                it.sender.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
                it.sender.sendPrefixMsg(SStr("&a付与済み"))
            },20)
        }
    }
}