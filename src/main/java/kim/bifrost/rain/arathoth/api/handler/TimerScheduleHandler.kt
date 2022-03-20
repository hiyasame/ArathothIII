package kim.bifrost.rain.arathoth.api.handler

import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.data.AttributeData
import org.bukkit.event.Event
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor

/**
 * kim.bifrost.rain.arathoth.api.handler.TimerScheduleHandler
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/18 18:59
 **/
class TimerScheduleHandler(
    private val async: Boolean = false,
    private val period: Long,
    private val subscriber: () -> Unit
) : AttributeHandler {

    private lateinit var task: PlatformExecutor.PlatformTask

    override fun register() {
        task = submit(async = async, period = period) {
            subscriber()
        }
    }

    override fun unregister() {
        task.cancel()
    }
}

fun <D: AttributeData> AttributeKey.Builder<D>.timer(async: Boolean = false, period: Long, subscriber: () -> Unit) = addHandler(TimerScheduleHandler(async, period, subscriber))