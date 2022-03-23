# ArathothIII

开源免费的Minecraft物品属性插件开发框架

## TODO

- 基础属性包

## API

### 快速创建一个属性

使用Kotlin优雅的DSL语法快速创建一个属性

~~~kotlin
@Register
val damage = createAttribute("arathoth", "damage") {
    event(EntityDamageByEntityEvent::class.java) {
        val status = damager.status(this@createAttribute) ?: return@event
        damage += status.generateValue()
    }
    config {
        set("patterns", listOf("[VALUE] damage"))
    }
}
~~~

### 自定义你的属性值类型

Arathoth支持储存任意类型的属性值，而不仅仅是数值

如果你希望储存给你的某个属性储存自定义类型的属性值，你需要创建一个`自定义值类型`

~~~kotlin
// 这是一个属性没有指定值类型时的默认值类型 - 数值类型 供参考
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

// 数值属性Data
data class NumberAttributeData(
    val range: List<Double>,
    val percent: Double
) : AttributeData {
    override fun append(data: AttributeData): AttributeData {
        data as NumberAttributeData
        return NumberAttributeData(
            range.mapIndexed { index, d -> d + data.range[index] },
            percent + data.percent
        )
    }

    fun generateValue(): Double {
        return random(range[0], range[1]) * (1 + percent / 100)
    }
}
~~~

### 自定义属性读取方式

Arathoth的主体属性值是储存在位于配置文件的`属性集`中的，这也是Arathoth于同类属性插件的优势之一，任何物品的属性都在服务器管理员的掌控之中。

但Arathoth仍然为开发者提供了定义属性读取方式的接口，并默认提供了从Lore/NBT中读取属性值的实现

~~~kotlin
// Lore属性解析器
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
        val lore = item.lore
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
}

// NBT属性解析器
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
}
~~~

### 自定义约束条件

每一个属性集都可以设置对应的约束集，一旦没有通过约束集，该属性集中的属性将不会被加载

Arathoth已经默认实现了以下使用到的所有约束集

~~~yaml
exampleItem:
# 游戏中手持物品/arathoth addattrtag <文件名>.<节点路径> (example.exampleItem)
# 条件列表，不满足其中任何一条则该属性节点无法生效
# 可用约束条件
# bind - 绑定是否视为加载该属性的条件之一，若视为，非该物品绑定的玩家将不会读取该物品的属性
# type - 该物品第一行是否含有这段字符串(去掉颜色之后)
# slot - 物品在玩家背包中的哪个槽位时物品属性生效(槽位id/main)
# perm - 权限节点约束
# level - 等级约束，可用符号 > < >= <= =
# kether - kether脚本条件，自由度极高，任你发挥 (返回值必须为boolean类型)
  rules:
  - type(主武器)
  - bind
  - slot(36, main, off)
  - perm(arathoth.exampleItem)
  - level(>=233)
  - kether(check player food level >= 10)
  # value 写法由对应属性的开发者决定
  # 在名称重复的情况下 在前面加上(namespace)来特指某属性
  attributes:
    (arathoth)damage:
      value: 10
~~~

当然，作为一款自定义度极高的属性插件，约束条件自然也是允许开发者自定义的。我们来看看level这个约束条件的实现。

~~~kotlin
@RuleImpl(key = "level")
object RuleLevel : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        return when {
            content.startsWith(">=") -> player.level >= NumberConversions.toInt(content.substring(2))
            content.startsWith("<=") -> player.level <= NumberConversions.toInt(content.substring(2))
            content.startsWith(">") -> player.level > NumberConversions.toInt(content.substring(1))
            content.startsWith("<") -> player.level < NumberConversions.toInt(content.substring(1))
            content.startsWith("=") -> player.level == NumberConversions.toInt(content.substring(1))
            else -> true
        }
    }
}
~~~