package kim.bifrost.rain.arathoth.internal.listener

import kim.bifrost.rain.arathoth.api.ArathothAPI
import kim.bifrost.rain.arathoth.api.LoadStatusParams
import kim.bifrost.rain.arathoth.utils.subscribe
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit

/**
 * kim.bifrost.rain.arathoth.internal.listener.ListenerAttributeLoad
 * Arathoth
 *
 *
 * @author 寒雨
 * @since 2022/3/19 21:32
 **/
object ListenerAttributeLoad {

    @Awake(LifeCycle.ACTIVE)
    internal fun init() {
        // 玩家属性加载
        subscribe<PlayerItemHeldEvent> {
            submit(async = true) {
                ArathothAPI.loadStatus(LoadStatusParams.Player, player)
            }
        }
        subscribe<PlayerSwapHandItemsEvent> {
            submit(async = true) {
                ArathothAPI.loadStatus(LoadStatusParams.Player, player)
            }
        }
        subscribe<PlayerItemBreakEvent> {
            submit(async = true) {
                ArathothAPI.loadStatus(LoadStatusParams.Player, player)
            }
        }
        subscribe<InventoryCloseEvent> {
            submit(async = true) {
                if (player is Player) {
                    ArathothAPI.loadStatus(LoadStatusParams.Player, player as Player)
                }
            }
        }
        // 怪物属性加载
        subscribe<EntitySpawnEvent> {
            submit(async = true) {
                if (entity is LivingEntity) {
                    ArathothAPI.loadStatus(LoadStatusParams.LivingEntity, entity as LivingEntity)
                }
            }
        }
//        subscribe<PlayerInteractEntityEvent> {
//            if (rightClicked is LivingEntity) {
//                ArathothAPI.loadStatus(LoadStatusParams.LivingEntity, rightClicked as LivingEntity)
//            }
//        }
        // 弓箭属性加载
        subscribe<EntityShootBowEvent> {
            submit(async = true) {
                if (projectile is Arrow) {
                    ArathothAPI.loadStatus(LoadStatusParams.Arrow(this@subscribe), projectile as Arrow)
                }
            }
        }
    }
}