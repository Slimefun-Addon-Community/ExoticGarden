package me.mrCookieSlime.ExoticGarden;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomFood extends EGPlant {
	
	float food;

	public CustomFood(Category category, ItemStack item, String name, ItemStack[] recipe, int food) {
		super(category, item, name, RecipeType.ENHANCED_CRAFTING_TABLE, true, recipe);
		this.food = food;
	}
	
	@Override
	public void restoreHunger(Player p) {
		int level = p.getFoodLevel() + (int) food;
		p.setFoodLevel(level > 20 ? 20: level);
		p.setSaturation(food);
	}

}
