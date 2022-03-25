package kim.bifrost.rain.arathoth.utils

import kim.bifrost.rain.arathoth.api.data.AttributeData
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.platform.util.isNotAir
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.regex.Pattern

/**
 * kim.bifrost.rain.arathoth.utils.Utils
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 18:41
 **/
fun File.new(name: String): File {
    return File(this, name)
}

inline fun <reified T : Event> subscribe(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline subscriber: T.() -> Unit
): Events<T> {
    return Events.subscribe(T::class.java, Consumer { it.subscriber() })
}

fun info(message: String) {
    console().sendMessage("&7&l[&f&lArathoth&7&l] &7$message".colored())
}

fun warn(message: String) {
    console().sendMessage("&c&l[&4&lArathoth&c&l] &c$message".colored())
}

fun CommandSender.info(message: String) {
    sendMessage("&7&l[&f&lArathoth&7&l] &7$message".colored())
}

fun CommandSender.error(message: String) {
    sendMessage("&c&l[&4&lArathoth&c&l] &c$message".colored())
}

fun String.takeContent(start: String, end: String): String? {
    if (indexOf(start) == -1 || indexOf(end) == -1) return null
    return substring(indexOf(start), indexOf(end) + 1)
}

fun String.asRegexPattern(): Pattern {
    return Pattern.compile(
        this.replace("[VALUE]", "(?<value>\\S+)")
            .replace("[NUMBER]", "(?<number>\\d+)")
    )
}

fun LivingEntity.getEntityItems(): List<ItemStack> {
    val equip = equipment ?: return emptyList()
    return buildList {
        addAll(equip.armorContents.filter { it.isNotAir() })
        if (equip.itemInMainHand.isNotAir()) add(equip.itemInMainHand)
        if (equip.itemInOffHand.isNotAir()) add(equip.itemInOffHand)
    }
}

fun <T> buildList(block: MutableList<T>.() -> Unit): List<T> {
    return mutableListOf<T>().apply(block)
}

private val timeRegex = Regex("(?<number>\\d+)(?<key>\\D+)")
private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun parseTimeStr(str: String): Long {
    var time = 0L
    timeRegex.findAll(str).forEach {
        val groups = it.groups as MatchNamedGroupCollection
        time += TimeUnit.SECONDS.toMillis(
            when (groups["key"]!!.value) {
                "s" -> groups["number"]!!.value.toLong()
                "m" -> groups["number"]!!.value.toLong() * 60
                "h" -> groups["number"]!!.value.toLong() * 60 * 60
                "d" -> groups["number"]!!.value.toLong() * 60 * 60 * 24
                "mo" -> groups["number"]!!.value.toLong() * 60 * 60 * 24 * 30
                "y" -> groups["number"]!!.value.toLong() * 60 * 60 * 24 * 30 * 12
                else -> 0
            }
        )
    }
    return time
}

fun MutableMap<String, AttributeData>.combine(data: Map<String, AttributeData>) {
    data.forEach {
        this[it.key] = it.value + this[it.key]
    }
}

fun Date.format(): String {
    return format.format(this)
}

fun String.stripColor(): String {
    return ChatColor.stripColor(this).orEmpty()
}

private fun loadClassByPath(r: String?, path: String, list: MutableList<Class<*>>, loader: ClassLoader) {
    val f = File(path)
    val root = r ?: f.path
    if (f.isFile
        && f.name.matches("^.*\\\\.class\$".toRegex())
        && f.path.contains("base")) {
        runCatching {
            val classPath = f.path
            val className = classPath.substring(root.length + 1, classPath.length - 6).replace(File.pathSeparator, ".")
            list.add(Class.forName(className, false, loader))
        }
    } else {
        val fs = f.listFiles() ?: return
        fs.forEach {
            loadClassByPath(root, it.path, list, loader)
        }
    }
}

val ItemStack.lore: List<String>
    get() {
        val lore = mutableListOf<String>()
        val meta = itemMeta
        if (meta != null) {
            if (meta.hasLore()) {
                lore.addAll(meta.lore!!)
            }
        }
        return lore
    }