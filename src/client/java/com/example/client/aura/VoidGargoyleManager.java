package com.example.client.aura;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Tick-owned runtime manager for the Obsidian Void Gargoyle Aura.
 * Handles accumulated wing phase (phase-jump free transitions), state-driven AI
 * (attack lunge + roar, idle curious look, sprint/elytra poise), world-space
 * inertia-compensated ribbon trails, and rotating 3D magma/obsidian cubes.
 */
public final class VoidGargoyleManager {
    public static final int TRAIL_LENGTH = 26;
    private static final int MAX_CUBES = 85;
    private static final double INERTIA_FACTOR = 0.65;

    private static final SakuraOrbitClock CLOCK = new SakuraOrbitClock();
    private static final Random RANDOM = new Random();
    private static ClientLevel level;
    private static int previousAge = -1;
    private static int playerId = -1;
    private static Vec3 lastPlayerPos = null;
    private static int nextAttacker = 0;
    private static int attackCooldown = 0;

    @SuppressWarnings("unchecked")
    private static final List<Vec3>[] TRAILS = new List[] {
            new ArrayList<>(TRAIL_LENGTH),
            new ArrayList<>(TRAIL_LENGTH),
            new ArrayList<>(TRAIL_LENGTH)
    };
    private static final List<WorldCube> CUBES = new ArrayList<>();

    public static final class WorldCube {
        public Vec3 pos;
        public Vec3 vel;
        public final int bornAge;
        public final int lifetime;
        public final float size;
        public final float rotSpeed;
        public final int seed;
        public final boolean isObsidian;

        public WorldCube(Vec3 pos, Vec3 vel, int bornAge, int lifetime, float size, float rotSpeed, int seed, boolean isObsidian) {
            this.pos = pos;
            this.vel = vel;
            this.bornAge = bornAge;
            this.lifetime = lifetime;
            this.size = size;
            this.rotSpeed = rotSpeed;
            this.seed = seed;
            this.isObsidian = isObsidian;
        }
    }

    public static final class GargoyleState {
        public int snarlTick = -1;
        public int cooldown;
        public boolean burstTriggered = false;
        public float prevJaw = 0.0f;
        public float jaw = 0.0f;
        public float recoil = 0.0f;

        public float prevWingPhase = 0.0f;
        public float wingPhase = 0.0f;
        public float currentFlapSpeed = 0.36f;
        public float currentWingAmp = 0.34f;

        public int lungeTick = -1;
        public float lungeAmount = 0.0f;

        public float lookYaw = 0.0f;
        public float lookPitch = 0.0f;

        public GargoyleState(int initialCooldown, float initialPhase) {
            this.cooldown = initialCooldown;
            this.wingPhase = initialPhase;
            this.prevWingPhase = initialPhase;
        }
    }

    private static final GargoyleState[] STATES = new GargoyleState[] {
            new GargoyleState(25, 0.0f),
            new GargoyleState(65, 2.094f),
            new GargoyleState(105, 4.188f)
    };

    private VoidGargoyleManager() { }

    public static float orbitTime(float age) {
        CLOCK.setSpeed(KiemConfig.voidGargoyleSpeed);
        return CLOCK.sample(age);
    }

    public static void setSpeed(float speed) {
        if (!Float.isFinite(speed)) speed = 1f;
        speed = Math.max(0.25f, Math.min(2f, speed));
        CLOCK.setSpeed(speed);
        KiemConfig.voidGargoyleSpeed = speed;
    }

    public static VoidGargoyleOrbit.Sample sample(float time, int index) {
        var base = VoidGargoyleOrbit.sample(time, index,
                KiemConfig.voidGargoyleHeights[index],
                KiemConfig.voidGargoyleRadii[index],
                KiemConfig.voidGargoyleBob);
        float lunge = STATES[index].lungeAmount;
        if (lunge <= 0.001f) {
            return base;
        }
        // Smooth forward lunge along the dragon's heading + slight outward/upward surge
        double fwdX = -Math.sin(base.yaw()) * lunge * 0.45;
        double fwdZ = Math.cos(base.yaw()) * lunge * 0.45;
        double upY = lunge * 0.14;
        return new VoidGargoyleOrbit.Sample(
                base.x() + fwdX, base.y() + upY, base.z() + fwdZ,
                base.yaw(), base.pitch() - lunge * 0.12f, base.roll(), base.direction());
    }

    public static float getJawAngle(int index, float partialTick) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        GargoyleState s = STATES[index];
        return Mth.lerp(partialTick, s.prevJaw, s.jaw);
    }

    public static float getWingPhase(int index, float partialTick) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        GargoyleState s = STATES[index];
        return Mth.lerp(partialTick, s.prevWingPhase, s.wingPhase);
    }

    public static float getWingAmp(int index) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.34f;
        return STATES[index].currentWingAmp;
    }

    public static float getRecoil(int index) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        return STATES[index].recoil;
    }

    public static float getLookYaw(int index) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        return STATES[index].lookYaw;
    }

    public static float getLookPitch(int index) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        return STATES[index].lookPitch;
    }

    public static List<Vec3> getTrail(int index) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return List.of();
        return TRAILS[index];
    }

    public static List<WorldCube> getCubes() {
        return CUBES;
    }

    public static void tick(Minecraft client) {
        var player = client.player;
        boolean enabled = "VOID_GARGOYLE".equals(KiemConfig.activeAura) || KiemConfig.voidGargoyleAuraEnabled;
        if (!enabled || player == null || client.level == null
                || player.isInvisible() || player.isSpectator() || player.isSleeping()
                || client.options.getCameraType().isFirstPerson()) {
            clear();
            return;
        }

        int age = player.tickCount;
        if (client.level != level || player.getId() != playerId || age < previousAge) {
            clear();
        }
        level = client.level;
        playerId = player.getId();
        if (age == previousAge) return;
        previousAge = age;

        Vec3 playerPos = player.position();
        Vec3 inertiaDelta = lastPlayerPos != null ? playerPos.subtract(lastPlayerPos).scale(INERTIA_FACTOR) : Vec3.ZERO;
        if (inertiaDelta.lengthSqr() > 16.0) {
            clear();
            lastPlayerPos = playerPos;
            return;
        }
        lastPlayerPos = playerPos;

        boolean sprinting = player.isSprinting() || player.getDeltaMovement().horizontalDistanceSqr() > 0.015;
        boolean gliding = player.isFallFlying();

        // State-driven attack hook: when player swings sword/hand, one dragon lunges & roars
        if (attackCooldown > 0) attackCooldown--;
        if (player.swingTime == 1 && attackCooldown == 0) {
            GargoyleState attacker = STATES[nextAttacker];
            nextAttacker = (nextAttacker + 1) % VoidGargoyleOrbit.COUNT;
            attacker.lungeTick = 0;
            attacker.snarlTick = 0;
            attacker.burstTriggered = false;
            attackCooldown = 14;
        }

        float time = orbitTime(age);

        // Update accumulated wing phases, lunge curves, curious idle look & snarl recoil
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            GargoyleState s = STATES[i];

            // 1) Smoothly interpolate flap speed & amplitude (zero phase-jump!)
            float targetSpeed;
            float targetAmp;
            if (gliding) {
                boolean beatBurst = ((age + i * 27) % 70) < 16;
                targetSpeed = beatBurst ? 0.56f : 0.14f;
                targetAmp = beatBurst ? 0.38f : 0.10f;
            } else if (sprinting) {
                targetSpeed = 0.62f;
                targetAmp = 0.42f;
            } else {
                targetSpeed = 0.34f;
                targetAmp = 0.32f;
            }
            if (s.lungeTick >= 0) {
                targetSpeed = 0.72f;
                targetAmp = 0.46f;
            }
            s.currentFlapSpeed = Mth.lerp(0.15f, s.currentFlapSpeed, targetSpeed);
            s.currentWingAmp = Mth.lerp(0.15f, s.currentWingAmp, targetAmp);
            s.prevWingPhase = s.wingPhase;
            s.wingPhase += s.currentFlapSpeed;

            // 2) Smooth lunge curve (3 ticks forward surge, 10 ticks smooth glide back)
            if (s.lungeTick >= 0) {
                s.lungeTick++;
                if (s.lungeTick <= 3) {
                    s.lungeAmount = Mth.sin((s.lungeTick / 3.0f) * (Mth.PI * 0.5f));
                } else if (s.lungeTick <= 13) {
                    float t = (s.lungeTick - 3) / 10.0f;
                    s.lungeAmount = (1.0f - t) * (1.0f - t);
                } else {
                    s.lungeTick = -1;
                    s.lungeAmount = 0.0f;
                }
            }

            // 3) Curious head look when idle
            float targetLookYaw = (!sprinting && !gliding)
                    ? (float) (Math.sin(age * 0.045 + i * 2.3) * 0.28 + Math.sin(age * 0.11 + i) * 0.08)
                    : 0.0f;
            float targetLookPitch = (!sprinting && !gliding)
                    ? (float) (Math.cos(age * 0.055 + i * 1.7) * 0.14)
                    : 0.0f;
            s.lookYaw = Mth.lerp(0.12f, s.lookYaw, targetLookYaw);
            s.lookPitch = Mth.lerp(0.12f, s.lookPitch, targetLookPitch);

            // 4) Jaw snapping + full-body recoil
            s.prevJaw = s.jaw;
            float targetRecoil = 0.0f;
            if (s.snarlTick >= 0) {
                s.snarlTick++;
                if (s.snarlTick <= 10) {
                    s.jaw = (s.snarlTick / 10.0f) * 0.45f;
                    targetRecoil = -(s.snarlTick / 10.0f) * 0.85f; // pull back / rear up
                } else if (s.snarlTick <= 12) {
                    s.jaw = 0.78f;
                    targetRecoil = 1.0f; // snap head forward on burst
                    if (s.snarlTick == 12 && !s.burstTriggered) {
                        s.burstTriggered = true;
                        spawnMouthFlameBurst(client, playerPos, i, time, age);
                    }
                } else if (s.snarlTick <= 16) {
                    s.jaw = 0.78f;
                    targetRecoil = 0.55f;
                } else if (s.snarlTick <= 19) {
                    s.jaw = (20 - s.snarlTick) * 0.18f;
                    targetRecoil = 0.0f;
                } else {
                    s.jaw = 0.0f;
                    s.snarlTick = -1;
                    s.burstTriggered = false;
                    s.cooldown = nextCooldown();
                }
            } else {
                float idleCycle = (float) Math.sin((age + i * 19) * 0.08f);
                s.jaw = idleCycle > 0.65f ? (idleCycle - 0.65f) * 0.42f : 0.0f;
                s.cooldown--;
                if (s.cooldown <= 0) {
                    s.snarlTick = 0;
                    s.burstTriggered = false;
                }
            }
            s.recoil = Mth.lerp(0.35f, s.recoil, targetRecoil);
        }

        // Update world-space trails with 65% inertia compensation
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            List<Vec3> trail = TRAILS[i];
            if (inertiaDelta.lengthSqr() > 1e-7) {
                for (int k = 0; k < trail.size(); k++) {
                    trail.set(k, trail.get(k).add(inertiaDelta));
                }
            }
            var orbit = sample(time, i);
            float yaw = orbit.yaw();
            Vec3 tailPos = playerPos.add(
                    orbit.x() + Math.sin(yaw) * 0.22,
                    orbit.y(),
                    orbit.z() - Math.cos(yaw) * 0.22);
            trail.add(0, tailPos);
            if (trail.size() > TRAIL_LENGTH) {
                trail.remove(trail.size() - 1);
            }
        }

        Iterator<WorldCube> it = CUBES.iterator();
        while (it.hasNext()) {
            WorldCube c = it.next();
            if (age - c.bornAge >= c.lifetime) {
                it.remove();
            } else {
                c.pos = c.pos.add(inertiaDelta).add(c.vel);
                c.vel = c.vel.scale(0.95);
            }
        }

        spawnSpiralEmberCubes(playerPos, time, age);
        spawnGroundEmberCubes(player, playerPos, age);

        if (CUBES.size() > MAX_CUBES) {
            CUBES.subList(0, CUBES.size() - MAX_CUBES).clear();
        }
    }

    private static int nextCooldown() {
        int rate = KiemConfig.voidGargoyleJawRate;
        return switch (rate) {
            case 0 -> 110 + RANDOM.nextInt(70);
            case 2 -> 35 + RANDOM.nextInt(30);
            default -> 60 + RANDOM.nextInt(45);
        };
    }

    private static void spawnMouthFlameBurst(Minecraft client, Vec3 playerPos, int index, float time, int age) {
        var orbit = sample(time, index);
        float yaw = orbit.yaw();
        Vec3 mouthPos = playerPos.add(
                orbit.x() - Math.sin(yaw) * 0.28,
                orbit.y() + 0.03,
                orbit.z() + Math.cos(yaw) * 0.28);
        double fx = -Math.sin(yaw) * 0.05;
        double fz = Math.cos(yaw) * 0.05;

        for (int k = 0; k < 5; k++) {
            Vec3 vel = new Vec3(
                    fx + (RANDOM.nextDouble() - 0.5) * 0.028,
                    (RANDOM.nextDouble() - 0.3) * 0.022,
                    fz + (RANDOM.nextDouble() - 0.5) * 0.028);
            CUBES.add(new WorldCube(mouthPos, vel, age, 16 + RANDOM.nextInt(8),
                    0.034f + RANDOM.nextFloat() * 0.016f, 0.22f, RANDOM.nextInt(1000), false));
        }
        if (client.level != null && RANDOM.nextFloat() < 0.45f) {
            client.level.addParticle(ParticleTypes.LAVA, mouthPos.x, mouthPos.y, mouthPos.z, 0, 0, 0);
        }
    }

    private static void spawnSpiralEmberCubes(Vec3 playerPos, float time, int age) {
        int density = KiemConfig.voidGargoyleEmbers;
        if (density == 0) return;
        int interval = density == 1 ? 4 : density == 2 ? 2 : 1;
        if (age % interval != 0) return;

        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            var orbit = sample(time, i);
            float yaw = orbit.yaw();
            double spiralAngle = age * 0.55 + i * 2.1;
            double offR = 0.06 + RANDOM.nextDouble() * 0.07;
            Vec3 spawnPos = playerPos.add(
                    orbit.x() + Math.sin(yaw) * 0.24 + Math.cos(spiralAngle) * offR,
                    orbit.y() + Math.sin(spiralAngle) * offR * 0.7,
                    orbit.z() - Math.cos(yaw) * 0.24 + Math.sin(spiralAngle) * offR);
            Vec3 vel = new Vec3(
                    Math.sin(yaw) * 0.012 + (RANDOM.nextDouble() - 0.5) * 0.008,
                    0.004 + (RANDOM.nextDouble() - 0.4) * 0.006,
                    -Math.cos(yaw) * 0.012 + (RANDOM.nextDouble() - 0.5) * 0.008);
            boolean isObsidian = RANDOM.nextFloat() < 0.38f;
            float size = isObsidian ? (0.020f + RANDOM.nextFloat() * 0.014f) : (0.028f + RANDOM.nextFloat() * 0.018f);
            CUBES.add(new WorldCube(spawnPos, vel, age, 20 + RANDOM.nextInt(10), size,
                    0.14f + RANDOM.nextFloat() * 0.14f, RANDOM.nextInt(10000), isObsidian));
        }
    }

    private static void spawnGroundEmberCubes(Player player, Vec3 playerPos, int age) {
        int density = KiemConfig.voidGargoyleGroundSmoke;
        if (density == 0) return;
        boolean onGround = player.onGround() && !player.isInWater() && !player.isInLava() && !player.isPassenger();
        if (!onGround) return;

        Vec3 delta = player.getDeltaMovement();
        boolean moving = (delta.x * delta.x + delta.z * delta.z) > 0.0009;
        int interval = moving
                ? (density == 1 ? 4 : density == 2 ? 2 : 1)
                : (density == 1 ? 10 : density == 2 ? 6 : 3);
        if (age % interval != 0) return;

        double r = 0.14 + RANDOM.nextDouble() * 0.34;
        double a = RANDOM.nextDouble() * Math.PI * 2;
        Vec3 spawnPos = playerPos.add(Math.cos(a) * r, 0.04, Math.sin(a) * r);
        Vec3 vel = new Vec3((RANDOM.nextDouble() - 0.5) * 0.006, 0.012 + RANDOM.nextDouble() * 0.010, (RANDOM.nextDouble() - 0.5) * 0.006);
        boolean isObsidian = RANDOM.nextFloat() < 0.45f;
        CUBES.add(new WorldCube(spawnPos, vel, age, 18 + RANDOM.nextInt(8),
                0.018f + RANDOM.nextFloat() * 0.012f, 0.12f, RANDOM.nextInt(10000), isObsidian));
    }

    private static void clear() {
        previousAge = -1;
        playerId = -1;
        level = null;
        lastPlayerPos = null;
        attackCooldown = 0;
        CLOCK.reset();
        for (List<Vec3> trail : TRAILS) {
            trail.clear();
        }
        CUBES.clear();
        for (int i = 0; i < STATES.length; i++) {
            STATES[i].snarlTick = -1;
            STATES[i].burstTriggered = false;
            STATES[i].jaw = 0.0f;
            STATES[i].prevJaw = 0.0f;
            STATES[i].recoil = 0.0f;
            STATES[i].lungeTick = -1;
            STATES[i].lungeAmount = 0.0f;
            STATES[i].wingPhase = i * 2.094f;
            STATES[i].prevWingPhase = STATES[i].wingPhase;
            STATES[i].cooldown = 20 + i * 40;
        }
    }
}
