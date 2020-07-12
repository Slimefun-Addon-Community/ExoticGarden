package io.github.thebusybiscuit.exoticgarden;

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
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.thebusybiscuit.exoticgarden.items.Crook;
import io.github.thebusybiscuit.exoticgarden.items.CustomFood;
import io.github.thebusybiscuit.exoticgarden.items.ExoticGardenFruit;
import io.github.thebusybiscuit.exoticgarden.items.GrassSeeds;
import io.github.thebusybiscuit.exoticgarden.items.Kitchen;
import io.github.thebusybiscuit.exoticgarden.items.MagicalEssence;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.Juice;
import io.papermc.lib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.bstats.bukkit.Metrics;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;

public class ExoticGarden extends JavaPlugin implements SlimefunAddon {

	public static ExoticGarden instance;

	private final File schematicsFolder = new File(getDataFolder(), "schematics");

	private final List<Berry> berries = new ArrayList<>();
	private final List<Tree> trees = new ArrayList<>();
	private final Map<String, ItemStack> items = new HashMap<>();

	protected Config cfg;

	private Category mainCategory;
    private Category miscCategory;
	private Category foodCategory;
	private Category drinksCategory;
	private Category magicalCategory;
	private Kitchen kitchen;

	@Override
	public void onEnable() {

		PaperLib.suggestPaper(this);

		if (!schematicsFolder.exists()) {
			schematicsFolder.mkdirs();
		}

    	instance = this;
    	cfg = new Config(this);

		// Setting up bStats
		new Metrics(this, 4575);
		
		// Auto Updater
		if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
		    Updater updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ExoticGarden/master");
		    updater.start();
		}
		
		SlimefunPlugin.getThirdPartySupportService().loadExoticGarden(this, b -> Optional.ofNullable(harvestPlant(b)));

		mainCategory = new Category(new NamespacedKey(this, "plants_and_fruits"), new CustomItem(SkullItem.fromHash("a5a5c4a0a16dabc9b1ec72fc83e23ac15d0197de61b138babca7c8a29c820"), "&aExotic Garden - Plants and Fruits"));
		miscCategory = new Category(new NamespacedKey(this, "misc"), new CustomItem(SkullItem.fromHash("606be2df2122344bda479feece365ee0e9d5da276afa0e8ce8d848f373dd131"), "&aExotic Garden - Ingredients and Tools"));
        foodCategory = new Category(new NamespacedKey(this, "food"), new CustomItem(SkullItem.fromHash("a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed"), "&aExotic Garden - Food"));
		drinksCategory = new Category(new NamespacedKey(this, "drinks"), new CustomItem(SkullItem.fromHash("2a8f1f70e85825607d28edce1a2ad4506e732b4a5345a5ea6e807c4b313e88"), "&aExotic Garden - Drinks"));
		magicalCategory = new Category(new NamespacedKey(this, "magical_crops"), new CustomItem(Material.BLAZE_POWDER, "&5Exotic Garden - Magical Plants"));

        kitchen = new Kitchen(this, miscCategory);
        kitchen.register(this);

		Research kitchenResearch = new Research(new NamespacedKey(this, "kitchen"), 600, "Kitchen", 30);
		kitchenResearch.addItems(kitchen);
		kitchenResearch.register();
        
		SlimefunItemStack iceCube = new SlimefunItemStack("ICE_CUBE", "9340bef2c2c33d113bac4e6a1a84d5ffcecbbfab6b32fa7a7f76195442bd1a2", "&bIce Cube");
		new SlimefunItem(miscCategory, iceCube, RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(iceCube, 4))
		.register(this);

		registerBerry("Grape", ChatColor.RED, Color.RED, PlantType.BUSH, "6ee97649bd999955413fcbf0b269c91be4342b10d0755bad7a17e95fcefdab0");
		registerBerry("Blueberry", ChatColor.BLUE, Color.BLUE, PlantType.BUSH, "a5a5c4a0a16dabc9b1ec72fc83e23ac15d0197de61b138babca7c8a29c820");
		registerBerry("Elderberry", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "1e4883a1e22c324e753151e2ac424c74f1cc646eec8ea0db3420f1dd1d8b");
		registerBerry("Raspberry", ChatColor.LIGHT_PURPLE, Color.FUCHSIA, PlantType.BUSH,"8262c445bc2dd1c5bbc8b93f2482f9fdbef48a7245e1bdb361d4a568190d9b5");
		registerBerry("Blackberry", ChatColor.DARK_GRAY, Color.GRAY, PlantType.BUSH, "2769f8b78c42e272a669d6e6d19ba8651b710ab76f6b46d909d6a3d482754");
		registerBerry("Cranberry", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "d5fe6c718fba719ff622237ed9ea6827d093effab814be2192e9643e3e3d7");
		registerBerry("Cowberry", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "a04e54bf255ab0b1c498ca3a0ceae5c7c45f18623a5a02f78a7912701a3249");
		registerBerry("Strawberry", ChatColor.DARK_RED, Color.FUCHSIA, PlantType.FRUIT, "cbc826aaafb8dbf67881e68944414f13985064a3f8f044d8edfb4443e76ba");

		registerPlant("Tomato", ChatColor.DARK_RED, PlantType.FRUIT, "99172226d276070dc21b75ba25cc2aa5649da5cac745ba977695b59aebd");
		registerPlant("Lettuce", ChatColor.DARK_GREEN, PlantType.FRUIT, "477dd842c975d8fb03b1add66db8377a18ba987052161f22591e6a4ede7f5");
		registerPlant("Tea Leaf", ChatColor.GREEN, PlantType.DOUBLE_PLANT, "1514c8b461247ab17fe3606e6e2f4d363dccae9ed5bedd012b498d7ae8eb3");
		registerPlant("Cabbage", ChatColor.DARK_GREEN, PlantType.FRUIT, "fcd6d67320c9131be85a164cd7c5fcf288f28c2816547db30a3187416bdc45b");
		registerPlant("Sweet Potato", ChatColor.GOLD, PlantType.FRUIT, "3ff48578b6684e179944ab1bc75fec75f8fd592dfb456f6def76577101a66");
		registerPlant("Mustard Seed", ChatColor.YELLOW, PlantType.FRUIT, "ed53a42495fa27fb925699bc3e5f2953cc2dc31d027d14fcf7b8c24b467121f");
		registerPlant("Curry Leaf", ChatColor.DARK_GREEN, PlantType.DOUBLE_PLANT, "32af7fa8bdf3252f69863b204559d23bfc2b93d41437103437ab1935f323a31f");
		registerPlant("Onion", ChatColor.RED, PlantType.FRUIT, "6ce036e327cb9d4d8fef36897a89624b5d9b18f705384ce0d7ed1e1fc7f56");
		registerPlant("Garlic", ChatColor.RESET, PlantType.FRUIT, "3052d9c11848ebcc9f8340332577bf1d22b643c34c6aa91fe4c16d5a73f6d8");
		registerPlant("Cilantro", ChatColor.GREEN, PlantType.DOUBLE_PLANT, "16149196f3a8d6d6f24e51b27e4cb71c6bab663449daffb7aa211bbe577242");
		registerPlant("Black Pepper", ChatColor.DARK_GRAY, PlantType.DOUBLE_PLANT, "2342b9bf9f1f6295842b0efb591697b14451f803a165ae58d0dcebd98eacc");

		registerPlant("Corn", ChatColor.GOLD,  PlantType.DOUBLE_PLANT, "9bd3802e5fac03afab742b0f3cca41bcd4723bee911d23be29cffd5b965f1");
		registerPlant("Pineapple", ChatColor.GOLD,  PlantType.DOUBLE_PLANT, "d7eddd82e575dfd5b7579d89dcd2350c991f0483a7647cffd3d2c587f21");

		registerTree("Oak Apple", "cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633", "&c", Color.FUCHSIA, "Oak Apple Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Coconut", "6d27ded57b94cf715b048ef517ab3f85bef5a7be69f14b1573e14e7e42e2e8", "&6", Color.MAROON, "Coconut Milk", false, Material.SAND);
		registerTree("Cherry", "c520766b87d2463c34173ffcd578b0e67d163d37a2d7c2e77915cd91144d40d1", "&c", Color.FUCHSIA, "Cherry Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pomegranate", "cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633", "&4", Color.RED, "Pomegranate Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lemon", "957fd56ca15978779324df519354b6639a8d9bc1192c7c3de925a329baef6c", "&e", Color.YELLOW, "Lemon Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Plum", "69d664319ff381b4ee69a697715b7642b32d54d726c87f6440bf017a4bcd7", "&5", Color.RED, "Plum Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lime", "5a5153479d9f146a5ee3c9e218f5e7e84c4fa375e4f86d31772ba71f6468", "&a", Color.LIME, "Lime Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Orange", "65b1db547d1b7956d4511accb1533e21756d7cbc38eb64355a2626412212", "&6", Color.ORANGE, "Orange Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Peach", "d3ba41fe82757871e8cbec9ded9acbfd19930d93341cf8139d1dfbfaa3ec2a5", "&5", Color.RED, "Peach Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pear", "2de28df844961a8eca8efb79ebb4ae10b834c64a66815e8b645aeff75889664b", "&a", Color.LIME, "Pear Juice", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Dragon Fruit", "847d73a91b52393f2c27e453fb89ab3d784054d414e390d58abd22512edd2b", "&d", Color.FUCHSIA, "Dragon Fruit Juice", true, Material.DIRT, Material.GRASS_BLOCK);

		registerDishes();

		registerMagicalPlant("Coal", new ItemStack(Material.COAL, 2), "7788f5ddaf52c5842287b9427a74dac8f0919eb2fdb1b51365ab25eb392c47",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("Iron", new ItemStack(Material.IRON_INGOT), "db97bdf92b61926e39f5cddf12f8f7132929dee541771e0b592c8b82c9ad52d",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("Gold", SlimefunItems.GOLD_4K, "e4df892293a9236f73f48f9efe979fe07dbd91f7b5d239e4acfd394f6eca",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});

		registerMagicalPlant("Copper", new CustomItem(SlimefunItems.COPPER_DUST, 8), "d4fc72f3d5ee66279a45ac9c63ac98969306227c3f4862e9c7c2a4583c097b8a",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("Aluminum", new CustomItem(SlimefunItems.ALUMINUM_DUST, 8), "f4455341eaff3cf8fe6e46bdfed8f501b461fb6f6d2fe536be7d2bd90d2088aa",
		new ItemStack[] {null, SlimefunItems.ALUMINUM_DUST, null, SlimefunItems.ALUMINUM_DUST, getItem("IRON_PLANT"), SlimefunItems.ALUMINUM_DUST, null, SlimefunItems.ALUMINUM_DUST, null});

		registerMagicalPlant("Tin", new CustomItem(SlimefunItems.TIN_DUST, 8), "6efb43ba2fe6959180ee7307f3f054715a34c0a07079ab73712547ffd753dedd",
		new ItemStack[] {null, SlimefunItems.TIN_DUST, null, SlimefunItems.TIN_DUST, getItem("IRON_PLANT"), SlimefunItems.TIN_DUST, null, SlimefunItems.TIN_DUST, null});
		
		registerMagicalPlant("Silver", new CustomItem(SlimefunItems.SILVER_DUST, 8), "1dd968b1851aa7160d1cd9db7516a8e1bf7b7405e5245c5338aa895fe585f26c",
		new ItemStack[] {null, SlimefunItems.SILVER_DUST, null, SlimefunItems.SILVER_DUST, getItem("IRON_PLANT"), SlimefunItems.SILVER_DUST, null, SlimefunItems.SILVER_DUST, null});
		
		registerMagicalPlant("Lead", new CustomItem(SlimefunItems.LEAD_DUST, 8), "93c3c418039c4b28b0da75a6d9b22712c7015432d4f4226d6cc0a77d54b64178",
		new ItemStack[] {null, SlimefunItems.LEAD_DUST, null, SlimefunItems.LEAD_DUST, getItem("IRON_PLANT"), SlimefunItems.LEAD_DUST, null, SlimefunItems.LEAD_DUST, null});
		
		registerMagicalPlant("Redstone", new ItemStack(Material.REDSTONE, 8), "e8deee5866ab199eda1bdd7707bdb9edd693444f1e3bd336bd2c767151cf2",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("Lapis", new ItemStack(Material.LAPIS_LAZULI, 16), "2aa0d0fea1afaee334cab4d29d869652f5563c635253c0cbed797ed3cf57de0",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("Ender", new ItemStack(Material.ENDER_PEARL, 4), "4e35aade81292e6ff4cd33dc0ea6a1326d04597c0e529def4182b1d1548cfe1",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("Quartz", new ItemStack(Material.QUARTZ, 8), "26de58d583c103c1cd34824380c8a477e898fde2eb9a74e71f1a985053b96",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("Diamond", new ItemStack(Material.DIAMOND), "f88cd6dd50359c7d5898c7c7e3e260bfcd3dcb1493a89b9e88e9cbecbfe45949",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("Emerald", new ItemStack(Material.EMERALD), "4fc495d1e6eb54a386068c6cb121c5875e031b7f61d7236d5f24b77db7da7f",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("Glowstone", new ItemStack(Material.GLOWSTONE_DUST, 8), "65d7bed8df714cea063e457ba5e87931141de293dd1d9b9146b0f5ab383866",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("Obsidian", new ItemStack(Material.OBSIDIAN, 2), "7840b87d52271d2a755dedc82877e0ed3df67dcc42ea479ec146176b02779a5",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("Slime", new ItemStack(Material.SLIME_BALL, 8), "90e65e6e5113a5187dad46dfad3d3bf85e8ef807f82aac228a59c4a95d6f6a",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		new Crook(miscCategory, new SlimefunItemStack("CROOK", new CustomItem(Material.WOODEN_HOE, "&rCrook", "", "&7+ &b25% &7Sapling Drop Rate")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
		.register(this);

		SlimefunItemStack grassSeeds = new SlimefunItemStack("GRASS_SEEDS", Material.PUMPKIN_SEEDS, "&rGrass Seeds", "", "&7&oCan be planted on Dirt");
		new GrassSeeds(mainCategory, grassSeeds, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new PlantsListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		
		for (Material sapling : Tag.SAPLINGS.getValues()) {
		    items.put(sapling.name(), new ItemStack(sapling));
		}
		
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
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("TOMATO_JUICE", new CustomPotion("&4Tomato Juice", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem("TOMATO"), null, null, null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("WINE", new CustomPotion("&cWine", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("GRAPE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("LEMON_ICED_TEA", new CustomPotion("&eLemon Iced Tea", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("RASPBERRY_ICED_TEA", new CustomPotion("&dRaspberry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("RASPBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("PEACH_ICED_TEA", new CustomPotion("&dPeach Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PEACH"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("STRAWBERRY_ICED_TEA", new CustomPotion("&4Strawberry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("STRAWBERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("CHERRY_ICED_TEA", new CustomPotion("&cCherry Iced Tea", Color.FUCHSIA, new PotionEffect(PotionEffectType.SATURATION, 13, 0), "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("CHERRY"), getItem("ICE_CUBE"), getItem("TEA_LEAF"), null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("THAI_TEA", new CustomPotion("&6Thai Tea", Color.RED, new PotionEffect(PotionEffectType.SATURATION, 14, 0), "", "&7&oRestores &b&o" + "7.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), SlimefunItems.HEAVY_CREAM, getItem("COCONUT_MILK"), null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_BREAD", "f3487d457f9062d787a3e6ce1c4664bf7402ec67dd111256f19b38ce4f670", "&rPumpkin Bread", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		8)
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("MAYO", "7f8d536c8c2c2596bcc1709590a9d7e33061c56e658974cd81bb832ea4d8842", "&rMayo"), RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.EGG), null, null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("MUSTARD", "9b9e99621b9773b29e375e62c6495ff1ac847f85b29816c2eb77b587874ba62", "&eMustard"), RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("MUSTARD_SEED"), null, null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("BBQ_SAUCE", "a86f19bf23d248e662c9c8b7fa15efb8a1f1d5bdacd3b8625a9b59e93ac8a", "&cBBQ Sauce"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("TOMATO"), getItem("MUSTARD"), getItem("SALT"), new ItemStack(Material.SUGAR), null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("VEGETABLE_OIL", "2acb28fb8a310443af02c7a1283ace95a9906b2e0e6f3636597edbe8cad4e", "&rVegetable Oil"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT_SEEDS), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("CORNMEAL", Material.SUGAR, "&rCornmeal"), RecipeType.GRIND_STONE,
		new ItemStack[] {getItem("CORN"), null, null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("YEAST", "606be2df2122344bda479feece365ee0e9d5da276afa0e8ce8d848f373dd131", "&rYeast"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("MOLASSES", "f21d7b155edf440cb87ec94487cba64e8d128171eb1187c26d5ffe58bd794c", "&8Molasses"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.BEETROOT), new ItemStack(Material.SUGAR_CANE), new ItemStack(Material.WATER_BUCKET), null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("BROWN_SUGAR", "964d4247278e1498374aa6b0e47368fe4f138abc94e583e8839965fbe241be", "&rBrown Sugar"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("MOLASSES"), null, null, null, null, null, null, null})
		.register(this);

		new SlimefunItem(miscCategory, new SlimefunItemStack("COUNTRY_GRAVY", "f21fa9439bfd8384464146f9c67ebd4c5fbf4196924892627eadf3bce1ff", "&rCountry Gravy"), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), getItem("BLACK_PEPPER"), null, null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_BAR", "819f948d17718adace5dd6e050c586229653fef645d7113ab94d17b639cc466", "&rChocolate Bar", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COCOA_BEANS), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		3)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("POTATO_SALAD", "1fe92e11a67b56935446a214caa3723d29e6db56c55fa8d43179a8a3176c6c1", "&rPotato Salad", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BAKED_POTATO), getItem("MAYO"), getItem("ONION"), new ItemStack(Material.BOWL), null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rChicken Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("FISH_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rFish Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_COD), getItem("MAYO"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		11)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BAGEL", "502e92f13de3bee69228c384478e761230681e5fce9bda195daeaf8484139331", "&rBagel", "", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"),
		new ItemStack[] {getItem("YEAST"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null, null},
		4)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("EGG_SALAD", "1fe92e11a67b56935446a214caa3723d29e6db56c55fa8d43179a8a3176c6c1", "&rEgg Salad", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.EGG), getItem("MAYO"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TOMATO_SOUP", "76366f17428a4990126844f74a02dbf5524f35be1323f8fab0bf61a57ff41de3", "&4Tomato Soup", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("TOMATO"), null, null, null, null, null, null, null},
		11)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("STRAWBERRY_SALAD", "1fe92e11a67b56935446a214caa3723d29e6db56c55fa8d43179a8a3176c6c1", "&cStrawberry Salad", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("STRAWBERRY"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("GRAPE_SALAD", "1fe92e11a67b56935446a214caa3723d29e6db56c55fa8d43179a8a3176c6c1", "&cGrape Salad", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BOWL), getItem("GRAPE"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_CURRY", "d09e0dd5489f03efdc8083088f521b82946cdec98fc1c94c4e09792e4735184a", "&rChicken Curry", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CILANTRO"), new ItemStack(Material.COOKED_CHICKEN), getItem("BROWN_SUGAR"), getItem("CURRY_LEAF"), getItem("VEGETABLE_OIL"), getItem("CURRY_LEAF"), getItem("ONION"), new ItemStack(Material.BOWL), getItem("GARLIC")},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("COCONUT_CHICKEN_CURRY", "d09e0dd5489f03efdc8083088f521b82946cdec98fc1c94c4e09792e4735184a", "&rCoconut Chicken Curry", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {getItem("COCONUT"), getItem("COCONUT"), getItem("CHICKEN_CURRY"), null, null, null, null, null, null},
		19)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BISCUIT", "ef094456fd794b6531fc6dec6f396b680b9536002063e11ce24d0a74b0b7d885", "&6Biscuit", "", "&7&oRestores &b&o" + "2.0" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, null, null, null, null, null, null, null},
		4)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BISCUITS_GRAVY", "28bbb835e22d9ec62e22411b8e015138d5597283ad36e618fe44ba5f1a6b60fd", "&rBiscuits and Country Gravy", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("COUNTRY_GRAVY"), getItem("BISCUIT"), getItem("BISCUIT"), getItem("BISCUIT"), null, new ItemStack(Material.BOWL), null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHEESECAKE", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&rCheesecake", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null, null, null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHERRY_CHEESECAKE", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&cCherry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("CHERRY"), null, null, null, null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_CHEESECAKE", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&9Blueberry Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_CHEESECAKE", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&6Pumpkin Cheesecake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.PUMPKIN), null, null, null, null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SWEETENED_PEAR_CHEESECAKE", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&6Sweetened Pear Cheesecake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESECAKE"), new ItemStack(Material.SUGAR), getItem("PEAR"), null, null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BLACKBERRY_COBBLER", "c6c36523c2d11b8c8ea2e992291c52a654760ec72dcc32da2cb63616481ee", "&8Blackberry Cobbler", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.SUGAR), getItem("BLACKBERRY"), SlimefunItems.WHEAT_FLOUR, null, null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("PAVLOVA", "6365b61e79fcb913bc860f4ec635d4a6ab1b74bfab62fb6ea6d89a16aa841", "&rPavlova", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("LEMON"), getItem("STRAWBERRY"), new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CORN_ON_THE_COB", Material.GOLDEN_CARROT, "&6Corn on the Cob", "", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.BUTTER, getItem("CORN"), null, null, null, null, null, null, null},
		9)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CREAMED_CORN", "9174b34c549eed8bafe727618bab6821afcb1787b5decd1eecd6c213e7e7c6d", "&rCreamed Corn", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.HEAVY_CREAM, getItem("CORN"), new ItemStack(Material.BOWL), null, null, null, null, null, null},
		8)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BACON", "e7ba22d5df21e821a6de4b8c9d373a3aa187d8ae74f288a82d2b61f272e5", "&rBacon", "", "&7&oRestores &b&o" + "1.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_PORKCHOP), null, null, null, null, null, null, null, null},
		3)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rSandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BLT", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rBLT", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("LEAFY_CHICKEN_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rLeafy Chicken Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHICKEN_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		1)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("LEAFY_FISH_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rLeafy Fish Sandwich", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("FISH_SANDWICH"), getItem("LETTUCE"), null, null, null, null, null, null, null},
		11)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("HAMBURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rHamburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_BEEF), null, null, null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHEESEBURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rCheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("HAMBURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_CHEESEBURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rBacon Cheeseburger", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("BACON"), null, null, null, null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("DELUXE_CHEESEBURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rDeluxe Cheeseburger", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHEESEBURGER"), getItem("LETTUCE"), getItem("TOMATO"), null, null, null, null, null, null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("GARLIC_BREAD", "a33fa7d3e63b280a5d7e2bb09332dff86b17decd2b09eccdd62da5265597f74d", "&rGarlic Bread", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("GARLIC_CHEESE_BREAD", "a33fa7d3e63b280a5d7e2bb09332dff86b17decd2b09eccdd62da5265597f74d", "&rGarlic Cheese Bread", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {SlimefunItems.CHEESE, getItem("GARLIC"), new ItemStack(Material.BREAD), null, null, null, null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CARROT_CAKE", "f9136514f342e7c5208a1422506a866158ef84d2b249220139e8bf6032e193", "&rCarrot Cake", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.SUGAR), new ItemStack(Material.EGG), null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_BURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rChickenburger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_CHICKEN), null, null, null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_CHEESEBURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rChicken Cheeseburger", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHICKEN_BURGER"), SlimefunItems.CHEESE, null, null, null, null, null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_BURGER", "cdadf1744433e1c79d1d59d2777d939de159a24cf57e8a61c82bc4fe3777553c", "&rBacon Burger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), null, null, null, null, null, null, null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rBacon Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("BACON"), getItem("MAYO"), getItem("TOMATO"), getItem("LETTUCE"), null, null, null, null},
		19)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TACO", "98ced74a22021a535f6bce21c8c632b273dc2d9552b71a38d57269b3538cf", "&rTaco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("FISH_TACO", "98ced74a22021a535f6bce21c8c632b273dc2d9552b71a38d57269b3538cf", "&rFish Taco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_COD), getItem("LETTUCE"), getItem("TOMATO"), getItem("CHEESE"), null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("STREET_TACO", "1ad7c0a04f1485c7a3ef261a48ee83b2f1aa701ab11f3fc911e0366a9b97e", "&rStreet Taco", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("CILANTRO"), getItem("ONION"), null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("JAMMY_DODGER", "1d00dfb3a57c068a0cc7b624d8d8852070435d2634c0e5da9cbbab46174af0df", "&cJammy Dodger", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {null, getItem("BISCUIT"), null, null, getItem("RASPBERRY_JUICE"), null, null, getItem("BISCUIT"), null},
		8)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("PANCAKES", "347f4f5a74c6691280cd80e7148b49b2ce17dcf64fd55368627f5d92a976a6a8", "&rPancakes", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.SUGAR), getItem("BUTTER"), new ItemStack(Material.EGG), new ItemStack(Material.EGG), null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_PANCAKES", "347f4f5a74c6691280cd80e7148b49b2ce17dcf64fd55368627f5d92a976a6a8", "&rBlueberry Pancakes", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("PANCAKES"), getItem("BLUEBERRY"), null, null, null, null, null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_BERRY_PANCAKES", "e44ca99e308a186b30281b2017c44189acafb591152f81feea96fecbe57", "&rSweet Berry Pancakes", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("PANCAKES"), new ItemStack(Material.SWEET_BERRIES), null, null, null, null, null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("FRIES", "563b8aeaf1df11488efc9bd303c233a87ccba3b33f7fba9c2fecaee9567f053", "&rFries", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.POTATO), getItem("SALT"), null, null, null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("POPCORN", "1497b147cfae52205597f72e3c4ef52512e9677020e4b4fa7512c3c6acdd8c1", "&rPopcorn", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), null, null, null, null, null, null, null},
		8)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_POPCORN", "1497b147cfae52205597f72e3c4ef52512e9677020e4b4fa7512c3c6acdd8c1", "&rPopcorn &7(Sweet)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), new ItemStack(Material.SUGAR), null, null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SALTY_POPCORN", "1497b147cfae52205597f72e3c4ef52512e9677020e4b4fa7512c3c6acdd8c1", "&rPopcorn &7(Salty)", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORN"), getItem("BUTTER"), getItem("SALT"), null, null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SHEPARDS_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", "&rShepard's Pie", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CABBAGE"), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.COOKED_BEEF), getItem("TOMATO"), null, null, null, null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_POT_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", "&rChicken Pot Pie", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.COOKED_CHICKEN), new ItemStack(Material.CARROT), SlimefunItems.WHEAT_FLOUR, new ItemStack(Material.POTATO), null, null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_CAKE", "9119fca4f28a755d37fbe5dcf6d8c3ef50fe394c1a7850bc7e2b71ee78303c4c", "&rChocolate Cake", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, new ItemStack(Material.EGG), null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CREAM_COOKIE", "dfd71e20fc50abf0de2ef7decfc01ce27ad51955759e072ceaab96355f594f0", "&rCream Cookie", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BLUEBERRY_MUFFIN", "83794c736fc76e45706830325b95969466d86f8d7b28fce8edb2c75e2ab25c", "&rBlueberry Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BLUEBERRY"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("PUMPKIN_MUFFIN", "83794c736fc76e45706830325b95969466d86f8d7b28fce8edb2c75e2ab25c", "&rPumpkin Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.PUMPKIN), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_CHIP_MUFFIN", "83794c736fc76e45706830325b95969466d86f8d7b28fce8edb2c75e2ab25c", "&rChocolate Chip Muffin", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, SlimefunItems.HEAVY_CREAM, new ItemStack(Material.EGG), null, null, null},
		13)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BOSTON_CREAM_PIE", "dfd71e20fc50abf0de2ef7decfc01ce27ad51955759e072ceaab96355f594f0", "&rBoston Cream Pie", "", "&7&oRestores &b&o" + "4.5" + " &7&oHunger"),
		new ItemStack[] {null, getItem("CHOCOLATE_BAR"), null, null, SlimefunItems.HEAVY_CREAM, null, null, getItem("BISCUIT"), null},
		9)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("HOT_DOG", "33f2d7d7a8b1b969142881eb5a87e737b5f75fb808b9a157adddb2c6aec382", "&rHot Dog", "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger"),
		new ItemStack[] {null, null, null, null, new ItemStack(Material.COOKED_PORKCHOP), null, null, new ItemStack(Material.BREAD), null},
		10)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BACON_WRAPPED_CHEESE_FILLED_HOT_DOG", "33f2d7d7a8b1b969142881eb5a87e737b5f75fb808b9a157adddb2c6aec382", "&rBacon wrapped Cheese filled Hot Dog", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("CHEESE"), null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BBQ_BACON_WRAPPED_HOT_DOG", "33f2d7d7a8b1b969142881eb5a87e737b5f75fb808b9a157adddb2c6aec382", "&rBBQ Bacon wrapped Hot Dog", "&7&o\"wanna talk about hot dogs?\" - @Pahimar", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("HOT_DOG"), getItem("BACON"), null, getItem("BBQ_SAUCE"), null, null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BBQ_DOUBLE_BACON_WRAPPED_HOT_DOG_IN_A_TORTILLA_WITH_CHEESE", "33f2d7d7a8b1b969142881eb5a87e737b5f75fb808b9a157adddb2c6aec382", "&rBBQ Double Bacon wrapped Hot Dog in a Tortilla with Cheese", "&7&o\"When I chef\" - @Eyamaz", "", "&7&oRestores &b&o" + "10.0" + " &7&oHunger"),
		new ItemStack[] {getItem("BACON"), getItem("BBQ_SAUCE"), getItem("BACON"), getItem("BACON"), new ItemStack(Material.COOKED_PORKCHOP), getItem("BACON"), getItem("CORNMEAL"), getItem("CHEESE"), getItem("CORNMEAL")},
		20)
		.register(this);

		new CustomFood(drinksCategory, new SlimefunItemStack("SWEETENED_TEA", "d8e94ddd769a5bea748376b4ec7383fd36d267894d7c3bee011e8e4f5fcd7", "&aSweetened Tea", "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TEA_LEAF"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null},
		6)
		.register(this);

		new CustomFood(drinksCategory, new SlimefunItemStack("HOT_CHOCOLATE", "411511bdd55bcb82803c8039f1c155fd43062636e23d4d46c4d761c04d22c2", "&6Hot Chocolate", "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), SlimefunItems.HEAVY_CREAM, null, null, null, null, null, null, null},
		8)
		.register(this);

		new CustomFood(drinksCategory, new SlimefunItemStack("PINACOLADA", "2a8f1f70e85825607d28edce1a2ad4506e732b4a5345a5ea6e807c4b313e88", "&6Pinacolada", "", "&7&oRestores &b&o" + "7.0" + " &7&oHunger"),
		new ItemStack[] {getItem("PINEAPPLE"), getItem("ICE_CUBE"), getItem("COCONUT_MILK"), null, null, null, null, null, null},
		14)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_STRAWBERRY", "6d4ed7c73ac2853dfcaa9ca789fb18da1d47b17ad68b2da748dbd11de1a49ef", "&cChocolate Strawberry", "", "&7&oRestores &b&o" + "2.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		5)
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("LEMONADE", new CustomPotion("&eLemonade", Color.YELLOW, new PotionEffect(PotionEffectType.SATURATION, 8, 0), "", "&7&oRestores &b&o" + "4.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("LEMON_JUICE"), new ItemStack(Material.SUGAR), null, null, null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("SWEET_POTATO_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", "&rSweet Potato Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem("SWEET_POTATO"), new ItemStack(Material.EGG), SlimefunItems.HEAVY_CREAM, SlimefunItems.WHEAT_FLOUR, null, null, null, null, null},
		13);

		new CustomFood(foodCategory, new SlimefunItemStack("LAMINGTON", "9119fca4f28a755d37fbe5dcf6d8c3ef50fe394c1a7850bc7e2b71ee78303c4c", "&rLamington", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("COCONUT"), null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("WAFFLES", "347f4f5a74c6691280cd80e7148b49b2ce17dcf64fd55368627f5d92a976a6a8", "&rWaffles", "", "&7&oRestores &b&o" + "6.0" + " &7&oHunger"),
		new ItemStack[] {getItem("WHEAT_FLOUR"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), getItem("BUTTER"), null, null, null, null, null},
		12)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CLUB_SANDWICH", "a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed", "&rClub Sandwich", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), getItem("MAYO"), getItem("BACON"), getItem("TOMATO"), getItem("LETTUCE"), getItem("MUSTARD"), null, null, null},
		19)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("BURRITO", "a387a621e266186e60683392eb274ebb225b04868ab959177d9dc181d8f286", "&rBurrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_BEEF), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHICKEN_BURRITO", "a387a621e266186e60683392eb274ebb225b04868ab959177d9dc181d8f286", "&rChicken Burrito", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("CORNMEAL"), new ItemStack(Material.COOKED_CHICKEN), getItem("LETTUCE"), getItem("TOMATO"), getItem("HEAVY_CREAM"), getItem("CHEESE"), null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("GRILLED_SANDWICH", "baee84d19c85aff796c88abda21ec4c92c655e2d67b72e5e77b5aa5e99ed", "&rGrilled Sandwich", "", "&7&oRestores &b&o" + "5.5" + " &7&oHunger"),
		new ItemStack[] {new ItemStack(Material.BREAD), new ItemStack(Material.COOKED_PORKCHOP), getItem("CHEESE"), null, null, null, null, null, null},
		11)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("LASAGNA", "03a3574a848f36ae37121e9058aa61c12a261ee5a3716f6d8269e11e19e37", "&rLasagna", "", "&7&oRestores &b&o" + "8.5" + " &7&oHunger"),
		new ItemStack[] {getItem("TOMATO"), getItem("CHEESE"), SlimefunItems.WHEAT_FLOUR, getItem("TOMATO"), getItem("CHEESE"), new ItemStack(Material.COOKED_BEEF), null, null, null},
		17)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("ICE_CREAM", "95366ca17974892e4fd4c7b9b18feb11f05ba2ec47aa5035c81a9533b28", "&rIce Cream", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("HEAVY_CREAM"), getItem("ICE_CUBE"), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), getItem("STRAWBERRY"), null, null, null, null},
		16)
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("PINEAPPLE_JUICE", new CustomPotion("&6Pineapple Juice", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem("PINEAPPLE"), null, null, null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack("PINEAPPLE_SMOOTHIE", new CustomPotion("&6Pineapple Smoothie", Color.ORANGE, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem("PINEAPPLE_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU", "169091d288022c7b0eb6d3e3f44b0fea7f2c069f497491a1dcab587eb1d56d4", "&rTiramisu", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {getItem("HEAVY_CREAM"), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.COCOA_BEANS), new ItemStack(Material.EGG), null, null, null, null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_STRAWBERRIES", "169091d288022c7b0eb6d3e3f44b0fea7f2c069f497491a1dcab587eb1d56d4", "&rTiramisu with Strawberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("STRAWBERRY"), null, null, null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_RASPBERRIES", "169091d288022c7b0eb6d3e3f44b0fea7f2c069f497491a1dcab587eb1d56d4", "&rTiramisu with Raspberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("RASPBERRY"), null, null, null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("TIRAMISU_WITH_BLACKBERRIES", "169091d288022c7b0eb6d3e3f44b0fea7f2c069f497491a1dcab587eb1d56d4", "&rTiramisu with Blackberries", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("TIRAMISU"), getItem("BLACKBERRY"), null, null, null, null, null, null, null},
		18)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("CHOCOLATE_PEAR_CAKE", "9119fca4f28a755d37fbe5dcf6d8c3ef50fe394c1a7850bc7e2b71ee78303c4c", "&rChocolate Pear Cake", "", "&7&oRestores &b&o" + "9.5" + " &7&oHunger"),
		new ItemStack[] {getItem("CHOCOLATE_BAR"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		19)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack("APPLE_PEAR_CAKE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", "&cApple Pear Cake", "", "&7&oRestores &b&o" + "9.0" + " &7&oHunger"),
		new ItemStack[] {getItem("APPLE"), new ItemStack(Material.SUGAR), SlimefunItems.WHEAT_FLOUR, SlimefunItems.BUTTER, getItem("PEAR"), new ItemStack(Material.EGG), null, null, null},
		18)
		.register(this);
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	private void registerTree(String name, String texture, String color, Color pcolor, String juice, boolean pie, Material... soil) {
		String id = name.toUpperCase(Locale.ROOT).replace(' ', '_');
		Tree tree = new Tree(id, texture, soil);
		trees.add(tree);

		SlimefunItemStack sapling = new SlimefunItemStack(id + "_SAPLING", Material.OAK_SAPLING, color + name + " Sapling");

		items.put(id + "_SAPLING", sapling);

		new SlimefunItem(mainCategory, sapling, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(id, texture, color + name), ExoticGardenRecipeTypes.HARVEST_TREE, true,
		new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
		.register(this);

		if (pcolor != null) {
			new Juice(drinksCategory, new SlimefunItemStack(juice.toUpperCase().replace(" ", "_"), new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
			new ItemStack[] {getItem(id), null, null, null, null, null, null, null, null})
			.register(this);
		}

		if (pie) {
			new CustomFood(foodCategory, new SlimefunItemStack(id + "_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", color + name + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
			new ItemStack[] {getItem(id), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register(this);
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

	public void registerBerry(String name, ChatColor color, Color potionColor, PlantType type, String texture) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		Berry berry = new Berry(upperCase, type, texture);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(upperCase + "_BUSH", Material.OAK_SAPLING, color + name + " Bush");

		items.put(upperCase + "_BUSH", sfi);

		new SlimefunItem(mainCategory, sfi, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(upperCase, texture, color + name), ExoticGardenRecipeTypes.HARVEST_BUSH, true,
		new ItemStack[] {null, null, null, null, getItem(upperCase + "_BUSH"), null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_JUICE", new CustomPotion(color + name + " Juice", potionColor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&oRestores &b&o" + "3.0" + " &7&oHunger")), RecipeType.JUICER,
		new ItemStack[] {getItem(upperCase), null, null, null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_SMOOTHIE", new CustomPotion(color + name + " Smoothie", potionColor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&oRestores &b&o" + "5.0" + " &7&oHunger")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(upperCase + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_JELLY_SANDWICH", "8c8a939093ab1cde6677faf7481f311e5f17f63d58825f0e0c174631fb0439", color + name + " Jelly Sandwich", "", "&7&oRestores &b&o" + "8.0" + " &7&oHunger"),
		new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(upperCase + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", color + name + " Pie", "", "&7&oRestores &b&o" + "6.5" + " &7&oHunger"),
		new ItemStack[] {getItem(upperCase), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
		13)
		.register(this);
	}

	public static ItemStack getItem(String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem() : null;
	}

	public void registerPlant(String name, ChatColor color, PlantType type, String texture) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		Berry berry = new Berry(enumStyle, type, texture);
		berries.add(berry);

		SlimefunItemStack bush = new SlimefunItemStack(enumStyle + "_BUSH", Material.OAK_SAPLING, color + name + " Plant");
		items.put(upperCase + "_BUSH", bush);

		new SlimefunItem(mainCategory, bush, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(enumStyle, texture, color + name), ExoticGardenRecipeTypes.HARVEST_BUSH, true,
		new ItemStack[] {null, null, null, null, getItem(enumStyle + "_BUSH"), null, null, null, null})
		.register(this);
	}

	private void registerMagicalPlant(String name, ItemStack item, String texture, ItemStack[] recipe) {
		String upperCase = name.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		SlimefunItemStack essence = new SlimefunItemStack(enumStyle + "_ESSENCE", Material.BLAZE_POWDER, "&rMagical Essence", "", "&7" + name);

		Berry berry = new Berry(essence, upperCase + "_ESSENCE", PlantType.ORE_PLANT, texture);
		berries.add(berry);

		new SlimefunItem(magicalCategory, new SlimefunItemStack(enumStyle + "_PLANT", Material.OAK_SAPLING, "&r" + name + " Plant"), RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register(this);

		MagicalEssence magicalEssence = new MagicalEssence(magicalCategory, essence);

		magicalEssence.setRecipeOutput(item.clone());
		magicalEssence.register(this);
	}

	public static ItemStack harvestPlant(Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item == null) return null;

		for (Berry berry : getBerries()) {
			if (item.getID().equalsIgnoreCase(berry.getID())) {
				switch (berry.getType()) {
					case ORE_PLANT:
					case DOUBLE_PLANT:
						Block plant = block;

						if (Tag.LEAVES.isTagged(block.getType()))
							block = block.getRelative(BlockFace.UP);
						else
							plant = block.getRelative(BlockFace.DOWN);

						BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
						block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
						block.setType(Material.AIR);

						plant.setType(Material.OAK_SAPLING);
						BlockStorage._integrated_removeBlockInfo(plant.getLocation(), false);
						BlockStorage.store(plant, getItem(berry.toBush()));
						return berry.getItem();
					default:
						block.setType(Material.OAK_SAPLING);
						BlockStorage._integrated_removeBlockInfo(block.getLocation(), false);
						BlockStorage.store(block, getItem(berry.toBush()));
						return berry.getItem();
				}
			}
		}

		return null;
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

	@Override
	public JavaPlugin getJavaPlugin() {
		return this;
	}

	@Override
	public String getBugTrackerURL() {
		return "https://github.com/TheBusyBiscuit/ExoticGarden/issues";
	}

}
