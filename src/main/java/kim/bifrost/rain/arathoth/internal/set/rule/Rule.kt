package kim.bifrost.rain.arathoth.internal.set.rule

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * kim.bifrost.rain.arathoth.internal.set.rule.Rule
 * Arathoth
 * 约束条件
 * 约束条件只对玩家起约束作用，其他实体属性不受影响
 *
 * @author 寒雨
 * @since 2022/3/19 1:16
 **/
interface Rule {

    /**
     * 判断玩家是否满足约束条件
     *
     * @param player 玩家
     * @param slot 当前物品所在槽位
     * @param item 物品
     * @param content 约束值
     * @return true则满足约束，false则不会加载属性
     */
    fun judge(player: Player, slot: Int, item: ItemStack, content: String): Boolean

    companion object {
        val registry: MutableMap<String, Rule> = mutableMapOf()
        private val regex = Regex("""(?<rule>\S+)\((?<content>(\s|\S)+)\)""")

        fun judge(str: String, player: Player, slot: Int, item: ItemStack): Boolean {
            val res = regex.find(str) ?: return true
            val group = res.groups as MatchNamedGroupCollection
            val rule = group["rule"]?.value ?: return true
            val content = group["content"]?.value ?: ""
            return registry[rule]?.run {
                judge(player, slot, item, content)
            } ?: true
        }
    }
}