package kim.bifrost.rain.arathoth.internal.set.rule

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.kether.KetherShell
import taboolib.platform.type.BukkitPlayer

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.RuleKether
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/19 1:39
 **/
@RuleImpl("kether")
object RuleKether : Rule {
    override fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean {
        return KetherShell.eval(content) {
            this.sender = BukkitPlayer(player)
        }.get() as? Boolean ?: true
    }
}