package net.draimcido.draimfarming.integrations.customplugin.itemsadder;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import net.draimcido.draimfarming.integrations.customplugin.CustomInterface;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderHook implements CustomInterface {

    @Override
    public void removeBlock(Location location) {
        CustomBlock.remove(location);
    }

    @Override
    public void placeWire(Location location, String crop) {
        CustomBlock.place(crop, location);
    }

    @Override
    public void placeNoteBlock(Location location, String blockID) {
        CustomBlock.place(blockID, location);
    }

    @Override
    @Nullable
    public String getBlockID(Location location) {
        CustomBlock cb = CustomBlock.byAlreadyPlaced(location.getBlock());
        if (cb == null) return null;
        return cb.getNamespacedID();
    }

    @Override
    @Nullable
    public ItemStack getItemStack(String id) {
        if (id == null) return null;
        CustomStack cs = CustomStack.getInstance(id);
        if (cs == null) return null;
        return cs.getItemStack();
    }

    @Override
    @Nullable
    public String getItemID(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return null;
        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("itemsadder");
        if (nbtCompound == null) return null;
        return nbtCompound.getString("namespace") + ":" + nbtCompound.getString("id");
    }

    @Override
    @Nullable
    public ItemFrame placeFurniture(Location location, String id) {
        CustomFurniture customFurniture = CustomFurniture.spawn(id, location.getBlock());
        Entity entity = customFurniture.getArmorstand();
        if (entity instanceof ItemFrame itemFrame) {
            return itemFrame;
        }
        else {
            customFurniture.remove(false);
            return null;
        }
    }

    @Override
    public void removeFurniture(Entity entity) {
        CustomFurniture.remove(entity,false);
    }

    @Override
    public boolean doesExist(String itemID) {
        return CustomStack.getInstance(itemID) != null;
    }

}
