package me.mrCookieSlime.ExoticGarden.items;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.DamageableItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.interfaces.NotPlaceable;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class Crook extends SimpleSlimefunItem<BlockBreakHandler> implements NotPlaceable, DamageableItem {
	
	private static final int CHANCE = 25;

	public Crook(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
	}

	@Override
	protected boolean areItemHandlersPrivate() {
		return false;
	}
	
	@Override
	public BlockBreakHandler getItemHandler() {
		return (e, item, fortune, drops) -> {
			if (isItem(item)) {
				damageItem(e.getPlayer(), item);
				if (Tag.LEAVES.isTagged(e.getBlock().getType()) && ThreadLocalRandom.current().nextInt(100) < CHANCE) {
					ItemStack sapling = new ItemStack(Material.getMaterial(e.getBlock().getType().toString().replace("LEAVES", "SAPLING")));
					drops.add(sapling);
				}
				return true;
			}
			return false;
		};
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

}
