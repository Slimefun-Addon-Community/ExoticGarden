package io.github.thebusybiscuit.exoticgarden.items;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.core.attributes.DamageableItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.NotPlaceable;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class Crook extends SimpleSlimefunItem<BlockBreakHandler> implements NotPlaceable, DamageableItem {

    private static final int CHANCE = 25;

    public Crook(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);
    }

    @Override
    public BlockBreakHandler getItemHandler() {
        return new BlockBreakHandler() {

            @Override
            public boolean isPrivate() {
                return false;
            }

            @Override
            public boolean onBlockBreak(BlockBreakEvent e, ItemStack item, int fortune, List<ItemStack> drops) {
                if (isItem(item)) {
                    damageItem(e.getPlayer(), item);

                    if (Tag.LEAVES.isTagged(e.getBlock().getType()) && ThreadLocalRandom.current().nextInt(100) < CHANCE) {
                        ItemStack sapling = new ItemStack(Material.getMaterial(e.getBlock().getType().toString().replace("LEAVES", "SAPLING")));
                        drops.add(sapling);
                    }

                    return true;
                }
                return false;
            }

        };
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

}
