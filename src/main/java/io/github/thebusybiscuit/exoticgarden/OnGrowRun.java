package io.github.thebusybiscuit.exoticgarden;

import io.github.thebusybiscuit.exoticgarden.schematics.Schematic;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullBlock;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.concurrent.ThreadLocalRandom;

public class OnGrowRun implements Runnable {

    private StructureGrowEvent e;
    private final BlockFace[] faces;

    public OnGrowRun(BlockFace[] faces) {
        this.faces = faces;
    }

    public void set(StructureGrowEvent e) {
        this.e = e;
    }

    @Override
    public void run() {
        SlimefunItem item = BlockStorage.check(e.getLocation().getBlock());

        if (item != null) {
            e.setCancelled(true);
            for (Tree tree : ExoticGarden.getTrees()) {
                if (item.getID().equalsIgnoreCase(tree.getSapling())) {
                    BlockStorage.clearBlockInfo(e.getLocation());
                    Schematic.pasteSchematic(e.getLocation(), tree);
                    return;
                }
            }

            for (Berry berry : ExoticGarden.getBerries()) {
                if (item.getID().equalsIgnoreCase(berry.toBush())) {
                    switch (berry.getType()) {
                        case BUSH:
                            e.getLocation().getBlock().setType(Material.OAK_LEAVES);
                            break;
                        case ORE_PLANT:
                        case DOUBLE_PLANT:
                            Block blockAbove = e.getLocation().getBlock().getRelative(BlockFace.UP);
                            item = BlockStorage.check(blockAbove);
                            if (item != null) return;

                            if (!Tag.SAPLINGS.isTagged(blockAbove.getType()) && !Tag.LEAVES.isTagged(blockAbove.getType())) {
                                switch (blockAbove.getType()) {
                                    case AIR:
                                    case CAVE_AIR:
                                    case SNOW:
                                        break;
                                    default:
                                        return;
                                }
                            }

                            BlockStorage.store(blockAbove, berry.getItem());
                            e.getLocation().getBlock().setType(Material.OAK_LEAVES);
                            blockAbove.setType(Material.PLAYER_HEAD);
                            Rotatable rotatable = (Rotatable) blockAbove.getBlockData();
                            rotatable.setRotation(faces[ThreadLocalRandom.current().nextInt(faces.length)]);
                            blockAbove.setBlockData(rotatable);

                            SkullBlock.setFromHash(blockAbove, berry.getTexture());
                            break;
                        default:
                            e.getLocation().getBlock().setType(Material.PLAYER_HEAD);
                            Rotatable s = (Rotatable) e.getLocation().getBlock().getBlockData();
                            s.setRotation(faces[ThreadLocalRandom.current().nextInt(faces.length)]);
                            e.getLocation().getBlock().setBlockData(s);

                            SkullBlock.setFromHash(e.getLocation().getBlock(), berry.getTexture());
                            break;
                    }

                    BlockStorage._integrated_removeBlockInfo(e.getLocation(), false);
                    BlockStorage.store(e.getLocation().getBlock(), berry.getItem());
                    e.getWorld().playEffect(e.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
                    break;
                }
            }
        }
    }
}
