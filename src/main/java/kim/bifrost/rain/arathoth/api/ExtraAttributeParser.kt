package kim.bifrost.rain.arathoth.api

import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.api.ExtraAttributeParser
 * Arathoth
 * 额外属性解析器
 * Lore/NBT 的额外属性均可基于此实现
 *
 * @author 寒雨
 * @since 2022/3/19 0:59
 **/
interface ExtraAttributeParser {
    fun parse(key: AttributeKey<*>, item: ItemStack): Any
}