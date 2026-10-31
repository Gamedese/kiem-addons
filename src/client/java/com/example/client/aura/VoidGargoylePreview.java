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

/** A GUI-owned clock and local trail for Void Gargoyles. Never advances the real player's manager. */
public final class VoidGargoylePreview {
    public static final int EMBER_LIFETIME = 24;
    private static final int MAX_EMBERS = 50;

    private final long started = System.nanoTime();
    private final SakuraOrbitClock clock = new SakuraOrbitClock();
    private final List<PreviewEmber> embers = new ArrayList<>();
    private final PreviewGargoyleState[] states = new PreviewGargoyleState[] {
            new PreviewGargoyleState(15),
            new PreviewGargoyleState(45),
            new PreviewGargoyleState(75)
    };
    private int lastTick = -1;

    public record PreviewEmber(Vec3 position, int born, int seed, boolean isSmoke) { }

    private static final class PreviewGargoyleState {
        int snarlTick = -1;
        int cooldown;
        float jaw = 0.0f;

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

    public record Frame(float age, float time, Settings settings, float[] jawAngles, float[] wingFlaps,
                        List<PreviewEmber> embers) {
        public Frame {
            jawAngles = jawAngles.clone();
            wingFlaps = wingFlaps.clone();
            embers = List.copyOf(embers);
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

        // Advance simulation ticks
        int first = Math.max(lastTick + 1, tick - EMBER_LIFETIME + 1);
        for (int t = first; t <= tick; t++) {
            advanceTick(t, settings);
        }
        lastTick = tick;
        if (embers.size() > MAX_EMBERS) {
            embers.subList(0, embers.size() - MAX_EMBERS).clear();
        }

        float[] jawAngles = new float[VoidGargoyleOrbit.COUNT];
        float[] wingFlaps = new float[VoidGargoyleOrbit.COUNT];
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            jawAngles[i] = states[i].jaw;
            wingFlaps[i] = (float) Math.sin(age * 0.45f + i * 2.094f) * 0.38f;
        }

        return new Frame(age, time, settings, jawAngles, wingFlaps, embers);
    }

    private void advanceTick(int t, Settings settings) {
        float time = clock.sample(t);
        // Snarl state machine
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            PreviewGargoyleState s = states[i];
            if (s.snarlTick >= 0) {
                s.snarlTick++;
                if (s.snarlTick <= 12) {
                    s.jaw = (s.snarlTick / 12.0f) * 0.65f;
                    if (s.snarlTick == 12 && settings.embers() > 0) {
                        // Mouth spark burst in preview
                        var orbit = settings.sample(time, i);
                        double mx = orbit.x() - Math.sin(orbit.yaw()) * 0.22;
                        double my = orbit.y() - 0.04;
                        double mz = orbit.z() + Math.cos(orbit.yaw()) * 0.22;
                        embers.add(new PreviewEmber(new Vec3(mx, my, mz), t, t * 11 + i, false));
                    }
                } else if (s.snarlTick <= 16) {
                    s.jaw = 0.65f;
                } else if (s.snarlTick <= 18) {
                    s.jaw = (19 - s.snarlTick) * 0.15f;
                } else {
                    s.jaw = 0.0f;
                    s.snarlTick = -1;
                    s.cooldown = switch (settings.jawRate()) {
                        case 0 -> 110 + (Math.abs(t * 31 + i * 47) % 60);
                        case 2 -> 35 + (Math.abs(t * 31 + i * 47) % 25);
                        default -> 60 + (Math.abs(t * 31 + i * 47) % 35);
                    };
                }
            } else {
                float idleCycle = (float) Math.sin((t + i * 19) * 0.08f);
                s.jaw = idleCycle > 0.65f ? (idleCycle - 0.65f) * 0.35f : 0.0f;
                s.cooldown--;
                if (s.cooldown <= 0) {
                    s.snarlTick = 0;
                }
            }
        }

        // Aerial embers behind gargoyles
        int emberDensity = settings.embers();
        int interval = emberDensity == 1 ? 4 : emberDensity == 2 ? 2 : 1;
        if (emberDensity > 0 && t % interval == 0) {
            for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
                var orbit = settings.sample(time, i);
                double tx = orbit.x() + Math.sin(orbit.yaw()) * 0.18;
                double ty = orbit.y() + 0.06;
                double tz = orbit.z() - Math.cos(orbit.yaw()) * 0.18;
                embers.add(new PreviewEmber(new Vec3(tx, ty, tz), t, t * 7 + i, false));
            }
        }

        // Ground smoke preview
        int groundDensity = settings.groundSmoke();
        int groundInterval = groundDensity == 1 ? 7 : groundDensity == 2 ? 4 : 2;
        if (groundDensity > 0 && t % groundInterval == 0) {
            double angle = ((t * 67) % 360) * (Math.PI / 180.0);
            double r = 0.12 + ((Math.abs(t * 29) % 22) / 100.0);
            embers.add(new PreviewEmber(new Vec3(Math.cos(angle) * r, 0.02, Math.sin(angle) * r), t, t * 13, true));
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

        double radius = 0.65;
        double minY = -0.15, maxY = Math.max(2.1, avatar.boundingBoxHeight + 0.3);
        for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
            double extent = settings.bob() * 0.12 + settings.scale() * 0.3;
            radius = Math.max(radius, settings.radii().get(i) + settings.scale() * 0.3 + 0.2);
            minY = Math.min(minY, settings.heights().get(i) - extent - 0.25);
            maxY = Math.max(maxY, settings.heights().get(i) + extent);
        }

        for (var ember : frame.embers()) {
            radius = Math.max(radius, Math.hypot(ember.position().x, ember.position().z) + 0.15);
            minY = Math.min(minY, ember.position().y - 0.2);
            maxY = Math.max(maxY, ember.position().y + 0.1);
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
