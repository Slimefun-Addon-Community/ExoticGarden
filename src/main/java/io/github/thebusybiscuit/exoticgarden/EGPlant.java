package io.github.thebusybiscuit.exoticgarden;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemUseHandler;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public class EGPlant extends HandledBlock {

    private final boolean edible;

    public EGPlant(Category category, SlimefunItemStack item, RecipeType recipeType, boolean edible, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        this.edible = edible;
    }

    @Override
    public void preRegister() {
        addItemHandler(onRightClick());
        super.preRegister();
    }

    public ItemUseHandler onRightClick() {
        return e -> {
            e.cancel();

            if (edible && e.getPlayer().getFoodLevel() < 20) {
                restoreHunger(e.getPlayer());
                ItemUtils.consumeItem(e.getItem(), false);
            }
        };
    }

    protected int getFoodValue() {
        return 2;
    }

    private void restoreHunger(Player p) {
        int level = p.getFoodLevel() + getFoodValue();
        p.playSound(p.getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
        p.setFoodLevel(Math.min(level, 20));
        p.setSaturation(p.getSaturation() + getFoodValue());
    }

}
