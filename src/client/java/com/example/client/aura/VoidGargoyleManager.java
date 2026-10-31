package com.example.client.aura;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/** Tick-owned runtime manager for the Obsidian Void Gargoyle Aura. */
public final class VoidGargoyleManager {
    private static final SakuraOrbitClock CLOCK = new SakuraOrbitClock();
    private static final Random RANDOM = new Random();
    private static ClientLevel level;
    private static int previousAge = -1;
    private static int playerId = -1;

    public static final class GargoyleState {
        public int snarlTick = -1; // -1 if idle, 0..18 if snarling
        public int cooldown;
        public boolean burstTriggered = false;
        public float prevJaw = 0.0f;
        public float jaw = 0.0f;

        public GargoyleState(int initialCooldown) {
            this.cooldown = initialCooldown;
        }
    }

    private static final GargoyleState[] STATES = new GargoyleState[] {
            new GargoyleState(25),
            new GargoyleState(65),
            new GargoyleState(105)
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
        return VoidGargoyleOrbit.sample(time, index,
                KiemConfig.voidGargoyleHeights[index],
                KiemConfig.voidGargoyleRadii[index],
                KiemConfig.voidGargoyleBob);
    }

    public static float getJawAngle(int index, float partialTick) {
        if (index < 0 || index >= VoidGargoyleOrbit.COUNT) return 0.0f;
        GargoyleState s = STATES[index];
        return Mth.lerp(partialTick, s.prevJaw, s.jaw);
    }

    public static float getWingFlap(int index, float age, boolean moving) {
        float flapSpeed = moving ? 0.68f : 0.42f;
        float flapAmp = moving ? 0.46f : 0.34f;
        return (float) Math.sin(age * flapSpeed + index * 2.094f) * flapAmp;
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

        float time = orbitTime(age);

        // Update Jaw Snapping State Machine
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            GargoyleState s = STATES[i];
            s.prevJaw = s.jaw;
            if (s.snarlTick >= 0) {
                s.snarlTick++;
                if (s.snarlTick <= 12) {
                    // Smooth ramp open: 0.0 -> 0.65f
                    s.jaw = (s.snarlTick / 12.0f) * 0.65f;
                    if (s.snarlTick == 12 && !s.burstTriggered) {
                        s.burstTriggered = true;
                        spawnMouthFlameBurst(client, player, i, time);
                    }
                } else if (s.snarlTick <= 16) {
                    // Hold open
                    s.jaw = 0.65f;
                } else if (s.snarlTick <= 18) {
                    // Snap shut quickly
                    s.jaw = (19 - s.snarlTick) * 0.15f;
                } else {
                    // Finished snarl
                    s.jaw = 0.0f;
                    s.snarlTick = -1;
                    s.burstTriggered = false;
                    s.cooldown = nextCooldown();
                }
            } else {
                // Idle snarl breathing
                float idleCycle = (float) Math.sin((age + i * 19) * 0.08f);
                s.jaw = idleCycle > 0.65f ? (idleCycle - 0.65f) * 0.35f : 0.0f;
                s.cooldown--;
                if (s.cooldown <= 0) {
                    s.snarlTick = 0;
                    s.burstTriggered = false;
                }
            }
        }

        // Particle effects in world
        spawnAerialEmberTrail(client, player, time, age);
        spawnGroundSmokeAndEmbers(client, player, age);
    }

    private static int nextCooldown() {
        int rate = KiemConfig.voidGargoyleJawRate;
        return switch (rate) {
            case 0 -> 110 + RANDOM.nextInt(70); // 5.5s - 9s (Calm / Rare)
            case 2 -> 35 + RANDOM.nextInt(30);  // 1.75s - 3.25s (Aggressive / Frequent)
            default -> 60 + RANDOM.nextInt(45); // 3s - 5.25s (Normal)
        };
    }

    private static Vec3 getGargoyleWorldPos(Player player, VoidGargoyleOrbit.Sample orbit) {
        return player.position().add(orbit.x(), orbit.y(), orbit.z());
    }

    private static void spawnMouthFlameBurst(Minecraft client, Player player, int index, float time) {
        if (client.level == null) return;
        var orbit = sample(time, index);
        Vec3 pos = getGargoyleWorldPos(player, orbit);
        float yaw = orbit.yaw();
        double mouthX = pos.x - Math.sin(yaw) * 0.22;
        double mouthY = pos.y + 0.05;
        double mouthZ = pos.z + Math.cos(yaw) * 0.22;
        double fx = -Math.sin(yaw) * 0.06;
        double fz = Math.cos(yaw) * 0.06;

        for (int k = 0; k < 3; k++) {
            double jx = (RANDOM.nextDouble() - 0.5) * 0.04;
            double jy = (RANDOM.nextDouble() - 0.5) * 0.03;
            double jz = (RANDOM.nextDouble() - 0.5) * 0.04;
            client.level.addParticle(ParticleTypes.SMALL_FLAME, mouthX, mouthY, mouthZ,
                    fx + jx, 0.01 + jy, fz + jz);
        }
        if (RANDOM.nextFloat() < 0.65f) {
            client.level.addParticle(ParticleTypes.FLAME, mouthX, mouthY, mouthZ, fx * 0.6, 0.015, fz * 0.6);
        }
        if (RANDOM.nextFloat() < 0.35f) {
            client.level.addParticle(ParticleTypes.LAVA, mouthX, mouthY, mouthZ, 0, 0, 0);
        }
    }

    private static void spawnAerialEmberTrail(Minecraft client, Player player, float time, int age) {
        if (client.level == null) return;
        int density = KiemConfig.voidGargoyleEmbers;
        if (density == 0) return;
        int interval = density == 1 ? 4 : density == 2 ? 2 : 1;
        if (age % interval != 0) return;

        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            var orbit = sample(time, i);
            Vec3 pos = getGargoyleWorldPos(player, orbit);
            float yaw = orbit.yaw();
            double tailX = pos.x + Math.sin(yaw) * 0.20;
            double tailY = pos.y + 0.10;
            double tailZ = pos.z - Math.cos(yaw) * 0.20;
            double vx = Math.sin(yaw) * 0.012;
            double vz = -Math.cos(yaw) * 0.012;

            client.level.addParticle(ParticleTypes.SMALL_FLAME, tailX, tailY, tailZ,
                    vx + (RANDOM.nextDouble() - 0.5) * 0.01,
                    0.006 + RANDOM.nextDouble() * 0.008,
                    vz + (RANDOM.nextDouble() - 0.5) * 0.01);
            if (RANDOM.nextFloat() < 0.45f) {
                client.level.addParticle(ParticleTypes.SMOKE, tailX, tailY, tailZ,
                        vx * 0.8, 0.012, vz * 0.8);
            }
        }
    }

    private static void spawnGroundSmokeAndEmbers(Minecraft client, Player player, int age) {
        if (client.level == null) return;
        int density = KiemConfig.voidGargoyleGroundSmoke;
        if (density == 0) return;
        boolean onGround = player.onGround() && !player.isInWater() && !player.isInLava() && !player.isPassenger();
        if (!onGround) return;

        Vec3 delta = player.getDeltaMovement();
        boolean moving = (delta.x * delta.x + delta.z * delta.z) > 0.0009;
        double px = player.getX();
        double py = player.getY() + 0.02;
        double pz = player.getZ();

        if (moving) {
            int interval = density == 1 ? 3 : density == 2 ? 2 : 1;
            if (age % interval == 0) {
                double r = 0.08 + RANDOM.nextDouble() * 0.26;
                double a = RANDOM.nextDouble() * Math.PI * 2;
                double sx = px + Math.cos(a) * r;
                double sz = pz + Math.sin(a) * r;
                client.level.addParticle(ParticleTypes.SMOKE, sx, py, sz, 0, 0.015 + RANDOM.nextDouble() * 0.015, 0);
                if (RANDOM.nextFloat() < (density * 0.32f)) {
                    client.level.addParticle(ParticleTypes.SMALL_FLAME, sx, py, sz,
                            (RANDOM.nextDouble() - 0.5) * 0.01, 0.015, (RANDOM.nextDouble() - 0.5) * 0.01);
                }
            }
        } else {
            int interval = density == 1 ? 9 : density == 2 ? 5 : 3;
            if (age % interval == 0) {
                double r = RANDOM.nextDouble() * 0.35;
                double a = RANDOM.nextDouble() * Math.PI * 2;
                double sx = px + Math.cos(a) * r;
                double sz = pz + Math.sin(a) * r;
                client.level.addParticle(ParticleTypes.SMOKE, sx, py, sz, 0, 0.008 + RANDOM.nextDouble() * 0.008, 0);
                if (RANDOM.nextFloat() < 0.45f) {
                    client.level.addParticle(ParticleTypes.SMALL_FLAME, sx, py, sz,
                            (RANDOM.nextDouble() - 0.5) * 0.006, 0.008, (RANDOM.nextDouble() - 0.5) * 0.006);
                }
            }
        }
    }

    private static void clear() {
        previousAge = -1;
        playerId = -1;
        level = null;
        CLOCK.reset();
        for (int i = 0; i < STATES.length; i++) {
            STATES[i].snarlTick = -1;
            STATES[i].burstTriggered = false;
            STATES[i].jaw = 0.0f;
            STATES[i].prevJaw = 0.0f;
            STATES[i].cooldown = 20 + i * 40;
        }
    }
}
