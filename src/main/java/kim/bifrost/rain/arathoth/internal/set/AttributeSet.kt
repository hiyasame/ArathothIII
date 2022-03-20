package kim.bifrost.rain.arathoth.internal.set

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.utils.new
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.io.File

/**
 * kim.bifrost.rain.arathoth.internal.set.AttributeSet
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:13
 **/
class AttributeSet(
    val data: Map<String, AttributeData>,
    val rules: List<String>
) {
    companion object {
        // path - set
        val registry = mutableMapOf<String, AttributeSet>()
        private val setDir = Arathoth.plugin.dataFolder.new("set")

        @Awake(LifeCycle.ENABLE)
        fun load() {
            registry.clear()
            setDir.mkdirs()
            loadDir(setDir)
        }

        private fun loadDir(dir: File) {
            dir.listFiles()?.forEach {
                if (it.isDirectory) {
                    loadDir(it)
                } else {
                    val fileNode = it.node()
                    if (it.extension != "yml") {
                        return@forEach
                    }
                    val conf = YamlConfiguration.loadConfiguration(it)
                    conf.getKeys(false).forEach { s ->
                        val node = "$fileNode.$s"
                        val section = conf.getConfigurationSection(s)
                        section?.let {
                            registry[node] = parseSet(section)
                        }
                    }
                }
            }
        }

        private fun parseSet(section: ConfigurationSection): AttributeSet {
            val rules = section.getStringList("rules")
            val data = section.getConfigurationSection("attributes")!!
                .getKeys(false)
                .mapNotNull { s ->
                    var attr = AttributeKey.registry.find { it.name == s }
                    if (s.startsWith("(")) {
                        val namespace = s.substring(s.indexOf("("), s.indexOf(")"))
                        val name = s.substring(s.indexOf(")") + 1)
                        attr = AttributeKey.registry.find { it.namespace == namespace && it.name == name }
                    }
                    if (attr == null) {
                        return@mapNotNull null
                    }
                    return@mapNotNull attr.node to attr.dataType.parse(section.getConfigurationSection("attributes.$s")!!)
                }
                .toMap()
            return AttributeSet(data, rules)
        }

        private fun File.node(): String {
            var path = nameWithoutExtension
            var cursor = this
            while (cursor.parentFile != setDir) {
                cursor = cursor.parentFile
                path = cursor.name + "." + path
            }
            return path
        }
    }
}