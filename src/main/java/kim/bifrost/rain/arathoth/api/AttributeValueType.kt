package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import kim.bifrost.rain.arathoth.utils.takeContent
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

    /**
     * PlaceHolderAPI 请求属性值
     *
     * @param data 可以直接cast成data的具体类型
     * @param args 属性名`:`后面的字符串
     * @return
     */
    fun onPAPIRequest(data: AttributeData, args: String): String

    /**
     * 将字符串转化为data
     * 主要用处在补查值命令中
     * eg. NumberAttributeData
     * [0.0,0.0,0.0] -> NumberAttributeData([0.0, 0.0], 0.0)
     * 失败返回null
     *
     * @param string
     * @return
     */
    fun fromString(string: String): T?
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

    override fun onPAPIRequest(data: AttributeData, args: String): String {
        data as NumberAttributeData
        return when (args) {
            "min" -> return data.range[0].toString()
            "max" -> return data.range[1].toString()
            "range" -> return data.range.joinToString("~")
            "percent" -> return data.percent.toString()
            else -> "WRONG PARAMS"
        }
    }

    override fun fromString(string: String): NumberAttributeData? {
        val content = string.takeContent("[", "]") ?: return null
        val array = content.split(",")
            .map { NumberConversions.toDouble(it) }
        if (array.size < 3) return null
        return NumberAttributeData(array.subList(0, 2).sorted(), array[2])
    }
}