package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import kim.bifrost.rain.arathoth.api.handler.AttributeHandler
import kim.bifrost.rain.arathoth.utils.info
import kim.bifrost.rain.arathoth.utils.new
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.Schedule
import java.io.File

/**
 * kim.bifrost.rain.arathoth.api.AttributeKey
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 17:04
 **/
class AttributeKey<T: AttributeData>(
    val namespace: String,
    val name: String,
    val dataType: AttributeValueType<T>,
    val extraParsers: List<ExtraAttributeParser<T>> = listOf(),
    private val initConf: FileConfiguration.() -> Unit,
    private val handlers: List<AttributeHandler>
) {

    // 是否启用
    val enable: Boolean
        get() = config.getBoolean("enable", true)

    // 是否注册
    private var registered: Boolean = false

    val node: String = "$namespace:$name"

    // 属性配置
    lateinit var config: FileConfiguration
        private set

    // 加载配置文件
    private fun loadConf() {
        val dir = Arathoth.plugin.dataFolder.new("attr${File.separator}$namespace")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = dir.new("$name.yml")
        var conf: FileConfiguration? = null
        if (!file.exists()) {
            file.createNewFile()
            conf = YamlConfiguration.loadConfiguration(file)
            conf.initConf()
            conf.save(file)
        }
        if (conf == null) {
            conf = YamlConfiguration.loadConfiguration(file)
        }
        config = conf
    }

    /**
     * 注册属性
     */
    fun register() {
        if (registered) return
        loadConf()
        if (!enable) {
            return
        }
        handlers.forEach { it.register() }
        registered = true
        registry.add(this)
    }

    class Builder<T: AttributeData>(val namespace: String, val name: String, private val dataType: AttributeValueType<T>) {
        private val extraParsers: MutableList<ExtraAttributeParser<T>> = mutableListOf()
        private var initConf: FileConfiguration.() -> Unit = {
            set("enable", true)
        }
        private val handlers = mutableListOf<AttributeHandler>()

        /**
         * 在创建属性的dsl中获取属性配置的手段
         * 不是在回调中调用会抛出异常
         */
        val config: FileConfiguration
            get() = registry.first { it.node == "$namespace.$name" }.config

        fun addExtraAttributeParser(parser: ExtraAttributeParser<T>): Builder<T> {
            extraParsers.add(parser)
            return this
        }

        fun config(initConf: FileConfiguration.() -> Unit): Builder<T> {
            this.initConf = {
                initConf()
            }
            return this
        }

        fun addHandler(handler: AttributeHandler): Builder<T> {
            handlers.add(handler)
            return this
        }

        fun build(): AttributeKey<T> {
            return AttributeKey(namespace, name, dataType, extraParsers, initConf, handlers)
        }
    }

    companion object {
        val registry = mutableSetOf<AttributeKey<*>>()

        fun reloadAll() {
            registry.forEach { it.loadConf() }
        }

        @Schedule(delay = 20)
        fun countRegistered()  {
            info("成功注册属性: &f${registry.size} &7个")
        }

        fun findByNode(node: String): AttributeKey<*>? {
            return registry.firstOrNull { it.node == node }
        }
    }
}

/**
 * 创建自定义数据类型的属性
 *
 * @param T
 * @param namespace
 * @param name
 * @param dataType
 * @param init
 * @receiver
 * @return
 */
fun <T: AttributeData> createAttribute(namespace: String, name: String, dataType: AttributeValueType<T>, init: AttributeKey.Builder<T>.() -> Unit): AttributeKey<T> {
    val builder = AttributeKey.Builder(namespace, name, dataType)
    builder.init()
    return builder.build()
}

/**
 * 创建一个数据类型为数值类型的属性
 * 会默认加上NBT/Lore的ExtraAttributeParser
 *
 * @param namespace
 * @param name
 * @param init
 * @receiver
 * @return
 */
fun createAttribute(namespace: String, name: String, init: AttributeKey.Builder<NumberAttributeData>.() -> Unit): AttributeKey<NumberAttributeData> {
    val builder = AttributeKey.Builder(namespace, name, NUMBER)
    builder.init()
    builder.addExtraAttributeParser(PARSER_NBT)
    builder.addExtraAttributeParser(PARSER_LORE)
    return builder.build()
}

/**
 * 通过字符串获取AttributeKey
 *
 * @param s
 * @return
 */
fun parseAttributeKey(s: String): AttributeKey<*>? {
    var key = AttributeKey.registry.find { it.name == s }
    if (s.startsWith("(")) {
        val namespace = s.substring(s.indexOf("("), s.indexOf(")"))
        val name = s.substring(s.indexOf(")") + 1)
        key = AttributeKey.registry.find { it.namespace == namespace && it.name == name }
    }
    return key
}