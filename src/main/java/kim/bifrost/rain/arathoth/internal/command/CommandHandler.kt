package kim.bifrost.rain.arathoth.internal.command

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.api.AttributeKey
import kim.bifrost.rain.arathoth.api.parseAttributeKey
import kim.bifrost.rain.arathoth.internal.ItemStatusManager.itemNodes
import kim.bifrost.rain.arathoth.internal.database.Correction
import kim.bifrost.rain.arathoth.internal.database.Database
import kim.bifrost.rain.arathoth.internal.set.AttributeSet
import kim.bifrost.rain.arathoth.utils.error
import kim.bifrost.rain.arathoth.utils.format
import kim.bifrost.rain.arathoth.utils.info
import kim.bifrost.rain.arathoth.utils.parseTimeStr
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.module.chat.TellrawJson
import taboolib.module.chat.colored
import taboolib.platform.type.BukkitCommandSender
import taboolib.platform.util.isAir
import java.util.*

/**
 * kim.bifrost.rain.arathoth.internal.command.CommandHandler
 * Arathoth
 *
 * @author 寒雨
 * @since 2022/3/21 1:38
 **/
@CommandHeader(name = "arathoth", description = "Arathoth", permission = "arathoth.admin")
object CommandHandler {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            Arathoth.reload()
            sender.info("重载成功")
        }
    }

    @CommandBody
    val attach = subCommand {
        dynamic("attrSets") {
            suggestion<CommandSender> { _, _ ->
                AttributeSet.registry.keys.toList()
            }
            execute<Player> { sender, _, argument ->
                val item = sender.inventory.itemInMainHand
                if (item.isAir()) {
                    sender.error("手中物品不能为空")
                    return@execute
                }
                item.itemNodes = item.itemNodes.also { it.toMutableList().add(argument) }
                sender.info("添加成功")
            }
        }
    }

    @CommandBody
    val info = subCommand {
        execute<Player> { sender, _, _ ->
            val item = sender.inventory.itemInMainHand
            if (item.isAir()) {
                sender.error("手中物品不能为空")
                return@execute
            }
            sender.info("手中物品有 &f${item.itemNodes.size} &7个属性节点:")
            item.itemNodes.forEach {
                sender.info(" &8&l· &f$it")
            }
        }
    }

    @CommandBody
    val clear = subCommand {
        execute<Player> { sender, _, _ ->
            val item = sender.inventory.itemInMainHand
            if (item.isAir()) {
                sender.error("手中物品不能为空")
                return@execute
            }
            item.itemNodes = emptyList()
            sender.info("清空成功")
        }
    }

    @CommandBody
    val listAttr = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.info("属性节点列表:")
            AttributeKey.registry.forEach {
                TellrawJson()
                    .append("&7&l[&f&lArathoth&7&l]   &8- &f${it.name}".colored())
                    .hoverText(
                        """
                        &8&l${it.name}

                        &7Namespace: &f${it.namespace}
                        &7DataType: &f${it.dataType.javaClass.typeName.removePrefix("kim.bifrost.rain.arathoth.api")}
                        &7Parsers: &f${it.extraParsers.map { p -> p.name }}
                        """.trimIndent().colored()
                    ).runCommand(it.node).sendTo(BukkitCommandSender(sender))
            }
        }
    }

    @CommandBody
    val correction = subCommand {
        literal("add") {
            dynamic("player") {
                dynamic("attr") {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        AttributeKey.registry.map { it.name }
                    }
                    dynamic("content") {
                        // 有效时间
                        dynamic("time", optional = true) {
                            execute<CommandSender> { sender, context, argument: String ->
                                val time = System.currentTimeMillis() + parseTimeStr(argument)
                                val attrKey = parseAttributeKey(context.argument(-2)) ?: return@execute let { sender.error("属性节点不存在") }
                                val data = attrKey.dataType.fromString(context.argument(-1)) ?: return@execute let { sender.error("属性节点数据类型不匹配") }
                                val player = context.argument(-3)
                                submit(async = true) {
                                    Database.insert(
                                        Correction(
                                            player = player,
                                            attrNode = attrKey.node,
                                            data = data,
                                            expire = time
                                        )
                                    )
                                    sender.info("添加成功")
                                }
                            }
                        }
                        execute<CommandSender> { sender, context, argument: String ->
                            val attrKey = parseAttributeKey(context.argument(-1)) ?: return@execute let { sender.error("属性节点不存在") }
                            val data = attrKey.dataType.fromString(argument) ?: return@execute let { sender.error("属性节点数据类型不匹配") }
                            val player = context.argument(-2)
                            submit(async = true) {
                                Database.insert(
                                    Correction(
                                        player = player,
                                        attrNode = attrKey.node,
                                        data = data,
                                        expire = Long.MAX_VALUE
                                    )
                                )
                                sender.info("添加成功")
                            }
                        }
                    }
                }
            }
        }
        literal("remove") {
            dynamic("id") {
                restrict<CommandSender> { _, _, argument ->
                    Coerce.asInteger(argument).isPresent
                }
                execute<CommandSender> { sender, _, argument: String ->
                    submit(async = true) {
                        Database.remove(argument.toInt())
                        sender.info("删除成功")
                    }
                }
            }
        }
        literal("list") {
            dynamic("player") {
                execute<CommandSender> { sender, _, argument: String ->
                    submit(async = true) {
                        sender.info("查询结果: &8(悬浮查看详细信息)")
                        Database.query(argument).forEach {
                            TellrawJson().append("&7&l[&f&lArathoth&7&l] &7".colored())
                                .append("&8&l· &8&oID: &7&o${it.id} &8&oNode: &7&o${it.attrNode}".colored())
                                .hoverText("""
                                    &8&lID: &f${it.id}
                                    &8&lNode: &f${it.attrNode}
                                    &8&l玩家: &f${it.player}
                                    &8&lJSON数据: &f${it.data}
                                    &8&l过期时间: &f${Date(it.expire).format()}
                                """.trimIndent().colored())
                                .sendTo(BukkitCommandSender(sender))
                        }
                    }
                }
            }
        }
    }
}