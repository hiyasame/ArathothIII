package kim.bifrost.rain.arathoth

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.platform.BukkitPlugin
import taboolib.platform.type.BukkitPlayer

object Arathoth : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    override fun onEnable() {
        info("Arathoth is enabled!")
    }
}