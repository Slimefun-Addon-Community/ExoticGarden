package me.mrCookieSlime.ExoticGarden.items;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;

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
					if (p.getGameMode() != GameMode.CREATIVE)
						ItemUtils.consumeItem(p.getInventory().getItemInMainHand(), false);
					b.setType(Material.GRASS_BLOCK);
					if (b.getRelative(BlockFace.UP).getType().isAir())
						b.getRelative(BlockFace.UP).setType(Material.GRASS);
					b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
				}
				return true;
			}
			return false;
		};
	}

}
