package net.draimcido.draimfarming.config;

import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.integrations.season.DFSeason;
import net.draimcido.draimfarming.objects.DFCrop;
import net.draimcido.draimfarming.objects.GiganticCrop;
import net.draimcido.draimfarming.objects.OtherLoot;
import net.draimcido.draimfarming.objects.QualityLoot;
import net.draimcido.draimfarming.objects.actions.*;
import net.draimcido.draimfarming.objects.requirements.*;
import net.draimcido.draimfarming.utils.AdventureUtil;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CropConfig {

    public static HashMap<String, Crop> CROPS;

    public static void load() {
        CROPS = new HashMap<>(16);
        YamlConfiguration config = ConfigUtil.getConfig("crops_" + MainConfig.customPlugin + ".yml");
        for (String key : config.getKeys(false)) {
            DFCrop crop = new DFCrop(key);
            for (String option : config.getConfigurationSection(key).getKeys(false)) {
                if (option.equals("quality-loots")) {
                    String amount = config.getString(key + ".quality-loots.amount", "1~2");
                    QualityLoot qualityLoot = new QualityLoot(
                            Integer.parseInt(amount.split("~")[0]),
                            Integer.parseInt(amount.split("~")[1]),
                            config.getString(key + ".quality-loots.quality.1"),
                            config.getString(key + ".quality-loots.quality.2"),
                            config.getString(key + ".quality-loots.quality.3")
                    );
                    crop.setQualityLoot(qualityLoot);
                }
                if (option.equals("other-loots")) {
                    List<OtherLoot> otherLoots = new ArrayList<>();
                    for (String loot : Objects.requireNonNull(config.getConfigurationSection(key + ".other-loots")).getKeys(false)) {
                        OtherLoot otherLoot = new OtherLoot(
                                config.getInt(key + ".other-loots." + loot + ".min_amount", 1),
                                config.getInt(key + ".other-loots." + loot + ".max_amount", 1),
                                config.getString(key + ".other-loots." + loot + ".item"),
                                config.getDouble(key + ".other-loots." + loot + ".chance", 1d)
                        );
                        otherLoots.add(otherLoot);
                    }
                    crop.setOtherLoots(otherLoots.toArray(new OtherLoot[0]));
                }
                if (option.equals("harvest-actions")) {
                    List<ActionInterface> actions = new ArrayList<>();
                    for (String action : Objects.requireNonNull(config.getConfigurationSection(key + ".harvest-actions")).getKeys(false)) {
                        switch (action) {
                            case "xp" -> actions.add(new ActionXP(config.getInt(key + ".harvest-actions." + action)));
                            case "skill-xp" -> actions.add(new ActionSkillXP(config.getDouble(key + ".harvest-actions." + action)));
                            case "commands" -> actions.add(new ActionCommand(config.getStringList(key + ".harvest-actions." + action).toArray(new String[0])));
                            case "messages" -> actions.add(new ActionMessage(config.getStringList(key + ".harvest-actions." + action).toArray(new String[0])));
                        }
                    }
                    crop.setActions(actions.toArray(new ActionInterface[0]));
                }
                if (option.equals("season")) {
                    List<String> seasonList = config.getStringList(key + ".season");
                    DFSeason[] seasons = new DFSeason[seasonList.size()];
                    for (int i = 0; i < seasonList.size(); i++) {
                        seasons[i] = DFSeason.valueOf(seasonList.get(i).toUpperCase());
                    }
                    crop.setSeasons(seasons);
                }
                if (option.equals("gigantic-crop")) {
                    boolean isBlock = true;
                    String blockID = config.getString(key + ".gigantic-crop.block");
                    if (blockID == null) {
                        blockID = config.getString(key + ".gigantic-crop.furniture");
                        isBlock = false;
                    }
                    GiganticCrop giganticCrop = new GiganticCrop(
                            config.getDouble(key + ".gigantic-crop.chance"),
                            isBlock,
                            blockID
                    );
                    crop.setGiganticCrop(giganticCrop);
                }
                if (option.equals("return")) {
                    crop.setReturnStage(config.getString(key + ".return"));
                }
                if (option.equals("requirements")) {
                    List<RequirementInterface> requirementList = new ArrayList<>();
                    for (String requirement : Objects.requireNonNull(config.getConfigurationSection(key + ".requirements")).getKeys(false)) {
                        String type = config.getString(key + ".requirements." + requirement + ".type");
                        if (type == null) continue;
                        switch (type) {
                            case "time" -> requirementList.add(new RequirementTime(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "weather" -> requirementList.add(new RequirementWeather(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "yPos" -> requirementList.add(new RequirementYPos(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "biome" -> requirementList.add(new RequirementBiome(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "world" -> requirementList.add(new RequirementWorld(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "permission" -> requirementList.add(new RequirementPermission(
                                    config.getStringList(key + ".requirements." + requirement + ".value").toArray(new String[0]),
                                    Objects.equals(config.getString(key + ".requirements." + requirement + ".mode"), "&&"),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                            case "papi-condition" -> requirementList.add(new CustomPapi(
                                    Objects.requireNonNull(config.getConfigurationSection(key + ".requirements." + requirement + ".papi-condition")).getValues(false),
                                    config.getString(key + ".requirements." + requirement + ".message")
                            ));
                        }
                    }
                    crop.setRequirements(requirementList.toArray(new RequirementInterface[0]));
                }
            }

            CROPS.put(key, crop);
        }
        AdventureUtil.consoleMessage("[DraimFishing] Загружено <green>" + CROPS.size() + "<gray> культур");
    }
}
