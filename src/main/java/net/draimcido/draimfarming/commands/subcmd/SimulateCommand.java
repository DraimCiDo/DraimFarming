package net.draimcido.draimfarming.commands.subcmd;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.commands.AbstractSubCommand;
import net.draimcido.draimfarming.commands.SubCommand;
import net.draimcido.draimfarming.config.MessageConfig;
import net.draimcido.draimfarming.utils.AdventureUtil;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimulateCommand extends AbstractSubCommand {

    public static final SubCommand INSTANCE = new SimulateCommand();

    private SimulateCommand() {
        super("simulate", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 4) {
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.lackArgs);
        }
        else {
            World world = Bukkit.getWorld(args.get(0));
            if (world == null) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.worldNotExists);
                return true;
            }
            int sprinklerTime;
            int growTime;
            int dryTime;
            try {
                sprinklerTime = Integer.parseInt(args.get(1));
                growTime = Integer.parseInt(args.get(3));
                dryTime = Integer.parseInt(args.get(2));
                if (sprinklerTime <= 0 || growTime <= 0 || dryTime <= 0 || (sprinklerTime + growTime + dryTime) > 23999) {
                    AdventureUtil.sendMessage(sender, MessageConfig.prefix + "Время должно быть положительным числом в диапазоне 1-23999");
                    return true;
                }
            }
            catch (IllegalArgumentException e) {
                AdventureUtil.sendMessage(sender, MessageConfig.prefix + "Время должно быть положительным числом в диапазоне 1-23999");
                e.printStackTrace();
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
                Main.plugin.getCropManager().grow(world, growTime, sprinklerTime, dryTime, false);
            });
            AdventureUtil.sendMessage(sender, MessageConfig.prefix + MessageConfig.growSimulation);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return getWorlds(args);
        }
        if (args.size() == 2) {
            return List.of("<SprinklerWorkTime>");
        }
        if (args.size() == 3) {
            return List.of("<PotDryTime>");
        }
        if (args.size() == 4) {
            return List.of("<CropGrowTime>");
        }
        return super.onTabComplete(sender, args);
    }
}
