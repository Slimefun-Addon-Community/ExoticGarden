package me.mrCookieSlime.ExoticGarden;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.multiblocks.MultiBlockMachine;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class Kitchen extends MultiBlockMachine {
	
	private RecipeType recipeT = new RecipeType(new CustomItem(Material.CAULDRON, "&eKitchen", "", "&a&oThis item should be made", "&a&oin the Kitchen"), "KITCHEN");
	private ExoticGarden plugin;
	
	public Kitchen(ExoticGarden plugin) {
		super(
			Categories.MACHINES_1, new CustomItem(Material.CAULDRON, "&eKitchen", new String[] {"", "&a&oYou can make a bunch of different yummies here!", "&a&oThe result goes in the Furnace output slot"}), "KITCHEN",
			new ItemStack[] {new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)"), new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)"), new ItemStack(Material.BRICKS), new ItemStack(Material.STONE_PRESSURE_PLATE), new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.BOOKSHELF), new ItemStack(Material.FURNACE), new ItemStack(Material.DISPENSER), new ItemStack(Material.CRAFTING_TABLE)},
			new ItemStack[0], 
			Material.IRON_TRAPDOOR
		);
		
		this.plugin = plugin;
		
		register();
		Slimefun.registerResearch(new Research(600, "Kitchen", 30), getItem());
	}
	
	public RecipeType asRecipeType() {
		return recipeT;
	}
	
	@Override
	public void onInteract(Player p, Block b) {
		Block raw_disp = b.getRelative(BlockFace.DOWN);

		Furnace resf = locateFurnace(raw_disp); 
		final FurnaceInventory resinv = resf.getInventory();

		final Inventory inv = ((Dispenser) raw_disp.getState()).getInventory();
		List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

		for (int i = 0; i < inputs.size(); i++) {
			boolean craft = true;
			for (int j = 0; j < inv.getContents().length; j++) {
				if (!SlimefunManager.isItemSimiliar(inv.getContents()[j], inputs.get(i)[j], true)) {
					craft = false;
					break;
				}
			}
			if (craft) {
				final ItemStack adding = RecipeType.getRecipeOutputList(this, inputs.get(i));
				if (Slimefun.hasUnlocked(p, adding, true)) {
					boolean fits_size = true;
					boolean is_same_r = true;

					if (resinv.getResult() != null) {
						fits_size = resinv.getResult().getAmount() + adding.getAmount() <= 64;
						ItemStack currentResult = new ItemStack(resinv.getResult());
						currentResult.setAmount(1);
						ItemStack newResult = new ItemStack(adding);
						newResult.setAmount(1);
						is_same_r = currentResult.equals(newResult);
					}

					if (is_same_r && fits_size) {
						for (int j = 0; j < 9; j++) {
							if (inv.getContents()[j] != null) {
								if (inv.getContents()[j].getType() != Material.AIR) {
									if (inv.getContents()[j].getType().toString().endsWith("_BUCKET")) inv.setItem(j, new ItemStack(Material.BUCKET));
									else if (inv.getContents()[j].getAmount() > 1) inv.setItem(j, new CustomItem(inv.getContents()[j], inv.getContents()[j].getAmount() - 1));
									else inv.setItem(j, null);
								}
							}
						}
						for (int j = 1; j < 7; j++) {
							Bukkit.getScheduler().runTaskLater(plugin, () -> {
								p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
							}, j*5L);
						}
						Bukkit.getScheduler().runTaskLater(plugin, () -> {
							p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
						}, 55L);

						if (resinv.getResult() != null) resinv.setResult(new CustomItem(resinv.getResult(), resinv.getResult().getAmount() + adding.getAmount()));
						else resinv.setResult(adding);
					}
					else {
						SlimefunPlugin.getLocal().sendMessage(p, "machines.full-inventory", true);
					}
				}
				
				return;
			}
		}
		
		SlimefunPlugin.getLocal().sendMessage(p, "machines.pattern-not-found", true);
	}

	private static Furnace locateFurnace(Block b) {
		if (b.getRelative(BlockFace.EAST).getType() == Material.FURNACE) {
			return (Furnace) b.getRelative(BlockFace.EAST).getState();
		} else if (b.getRelative(BlockFace.WEST).getType() == Material.FURNACE) {
			return (Furnace) b.getRelative(BlockFace.WEST).getState();
		} else if (b.getRelative(BlockFace.NORTH).getType() == Material.FURNACE) {
			return (Furnace) b.getRelative(BlockFace.NORTH).getState();
		} else {
			return (Furnace) b.getRelative(BlockFace.SOUTH).getState();
		}
	}

}
