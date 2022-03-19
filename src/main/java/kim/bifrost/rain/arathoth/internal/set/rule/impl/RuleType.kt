package kim.bifrost.rain.arathoth.internal.set.rule.impl

import kim.bifrost.rain.arathoth.internal.set.rule.Rule
import kim.bifrost.rain.arathoth.internal.set.rule.RuleImpl
import kim.bifrost.rain.arathoth.utils.lore
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.impl.RuleType
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 11:15
 **/
@RuleImpl("type")
object RuleType : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        return item.lore.firstOrNull()?.contains(content) ?: false
    }
}