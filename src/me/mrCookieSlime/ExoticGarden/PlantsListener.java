package me.mrCookieSlime.ExoticGarden;

import java.util.Random;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.Schematic.Schematic;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class PlantsListener implements Listener {

	ExoticGarden plugin;
	Config cfg;
	BlockFace[] bf = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

	public PlantsListener(ExoticGarden plugin) {
		this.plugin = plugin;
		cfg = plugin.cfg;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		SlimefunItem item = BlockStorage.check(e.getLocation().getBlock());
		if (item != null) {
			e.setCancelled(true);
			if (!e.getLocation().getChunk().isLoaded()) e.getLocation().getWorld().loadChunk(e.getLocation().getChunk());
			for (Tree tree : ExoticGarden.trees) {
				if (item.getID().equalsIgnoreCase(tree.getSapling())) {
					BlockStorage.retrieve(e.getLocation().getBlock());
					Schematic.pasteSchematic(e.getLocation(), tree);
					return;
				}
			}
			for (Berry berry : ExoticGarden.berries) {
				if (item.getID().equalsIgnoreCase(berry.toBush())) {
					switch(berry.getType()) {
						case BUSH: {
							e.getLocation().getBlock().setType(Material.OAK_LEAVES);
							break;
						}
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							item = BlockStorage.check(e.getLocation().getBlock().getRelative(BlockFace.UP));
							if (item != null) return;
							switch(e.getLocation().getBlock().getRelative(BlockFace.UP).getType()) {
								case AIR:
								case CAVE_AIR:
								case OAK_SAPLING:
								case SPRUCE_SAPLING:
								case BIRCH_SAPLING:
								case JUNGLE_SAPLING:
								case ACACIA_SAPLING:
								case DARK_OAK_SAPLING:
								case OAK_LEAVES:
								case SPRUCE_LEAVES:
								case BIRCH_LEAVES:
								case JUNGLE_LEAVES:
								case ACACIA_LEAVES:
								case DARK_OAK_LEAVES:
								case SNOW:
									break;
								default:
									return;
							}

							BlockStorage.store(e.getLocation().getBlock().getRelative(BlockFace.UP), berry.getItem());
							e.getLocation().getBlock().setType(Material.OAK_LEAVES);
							e.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.PLAYER_HEAD);
							Rotatable s = (Rotatable) e.getLocation().getBlock().getRelative(BlockFace.UP).getBlockData();
							s.setRotation(bf[new Random().nextInt(bf.length)]);
							e.getLocation().getBlock().getRelative(BlockFace.UP).setBlockData(s);
							try {
								CustomSkull.setSkull(e.getLocation().getBlock().getRelative(BlockFace.UP), berry.getData().getTexture());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							break;
						}
						default: {
							e.getLocation().getBlock().setType(Material.PLAYER_HEAD);
							Rotatable s = (Rotatable) e.getLocation().getBlock().getBlockData();
							s.setRotation(bf[new Random().nextInt(bf.length)]);
							e.getLocation().getBlock().setBlockData(s);
							try {
								CustomSkull.setSkull(e.getLocation().getBlock(), berry.getData().getTexture());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							break;
						}
					}
					BlockStorage.store(e.getLocation().getBlock(), berry.getItem());
					e.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onGenerate(ChunkPopulateEvent e) {
		if (!cfg.getStringList("world-blacklist").contains(e.getWorld().getName())) {
			if (CSCoreLib.randomizer().nextInt(100) < cfg.getInt("chances.BUSH")) {
				Berry berry = ExoticGarden.berries.get(CSCoreLib.randomizer().nextInt(ExoticGarden.berries.size()));
				if (berry.getType().equals(PlantType.ORE_PLANT)) return;
				int x, z, y;
				x = e.getChunk().getX() * 16 + CSCoreLib.randomizer().nextInt(16);
				z = e.getChunk().getZ() * 16 + CSCoreLib.randomizer().nextInt(16);
				for (y = e.getWorld().getMaxHeight(); y > 30; y--) {
					Block current = e.getWorld().getBlockAt(x, y, z);
					if (!current.getType().isSolid() && current.getType() != Material.WATER && berry.isSoil(current.getRelative(BlockFace.DOWN).getType())) {
						BlockStorage.store(current, berry.getItem());
						switch (berry.getType()) {
							case BUSH: {
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> current.setType(Material.OAK_LEAVES));
								break;
							}
							case FRUIT: {
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
									current.setType(Material.PLAYER_HEAD);
									Rotatable s = (Rotatable) current.getBlockData();
									s.setRotation(bf[new Random().nextInt(bf.length)]);
									current.setBlockData(s);
									try {
										CustomSkull.setSkull(current, berry.getData().getTexture());
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								});
								break;
							}
							case ORE_PLANT:
							case DOUBLE_PLANT: {
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
									BlockStorage.store(current.getRelative(BlockFace.UP), berry.getItem());
									current.setType(Material.OAK_LEAVES);
									current.getRelative(BlockFace.UP).setType(Material.PLAYER_HEAD);
									Rotatable s = (Rotatable) current.getRelative(BlockFace.UP).getBlockData();
									s.setRotation(bf[new Random().nextInt(bf.length)]);
									current.getRelative(BlockFace.UP).setBlockData(s);
									try {
										CustomSkull.setSkull(current.getRelative(BlockFace.UP), berry.getData().getTexture());
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								});
								break;
							}
							default:
								break;
						}
						break;
					}
				}
			}
			else if (CSCoreLib.randomizer().nextInt(100) < cfg.getInt("chances.TREE")) {
				Tree tree = ExoticGarden.trees.get(CSCoreLib.randomizer().nextInt(ExoticGarden.trees.size()));
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
					int x, z, y;
					x = e.getChunk().getX() * 16 + CSCoreLib.randomizer().nextInt(16);
					z = e.getChunk().getZ() * 16 + CSCoreLib.randomizer().nextInt(16);
					boolean flat = false;
					for (y = e.getWorld().getMaxHeight(); y > 30; y--) {
						Block current = e.getWorld().getBlockAt(x, y, z);
						if (!current.getType().isSolid() && current.getType() != Material.WATER && current.getType() != Material.SEAGRASS && current.getType() != Material.TALL_SEAGRASS
								&& !(current.getBlockData() instanceof Waterlogged && ((Waterlogged) current.getBlockData()).isWaterlogged()) && tree.isSoil(current.getRelative(0, -1, 0).getType())) {
							flat = true;
							a: for (int i = 0; i < 5; i++) {
								for (int j = 0; j < 5; j++) {
									for (int k = 0; k < 6; k++) {
										if (current.getRelative(i, k, j).getType().isSolid() || Tag.LEAVES.isTagged(current.getRelative(i, k, j).getType()) || !current.getRelative(i, -1, j).getType().isSolid()) {
											flat = false;
											break a;
										}
									}
								}
							}
							if (flat) {
								Schematic.pasteSchematic(new Location(e.getWorld(), x, y, z), tree);
								break;
							}
						}
					}
				});
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHarvest(BlockBreakEvent e) {
		if (CSCoreLib.getLib().getProtectionManager().canBuild(e.getPlayer().getUniqueId(), e.getBlock())) {
			if (e.getBlock().getType().equals(Material.PLAYER_HEAD)) dropFruitFromTree(e.getBlock());
			if (Tag.LEAVES.isTagged(e.getBlock().getType())) dropFruitFromTree(e.getBlock());
			if (e.getBlock().getType() == Material.GRASS) {
				if (ExoticGarden.items.keySet().size() > 0 && e.getPlayer().getGameMode() != GameMode.CREATIVE)
					if (CSCoreLib.randomizer().nextInt(100) < 6) e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), ExoticGarden.items.get(ExoticGarden.items.keySet().toArray(new String[ExoticGarden.items.keySet().size()])[CSCoreLib.randomizer().nextInt(ExoticGarden.items.keySet().size())]));
			} else {
				ItemStack item = ExoticGarden.harvestPlant(e.getBlock());
				if (item != null) {
					e.setCancelled(true);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDecay(LeavesDecayEvent e) {
		String id = BlockStorage.checkID(e.getBlock());
		if (id != null) {
			for (Berry berry : ExoticGarden.berries) {
				if (id.equalsIgnoreCase(berry.getID())) {
					e.setCancelled(true);
					return;
				}
			}
		}
		dropFruitFromTree(e.getBlock());
		ItemStack item = BlockStorage.retrieve(e.getBlock());
		if (item != null) {
			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getPlayer().isSneaking()) return;
		if (CSCoreLib.getLib().getProtectionManager().canBuild(e.getPlayer().getUniqueId(), e.getClickedBlock())) {
			ItemStack item = ExoticGarden.harvestPlant(e.getClickedBlock());
			if (item != null ) {
				e.getClickedBlock().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
				e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), item);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onBlockExplode(BlockExplodeEvent e) {
		e.blockList().removeAll(explosionHandler(e.blockList()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onEntityExplode(EntityExplodeEvent e) {
		e.blockList().removeAll(explosionHandler(e.blockList()));
	}

	Set<Block> explosionHandler(List<Block> blockList) {
		Set<Block> blocksToRemove = new HashSet<Block>();
		for (Block block : blockList) {
			ItemStack item = ExoticGarden.harvestPlant(block);
			if (item != null) {
				blocksToRemove.add(block);
				block.getWorld().dropItemNaturally(block.getLocation(), item);
			}
		}
		return blocksToRemove;
	}

	public void dropFruitFromTree(Block block) {
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				for (int z = -1; z < 2; z++) { //inspect a cube at the reference
					Block drop = block.getRelative(x, y, z);
					SlimefunItem check = BlockStorage.check(drop);
					if (check != null) {
						for (Tree tree: ExoticGarden.trees) {
							if (check.getID().equalsIgnoreCase(tree.fruit)) {
								BlockStorage.clearBlockInfo(drop);
								ItemStack fruits = check.getItem();
								drop.getWorld().playEffect(drop.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								drop.getWorld().dropItemNaturally(drop.getLocation(), fruits);
								drop.setType(Material.AIR);
							}
						}
					}
				}
			}
		}
	}

}
