package io.github.thebusybiscuit.exoticgarden.items;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.exoticgarden.ExoticGarden;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.Slimefun;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;

public class Kitchen extends MultiBlockMachine {

    private ExoticGarden plugin;

    public Kitchen(ExoticGarden plugin, Category category) {
        super(category, new SlimefunItemStack("KITCHEN", Material.CAULDRON, "&eKitchen", "", "&a&oYou can make a bunch of different yummies here!", "&a&oThe result goes in the Furnace output slot"), new ItemStack[] { new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)"), new CustomItem(Material.BRICK_STAIRS, "&oBrick Stairs (upside down)"), new ItemStack(Material.BRICKS), new ItemStack(Material.STONE_PRESSURE_PLATE), new ItemStack(Material.IRON_TRAPDOOR), new ItemStack(Material.BOOKSHELF), new ItemStack(Material.FURNACE), new ItemStack(Material.DISPENSER), new ItemStack(Material.CRAFTING_TABLE) }, new ItemStack[0], BlockFace.SELF);

        this.plugin = plugin;
    }

    @Override
    public void onInteract(Player p, Block b) {
        Block dispenser = b.getRelative(BlockFace.DOWN);

        Furnace furnace = locateFurnace(dispenser);
        FurnaceInventory furnaceInventory = furnace.getInventory();

        Inventory inv = ((Dispenser) dispenser.getState()).getInventory();
        List<ItemStack[]> inputs = RecipeType.getRecipeInputList(this);

        recipe:
        for (ItemStack[] input : inputs) {
            for (int i = 0; i < inv.getContents().length; i++) {
                if (!SlimefunUtils.isItemSimilar(inv.getContents()[i], input[i], true)) continue recipe;
            }

            ItemStack adding = RecipeType.getRecipeOutputList(this, input);

            if (Slimefun.hasUnlocked(p, adding, true)) {
                boolean canFit = furnaceInventory.getResult() == null || (furnaceInventory.getResult().getAmount() + adding.getAmount() <= 64 && SlimefunUtils.isItemSimilar(furnaceInventory.getResult(), adding, true));

                if (!canFit) {
                    SlimefunPlugin.getLocalization().sendMessage(p, "machines.full-inventory", true);
                    return;
                }

                for (int i = 0; i < inv.getContents().length; i++) {
                    ItemStack item = inv.getItem(i);

                    if (item != null) {
                        ItemUtils.consumeItem(item, item.getType() == Material.MILK_BUCKET);
                    }
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> p.getWorld().playSound(furnace.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1F, 1F), 55L);

                for (int i = 1; i < 7; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> p.getWorld().playSound(furnace.getLocation(), Sound.BLOCK_METAL_PLACE, 7F, 1F), i * 5L);
                }

                if (furnaceInventory.getResult() == null) {
                    furnaceInventory.setResult(adding);
                }
                else {
                    furnaceInventory.getResult().setAmount(furnaceInventory.getResult().getAmount() + adding.getAmount());
                }
            }

            return;
        }

        SlimefunPlugin.getLocalization().sendMessage(p, "machines.pattern-not-found", true);
    }

    private static Furnace locateFurnace(Block b) {
        if (b.getRelative(BlockFace.EAST).getType() == Material.FURNACE) {
            return (Furnace) b.getRelative(BlockFace.EAST).getState();
        }
        else if (b.getRelative(BlockFace.WEST).getType() == Material.FURNACE) {
            return (Furnace) b.getRelative(BlockFace.WEST).getState();
        }
        else if (b.getRelative(BlockFace.NORTH).getType() == Material.FURNACE) {
            return (Furnace) b.getRelative(BlockFace.NORTH).getState();
        }
        else {
            return (Furnace) b.getRelative(BlockFace.SOUTH).getState();
        }
    }
}
