package kim.bifrost.rain.arathoth.internal

import kim.bifrost.rain.arathoth.api.ArathothEvents
import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.ExtraAttributeParser
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.internal.set.AttributeSet
import kim.bifrost.rain.arathoth.internal.set.rule.Rule
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

/**
 * kim.bifrost.rain.arathoth.internal.ItemStatusManager
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 20:18
 **/
object ItemStatusManager {

    val ItemStack.hasItemNode: Boolean
        get() = getItemTag().getDeep("Arathoth.attrNode") != null

    val ItemStack.itemNode: String?
        get() = getItemTag().getDeep("Arathoth.attrNode")?.asString()

    fun ItemStack.setItemNode(node: String) {
        getItemTag().apply {
            putDeep("Arathoth.attrNode", node)
            saveTo(this@setItemNode)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun ItemStack.readAttribute(entity: Entity): MutableMap<String, AttributeData> {
        val set = AttributeSet.registry[itemNode] ?: return mutableMapOf()
        // 条件不通过, 不读取该物品属性
        if (
            entity is Player
            && !set.rules.all { Rule.judge(it, entity, entity.inventory.indexOf(this), this) }
        ) return mutableMapOf()
        val map = set.data.toMutableMap()
        AttributeKey.registry.forEach {
            it.extraParsers.forEach { parser ->
                val data = (parser as ExtraAttributeParser<AttributeData>).parse(it as AttributeKey<AttributeData>, this)
                map[it.node] = map[it.node]?.append(data) ?: data
            }
        }
        val event = ArathothEvents.ReadItem(entity, this, map)
        event.call()
        return event.data
    }

    fun List<ItemStack>.readAllAttribute(entity: Entity): MutableMap<String, AttributeData> {
        val map = mutableMapOf<String, AttributeData>()
        forEach { item ->
            item.readAttribute(entity).forEach {
                map[it.key] = map[it.key]?.append(it.value) ?: it.value
            }
        }
        return map
    }
}