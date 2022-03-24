package kim.bifrost.rain.arathoth.internal.listener

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.utils.lore
import kim.bifrost.rain.arathoth.utils.stripColor
import kim.bifrost.rain.arathoth.utils.subscribe
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.platform.util.modifyLore

/**
 * kim.bifrost.rain.arathoth.internal.listener.ListenerBind
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/24 19:01
 **/
object ListenerBind {
    private val enable: Boolean
        get() = Arathoth.conf.getBoolean("settings.lore.enable")

    private val pattern: String
        get() = Arathoth.conf.getString("settings.lore.bind.auto-bind", "Owner: %player%")!!

    private val enablePick: Boolean
        get() = Arathoth.conf.getBoolean("settings.lore.bind.listener.pick", false)

    private val enableClick: Boolean
        get() = Arathoth.conf.getBoolean("settings.lore.bind.listener.click", false)

    private val enableInteract: Boolean
        get() = Arathoth.conf.getBoolean("settings.lore.bind.listener.interact", false)

    @Awake(LifeCycle.ENABLE)
    internal fun init() {
        subscribe<PlayerPickupItemEvent> {
            if (enable && enablePick) {
                val line = item.itemStack.lore.indexOfFirst { it.stripColor().contains(pattern) }
                if (line != -1) {
                    item.itemStack = item.itemStack.modifyLore {
                        set(line, get(line).replace("%player%", player.name))
                    }
                }
            }
        }
        subscribe<InventoryClickEvent> {
            if (enable && enableClick) {
                currentItem?.let {
                    val line = currentItem!!.lore.indexOfFirst { it.stripColor().contains(pattern) }
                    if (line != -1) {
                        currentItem = currentItem!!.modifyLore {
                            set(line, get(line).replace("%player%", whoClicked.name))
                        }
                    }
                }
            }
        }
        subscribe<PlayerInteractEvent> {
            if (enable && enableInteract) {
                item?.let {
                    val line = item!!.lore.indexOfFirst { it.stripColor().contains(pattern) }
                    if (line != -1) {
                        item!!.itemMeta = item?.modifyLore {
                            set(line, get(line).replace("%player%", player.name))
                        }?.itemMeta
                    }
                }
            }
        }
    }
}