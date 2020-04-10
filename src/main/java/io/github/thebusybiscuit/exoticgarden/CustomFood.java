package io.github.thebusybiscuit.exoticgarden;

import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class CustomFood extends EGPlant {

    private final int food;

    public CustomFood(Category category, SlimefunItemStack item, ItemStack[] recipe, int food) {
        super(category, item, ExoticGardenRecipeTypes.KITCHEN, true, recipe);
        this.food = food;
    }

    @Override
    protected int getFoodValue() {
        return food;
    }

}
