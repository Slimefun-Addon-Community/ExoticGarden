package io.github.thebusybiscuit.exoticgarden.items;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;

public class GrassSeeds extends SimpleSlimefunItem<ItemUseHandler> {

    @ParametersAreNonnullByDefault
    public GrassSeeds(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ItemUseHandler getItemHandler() {
        return e -> {
            if (e.getClickedBlock().isPresent()) {
                Block b = e.getClickedBlock().get();

                if (b.getType() == Material.DIRT) {
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        ItemUtils.consumeItem(e.getItem(), false);
                    }

                    b.setType(Material.GRASS_BLOCK);

                    if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        b.getRelative(BlockFace.UP).setType(Material.GRASS);
                    }

                    b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
                }
            }
        };
    }

}
