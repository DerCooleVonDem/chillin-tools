package derc.addon.chillintools.modules;

import derc.addon.chillintools.ChillinTools;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;

public class BlockAim extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("The range at which an block will be looked at.")
        .defaultValue(7)
        .min(2)
        .max(20)
        .build()
    );

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("The speed at which the block will be looked at.")
        .defaultValue(5)
        .min(0)
        .build()
    );

    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("The blocks that will be looked at.")
        .build()
    );

    //Boolsetting for instant rotation
    private final Setting<Boolean> instant = sgGeneral.add(new BoolSetting.Builder()
        .name("instant")
        .description("Whether or not the block will be looked at instantly.")
        .defaultValue(false)
        .build()
    );

    public BlockAim() {
        super(ChillinTools.CATEGORY, "Block Aim", "Automatically Looks at a Selected Block");
    }

    public Vector3d normalize(Vector3d vector) {
        double length = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
        return new Vector3d(vector.x / length, vector.y / length, vector.z / length);
    }

    //Code originally from Meteor-Development but slightly modified
    private void aim(BlockPos target, double delta, boolean instant) {
        assert mc.player != null;
        double deltaX = (target.getX() + 0.5f) - mc.player.getPos().getX();
        double deltaZ = (target.getZ() + 0.5f) - mc.player.getPos().getZ();
        double deltaY = (target.getY() + 0.5f) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));

        // Yaw
        double angle = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
        double deltaAngle;
        double toRotate;

        if (instant) {
            mc.player.setYaw((float) angle);
        } else {
            deltaAngle = MathHelper.wrapDegrees(angle - mc.player.getYaw());
            toRotate = speed.get() * (deltaAngle >= 0 ? 1 : -1) * delta;
            if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
            mc.player.setYaw(mc.player.getYaw() + (float) toRotate);
        }

        // Pitch
        double idk = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        angle = -Math.toDegrees(Math.atan2(deltaY, idk));

        if (instant) {
            mc.player.setPitch((float) angle);
        } else {
            deltaAngle = MathHelper.wrapDegrees(angle - mc.player.getPitch());
            toRotate = speed.get() * (deltaAngle >= 0 ? 1 : -1) * delta;
            if ((toRotate >= 0 && toRotate > deltaAngle) || (toRotate < 0 && toRotate < deltaAngle)) toRotate = deltaAngle;
            mc.player.setPitch(mc.player.getPitch() + (float) toRotate);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        World world = mc.world;
        double range = this.range.get();
        List<Block> blocks = this.blocks.get();
        if (world == null || blocks.isEmpty()) {
            return;
        }
        assert mc.player != null;
        double x = mc.player.getPos().getX();
        double y = mc.player.getPos().getY();
        double z = mc.player.getPos().getZ();
        ArrayList<BlockPos> blockPositions = new ArrayList<>();
        for (Block block : blocks) {
            if (block == null) {
                continue;
            }
            for (int x1 = (int) (x - range); x1 <= x + range; x1++) {
                for (int y1 = (int) (y - range); y1 <= y + range; y1++) {
                    for (int z1 = (int) (z - range); z1 <= z + range; z1++) {
                        if (world.getBlockState(new BlockPos(x1, y1, z1)).getBlock() == block) {
                            //Add the block to the list
                            blockPositions.add(new BlockPos(x1, y1, z1));
                        }
                    }
                }
            }
        }
        // Look at the closest block in the list
        if (blockPositions.isEmpty()) {
            return;
        }
        BlockPos closest = blockPositions.get(0);
        for (BlockPos blockPos : blockPositions) {
            double dx = blockPos.getX() - x;
            double dz = blockPos.getZ() - z;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < Math.sqrt(Math.pow(closest.getX() - x, 2) + Math.pow(closest.getZ() - z, 2))) {
                closest = blockPos;
            }
        }
        aim(closest, event.tickDelta, this.instant.get());
    }
}
