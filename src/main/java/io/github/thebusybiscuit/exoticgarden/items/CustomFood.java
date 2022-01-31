package io.github.thebusybiscuit.exoticgarden.items;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.exoticgarden.ExoticGardenRecipeTypes;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;

public class CustomFood extends ExoticGardenFruit {

    private final int food;

    @ParametersAreNonnullByDefault
    public CustomFood(ItemGroup itemGroup, SlimefunItemStack item, ItemStack[] recipe, int food) {
        super(itemGroup, item, ExoticGardenRecipeTypes.KITCHEN, true, recipe);
        this.food = food;
    }

    @ParametersAreNonnullByDefault
    public CustomFood(ItemGroup itemGroup, SlimefunItemStack item, int amount, ItemStack[] recipe, int food) {
        super(itemGroup, item, ExoticGardenRecipeTypes.KITCHEN, true, recipe, new SlimefunItemStack(item, amount));
        this.food = food;
    }

    @Override
    public int getFoodValue() {
        return food;
    }

}
