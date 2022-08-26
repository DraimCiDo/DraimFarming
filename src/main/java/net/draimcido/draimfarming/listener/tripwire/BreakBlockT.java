package net.draimcido.draimfarming.listener.tripwire;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.datamanager.CropManager;
import net.draimcido.draimfarming.datamanager.PotManager;
import net.draimcido.draimfarming.integrations.protection.Integration;
import net.draimcido.draimfarming.objects.Crop;
import net.draimcido.draimfarming.objects.SimpleLocation;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.QualityCrop;
import net.draimcido.draimfarming.objects.fertilizer.YieldIncreasing;
import net.draimcido.draimfarming.utils.DropUtils;
import net.draimcido.draimfarming.utils.LocUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreakBlockT implements Listener {

    @EventHandler
    public void onBreak(CustomBlockBreakEvent event){
        String namespacedId = event.getNamespacedID();
        if(namespacedId.contains("_stage_")){
            Player player = event.getPlayer();
            Location location = event.getBlock().getLocation();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, player)) return;
            SimpleLocation simpleLocation = LocUtils.fromLocation(location);
            if (CropManager.Cache.remove(simpleLocation) == null){
                CropManager.RemoveCache.add(simpleLocation);
            }
            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                CustomBlock.place(namespacedId, location);
                CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot().forEach(itemStack -> location.getWorld().dropItem(location.clone().add(0.5,0.2,0.5), itemStack));
                CustomBlock.remove(location);
                return;
            }
            if (!ConfigReader.Config.quality || namespacedId.equals(ConfigReader.Basic.dead)) return;
            String[] cropNameList = StringUtils.split(StringUtils.split(namespacedId, ":")[1], "_");
            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            if (CustomBlock.getInstance(StringUtils.chop(namespacedId) + nextStage) == null) {
                Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, ()-> {
                    if (location.getBlock().getType() != Material.AIR) return;
                    Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                    ThreadLocalRandom current = ThreadLocalRandom.current();
                    int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                    Location itemLoc = location.clone().add(0.5,0.2,0.5);
                    World world = location.getWorld();
                    List<String> commands = cropInstance.getCommands();
                    Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
                    if (commands != null)
                        Bukkit.getScheduler().runTask(Main.plugin, ()-> {
                            for (String command : commands)
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
                        });
                    if (ConfigReader.Config.skillXP != null && cropInstance.getSkillXP() != 0)
                        Bukkit.getScheduler().runTask(Main.plugin, ()-> ConfigReader.Config.skillXP.addXp(player, cropInstance.getSkillXP()));
                    if (fertilizer != null){
                        if (fertilizer instanceof QualityCrop qualityCrop){
                            int[] weights = qualityCrop.getChance();
                            double weightTotal = weights[0] + weights[1] + weights[2];
                            Bukkit.getScheduler().runTask(Main.plugin, ()-> {
                                for (int i = 0; i < random; i++){
                                    double ran = Math.random();
                                    if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                    else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                    else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                }
                            });
                        }
                        else Bukkit.getScheduler().runTask(Main.plugin, ()-> DropUtils.normalDrop(cropInstance, random, itemLoc, world));
                    }
                    else Bukkit.getScheduler().runTask(Main.plugin, ()-> DropUtils.normalDrop(cropInstance, random, itemLoc, world));
                });
            }
        }
        else if(namespacedId.equalsIgnoreCase(ConfigReader.Basic.watered_pot) || namespacedId.equalsIgnoreCase(ConfigReader.Basic.pot)){
            Location location = event.getBlock().getLocation();
            PotManager.Cache.remove(LocUtils.fromLocation(location));
            World world = location.getWorld();
            Block blockUp = location.add(0,1,0).getBlock();
            for (Integration integration : ConfigReader.Config.integration)
                if(!integration.canBreak(location, event.getPlayer())) return;
            if(CustomBlock.byAlreadyPlaced(blockUp) != null){
                CustomBlock customBlock = CustomBlock.byAlreadyPlaced(blockUp);
                String cropNamespacedId = customBlock.getNamespacedID();
                if(cropNamespacedId.contains("_stage_")){
                    CustomBlock.remove(location);
                    SimpleLocation simpleLocation = LocUtils.fromLocation(location);
                    if (CropManager.Cache.remove(simpleLocation) == null){
                        CropManager.RemoveCache.add(simpleLocation);
                    }
                    if (cropNamespacedId.equals(ConfigReader.Basic.dead)) return;
                    if (ConfigReader.Config.quality){
                        String[] cropNameList = StringUtils.split(StringUtils.split(cropNamespacedId, ":")[1], "_");
                        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
                        if (CustomBlock.getInstance(StringUtils.chop(cropNamespacedId) + nextStage) == null) {
                            Crop cropInstance = ConfigReader.CROPS.get(cropNameList[0]);
                            ThreadLocalRandom current = ThreadLocalRandom.current();
                            int random = current.nextInt(cropInstance.getMin(), cropInstance.getMax() + 1);
                            Location itemLoc = location.clone().add(0.5,0.2,0.5);
                            Fertilizer fertilizer = PotManager.Cache.get(LocUtils.fromLocation(location.clone().subtract(0,1,0)));
                            if (fertilizer != null){
                                if (fertilizer instanceof QualityCrop qualityCrop){
                                    int[] weights = qualityCrop.getChance();
                                    double weightTotal = weights[0] + weights[1] + weights[2];
                                    for (int i = 0; i < random; i++){
                                        double ran = Math.random();
                                        if (ran < weights[0]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_1()).getItemStack());
                                        else if(ran > 1 - weights[1]/(weightTotal)) world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_2()).getItemStack());
                                        else world.dropItem(itemLoc, CustomStack.getInstance(cropInstance.getQuality_3()).getItemStack());
                                    }
                                }else if (fertilizer instanceof YieldIncreasing yieldIncreasing){
                                    if (Math.random() < yieldIncreasing.getChance()){
                                        random += yieldIncreasing.getBonus();
                                    }
                                    DropUtils.normalDrop(cropInstance, random , itemLoc, world);
                                }
                            }
                            else DropUtils.normalDrop(cropInstance, random, itemLoc, world);
                            return;
                        }
                    }
                    for (ItemStack itemStack : customBlock.getLoot())
                        world.dropItem(location.clone().add(0.5, 0.2, 0.5), itemStack);
                }
            }
        }
    }
}
