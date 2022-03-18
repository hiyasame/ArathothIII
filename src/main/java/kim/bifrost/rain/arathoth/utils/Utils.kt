package kim.bifrost.rain.arathoth.utils

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import java.io.File
import java.util.function.Consumer

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

inline fun <reified T: Event> subscribe(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline subscriber: T.() -> Unit
): Events<T> {
    return Events.subscribe(T::class.java, Consumer { it.subscriber() })
}

fun info(message: String) {
    console().sendMessage("&7&l[&f&lArathoth&7&l] &7$message".colored())
}

fun error(message: String) {
    console().sendMessage("&c&l[&4&lArathoth&c&l] &c$message".colored())
}

fun CommandSender.info(message: String) {
    sendMessage("&7&l[&f&lArathoth&7&l] &7$message".colored())
}

fun CommandSender.error(message: String) {
    sendMessage("&c&l[&4&lArathoth&c&l] &c$message".colored())
}