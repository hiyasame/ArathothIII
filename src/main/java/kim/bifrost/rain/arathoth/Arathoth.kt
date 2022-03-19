package kim.bifrost.rain.arathoth

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import taboolib.platform.type.BukkitPlayer

object Arathoth : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    @Config
    lateinit var conf: Configuration
        private set

    val enableLore: Boolean
        get() = conf.getBoolean("settings.lore.enable")

    override fun onEnable() {
        info("Arathoth is enabled!")
    }
}