package no.netb.mc.hsrails;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Configuration {

    private Set<Material> boostBlocks;
    private Set<Material> hardBrakeBlocks;
    private Set<Material> maglevBlocks;
    private double speedMultiplier;
    private double hardBrakeMultiplier;
    private double maglevSpeedMultiplier;
    private double maglevAcceleration;
    private double maglevLevitationAmount;


    public Set<Material> getBoostBlocks() {
        return boostBlocks;
    }

    public Set<Material> getHardBrakeBlocks() {
        return hardBrakeBlocks;
    }

    public Set<Material> getMaglevBlocks() {
        return maglevBlocks;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getHardBrakeMultiplier() {
        return hardBrakeMultiplier;
    }

    public double getMaglevSpeedMultiplier() { return maglevSpeedMultiplier; }

    public double getMaglevAcceleration() { return maglevAcceleration; }

    public double getMaglevLevitationAmount() { return maglevLevitationAmount; }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public void readConfig(FileConfiguration fileConfig, Logger logger) {
        readBoostBlocks(fileConfig, logger);
        readHardBrakeBlocks(fileConfig, logger);
        readSpeedMultiplier(fileConfig, logger);
        readHardBrakeMultiplier(fileConfig, logger);
        readMaglevAcceleration(fileConfig, logger);
        readMaglevSpeedMultiplier(fileConfig, logger);
        readMaglevLevitationAmount(fileConfig, logger);
        readMaglevBlocks(fileConfig, logger);
    }

    private void readBoostBlocks(FileConfiguration fileConfig, Logger logger) {
        List<String> boostBlockList = fileConfig.getStringList("boostBlocks");
        boostBlocks = boostBlockList.stream().map(Material::matchMaterial).collect(Collectors.toSet());
        logger.info(
            String.format("Setting boost blocks to %s", boostBlocks.stream()
                    .map( bb -> "'" + bb.getKey() + "'" )
                    .collect(Collectors.joining(", ")))
        );
    }

    private void readHardBrakeBlocks(FileConfiguration fileConfig, Logger logger) {
        List<String> hardBrakeBlockList = fileConfig.getStringList("hardBrakeBlocks");
        hardBrakeBlocks = hardBrakeBlockList.stream().map(Material::matchMaterial).collect(Collectors.toSet());
        logger.info(
                String.format("Setting hard brake blocks to %s", hardBrakeBlocks.stream()
                        .map( bb -> "'" + bb.getKey() + "'" )
                        .collect(Collectors.joining(", ")))
        );
    }

    private void readMaglevBlocks(FileConfiguration fileConfig, Logger logger) {
        List<String> maglevBlockList = fileConfig.getStringList("maglevBlocks");
        maglevBlocks = maglevBlockList.stream().map(Material::matchMaterial).collect(Collectors.toSet());
        logger.info(
                String.format("Setting maglev blocks to %s", maglevBlocks.stream()
                        .map( bb -> "'" + bb.getKey() + "'" )
                        .collect(Collectors.joining(", ")))
        );
    }

    private void readSpeedMultiplier(FileConfiguration fileConfig, Logger logger) {
        double speedMultiplier = fileConfig.getDouble("speedMultiplier");
        if (speedMultiplier <= 0) {
            logger.warning("Warning: speed multiplier set to 0 or below in config. Using value of 0.1 as fallback.");
            speedMultiplier = 0.1;
        } else if (speedMultiplier > 8) {
            logger.warning("Warning: speed multiplier set above 8 in config. Using value of 8 as fallback.");
            speedMultiplier = 8d;
        } else {
            logger.info("Setting speed multiplier to " + speedMultiplier);
        }

        if (speedMultiplier > 4) {
            logger.info("Note: speed multiplier is set above 4. Typically, due to server limitations you may not see an increase in speed greater than 4x,"
                    + " however the carts will have more momentum. This means they will coast for longer even though the max speed is seemingly 4x.");
        }
        this.speedMultiplier = speedMultiplier;
    }

    private void readMaglevAcceleration(FileConfiguration fileConfig, Logger logger) {
        this.maglevAcceleration = fileConfig.getDouble("maglevAcceleration");
    }

    private void readMaglevSpeedMultiplier(FileConfiguration fileConfig, Logger logger) {
        this.maglevSpeedMultiplier = fileConfig.getDouble("maglevSpeedMultiplier");
    }

    private void readMaglevLevitationAmount(FileConfiguration fileConfig, Logger logger) {
        this.maglevLevitationAmount = fileConfig.getDouble("maglevLevitationAmount");
    }

    private void readHardBrakeMultiplier(FileConfiguration fileConfig, Logger logger) {
        double hardBrakeMultiplier = fileConfig.getDouble("hardBrakeMultiplier");
        if (hardBrakeMultiplier < 1.0) {
            logger.warning("Warning: brake multiplier not set or set to below 1 in config. Using value of 1 as fallback.");
            hardBrakeMultiplier = 1.0;
        }
        else {
            logger.info("Setting brake multiplier to " + hardBrakeMultiplier);
        }

        this.hardBrakeMultiplier = 1.0 / hardBrakeMultiplier;
    }
}
