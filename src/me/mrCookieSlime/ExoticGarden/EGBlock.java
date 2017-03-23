package me.mrCookieSlime.ExoticGarden;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.inventory.ItemStack;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;

public class EGBlock extends HandledBlock
{
    public EGBlock(final Category category, final ItemStack item, final String name, final RecipeType recipeType, final ItemStack[] recipe) {
        super(category, item, name, recipeType, recipe);
    }
}
