package io.github.thebusybiscuit.exoticgarden.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import org.bukkit.inventory.ItemStack;

public class BonemealableItem extends SlimefunItem {

    private final ItemSetting<Boolean> disableBoneMeal = new ItemSetting<>("disable-bonemeal", false);

    public BonemealableItem(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(disableBoneMeal);
    }

    public boolean isBonemealDisabled() {
        return disableBoneMeal.getValue();
    }
}
