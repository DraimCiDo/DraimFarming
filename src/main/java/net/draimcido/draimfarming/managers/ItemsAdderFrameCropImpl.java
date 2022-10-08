package net.draimcido.draimfarming.managers;

import net.draimcido.draimfarming.Main;
import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.config.BasicItemConfig;
import net.draimcido.draimfarming.config.CropConfig;
import net.draimcido.draimfarming.config.MainConfig;
import net.draimcido.draimfarming.integrations.customplugin.CustomInterface;
import net.draimcido.draimfarming.objects.GiganticCrop;
import net.draimcido.draimfarming.objects.fertilizer.Fertilizer;
import net.draimcido.draimfarming.objects.fertilizer.Gigantic;
import net.draimcido.draimfarming.objects.fertilizer.SpeedGrow;
import net.draimcido.draimfarming.utils.FurnitureUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

public class ItemsAdderFrameCropImpl implements CropModeInterface {

    private final CropManager cropManager;
    private final CustomInterface customInterface;

    public ItemsAdderFrameCropImpl(CropManager cropManager) {
        this.cropManager = cropManager;
        this.customInterface = cropManager.getCustomInterface();
    }

    @Override
    public boolean growJudge(Location location) {

        Chunk chunk = location.getChunk();

        if (chunk.isEntitiesLoaded()) {

            Location cropLoc = location.clone().add(0.5,0.5,0.5);
            ItemFrame itemFrame = FurnitureUtil.getItemFrame(cropLoc);
            if (itemFrame == null) return true;
            String id = customInterface.getItemID(itemFrame.getItem());
            if (id == null) return true;
            if (id.equals(BasicItemConfig.deadCrop)) return true;

            String[] cropNameList = StringUtils.split(id,"_");
            String cropKey = StringUtils.split(cropNameList[0], ":")[1];
            Crop crop = CropConfig.CROPS.get(cropKey);
            if (crop == null) return true;
            if (cropManager.isWrongSeason(location, crop.getSeasons())) {
                itemFrame.setItem(customInterface.getItemStack(BasicItemConfig.deadCrop), false);
                return true;
            }

            Location potLoc = location.clone().subtract(0,1,0);
            String potID = customInterface.getBlockID(potLoc);
            if (potID == null) return true;

            Fertilizer fertilizer = cropManager.getFertilizer(potLoc);
            boolean certainGrow = potID.equals(BasicItemConfig.wetPot);

            int nextStage = Integer.parseInt(cropNameList[2]) + 1;
            String temp = StringUtils.chop(id);
            if (customInterface.doesExist(temp + nextStage)) {
                if (MainConfig.enableCrow && cropManager.crowJudge(location, itemFrame)) return true;
                if (fertilizer instanceof SpeedGrow speedGrow && Math.random() < speedGrow.getChance()) {
                    if (customInterface.doesExist(temp + (nextStage+1))) {
                        addStage(itemFrame, temp + (nextStage+1));
                    }
                }
                else if (certainGrow || Math.random() < MainConfig.dryGrowChance) {
                    addStage(itemFrame, temp + nextStage);
                }
            }
            else {
                if (MainConfig.enableCrow && cropManager.crowJudge(location, itemFrame)) return true;
                GiganticCrop giganticCrop = crop.getGiganticCrop();
                if (giganticCrop != null) {
                    double chance = giganticCrop.getChance();
                    if (fertilizer instanceof Gigantic gigantic) {
                        chance += gigantic.getChance();
                    }
                    if (Math.random() < chance) {
                        Bukkit.getScheduler().runTask(Main.plugin, () -> {
                            customInterface.removeFurniture(itemFrame);
                            if (giganticCrop.isBlock()) {
                                customInterface.placeWire(location, giganticCrop.getBlockID());
                            }
                            else {
                                customInterface.placeFurniture(location, giganticCrop.getBlockID());
                            }
                        });
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void addStage(ItemFrame itemFrame, String stage) {
        itemFrame.setItem(customInterface.getItemStack(stage), false);
    }
}
