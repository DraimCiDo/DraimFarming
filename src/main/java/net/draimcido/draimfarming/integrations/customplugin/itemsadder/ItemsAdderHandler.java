package net.draimcido.draimfarming.integrations.customplugin.itemsadder;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.*;
import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.config.*;
import net.draimcido.draimfarming.integrations.AntiGrief;
import net.draimcido.draimfarming.managers.CustomWorld;
import net.kyori.adventure.key.Key;
import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.api.event.WaterEvent;
import net.draimcido.draimfarming.integrations.customplugin.HandlerP;
import net.draimcido.draimfarming.integrations.customplugin.itemsadder.listeners.ItemsAdderBlockListener;
import net.draimcido.draimfarming.integrations.customplugin.itemsadder.listeners.ItemsAdderFurnitureListener;
import net.draimcido.draimfarming.managers.CropManager;
import net.draimcido.draimfarming.objects.WaterCan;
import net.draimcido.draimfarming.utils.AdventureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemsAdderHandler extends HandlerP {

    private final ItemsAdderBlockListener itemsAdderBlockListener;
    private final ItemsAdderFurnitureListener itemsAdderFurnitureListener;

    public ItemsAdderHandler(CropManager cropManager) {
        super(cropManager);
        this.itemsAdderBlockListener = new ItemsAdderBlockListener(this);
        this.itemsAdderFurnitureListener = new ItemsAdderFurnitureListener(this);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getPluginManager().registerEvents(this.itemsAdderBlockListener, Main.plugin);
        Bukkit.getPluginManager().registerEvents(this.itemsAdderFurnitureListener, Main.plugin);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(this.itemsAdderBlockListener);
        HandlerList.unregisterAll(this.itemsAdderFurnitureListener);
    }

    public void placeScarecrow(FurniturePlaceSuccessEvent event) {
        if (!MainConfig.enableCrow) return;
        String id = event.getNamespacedID();
        if (id == null || !id.equals(BasicItemConfig.scarecrow)) return;
        Location location = event.getBukkitEntity().getLocation();
        CustomWorld customWorld = cropManager.getCustomWorld(location.getWorld());
        if (customWorld == null) return;
        customWorld.addScarecrow(location);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {

            Block block = event.getClickedBlock();

            if (block != null && ((block.getType().isInteractable() && block.getType() != Material.NOTE_BLOCK) || block.getType() == Material.TRIPWIRE)) return;

            ItemStack item = event.getItem();
            if (item == null || item.getType() == Material.AIR) return;
            NBTItem nbtItem = new NBTItem(item);
            NBTCompound iaCompound = nbtItem.getCompound("itemsadder");
            if (iaCompound == null) return;
            String namespace = iaCompound.getString("namespace");
            String id = iaCompound.getString("id");
            String namespacedID = namespace + ":" + id;

            if (fillWaterCan(namespacedID, nbtItem, item, player)) {
                return;
            }

            if (block == null) return;
            if (!AntiGrief.testPlace(player, block.getLocation())) return;

            if (event.getBlockFace() == BlockFace.UP && placeSprinkler(namespacedID, event.getClickedBlock().getLocation(), player, item)) {
                return;
            }
        }
    }

    public boolean tryMisc(Player player, ItemStack itemInHand, Location potLoc) {
        if (itemInHand == null || itemInHand.getType() == Material.AIR) return true;
        CustomStack customStack = CustomStack.byItemStack(itemInHand);

        if (customStack == null) return false;
        String itemID = customStack.getNamespacedID();

        if (useSurveyor(potLoc, itemID, player)) {
            return true;
        }
        if (useFertilizer(potLoc, itemID, player, itemInHand)){
            return true;
        }
        if (useWateringCan(potLoc, itemID, player, customStack)) {
            return true;
        }
        return false;
    }

    private boolean useWateringCan(Location potLoc, String namespacedID, Player player, @NotNull CustomStack can) {
        WaterCan waterCan = WaterCanConfig.CANS.get(namespacedID);

        if (waterCan == null) return false;

        ItemStack itemStack = can.getItemStack();
        NBTItem nbtItem = new NBTItem(itemStack);
        int water = nbtItem.getInteger("WaterAmount");
        if (water > 0) {

            WaterEvent waterEvent = new WaterEvent(player, can.getItemStack());
            Bukkit.getPluginManager().callEvent(waterEvent);
            if (waterEvent.isCancelled()) {
                return true;
            }

            NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
            if (nbtCompound.hasKey("custom_durability")){
                int dur = nbtCompound.getInteger("custom_durability");
                int max_dur = nbtCompound.getInteger("max_custom_durability");
                if (dur > 0){
                    nbtCompound.setInteger("custom_durability", dur - 1);
                    nbtCompound.setDouble("fake_durability", (int) itemStack.getType().getMaxDurability() * (double) (dur/max_dur));
                    nbtItem.setInteger("Damage", (int) (itemStack.getType().getMaxDurability() * (1 - (double) dur/max_dur)));
                }
                else {
                    AdventureUtil.playerSound(player, net.kyori.adventure.sound.Sound.Source.PLAYER, Key.key("minecraft:item.shield.break"), 1, 1);
                    itemStack.setAmount(itemStack.getAmount() - 1);
                }
            }

            nbtItem.setInteger("WaterAmount", --water);

            if (SoundConfig.waterPot.isEnable()) {
                AdventureUtil.playerSound(
                        player,
                        SoundConfig.waterPot.getSource(),
                        SoundConfig.waterPot.getKey(),
                        1,1
                );
            }

            if (MainConfig.enableActionBar) {
                String canID = customInterface.getItemID(itemStack);
                WaterCan canConfig = WaterCanConfig.CANS.get(canID);
                if (canConfig == null) return true;

                AdventureUtil.playerActionbar(
                        player,
                        (MainConfig.actionBarLeft +
                                MainConfig.actionBarFull.repeat(water) +
                                MainConfig.actionBarEmpty.repeat(canConfig.getMax() - water) +
                                MainConfig.actionBarRight)
                                .replace("{max_water}", String.valueOf(canConfig.getMax()))
                                .replace("{water}", String.valueOf(water))
                );
            }

            itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
            super.waterPot(waterCan.getWidth(), waterCan.getLength(), potLoc, player.getLocation().getYaw());
        }

        return true;
    }

    public Crop getCropFromID(String namespacedID) {
        String[] cropNameList = StringUtils.split(StringUtils.split(namespacedID, ":")[1], "_");
        return CropConfig.CROPS.get(cropNameList[0]);
    }

    public void onBreakBlock(CustomBlockBreakEvent event) {
    }

    public void onInteractBlock(CustomBlockInteractEvent event) {
    }

    public void onInteractFurniture(FurnitureInteractEvent event) {
    }

    public void onBreakFurniture(FurnitureBreakEvent event) {
    }

    public void chorusFix(Block block) {
        if (block.getType() != Material.CHORUS_PLANT) return;
        CustomBlock.remove(block.getLocation());
    }
}