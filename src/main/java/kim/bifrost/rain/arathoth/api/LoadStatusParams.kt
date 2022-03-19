package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.internal.EntityStatusManager
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityShootBowEvent

/**
 * kim.bifrost.rain.arathoth.api.LoadStatusParams
 * Arathoth
 * 针对不同类型的实体读取属性的策略
 *
 * @author 寒雨
 * @since 2022/3/19 16:46
 **/
interface LoadStatusParams<T: Entity> {
    fun load(entity: T)

    object LivingEntity : LoadStatusParams<org.bukkit.entity.LivingEntity> {
        override fun load(entity: org.bukkit.entity.LivingEntity) {
            EntityStatusManager.load(entity, EntityStatusManager.read(entity))
        }
    }

    object Player : LoadStatusParams<org.bukkit.entity.Player> {
        override fun load(entity: org.bukkit.entity.Player) {
            EntityStatusManager.load(entity, EntityStatusManager.read(entity))
        }
    }

    class Arrow(private val event: EntityShootBowEvent) : LoadStatusParams<org.bukkit.entity.Arrow> {
        override fun load(entity: org.bukkit.entity.Arrow) {
            EntityStatusManager.load(entity, EntityStatusManager.read(event.entity))
        }
    }
}