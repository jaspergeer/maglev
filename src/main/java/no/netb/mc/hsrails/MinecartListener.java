package no.netb.mc.hsrails;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RedstoneRail;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import java.util.Set;


public class MinecartListener implements Listener {

    /**
     * Default speed, in meters per tick. A tick is 0.05 seconds, thus 0.4 * 1/0.05 = 8 m/s
     */
    private static final double DEFAULT_SPEED_METERS_PER_TICK = 0.4d;

    private final Set<Material> boostBlocks;
    private final Set<Material> hardBrakeBlocks;

    private final Set<Material> maglevBlocks;

    public MinecartListener(Set<Material> boostBlocks,
                            Set<Material> hardBrakeBlocks,
                            Set<Material> maglevBlocks) {
        this.boostBlocks = boostBlocks;
        this.hardBrakeBlocks = hardBrakeBlocks;
        this.maglevBlocks = maglevBlocks;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent event) {

        if (event.getVehicle() instanceof Minecart) {
            Minecart cart = (Minecart) event.getVehicle();
            Location cartLocation = cart.getLocation();
            World cartsWorld = cart.getWorld();

            Block rail = cartsWorld.getBlockAt(cartLocation);
            Block blockBelow = cartsWorld.getBlockAt(cartLocation.add(0, -1, 0));

            if (rail.getType() == Material.POWERED_RAIL) {
                if (boostBlocks.contains(blockBelow.getType())) {
                    cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK * HsRails.getConfiguration().getSpeedMultiplier());
                }
                else {
                    cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK);
                }
                RedstoneRail railBlockData = (RedstoneRail) rail.getBlockData();
                if (!railBlockData.isPowered() && hardBrakeBlocks.contains(blockBelow.getType())) {
                    Vector cartVelocity = cart.getVelocity();
                    cartVelocity.multiply(HsRails.getConfiguration().getHardBrakeMultiplier());
                    cart.setVelocity(cartVelocity);
                }
            } else if (maglevBlocks.contains(blockBelow.getType()) &&
                    (blockBelow.isBlockPowered() || blockBelow.isBlockIndirectlyPowered())) {
                cart.setGravity(false);
                if (cart.getLocation().getY() % 1 < HsRails.getConfiguration().getMaglevLevitationAmount()) {
                    Location cartLoc = cart.getLocation();
                    cartLoc.setY(cartLoc.getY() - (cartLoc.getY() % 1) +
                            HsRails.getConfiguration().getMaglevLevitationAmount());
                    cart.teleport(cartLoc);
                }
                cart.setMaxSpeed(DEFAULT_SPEED_METERS_PER_TICK * HsRails.getConfiguration().getMaglevSpeedMultiplier());
                cart.setFlyingVelocityMod(
                        new Vector(1, 1, 1)
                        .multiply(1 + (HsRails.getConfiguration().getMaglevAcceleration() / cart.getVelocity().length())));
                return;
            }
            cart.setGravity(true);
        }
    }
}
