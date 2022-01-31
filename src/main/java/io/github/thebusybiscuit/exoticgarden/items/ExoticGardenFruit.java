package io.github.thebusybiscuit.exoticgarden.items;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.dre.brewery.BCauldron;
import com.dre.brewery.utility.LegacyUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;

public class ExoticGardenFruit extends SimpleSlimefunItem<ItemUseHandler> {

    private final boolean edible;

    @ParametersAreNonnullByDefault
    public ExoticGardenFruit(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, boolean edible, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        this.edible = edible;
    }

    @ParametersAreNonnullByDefault
    public ExoticGardenFruit(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, boolean edible, ItemStack[] recipe, ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
        this.edible = edible;
    }

    @Override
    public boolean useVanillaBlockBreaking() {
        return true;
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            Optional<Block> optionalBlock = e.getClickedBlock();

            if (optionalBlock.isPresent()) {
                Block block = optionalBlock.get();

                // Cancel the Block placement if the Player sneaks or the Block is not interactable
                if (e.getPlayer().isSneaking() || !isInteractable(block)) {
                    e.cancel();
                } else {
                    return;
                }
            } else {
                if (edible && e.getPlayer().getFoodLevel() < 20) {
                    restoreHunger(e.getPlayer());
                    ItemUtils.consumeItem(e.getItem(), false);
                }
            }
        };
    }

    private boolean isInteractable(@Nonnull Block block) {
        // We cannot rely on Material#isInteractable() sadly
        // as it would allow the placement of this block on strange items like stairs...
        Material material = block.getType();
        switch (material) {
            case ANVIL:
            case BREWING_STAND:
            case CAKE:
            case CHEST:
            case HOPPER:
            case TRAPPED_CHEST:
            case ENDER_CHEST:
            case SHULKER_BOX:
                return true;
            default:
                return material.name().equals("BARREL") ||
                        material.name().endsWith("_SHULKER_BOX") ||
                        isBreweryCauldron(block);
        }
    }

    private boolean isBreweryCauldron(@Nonnull Block block) {
        try {
            return LegacyUtil.isWaterCauldron(block.getType()) &&
                    LegacyUtil.isCauldronHeatsource(block.getRelative(BlockFace.DOWN));
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    protected int getFoodValue() {
        return 2;
    }

    private void restoreHunger(@Nonnull Player p) {
        int level = p.getFoodLevel() + getFoodValue();
        p.playSound(p.getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
        p.setFoodLevel(Math.min(level, 20));
        p.setSaturation(p.getSaturation() + getFoodValue());
    }

}
