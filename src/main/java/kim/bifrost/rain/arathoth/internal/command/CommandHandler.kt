package kim.bifrost.rain.arathoth.internal.command

import kim.bifrost.rain.arathoth.Arathoth
import kim.bifrost.rain.arathoth.utils.info
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

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
}