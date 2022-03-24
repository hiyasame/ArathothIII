package kim.bifrost.rain.arathoth.internal.set.rule.impl

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.internal.set.rule.Rule
import kim.bifrost.rain.arathoth.internal.set.rule.RuleImpl
import kim.bifrost.rain.arathoth.utils.asRegexPattern
import kim.bifrost.rain.arathoth.utils.lore
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.impl.RuleBind
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 11:24
 **/
@RuleImpl("bind")
object RuleBind : Rule {

    private val bindPattern by lazy {
        Arathoth.conf.getString("settings.lore.bind.pattern", "Owner: [VALUE]")!!.asRegexPattern()
    }

    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        if (!Arathoth.enableLore) return true
        var owner: String? = null
        for (lore in item.lore) {
            val m = bindPattern.matcher(lore)
            if (m.find()) {
                owner = m.group("value")
                break
            }
        }
        return owner == null || owner == player.name
    }
}