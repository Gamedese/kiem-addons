package com.example.client.pet;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

/**
 * Client-side otonom yerde gezme yoneticisi.
 * Oyuncu sabit kalip pet "sikildiginda" omuzdan yere atlar, etrafta gezinir ve yuruyunce takip edip omza doner.
 */
public final class GroundPetManager {
    public enum State { SHOULDER, HOPPING_DOWN, ROAMING, RETURNING }

    private static final Random RNG = new Random();
    private static State state = State.SHOULDER;
    private static ShoulderPetType activeType = ShoulderPetType.OFF;
    private static int stillTicks = 0;
    private static int stateTicks = 0;

    private static double x, y, z;
    private static double prevX, prevY, prevZ;
    private static double targetX, targetZ;
    private static int pauseTicks = 0;
    private static float yaw = 0f, prevYaw = 0f;
    private static float walkPos = 0f, prevWalkPos = 0f;
    private static float walkSpeed = 0f, prevWalkSpeed = 0f;
    private static float hopArcY = 0f, prevHopArcY = 0f;
    private static float swimAmount = 0f, prevSwimAmount = 0f;
    private static boolean inWater = false, wasInWater = false;
    private static int dryShakeTicks = 0;
    private static float petAge = 0f;
    private static int waitTicks = 0;

    private GroundPetManager() { }

    public static boolean supportsGroundRoam(ShoulderPetType type) {
        return type == ShoulderPetType.HAMSTER_HD;
    }

    public static boolean isUsingWorldRenderer(ShoulderPetType type) {
        return supportsGroundRoam(type) && state != State.SHOULDER && activeType == type;
    }

    public static void reset() {
        state = State.SHOULDER;
        stillTicks = 0;
        stateTicks = 0;
        walkSpeed = 0f;
        prevWalkSpeed = 0f;
        hopArcY = 0f;
        prevHopArcY = 0f;
        swimAmount = 0f;
        prevSwimAmount = 0f;
        inWater = false;
        wasInWater = false;
        dryShakeTicks = 0;
    }

    public static void tick(Minecraft mc) {
        ShoulderPetType type = ShoulderPetType.fromName(KiemConfig.shoulderPetType);
        boolean fullGround = "GROUND".equalsIgnoreCase(KiemConfig.petMode);
        boolean allowGround = !"SHOULDER".equalsIgnoreCase(KiemConfig.petMode) && KiemConfig.petGroundRoam;
        if (mc.player == null || mc.level == null || !supportsGroundRoam(type) || !allowGround) {
            activeType = type;
            reset();
            return;
        }
        if (activeType != type) {
            activeType = type;
            reset();
        }
        if (mc.isPaused()) return;

        LocalPlayer p = mc.player;
        ClientLevel level = mc.level;
        boolean playerFlying = p.getAbilities().flying || p.isFallFlying();

        if (fullGround && state == State.SHOULDER && !playerFlying) {
            spawnNearPlayer(p, level);
        }
        petAge += 1f;
        prevX = x;
        prevY = y;
        prevZ = z;
        prevYaw = yaw;
        prevWalkPos = walkPos;
        prevWalkSpeed = walkSpeed;
        prevHopArcY = hopArcY;
        prevSwimAmount = swimAmount;
        if (dryShakeTicks > 0) dryShakeTicks--;

        Vec3 vel = p.getDeltaMovement();
        boolean playerStill = vel.horizontalDistanceSqr() < 0.0005 && p.onGround()
                && !playerFlying && !p.isInWater() && !p.isSleeping() && !p.isPassenger();

        switch (state) {
            case SHOULDER -> {
                if (playerStill) {
                    stillTicks++;
                    if (stillTicks >= KiemConfig.petBoredSeconds * 20) {
                        startHopDown(p, level);
                    }
                } else {
                    stillTicks = 0;
                }
            }
            case HOPPING_DOWN -> {
                stateTicks++;
                float prog = Math.min(1f, stateTicks / 10f);
                Vec3 sh = shoulderWorldPos(p);
                double floorY = findFloorY(level, targetX, p.getY(), targetZ);
                x = Mth.lerp(prog, sh.x, targetX);
                z = Mth.lerp(prog, sh.z, targetZ);
                double baseY = Mth.lerp(prog, sh.y, floorY);
                hopArcY = (float) Math.sin(prog * Math.PI) * 0.35f;
                y = baseY;
                yaw = approachAngle(yaw, (float) Math.toDegrees(Math.atan2(-(targetX - sh.x), targetZ - sh.z)), 25f);
                walkSpeed = 0.25f;
                walkPos += 0.3f;
                if (stateTicks >= 10) {
                    state = State.ROAMING;
                    stateTicks = 0;
                    hopArcY = 0f;
                    prevHopArcY = 0f;
                    pauseTicks = 25 + RNG.nextInt(35);
                    waitTicks = 0;
                }
            }
            case ROAMING -> {
                stateTicks++;
                double dxP = p.getX() - x;
                double dzP = p.getZ() - z;
                double distP = Math.hypot(dxP, dzP);

                if (playerFlying) {
                    state = State.RETURNING;
                    stateTicks = 0;
                    break;
                }

                if (fullGround) {
                    double maxVert = (inWater || p.isInWater()) ? 10.0 : 4.5;
                    if (distP > 9.0 || Math.abs(p.getY() - y) > maxVert) {
                        spawnNearPlayer(p, level);
                        break;
                    }
                } else {
                    // Oyuncu uzaklasti veya suya girdi -> omza geri zipla
                    if (distP > 4.6 || Math.abs(p.getY() - y) > 2.6 || p.isInWater() || p.isSleeping()) {
                        state = State.RETURNING;
                        stateTicks = 0;
                        break;
                    }
                }

                // Oyuncu yurumeye basladiysa arkasindan takip et
                boolean playerMoving = vel.horizontalDistanceSqr() > 0.002 || (fullGround && distP > 1.75);
                if (playerMoving) {
                    waitTicks++;
                    float bodyRad = (float) Math.toRadians(p.yBodyRot);
                    double sideSign = "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide) ? 1.0 : -1.0;
                    targetX = p.getX() - Math.sin(bodyRad) * 0.95 + Math.cos(bodyRad) * 0.65 * sideSign;
                    targetZ = p.getZ() + Math.cos(bodyRad) * 0.95 + Math.sin(bodyRad) * 0.65 * sideSign;
                    pauseTicks = 0;
                    if (!fullGround && waitTicks > 55) {
                        state = State.RETURNING;
                        stateTicks = 0;
                        break;
                    }
                } else {
                    waitTicks = 0;
                }

                double dx = targetX - x;
                double dz = targetZ - z;
                double distT = Math.hypot(dx, dz);

                if (pauseTicks > 0) {
                    pauseTicks--;
                    walkSpeed = Mth.lerp(0.25f, walkSpeed, 0f);
                    // Ara sira oyuncuya donup baksin
                    if (pauseTicks % 30 == 0 && RNG.nextFloat() < 0.6f) {
                        float toPlayer = (float) Math.toDegrees(Math.atan2(-(p.getX() - x), p.getZ() - z));
                        yaw = approachAngle(yaw, toPlayer, 18f);
                    }
                    if (pauseTicks == 0) {
                        pickNewRoamTarget(p);
                    }
                } else if (distT > 0.14) {
                    double spd = playerMoving ? (fullGround ? Math.min(0.24, 0.08 + distT * 0.07) : Math.min(0.16, 0.06 + distT * 0.05)) : 0.065;
                    double stepX = (dx / distT) * spd;
                    double stepZ = (dz / distT) * spd;
                    double nextSurface = findSurfaceY(level, x + stepX, y, z + stepZ);
                    if (Math.abs(nextSurface - y) <= 1.25) {
                        x += stepX;
                        z += stepZ;
                        float targetYaw = (float) Math.toDegrees(Math.atan2(-stepX, stepZ));
                        yaw = approachAngle(yaw, targetYaw, 22f);
                        walkSpeed = (float) (spd * 12f);
                        walkPos += walkSpeed * 0.55f;
                    } else {
                        pickNewRoamTarget(p);
                        pauseTicks = 15;
                    }
                } else {
                    walkSpeed = Mth.lerp(0.25f, walkSpeed, 0f);
                    pauseTicks = 30 + RNG.nextInt(55);
                }

                // Her tick zemin veya su yuzeyine puruzsuz otur (dururken de titremez!)
                double targetY = findSurfaceY(level, x, y, z);
                y = Mth.lerp(0.35, y, targetY);
                inWater = isWaterAt(level, x, y + 0.12, z);
                if (wasInWater && !inWater) {
                    dryShakeTicks = 18;
                }
                wasInWater = inWater;
                swimAmount = Mth.lerp(0.22f, swimAmount, inWater ? 1f : 0f);
                hopArcY = 0f;
            }
            case RETURNING -> {
                stateTicks++;
                float prog = Math.min(1f, stateTicks / 8f);
                Vec3 sh = shoulderWorldPos(p);
                x = Mth.lerp(prog * 0.45f + 0.35f, x, sh.x);
                z = Mth.lerp(prog * 0.45f + 0.35f, z, sh.z);
                y = Mth.lerp(prog * 0.45f + 0.35f, y, sh.y);
                hopArcY = (float) Math.sin(prog * Math.PI) * 0.32f;
                yaw = approachAngle(yaw, p.yBodyRot, 30f);
                walkSpeed = 0.2f;
                walkPos += 0.25f;
                if (stateTicks >= 8) {
                    reset();
                }
            }
        }
    }

    private static void spawnNearPlayer(LocalPlayer p, ClientLevel level) {
        float bodyRad = (float) Math.toRadians(p.yBodyRot);
        double sideSign = "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide) ? 1.0 : -1.0;
        x = p.getX() + Math.cos(bodyRad) * 0.85 * sideSign - Math.sin(bodyRad) * 0.45;
        z = p.getZ() + Math.sin(bodyRad) * 0.85 * sideSign + Math.cos(bodyRad) * 0.45;
        y = findFloorY(level, x, p.getY(), z);
        prevX = x;
        prevY = y;
        prevZ = z;
        targetX = x;
        targetZ = z;
        yaw = p.yBodyRot;
        prevYaw = yaw;
        state = State.ROAMING;
        stateTicks = 0;
        hopArcY = 0f;
        prevHopArcY = 0f;
        pauseTicks = 20;
    }

    private static void startHopDown(LocalPlayer p, ClientLevel level) {
        Vec3 sh = shoulderWorldPos(p);
        x = sh.x;
        y = sh.y;
        z = sh.z;
        prevX = x;
        prevY = y;
        prevZ = z;
        yaw = p.yBodyRot;
        prevYaw = yaw;
        float bodyRad = (float) Math.toRadians(p.yBodyRot);
        double sideSign = "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide) ? 1.0 : -1.0;
        targetX = p.getX() + Math.cos(bodyRad) * 0.85 * sideSign - Math.sin(bodyRad) * 0.45;
        targetZ = p.getZ() + Math.sin(bodyRad) * 0.85 * sideSign + Math.cos(bodyRad) * 0.45;
        state = State.HOPPING_DOWN;
        stateTicks = 0;
        hopArcY = 0f;
        prevHopArcY = 0f;
        stillTicks = 0;
    }

    private static void pickNewRoamTarget(LocalPlayer p) {
        double angle = RNG.nextDouble() * Math.PI * 2.0;
        double rad = 0.75 + RNG.nextDouble() * 1.45;
        targetX = p.getX() + Math.cos(angle) * rad;
        targetZ = p.getZ() + Math.sin(angle) * rad;
    }

    private static Vec3 shoulderWorldPos(LocalPlayer p) {
        float bodyRad = (float) Math.toRadians(p.yBodyRot);
        double sideSign = "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide) ? 1.0 : -1.0;
        double sx = p.getX() + Math.cos(bodyRad) * 0.36 * sideSign;
        double sz = p.getZ() + Math.sin(bodyRad) * 0.36 * sideSign;
        double sy = p.getY() + (p.isCrouching() ? 1.15 : 1.42);
        return new Vec3(sx, sy, sz);
    }

    private static boolean isWaterAt(ClientLevel level, double wx, double wy, double wz) {
        BlockPos pos = BlockPos.containing(wx, wy, wz);
        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    private static double findSurfaceY(ClientLevel level, double wx, double refY, double wz) {
        int bx = Mth.floor(wx);
        int bz = Mth.floor(wz);

        // Oyuncu veya pet suyun icindeyse once suyun gercek ust yuzeyini tara
        int checkY = Mth.floor(refY + 0.5);
        BlockPos checkPos = new BlockPos(bx, checkY, bz);
        if (level.getFluidState(checkPos).is(FluidTags.WATER)) {
            int topY = checkY;
            for (int up = 1; up <= 24; up++) {
                BlockPos above = new BlockPos(bx, checkY + up, bz);
                if (level.getFluidState(above).is(FluidTags.WATER)) {
                    topY = checkY + up;
                } else {
                    break;
                }
            }
            BlockPos topPos = new BlockPos(bx, topY, bz);
            double surfaceY = topY + level.getFluidState(topPos).getHeight(level, topPos) - 0.14;
            // Derin su altindaysa yuzeye tirmanip teleport dongusune girmek yerine oyuncunun yaninda yuzsun
            if (surfaceY - refY > 2.5) {
                return refY;
            }
            return surfaceY;
        }

        int startY = Mth.floor(refY + 1.2);
        for (int by = startY; by >= startY - 4; by--) {
            BlockPos pos = new BlockPos(bx, by, bz);
            FluidState fluid = level.getFluidState(pos);
            if (fluid.is(FluidTags.WATER)) {
                // Su yuzeyinde basi/kulaklari disarida kalacak sekilde yuzsun
                return by + fluid.getHeight(level, pos) - 0.14;
            }
            BlockState bs = level.getBlockState(pos);
            VoxelShape shape = bs.getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
                return by + shape.max(net.minecraft.core.Direction.Axis.Y);
            }
        }
        return refY;
    }

    private static double findFloorY(ClientLevel level, double wx, double refY, double wz) {
        return findSurfaceY(level, wx, refY, wz);
    }

    private static float approachAngle(float current, float target, float maxStep) {
        float diff = Mth.wrapDegrees(target - current);
        return current + Mth.clamp(diff, -maxStep, maxStep);
    }

    public static double getRenderX(float pt) { return Mth.lerp(pt, prevX, x); }
    public static double getRenderY(float pt) { return Mth.lerp(pt, prevY, y) + Mth.lerp(pt, prevHopArcY, hopArcY); }
    public static double getRenderZ(float pt) { return Mth.lerp(pt, prevZ, z); }
    public static float getRenderYaw(float pt) { return Mth.rotLerp(pt, prevYaw, yaw); }
    public static float getRenderWalkPos(float pt) { return Mth.lerp(pt, prevWalkPos, walkPos); }
    public static float getRenderWalkSpeed(float pt) { return Mth.lerp(pt, prevWalkSpeed, walkSpeed); }
    public static float getRenderSwimAmount(float pt) { return Mth.lerp(pt, prevSwimAmount, swimAmount); }
    public static float getRenderDryShake(float pt) { return dryShakeTicks > 0 ? Math.max(0f, (dryShakeTicks - pt) / 18f) : 0f; }
    public static float getAge(float pt) { return petAge + pt; }
    public static State getState() { return state; }
}
