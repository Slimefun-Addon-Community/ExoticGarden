package me.mrCookieSlime.ExoticGarden.Schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.InventoryHolder;

import me.mrCookieSlime.CSCoreLibPlugin.general.World.CustomSkull;
import me.mrCookieSlime.ExoticGarden.ExoticGarden;
import me.mrCookieSlime.ExoticGarden.Tree;
import me.mrCookieSlime.ExoticGarden.Schematic.org.jnbt.ByteArrayTag;
import me.mrCookieSlime.ExoticGarden.Schematic.org.jnbt.CompoundTag;
import me.mrCookieSlime.ExoticGarden.Schematic.org.jnbt.NBTInputStream;
import me.mrCookieSlime.ExoticGarden.Schematic.org.jnbt.ShortTag;
import me.mrCookieSlime.ExoticGarden.Schematic.org.jnbt.Tag;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

/*
*
*	This class is free software: you can redistribute it and/or modify
*	it under the terms of the GNU General Public License as published by
*	the Free Software Foundation, either version 3 of the License, or
*	(at your option) any later version.
*
*	This class is distributed in the hope that it will be useful,
*	but WITHOUT ANY WARRANTY; without even the implied warranty of
*	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*	GNU General Public License for more details.
*
*	You should have received a copy of the GNU General Public License
*	along with this class.  If not, see <http://www.gnu.org/licenses/>.
*
*/

/**
*
* @author Max
*/
public class Schematic {

	private short[] blocks;
	private byte[] data;
	private short width;
	private short lenght;
	private short height;
	private String name;

	public Schematic(String name, short[] blocks, byte[] data, short width, short lenght, short height) {
		this.blocks = blocks;
		this.data = data;
		this.width = width;
		this.lenght = lenght;
		this.height = height;
		this.name = name;
	}

	/**
	* @return the blocks
	*/
	public short[] getBlocks() {
		return blocks;
	}

	public String getName() {
		return name;
	}

	/**
	* @return the data
	*/
	public byte[] getData() {
		return data;
	}

	/**
	* @return the width
	*/
	public short getWidth() {
		return width;
	}

	/**
	* @return the lenght
	*/
	public short getLenght() {
		return lenght;
	}

	/**
	* @return the height
	*/
	public short getHeight() {
		return height;
	}

	public static void pasteSchematic(Location loc, Tree tree) {
		Schematic schematic = null;
		
		try {
			schematic = tree.getSchematic();
		} catch (IOException e) {
			ExoticGarden.instance.getLogger().log(Level.WARNING, "Could not paste Schematic for Tree: " + tree.getFruitID() + "_TREE (" + e.getClass().getSimpleName() + ")");
			return;
		}

		BlockFace[] bf = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
		short[] blocks = schematic.getBlocks();
		byte[] blockData = schematic.getData();

		short length = schematic.getLenght();
		short width = schematic.getWidth();
		short height = schematic.getHeight();

		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					int index = y * width * length + z * width + x;
					Block block = new Location(loc.getWorld(), x + loc.getX() - length / 2, y + loc.getY(), z + loc.getZ() - width / 2).getBlock();
					
					if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR || !block.getType().isSolid()) {
						if (parseId(blocks[index], blockData[index]) != null && !(block.getState() instanceof InventoryHolder)) {
							if (blocks[index] != 0) {
								block.setType(parseId(blocks[index], blockData[index]));
							}
							if (org.bukkit.Tag.LEAVES.isTagged(parseId(blocks[index], blockData[index]))) {
								if (ThreadLocalRandom.current().nextInt(100) < 25) BlockStorage.store(block, tree.getItem());
							}
							else if (parseId(blocks[index], blockData[index]) == Material.PLAYER_HEAD) {
								Rotatable s = (Rotatable) block.getBlockData();
								s.setRotation(bf[new Random().nextInt(bf.length)]);
								block.setBlockData(s);

								try {
									CustomSkull.setSkull(block, tree.getTexture());
								} catch (Exception e) {
									e.printStackTrace();
								}

								BlockStorage.store(block, tree.getFruit());
							}
						}
					}
				}
			}
		}
	}

	public static Material parseId(short blockId, byte blockData) {
		switch (blockId) {
			case 6:
				if (blockData == 0) return Material.OAK_SAPLING;
				if (blockData == 1) return Material.SPRUCE_SAPLING;
				if (blockData == 2) return Material.BIRCH_SAPLING;
				if (blockData == 3) return Material.JUNGLE_SAPLING;
				if (blockData == 4) return Material.ACACIA_SAPLING;
				if (blockData == 5) return Material.DARK_OAK_SAPLING;
				break;
			case 17:
				if (blockData == 0 || blockData == 4 || blockData == 8 || blockData == 12) return Material.OAK_LOG;
				if (blockData == 1 || blockData == 5 || blockData == 9 || blockData == 13) return Material.SPRUCE_LOG;
				if (blockData == 2 || blockData == 6 || blockData == 10 || blockData == 14) return Material.BIRCH_LOG;
				if (blockData == 3 || blockData == 7 || blockData == 11 || blockData == 15) return Material.JUNGLE_LOG;
				break;
			case 18:
				if (blockData == 0 || blockData == 4 || blockData == 8 || blockData == 12) return Material.OAK_LEAVES;
				if (blockData == 1 || blockData == 5 || blockData == 9 || blockData == 13) return Material.SPRUCE_LEAVES;
				if (blockData == 2 || blockData == 6 || blockData == 10 || blockData == 14) return Material.BIRCH_LEAVES;
				if (blockData == 3 || blockData == 7 || blockData == 11 || blockData == 15) return Material.JUNGLE_LEAVES;
				return Material.OAK_LEAVES;
			case 161:
				if (blockData == 0 || blockData == 4 || blockData == 8 || blockData == 12) return Material.ACACIA_LEAVES;
				if (blockData == 1 || blockData == 5 || blockData == 9 || blockData == 13) return Material.DARK_OAK_LEAVES;
				break;
			case 162:
				if (blockData == 0 || blockData == 4 || blockData == 8 || blockData == 12) return Material.ACACIA_LOG;
				if (blockData == 1 || blockData == 5 || blockData == 9 || blockData == 13) return Material.DARK_OAK_LOG;
				break;
			case 144:
				return Material.PLAYER_HEAD;
		}
		return null;
	}

	public static Schematic loadSchematic(File file) throws IOException {
		Map<String, Tag> schematic = null;
		
		try (NBTInputStream stream = new NBTInputStream(new FileInputStream(file))) {
			CompoundTag schematicTag = (CompoundTag) stream.readTag();
			if (!schematicTag.getName().equals("Schematic")) {
				throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
			}
			
			schematic = schematicTag.getValue();
			if (!schematic.containsKey("Blocks")) {
				throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
			}
		}

		short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
		short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
		short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

		// Get blocks
		byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
		byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
		byte[] addId = new byte[0];
		short[] blocks = new short[blockId.length]; // Have to later combine IDs

		// We support 4096 block IDs using the same method as vanilla Minecraft, where
		// the highest 4 bits are stored in a separate byte array.
		if (schematic.containsKey("AddBlocks")) {
			addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
		}

		// Combine the AddBlocks data with the first 8-bit block ID
		for (int index = 0; index < blockId.length; index++) {
			if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
				blocks[index] = (short) (blockId[index] & 0xFF);
			} 
			else {
				if ((index & 1) == 0) {
					blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
				} 
				else {
					blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
				}
			}
		}
		
		return new Schematic(file.getName().replace(".schematic", ""), blocks, blockData, width, length, height);
	}

	/**
	* Get child tag of a NBT structure.
	*
	* @param items The parent tag map
	* @param key The name of the tag to get
	* @param expected The expected type of the tag
	* @return child tag casted to the expected type
	* @throws IllegalArgumentException if the tag does not exist or the tag is not of the
	* expected type
	*/
	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws IllegalArgumentException {
		if (!items.containsKey(key)) {
			throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
		}

		Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
		}

		return expected.cast(tag);
	}

}
