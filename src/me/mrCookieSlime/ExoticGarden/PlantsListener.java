package me.mrCookieSlime.ExoticGarden;

import java.util.Random;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
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

public class PlantsListener implements Listener {
	
	Config cfg;
	BlockFace[] bf = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
	
	public PlantsListener(ExoticGarden plugin) {
		cfg = plugin.cfg;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGrow(StructureGrowEvent e) {
		SlimefunItem item = BlockStorage.check(e.getLocation().getBlock());
		if (item != null) {
			e.setCancelled(true);
			if (!e.getLocation().getChunk().isLoaded()) e.getLocation().getWorld().loadChunk(e.getLocation().getChunk());
			for (Tree tree: ExoticGarden.trees) {
				if (item.getName().equalsIgnoreCase(tree.getSapling())) {
					BlockStorage.retrieve(e.getLocation().getBlock());
					Schematic.pasteSchematic(e.getLocation(), tree);
					break;
				}
			}
			for (Berry berry: ExoticGarden.berries) {
				if (item.getName().equalsIgnoreCase(berry.toBush())) {
					BlockStorage.store(e.getLocation().getBlock(), berry.getItem());
					switch(berry.getType()) {
					case BUSH: {
						e.getLocation().getBlock().setType(Material.LEAVES);
						e.getLocation().getBlock().setData(berry.getData().toByte());
						break;
					}
					case ORE_PLANT:
					case DOUBLE_PLANT: {
						BlockStorage.store(e.getLocation().getBlock().getRelative(BlockFace.UP), berry.getItem());
						e.getLocation().getBlock().setType(Material.LEAVES);
						e.getLocation().getBlock().setData((byte) 0x4);
						e.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.SKULL);
						Skull s = (Skull) e.getLocation().getBlock().getRelative(BlockFace.UP).getState();
    					s.setSkullType(SkullType.PLAYER);
    					s.setRotation(bf[new Random().nextInt(bf.length)]);
    					s.setRawData((byte) 1);
    					s.update();
    					
    					try {
							CustomSkull.setSkull(e.getLocation().getBlock().getRelative(BlockFace.UP), berry.getData().getTexture());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						break;
					}
					default: {
						e.getLocation().getBlock().setType(Material.SKULL);
						Skull s = (Skull) e.getLocation().getBlock().getState();
    					s.setSkullType(SkullType.PLAYER);
    					s.setRotation(bf[new Random().nextInt(bf.length)]);
    					s.setRawData((byte) 1);
    					s.update();
    					
    					try {
							CustomSkull.setSkull(e.getLocation().getBlock(), berry.getData().getTexture());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						break;
					}
					}
					e.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGenerate(ChunkPopulateEvent e) {
		if (!cfg.getStringList("world-blacklist").contains(e.getWorld().getName())) {
		    if (CSCoreLib.randomizer().nextInt(100) < cfg.getInt("chances.TREE")) {
		    	Berry berry = ExoticGarden.berries.get(CSCoreLib.randomizer().nextInt(ExoticGarden.berries.size()));
		    	if (berry.getType().equals(PlantType.ORE_PLANT)) return;
		    	int x, z, y;
				x = e.getChunk().getX() * 16 + CSCoreLib.randomizer().nextInt(16);
				z = e.getChunk().getZ() * 16 + CSCoreLib.randomizer().nextInt(16);
				for (y = e.getWorld().getMaxHeight(); y > 30; y--) {
					Block current = e.getWorld().getBlockAt(x, y, z);
					if (!current.getType().isSolid() && current.getType() != Material.STATIONARY_WATER && berry.isSoil(current.getRelative(BlockFace.DOWN).getType())) {
						BlockStorage.store(current, berry.getItem());
						switch(berry.getType()) {
						case BUSH: {
							current.setType(Material.LEAVES);
							current.setData(berry.getData().toByte());
							break;
						}
						case FRUIT: {
							current.setType(Material.SKULL);
							Skull s = (Skull) current.getState();
	    					s.setSkullType(SkullType.PLAYER);
	    					s.setRotation(bf[new Random().nextInt(bf.length)]);
	    					s.setRawData((byte) 1);
	    					s.update();
	    					
	    					try {
								CustomSkull.setSkull(current, berry.getData().getTexture());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							break;
						}
						case ORE_PLANT:
						case DOUBLE_PLANT: {
							BlockStorage.store(current.getRelative(BlockFace.UP), berry.getItem());
							current.setType(Material.LEAVES);
							current.setData((byte) 0x4);
							current.getRelative(BlockFace.UP).setType(Material.SKULL);
							Skull s = (Skull) current.getRelative(BlockFace.UP).getState();
	    					s.setSkullType(SkullType.PLAYER);
	    					s.setRotation(bf[new Random().nextInt(bf.length)]);
	    					s.setRawData((byte) 1);
	    					s.update();
	    					
	    					try {
								CustomSkull.setSkull(current.getRelative(BlockFace.UP), berry.getData().getTexture());
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							break;
						}
						default:
							break;
						}
						break;
					}
				}
		    }
		    else if (CSCoreLib.randomizer().nextInt(100) < cfg.getInt("chances.BUSH")) {
				Tree tree = ExoticGarden.trees.get(CSCoreLib.randomizer().nextInt(ExoticGarden.trees.size()));
				int x, z, y;
				x = e.getChunk().getX() * 16 + CSCoreLib.randomizer().nextInt(16);
				z = e.getChunk().getZ() * 16 + CSCoreLib.randomizer().nextInt(16);
				boolean flat = false;
				for (y = e.getWorld().getMaxHeight(); y > 30; y--) {
					Block current = e.getWorld().getBlockAt(x, y, z);
					if (!current.getType().isSolid() && current.getType() != Material.STATIONARY_WATER && tree.isSoil(current.getRelative(0, -1, 0).getType())) {
						flat = true;
						for (int i = 0; i < 5; i++) {
							for (int j = 0; j < 5; j++) {
								for (int k = 0; k < 6; k++) {
									if ((current.getRelative(i, k, j).getType().isSolid() || current.getRelative(i, k, j).getType().toString().contains("LEAVES")) || !current.getRelative(i, -1, j).getType().isSolid()) flat = false;
								}
							}
						}
						if (flat) {
							Schematic.pasteSchematic(new Location(e.getWorld(), x, y, z), tree);
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHarvest(BlockBreakEvent e) {
		if(e.getBlock().getType().equals(Material.SKULL)) dropFruitFromTree(e.getBlock());
		if (e.getBlock().getType().equals(Material.LEAVES) || e.getBlock().getType().equals(Material.LEAVES_2)) dropFruitFromTree(e.getBlock());
		if (e.getBlock().getType() == Material.LONG_GRASS) {
			if (CSCoreLib.randomizer().nextInt(100) < 6) e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), ExoticGarden.items.get(ExoticGarden.items.keySet().toArray(new String[ExoticGarden.items.keySet().size()])[CSCoreLib.randomizer().nextInt(ExoticGarden.items.keySet().size())]));
		}
		else {
			ItemStack item = ExoticGarden.harvestPlant(e.getBlock());
			if (item != null) {
				e.setCancelled(true);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDecay(LeavesDecayEvent e) {
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
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		ItemStack item = ExoticGarden.harvestPlant(e.getClickedBlock());
		if (item != null ) {
			e.getClickedBlock().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.STEP_SOUND, Material.LEAVES);
			e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), item);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onBlockExplode(BlockExplodeEvent e)
	{
		e.blockList().removeAll(explosionHandler(e.blockList()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	void onEntityExplode(EntityExplodeEvent e)
	{
		e.blockList().removeAll(explosionHandler(e.blockList()));
	}

	Set<Block> explosionHandler(List<Block> blockList)
	{
		Set<Block> blocksToRemove = new HashSet<Block>();
		for (Block block : blockList)
		{
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
							if (check.getName().equalsIgnoreCase(tree.fruit)) {
								BlockStorage.clearBlockInfo(drop);
								ItemStack fruits = check.getItem();
								drop.getWorld().playEffect(drop.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
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
