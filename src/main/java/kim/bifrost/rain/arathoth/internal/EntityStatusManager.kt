package kim.bifrost.rain.arathoth.internal

import kim.bifrost.rain.arathoth.api.AttributeKey
import org.bukkit.entity.Entity
import java.util.*
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
    private val statusMap = WeakHashMap<UUID, HashMap<String, Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> Entity.status(key: AttributeKey<T>): T? {
        return statusMap[uniqueId]?.get("${key.namespace}.${key.name}") as? T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> Entity.status(key: AttributeKey.Builder<T>): T? {
        return statusMap[uniqueId]?.get("${key.namespace}.${key.name}") as? T
    }
}