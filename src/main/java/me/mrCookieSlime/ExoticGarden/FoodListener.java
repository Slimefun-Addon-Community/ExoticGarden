package me.mrCookieSlime.ExoticGarden;

import org.bukkit.Bukkit;
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

import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.InvUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

public class FoodListener implements Listener {

	private ExoticGarden plugin;

	public FoodListener(ExoticGarden plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onUse(ItemUseEvent e) {
		Player p = e.getPlayer();
		
		if (p.getFoodLevel() >= 20) return;

		if (e.getClickedBlock() != null && !p.isSneaking() && (e.getClickedBlock().getType().isInteractable() || ExoticGarden.getBerry(e.getClickedBlock()) != null)) {
			return;
		}

		EquipmentSlot hand = e.getParentEvent().getHand();

		switch (hand) {
			case HAND:
				SlimefunItem mainHand = SlimefunItem.getByItem(p.getInventory().getItemInMainHand());
				if (mainHand instanceof EGPlant && ((EGPlant) mainHand).isEdible()) {
					((EGPlant) mainHand).restoreHunger(p);
					p.getWorld().playSound(p.getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					final int slot = p.getInventory().getHeldItemSlot();
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> p.getInventory().setItem(slot, InvUtils.decreaseItem(p.getInventory().getItem(slot), 1)), 0L);
				}
				break;
			case OFF_HAND:
				SlimefunItem offHand = SlimefunItem.getByItem(p.getInventory().getItemInOffHand());
				if (offHand instanceof EGPlant && ((EGPlant) offHand).isEdible()) {
					((EGPlant) offHand).restoreHunger(p);
					p.getWorld().playSound(p.getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					
					Bukkit.getScheduler().runTaskLater(plugin, () -> p.getInventory().setItemInOffHand(InvUtils.decreaseItem(p.getInventory().getItemInOffHand(), 1)), 0L);
				}
				break;
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
		if (e.getItemInHand().getType() == Material.PLAYER_HEAD) {
			SlimefunItem item = SlimefunItem.getByItem(e.getItemInHand());
			
			if (item instanceof EGPlant) e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEquip(InventoryClickEvent e) {
		if (e.getSlotType() != SlotType.ARMOR || e.getCursor() == null || e.getCursor().getType() != Material.PLAYER_HEAD)
			return;
		
		SlimefunItem item = SlimefunItem.getByItem(e.getCursor());
		
		if (item instanceof EGPlant)
			e.setCancelled(true);
	}

}
