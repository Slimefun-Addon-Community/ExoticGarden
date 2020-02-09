package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.ExoticGarden.items.Crook;
import me.mrCookieSlime.ExoticGarden.items.GrassSeeds;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.BukkitUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;

public class ExoticGarden extends JavaPlugin {

	public static ExoticGarden instance;

	private final File schematicsFolder = new File(getDataFolder(), "schematics");

	private List<Berry> berries = new ArrayList<>();
	private List<Tree> trees = new ArrayList<>();
	private Map<String, ItemStack> items = new HashMap<>();

	protected Config cfg;

	private Category mainCategory;
	private Category foodCategory;
	private Category drinksCategory;
	private Category magicalCategory;
	private Kitchen kitchen;

	@Override
	public void onEnable() {
		if (!schematicsFolder.exists()) schematicsFolder.mkdirs();

    	instance = this;
    	cfg = new Config(this);

		// Setting up bStats
		new Metrics(this, 4575);

		// Setting up the Auto-Updater
		Updater updater;

		if (!getDescription().getVersion().startsWith("DEV - ")) {
			// We are using an official build, use the BukkitDev Updater
			updater = new BukkitUpdater(this, getFile(), 88425);
		}
		else {
			// If we are using a development build, we want to switch to our custom
			updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ExoticGarden/master");
		}

		// Only run the Updater if it has not been disabled
		if (cfg.getBoolean("options.auto-update")) updater.start();

		mainCategory = new Category(new NamespacedKey(this, "plants_and_fruits"), new CustomItem(SkullItem.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&aExotic Garden - Plants and Fruits"));
		foodCategory = new Category(new NamespacedKey(this, "food"), new CustomItem(SkullItem.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&aExotic Garden - Food"));
		drinksCategory = new Category(new NamespacedKey(this, "drinks"), new CustomItem(SkullItem.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&aExotic Garden - Drinks"));
		magicalCategory = new Category(new NamespacedKey(this, "magical_crops"), new CustomItem(Material.BLAZE_POWDER, "&5Exotic Garden - Magical Plants"));

		SlimefunItemStack iceCube = new SlimefunItemStack("ICE_CUBE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ==", "&bIce Cube");
		new SlimefunItem(Categories.MISC, iceCube, RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(iceCube, 4))
		.register();

		kitchen = new Kitchen(this);

		registerBerry("Grape", "&c", Color.RED, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ==");
		registerBerry("Blueberry", "&9", Color.BLUE, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0=");
		registerBerry("Elderberry", "&c", Color.FUCHSIA, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ==");
		registerBerry("Raspberry", "&d", Color.FUCHSIA, PlantType.BUSH,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ==");
		registerBerry("Blackberry", "&8", Color.GRAY, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0=");
		registerBerry("Cranberry", "&c", Color.FUCHSIA, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0=");
		registerBerry("Cowberry", "&c", Color.FUCHSIA, PlantType.BUSH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19");
		registerBerry("Strawberry", "&4", Color.FUCHSIA, PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0=");

		registerPlant("Tomato", "&4", PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19");
		registerPlant("Lettuce", "&2", PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0=");
		registerPlant("Tea Leaf", "&a", PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0=");
		registerPlant("Cabbage", "&2",  PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ==");
		registerPlant("Sweet Potato", "&6",  PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0=");
		registerPlant("Mustard Seed", "&e",  PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ==");
		registerPlant("Curry Leaf", "&2",  PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzJhZjdmYThiZGYzMjUyZjY5ODYzYjIwNDU1OWQyM2JmYzJiOTNkNDE0MzcxMDM0MzdhYjE5MzVmMzIzYTMxZiJ9fX0=");
		registerPlant("Onion", "&c",  PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlMDM2ZTMyN2NiOWQ0ZDhmZWYzNjg5N2E4OTYyNGI1ZDliMThmNzA1Mzg0Y2UwZDdlZDFlMWZjN2Y1NiJ9fX0=");
		registerPlant("Garlic", "&r",  PlantType.FRUIT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA1MmQ5YzExODQ4ZWJjYzlmODM0MDMzMjU3N2JmMWQyMmI2NDNjMzRjNmFhOTFmZTRjMTZkNWE3M2Y2ZDgifX19");
		registerPlant("Cilantro", "&a",  PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYxNDkxOTZmM2E4ZDZkNmYyNGU1MWIyN2U0Y2I3MWM2YmFiNjYzNDQ5ZGFmZmI3YWEyMTFiYmU1NzcyNDIifX19");
		registerPlant("Black Pepper", "&8",  PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MmI5YmY5ZjFmNjI5NTg0MmIwZWZiNTkxNjk3YjE0NDUxZjgwM2ExNjVhZTU4ZDBkY2ViZDk4ZWFjYyJ9fX0=");

		registerPlant("Corn", "&6",  PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0=");
		registerPlant("Pineapple", "&6",  PlantType.DOUBLE_PLANT, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19");

		registerTree("Oak Apple", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "&c", Color.FUCHSIA, "Oak Apple Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Coconut", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "&6", Color.MAROON, "Coconut Milk", false, Material.SAND);
		registerTree("Cherry", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "&c", Color.FUCHSIA, "Cherry Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pomegranate", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "&4", Color.RED, "Pomegranate Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lemon", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "&e", Color.YELLOW, "Lemon Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Plum", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "&5", Color.RED, "Plum Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lime", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "&a", Color.LIME, "Lime Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Orange", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "&6", Color.ORANGE, "Orange Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Peach", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "&5", Color.RED, "Peach Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pear", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "&a", Color.LIME, "Pear Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Dragon Fruit", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "&d", Color.FUCHSIA, "Dragon Fruit Juice", true, Material.DIRT, Material.GRASS_BLOCK);

		registerDishes();

		registerMagicalPlant("Coal", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("Iron", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("Gold", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});

		registerMagicalPlant("Copper", new CustomItem(SlimefunItems.COPPER_DUST, 8),  "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkyM2RiZmQ4ZjMxMTI2OTBiYjVhNjE2OGE4ZDNjYTVhYjllN2Q0M2IxZDExY2ZjYjY0M2RlN2RmZTIxIn19fQ==",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("Redstone", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("Lapis", new ItemStack(Material.LAPIS_LAZULI, 16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("Ender", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("Quartz", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("Diamond", new ItemStack(Material.DIAMOND), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg4Y2Q2ZGQ1MDM1OWM3ZDU4OThjN2M3ZTNlMjYwYmZjZDNkY2IxNDkzYTg5YjllODhlOWNiZWNiZmU0NTk0OSJ9fX0=",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("Emerald", new ItemStack(Material.EMERALD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZjNDk1ZDFlNmViNTRhMzg2MDY4YzZjYjEyMWM1ODc1ZTAzMWI3ZjYxZDcyMzZkNWYyNGI3N2RiN2RhN2YifX19",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("Glowstone", new ItemStack(Material.GLOWSTONE_DUST, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjVkN2JlZDhkZjcxNGNlYTA2M2U0NTdiYTVlODc5MzExNDFkZTI5M2RkMWQ5YjkxNDZiMGY1YWIzODM4NjYifX19",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("Obsidian", new ItemStack(Material.OBSIDIAN, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("Slime", new ItemStack(Material.SLIME_BALL, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNjVlNmU1MTEzYTUxODdkYWQ0NmRmYWQzZDNiZjg1ZThlZjgwN2Y4MmFhYzIyOGE1OWM0YTk1ZDZmNmEifX19",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		new Crook(Categories.TOOLS, new SlimefunItemStack("CROOK", new CustomItem(Material.WOODEN_HOE, "&rCrook", "", "&7+ &b25% &7Sapling Drop Rate")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
		.register();

		SlimefunItemStack grassSeeds = new SlimefunItemStack("GRASS_SEEDS", Material.PUMPKIN_SEEDS, "&rGrass Seeds", "", "&7&oCan be planted on Dirt");
		new GrassSeeds(mainCategory, grassSeeds, new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new PlantsListener(this);
		new FoodListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		items.put("OAK_SAPLING", new ItemStack(Material.OAK_SAPLING));
		items.put("SPRUCE_SAPLING", new ItemStack(Material.SPRUCE_SAPLING));
		items.put("BIRCH_SAPLING", new ItemStack(Material.BIRCH_SAPLING));
		items.put("JUNGLE_SAPLING", new ItemStack(Material.JUNGLE_SAPLING));
		items.put("ACACIA_SAPLING", new ItemStack(Material.ACACIA_SAPLING));
		items.put("DARK_OAK_SAPLING", new ItemStack(Material.DARK_OAK_SAPLING));
		items.put("GRASS_SEEDS", grassSeeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		cfg.save();
	}

	private void registerDishes() {
		new Juice(drinksCategory, new SlimefunItemStack("LIME_SMOOTHIE", new CustomPotion("&aLime Smoothie", Color.LIME, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIME_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("TOMATO_JUICE", new CustomPotion("&4Tomato Juice", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("WINE", new CustomPotion("&cWine", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("LEMON_ICED_TEA", new CustomPotion("&eLemon Iced Tea", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("RASPBERRY_ICED_TEA", new CustomPotion("&dRaspberry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("PEACH_ICED_TEA", new CustomPotion("&dPeach Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("STRAWBERRY_ICED_TEA", new CustomPotion("&4Strawberry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("CHERRY_ICED_TEA", new CustomPotion("&cCherry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("THAI_TEA", new CustomPotion("&6Thai Tea", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&oRestores &b&o" + "7.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0=", "&rPumpkin Bread", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("MAYO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ==", "&rMayo"), RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("MUSTARD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ==", "&eMustard"), RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register();

		new EGPlant(Categories.MISC, new SlimefunItemStack("BBQ_SAUCE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0=", "&cBBQ Sauce"), RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("VEGETABLE_OIL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFjYjI4ZmI4YTMxMDQ0M2FmMDJjN2ExMjgzYWNlOTVhOTkwNmIyZTBlNmYzNjM2NTk3ZWRiZThjYWQ0ZSJ9fX0=", "&rVegetable Oil"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT_SEEDS), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("CORNMEAL", Material.SUGAR, "&rCornmeal"), RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("YEAST", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjA2YmUyZGYyMTIyMzQ0YmRhNDc5ZmVlY2UzNjVlZTBlOWQ1ZGEyNzZhZmEwZThjZThkODQ4ZjM3M2RkMTMxIn19fQ==", "&rYeast"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("MOLASSES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZDdiMTU1ZWRmNDQwY2I4N2VjOTQ0ODdjYmE2NGU4ZDEyODE3MWViMTE4N2MyNmQ1ZmZlNThiZDc5NGMifX19", "&8Molasses"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT), new ItemStack(Material.SUGAR_CANE), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("BROWN_SUGAR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTY0ZDQyNDcyNzhlMTQ5ODM3NGFhNmIwZTQ3MzY4ZmU0ZjEzOGFiYzk0ZTU4M2U4ODM5OTY1ZmJlMjQxYmUifX19", "&rBrown Sugar"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("MOLASSES"), null, null, null, null, null, null, null})
		.register();

		new SlimefunItem(Categories.MISC, new SlimefunItemStack("COUNTRY_GRAVY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjIxZmE5NDM5YmZkODM4NDQ2NDE0NmY5YzY3ZWJkNGM1ZmJmNDE5NjkyNDg5MjYyN2VhZGYzYmNlMWZmIn19fQ==", "&rCountry Gravy"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), getItem("BLACK_PEPPER"), null, null, null, null, null, null})
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_BAR", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ==", "&rChocolate Bar", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("POTATO_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&rPotato Salad", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), getItem("ONION"), new ItemStack(Material.BOWL), null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rChicken Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("FISH_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rFish Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BAGEL", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTAyZTkyZjEzZGUzYmVlNjkyMjhjMzg0NDc4ZTc2MTIzMDY4MWU1ZmNlOWJkYTE5NWRhZWFmODQ4NDEzOTMzMSJ9fX0=", "&rBagel", "", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"),
		new ItemStack[] {getItem("YEAST"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("EGG_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&rEgg Salad", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TOMATO_SOUP", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzYzNjZmMTc0MjhhNDk5MDEyNjg0NGY3NGEwMmRiZjU1MjRmMzViZTEzMjNmOGZhYjBiZjYxYTU3ZmY0MWRlMyJ9fX0=", "&4Tomato Soup", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("STRAWBERRY_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&cStrawberry Salad", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("GRAPE_SALAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZlOTJlMTFhNjdiNTY5MzU0NDZhMjE0Y2FhMzcyM2QyOWU2ZGI1NmM1NWZhOGQ0MzE3OWE4YTMxNzZjNmMxIn19fQ==", "&cGrape Salad", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_CURRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0=", "&rChicken Curry", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CILANTRO"), new ItemStack(Material.COOKED_CHICKEN), getItem("BROWN_SUGAR"), getItem("CURRY_LEAF"), getItem("VEGETABLE_OIL"), getItem("CURRY_LEAF"), getItem("ONION"), new ItemStack(Material.BOWL), getItem("GARLIC")},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("COCONUT_CHICKEN_CURRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5ZTBkZDU0ODlmMDNlZmRjODA4MzA4OGY1MjFiODI5NDZjZGVjOThmYzFjOTRjNGUwOTc5MmU0NzM1MTg0YSJ9fX0=", "&rCoconut Chicken Curry", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {getItem("COCONUT"), getItem("COCONUT"), getItem("CHICKEN_CURRY"), null, null, null, null, null, null},
		19)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BISCUIT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWYwOTQ0NTZmZDc5NGI2NTMxZmM2ZGVjNmYzOTZiNjgwYjk1MzYwMDIwNjNlMTFjZTI0ZDBhNzRiMGI3ZDg4NSJ9fX0=", "&6Biscuit", "", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null},
		4)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BISCUITS_GRAVY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhiYmI4MzVlMjJkOWVjNjJlMjI0MTFiOGUwMTUxMzhkNTU5NzI4M2FkMzZlNjE4ZmU0NGJhNWYxYTZiNjBmZCJ9fX0=", "&rBiscuits and Country Gravy", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("BISCUIT"), getItem("BISCUIT"), getItem("BISCUIT"), null, new ItemStack(Material.BOWL), null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&rCheesecake", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHERRY_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&cCherry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&9Blueberry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&6Pumpkin Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SWEETENED_PEAR_CHEESECAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&6Sweetened Pear Cheesecake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BLACKBERRY_COBBLER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0=", "&8Blackberry Cobbler", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("PAVLOVA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0=", "&rPavlova", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CORN_ON_THE_COB", Material.GOLDEN_CARROT, "&6Corn on the Cob", "", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		9)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CREAMED_CORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE3NGIzNGM1NDllZWQ4YmFmZTcyNzYxOGJhYjY4MjFhZmNiMTc4N2I1ZGVjZDFlZWNkNmMyMTNlN2U3YzZkIn19fQ==", "&rCreamed Corn", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BACON", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ==", "&rBacon", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rSandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BLT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rBLT", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("LEAFY_CHICKEN_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rLeafy Chicken Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("LEAFY_FISH_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rLeafy Fish Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("HAMBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rHamburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rCheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rBacon Cheeseburger", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("DELUXE_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rDeluxe Cheeseburger", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("GARLIC_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0=", "&rGarlic Bread", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("GARLIC_CHEESE_BREAD", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTMzZmE3ZDNlNjNiMjgwYTVkN2UyYmIwOTMzMmRmZjg2YjE3ZGVjZDJiMDllY2NkZDYyZGE1MjY1NTk3Zjc0ZCJ9fX0=", "&rGarlic Cheese Bread", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.CHEESE, getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CARROT_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19", "&rCarrot Cake", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_BURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rChickenburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_CHEESEBURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rChicken Cheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_BURGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=", "&rBacon Burger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rBacon Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0=", "&rTaco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("FISH_TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0=", "&rFish Taco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_COD), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("STREET_TACO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkN2MwYTA0ZjE0ODVjN2EzZWYyNjFhNDhlZTgzYjJmMWFhNzAxYWIxMWYzZmM5MTFlMDM2NmE5Yjk3ZSJ9fX0=", "&rStreet Taco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("CILANTRO"), getItem("ONION"), null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("JAMMY_DODGER", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQwMGRmYjNhNTdjMDY4YTBjYzdiNjI0ZDhkODg1MjA3MDQzNWQyNjM0YzBlNWRhOWNiYmFiNDYxNzRhZjBkZiJ9fX0=", "&cJammy Dodger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null},
		8)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rPancakes", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rBlueberry Pancakes", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_BERRY_PANCAKES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ0Y2E5OWUzMDhhMTg2YjMwMjgxYjIwMTdjNDQxODlhY2FmYjU5MTE1MmY4MWZlZWE5NmZlY2JlNTcifX19", "&rSweet Berry Pancakes", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("PANCAKES"), new ItemStack(Material.SWEET_BERRIES), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("FRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ==", "&rFries", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rPopcorn", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rPopcorn &7(Sweet)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SALTY_POPCORN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ==", "&rPopcorn &7(Salty)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SHEPARDS_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rShepard's Pie", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_POT_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rChicken Pot Pie", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rChocolate Cake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CREAM_COOKIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ==", "&rCream Cookie", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rBlueberry Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rPumpkin Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_CHIP_MUFFIN", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19", "&rChocolate Chip Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BOSTON_CREAM_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ==", "&rBoston Cream Pie", "", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"),
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rHot Dog", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_WRAPPED_CHEESE_FILLED_HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rBacon wrapped Cheese filled Hot Dog", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BBQ_BACON_WRAPPED_HOT_DOG", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rBBQ Bacon wrapped Hot Dog", "&7&o\"wanna talk about hot dogs?\" - @Pahimar", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19", "&rBBQ Double Bacon wrapped Hot Dog in a Tortilla with Cheese", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "10.0" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register();

		new CustomFood(drinksCategory, new SlimefunItemStack("SWEETENED_TEA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0=", "&aSweetened Tea", "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();

		new CustomFood(drinksCategory, new SlimefunItemStack("HOT_CHOCOLATE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19", "&6Hot Chocolate", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();

		new CustomFood(drinksCategory, new SlimefunItemStack("PINACOLADA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19", "&6Pinacolada", "", "&7&oRestores &b&o" + "7.0" + " &7&oHunger"),
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_STRAWBERRY", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ==", "&cChocolate Strawberry", "", "&7&oRestores &b&o" + "2.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("LEMONADE", new CustomPotion("&eLemonade", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_POTATO_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&rSweet Potato Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(foodCategory, new SlimefunItemStack("LAMINGTON", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rLamington", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("WAFFLES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0=", "&rWaffles", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CLUB_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19", "&rClub Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("BURRITO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19", "&rBurrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_BURRITO", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19", "&rChicken Burrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("GRILLED_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ==", "&rGrilled Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("LASAGNA", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0=", "&rLasagna", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("ICE_CREAM", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19", "&rIce Cream", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("PINEAPPLE_JUICE", new CustomPotion("&6Pineapple Juice", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack("PINEAPPLE_SMOOTHIE", new CustomPotion("&6Pineapple Smoothie", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rTiramisu", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_STRAWBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rTiramisu with Strawberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_RASPBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rTiramisu with Raspberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_BLACKBERRIES", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ==", "&rTiramisu with Blackberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_PEAR_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0=", "&rChocolate Pear Cake", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack("APPLE_PEAR_CAKE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", "&cApple Pear Cake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	private void registerTree(String name, String texture, String color, Color pcolor, String juice, boolean pie, Material... soil) {
		String id = name.toUpperCase(Locale.ROOT).replace(' ', '_');
		Tree tree = new Tree(id, texture, soil);
		trees.add(tree);

		SlimefunItemStack sfi = new SlimefunItemStack(id + "_SAPLING", Material.OAK_SAPLING, color + name + " Sapling");

		items.put(id + "_SAPLING", sfi);

		new SlimefunItem(mainCategory, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(mainCategory, new SlimefunItemStack(id, texture, color + name), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtained by harvesting the specific Tree")), true,
		new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
		.register();

		if (pcolor != null) {
			new Juice(drinksCategory, new SlimefunItemStack(juice.toUpperCase().replace(" ", "_"), new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
			new ItemStack[] {getItem(id), null, null, null, null, null, null, null, null})
			.register();
		}

		if (pie) {
			new CustomFood(foodCategory, new SlimefunItemStack(id + "_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", color + name + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
			new ItemStack[] {getItem(id), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		}

		if (!new File(schematicsFolder, id + "_TREE.schematic").exists()) {
			saveSchematic(id + "_TREE");
		}
	}

	private void saveSchematic(String id) {
		try (InputStream input = getClass().getResourceAsStream("/schematics/" + id + ".schematic")) {
			try (FileOutputStream output = new FileOutputStream(new File(schematicsFolder, id + ".schematic"))) {
				byte[] buffer = new byte[1024];
                int len;
                
                while ((len = input.read(buffer)) > 0) {
                	output.write(buffer, 0, len);
                }
			}
		} catch (IOException e) {
			Slimefun.getLogger().log(Level.SEVERE, "Failed to load file: \"" + id + ".schematic\"", e);
		}
	}

	private void registerBerry(String name, String color, Color pcolor, PlantType type, String texture) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		Berry berry = new Berry(upperCase, type, texture);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(upperCase + "_BUSH", Material.OAK_SAPLING, color + name + " Bush");

		items.put(upperCase + "_BUSH", sfi);

		new SlimefunItem(mainCategory, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(mainCategory, new SlimefunItemStack(upperCase, texture, color + name), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtained by harvesting the specific Bush")), true,
		new ItemStack[] {null, null, null, null, getItem(upperCase + "_BUSH"), null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_JUICE", new CustomPotion(color + name + " Juice", pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem(upperCase), null, null, null, null, null, null, null, null})
		.register();

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_SMOOTHIE", new CustomPotion(color + name + " Smoothie", pcolor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(upperCase + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_JELLY_SANDWICH", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19", color + name + " Jelly Sandwich", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(upperCase + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
		16)
		.register();

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_PIE", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ==", color + name + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem(upperCase), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
		13)
		.register();
	}

	public static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem() : null;
	}

	private void registerPlant(String name, String color, PlantType type, String texture) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		Berry berry = new Berry(enumStyle, type, texture);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(enumStyle + "_BUSH", Material.OAK_SAPLING, color + name + " Plant");

		items.put(upperCase + "_BUSH", sfi);

		new SlimefunItem(mainCategory, sfi, new RecipeType(new CustomItem(Material.GRASS, "&7Breaking Grass")),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register();

		new EGPlant(mainCategory, new SlimefunItemStack(enumStyle, texture, color + name), new RecipeType(new CustomItem(Material.OAK_LEAVES, "&7Obtained by harvesting the specific Bush")), true,
		new ItemStack[] {null, null, null, null, getItem(enumStyle + "_BUSH"), null, null, null, null})
		.register();
	}

	private void registerMagicalPlant(String name, ItemStack item, String texture, ItemStack[] recipe) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		SlimefunItemStack essence = new SlimefunItemStack(enumStyle + "_ESSENCE", Material.BLAZE_POWDER, "&rMagical Essence", "", "&7" + name);

		Berry berry = new Berry(essence, upperCase + "_ESSENCE", PlantType.ORE_PLANT, texture);
		berries.add(berry);

		new SlimefunItem(magicalCategory, new SlimefunItemStack(enumStyle + "_PLANT", Material.OAK_SAPLING, "&r" + name + " Plant"), RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();

		HandledBlock plant = new HandledBlock(magicalCategory, essence, RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});

		plant.setRecipeOutput(item.clone());
		plant.register();
	}

	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		
		if (item instanceof HandledBlock) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		
		return null;
	}

	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		
		if (item != null) {
			for (Berry berry : instance.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
						case ORE_PLANT:
						case DOUBLE_PLANT:
							Block plant = block;
							
							if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
								BlockStorage.clearBlockInfo(block.getRelative(BlockFace.UP));
								block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.getRelative(BlockFace.UP).setType(Material.AIR);
							}
							else {
								plant = block.getRelative(BlockFace.DOWN);
								BlockStorage.clearBlockInfo(block);
								block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
								block.setType(Material.AIR);
							}
							
							plant.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage._integrated_removeBlockInfo(plant.getLocation(), false);
							BlockStorage.store(plant, getItem(berry.toBush()));
							break;
						default:
							block.setType(Material.OAK_SAPLING);
							itemstack = berry.getItem();
							BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
							BlockStorage.store(block, getItem(berry.toBush()));
							break;
					}
				}
			}
		}
		
		return itemstack;
	}

	public static ExoticGarden getInstance() {
		return instance;
	}

	public File getSchematicsFolder() {
		return schematicsFolder;
	}

	public static Kitchen getKitchen() {
		return instance.kitchen;
	}

	public static List<Tree> getTrees() {
		return instance.trees;
	}

	public static List<Berry> getBerries() {
		return instance.berries;
	}

	public static Map<String, ItemStack> getItems() {
		return instance.items;
	}

}
