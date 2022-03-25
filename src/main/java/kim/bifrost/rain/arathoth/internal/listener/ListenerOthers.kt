package kim.bifrost.rain.arathoth.internal.listener

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.utils.subscribe
import kim.bifrost.rain.arathoth.utils.takeContent
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.module.chat.colored
import taboolib.module.kether.KetherShell
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.type.BukkitCommandSender

/**
 * kim.bifrost.rain.arathoth.internal.listener.ListenerOthers
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 17:21
 **/
object ListenerOthers {

    @Awake(LifeCycle.ACTIVE)
    internal fun init() {
        // 取消弓近战伤害
         subscribe<EntityDamageByEntityEvent>(priority = EventPriority.MONITOR) {
             if (
                 Arathoth.conf.getBoolean("settings.bow.cancel-entity-attack")
                 && damager is LivingEntity
                 && (damager as LivingEntity).equipment?.itemInMainHand?.type == Material.BOW
                 && cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
             ) {
                 isCancelled = true
             }
         }
        // 属性面板命令
        subscribe<PlayerCommandPreprocessEvent> {
            val perm = Arathoth.conf.getString("settings.status.perm", "")!!
            if (
                message == Arathoth.conf.getString("settings.status.message.command")
                && (perm.isEmpty() || player.hasPermission(perm))
            ) {
                isCancelled = true
                val hideZero = Arathoth.conf.getBoolean("settings.status.message.hide-zero")
                val minLines = Arathoth.conf.getInt("settings.status.message.min-lines")
                val noStatusAction = Arathoth.conf.getString("settings.status.message.no-status-action", "")!!
                val text = Arathoth.conf
                    .getStringList("settings.status.message.text")
                    .mapNotNull {
                        val str = it.takeContent("%arathoth_", "%") ?: return@mapNotNull it.replacePlaceholder(player)
                        if (str.replacePlaceholder(player) == "0" && hideZero) return@mapNotNull null
                        return@mapNotNull it.replacePlaceholder(player)
                    }
                if (text.size < minLines) {
                    KetherShell.eval(noStatusAction) {
                        sender = BukkitCommandSender(player)
                    }
                    return@subscribe
                }
                text.forEach {
                    player.sendMessage(it.colored())
                }
            }
        }
    }
}