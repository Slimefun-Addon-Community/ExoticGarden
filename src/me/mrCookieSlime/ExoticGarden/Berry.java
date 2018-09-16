package me.mrCookieSlime.ExoticGarden;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

public class Berry {

	ItemStack item;
	String id;
	PlantData data;
	PlantType type;

	public Berry(String id, PlantType type, PlantData data) {
		this.id = id;
		this.data = data;
		this.type = type;
	}

	public Berry(ItemStack item, String id, PlantType type, PlantData data) {
		this.item = item;
		this.id = id;
		this.data = data;
		this.type = type;
	}

	/**
	 * Returns the identifier of this Berry.
	 *
	 * @return the identifier of this Berry
	 *
	 * @since 1.7.0
	 * @deprecated As of 1.7.0, renamed to {@link #getID()} for better name convenience.
	 */
	@Deprecated
	public String getName() {
		return this.id;
	}

	/**
	 * Returns the identifier of this Berry.
	 *
	 * @return the identifier of this Berry
	 *
	 * @since 1.7.0, rename of {@link #getName()}.
	 */
	public String getID() {
		return this.id;
	}

	public ItemStack getItem() {
		switch(type) {
			case ORE_PLANT: return item;
			default: return SlimefunItem.getByID(id).getItem();
		}
	}

	public PlantData getData() {
		return this.data;
	}

	public PlantType getType() {
		return type;
	}

	public String toBush() {
		switch(type) {
			case ORE_PLANT: return this.id.replace("_ESSENCE", "_PLANT");
			default: return this.id + "_BUSH";
		}
	}

	public boolean isSoil(Material type) {
		List<Material> soils = Arrays.asList(Material.GRASS, Material.DIRT);
		return soils.contains(type);
	}

}
