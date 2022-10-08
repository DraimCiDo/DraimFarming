package net.draimcido.draimfarming.api.utils;

import net.draimcido.draimfarming.api.crop.Crop;
import net.draimcido.draimfarming.config.CropConfig;

import org.jetbrains.annotations.Nullable;

public class CropUtils {

    @Nullable
    public static Crop getCrop(String crop) {
        return CropConfig.CROPS.get(crop);
    }
}
