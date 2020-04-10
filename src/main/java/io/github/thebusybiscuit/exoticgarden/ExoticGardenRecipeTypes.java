package io.github.thebusybiscuit.exoticgarden;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

public final class ExoticGardenRecipeTypes {

    private ExoticGardenRecipeTypes() {}

    public static final RecipeType KITCHEN = new RecipeType(new NamespacedKey(ExoticGarden.instance, "kitchen"), new SlimefunItemStack("KITCHEN", Material.CAULDRON, "&eKitchen"), "", "&rThis item must be made", "&rin a Kitchen");
    public static final RecipeType BREAKING_GRASS = new RecipeType(new NamespacedKey(ExoticGarden.instance, "breaking_grass"), new CustomItem(Material.GRASS, "&7Breaking Grass"));
    public static final RecipeType HARVEST_TREE = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_tree"), new CustomItem(Material.OAK_LEAVES, "&aHarvesting a Tree", "", "&rYou can obtain this Item by", "&rharvesting the shown Tree"));
    public static final RecipeType HARVEST_BUSH = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_bush"), new CustomItem(Material.OAK_LEAVES, "&aHarvesting a Bush", "", "&rYou can obtain this Item by", "&rharvesting the shown Bush"));

}
