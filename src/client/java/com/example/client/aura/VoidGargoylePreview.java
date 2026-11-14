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

/** A GUI-owned clock and local trail/cubes for Void Gargoyles. Never advances the real player's manager. */
public final class VoidGargoylePreview {
    public static final int EMBER_LIFETIME = 24;
    private static final int MAX_EMBERS = 64;
    private static final int TRAIL_SAMPLES = 24;

    private final long started = System.nanoTime();
    private final SakuraOrbitClock clock = new SakuraOrbitClock();
    private final List<PreviewEmber> embers = new ArrayList<>();
    private final PreviewGargoyleState[] states = new PreviewGargoyleState[] {
            new PreviewGargoyleState(15),
            new PreviewGargoyleState(45),
            new PreviewGargoyleState(75)
    };
    private int lastTick = -1;

    public record PreviewEmber(Vec3 position, int born, int seed, boolean isObsidian) { }

    private static final class PreviewGargoyleState {
        int snarlTick = -1;
        int cooldown;
        float jaw = 0.0f;
        float recoil = 0.0f;

        PreviewGargoyleState(int initialCooldown) {
            this.cooldown = initialCooldown;
        }
    }

    public record Settings(float scale, float speed, float bob, int embers, int groundSmoke, int jawRate,
                           List<Float> heights, List<Float> radii) {
        public Settings {
            heights = List.copyOf(heights);
            radii = List.copyOf(radii);
        }

        public static Settings capture() {
            return new Settings(KiemConfig.voidGargoyleScale, KiemConfig.voidGargoyleSpeed,
                    KiemConfig.voidGargoyleBob, KiemConfig.voidGargoyleEmbers,
                    KiemConfig.voidGargoyleGroundSmoke, KiemConfig.voidGargoyleJawRate,
                    List.of(KiemConfig.voidGargoyleHeights[0], KiemConfig.voidGargoyleHeights[1], KiemConfig.voidGargoyleHeights[2]),
                    List.of(KiemConfig.voidGargoyleRadii[0], KiemConfig.voidGargoyleRadii[1], KiemConfig.voidGargoyleRadii[2]));
        }

        public VoidGargoyleOrbit.Sample sample(float time, int index) {
            return VoidGargoyleOrbit.sample(time, index, heights.get(index), radii.get(index), bob);
        }
    }

    public record Frame(float age, float time, Settings settings, float[] jawAngles, float[] wingPhases,
                        float[] recoils, List<PreviewEmber> embers, List<List<Vec3>> trails) {
        public Frame {
            jawAngles = jawAngles.clone();
            wingPhases = wingPhases.clone();
            recoils = recoils.clone();
            embers = List.copyOf(embers);
            trails = List.copyOf(trails);
        }
    }

    private Frame frame(Settings settings) {
        float age = (System.nanoTime() - started) / 50_000_000f;
        int tick = (int) age;
        clock.setSpeed(settings.speed());
        float time = clock.sample(age);

        embers.removeIf(e -> age - e.born() >= EMBER_LIFETIME);
        if (settings.embers() == 0 && settings.groundSmoke() == 0) {
            embers.clear();
        }

        int first = Math.max(lastTick + 1, tick - EMBER_LIFETIME + 1);
        for (int t = first; t <= tick; t++) {
            advanceTick(t, settings);
        }
        lastTick = tick;
        if (embers.size() > MAX_EMBERS) {
            embers.subList(0, embers.size() - MAX_EMBERS).clear();
        }

        float[] jawAngles = new float[VoidGargoyleOrbit.COUNT];
        float[] wingPhases = new float[VoidGargoyleOrbit.COUNT];
        float[] recoils = new float[VoidGargoyleOrbit.COUNT];
        List<List<Vec3>> trails = new ArrayList<>(VoidGargoyleOrbit.COUNT);
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            jawAngles[i] = states[i].jaw;
            wingPhases[i] = age * 0.36f + i * 2.094f;
            recoils[i] = states[i].recoil;

            List<Vec3> trail = new ArrayList<>(TRAIL_SAMPLES);
            if (settings.embers() > 0) {
                for (int k = 0; k < TRAIL_SAMPLES; k++) {
                    float tSample = time - k * 0.85f * settings.speed();
                    var orb = settings.sample(tSample, i);
                    float yaw = orb.yaw();
                    trail.add(new Vec3(
                            orb.x() + Math.sin(yaw) * 0.22,
                            orb.y(),
                            orb.z() - Math.cos(yaw) * 0.22));
                }
            }
            trails.add(List.copyOf(trail));
        }

        return new Frame(age, time, settings, jawAngles, wingPhases, recoils, embers, trails);
    }

    private void advanceTick(int t, Settings settings) {
        float time = clock.sample(t);
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            PreviewGargoyleState s = states[i];
            if (s.snarlTick >= 0) {
                s.snarlTick++;
                if (s.snarlTick <= 10) {
                    s.jaw = (s.snarlTick / 10.0f) * 0.45f;
                    s.recoil = -(s.snarlTick / 10.0f) * 0.85f;
                } else if (s.snarlTick <= 12) {
                    s.jaw = 0.78f;
                    s.recoil = 1.0f;
                    if (s.snarlTick == 12 && settings.embers() > 0) {
                        var orbit = settings.sample(time, i);
                        double mx = orbit.x() - Math.sin(orbit.yaw()) * 0.26;
                        double my = orbit.y() - 0.02;
                        double mz = orbit.z() + Math.cos(orbit.yaw()) * 0.26;
                        embers.add(new PreviewEmber(new Vec3(mx, my, mz), t, t * 11 + i, false));
                    }
                } else if (s.snarlTick <= 16) {
                    s.jaw = 0.78f;
                    s.recoil = 0.55f;
                } else if (s.snarlTick <= 19) {
                    s.jaw = (20 - s.snarlTick) * 0.18f;
                    s.recoil = 0.0f;
                } else {
                    s.jaw = 0.0f;
                    s.recoil = 0.0f;
                    s.snarlTick = -1;
                    s.cooldown = switch (settings.jawRate()) {
                        case 0 -> 110 + (Math.abs(t * 31 + i * 47) % 60);
                        case 2 -> 35 + (Math.abs(t * 31 + i * 47) % 25);
                        default -> 60 + (Math.abs(t * 31 + i * 47) % 35);
                    };
                }
            } else {
                float idleCycle = (float) Math.sin((t + i * 19) * 0.08f);
                s.jaw = idleCycle > 0.65f ? (idleCycle - 0.65f) * 0.42f : 0.0f;
                s.recoil = 0.0f;
                s.cooldown--;
                if (s.cooldown <= 0) {
                    s.snarlTick = 0;
                }
            }
        }

        int emberDensity = settings.embers();
        int interval = emberDensity == 1 ? 4 : emberDensity == 2 ? 2 : 1;
        if (emberDensity > 0 && t % interval == 0) {
            for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
                var orbit = settings.sample(time, i);
                double spiral = t * 0.55 + i * 2.1;
                double tx = orbit.x() + Math.sin(orbit.yaw()) * 0.24 + Math.cos(spiral) * 0.08;
                double ty = orbit.y() + Math.sin(spiral) * 0.05;
                double tz = orbit.z() - Math.cos(orbit.yaw()) * 0.24 + Math.sin(spiral) * 0.08;
                boolean isObsidian = (Math.abs(t * 7 + i * 13) % 5) < 2;
                embers.add(new PreviewEmber(new Vec3(tx, ty, tz), t, t * 7 + i, isObsidian));
            }
        }

        int groundDensity = settings.groundSmoke();
        int groundInterval = groundDensity == 1 ? 8 : groundDensity == 2 ? 5 : 3;
        if (groundDensity > 0 && t % groundInterval == 0) {
            double angle = ((t * 67) % 360) * (Math.PI / 180.0);
            double r = 0.14 + ((Math.abs(t * 29) % 26) / 100.0);
            boolean isObsidian = (t % 3) == 0;
            embers.add(new PreviewEmber(new Vec3(Math.cos(angle) * r, 0.04, Math.sin(angle) * r), t, t * 13, isObsidian));
        }
    }

    /** Queue own-player plus gargoyle aura in one native depth-tested PiP. Angles are degrees. */
    public void extract(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float yaw, float pitch) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || x1 <= x0 || y1 <= y0) return;
        var renderer = client.getEntityRenderDispatcher().getRenderer(client.player);
        var fresh = renderer.createRenderState(client.player, 1f);
        if (!(fresh instanceof AvatarRenderState avatar)) return;

        Settings settings = Settings.capture();
        Frame frame = frame(settings);
        ((SakuraPreviewState) (Object) avatar).kiem$setGargoylePreview(frame);

        avatar.x = avatar.y = avatar.z = 0;
        avatar.passengerOffset = null;
        avatar.shadowPieces.clear();
        avatar.outlineColor = 0;
        avatar.bodyRot = 180;
        avatar.yRot = 0;
        avatar.xRot = 0;

        double radius = 0.75;
        double minY = -0.15, maxY = Math.max(2.1, avatar.boundingBoxHeight + 0.35);
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            double extent = settings.bob() * 0.14 + settings.scale() * 0.52;
            radius = Math.max(radius, settings.radii().get(i) + settings.scale() * 0.55 + 0.25);
            minY = Math.min(minY, settings.heights().get(i) - extent - 0.25);
            maxY = Math.max(maxY, settings.heights().get(i) + extent);
        }

        for (var ember : frame.embers()) {
            radius = Math.max(radius, Math.hypot(ember.position().x, ember.position().z) + 0.18);
            minY = Math.min(minY, ember.position().y - 0.2);
            maxY = Math.max(maxY, ember.position().y + 0.15);
        }

        float pitchRadians = (float) Math.toRadians(pitch);
        float yawRadians = (float) Math.toRadians(yaw);
        Quaternionf view = new Quaternionf().rotationX(pitchRadians).rotateY(yawRadians);
        Quaternionf rotation = new Quaternionf().rotationZ((float) Math.PI).mul(view);
        Vector3f translation = rotation.transform(new Vector3f(0, (float) (-(minY + maxY) * 0.5), 0));
        double projectedHalfHeight = (maxY - minY) * 0.5 * Math.abs(Math.cos(pitchRadians))
                + radius * Math.abs(Math.sin(pitchRadians));
        float scale = (float) Math.max(1, Math.min((x1 - x0 - 16) / (2 * radius),
                (y1 - y0 - 16) / (2 * projectedHalfHeight)));
        graphics.entity(avatar, scale, translation, rotation, view, x0, y0, x1, y1);
    }
}
