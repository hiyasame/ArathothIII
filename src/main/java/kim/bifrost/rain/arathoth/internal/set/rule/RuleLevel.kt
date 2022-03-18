package kim.bifrost.rain.arathoth.internal.set.rule

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.NumberConversions

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.RuleLevel
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:22
 **/
@RuleImpl(key = "level")
object RuleLevel : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        return when {
            content.startsWith(">=") -> player.level >= NumberConversions.toInt(content.substring(2))
            content.startsWith("<=") -> player.level <= NumberConversions.toInt(content.substring(2))
            content.startsWith(">") -> player.level > NumberConversions.toInt(content.substring(1))
            content.startsWith("<") -> player.level < NumberConversions.toInt(content.substring(1))
            content.startsWith("=") -> player.level == NumberConversions.toInt(content.substring(1))
            else -> true
        }
    }
}