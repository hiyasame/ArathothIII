package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions

/**
 * kim.bifrost.rain.arathoth.api.AttributeDataType
 * Arathoth
 * 属性值类型, 可以自己实现自己想要储存的值类型
 *
 * @author 寒雨
 * @since 2022/3/18 17:17
 **/
interface AttributeValueType<T : AttributeData> {
    /**
     * 解析属性Data
     *
     * @param section 属性对应属性节点
     * @return
     */
    fun parse(section: ConfigurationSection): T

}

// 从字符串中判断值类型
// 并返回一个含有最大/最小值/百分比值的属性Data
val NUMBER = object : AttributeValueType<NumberAttributeData> {

    override fun parse(section: ConfigurationSection): NumberAttributeData {
        val content = section.getString("value")!!
        // 百分比不支持范围
        if (content.endsWith("%")) {
            return NumberAttributeData(
                listOf(0.0, 0.0),
                NumberConversions.toDouble(content.removeSuffix("%"))
            )
        }
        if (content.contains("~")) {
            val array = content.split("~")
                .map { NumberConversions.toDouble(it) }
                .sorted()
            return NumberAttributeData(array, 0.0)
        }
        val value = NumberConversions.toDouble(content)
        return NumberAttributeData(
            listOf(value, value),
            0.0
        )
    }
}