package kim.bifrost.rain.arathoth.internal.set.rule.impl

import kim.bifrost.rain.arathoth.internal.set.rule.Rule
import kim.bifrost.rain.arathoth.internal.set.rule.RuleImpl
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.impl.RuleSlot
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 11:09
 **/
@RuleImpl("slot")
object RuleSlot : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        val params = content.split(",").map { it.trim() }
        val key = if (player.inventory.itemInMainHand == item)
            "main"
        else if (player.inventory.itemInOffHand == item)
            "off"
        else slot.toString()
        return params.contains(key)
    }
}