package kim.bifrost.rain.arathoth.internal.hook

import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.internal.EntityStatusManager.status
import org.bukkit.entity.Player
import taboolib.common.platform.Awake
import taboolib.platform.compat.PlaceholderExpansion

/**
 * kim.bifrost.rain.arathoth.internal.hook.PlaceHolderHook
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 17:42
 **/
@Awake
object PlaceHolderHook : PlaceholderExpansion {

    override val identifier: String = "arathoth"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        player ?: return "PLAYER NOT FOUND"
        val (s, params) = args.split(":")
        var key = AttributeKey.registry.find { it.name == s }
        if (s.startsWith("(")) {
            val namespace = s.substring(s.indexOf("("), s.indexOf(")"))
            val name = s.substring(s.indexOf(")") + 1)
            key = AttributeKey.registry.find { it.namespace == namespace && it.name == name }
        }
        if (key == null) return "ATTRIBUTE NOT FOUND"
        return key.dataType.onPAPIRequest(player.status(key) ?: return "none", params)
    }
}