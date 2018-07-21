package me.mrCookieSlime.ExoticGarden;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.Slimefun.Objects.Category;

public class CustomFood extends EGPlant {

	float food;

	public CustomFood(Category category, ItemStack item, String name, ItemStack[] recipe, int food) {
		super(category, item, name, Kitchen.KITCHEN, true, recipe);
		this.food = food;
	}

	@Override
	public void restoreHunger(Player p) {
		int level = p.getFoodLevel() + (int) food;
		p.setFoodLevel(level > 20 ? 20: level);
		p.setSaturation(food);
	}

}
