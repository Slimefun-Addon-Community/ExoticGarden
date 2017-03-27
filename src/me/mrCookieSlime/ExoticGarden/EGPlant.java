package me.mrCookieSlime.ExoticGarden;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;

public class EGPlant extends HandledBlock {

	boolean edible;
	
	public EGPlant(Category category, ItemStack item, String name, RecipeType recipeType, boolean edible, ItemStack[] recipe) {
		super(category, item, name, recipeType, recipe);
		this.edible = edible;
	}
	
	public boolean isEdible() {
		return this.edible;
	}
	
	private static final int food = 2;
	
	public void restoreHunger(Player p) {
		int level = p.getFoodLevel() + (int) food;
		p.setFoodLevel(level > 20 ? 20: level);
		p.setSaturation(food);
	}

}
