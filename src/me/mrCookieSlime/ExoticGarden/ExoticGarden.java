package me.mrCookieSlime.ExoticGarden;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.CSCoreLibPlugin.PluginUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.CSCoreLibPlugin.general.Player.PlayerInventory;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.CSCoreLibSetup.CSCoreLibLoader;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Lists.SlimefunItems;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.HandledBlock;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.Juice;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.BlockBreakHandler;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.handlers.ItemInteractionHandler;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class ExoticGarden extends JavaPlugin {
	
	static List<Berry> berries = new ArrayList<Berry>();
	static List<Tree> trees = new ArrayList<Tree>();
	static Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	Category category_main, category_food, category_drinks, category_magic;
	Config cfg;
	
	private static boolean skullitems;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		CSCoreLibLoader loader = new CSCoreLibLoader(this);
		if (loader.load()) {
			if (!new File("plugins/ExoticGarden").exists()) new File("plugins/ExoticGarden").mkdirs();
			PluginUtils utils = new PluginUtils(this);
			utils.setupConfig();
			cfg = utils.getConfig();
			utils.setupMetrics();
			utils.setupUpdater(88425, getFile());
			
			skullitems = cfg.getBoolean("options.item-heads");
			
			category_main = new Category(new CustomItem(getSkull(Material.NETHER_STALK, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="), "&aExotic Garden - Plants and Fruits", "", "&a> Click to open"));
			category_food = new Category(new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&aExotic Garden - Food", "", "&a> Click to open"));
			category_drinks = new Category(new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&aExotic Garden - Drinks", "", "&a> Click to open"));
			category_magic = new Category(new CustomItem(new MaterialData(Material.BLAZE_POWDER), "&5Exotic Garden - Magical Plants", "", "&a> Click to open"));
			
			new SlimefunItem(Categories.MISC, new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bIce Cube"), "ICE_CUBE", RecipeType.GRIND_STONE,
			new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(new CustomItem(getSkull(Material.ICE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTM0MGJlZjJjMmMzM2QxMTNiYWM0ZTZhMWE4NGQ1ZmZjZWNiYmZhYjZiMzJmYTdhN2Y3NjE5NTQ0MmJkMWEyIn19fQ=="), "&bIce Cube"), 4))
			.register();
			
			registerBerry("Grape", "&c", PotionType.STRENGTH, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlOTc2NDliZDk5OTk1NTQxM2ZjYmYwYjI2OWM5MWJlNDM0MmIxMGQwNzU1YmFkN2ExN2U5NWZjZWZkYWIwIn19fQ=="));
			registerBerry("Blueberry", "&9", PotionType.WATER_BREATHING, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVhNWM0YTBhMTZkYWJjOWIxZWM3MmZjODNlMjNhYzE1ZDAxOTdkZTYxYjEzOGJhYmNhN2M4YTI5YzgyMCJ9fX0="));
			registerBerry("Elderberry", "&c", PotionType.REGEN, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU0ODgzYTFlMjJjMzI0ZTc1MzE1MWUyYWM0MjRjNzRmMWNjNjQ2ZWVjOGVhMGRiMzQyMGYxZGQxZDhiIn19fQ=="));
			registerBerry("Raspberry", "&d", PotionType.REGEN, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI2MmM0NDViYzJkZDFjNWJiYzhiOTNmMjQ4MmY5ZmRiZWY0OGE3MjQ1ZTFiZGIzNjFkNGE1NjgxOTBkOWI1In19fQ=="));
			registerBerry("Blackberry", "&8", PotionType.WEAKNESS, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OWY4Yjc4YzQyZTI3MmE2NjlkNmU2ZDE5YmE4NjUxYjcxMGFiNzZmNmI0NmQ5MDlkNmEzZDQ4Mjc1NCJ9fX0="));
			registerBerry("Cranberry", "&c", PotionType.REGEN, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVmZTZjNzE4ZmJhNzE5ZmY2MjIyMzdlZDllYTY4MjdkMDkzZWZmYWI4MTRiZTIxOTJlOTY0M2UzZTNkNyJ9fX0="));
			registerBerry("Cowberry", "&c", PotionType.REGEN, PlantType.BUSH, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTA0ZTU0YmYyNTVhYjBiMWM0OThjYTNhMGNlYWU1YzdjNDVmMTg2MjNhNWEwMmY3OGE3OTEyNzAxYTMyNDkifX19"));
			registerBerry("Strawberry", "&4", PotionType.REGEN, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0="));
			
			registerPlant("Tomato", "&4", Material.APPLE, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTkxNzIyMjZkMjc2MDcwZGMyMWI3NWJhMjVjYzJhYTU2NDlkYTVjYWM3NDViYTk3NzY5NWI1OWFlYmQifX19"));
			registerPlant("Lettuce", "&2", Material.LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc3ZGQ4NDJjOTc1ZDhmYjAzYjFhZGQ2NmRiODM3N2ExOGJhOTg3MDUyMTYxZjIyNTkxZTZhNGVkZTdmNSJ9fX0="));
			registerPlant("Tea Leaf", "&a", Material.LEAVES, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTUxNGM4YjQ2MTI0N2FiMTdmZTM2MDZlNmUyZjRkMzYzZGNjYWU5ZWQ1YmVkZDAxMmI0OThkN2FlOGViMyJ9fX0="));
			registerPlant("Cabbage", "&2", Material.LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNkNmQ2NzMyMGM5MTMxYmU4NWExNjRjZDdjNWZjZjI4OGYyOGMyODE2NTQ3ZGIzMGEzMTg3NDE2YmRjNDViIn19fQ=="));
			registerPlant("Sweet Potato", "&6", Material.LEAVES, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZmNDg1NzhiNjY4NGUxNzk5NDRhYjFiYzc1ZmVjNzVmOGZkNTkyZGZiNDU2ZjZkZWY3NjU3NzEwMWE2NiJ9fX0="));
			registerPlant("Mustard Seed", "&e", Material.GOLD_NUGGET, PlantType.FRUIT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ1M2E0MjQ5NWZhMjdmYjkyNTY5OWJjM2U1ZjI5NTNjYzJkYzMxZDAyN2QxNGZjZjdiOGMyNGI0NjcxMjFmIn19fQ=="));
			
			registerPlant("Corn", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJkMzgwMmU1ZmFjMDNhZmFiNzQyYjBmM2NjYTQxYmNkNDcyM2JlZTkxMWQyM2JlMjljZmZkNWI5NjVmMSJ9fX0="));
			registerPlant("Pineapple", "&6", Material.GOLDEN_CARROT, PlantType.DOUBLE_PLANT, new PlantData("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlZGRkODJlNTc1ZGZkNWI3NTc5ZDg5ZGNkMjM1MGM5OTFmMDQ4M2E3NjQ3Y2ZmZDNkMmM1ODdmMjEifX19"));
			
			registerTree("Apple Oak", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "APPLE", "&c", null, "", true, Material.DIRT, Material.GRASS);
			registerTree("Coconut", new MaterialData(Material.INK_SACK, (byte) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyN2RlZDU3Yjk0Y2Y3MTViMDQ4ZWY1MTdhYjNmODViZWY1YTdiZTY5ZjE0YjE1NzNlMTRlN2U0MmUyZTgifX19", "COCONUT", "&6", PotionType.SPEED, "Coconut Milk", false, Material.SAND);
			registerTree("Cherry", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUyMDc2NmI4N2QyNDYzYzM0MTczZmZjZDU3OGIwZTY3ZDE2M2QzN2EyZDdjMmU3NzkxNWNkOTExNDRkNDBkMSJ9fX0=", "CHERRY", "&c", PotionType.REGEN, "Cherry Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Pomegranate", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", "POMEGRANATE", "&4", PotionType.STRENGTH, "Pomegranate Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Lemon", new MaterialData(Material.POTATO_ITEM), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU3ZmQ1NmNhMTU5Nzg3NzkzMjRkZjUxOTM1NGI2NjM5YThkOWJjMTE5MmM3YzNkZTkyNWEzMjliYWVmNmMifX19", "LEMON", "&e", PotionType.FIRE_RESISTANCE, "Lemon Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Plum", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlkNjY0MzE5ZmYzODFiNGVlNjlhNjk3NzE1Yjc2NDJiMzJkNTRkNzI2Yzg3ZjY0NDBiZjAxN2E0YmNkNyJ9fX0=", "PLUM", "&5", PotionType.STRENGTH, "Plum Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Lime", new MaterialData(Material.SLIME_BALL), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE1MTUzNDc5ZDlmMTQ2YTVlZTNjOWUyMThmNWU3ZTg0YzRmYTM3NWU0Zjg2ZDMxNzcyYmE3MWY2NDY4In19fQ==", "LIME", "&a", PotionType.JUMP, "Lime Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Orange", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==", "ORANGE", "&6", PotionType.FIRE_RESISTANCE, "Orange Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Peach", new MaterialData(Material.APPLE), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDNiYTQxZmU4Mjc1Nzg3MWU4Y2JlYzlkZWQ5YWNiZmQxOTkzMGQ5MzM0MWNmODEzOWQxZGZiZmFhM2VjMmE1In19fQ==", "PEACH", "&5", PotionType.STRENGTH, "Peach Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Pear", new MaterialData(Material.SLIME_BALL), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRlMjhkZjg0NDk2MWE4ZWNhOGVmYjc5ZWJiNGFlMTBiODM0YzY0YTY2ODE1ZThiNjQ1YWVmZjc1ODg5NjY0YiJ9fX0=", "PEAR", "&a", PotionType.JUMP, "Pear Juice", true, Material.DIRT, Material.GRASS);
			registerTree("Dragon Fruit", new MaterialData(Material.APPLE), "", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3ZDczYTkxYjUyMzkzZjJjMjdlNDUzZmI4OWFiM2Q3ODQwNTRkNDE0ZTM5MGQ1OGFiZDIyNTEyZWRkMmIifX19\\", "&d", PotionType.REGEN, "Dragon Fruit Juice", true, Material.DIRT, Material.GRASS);
			
			registerDishes();
			
			registerMagicalPlant("Coal", new ItemStack(Material.COAL, 2), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc4OGY1ZGRhZjUyYzU4NDIyODdiOTQyN2E3NGRhYzhmMDkxOWViMmZkYjFiNTEzNjVhYjI1ZWIzOTJjNDcifX19",
			new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});
			
			registerMagicalPlant("Iron", new ItemStack(Material.IRON_INGOT), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGI5N2JkZjkyYjYxOTI2ZTM5ZjVjZGRmMTJmOGY3MTMyOTI5ZGVlNTQxNzcxZTBiNTkyYzhiODJjOWFkNTJkIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});
			
			registerMagicalPlant("Gold", SlimefunItems.GOLD_4K, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTRkZjg5MjI5M2E5MjM2ZjczZjQ4ZjllZmU5NzlmZTA3ZGJkOTFmN2I1ZDIzOWU0YWNmZDM5NGY2ZWNhIn19fQ==",
			new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});
			
			registerMagicalPlant("Redstone", new ItemStack(Material.REDSTONE, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThkZWVlNTg2NmFiMTk5ZWRhMWJkZDc3MDdiZGI5ZWRkNjkzNDQ0ZjFlM2JkMzM2YmQyYzc2NzE1MWNmMiJ9fX0=",
			new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});
			
			registerMagicalPlant("Lapis", new MaterialData(Material.INK_SACK, (byte) 4).toItemStack(16), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFhMGQwZmVhMWFmYWVlMzM0Y2FiNGQyOWQ4Njk2NTJmNTU2M2M2MzUyNTNjMGNiZWQ3OTdlZDNjZjU3ZGUwIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});
			
			registerMagicalPlant("Ender", new ItemStack(Material.ENDER_PEARL, 4), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUzNWFhZGU4MTI5MmU2ZmY0Y2QzM2RjMGVhNmExMzI2ZDA0NTk3YzBlNTI5ZGVmNDE4MmIxZDE1NDhjZmUxIn19fQ==",
			new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});
			
			registerMagicalPlant("Quartz", new ItemStack(Material.QUARTZ, 8), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjZkZTU4ZDU4M2MxMDNjMWNkMzQ4MjQzODBjOGE0NzdlODk4ZmRlMmViOWE3NGU3MWYxYTk4NTA1M2I5NiJ9fX0=",
			new ItemStack[] {null, new ItemStack(Material.QUARTZ_ORE), null, new ItemStack(Material.QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.QUARTZ_ORE), null, new ItemStack(Material.QUARTZ_ORE), null});
			
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
			
			
			final ItemStack grass_seeds = new CustomItem(new MaterialData(Material.PUMPKIN_SEEDS), "&rGrass Seeds", "", "&7&oCan be planted on Dirt");
			
			final SlimefunItem crook = new SlimefunItem(Categories.TOOLS, new CustomItem(new MaterialData(Material.WOOD_HOE), "&rCrook", "", "&7+ &b25% &7Sapling Drop Rate"), "CROOK", RecipeType.ENHANCED_CRAFTING_TABLE,
			new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null});
			
			crook.register(false, new BlockBreakHandler() {
				
				@Override
				public boolean onBlockBreak(BlockBreakEvent arg0, ItemStack arg1, int arg2,List<ItemStack> arg3) {
					if (SlimefunManager.isItemSimiliar(arg1, crook.getItem(), true)) {
						PlayerInventory.damageItemInHand(arg0.getPlayer());
						if ((arg0.getBlock().getType() == Material.LEAVES || arg0.getBlock().getType() == Material.LEAVES_2) && CSCoreLib.randomizer().nextInt(100) < 25) {
							ItemStack sapling = new MaterialData(Material.SAPLING, (byte) ((arg0.getBlock().getData() % 4) + (arg0.getBlock().getType() == Material.LEAVES_2 ? 4: 0))).toItemStack(1);
							arg3.add(sapling);
						}
						return true;
					}
					return false;
				}
			});
			
			new SlimefunItem(category_main, grass_seeds, "GRASS_SEEDS", new RecipeType(new CustomItem(Material.LONG_GRASS, "&7Breaking Grass", 1)),
			new ItemStack[] {null, null, null, null, new CustomItem(Material.LONG_GRASS, 1), null, null, null, null})
			.register(false, new ItemInteractionHandler() {
				
				@Override
				public boolean onRightClick(ItemUseEvent arg0, Player arg1, ItemStack arg2) {
					if (SlimefunManager.isItemSimiliar(arg2, grass_seeds, true)) {
						if (arg0.getClickedBlock() != null && arg0.getClickedBlock().getType() == Material.DIRT) {
							PlayerInventory.consumeItemInHand(arg1);
							arg0.getClickedBlock().setType(Material.GRASS);
							arg0.getClickedBlock().getWorld().playEffect(arg0.getClickedBlock().getLocation(), Effect.STEP_SOUND, Material.GRASS);
						}
						return true;
					}
					else return false;
				}
			});
			
			new PlantsListener(this);
			new FoodListener(this);
			
			items.put("SEEDS", new ItemStack(Material.SEEDS));
			items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
			items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
			items.put("OAK_SAPLING", new ItemStack(Material.SAPLING));
			items.put("SPRUCE_SAPLING", new CustomItem(Material.SAPLING, 1));
			items.put("BIRCH_SAPLING", new CustomItem(Material.SAPLING, 2));
			items.put("JUNGLE_SAPLING", new CustomItem(Material.SAPLING, 3));
			items.put("ACACIA_SAPLING", new CustomItem(Material.SAPLING, 4));
			items.put("DARK_OAK_SAPLING", new CustomItem(Material.SAPLING, 5));
			items.put("GRASS_SEEDS", grass_seeds);
			
			Iterator<String> iterator = items.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				cfg.setDefaultValue("long-grass-drops." + key, true);
				if (!cfg.getBoolean("long-grass-drops." + key)) iterator.remove();
			}
			cfg.save();
		}
	}

	@SuppressWarnings("deprecation")
	private void registerDishes() {
		new Juice(category_drinks, new CustomPotion("&aLime Smoothie", 8203, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 10, 0)), "LIME_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LIME_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&4Tomato Juice", 8193, new String[] {"", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 6, 0)), "TOMATO_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&cWine", 8201, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 10, 0)), "WINE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&eLemon Iced Tea", 8227, new String[] {"", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 13, 0)), "LEMON_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&dRaspberry Iced Tea", 8193, new String[] {"", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 13, 0)), "RASPBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&dPeach Iced Tea", 8193, new String[] {"", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 13, 0)), "PEACH_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&4Strawberry Iced Tea", 8193, new String[] {"", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 13, 0)), "STRAWBERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&cCherry Iced Tea", 8193, new String[] {"", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 13, 0)), "CHERRY_ICED_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&6Thai Tea", 8201, new String[] {"", "&7&oRestores &b&o" + "7.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 14, 0)), "THAI_TEA", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjM0ODdkNDU3ZjkwNjJkNzg3YTNlNmNlMWM0NjY0YmY3NDAyZWM2N2RkMTExMjU2ZjE5YjM4Y2U0ZjY3MCJ9fX0="), "&rPumpkin Bread", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"), "PUMPKIN_BREAD",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register();
		
		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y4ZDUzNmM4YzJjMjU5NmJjYzE3MDk1OTBhOWQ3ZTMzMDYxYzU2ZTY1ODk3NGNkODFiYjgzMmVhNGQ4ODQyIn19fQ=="), "&rMayo"), "MAYO", RecipeType.GRIND_STONE, false,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register();
		
		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWI5ZTk5NjIxYjk3NzNiMjllMzc1ZTYyYzY0OTVmZjFhYzg0N2Y4NWIyOTgxNmMyZWI3N2I1ODc4NzRiYTYyIn19fQ=="), "&eMustard"), "MUSTARD", RecipeType.GRIND_STONE, false,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register();
		
		new EGPlant(Categories.MISC, new CustomItem(getSkull(Material.MILK_BUCKET, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg2ZjE5YmYyM2QyNDhlNjYyYzljOGI3ZmExNWVmYjhhMWYxZDViZGFjZDNiODYyNWE5YjU5ZTkzYWM4YSJ9fX0="), "&cBBQ Sauce"), "BBQ_SAUCE", RecipeType.ENHANCED_CRAFTING_TABLE, false,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register();
		
		new SlimefunItem(Categories.MISC, new CustomItem(new MaterialData(Material.SUGAR), "&rCornmeal"), "CORNMEAL", RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(new MaterialData(Material.INK_SACK, (byte) 3), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE5Zjk0OGQxNzcxOGFkYWNlNWRkNmUwNTBjNTg2MjI5NjUzZmVmNjQ1ZDcxMTNhYjk0ZDE3YjYzOWNjNDY2In19fQ=="), "&rChocolate Bar", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"), "CHOCOLATE_BAR",
		new ItemStack[] {new MaterialData(Material.INK_SACK, (byte) 3).toItemStack(1), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&rPotato Salad", 0, new String[] {"", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"}), "POTATO_SALAD",
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rChicken Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"), "CHICKEN_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rFish Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"), "FISH_SANDWICH",
		new ItemStack[] {new ItemStack(Material.COOKED_FISH), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&rEgg Salad", 0, new String[] {"", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"}), "EGG_SALAD",
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		6)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&4Tomato Soup", 0, new String[] {"", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"}), "TOMATO_SOUP",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		5)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&cStrawberry Salad", 0, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}), "STRAWBERRY_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		4)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&cGrape Salad", 0, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}), "GRAPE_SALAD",
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), null, null, null, null, null, null, null},
		4)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&rCheesecake", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), "CHEESECAKE",
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&cCherry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "CHERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&9Blueberry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "BLUEBERRY_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6Pumpkin Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "PUMPKIN_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&6Sweetened Pear Cheesecake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "SWEETENED_PEAR_CHEESECAKE",
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&6Biscuit", 0, new String[] {"", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"}), "BISCUIT",
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null}, 
		2)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZjMzY1MjNjMmQxMWI4YzhlYTJlOTkyMjkxYzUyYTY1NDc2MGVjNzJkY2MzMmRhMmNiNjM2MTY0ODFlZSJ9fX0="), "&8Blackberry Cobbler", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "BLACKBERRY_COBBLER",
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		4)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2NWI2MWU3OWZjYjkxM2JjODYwZjRlYzYzNWQ0YTZhYjFiNzRiZmFiNjJmYjZlYTZkODlhMTZhYTg0MSJ9fX0="), "&rPavlova", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "PAVLOVA",
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.GOLDEN_CARROT, "&6Corn on the Cob", 0, new String[] {"", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"}), "CORN_ON_THE_COB",
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		3)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.MUSHROOM_SOUP, "&rCreamed Corn", 0, new String[] {"", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"}), "CREAMED_CORN",
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		2)
		.register();
		new CustomFood(category_food, new CustomItem(getSkull(Material.GRILLED_PORK, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdiYTIyZDVkZjIxZTgyMWE2ZGU0YjhjOWQzNzNhM2FhMTg3ZDhhZTc0ZjI4OGE4MmQyYjYxZjI3MmU1In19fQ=="), "&rBacon", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"), "BACON",
		new ItemStack[] {new ItemStack(Material.GRILLED_PORK), null, null, null, null, null, null, null, null},
		3)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rSandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"), "SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rBLT", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "BLT",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.GRILLED_PORK), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rLeafy Chicken Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "LEAFY_CHICKEN_SANDWICH",
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rLeafy Fish Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "LEAFY_FISH_SANDWICH",
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rHamburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"), "HAMBURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rCheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "CHEESEBURGER",
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rBacon Cheeseburger", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "BACON_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rDeluxe Cheeseburger", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), "DELUXE_CHEESEBURGER",
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkxMzY1MTRmMzQyZTdjNTIwOGExNDIyNTA2YTg2NjE1OGVmODRkMmIyNDkyMjAxMzllOGJmNjAzMmUxOTMifX19"), "&rCarrot Cake", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "CARROT_CAKE",
		new ItemStack[] {new ItemStack(Material.CARROT_ITEM), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rChickenburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"), "CHICKEN_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rChicken Cheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "CHICKEN_CHEESEBURGER",
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0="), "&rBacon Burger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"), "BACON_BURGER",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rBacon Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"), "BACON_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&rTaco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThjZWQ3NGEyMjAyMWE1MzVmNmJjZTIxYzhjNjMyYjI3M2RjMmQ5NTUyYjcxYTM4ZDU3MjY5YjM1MzhjZiJ9fX0="), "&rFish Taco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "FISH_TACO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_FISH), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(Material.COOKIE, "&cJammy Dodger", 0, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}), "JAMMY_DODGER",
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null}, 
		8)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rPancakes", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "PANCAKES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rBlueberry Pancakes", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "BLUEBERRY_PANCAKES",
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register();

		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTYzYjhhZWFmMWRmMTE0ODhlZmM5YmQzMDNjMjMzYTg3Y2NiYTNiMzNmN2ZiYTljMmZlY2FlZTk1NjdmMDUzIn19fQ=="), "&rFries", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "FRIES",
		new ItemStack[] {new ItemStack(Material.POTATO_ITEM), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPopcorn", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"), "POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPopcorn &7(Sweet)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "SWEET_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.POTATO_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ5N2IxNDdjZmFlNTIyMDU1OTdmNzJlM2M0ZWY1MjUxMmU5Njc3MDIwZTRiNGZhNzUxMmMzYzZhY2RkOGMxIn19fQ=="), "&rPopcorn &7(Salty)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "SALTY_POPCORN",
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rShepard's Pie", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), "SHEPARDS_PIE",
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT_ITEM), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rChicken Pot Pie", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "CHICKEN_POT_PIE",
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT_ITEM), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO_ITEM), null, null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rChocolate Cake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "CHOCOLATE_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.COOKIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&rCream Cookie", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "CREAM_COOKIE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rBlueberry Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "BLUEBERRY_MUFFIN",
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rPumpkin Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "PUMPKIN_MUFFIN",
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM3OTRjNzM2ZmM3NmU0NTcwNjgzMDMyNWI5NTk2OTQ2NmQ4NmY4ZDdiMjhmY2U4ZWRiMmM3NWUyYWIyNWMifX19"), "&rChocolate Chip Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "CHOCOLATE_CHIP_MUFFIN",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZkNzFlMjBmYzUwYWJmMGRlMmVmN2RlY2ZjMDFjZTI3YWQ1MTk1NTc1OWUwNzJjZWFhYjk2MzU1ZjU5NGYwIn19fQ=="), "&rBoston Cream Pie", "", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"), "BOSTON_CREAM_PIE",
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rHot Dog", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"), "HOT_DOG",
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRILLED_PORK), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rBacon wrapped Cheese filled Hot Dog", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "BACON_WRAPPED_CHEESE_FILLED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rBBQ Bacon wrapped Hot Dog", "&7&o\"wanna talk about hot dogs?\" - @Pahimar", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "BBQ_BACON_WRAPPED_HOT_DOG",
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzNmMmQ3ZDdhOGIxYjk2OTE0Mjg4MWViNWE4N2U3MzdiNWY3NWZiODA4YjlhMTU3YWRkZGIyYzZhZWMzODIifX19"), "&rBBQ Double Bacon wrapped Hot Dog in a Tortilla with Cheese", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "10.0" + " &7&oHunger"), "BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE",
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.GRILLED_PORK), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register();
		
		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDhlOTRkZGQ3NjlhNWJlYTc0ODM3NmI0ZWM3MzgzZmQzNmQyNjc4OTRkN2MzYmVlMDExZThlNGY1ZmNkNyJ9fX0="), "&aSweetened Tea", "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"), "SWEETENED_TEA",
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register();
		
		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDExNTExYmRkNTViY2I4MjgwM2M4MDM5ZjFjMTU1ZmQ0MzA2MjYzNmUyM2Q0ZDQ2YzRkNzYxYzA0ZDIyYzIifX19"), "&6Hot Chocolate", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"), "HOT_CHOCOLATE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register();
		
		new CustomFood(category_drinks, new CustomItem(getSkull(Material.POTION, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmE4ZjFmNzBlODU4MjU2MDdkMjhlZGNlMWEyYWQ0NTA2ZTczMmI0YTUzNDVhNWVhNmU4MDdjNGIzMTNlODgifX19"), "&6Pinacolada", "", "&7&oRestores &b&o" + "7.0" + " &7&oHunger"), "PINACOLADA",
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.NETHER_STALK, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ0ZWQ3YzczYWMyODUzZGZjYWE5Y2E3ODlmYjE4ZGExZDQ3YjE3YWQ2OGIyZGE3NDhkYmQxMWRlMWE0OWVmIn19fQ=="), "&cChocolate Strawberry", "", "&7&oRestores &b&o" + "2.5" + " &7&oHunger"), "CHOCOLATE_STRAWBERRY",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register();
		
		new Juice(category_drinks, new CustomPotion("&eLemonade", 8227, new String[] {"", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 8, 0)), "LEMONADE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&rSweet Potato Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), "SWEET_POTATO_PIE",
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rLamington", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "LAMINGTON",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQ3ZjRmNWE3NGM2NjkxMjgwY2Q4MGU3MTQ4YjQ5YjJjZTE3ZGNmNjRmZDU1MzY4NjI3ZjVkOTJhOTc2YTZhOCJ9fX0="), "&rWaffles", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"), "WAFFLES",
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTE0MjE2ZDEwNzE0MDgyYmJlM2Y0MTI0MjNlNmIxOTIzMjM1MmY0ZDY0ZjlhY2EzOTEzY2I0NjMxOGQzZWQifX19"), "&rClub Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"), "CLUB_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&rBurrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4N2E2MjFlMjY2MTg2ZTYwNjgzMzkyZWIyNzRlYmIyMjViMDQ4NjhhYjk1OTE3N2Q5ZGMxODFkOGYyODYifX19"), "&rChicken Burrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "CHICKEN_BURRITO",
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ=="), "&rGrilled Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"), "GRILLED_SANDWICH",
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.GRILLED_PORK), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDNhMzU3NGE4NDhmMzZhZTM3MTIxZTkwNThhYTYxYzEyYTI2MWVlNWEzNzE2ZjZkODI2OWUxMWUxOWUzNyJ9fX0="), "&rLasagna", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"), "LASAGNA",
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOW_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUzNjZjYTE3OTc0ODkyZTRmZDRjN2I5YjE4ZmViMTFmMDViYTJlYzQ3YWE1MDM1YzgxYTk1MzNiMjgifX19"), "&rIce Cream", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), "ICE_CREAM",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new MaterialData(Material.INK_SACK, (byte) 3).toItemStack(1), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register();
		
		new Juice(category_drinks, new CustomPotion("&6Pineapple Juice", 8195, new String[] {"", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 6, 0)), "PINEAPPLE_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion("&6Pineapple Smoothie", 8195, new String[] {"", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"}, new PotionEffect(PotionEffectType.SATURATION, 10, 0)), "PINEAPPLE_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOW_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisu", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), "TIRAMISU",
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new MaterialData(Material.INK_SACK, (byte) 3).toItemStack(1), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOW_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisu with Strawberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "TIRAMISU_WITH_STRAWBERRIES",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOW_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisu with Raspberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "TIRAMISU_WITH_RASPBERRIES",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.SNOW_BALL, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTY5MDkxZDI4ODAyMmM3YjBlYjZkM2UzZjQ0YjBmZWE3ZjJjMDY5ZjQ5NzQ5MWExZGNhYjU4N2ViMWQ1NmQ0In19fQ=="), "&rTiramisu with Blackberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "TIRAMISU_WITH_BLACKBERRIES",
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTExOWZjYTRmMjhhNzU1ZDM3ZmJlNWRjZjZkOGMzZWY1MGZlMzk0YzFhNzg1MGJjN2UyYjcxZWU3ODMwM2M0YyJ9fX0="), "&rChocolate Pear Cake", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"), "CHOCOLATE_PEAR_CAKE",
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register();
		
		new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), "&cApple Pear Cake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"), "APPLE_PEAR_CAKE",
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register();
	}

	@Override
	public void onDisable() {
		berries = null;
		trees = null;
		items = null;
	}
	
	public void registerTree(String name, MaterialData material, String texture, String fruitName, String color, PotionType ptype, String juice, boolean pie, Material... soil) {
		String id = name.toUpperCase().replace(" ", "_");
		Tree tree = new Tree(id, fruitName, texture, soil);
		trees.add(tree);
		
		items.put(id + "_SAPLING", new CustomItem(Material.SAPLING, color + name + " Sapling", 0));
		
		new SlimefunItem(category_main, new CustomItem(Material.SAPLING, color + name + " Sapling", 0), id + "_SAPLING", new RecipeType(new CustomItem(Material.LONG_GRASS, "&7Breaking Grass", 1)),
		new ItemStack[] {null, null, null, null, new CustomItem(Material.LONG_GRASS, 1), null, null, null, null})
		.register();
		
		try {
			new EGPlant(category_main, new CustomItem(getSkull(material, texture), color + StringUtils.format(fruitName)), fruitName, new RecipeType(new CustomItem(Material.LEAVES, "&7Obtained by harvesting the specific Tree", 0)), true,
			new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
			.register();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (ptype != null) {
			new Juice(category_drinks, new CustomPotion(color + juice, ptype, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"), juice.toUpperCase().replace(" ", "_"), RecipeType.JUICER,
			new ItemStack[] {getItem(fruitName), null, null, null, null, null, null, null, null})
			.register();
		}
		if (pie) {
			try {
				new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + StringUtils.format(name) + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), name.toUpperCase() + "_PIE",
				new ItemStack[] {getItem(name.toUpperCase()), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
				13)
				.register();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		InputStream stream = Tree.class.getResourceAsStream("schematics/" + id + "_TREE.schematic");
	    OutputStream out = null;
	    int read;
	    byte[] buffer = new byte[4096];
	    try {
	        out = new FileOutputStream(new File("plugins/ExoticGarden/" + id + "_TREE.schematic"));
	        while ((read = stream.read(buffer)) > 0) {
	            out.write(buffer, 0, read);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
				stream.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
	
	public void registerBerry(String name, String color, PotionType ptype, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase(), type, data);
		berries.add(berry);
		
		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.SAPLING, color + name + " Bush", 0));
		
		new SlimefunItem(category_main, new CustomItem(Material.SAPLING, color + name + " Bush", 0), name.toUpperCase() + "_BUSH", new RecipeType(new CustomItem(Material.LONG_GRASS, "&7Breaking Grass", 1)),
		new ItemStack[] {null, null, null, null, new CustomItem(Material.LONG_GRASS, 1), null, null, null, null})
		.register();
		
		new EGPlant(category_main, new CustomItem(getSkull(Material.NETHER_STALK, data.getTexture()), color + name), name.toUpperCase(), new RecipeType(new CustomItem(Material.LEAVES, "&7Obtained by harvesting the specific Bush", 0)), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase() + "_BUSH"), null, null, null, null})
		.register();

		new Juice(category_drinks, new CustomPotion(color + name + " Juice", ptype, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"), name.toUpperCase() + "_JUICE", RecipeType.JUICER,
		new ItemStack[] {getItem(name.toUpperCase()), null, null, null, null, null, null, null, null})
		.register();
		
		new Juice(category_drinks, new CustomPotion(color + name + " Smoothie", ptype, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"), name.toUpperCase() + "_SMOOTHIE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(name.toUpperCase() + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register();
		
		try {
			new CustomFood(category_food, new CustomItem(getSkull(Material.BREAD, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4YTkzOTA5M2FiMWNkZTY2NzdmYWY3NDgxZjMxMWU1ZjE3ZjYzZDU4ODI1ZjBlMGMxNzQ2MzFmYjA0MzkifX19"), color + name + " Jelly Sandwich", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"), name.toUpperCase() + "_JELLY_SANDWICH",
			new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(name.toUpperCase() + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
			16)
			.register();
			
			new CustomFood(category_food, new CustomItem(getSkull(Material.PUMPKIN_PIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxOGM2YjBhMjlmYzFmZTc5MWM4OTc3NGQ4MjhmZjYzZDJhOWZhNmM4MzM3M2VmM2FhNDdiZjNlYjc5In19fQ=="), color + name + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"), name.toUpperCase() + "_PIE",
			new ItemStack[] {getItem(name.toUpperCase()), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem(): null;
	}
	
	private ItemStack getSkull(Material material, String texture) {
		return getSkull(new MaterialData(material), texture);
	}
	
	public static ItemStack getSkull(MaterialData material, String texture) {
		try {
			if (texture.equals("NO_SKULL_SPECIFIED")) return material.toItemStack(1);
			return skullitems ? CustomSkull.getItem(texture): material.toItemStack(1);
		} catch (Exception e) {
			e.printStackTrace();
			return material.toItemStack(1);
		}
	}

	public void registerPlant(String name, String color, Material material, PlantType type, PlantData data) {
		Berry berry = new Berry(name.toUpperCase().replace(" ", "_"), type, data);
		berries.add(berry);
		
		items.put(name.toUpperCase() + "_BUSH", new CustomItem(Material.SAPLING, color + name + " Plant", 0));
		
		new SlimefunItem(category_main, new CustomItem(Material.SAPLING, color + name + " Plant", 0), name.toUpperCase().replace(" ", "_") + "_BUSH", new RecipeType(new CustomItem(Material.LONG_GRASS, "&7Breaking Grass", 1)),
		new ItemStack[] {null, null, null, null, new CustomItem(Material.LONG_GRASS, 1), null, null, null, null})
		.register();

		new EGPlant(category_main, new CustomItem(getSkull(material, data.getTexture()), color + name), name.toUpperCase().replace(" ", "_"), new RecipeType(new CustomItem(Material.LEAVES, "&7Obtained by harvesting the specific Bush", 0)), true,
		new ItemStack[] {null, null, null, null, getItem(name.toUpperCase().replace(" ", "_") + "_BUSH"), null, null, null, null})
		.register();
	}

	public void registerMagicalPlant(String name, ItemStack item, String skull, ItemStack[] recipe) {
		ItemStack essence = new CustomItem(new MaterialData(Material.BLAZE_POWDER), "&rMagical Essence", "", "&7" + name);
		
		Berry berry = new Berry(essence, name.toUpperCase() + "_ESSENCE", PlantType.ORE_PLANT, new PlantData(skull));
		berries.add(berry);
		
		new SlimefunItem(category_magic, new CustomItem(Material.SAPLING, "&r" + name + " Plant", 0), name.toUpperCase().replace(" ", "_") + "_PLANT", RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register();
		
		HandledBlock plant = new HandledBlock(category_magic, essence, name.toUpperCase().replace(" ", "_") + "_ESSENCE", RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {essence, essence, essence, essence, null, essence, essence, essence, essence});
		
		plant.setRecipeOutput(item.clone());
		plant.register();
	}
	
	public static Berry getBerry(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item != null && item instanceof HandledBlock) {
			for (Berry berry: ExoticGarden.berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) return berry;
			}
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack harvestPlant(Block block) {
		ItemStack itemstack = null;
		SlimefunItem item = BlockStorage.check(block);
		if (item != null) {
			for (Berry berry: berries) {
				if (item.getID().equalsIgnoreCase(berry.getID())) {
					switch (berry.getType()) {
					case ORE_PLANT:
					case DOUBLE_PLANT: {
						Block plant;
						if (BlockStorage.check(block.getRelative(BlockFace.DOWN)) == null) {
							plant = block;
							BlockStorage.retrieve(block.getRelative(BlockFace.UP));
							block.getWorld().playEffect(block.getRelative(BlockFace.UP).getLocation(), Effect.STEP_SOUND, Material.LEAVES);
							block.getRelative(BlockFace.UP).setType(Material.AIR);;
						}
						else {
							plant = block.getRelative(BlockFace.DOWN);
							BlockStorage.retrieve(block);
							block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LEAVES);
							block.setType(Material.AIR);;
						}
						plant.setType(Material.SAPLING);
						plant.setData((byte) 0);
						itemstack = berry.getItem();
						BlockStorage.store(plant, getItem(berry.toBush()));
						break;
					}
					default: {
						block.setType(Material.SAPLING);
						block.setData((byte) 0);
						itemstack = berry.getItem();
						BlockStorage.store(block, getItem(berry.toBush()));
						break;
					}
					}
				}
			}
		}
		return itemstack;
	}

}
