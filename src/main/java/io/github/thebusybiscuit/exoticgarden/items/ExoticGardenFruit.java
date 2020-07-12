package io.github.thebusybiscuit.exoticgarden.items;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

public class ExoticGardenFruit extends SimpleSlimefunItem<ItemUseHandler> {

    private final boolean edible;

    public ExoticGardenFruit(Category category, SlimefunItemStack item, RecipeType recipeType, boolean edible, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
        this.edible = edible;
    }

    @Override
    public boolean useVanillaBlockBreaking() {
        return true;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> block = e.getClickedBlock();

            if (block.isPresent()) {
                Material material = block.get().getType();

                // Cancel the Block placement if the Player sneaks or the Block is not interactable
                if (e.getPlayer().isSneaking() || !isInteractable(material)) {
                    e.cancel();
                }
                else {
                    return;
                }
            }

            if (edible && e.getPlayer().getFoodLevel() < 20) {
                restoreHunger(e.getPlayer());
                ItemUtils.consumeItem(e.getItem(), false);
            }
        };
    }

    private boolean isInteractable(Material material) {
        // We cannot rely on Material#isInteractable() sadly
        // as it would allow the placement of this block on strange items like stairs...
        switch (material) {
        case ANVIL:
        case BREWING_STAND:
        case CAKE:
        case CHEST:
        case HOPPER:
        case TRAPPED_CHEST:
        case ENDER_CHEST:
        case CAULDRON:
        case SHULKER_BOX:
            return true;
        default:
            return material.name().equals("BARREL") || material.name().endsWith("_SHULKER_BOX");
        }
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
