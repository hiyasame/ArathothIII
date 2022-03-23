package kim.bifrost.rain.arathoth.api.script

import jdk.dynalink.beans.StaticClass
import jdk.nashorn.api.scripting.ScriptObjectMirror
import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.createAttribute
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.api.data.NumberAttributeData
import kim.bifrost.rain.arathoth.api.handler.timer
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import kim.bifrost.rain.arathoth.utils.Events
import kim.bifrost.rain.arathoth.utils.info
import kim.bifrost.rain.arathoth.utils.new
import kim.bifrost.rain.arathoth.utils.warn
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.module.configuration.util.getMap
import java.io.File
import java.util.function.Consumer
import javax.script.Invocable
import javax.script.ScriptEngineManager
import kotlin.reflect.full.companionObjectInstance

/**
 * kim.bifrost.rain.arathoth.api.script.JSAttrManager
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/22 20:43
 **/
class JSAttrManager(private val file: File) {
    private val manager = ScriptEngineManager()
    lateinit var key: AttributeKey<NumberAttributeData>
        private set

    val config: FileConfiguration
        get() = key.config

    @Suppress("UNCHECKED_CAST")
    fun getStatus(entity: Entity, attrNode: String): AttributeData? {
        val key = AttributeKey.findByNode(attrNode) as AttributeKey<AttributeData>? ?: return null
        return entity.status(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun register() {
        // 异步注册
        submit(async = true) {
            info("尝试注册脚本属性: &f${file.name}")
            val engine = manager.getEngineByName("js")
            jsBuilding.forEach { (k, v) ->
                engine.put(k, v)
            }
            engine.put("manager", this@JSAttrManager)
            engine.eval(file.readText())
            key = createAttribute("js", file.nameWithoutExtension) {
                val handlers = engine.get("handlers") as ScriptObjectMirror
                handlers.forEach { (k, v) ->
                    val (type, params) = k.split(":")
                    when (type.lowercase()) {
                        "event" -> {
                            kotlin.runCatching {
                                val clazz = Class.forName(params)
                                if (Event::class.java.isAssignableFrom(clazz)) {
                                    Events.subscribe(clazz as Class<Event>, Consumer {
                                        val invocable = engine as? Invocable
                                        invocable?.invokeFunction(v as String, it)
                                    })
                                }
                            }.onFailure {
                                warn("脚本属性 &f${file.name} &c事件处理器解析异常: &4${it.message}")
                            }
                        }
                        "timer" -> {
                            kotlin.runCatching {
                                val (t, a) = params.split(",")
                                val time = t.toLong()
                                timer(period = time, async = a.toBoolean()) {
                                    val invocable = engine as? Invocable
                                    invocable?.invokeFunction(v as String)
                                }
                            }.onFailure {
                                warn("脚本属性 &f${file.name} &c定时处理器解析异常: &4${it.message}")
                            }
                        }
                    }
                }
                config {
                    kotlin.runCatching {
                        val invocable = engine as? Invocable
                        invocable?.invokeFunction("config", this)
                    }.onFailure {
                        warn("脚本属性 &f${file.name} &c定时处理器解析异常: &4${it.message}")
                    }
                }
            }
            info("注册脚本属性: &f${file.name}")
            key.register()
        }
    }

    companion object {
        private val jsBuilding by lazy {
            Arathoth.conf.getMap<String, String>("settings.script.js-building")
                .map { (key, value) -> key to parseClazzOrSingleInstance(value) }
                .filter { it.second != null }
                .toMap()
        }

        private fun parseClazzOrSingleInstance(name: String): Any? {
            val clazz = kotlin.runCatching { Class.forName(name) }.getOrNull()?.kotlin ?: return null
            // 是单例则返回单例
            if (clazz.objectInstance != null) {
                return clazz.objectInstance
            }
            // 有伴生对象则返回伴生对象
            if (clazz.companionObjectInstance != null) {
                return clazz.companionObjectInstance
            }
            // 都没有 返回StaticClass
            return StaticClass.forClass(clazz.java)
        }

        // 加载脚本属性
        @Awake(LifeCycle.ENABLE)
        internal fun loadScripts() {
            val dir = getDataFolder().new("js")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            dir.listFiles()?.forEach {
                JSAttrManager(it).register()
            }
        }
    }
}