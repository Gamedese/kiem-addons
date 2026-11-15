package com.example.client.cosmetic;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Client-side cosmetic trail & aura particle manager.
 * Emits distinct, lightweight world-space particles around the local player
 * based on {@link KiemConfig#cosmeticTrailEffect}.
 */
public final class CosmeticTrailManager {
    private static ClientLevel prevLevel;
    private static int prevPlayerId = -1;
    private static int prevAge = -1;
    private static Vec3 prevPos;
    private static boolean wasOnGround = true;

    private CosmeticTrailManager() { }

    public static void tick(Minecraft client) {
        if (client == null) return;
        Player player = client.player;
        ClientLevel level = client.level;
        if (player == null || level == null) {
            reset();
            return;
        }

        CosmeticCatalog.TrailEffectType type = CosmeticCatalog.TrailEffectType.fromName(KiemConfig.cosmeticTrailEffect);
        if (type == CosmeticCatalog.TrailEffectType.OFF
                || player.isInvisible()
                || player.isSpectator()
                || player.isSleeping()) {
            reset();
            return;
        }

        Vec3 pos = player.position();
        int age = player.tickCount;
        if (level != prevLevel
                || player.getId() != prevPlayerId
                || age < prevAge
                || (prevPos != null && prevPos.distanceToSqr(pos) > 16.0)) {
            reset();
        }

        prevLevel = level;
        prevPlayerId = player.getId();
        Vec3 lastPos = prevPos;
        prevPos = pos;

        if (age == prevAge) {
            return;
        }
        prevAge = age;

        Vec3 delta = player.getDeltaMovement();
        double horizSpeedSqr = delta.x * delta.x + delta.z * delta.z;
        double movedDistSqr = lastPos != null
                ? (pos.x - lastPos.x) * (pos.x - lastPos.x) + (pos.z - lastPos.z) * (pos.z - lastPos.z)
                : 0.0;
        boolean moving = horizSpeedSqr > 0.0009 || movedDistSqr > 0.0004;
        boolean sprinting = player.isSprinting();
        boolean onGround = player.onGround();
        boolean jumped = wasOnGround && !onGround && delta.y > 0.1;
        wasOnGround = onGround;

        boolean firstPerson = client.options.getCameraType().isFirstPerson();
        RandomSource rng = level.getRandom();

        switch (type) {
            case JUMP_MAGIC_CIRCLE -> tickJumpMagicCircle(level, pos, age, jumped, onGround, delta);
            case SHADOW_SMOKE -> tickShadowSmoke(level, pos, age, moving, sprinting, rng);
            case SNOWFLAKE_AURA -> tickSnowflakeAura(level, pos, age, moving, firstPerson, rng);
            case CRIMSON_PETALS -> tickCrimsonPetals(level, pos, age, moving, firstPerson, rng);
            case OFF -> {}
        }
    }

    private static void tickJumpMagicCircle(ClientLevel level, Vec3 pos, int age, boolean jumped, boolean onGround, Vec3 delta) {
        double rotOffset = age * 0.28;
        if (jumped) {
            double y = pos.y + 0.04;
            // 16-point geometric outer arcane ring
            int outerCount = 16;
            for (int i = 0; i < outerCount; i++) {
                double angle = rotOffset + (Math.PI * 2.0 * i) / outerCount;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double r = 0.72;
                var outerParticle = (i % 2 == 0) ? ParticleTypes.WITCH : ParticleTypes.ENCHANT;
                level.addParticle(outerParticle, pos.x + cos * r, y, pos.z + sin * r, cos * 0.035, 0.012, sin * 0.035);
                if (i % 4 == 0) {
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x + cos * (r * 0.85), y + 0.02, pos.z + sin * (r * 0.85), cos * 0.02, 0.025, sin * 0.02);
                }
            }
            // 8-point inner star / cross sigil
            int innerCount = 8;
            for (int i = 0; i < innerCount; i++) {
                double angle = -rotOffset + (Math.PI * 2.0 * i) / innerCount;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                level.addParticle(ParticleTypes.END_ROD, pos.x + cos * 0.42, y + 0.02, pos.z + sin * 0.42, cos * 0.015, 0.025, sin * 0.015);
                if (i % 2 == 0) {
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x + cos * 0.22, y + 0.03, pos.z + sin * 0.22, 0.0, 0.03, 0.0);
                }
            }
        } else if (!onGround && delta.y > 0.04) {
            // Ascending golden/cyan launch spiral
            double spiralAngle = age * 0.85;
            double r = 0.36;
            double cos = Math.cos(spiralAngle);
            double sin = Math.sin(spiralAngle);
            level.addParticle(ParticleTypes.END_ROD, pos.x + cos * r, pos.y + 0.12, pos.z + sin * r, 0.0, 0.02, 0.0);
            if (age % 2 == 0) {
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x - cos * r, pos.y + 0.08, pos.z - sin * r, 0.0, 0.025, 0.0);
            } else {
                level.addParticle(ParticleTypes.ENCHANT, pos.x - cos * (r * 0.8), pos.y + 0.15, pos.z - sin * (r * 0.8), 0.0, 0.015, 0.0);
            }
        }
    }

    private static void tickShadowSmoke(ClientLevel level, Vec3 pos, int age, boolean moving, boolean sprinting, RandomSource rng) {
        double helixAngle = age * (sprinting ? 0.55 : 0.35);
        double radius = moving ? 0.34 : 0.28;
        double py1 = pos.y + 0.14 + Math.sin(age * 0.25) * 0.10;
        double py2 = pos.y + 0.24 - Math.sin(age * 0.25) * 0.10;

        if (moving) {
            // Twin-helix abyssal shadow ribbons around ankles/lower back
            double cos = Math.cos(helixAngle);
            double sin = Math.sin(helixAngle);
            level.addParticle(ParticleTypes.SMOKE, pos.x + cos * radius, py1, pos.z + sin * radius, -sin * 0.015, 0.016, cos * 0.015);
            level.addParticle(ParticleTypes.SQUID_INK, pos.x - cos * radius, py2, pos.z - sin * radius, sin * 0.015, 0.012, -cos * 0.015);

            if (sprinting || age % 2 == 0) {
                double trailX = (rng.nextDouble() - 0.5) * 0.32;
                double trailZ = (rng.nextDouble() - 0.5) * 0.32;
                level.addParticle(ParticleTypes.LARGE_SMOKE, pos.x + trailX, pos.y + 0.06, pos.z + trailZ, 0.0, 0.014, 0.0);
                level.addParticle(ParticleTypes.PORTAL, pos.x + cos * 0.26, pos.y + 0.32, pos.z + sin * 0.26, 0.0, 0.012, 0.0);
            }
            if (sprinting) {
                double extraAngle = helixAngle + Math.PI * 0.5;
                level.addParticle(ParticleTypes.SMOKE, pos.x + Math.cos(extraAngle) * 0.38, pos.y + 0.10, pos.z + Math.sin(extraAngle) * 0.38, 0.0, 0.02, 0.0);
            }
        } else if (age % 3 == 0) {
            double cos = Math.cos(helixAngle);
            double sin = Math.sin(helixAngle);
            level.addParticle(ParticleTypes.SMOKE, pos.x + cos * radius, py1, pos.z + sin * radius, 0.0, 0.01, 0.0);
            if (age % 6 == 0) {
                level.addParticle(ParticleTypes.SQUID_INK, pos.x - cos * radius, py2, pos.z - sin * radius, 0.0, 0.008, 0.0);
            }
        }
    }

    private static void tickSnowflakeAura(ClientLevel level, Vec3 pos, int age, boolean moving, boolean firstPerson, RandomSource rng) {
        int interval = moving ? 2 : 3;
        if (age % interval != 0) return;

        double baseAngle = age * 0.18;
        double minY = firstPerson ? 0.45 : 1.05;
        double maxY = firstPerson ? 1.15 : 2.05;

        // Gentle drifting winter snowfall canopy around upper body/head
        for (int i = 0; i < 2; i++) {
            double angle = baseAngle + i * Math.PI + (rng.nextDouble() - 0.5) * 0.45;
            double r = 0.48 + rng.nextDouble() * 0.30;
            double py = pos.y + minY + rng.nextDouble() * (maxY - minY);
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.x + Math.cos(angle) * r, py, pos.z + Math.sin(angle) * r, -Math.sin(angle) * 0.012, -0.018, Math.cos(angle) * 0.012);
        }

        // Subtle frost glints in the canopy
        if (age % 6 == 0) {
            double glintAngle = baseAngle * 1.4 + rng.nextDouble() * Math.PI;
            double r = 0.42 + rng.nextDouble() * 0.25;
            double py = pos.y + (firstPerson ? 0.75 : 1.45) + rng.nextDouble() * 0.45;
            level.addParticle(ParticleTypes.END_ROD, pos.x + Math.cos(glintAngle) * r, py, pos.z + Math.sin(glintAngle) * r, 0.0, -0.005, 0.0);
        }

        // Soft frost mist at the feet when moving
        if (moving) {
            double footAngle = rng.nextDouble() * Math.PI * 2.0;
            double r = 0.18 + rng.nextDouble() * 0.22;
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.x + Math.cos(footAngle) * r, pos.y + 0.06, pos.z + Math.sin(footAngle) * r, Math.cos(footAngle) * 0.01, 0.004, Math.sin(footAngle) * 0.01);
        }
    }

    private static void tickCrimsonPetals(ClientLevel level, Vec3 pos, int age, boolean moving, boolean firstPerson, RandomSource rng) {
        int interval = moving ? 1 : 2;
        if (age % interval != 0) return;

        double t = age * 0.18;
        double minY = firstPerson ? 0.25 : 0.40;
        double maxY = firstPerson ? 1.05 : 1.75;
        double midY = (minY + maxY) * 0.5;
        double ampY = (maxY - minY) * 0.5;

        // Figure-8 / sine-wave crimson blossom vortex
        double x1 = Math.sin(t) * 0.65;
        double z1 = Math.sin(t * 2.0) * 0.42;
        double y1 = pos.y + midY + Math.sin(t * 0.8) * ampY;
        level.addParticle(ParticleTypes.CHERRY_LEAVES, pos.x + x1, y1, pos.z + z1, Math.cos(t) * 0.018, -0.008, Math.cos(t * 2.0) * 0.018);

        double t2 = t + Math.PI;
        double x2 = Math.sin(t2) * 0.60;
        double z2 = Math.sin(t2 * 2.0) * 0.40;
        double y2 = pos.y + midY + Math.cos(t * 0.8) * ampY;
        level.addParticle(ParticleTypes.CHERRY_LEAVES, pos.x + x2, y2, pos.z + z2, Math.cos(t2) * 0.018, -0.008, Math.cos(t2 * 2.0) * 0.018);
    }

    private static void reset() {
        prevLevel = null;
        prevPlayerId = -1;
        prevAge = -1;
        prevPos = null;
        wasOnGround = true;
    }
}
