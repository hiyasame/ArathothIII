package kim.bifrost.rain.arathoth.internal.listener

import kim.bifrost.rain.arathoth.api.ArathothEvents
import kim.bifrost.rain.arathoth.internal.database.Database
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent

/**
 * kim.bifrost.rain.arathoth.internal.listener.ListenerCorrection
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 21:14
 **/
object ListenerCorrection {
    @SubscribeEvent
    fun e(e: ArathothEvents.Load) {
        if (e.entity is Player) {
            val correction = Database.query(e.entity.name)
            e.data.putAll(correction.filter { !it.expired() }.associate { it.attrNode to it.data })
        }
    }
}