package net.draimcido.draimfarming.config;

import net.draimcido.draimfarming.objects.QualityRatio;
import net.draimcido.draimfarming.objects.fertilizer.*;
import net.draimcido.draimfarming.utils.AdventureUtil;

import org.apache.commons.lang.StringUtils;

import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Objects;

public class FertilizerConfig {

    public static HashMap<String, Fertilizer> FERTILIZERS;

    public static void load() {
        FERTILIZERS = new HashMap<>(16);
        YamlConfiguration config = ConfigUtil.getConfig("fertilizers_" + MainConfig.customPlugin + ".yml");
        int amount = 0;
        for (String key : config.getKeys(false)) {
            switch (key) {
                case "speed" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        SpeedGrow speedGrow = new SpeedGrow(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            speedGrow.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), speedGrow);
                        FERTILIZERS.put(fertilizer, speedGrow);
                        amount++;
                    }
                }
                case "gigantic" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        Gigantic gigantic = new Gigantic(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            gigantic.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), gigantic);
                        FERTILIZERS.put(fertilizer, gigantic);
                        amount++;
                    }
                }
                case "retaining" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        RetainingSoil retainingSoil = new RetainingSoil(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            retainingSoil.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), retainingSoil);
                        FERTILIZERS.put(fertilizer, retainingSoil);
                        amount++;
                    }
                }
                case "quantity" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        YieldIncreasing yieldIncreasing = new YieldIncreasing(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                config.getInt(key + "." +fertilizer + ".bonus",1),
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            yieldIncreasing.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), yieldIncreasing);
                        FERTILIZERS.put(fertilizer, yieldIncreasing);
                        amount++;
                    }
                }
                case "quality" -> {
                    for (String fertilizer : Objects.requireNonNull(config.getConfigurationSection(key)).getKeys(false)) {
                        String[] split = StringUtils.split(config.getString(key + "." + fertilizer + ".ratio"), "/");
                        double[] weight = new double[3];
                        weight[0] = Double.parseDouble(split[0]);
                        weight[1] = Double.parseDouble(split[1]);
                        weight[2] = Double.parseDouble(split[2]);
                        double weightTotal = weight[0] + weight[1] + weight[2];
                        QualityRatio qualityRatio = new QualityRatio(weight[0]/(weightTotal), 1 - weight[1]/(weightTotal));
                        QualityCrop qualityCrop = new QualityCrop(
                                fertilizer,
                                config.getInt(key + "." + fertilizer + ".times", 14),
                                config.getDouble(key + "." +fertilizer + ".chance", 0.01),
                                qualityRatio,
                                config.getBoolean(key + "." + fertilizer + ".before-plant", true),
                                config.getString(key + "." + fertilizer + ".name")
                        );
                        if (config.contains(key + "." + fertilizer + ".particle")) {
                            qualityCrop.setParticle(Particle.valueOf(config.getString(key + "." + fertilizer + ".particle", "VILLAGER_HAPPY").toUpperCase()));
                        }
                        FERTILIZERS.put(config.getString(key + "." + fertilizer + ".item"), qualityCrop);
                        FERTILIZERS.put(fertilizer, qualityCrop);
                        amount++;
                    }
                }
            }
        }
        AdventureUtil.consoleMessage("[DraimFishing] Загружено <green>" + amount + "<gray> удобрений");
    }
}
