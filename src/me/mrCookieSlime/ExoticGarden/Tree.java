package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.ExoticGarden.Schematic.Schematic;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

public class Tree {

	private String sapling;
	private String schematic;
	private String texture;
	private String fruit;
	private List<Material> soils;

	public Tree(String name, String fruit, String texture, Material... soil) {
		this.sapling = name + "_SAPLING";
		this.schematic = name + "_TREE";
		this.texture = texture;
		this.fruit = fruit;
		this.soils = Arrays.asList(soil);
	}

	public Schematic getSchematic() throws IOException {
	    return Schematic.loadSchematic(new File("plugins/ExoticGarden/schematics/" + schematic + ".schematic"));
	}

	public ItemStack getItem() {
		return SlimefunItem.getByID(sapling).getItem();
	}

	public String getTexture() {
		return this.texture;
	}

	public ItemStack getFruit() {
		return SlimefunItem.getByID(fruit).getItem();
	}
	
	public String getFruitID() {
		return fruit;
	}

	public String getSapling() {
		return this.sapling;
	}

	public boolean isSoil(Material material) {
		return soils.contains(material);
	}

}
