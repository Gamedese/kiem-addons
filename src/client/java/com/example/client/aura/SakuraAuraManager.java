package com.example.client.aura;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/** Tick-owned world-space trail. Render submissions only read an immutable snapshot. */
public final class SakuraAuraManager {
    public static final int PETAL_LIFETIME = 22;
    private static final int MAX_PETALS = 66;

    // Ground Bloom Trail settings: ~1.5s staying on ground (30 ticks)
    public static final int GROUND_FLOWER_BLOOM_TICKS = 6;
    public static final int GROUND_FLOWER_STAY_TICKS = 30; // ~1.5 seconds
    public static final int GROUND_FLOWER_SINK_TICKS = 12; // smoothly sinks below ground
    public static final int GROUND_FLOWER_LIFETIME = GROUND_FLOWER_BLOOM_TICKS + GROUND_FLOWER_STAY_TICKS + GROUND_FLOWER_SINK_TICKS; // 48 ticks (~2.4s)
    private static final int MAX_GROUND_FLOWERS = 140;

    private static final SakuraOrbitClock CLOCK = new SakuraOrbitClock();
    private static ClientLevel level;
    private static Vec3 previousPosition;
    private static int previousAge = -1;
    private static int playerId = -1;
    private static final List<Petal> petals = new ArrayList<>();
    private static List<Petal> snapshot = List.of();

    private static final List<GroundFlower> groundFlowers = new ArrayList<>();
    private static List<GroundFlower> groundSnapshot = List.of();
    private static Vec3 lastGroundFlowerPos;
    private static int footstepCount = 0;

    public record Petal(Vec3 position, int born, int seed) { }
    public record GroundFlower(Vec3 position, int born, float yaw, float scale) { }

    private SakuraAuraManager() { }

    public static List<Petal> petals() { return snapshot; }
    public static List<GroundFlower> groundFlowers() { return groundSnapshot; }

    public static float orbitTime(float age) {
        CLOCK.setSpeed(KiemConfig.sakuraAuraSpeed);
        return CLOCK.sample(age);
    }

    public static void setSpeed(float speed) {
        if (!Float.isFinite(speed)) speed = 1;
        speed = Math.max(0.25f, Math.min(2f, speed));
        CLOCK.setSpeed(speed);
        KiemConfig.sakuraAuraSpeed = speed;
    }

    public static SakuraOrbit.Sample sample(float time, int index) {
        return SakuraOrbit.sample(time, index, KiemConfig.sakuraAuraHeights[index],
                KiemConfig.sakuraAuraRadii[index], KiemConfig.sakuraAuraBob);
    }

    public static void tick(Minecraft client) {
        var player = client.player;
        if (!KiemConfig.sakuraAuraEnabled || player == null || client.level == null
                || player.isInvisible() || player.isSpectator() || player.isSleeping()
                || client.options.getCameraType().isFirstPerson()) {
            clear();
            return;
        }
        Vec3 position = player.position();
        int age = player.tickCount;
        if (client.level != level || player.getId() != playerId || age < previousAge
                || (previousPosition != null && previousPosition.distanceToSqr(position) > 16)) {
            clear();
        }
        level = client.level;
        playerId = player.getId();
        Vec3 lastTickPos = previousPosition;
        previousPosition = position;
        if (age == previousAge) return;
        previousAge = age;
        float time = orbitTime(age);
        petals.removeIf(p -> age - p.born() >= PETAL_LIFETIME);
        int density = KiemConfig.sakuraAuraPetals;
        if (density == 0) petals.clear();
        // Low: every 4 ticks; normal: every 2; high: every tick. Always bounded.
        int interval = density == 1 ? 4 : density == 2 ? 2 : 1;
        if (density > 0 && age % interval == 0) {
            double angle = (age * 0.45) + (age % 3) * 2.094;
            double rad = 0.25 + ((age * 17) % 10) * 0.04;
            double py = 0.35 + ((age * 13) % 12) * 0.08;
            petals.add(new Petal(position.add(Math.cos(angle) * rad, py, Math.sin(angle) * rad),
                    age, age * 3));
        }
        if (petals.size() > MAX_PETALS) petals.subList(0, petals.size() - MAX_PETALS).clear();
        snapshot = List.copyOf(petals);

        // Ground Flower Trail: Karada ayak basılan yerde (r ~ 0.20 - 0.42m dairesel gölge alanı boyunca)
        int bloomDensity = KiemConfig.sakuraAuraGroundBloom;
        if (bloomDensity == 0) {
            groundFlowers.clear();
        } else {
            boolean onGround = player.onGround() && !player.isInWater() && !player.isInLava() && !player.isPassenger();
            if (onGround) {
                double movedDistSqr = (lastTickPos != null) ?
                        (position.x - lastTickPos.x) * (position.x - lastTickPos.x)
                        + (position.z - lastTickPos.z) * (position.z - lastTickPos.z) : 0.0;
                Vec3 delta = player.getDeltaMovement();
                double horizSpeedSqr = delta.x * delta.x + delta.z * delta.z;
                boolean moving = horizSpeedSqr > 0.0009 || movedDistSqr > 0.0004;

                double rMaxSqr = 0.45 * 0.45; // 0.2025 m^2 (r < 0.45m circle under player)
                if (!moving) {
                    // Oyuncu karada sabit dururken ayaklarının altındaki çiçeklerin yaşlanmasını durdur:
                    int underFeetCount = 0;
                    for (int i = 0; i < groundFlowers.size(); i++) {
                        var f = groundFlowers.get(i);
                        double dx = f.position().x - position.x;
                        double dz = f.position().z - position.z;
                        if (dx * dx + dz * dz <= rMaxSqr) {
                            underFeetCount++;
                            int elapsed = age - f.born();
                            // Çiçek açma aşamasını (0..6 tick) tamamlamışsa tam açık (Phase 2) durumunda dondur
                            if (elapsed >= GROUND_FLOWER_BLOOM_TICKS) {
                                int sustainedBorn = age - GROUND_FLOWER_BLOOM_TICKS - 4;
                                if (f.born() != sustainedBorn) {
                                    groundFlowers.set(i, new GroundFlower(f.position(), sustainedBorn, f.yaw(), f.scale()));
                                }
                            }
                        }
                    }

                    // Eğer durduğumuzda altımızda hiç çiçek yoksa o gölge çemberini bir kez çiçekle doldur:
                    if (underFeetCount == 0) {
                        footstepCount++;
                        spawnGroundBloomCluster(player, position, age, bloomDensity);
                        lastGroundFlowerPos = position;
                    }
                } else {
                    // Oyuncu hareket ediyor: ayak basılan yerde çiçek kümesi üret
                    double distSqr = lastGroundFlowerPos == null ? 100 :
                            (position.x - lastGroundFlowerPos.x) * (position.x - lastGroundFlowerPos.x)
                            + (position.z - lastGroundFlowerPos.z) * (position.z - lastGroundFlowerPos.z);
                    // Spawn approximately every ~0.42m of footstep movement
                    if (distSqr >= 0.18) {
                        footstepCount++;
                        spawnGroundBloomCluster(player, position, age, bloomDensity);
                        lastGroundFlowerPos = position;
                    }
                }
            }
            // Yaşlanan çiçekleri temizle (dururken altımızdaki çiçeklerin born tick'i yenilendiği için silinmezler)
            groundFlowers.removeIf(f -> age - f.born() >= GROUND_FLOWER_LIFETIME);
        }
        if (groundFlowers.size() > MAX_GROUND_FLOWERS) {
            groundFlowers.subList(0, groundFlowers.size() - MAX_GROUND_FLOWERS).clear();
        }
        groundSnapshot = List.copyOf(groundFlowers);
    }

    private static void spawnGroundBloomCluster(Player player, Vec3 position, int age, int density) {
        int flowerCount;
        if (density == 1) {
            // Düşük (1): 1-2 çiçek (hafif ayak izi)
            flowerCount = (footstepCount % 2 == 0) ? 2 : 1;
        } else if (density == 2) {
            // Normal (2): 3-4 çiçek (daireye dengeli dağılan)
            flowerCount = (footstepCount % 2 == 0) ? 4 : 3;
        } else {
            // Yüksek / Full (3): 5-7 çiçek (gölgenin kapladığı tüm daire boyunca zengin Sakura Çemberi / Çiçek Halısı)
            int cycle = Math.abs(footstepCount * 7 + age) % 3; // 0, 1, 2
            flowerCount = 5 + cycle; // 5, 6, 7
        }

        // Base angle with organic progression per step
        double baseAngle = ((age * 41 + footstepCount * 73) % 360) * (Math.PI / 180.0);
        float bodyYaw = player.getYRot();
        double bodyRad = Math.toRadians(bodyYaw);

        for (int i = 0; i < flowerCount; i++) {
            double offsetX;
            double offsetZ;

            if (density == 1) {
                // Düşük: Ayak izi hizalı dağılım (~0.18 - 0.26m)
                if (flowerCount == 1) {
                    float sideSign = (footstepCount % 2 == 0) ? 1.0f : -1.0f;
                    double footDist = 0.18 + ((Math.abs(age * 31 + footstepCount * 17) % 80) / 1000.0);
                    offsetX = Math.cos(bodyRad) * (footDist * sideSign);
                    offsetZ = Math.sin(bodyRad) * (footDist * sideSign);
                } else {
                    float sign = (i == 0) ? 1.0f : -1.0f;
                    double footDist = 0.18 + ((Math.abs(age * 31 + footstepCount * 17 + i * 43) % 70) / 1000.0);
                    double sideX = Math.cos(bodyRad) * (footDist * sign);
                    double sideZ = Math.sin(bodyRad) * (footDist * sign);
                    double forwardDist = (i == 0) ? 0.07 : -0.05;
                    offsetX = sideX - Math.sin(bodyRad) * forwardDist;
                    offsetZ = sideZ + Math.cos(bodyRad) * forwardDist;
                }
            } else if (density == 2) {
                // Normal: Daireye dengeli dağılan 3-4 çiçek (~0.25 - 0.36m yarıçap)
                double angleStep = (2.0 * Math.PI) / flowerCount;
                double angleJitter = (((Math.abs(age * 19 + i * 37) % 20) - 10) / 100.0);
                double angle = baseAngle + i * angleStep + angleJitter;
                double r = 0.25 + ((Math.abs(age * 13 + footstepCount * 29 + i * 47) % 110) / 1000.0);
                offsetX = Math.cos(angle) * r;
                offsetZ = Math.sin(angle) * r;
            } else {
                // Yüksek / Full: Gölgenin tüm dairesi boyunca (r ~ 0.20 - 0.42m) Sakura Çemberi / Çiçek Halısı
                if (flowerCount == 7 && i == 6) {
                    // İç göbek çiçeği
                    double r = 0.08 + ((Math.abs(age * 11 + footstepCount * 41) % 80) / 1000.0); // 0.08 - 0.16m
                    double angle = ((age * 53 + footstepCount * 97) % 360) * (Math.PI / 180.0);
                    offsetX = Math.cos(angle) * r;
                    offsetZ = Math.sin(angle) * r;
                } else {
                    int ringCount = (flowerCount == 7) ? 6 : flowerCount;
                    double angleStep = (2.0 * Math.PI) / ringCount;
                    double angleJitter = (((Math.abs(age * 17 + i * 59) % 24) - 12) / 100.0);
                    double angle = baseAngle + i * angleStep + angleJitter;
                    // Dış gölge halkası (0.34 - 0.42m) ve iç gölge çemberi (0.20 - 0.29m) arasında ardışık dağılım
                    double r;
                    if (i % 2 == 0) {
                        r = 0.34 + ((Math.abs(age * 23 + i * 43) % 80) / 1000.0); // 0.34 - 0.42m
                    } else {
                        r = 0.20 + ((Math.abs(age * 31 + i * 61) % 90) / 1000.0); // 0.20 - 0.29m
                    }
                    offsetX = Math.cos(angle) * r;
                    offsetZ = Math.sin(angle) * r;
                }
            }

            // Doğal rotasyon ve küçük boyut varyasyonu (0.88 - 1.16 scale)
            int seed = age * 79 + footstepCount * 151 + i * 67;
            float flowerYaw = (float) ((Math.abs(seed * 37) % 360) * (Math.PI / 180.0));
            float flowerScale = 0.88f + ((float) (Math.abs(seed * 23) % 28) / 100.0f);

            // Z-fighting önleyici 1mm mikro katman kademesi
            double yNudge = 0.015 + (i * 0.001);
            Vec3 flowerPos = new Vec3(position.x + offsetX, position.y + yNudge, position.z + offsetZ);
            groundFlowers.add(new GroundFlower(flowerPos, age, flowerYaw, flowerScale));
        }
    }

    private static void clear() {
        petals.clear();
        snapshot = List.of();
        groundFlowers.clear();
        groundSnapshot = List.of();
        lastGroundFlowerPos = null;
        footstepCount = 0;
        previousPosition = null;
        previousAge = -1;
        playerId = -1;
        level = null;
        CLOCK.reset();
    }
}
