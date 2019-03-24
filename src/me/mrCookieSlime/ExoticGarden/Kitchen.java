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

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.MultiBlock;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunMachine;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.MultiBlockInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.Messages;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class Kitchen {

	public static final RecipeType KITCHEN = new RecipeType(new CustomItem(Material.CAULDRON, "&eKitchen", "", "&a&oThis item should be made", "&a&oin the Kitchen"), "KITCHEN");

	public static void registerKitchen(ExoticGarden plugin) {

		new SlimefunMachine(Categories.MACHINES_1, ExoticGarden.KITCHEN, "KITCHEN",
		new ItemStack[] {new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)", 1), new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)", 1), new ItemStack(Material.BRICKS), new ItemStack(Material.STONE_PRESSURE_PLATE), new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.BOOKSHELF), new ItemStack(Material.FURNACE), new ItemStack(Material.DISPENSER), new ItemStack(Material.CRAFTING_TABLE)},
		new ItemStack[0], Material.IRON_TRAPDOOR)
		.register(true, new MultiBlockInteractionHandler() {
			@Override
			public boolean onInteract(Player p, MultiBlock mb, Block b) {
				SlimefunMachine machine = (SlimefunMachine) SlimefunItem.getByID("KITCHEN");
				if (mb.isMultiBlock(machine)) {
					if (CSCoreLib.getLib().getProtectionManager().canAccessChest(p.getUniqueId(), b, true) && Slimefun.hasUnlocked(p, machine.getItem(), true)) {
						Block raw_disp = b.getRelative(BlockFace.DOWN);

						Furnace resf = locateFurnace(raw_disp); 
						final FurnaceInventory resinv = resf.getInventory();

						final Inventory inv = ((Dispenser) raw_disp.getState()).getInventory();
						List<ItemStack[]> inputs = RecipeType.getRecipeInputList(machine);

						for (int i = 0; i < inputs.size(); i++) {
							boolean craft = true;
							for (int j = 0; j < inv.getContents().length; j++) {
								if (!SlimefunManager.isItemSimiliar(inv.getContents()[j], inputs.get(i)[j], true)) {
									craft = false;
									break;
								}
							}
							if (craft) {
								final ItemStack adding = RecipeType.getRecipeOutputList(machine, inputs.get(i));
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
									else Messages.local.sendTranslation(p, "machines.full-inventory", true);
								}
								return true;
							}
						}
						Messages.local.sendTranslation(p, "machines.pattern-not-found", true);
					}
					return true;
				}
				else return false;
			}
		});

	    Slimefun.registerResearch(new Research(600, "Kitchen", 30), ExoticGarden.KITCHEN);
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
