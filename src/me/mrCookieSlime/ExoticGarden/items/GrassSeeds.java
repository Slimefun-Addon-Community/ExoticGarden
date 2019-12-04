package me.mrCookieSlime.ExoticGarden.items;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;

public class GrassSeeds extends SimpleSlimefunItem<ItemInteractionHandler> {

	public GrassSeeds(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
	}
	
	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (isItem(item)) {
				Block b = e.getClickedBlock();
				if (b != null && b.getType() == Material.DIRT) {
					ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
					hand.setAmount(hand.getAmount() - 1);
					b.setType(Material.GRASS_BLOCK);
					if (b.getRelative(BlockFace.UP).getType() == Material.AIR || b.getRelative(BlockFace.UP).getType() == Material.CAVE_AIR) {
						b.getRelative(BlockFace.UP).setType(Material.GRASS);
					}
					
					b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
				}
				return true;
			}
			else return false;
		};
	}

}
