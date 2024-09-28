package au.com.mineauz.minigames.display;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DisplayCuboid extends AbstractDisplayObject implements INonPersistentDisplay, IDisplayObject {
    private static final Location temp = new Location(null, 0, 0, 0);

    private final @NotNull Vector minCorner;
    private final @NotNull Vector maxCorner;

    private int lastBarrier = 41;

    public DisplayCuboid(@NotNull DisplayManager manager, @NotNull World world, @NotNull Vector minCorner, @NotNull Vector maxCorner) {
        super(manager, world);
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
    }

    public DisplayCuboid(@NotNull DisplayManager manager, @NotNull Player player, @NotNull Vector minCorner, @NotNull Vector maxCorner) {
        this(manager, player.getWorld(), minCorner, maxCorner);
        this.player = player;
    }

    @Override
    public void show() {
        refresh();
        super.show();
    }

    @Override
    public int getRefreshInterval() {
        return 10;
    }

    @Override
    public void refresh() {
        // Don't display effect if they cant see it
        if (player != null && player.getWorld() != getWorld()) {
            return;
        }
        double step = 0.5;

        // X axis
        for (double x = minCorner.getX(); x <= maxCorner.getX(); x += step) {
            placeEffectAt(x, minCorner.getY(), minCorner.getZ());
            placeEffectAt(x, maxCorner.getY(), minCorner.getZ());
            placeEffectAt(x, minCorner.getY(), maxCorner.getZ());
            placeEffectAt(x, maxCorner.getY(), maxCorner.getZ());
        }

        // Y axis
        for (double y = minCorner.getY(); y <= maxCorner.getY(); y += step) {
            placeEffectAt(minCorner.getX(), y, minCorner.getZ());
            placeEffectAt(maxCorner.getX(), y, minCorner.getZ());
            placeEffectAt(minCorner.getX(), y, maxCorner.getZ());
            placeEffectAt(maxCorner.getX(), y, maxCorner.getZ());
        }

        // Z axis
        for (double z = minCorner.getZ(); z <= maxCorner.getZ(); z += step) {
            placeEffectAt(minCorner.getX(), minCorner.getY(), z);
            placeEffectAt(maxCorner.getX(), minCorner.getY(), z);
            placeEffectAt(minCorner.getX(), maxCorner.getY(), z);
            placeEffectAt(maxCorner.getX(), maxCorner.getY(), z);
        }
    }

    private void placeEffectAt(double x, double y, double z) {
        lastBarrier++;
        if (lastBarrier < 41) {
            return;
        }
        lastBarrier = 0;
        temp.setX(x);
        temp.setY(y);
        temp.setZ(z);
        temp.setWorld(getWorld());

        if (player == null) {
            getWorld().spawnParticle(Particle.BLOCK_MARKER, temp, 1, 0, 0, 0, 0, Material.BARRIER.createBlockData());
        } else {
            player.spawnParticle(Particle.BLOCK_MARKER, temp, 1, 0, 0, 0, 0, Material.BARRIER.createBlockData());
        }
    }
}