package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import kim.bifrost.rain.arathoth.api.handler.AttributeHandler
import kim.bifrost.rain.arathoth.utils.info
import kim.bifrost.rain.arathoth.utils.new
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.io.File

/**
 * kim.bifrost.rain.arathoth.api.AttributeKey
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 17:04
 **/
class AttributeKey<T : AttributeData<T>>(
    val namespace: String,
    val name: String,
    val dataType: AttributeValueType<T>,
    private val extraParsers: List<ExtraAttributeParser<T>> = listOf(),
    private val initConf: FileConfiguration.() -> Unit,
    private val handlers: List<AttributeHandler>
) {

    // 是否注册
    private var registered: Boolean = false

    val node: String = "$namespace.$name"

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
        registered = true
        loadConf()
        registry.add(this)
    }

    class Builder<T: AttributeData<T>>(val namespace: String, val name: String, private val dataType: AttributeValueType<T>) {
        private val extraParsers: MutableList<ExtraAttributeParser<T>> = mutableListOf()
        private var initConf: FileConfiguration.() -> Unit = {}
        private val handlers = mutableListOf<AttributeHandler>()

        fun addExtraAttributeParser(parser: ExtraAttributeParser<T>): Builder<T> {
            extraParsers.add(parser)
            return this
        }

        fun config(initConf: FileConfiguration.() -> Unit): Builder<T> {
            this.initConf = initConf
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
        private val registry = mutableSetOf<AttributeKey<*>>()

        fun reloadAll() {
            registry.forEach { it.loadConf() }
        }

        @Awake(LifeCycle.ACTIVE)
        fun countRegistered()  {
            info("成功注册属性: &f${registry.size} &7个")
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
fun <T: AttributeData<T>> createAttribute(namespace: String, name: String, dataType: AttributeValueType<T>, init: AttributeKey.Builder<T>.() -> Unit): AttributeKey<T> {
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