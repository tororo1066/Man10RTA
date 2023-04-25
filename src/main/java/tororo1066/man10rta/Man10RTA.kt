package tororo1066.man10rta

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.utils.sendMessage
import java.util.UUID

class Man10RTA: SJavaPlugin() {

    companion object{
        val data = HashMap<String,RTATaskData>()
        val prefix = SStr("&f[&d&lMan10&a&lRTA&f]&r")
        var nowTask: RTATask? = null

        fun CommandSender.sendPrefixMsg(msg: SStr){
            this.sendMessage(prefix + msg)
        }
    }

    override fun onLoad() {
        isFolia = try {
            Bukkit.getGlobalRegionScheduler()
            true
        } catch (e: Exception){
            false
        }
        Bukkit.broadcastMessage(isFolia.toString())
    }

    override fun onStart() {

        Bukkit.broadcastMessage(isFolia.toString())

        Command()
        saveDefaultConfig()
        Bukkit.advancementIterator().forEach {
            data[it.key.key] = RTATaskData.load(it)?:return@forEach
        }
    }
}