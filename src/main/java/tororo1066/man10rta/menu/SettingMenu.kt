package tororo1066.man10rta.menu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import tororo1066.man10rta.Man10RTA
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem

class SettingMenu: LargeSInventory(SJavaPlugin.plugin, "SettingMenu") {

    override fun renderMenu(p: Player): Boolean {
        val items = arrayListOf<SInventoryItem>()
        Bukkit.advancementIterator().forEach {
            val data = Man10RTA.data[it.key.key]?:return@forEach
            val item = SInventoryItem(data.itemStack).apply { editMeta { meta -> meta.displayName(data.displayName) } }
                .addLore(if (data.isEnabled) "§a§l有効" else "§c§l無効")
                .addLore(if (data.isKillServer) "§a§lサーバーを落とす" else "§c§lサーバーを落とさない")
                .addLore(if (data.isWriteAll) "§a§l全てのプレイヤーを記録する" else "§c§l全てのプレイヤーを記録しない")
                .addLore("§e§lシフト左クリックで有効化切り替え")
                .addLore("§d§lシフト右クリックでサーバー落とし切り替え")
                .addLore("§b§l右クリックで全てのプレイヤー記録切り替え")
                .setCanClick(false).setClickEvent { e ->
                    if (e.click == ClickType.SHIFT_LEFT){
                        data.isEnabled = !data.isEnabled
                        SJavaPlugin.plugin.config.set("${it.key.key}.isEnabled", data.isEnabled)
                        SJavaPlugin.plugin.saveConfig()
                        allRenderMenu(p)
                    }
                    if (e.click == ClickType.SHIFT_RIGHT){
                        data.isKillServer = !data.isKillServer
                        SJavaPlugin.plugin.config.set("${it.key.key}.isKillServer", data.isKillServer)
                        SJavaPlugin.plugin.saveConfig()
                        allRenderMenu(p)
                    }
                    if (e.click == ClickType.RIGHT){
                        data.isWriteAll = !data.isWriteAll
                        SJavaPlugin.plugin.config.set("${it.key.key}.isWriteAll", data.isWriteAll)
                        SJavaPlugin.plugin.saveConfig()
                        allRenderMenu(p)
                    }

                }

            items.add(item)
        }

        setResourceItems(items)
        return true
    }
}