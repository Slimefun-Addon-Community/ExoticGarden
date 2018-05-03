package me.mrCookieSlime.ExoticGarden;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

public class FoodListener implements Listener {
	
	ExoticGarden plugin;
	
	public FoodListener(ExoticGarden plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onUse(final ItemUseEvent e) {
		if (e.getPlayer().getFoodLevel() >= 20) return;
		
		if (e.getClickedBlock() != null) {
			if (!e.getPlayer().isSneaking()) {
				switch (e.getClickedBlock().getType()) {
					case DISPENSER:
					case NOTE_BLOCK:
					case CHEST:
					case WORKBENCH:
					case FURNACE:
					case BURNING_FURNACE:
					case TRAP_DOOR:
					case FENCE_GATE:
					case ENCHANTMENT_TABLE:
					case BREWING_STAND:
					case ANVIL:
					case TRAPPED_CHEST:
					case HOPPER:
					case DROPPER:
					case IRON_TRAPDOOR:
					case SPRUCE_FENCE_GATE:
					case BIRCH_FENCE_GATE:
					case JUNGLE_FENCE_GATE:
					case DARK_OAK_FENCE_GATE:
					case ACACIA_FENCE_GATE:
					case SPRUCE_DOOR:
					case BIRCH_DOOR:
					case JUNGLE_DOOR:
					case ACACIA_DOOR:
					case DARK_OAK_DOOR:
					case WOOD_DOOR:
					case IRON_DOOR:
					case BED: 
						return;
					default:
				}
			}
		}
		
		EquipmentSlot hand = e.getParentEvent().getHand();
		
		switch (hand) {
			case HAND: {
				SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
				if (item != null && (item instanceof EGPlant) && ((EGPlant) item).isEdible()) {
					((EGPlant) item).restoreHunger(e.getPlayer());
					e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							e.getPlayer().getInventory().setItemInMainHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
						}
					}, 0L);
				}
				break;
			}
			case OFF_HAND: {
				SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
				if (item != null && (item instanceof EGPlant) && ((EGPlant) item).isEdible()) {
					((EGPlant) item).restoreHunger(e.getPlayer());
					e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						
						@Override
						public void run() {
							e.getPlayer().getInventory().setItemInOffHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
						}
					}, 0L);
				}
				break;
			}
			default:
				break;
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlace(BlockPlaceEvent e) {
		SlimefunItem item = SlimefunItem.getByItem(e.getItemInHand());
		if (item != null && (item instanceof EGPlant) && e.getItemInHand().getType() == Material.SKULL_ITEM) e.setCancelled(true);
	}
	
	@EventHandler
	public void onEquip(InventoryClickEvent e) {
		if (e.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
		if (!e.isShiftClick()) {
			if (e.getSlotType() != SlotType.ARMOR) return;
			SlimefunItem item = SlimefunItem.getByItem(e.getCursor());
			if (item != null && (item instanceof EGPlant) && e.getCursor().getType() == Material.SKULL_ITEM) e.setCancelled(true);
		} else {
			if (e.getAction().toString().startsWith("DROP") || e.getWhoClicked().getEquipment().getHelmet() != null || e.getSlotType() == SlotType.CRAFTING) return;
			SlimefunItem item = SlimefunItem.getByItem(e.getCurrentItem());
			if (item != null && (item instanceof EGPlant) && e.getCurrentItem().getType() == Material.SKULL_ITEM) {
				if (e.getSlot() > 8) {
					for (int i = 0; i < 9; i++) {
						if (e.getClickedInventory().getItem(i) == null || e.getClickedInventory().getItem(i).getType() == Material.AIR) {
							e.getClickedInventory().setItem(i, e.getCurrentItem());
							e.setCurrentItem(new ItemStack(Material.AIR));
							((Player) e.getWhoClicked()).updateInventory();
							return;
						}
					}
				}
				for (int i = 9; i < 36; i++) {
					if (e.getClickedInventory().getItem(i) == null || e.getClickedInventory().getItem(i).getType() == Material.AIR) {
						e.getClickedInventory().setItem(i, e.getCurrentItem());
						e.setCurrentItem(new ItemStack(Material.AIR));
						((Player) e.getWhoClicked()).updateInventory();
						return;
					}
				}
				e.setCancelled(true);
			}
		}
	}

}
