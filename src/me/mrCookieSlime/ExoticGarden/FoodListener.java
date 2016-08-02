package me.mrCookieSlime.ExoticGarden;

import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EquipmentSlot;

public class FoodListener implements Listener {
	
	ExoticGarden plugin;
	
	public FoodListener(ExoticGarden plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onUse(final ItemUseEvent e) {
		if (e.getPlayer().getFoodLevel() >= 20) return;
		
		EquipmentSlot hand = e.getParentEvent().getHand();
		
		switch (hand) {
		case HAND: {
			SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
			if (item != null) {
				if (item instanceof EGPlant) {
					if (((EGPlant) item).isEdible()) {
						((EGPlant) item).restoreHunger(e.getPlayer());
						e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								e.getPlayer().getInventory().setItemInMainHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
							}
						}, 0L);
					}
				}
			}
			break;
		}
		case OFF_HAND: {
			SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
			if (item != null) {
				if (item instanceof EGPlant) {
					if (((EGPlant) item).isEdible()) {
						((EGPlant) item).restoreHunger(e.getPlayer());
						e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							
							@Override
							public void run() {
								e.getPlayer().getInventory().setItemInOffHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
							}
						}, 0L);
					}
				}
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
		if (e.getSlotType() != SlotType.ARMOR) return;
		SlimefunItem item = SlimefunItem.getByItem(e.getCursor());
		if (item != null && (item instanceof EGPlant) && e.getCursor().getType() == Material.SKULL_ITEM) e.setCancelled(true);
	}
}
