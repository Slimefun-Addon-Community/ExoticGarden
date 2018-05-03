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
	
	public static final RecipeType KITCHEN = new RecipeType(new CustomItem(Material.CAULDRON_ITEM, "&eKitchen", 0, new String[] {"", "&a&oThis item should be made", "&a&oin the kitchen"}), "KITCHEN");
	
	public static void registerKitchen(ExoticGarden plugin) {
		
		new SlimefunMachine(Categories.MACHINES_1, ExoticGarden.KITCHEN, "KITCHEN",
		new ItemStack[] {new CustomItem(Material.WOOD_STAIRS, "&oWooden stairs (upside down)", 1), new CustomItem(Material.WOOD_STAIRS, "&oWooden stairs (upside down)", 1), new ItemStack(Material.WOOD), new ItemStack(Material.STONE_PLATE), new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.BOOKSHELF), new ItemStack(Material.FURNACE), new ItemStack(Material.DISPENSER), new ItemStack(Material.WORKBENCH)},
		new ItemStack[0], Material.IRON_TRAPDOOR)
		.register(true, new MultiBlockInteractionHandler() {

			@Override
			public boolean onInteract(Player p, MultiBlock mb, Block b) {

				SlimefunMachine machine = (SlimefunMachine) SlimefunItem.getByID("KITCHEN");

				if (mb.isMultiBlock(machine)) {
					if (Slimefun.hasUnlocked(p, machine.getItem(), true)) {
						Block raw_disp = b.getRelative(BlockFace.DOWN);
						Dispenser disp = (Dispenser) raw_disp.getState();
						final FurnaceInventory resinv;
						Furnace resf; 

						if (raw_disp.getRelative(BlockFace.EAST).getState().getBlock().getType().name() == "FURNACE") {
							resf = (Furnace) raw_disp.getRelative(BlockFace.EAST).getState();
							resinv = resf.getInventory();
						} else if (raw_disp.getRelative(BlockFace.WEST).getState().getBlock().getType().name() == "FURNACE") {
							resf = (Furnace) raw_disp.getRelative(BlockFace.WEST).getState();
							resinv = resf.getInventory();
						} else if (raw_disp.getRelative(BlockFace.NORTH).getState().getBlock().getType().name() == "FURNACE") {
							resf = (Furnace) raw_disp.getRelative(BlockFace.NORTH).getState();
							resinv = resf.getInventory();
						} else  {
							resf = (Furnace) raw_disp.getRelative(BlockFace.SOUTH).getState();
							resinv = resf.getInventory();
						}

						final Inventory inv = disp.getInventory();
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
									Inventory inv2 = Bukkit.createInventory(null, 9, "test");
									for (int j = 0; j < inv.getContents().length; j++) {
										inv2.setItem(j, inv.getContents()[j] != null ? (inv.getContents()[j].getAmount() > 1 ? new CustomItem(inv.getContents()[j], inv.getContents()[j].getAmount() - 1): null): null);
									}
									
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
										//SOUND
										Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										
											@Override
											public void run() {
												p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
												Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

													@Override
													public void run() {
														p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
														Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

															@Override
															public void run() {
																p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
																Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

																	@Override
																	public void run() {
																		p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
																		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

																			@Override
																			public void run() {
																				p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
																				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

																					@Override
																					public void run() {
																						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F);
																						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

																							@Override
																							public void run() {
																								p.getWorld().playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F);
																							}
																						}, 30L);
																					}
																				}, 5L);
																			}
																		}, 5L);
																	}
																}, 5L);
															}
														}, 5L);
													}
												}, 5L);
											}
										}, 5L);
										//END OF THE SOUND
										
										if (resinv.getResult() != null) {
											ItemStack s = new CustomItem(resinv.getResult(), resinv.getResult().getAmount() + adding.getAmount());
											resinv.setResult(s);
										}
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

}
