package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import kim.bifrost.rain.arathoth.utils.lore
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag

/**
 * kim.bifrost.rain.arathoth.api.ExtraAttributeParser
 * Arathoth
 * 额外属性解析器
 * Lore/NBT 的额外属性均可基于此实现
 *
 * @author 寒雨
 * @since 2022/3/19 0:59
 **/
interface ExtraAttributeParser<T: AttributeData> {

    val name: String

    fun parse(key: AttributeKey<T>, item: ItemStack): T
}

val PARSER_LORE = object : ExtraAttributeParser<NumberAttributeData> {

    private val regexMap = mutableMapOf<String, List<Regex>>()
    private val percentRegexMap = mutableMapOf<String, List<Regex>>()
    private val regex = "(?<min>[-+]?\\d+(?:\\.\\d+)?)(?:-?)((?<max>[-+]?\\d+(?:\\.\\d+)?)?)"
    private val percentRegex = "(?<percent>[-+]?\\d+(?:\\.\\d+)?)%"

    override fun parse(key: AttributeKey<NumberAttributeData>, item: ItemStack): NumberAttributeData {
        if (!Arathoth.enableLore) return NumberAttributeData(listOf(0.0, 0.0), 0.0)
        val conf = key.config
        if (!regexMap.containsKey(key.node) || !percentRegexMap.containsKey(key.node)) {
            val regex = conf.getStringList("lore").map { it.replace("[VALUE]", regex).toRegex() }
            val percentRegex = conf.getStringList("lore").map { it.replace("[VALUE]", percentRegex).toRegex() }
            regexMap[key.node] = regex
            percentRegexMap[key.node] = percentRegex
        }
        val regex = regexMap[key.node]!!
        val percentRegex = percentRegexMap[key.node]!!
        val lore = item.lore.map { ChatColor.stripColor(it)!! }
        var min = 0.0
        var max = 0.0
        var percent = 0.0
        for (s in lore) {
            regex.forEach {
                it.findAll(s).forEach { r ->
                    val groups = r.groups as MatchNamedGroupCollection
                    val min1 = groups["min"]?.value?.toDouble() ?: 0.0
                    val max1 = groups["max"]?.value?.toDouble() ?: min1
                    min += min1
                    max += max1
                }
            }
            percentRegex.forEach {
                it.findAll(s).forEach { r ->
                    val groups = r.groups as MatchNamedGroupCollection
                    val pct = groups["percent"]?.value?.toDouble() ?: 0.0
                    percent += pct
                }
            }
        }
        return NumberAttributeData(listOf(min, max).sorted(), percent)
    }

    override val name: String
        get() = "LORE_PARSER"
}

val PARSER_NBT = object : ExtraAttributeParser<NumberAttributeData> {
    override fun parse(key: AttributeKey<NumberAttributeData>, item: ItemStack): NumberAttributeData {
        val itemTag = item.getItemTag().getDeep("Arathoth.NBTExtra.$key")?.asCompound()
        if (itemTag != null) {
            val min = itemTag["min"]?.asDouble() ?: 0.0
            val max = itemTag["max"]?.asDouble() ?: min
            val percent = itemTag["percent"]?.asDouble() ?: 0.0
            return NumberAttributeData(listOf(min, max).sorted(), percent)
        }
        return NumberAttributeData(listOf(0.0, 0.0), 0.0)
    }

    override val name: String
        get() = "NBT_PARSER"
}