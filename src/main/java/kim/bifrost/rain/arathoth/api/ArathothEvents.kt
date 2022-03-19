package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.api.data.AttributeData
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * kim.bifrost.rain.arathoth.api.ArathothEvents
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 17:02
 **/
class ArathothEvents {
    /**
     * 读取阶段触发该事件
     *
     * @property entity 读取属性的实体 注意 不一定为要加载属性上去的实体
     * @property data data
     * @constructor Create empty Read
     */
    class Read(val entity: Entity, val data: MutableMap<String, AttributeData>): BukkitProxyEvent()

    /**
     * 读取指定物品属性触发该事件
     *
     * @property entity
     * @property item
     * @property data
     * @constructor Create empty Read item
     */
    class ReadItem(val entity: Entity, val item: ItemStack, var data: MutableMap<String, AttributeData>): BukkitProxyEvent()

    /**
     * 加载阶段触发该事件
     *
     * @property entity
     * @property data
     * @constructor Create empty Load
     */
    class Load(val entity: Entity, val data: MutableMap<String, AttributeData>): BukkitProxyEvent()
}