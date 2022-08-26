package net.draimcido.draimfarming.limits;

import net.draimcido.draimfarming.ConfigReader;
import net.draimcido.draimfarming.utils.FurnitureUtils;
import org.bukkit.Location;

public class CropsPerChunkEntity {

    public static boolean isLimited(Location location){
        if (!ConfigReader.Config.enableLimit) return false;
        int n = 1;
        Location chunkLocation = new Location(location.getWorld(),location.getChunk().getX()*16+0.5,ConfigReader.Config.yMin+0.1,location.getChunk().getZ()*16+0.5);
        Label_out:
        for (int i = 0; i < 16; ++i)
            for (int j = 0; j < 16; ++j) {
                Location square = chunkLocation.clone().add(i, 0, j);
                for (int k = ConfigReader.Config.yMin; k <= ConfigReader.Config.yMax; ++k) {
                    square.add(0.0, 1.0, 0.0);
                    String namespacedID = FurnitureUtils.getNamespacedID(square);
                    if(namespacedID != null && namespacedID.contains("_stage_")){
                        if (n++ > ConfigReader.Config.cropLimit)
                            break Label_out;
                    }
                }
            }
        return n > ConfigReader.Config.cropLimit;
    }
}
