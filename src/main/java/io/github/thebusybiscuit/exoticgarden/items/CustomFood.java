package io.github.thebusybiscuit.exoticgarden.items;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.exoticgarden.ExoticGardenRecipeTypes;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class CustomFood extends ExoticGardenFruit {

    private final int food;

    @ParametersAreNonnullByDefault
    public CustomFood(Category category, SlimefunItemStack item, ItemStack[] recipe, int food) {
        super(category, item, ExoticGardenRecipeTypes.KITCHEN, true, recipe);
        this.food = food;
    }

    @ParametersAreNonnullByDefault
    public CustomFood(Category category, SlimefunItemStack item, int amount, ItemStack[] recipe, int food) {
        super(category, item, ExoticGardenRecipeTypes.KITCHEN, true, recipe, new SlimefunItemStack(item, amount));
        this.food = food;
    }

    @Override
    public int getFoodValue() {
        return food;
    }

}
