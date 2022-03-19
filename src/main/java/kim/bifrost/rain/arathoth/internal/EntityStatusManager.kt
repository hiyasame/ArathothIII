package kim.bifrost.rain.arathoth.internal

import kim.bifrost.rain.arathoth.api.ArathothEvents
import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.internal.ItemStatusManager.hasItemNode
import kim.bifrost.rain.arathoth.internal.ItemStatusManager.readAllAttribute
import kim.bifrost.rain.arathoth.utils.getEntityItems
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * kim.bifrost.rain.arathoth.internal.EntityStatusManager
 * Arathoth
 * 并不是所有实体都有属性的
 * 只会读取部分特殊实体的属性
 *
 * @author 寒雨
 * @since 2022/3/18 23:57
 **/
object EntityStatusManager {
    private val statusMap = Collections.synchronizedMap(WeakHashMap<UUID, Map<String, AttributeData>>())

    @Suppress("UNCHECKED_CAST")
    fun <T: AttributeData> Entity.status(key: AttributeKey<T>): T? {
        return statusMap[uniqueId]?.get("${key.namespace}.${key.name}") as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: AttributeData> Entity.status(key: AttributeKey.Builder<T>): T? {
        return statusMap[uniqueId]?.get("${key.namespace}.${key.name}") as? T
    }

    internal fun read(livingEntity: LivingEntity): Map<String, AttributeData> {
        val status = livingEntity.getEntityItems().filter { it.hasItemNode }.readAllAttribute(livingEntity)
        val event = ArathothEvents.Read(livingEntity, status)
        event.call()
        return event.data
    }

    internal fun read(player: Player): Map<String, AttributeData> {
        val status = player.inventory.contents.filter { it.hasItemNode }.readAllAttribute(player)
        val event = ArathothEvents.Read(player, status)
        event.call()
        return event.data
    }

    internal fun load(entity: Entity, data: Map<String, AttributeData>) {
        val event = ArathothEvents.Load(entity, data.toMutableMap())
        event.call()
        statusMap[entity.uniqueId] = event.data
    }

}