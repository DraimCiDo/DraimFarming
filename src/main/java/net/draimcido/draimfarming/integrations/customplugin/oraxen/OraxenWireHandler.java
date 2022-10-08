package net.draimcido.draimfarming.integrations.customplugin.oraxen;

import io.th0rgal.oraxen.events.*;
import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import io.th0rgal.oraxen.utils.drops.Drop;

import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.api.event.SeedPlantEvent;
import net.draimcido.draimfarming.config.*;
import net.draimcido.draimfarming.integrations.AntiGrief;
import net.draimcido.draimfarming.integrations.season.DFSeason;
import net.draimcido.draimfarming.managers.CropManager;
import net.draimcido.draimfarming.managers.CustomWorld;
import net.draimcido.draimfarming.objects.Sprinkler;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.requirements.PlantingCondition;
import net.draimcido.draimfarming.objects.requirements.RequirementInterface;
import net.draimcido.draimfarming.utils.AdventureUtil;
import net.draimcido.draimfarming.utils.FurnitureUtil;

import net.draimcido.draimfarming.utils.LimitationUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OraxenWireHandler extends OraxenHandler{

    public OraxenWireHandler(CropManager cropManager) {
        super(cropManager);
    }

    @Override
    public void onBreakNoteBlock(OraxenNoteBlockBreakEvent event) {
        if (event.isCancelled()) return;
    }

    @Override
    public void onBreakStringBlock(OraxenStringBlockBreakEvent event) {
        if (event.isCancelled()) return;

        StringBlockMechanic mechanic = event.getStringBlockMechanic();
        String id = mechanic.getItemID();

        final Player player = event.getPlayer();
        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        if (id.contains("_stage_")) {

            final Block block = event.getBlock();

            if (!AntiGrief.testBreak(player, block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH) || player.getInventory().getItemInMainHand().getType() == Material.SHEARS){
                event.setCancelled(true);
                Drop drop = mechanic.getDrop();
                if (player.getGameMode() != GameMode.CREATIVE && drop != null)
                    drop.spawns(block.getLocation(), new ItemStack(Material.AIR));
                block.setType(Material.AIR);
            }

            if (id.equals(BasicItemConfig.deadCrop)) return;
            if (hasNextStage(id)) {
                super.onBreakUnripeCrop(block.getLocation());
                return;
            }
            super.onBreakRipeCrop(block.getLocation(), id, player, true, false);
        }
    }

    @Override
    public void onBreakFurniture(OraxenFurnitureBreakEvent event) {
        if (event.isCancelled()) return;


        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onBreakSprinkler(event.getBlock().getLocation());
            return;
        }
        if (MainConfig.enableCrow && id.equals(BasicItemConfig.scarecrow)) {
            super.removeScarecrow(event.getBlock().getLocation());
        }
    }

    @Override
    public void onInteractFurniture(OraxenFurnitureInteractEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Location blockLoc = event.getItemFrame().getLocation();

        if (!AntiGrief.testPlace(player, blockLoc)) return;

        FurnitureMechanic mechanic = event.getFurnitureMechanic();
        if (mechanic == null) return;
        String id = mechanic.getItemID();
        Sprinkler sprinkler = SprinklerConfig.SPRINKLERS_3D.get(id);
        if (sprinkler != null) {
            super.onInteractSprinkler(blockLoc, player, player.getInventory().getItemInMainHand(), sprinkler);
        }
    }

    @Override
    public void onInteractNoteBlock(OraxenNoteBlockInteractEvent event) {
        if (event.isCancelled()) return;

        ItemStack itemInHand = event.getItemInHand();
        Location potLoc = event.getBlock().getLocation();
        Player player = event.getPlayer();

        if (!AntiGrief.testPlace(player, potLoc)) return;
        if (super.tryMisc(event.getPlayer(), itemInHand, potLoc)) return;
        if (event.getBlockFace() != BlockFace.UP) return;

        String id = OraxenItems.getIdByItem(itemInHand);
        if (id == null) return;
        if (id.endsWith("_seeds")) {
            String cropName = id.substring(0, id.length() - 6);
            Crop crop = CropConfig.CROPS.get(cropName);
            if (crop == null) return;

            Location seedLoc = potLoc.clone().add(0,1,0);
            CustomWorld customWorld = cropManager.getCustomWorld(seedLoc.getWorld());
            if (customWorld == null) return;

            if (FurnitureUtil.hasFurniture(seedLoc.clone().add(0.5,0.03125,0.5))) return;
            if (seedLoc.getBlock().getType() != Material.AIR) return;

            PlantingCondition plantingCondition = new PlantingCondition(seedLoc, player);

            if (crop.getRequirements() != null) {
                for (RequirementInterface requirement : crop.getRequirements()) {
                    if (!requirement.isConditionMet(plantingCondition)) {
                        return;
                    }
                }
            }

            if (MainConfig.limitation && LimitationUtil.reachWireLimit(potLoc)) {
                AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.limitWire.replace("{max}", String.valueOf(MainConfig.wireAmount)));
                return;
            }

            DFSeason[] seasons = crop.getSeasons();
            if (SeasonConfig.enable && seasons != null) {
                if (cropManager.isWrongSeason(seedLoc, seasons)) {
                    if (MainConfig.notifyInWrongSeason) AdventureUtil.playerMessage(player, MessageConfig.prefix + MessageConfig.wrongSeason);
                    if (MainConfig.preventInWrongSeason) return;
                }
            }

            SeedPlantEvent seedPlantEvent = new SeedPlantEvent(player, seedLoc, crop);
            Bukkit.getPluginManager().callEvent(seedPlantEvent);
            if (seedPlantEvent.isCancelled()) {
                return;
            }

            if (SoundConfig.plantSeed.isEnable()) {
                AdventureUtil.playerSound(
                        player,
                        SoundConfig.plantSeed.getSource(),
                        SoundConfig.plantSeed.getKey(),
                        1,1
                );
            }

            if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
            StringBlockMechanicFactory.setBlockModel(seedLoc.getBlock(), id.substring(0, id.length() - 5) + "stage_1");
            customWorld.addCrop(seedLoc, cropName);
        }
    }

    @Override
    public void onInteractStringBlock(OraxenStringBlockInteractEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();

        long time = System.currentTimeMillis();
        if (time - (coolDown.getOrDefault(player, time - 50)) < 50) return;
        coolDown.put(player, time);

        final Block block = event.getBlock();
        final String id = event.getStringBlockMechanic().getItemID();

        if (id.contains("_stage_")) {

            Location seedLoc = block.getLocation();
            ItemStack itemInHand = event.getItemInHand();
            if (!id.equals(BasicItemConfig.deadCrop)) {

                if (!hasNextStage(id)) {
                    if (MainConfig.canRightClickHarvest && !(MainConfig.emptyHand && itemInHand != null && itemInHand.getType() != Material.AIR)) {
                        if (!AntiGrief.testBreak(player, seedLoc)) return;

                        block.setType(Material.AIR);
                        this.onInteractRipeCrop(seedLoc, id, player);
                        return;
                    }
                }
                else if (MainConfig.enableBoneMeal && itemInHand != null && itemInHand.getType() == Material.BONE_MEAL) {
                    if (!AntiGrief.testPlace(player, seedLoc)) return;
                    if (player.getGameMode() != GameMode.CREATIVE) itemInHand.setAmount(itemInHand.getAmount() - 1);
                    if (Math.random() < MainConfig.boneMealChance) {
                        seedLoc.getWorld().spawnParticle(MainConfig.boneMealSuccess, seedLoc.clone().add(0.5,0.5, 0.5),3,0.2,0.2,0.2);
                        if (SoundConfig.boneMeal.isEnable()) {
                            AdventureUtil.playerSound(
                                    player,
                                    SoundConfig.boneMeal.getSource(),
                                    SoundConfig.boneMeal.getKey(),
                                    1,1
                            );
                        }
                        StringBlockMechanicFactory.setBlockModel(block, getNextStage(id));
                    }
                    return;
                }
            }

            if (!AntiGrief.testPlace(player, seedLoc)) return;

            Location potLoc = block.getLocation().clone().subtract(0,1,0);
            super.tryMisc(player, event.getItemInHand(), potLoc);

        }
    }

    private void onInteractRipeCrop(Location location, String id, Player player) {

        Crop crop = getCropFromID(id);
        if (crop == null) return;
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;

        Fertilizer fertilizer = customWorld.getFertilizer(location.clone().subtract(0,1,0));
        cropManager.proceedHarvest(crop, player, location, fertilizer);

        if (crop.getReturnStage() == null) {
            customWorld.removeCrop(location);
            return;
        }
        customWorld.addCrop(location, crop.getKey());
        StringBlockMechanicFactory.setBlockModel(location.getBlock(), crop.getReturnStage());
    }
}
