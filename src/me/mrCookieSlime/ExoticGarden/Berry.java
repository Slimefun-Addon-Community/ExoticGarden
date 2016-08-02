package me.mrCookieSlime.ExoticGarden;

import java.util.Arrays;
import java.util.List;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class Berry {
	
	ItemStack item;
	String name;
	PlantData data;
	PlantType type;
	
	public Berry(String name, PlantType type, PlantData data) {
		this.name = name;
		this.data = data;
		this.type = type;
	}
	
	public Berry(ItemStack item, String name, PlantType type, PlantData data) {
		this.item = item;
		this.name = name;
		this.data = data;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ItemStack getItem() {
		switch(type) {
			case ORE_PLANT: return item;
			default: return SlimefunItem.getByName(name).getItem();
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
			case ORE_PLANT: return this.name.replace("_ESSENCE", "_PLANT");
			default: return this.name + "_BUSH";
		}
	}

	public boolean isSoil(Material type) {
		List<Material> soils = Arrays.asList(Material.GRASS, Material.DIRT);
		return soils.contains(type);
	}

}
