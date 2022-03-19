package kim.bifrost.rain.arathoth.example

import kim.bifrost.rain.arathoth.api.Register
import kim.bifrost.rain.arathoth.api.createAttribute
import kim.bifrost.rain.arathoth.api.handler.event
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * kim.bifrost.rain.arathoth.example.ExampleAttr
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 21:55
 **/
@Register
val damage = createAttribute("arathoth", "damage") {
    event(EntityDamageByEntityEvent::class.java) {
        val status = damager.status(this@createAttribute) ?: return@event
        damage += status.generateValue()
    }
    config {
        set("patterns", listOf("[VALUE] damage"))
    }
}