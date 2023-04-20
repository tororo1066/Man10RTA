package tororo1066.man10rta

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.advancement.Advancement
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin

class RTATaskData {

    var name = ""
    lateinit var displayName: Component
    lateinit var advancement: Advancement
    lateinit var itemStack: ItemStack
    var isEnabled = false
    var isKillServer = false
    var isWriteAll = false
    var count = false

    companion object{
        fun load(advancement: Advancement): RTATaskData? {
            if (advancement.display == null)return null
            val data = RTATaskData()
            data.advancement = advancement
            data.name = advancement.key.key
            data.displayName = advancement.display!!.title()
            data.itemStack = advancement.display!!.icon()

            val config = SJavaPlugin.plugin.config.getConfigurationSection(data.name)?:return data
            data.isEnabled = config.getBoolean("isEnabled")
            data.isKillServer = config.getBoolean("isKillServer")
            data.isWriteAll = config.getBoolean("isWriteAll")


            return data
        }
    }


}