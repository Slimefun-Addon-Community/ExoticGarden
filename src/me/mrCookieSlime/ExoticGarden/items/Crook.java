package me.mrCookieSlime.ExoticGarden.items;

import java.util.Random;

import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;

public class Crook extends SimpleSlimefunItem<BlockBreakHandler> {
	
	private Random random = new Random();
	private int chance = 25;

	public Crook(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, id, recipeType, recipe);
	}
	
	public int getChance() {
		return chance;
	}
	
	@Override
	public BlockBreakHandler getItemHandler() {
		return (e, item, fortune, drops) -> {
			if (SlimefunManager.isItemSimiliar(item, getItem(), true)) {
				PlayerInventory.damageItemInHand(e.getPlayer());
				if (Tag.LEAVES.isTagged(e.getBlock().getType()) && random.nextInt(100) < getChance()) {
					ItemStack sapling = new ItemStack(e.getBlock().getType());
					drops.add(sapling);
				}
				return true;
			}
			return false;
		};
	}

}
