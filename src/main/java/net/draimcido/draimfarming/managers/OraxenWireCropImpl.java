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
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class OraxenWireCropImpl implements CropModeInterface{

    private final CropManager cropManager;
    private final CustomInterface customInterface;

    public OraxenWireCropImpl(CropManager cropManager) {
        this.cropManager = cropManager;
        this.customInterface = cropManager.getCustomInterface();
    }

    @Override
    public boolean growJudge(Location location) {
        String blockID = customInterface.getBlockID(location);
        if (blockID == null) return true;
        if (!blockID.contains("_stage_")) return true;
        String[] cropNameList = StringUtils.split(blockID,"_");
        String cropKey = cropNameList[0];
        Crop crop = CropConfig.CROPS.get(cropKey);
        if (crop == null) return true;
        if (cropManager.isWrongSeason(location, crop.getSeasons())) {
            Bukkit.getScheduler().runTask(Main.plugin, () -> {
                customInterface.removeBlock(location);
                customInterface.placeWire(location, BasicItemConfig.deadCrop);
            });
            return true;
        }
        Location potLoc = location.clone().subtract(0,1,0);
        String potID = customInterface.getBlockID(potLoc);
        if (potID == null) return true;

        Fertilizer fertilizer = cropManager.getFertilizer(potLoc);
        boolean certainGrow = potID.equals(BasicItemConfig.wetPot);
        int nextStage = Integer.parseInt(cropNameList[2]) + 1;
        String temp = StringUtils.chop(blockID);
        if (customInterface.doesExist(temp + nextStage)) {
            if (MainConfig.enableCrow && cropManager.crowJudge(location)) return true;
            if (fertilizer instanceof SpeedGrow speedGrow && Math.random() < speedGrow.getChance()) {
                if (customInterface.doesExist(temp + (nextStage+1))) {
                    addStage(location, temp + (nextStage+1));
                }
            }
            else if (certainGrow || Math.random() < MainConfig.dryGrowChance) {
                addStage(location, temp + nextStage);
            }
        }
        else {
            if (MainConfig.enableCrow && cropManager.crowJudge(location)) return true;
            GiganticCrop giganticCrop = crop.getGiganticCrop();
            if (giganticCrop != null) {
                double chance = giganticCrop.getChance();
                if (fertilizer instanceof Gigantic gigantic) {
                    chance += gigantic.getChance();
                }
                if (Math.random() < chance) {
                    Bukkit.getScheduler().runTask(Main.plugin, () -> {
                        customInterface.removeBlock(location);
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
        return false;
    }

    private void addStage(Location seedLoc, String stage) {
        Bukkit.getScheduler().runTask(Main.plugin, () -> customInterface.placeWire(seedLoc, stage));
    }
}
