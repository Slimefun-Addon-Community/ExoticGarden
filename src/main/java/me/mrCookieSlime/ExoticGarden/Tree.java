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

	private final String sapling;
	private final String texture;
	private final String fruit;
	private final List<Material> soils;
	
	private Schematic schematic;

	public Tree(String fruit, String texture, Material... soil) {
		this.sapling = fruit + "_SAPLING";
		this.texture = texture;
		this.fruit = fruit;
		this.soils = Arrays.asList(soil);
	}

	public Schematic getSchematic() throws IOException {
	    if (schematic == null) {
	    	schematic = Schematic.loadSchematic(new File(ExoticGarden.getInstance().getSchematicsFolder(),
				fruit + "_TREE.schematic"));
	    }
		
		return schematic;
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
