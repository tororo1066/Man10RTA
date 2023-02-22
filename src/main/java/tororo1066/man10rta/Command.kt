package tororo1066.man10rta

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound
import tororo1066.man10rta.menu.SettingMenu
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import java.time.Duration

class Command: SCommand("rta",Man10RTA.prefix.toString(),"rta.op") {

    @SCommandBody
    val setting = command().addArg(SCommandArg("setting")).setPlayerExecutor {
        SettingMenu().open(it.sender)
    }

    @SCommandBody
    val start = command().addArg(SCommandArg("start")).setNormalExecutor {
        Bukkit.getOnlinePlayers().forEach {
            it.showTitle(Title.title(Component.text("§0§kaaa §l§eSTART!! §0§kaaa"),Component.text(""),Title.Times.times(
                Duration.ZERO, Duration.ofSeconds(3), Duration.ofSeconds(1))))
            it.playSound(it.location, Sound.ENTITY_WITHER_SPAWN,1f,1f)
        }
        RTATask().start()
    }
}