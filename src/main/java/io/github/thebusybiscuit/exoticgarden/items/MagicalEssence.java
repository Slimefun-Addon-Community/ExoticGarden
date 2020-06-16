package io.github.thebusybiscuit.exoticgarden.items;

import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class MagicalEssence extends SlimefunItem {

    public MagicalEssence(Category category, SlimefunItemStack item) {
        super(category, item, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] { item, item, item, item, null, item, item, item, item });
    }

    @Override
    public boolean useVanillaBlockBreaking() {
        return true;
    }

}
