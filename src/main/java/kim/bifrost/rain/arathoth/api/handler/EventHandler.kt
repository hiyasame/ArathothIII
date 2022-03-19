package kim.bifrost.rain.arathoth.api.handler

import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.data.AttributeData
import kim.bifrost.rain.arathoth.utils.Events
import org.bukkit.event.Event
import org.bukkit.event.EventPriority

/**
 * kim.bifrost.rain.arathoth.api.handler.EventHandler
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 18:58
 **/
class EventHandler<T : Event>(
    private val priority: EventPriority = EventPriority.NORMAL,
    private val ignoredCancelled: Boolean = false,
    private val subscriber: T.() -> Unit,
    private val clazz: Class<T>
    ) : AttributeHandler {

    private lateinit var events: Events<T>

    override fun register() {
        events = Events.subscribe(clazz, subscriber)
    }

    override fun unregister() {
        events.unregister()
    }
}

inline fun <reified T: Event, D: AttributeData<D>> AttributeKey.Builder<D>.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoredCancelled: Boolean = false,
    noinline subscriber: T.() -> Unit
) = addHandler(EventHandler(priority, ignoredCancelled, subscriber, T::class.java))