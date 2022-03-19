package kim.bifrost.rain.arathoth.internal.set.rule.impl

import kim.bifrost.rain.arathoth.internal.set.rule.Rule
import kim.bifrost.rain.arathoth.internal.set.rule.RuleImpl
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.impl.RulePerm
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 11:07
 **/
@RuleImpl("perm")
object RulePerm : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        return player.hasPermission(content)
    }
}