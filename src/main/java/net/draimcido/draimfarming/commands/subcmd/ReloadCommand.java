package net.draimcido.draimfarming.commands.subcmd;

import net.draimcido.draimfarming.commands.AbstractSubCommand;
import net.draimcido.draimfarming.commands.SubCommand;
import net.draimcido.draimfarming.config.ConfigUtil;
import net.draimcido.draimfarming.config.MessageConfig;
import net.draimcido.draimfarming.utils.AdventureUtil;

import org.bukkit.command.CommandSender;

import java.util.List;

public final class ReloadCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() {
        super("reload", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            long time1 = System.currentTimeMillis();
            ConfigUtil.reloadConfigs();
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.reload.replace("{time}", String.valueOf(System.currentTimeMillis() - time1)));
            return true;
        }
        return super.onCommand(sender, args);
    }
}
