package io.github.thebusybiscuit.exoticgarden.items;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class has an {@link ItemSetting} to disable bonemeal usage on this {@link SlimefunItem}.
 *
 * @author Walshy
 */
public class BonemealableItem extends SlimefunItem {

    private final ItemSetting<Boolean> disableBoneMeal = new ItemSetting<>(this, "disable-bonemeal", false);

    @ParametersAreNonnullByDefault
    public BonemealableItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);

        addItemSetting(disableBoneMeal);
    }

    public boolean isBonemealDisabled() {
        return disableBoneMeal.getValue();
    }
}
