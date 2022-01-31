package io.github.thebusybiscuit.exoticgarden;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

public class Berry {

    private final ItemStack item;
    private final String id;
    private final String texture;
    private final PlantType type;

    @ParametersAreNonnullByDefault
    public Berry(String id, PlantType type, String texture) {
        this(null, id, type, texture);
    }

    @ParametersAreNonnullByDefault
    public Berry(@Nullable ItemStack item, String id, PlantType type, String texture) {
        this.item = item;
        this.id = id;
        this.texture = texture;
        this.type = type;
    }

    /**
     * Returns the identifier of this Berry.
     *
     * @return the identifier of this Berry
     *
     * @since 1.7.0, rename of {@link #getName()}.
     */
    public String getID() {
        return this.id;
    }

    public ItemStack getItem() {
        return type == PlantType.ORE_PLANT ? item : SlimefunItem.getById(id).getItem();
    }

    public String getTexture() {
        return this.texture;
    }

    public PlantType getType() {
        return type;
    }

    public String toBush() {
        return type == PlantType.ORE_PLANT ? this.id.replace("_ESSENCE", "_PLANT") : this.id + "_BUSH";
    }

    public boolean isSoil(Material type) {
        List<Material> soils = Arrays.asList(Material.GRASS_BLOCK, Material.DIRT);
        return soils.contains(type);
    }

}
