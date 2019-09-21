package me.mrCookieSlime.ExoticGarden.items;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;

public class GrassSeeds extends SimpleSlimefunItem<ItemInteractionHandler> {

	public GrassSeeds(Category category, ItemStack item, String id, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, id, recipeType, recipe);
	}
	
	@Override
	public ItemInteractionHandler getItemHandler() {
		return (e, p, item) -> {
			if (SlimefunManager.isItemSimiliar(item, getItem(), true)) {
				Block b = e.getClickedBlock();
				if (b != null && b.getType() == Material.DIRT) {
					PlayerInventory.consumeItemInHand(p);
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
