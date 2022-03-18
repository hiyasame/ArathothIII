package kim.bifrost.rain.arathoth.api

import kim.bifrost.rain.arathoth.Arathoth
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
class AttributeKey<T>(
    val namespace: String,
    val name: String,
    val dataType: AttributeValueType<T>,
    private val lorePattern: List<String> = listOf(),
    private val initConf: FileConfiguration.() -> Unit,
    private val handlers: List<AttributeHandler>
) {

    // 是否注册
    private var registered: Boolean = false

    // 启用lore
    val enableLore: Boolean
        get() = lorePattern.isNotEmpty()

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

    class Builder<T>(val namespace: String, val name: String, private val dataType: AttributeValueType<T>) {
        private var lorePattern: List<String> = listOf()
        private var initConf: FileConfiguration.() -> Unit = {}
        private val handlers = mutableListOf<AttributeHandler>()

        fun lorePattern(lorePattern: List<String>): Builder<T> {
            this.lorePattern = lorePattern
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
            return AttributeKey(namespace, name, dataType, lorePattern, initConf, handlers)
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

fun <T> createAttribute(namespace: String, name: String, dataType: AttributeValueType<T>, init: AttributeKey.Builder<T>.() -> Unit): AttributeKey<T> {
    val builder = AttributeKey.Builder(namespace, name, dataType)
    builder.init()
    return builder.build()
}

fun createAttribute(namespace: String, name: String, init: AttributeKey.Builder<NumberAttributeData>.() -> Unit): AttributeKey<NumberAttributeData> {
    return createAttribute(namespace, name, NUMBER, init)
}