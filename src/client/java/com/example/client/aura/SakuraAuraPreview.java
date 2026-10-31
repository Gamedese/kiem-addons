package com.example.client.aura;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** A GUI-owned clock and local trail. Never advances the real player's aura manager. */
public final class SakuraAuraPreview {
    private static final int MAX_PETALS = 66;
    private final long started = System.nanoTime();
    private final SakuraOrbitClock clock = new SakuraOrbitClock();
    private final List<SakuraAuraManager.Petal> petals = new ArrayList<>();
    private final List<SakuraAuraManager.GroundFlower> groundFlowers = new ArrayList<>();
    private int lastTick = -1;
    private int lastFlowerTick = -100;
    private int lastGroundBloomSetting = -1;
    private int previewStepCount = 0;

    /** Immutable values for a queued render; lists do not refer to the mutable config arrays. */
    public record Settings(float scale, float speed, float bob, int density, int groundBloom,
                           List<Float> heights, List<Float> radii) {
        public Settings(float scale, float speed, float bob, int density,
                        List<Float> heights, List<Float> radii) {
            this(scale, speed, bob, density, 3, heights, radii);
        }

        public Settings {
            heights = List.copyOf(heights);
            radii = List.copyOf(radii);
        }

        public static Settings capture() {
            return new Settings(KiemConfig.sakuraAuraScale, KiemConfig.sakuraAuraSpeed,
                    KiemConfig.sakuraAuraBob, KiemConfig.sakuraAuraPetals, KiemConfig.sakuraAuraGroundBloom,
                    List.of(KiemConfig.sakuraAuraHeights[0], KiemConfig.sakuraAuraHeights[1], KiemConfig.sakuraAuraHeights[2]),
                    List.of(KiemConfig.sakuraAuraRadii[0], KiemConfig.sakuraAuraRadii[1], KiemConfig.sakuraAuraRadii[2]));
        }

        public SakuraOrbit.Sample sample(float time, int index) {
            return SakuraOrbit.sample(time, index, heights.get(index), radii.get(index), bob);
        }
    }

    public record Frame(float age, float time, Settings settings, List<SakuraAuraManager.Petal> petals,
                        List<SakuraAuraManager.GroundFlower> groundBloom) {
        public Frame(float age, float time, Settings settings, List<SakuraAuraManager.Petal> petals) {
            this(age, time, settings, petals, List.of());
        }

        public Frame {
            petals = List.copyOf(petals);
            groundBloom = List.copyOf(groundBloom);
        }
    }

    private Frame frame(Settings settings) {
        float age = (System.nanoTime() - started) / 50_000_000f;
        int tick = (int) age;
        clock.setSpeed(settings.speed());
        petals.removeIf(p -> age - p.born() >= SakuraAuraManager.PETAL_LIFETIME);
        if (settings.density() == 0) petals.clear();
        int interval = settings.density() == 1 ? 4 : settings.density() == 2 ? 2 : 1;
        // Catch up only a lifetime, even if the screen was hidden or the app stalled.
        int first = Math.max(lastTick + 1, tick - SakuraAuraManager.PETAL_LIFETIME + 1);
        for (int t = first; t <= tick; t++) {
            float time = clock.sample(t);
            if (settings.density() > 0 && t % interval == 0) {
                for (int i = 0; i < SakuraOrbit.COUNT; i++) {
                    var orbit = settings.sample(time, i);
                    petals.add(new SakuraAuraManager.Petal(new Vec3(orbit.x(), orbit.y(), orbit.z()), t, t * 3 + i));
                }
            }
        }
        lastTick = tick;
        if (petals.size() > MAX_PETALS) petals.subList(0, petals.size() - MAX_PETALS).clear();

        // Ground Flowers Preview:
        int bloomSetting = settings.groundBloom();
        groundFlowers.removeIf(f -> age - f.born() >= SakuraAuraManager.GROUND_FLOWER_LIFETIME);
        if (bloomSetting == 0) {
            groundFlowers.clear();
            lastGroundBloomSetting = 0;
        } else {
            // Setting changed or first run: immediately seed active blooms
            if (bloomSetting != lastGroundBloomSetting) {
                groundFlowers.clear();
                lastGroundBloomSetting = bloomSetting;
                previewStepCount++;
                // Seed flower cluster that is already in full bloom (born 8 ticks ago)
                spawnPreviewFlowers(tick - SakuraAuraManager.GROUND_FLOWER_BLOOM_TICKS - 2, bloomSetting, previewStepCount);
                lastFlowerTick = tick;
            } else if (tick - lastFlowerTick >= 22 || groundFlowers.isEmpty()) {
                // Periodically spawn overlapping fresh blooming clusters
                previewStepCount++;
                spawnPreviewFlowers(tick, bloomSetting, previewStepCount);
                lastFlowerTick = tick;
            }
        }

        return new Frame(age, clock.sample(age), settings, petals, groundFlowers);
    }

    private void spawnPreviewFlowers(int born, int density, int step) {
        int count;
        if (density == 1) {
            count = (step % 2 == 0) ? 2 : 1;
        } else if (density == 2) {
            count = (step % 2 == 0) ? 4 : 3;
        } else {
            int cycle = Math.abs(step * 7 + born) % 3;
            count = 5 + cycle; // 5, 6, 7
        }

        double baseAngle = ((born * 41 + step * 73) % 360) * (Math.PI / 180.0);

        for (int i = 0; i < count; i++) {
            double offsetX;
            double offsetZ;

            if (density == 1) {
                if (count == 1) {
                    float sideSign = (step % 2 == 0) ? 1.0f : -1.0f;
                    double footDist = 0.19;
                    offsetX = footDist * sideSign;
                    offsetZ = 0.02 * sideSign;
                } else {
                    float sign = (i == 0) ? 1.0f : -1.0f;
                    double footDist = 0.20 + (i * 0.03);
                    offsetX = footDist * sign;
                    offsetZ = (i == 0 ? 0.06 : -0.05);
                }
            } else if (density == 2) {
                double angleStep = (2.0 * Math.PI) / count;
                double angle = baseAngle + i * angleStep;
                double r = 0.26 + ((i % 2 == 0) ? 0.07 : 0.02);
                offsetX = Math.cos(angle) * r;
                offsetZ = Math.sin(angle) * r;
            } else {
                if (count == 7 && i == 6) {
                    double r = 0.09;
                    double angle = baseAngle + Math.PI * 0.25;
                    offsetX = Math.cos(angle) * r;
                    offsetZ = Math.sin(angle) * r;
                } else {
                    int ringCount = (count == 7) ? 6 : count;
                    double angleStep = (2.0 * Math.PI) / ringCount;
                    double angle = baseAngle + i * angleStep;
                    double r = (i % 2 == 0) ? 0.36 : 0.24;
                    offsetX = Math.cos(angle) * r;
                    offsetZ = Math.sin(angle) * r;
                }
            }

            int seed = born * 79 + step * 151 + i * 67;
            float yaw = (float) ((Math.abs(seed * 37) % 360) * (Math.PI / 180.0));
            float scale = 0.92f + ((float) (Math.abs(seed * 23) % 25) / 100.0f);
            double yNudge = 0.015 + (i * 0.001);

            groundFlowers.add(new SakuraAuraManager.GroundFlower(
                    new Vec3(offsetX, yNudge, offsetZ), born, yaw, scale));
        }
    }

    /** Queue own-player plus aura in one native depth-tested PiP. Angles are degrees. */
    public void extract(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float yaw, float pitch) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || x1 <= x0 || y1 <= y0) return;
        var renderer = client.getEntityRenderDispatcher().getRenderer(client.player);
        var fresh = renderer.createRenderState(client.player, 1f);
        if (!(fresh instanceof AvatarRenderState avatar)) return;
        Settings settings = Settings.capture();
        Frame frame = frame(settings);
        ((SakuraPreviewState) (Object) avatar).kiem$setSakuraPreview(frame);
        // Only this fresh GUI state changes; preserve age for native cape/pet animation timers.
        avatar.x = avatar.y = avatar.z = 0;
        avatar.passengerOffset = null;
        avatar.shadowPieces.clear();
        avatar.outlineColor = 0;
        avatar.bodyRot = 180;
        avatar.yRot = 0;
        avatar.xRot = 0;

        double radius = 0.6;
        double minY = -0.15, maxY = Math.max(2.0, avatar.boundingBoxHeight + 0.2);
        for (int i = 0; i < SakuraOrbit.COUNT; i++) {
            double extent = settings.bob() * 0.1 + settings.scale() * 0.25;
            radius = Math.max(radius, settings.radii().get(i) + settings.scale() * 0.25 + 0.16);
            minY = Math.min(minY, settings.heights().get(i) - extent - 0.20);
            maxY = Math.max(maxY, settings.heights().get(i) + extent);
        }
        // Slider changes do not move already-born petals. Fit their old local positions too.
        for (var petal : frame.petals()) {
            radius = Math.max(radius, Math.hypot(petal.position().x, petal.position().z) + 0.20);
            minY = Math.min(minY, petal.position().y - 0.27);
            maxY = Math.max(maxY, petal.position().y + 0.07);
        }
        for (var flower : frame.groundBloom()) {
            radius = Math.max(radius, Math.hypot(flower.position().x, flower.position().z) + 0.20);
        }
        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);
        Quaternionf view = new Quaternionf().rotationX(pitchRadians).rotateY(yawRadians);
        Quaternionf rotation = new Quaternionf().rotationZ((float) Math.PI).mul(view);
        // Translate opposite the rotated scene center; native Z=PI makes vanilla Y translation positive.
        Vector3f translation = rotation.transform(new Vector3f(0, (float) (-(minY + maxY) * 0.5), 0));
        double projectedHalfHeight = (maxY - minY) * 0.5 * Math.abs(Math.cos(pitchRadians))
                + radius * Math.abs(Math.sin(pitchRadians));
        float scale = (float) Math.max(1, Math.min((x1 - x0 - 16) / (2 * radius),
                (y1 - y0 - 16) / (2 * projectedHalfHeight)));
        graphics.entity(avatar, scale, translation, rotation, view, x0, y0, x1, y1);
    }
}
