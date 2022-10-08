package net.draimcido.draimfarming.integrations.customplugin;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CustomInterface {

    void removeBlock(Location location);

    void placeWire(Location location, String crop);

    void placeNoteBlock(Location location, String blockID);

    @Nullable
    String getBlockID(Location location);

    @Nullable
    ItemStack getItemStack(String id);

    @Nullable
    String getItemID(ItemStack itemStack);

    @Nullable
    ItemFrame placeFurniture(Location location, String id);

    void removeFurniture(Entity entity);

    boolean doesExist(String itemID);

}
