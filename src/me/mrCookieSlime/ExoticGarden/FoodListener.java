package me.mrCookieSlime.ExoticGarden;

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

	@EventHandler(priority = EventPriority.HIGH)
	public void onUse(final ItemUseEvent e) {
		if (e.getPlayer().getFoodLevel() >= 20) return;

		if (e.getClickedBlock() != null) {
			if (!e.getPlayer().isSneaking()) {
				switch (e.getClickedBlock().getType()) {
					case DISPENSER:
					case NOTE_BLOCK:
					case CHEST:
					case CRAFTING_TABLE:
					case FURNACE:
					case OAK_TRAPDOOR:
					case SPRUCE_TRAPDOOR:
					case BIRCH_TRAPDOOR:
					case JUNGLE_TRAPDOOR:
					case ACACIA_TRAPDOOR:
					case DARK_OAK_TRAPDOOR:
					case OAK_FENCE_GATE:
					case SPRUCE_FENCE_GATE:
					case BIRCH_FENCE_GATE:
					case JUNGLE_FENCE_GATE:
					case ACACIA_FENCE_GATE:
					case DARK_OAK_FENCE_GATE:
					case ENCHANTING_TABLE:
					case BREWING_STAND:
					case ANVIL:
					case TRAPPED_CHEST:
					case HOPPER:
					case DROPPER:
					case IRON_TRAPDOOR:
					case OAK_DOOR:
					case SPRUCE_DOOR:
					case BIRCH_DOOR:
					case JUNGLE_DOOR:
					case ACACIA_DOOR:
					case DARK_OAK_DOOR:
					case IRON_DOOR:
					case WHITE_BED:
					case ORANGE_BED:
					case MAGENTA_BED:
					case LIGHT_BLUE_BED:
					case YELLOW_BED:
					case LIME_BED:
					case PINK_BED:
					case GRAY_BED:
					case LIGHT_GRAY_BED:
					case CYAN_BED:
					case PURPLE_BED:
					case BLUE_BED:
					case BROWN_BED:
					case RED_BED:
					case BLACK_BED:
						return;
					default:
				}
				if (ExoticGarden.getBerry(e.getClickedBlock()) != null) return;
			}
		}

		EquipmentSlot hand = e.getParentEvent().getHand();

		switch (hand) {
			case HAND: {
				SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
				if (item != null && (item instanceof EGPlant) && ((EGPlant) item).isEdible()) {
					((EGPlant) item).restoreHunger(e.getPlayer());
					e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						e.getPlayer().getInventory().setItemInMainHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInMainHand(), 1));
					}, 0L);
				}
				break;
			}
			case OFF_HAND: {
				SlimefunItem item = SlimefunItem.getByItem(new CustomItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
				if (item != null && (item instanceof EGPlant) && ((EGPlant) item).isEdible()) {
					((EGPlant) item).restoreHunger(e.getPlayer());
					e.getPlayer().getWorld().playSound(e.getPlayer().getEyeLocation(), Sound.ENTITY_GENERIC_EAT, 1F, 1F);
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						e.getPlayer().getInventory().setItemInOffHand(InvUtils.decreaseItem(e.getPlayer().getInventory().getItemInOffHand(), 1));
					}, 0L);
				}
				break;
			}
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
		SlimefunItem item = SlimefunItem.getByItem(e.getItemInHand());
		if (item != null && (item instanceof EGPlant) && e.getItemInHand().getType() == Material.PLAYER_HEAD) e.setCancelled(true);
	}

	@EventHandler
	public void onEquip(InventoryClickEvent e) {
		if (e.getSlotType() != SlotType.ARMOR) return;
		SlimefunItem item = SlimefunItem.getByItem(e.getCursor());
		if (item != null && (item instanceof EGPlant) && e.getCursor().getType() == Material.PLAYER_HEAD) e.setCancelled(true);
	}

}
