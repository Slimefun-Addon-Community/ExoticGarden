package me.mrCookieSlime.ExoticGarden;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;

public class EGPlant extends HandledBlock {

	private static final int FOOD = 2;

	private final boolean edible;

	public EGPlant(Category category, SlimefunItemStack item, RecipeType recipeType, boolean edible, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
		this.edible = edible;
	}

	public boolean isEdible() {
		return this.edible;
	}

	public void restoreHunger(Player p) {
		int level = p.getFoodLevel() + FOOD;
		p.setFoodLevel(Math.min(level, 20));
		p.setSaturation(FOOD);
	}

}
