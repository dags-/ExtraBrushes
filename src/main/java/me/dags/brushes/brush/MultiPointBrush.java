package me.dags.brushes.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeAction;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.IBrush;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;

/**
 * @author dags <dags@dags.me>
 */
public class MultiPointBrush extends Brush {

    private IBrush brush = null;
    private int spacing = 1;
    private int chance = 10;

    @Override
    public void info(Message vm) {
        vm.brushMessage(getName());
    }

    @Override
    public String getPermissionNode() {
        return brush == null ? "extrabrushes.multipointbrush" : brush.getPermissionNode();
    }

        @Override
    protected void arrow(final SnipeData data) {
        apply(SnipeAction.ARROW, data);
    }

    @Override
    protected void powder(final SnipeData data) {
        apply(SnipeAction.GUNPOWDER, data);
    }

    @Override
    public void parameters(final String[] args, final SnipeData data) {
        if (this.brush == null) {
            String id = data.owner().getCurrentToolId();
            this.brush = data.owner().previousBrush(id);
            data.sendMessage("Set wrapped brush to: " + this.brush.getName());
        }

        for (String arg : args) {
            if (arg.startsWith("s")) {
                int spacing = parseInt(arg.substring(1), this.spacing);
                this.spacing = Math.max(spacing, 1);
                data.sendMessage("Set spacing to: " + this.spacing);
            } else if (arg.startsWith("c")) {
                int chance = parseInt(arg.substring(1), this.chance);
                this.chance = Math.max(chance, 1);
                data.sendMessage("Set chance to: " + this.chance);
            }
        }
    }

    private void apply(SnipeAction action, SnipeData data) {
        if (this.brush == null) {
            return;
        }

        float radius = (float) data.getBrushSize();
        float radsq = radius * radius;

        Random random = new Random();
        int minX = Math.round(targetBlock.getBlockX() - radius);
        int maxX = Math.round(targetBlock.getBlockX() + radius);
        int minZ = Math.round(targetBlock.getBlockZ() - radius);
        int maxZ = Math.round(targetBlock.getBlockZ() + radius);

        int yOffset = action == SnipeAction.GUNPOWDER ? 1 : 0;

        for (int x = minX + random.nextInt(spacing); x <= maxX; x += spacing) {
            for (int z = minZ + random.nextInt(spacing); z <= maxZ; z += spacing) {
                int dx = x - targetBlock.getBlockX();
                int dz = z - targetBlock.getBlockZ();
                int dsq = (dx * dx) + (dz * dz);
                if (dsq <= radsq && chance > random.nextInt(100)) {
                    Location<World> target = getSolidBlockAt(data.getWorld(), x, targetBlock.getBlockY(), z, yOffset);
                    if (target != null) {
                        this.brush.perform(action, data, target, target);
                    }
                }
            }
        }
    }

    private static Location<World> getSolidBlockAt(World world, int x, int startY, int z, int offsetY) {
        for (int offset = 0; offset < 256; offset++) {
            if ((startY - offset + offsetY) >= 0) {
                Location<World> down = new Location<>(world, x, startY - offset, z);
                if (isSolidCube(down.getBlock())) {
                    return down.add(0, offsetY, 0);
                }
            }
            if ((startY + offset + offsetY) < 256) {
                Location<World> up = new Location<>(world, x, startY + offset, z);
                if (isSolidCube(up.getBlock())) {
                    return up.add(0, offsetY, 0);
                }
            }
        }
        return null;
    }

    private static boolean isSolidCube(BlockState state) {
        return state.getProperty(SolidCubeProperty.class).map(SolidCubeProperty::getValue).orElse(false);
    }

    private static int parseInt(String in, int defaulVal) {
        try {
            return Integer.parseInt(in);
        } catch (NumberFormatException e) {
            return defaulVal;
        }
    }
}
