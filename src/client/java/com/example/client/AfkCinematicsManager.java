package com.example.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AfkCinematicsManager {

    // AFK State
    private static long lastInputTimeMillis = System.currentTimeMillis();
    private static boolean afkActive = false;
    private static long afkStartTimeMillis = 0;

    // Test countdown state (/kiemafkc command)
    private static int countdownTicks = -1;

    // Camera animation state
    private static float currentAngleRad = 0f;
    private static double smoothedCamX = 0;
    private static double smoothedCamY = 0;
    private static double smoothedCamZ = 0;
    private static float currentFovZoom = 1.0f; // Target ~1.5x zoom (equivalent to 45-50 FOV)
    private static long lastNanoTime = 0;
    private static boolean initialized = false;

    // Last known player position for movement check
    private static double lastPlayerX = 0;
    private static double lastPlayerY = 0;
    private static double lastPlayerZ = 0;

    private static long countdownStartTimeMillis = 0;

    private static void sendFeedback(String text) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        Component comp = Component.literal(text);
        KiemCompat.setOverlayMessage(mc, comp, false);
    }

    public static void onUserInput(String source) {
        // Ignore input for the first 350ms after triggering countdown
        // to prevent the Enter key release of /kiemafkc or chat closing mouse re-grab from cancelling it
        if (System.currentTimeMillis() - countdownStartTimeMillis < 350) {
            return;
        }

        lastInputTimeMillis = System.currentTimeMillis();
        if (afkActive) {
            stopAfk("Kullanıcı girdisi (" + source + ")");
        }
        if (countdownTicks >= 0) {
            countdownTicks = -1;
            sendFeedback("§c[Kiem] AFK geri sayımı iptal edildi (" + source + ").");
        }
    }

    public static void onUserInput() {
        onUserInput("bilinmeyen");
    }

    public static void startCountdown() {
        countdownStartTimeMillis = System.currentTimeMillis();
        countdownTicks = 60; // 3 seconds at 20 tps
        sendFeedback("§e[Kiem] AFK Sinematiği 3 saniye içinde başlıyor...");
    }

    public static void clientTick(Minecraft mc) {
        if (!KiemConfig.afkCinematicsEnabled) {
            if (afkActive) stopAfk("Ayar kapatıldı");
            countdownTicks = -1;
            return;
        }

        // Check if player moved in world (e.g. falling, walking, being pushed)
        if (mc.player != null) {
            double px = mc.player.getX();
            double py = mc.player.getY();
            double pz = mc.player.getZ();
            double distSq = (px - lastPlayerX) * (px - lastPlayerX) +
                    (py - lastPlayerY) * (py - lastPlayerY) +
                    (pz - lastPlayerZ) * (pz - lastPlayerZ);
            if (distSq > 0.04) { // Moved more than 0.2 blocks
                if (afkActive) {
                    stopAfk("Oyuncu hareket etti");
                }
                lastInputTimeMillis = System.currentTimeMillis();
            }
            lastPlayerX = px;
            lastPlayerY = py;
            lastPlayerZ = pz;
        }

        // Handle test countdown
        if (countdownTicks > 0) {
            countdownTicks--;
            if (countdownTicks % 20 == 0) {
                int secs = countdownTicks / 20;
                sendFeedback("§6[Kiem] AFK Sinematiği: §f" + secs + "s...");
            }
            if (countdownTicks == 0) {
                countdownTicks = -1;
                startAfk();
            }
            return;
        }

        // Inactivity trigger: default 120 seconds (afkCinematicsSeconds)
        if (!afkActive && mc.player != null && mc.level != null && KiemCompat.getScreen(mc) == null) {
            long idleMs = System.currentTimeMillis() - lastInputTimeMillis;
            long requiredMs = (long) KiemConfig.afkCinematicsSeconds * 1000L;
            if (idleMs >= requiredMs) {
                startAfk();
            }
        }
    }

    public static void startAfk() {
        afkActive = true;
        afkStartTimeMillis = System.currentTimeMillis();
        initialized = false;
        trackStart = null;
        trackEnd = null;
        trackTarget = null;
        shotProgress = 0.0;
        lastNanoTime = 0;
        sendFeedback("§a[Kiem] AFK Sinematik Modu devrede.");
    }

    public static void stopAfk(String reason) {
        if (!afkActive) return;
        afkActive = false;
        initialized = false;
        trackStart = null;
        trackEnd = null;
        trackTarget = null;
        shotProgress = 0.0;
        sendFeedback("§7[Kiem] Sinematik moddan çıkıldı.");
    }

    public static boolean isAfkActive() {
        return afkActive;
    }

    /**
     * Updates and overrides camera transform during AFK:
     * - Low camera height (0.45 - 0.6 blocks above ground)
     * - Close/medium distance (2.2 blocks)
     * - Smooth slow orbit around player
     * - Upward pitch looking up at the player
     * - Raycast wall check (toggleable via KiemConfig.afkRaycastEnabled)
     */
    // Multi-Shot Director State
    private static int currentShotIndex = 0; // 0 = Rear Pan, 1 = Low Front Hero, 2 = High Overhead Angle
    private static Vec3 trackStart = null;
    private static Vec3 trackEnd = null;
    private static Vec3 trackTarget = null;
    private static double shotProgress = 0.0;
    private static float currentShotFovMultiplier = 1.45f;

    private static void setupShot(int shotIndex, double px, double py, double pz, float bodyYaw, float eyeHeight) {
        currentShotIndex = shotIndex;
        shotProgress = 0.0;

        double baseRad = Math.toRadians(bodyYaw);
        double fwdX = -Math.sin(baseRad);
        double fwdZ = Math.cos(baseRad);
        double rightX = -fwdZ;
        double rightZ = fwdX;

        if (shotIndex == 0) {
            // SHOT 1: Rear/Flank Dolly Slide (Right to Left across the back)
            // Duration: 6.5s, Low angle (0.42b), Telephoto (1.45x)
            currentShotFovMultiplier = 1.45f;
            trackTarget = new Vec3(px, py + (eyeHeight * 0.70), pz);

            double camY = py + 0.42;
            double startDistBack = 2.3;
            double startDistSide = 1.9;
            trackStart = new Vec3(
                    px - (fwdX * startDistBack) + (rightX * startDistSide),
                    camY,
                    pz - (fwdZ * startDistBack) + (rightZ * startDistSide)
            );

            double endDistBack = 2.4;
            double endDistSide = -1.9;
            trackEnd = new Vec3(
                    px - (fwdX * endDistBack) + (rightX * endDistSide),
                    camY + 0.12,
                    pz - (fwdZ * endDistBack) + (rightZ * endDistSide)
            );
        } else if (shotIndex == 1) {
            // SHOT 2: Front Low-Angle Hero Shot (Clip 2: looking straight/low at character with slow zoom-in)
            // Duration: 6.0s, Low angle (0.38b), Medium Telephoto (1.35x)
            currentShotFovMultiplier = 1.35f;
            trackTarget = new Vec3(px, py + (eyeHeight * 0.75), pz);

            double camY = py + 0.38;
            double startDistFront = 3.2;
            double endDistFront = 2.4;
            // Slowly pushing in from front slightly left to center
            trackStart = new Vec3(
                    px + (fwdX * startDistFront) - (rightX * 0.5),
                    camY,
                    pz + (fwdZ * startDistFront) - (rightZ * 0.5)
            );
            trackEnd = new Vec3(
                    px + (fwdX * endDistFront) + (rightX * 0.3),
                    camY + 0.08,
                    pz + (fwdZ * endDistFront) + (rightZ * 0.3)
            );
        } else {
            // SHOT 3: High Angle Overhead Pan (Clip 3: top-down 45 degree looking down at character)
            // Duration: 6.0s, High angle (+2.4b height), Wide-Medium (1.20x)
            currentShotFovMultiplier = 1.20f;
            trackTarget = new Vec3(px, py + (eyeHeight * 0.5), pz);

            double camY = py + 2.2;
            // Drifting from high-front-left to high-front-right
            trackStart = new Vec3(
                    px + (fwdX * 2.5) - (rightX * 1.5),
                    camY,
                    pz + (fwdZ * 2.5) - (rightZ * 1.5)
            );
            trackEnd = new Vec3(
                    px + (fwdX * 2.2) + (rightX * 1.4),
                    camY - 0.2,
                    pz + (fwdZ * 2.2) + (rightZ * 1.4)
            );
        }

        smoothedCamX = trackStart.x;
        smoothedCamY = trackStart.y;
        smoothedCamZ = trackStart.z;
        initialized = true;
    }

    public static void updateCamera(Camera camera, Level level, Entity entity, float partialTicks,
                                    java.util.function.BiConsumer<Float, Float> setRotFunc,
                                    java.util.function.Consumer<Vec3> setPosFunc) {
        if (!afkActive || entity == null) return;

        long now = System.nanoTime();
        float dt = (lastNanoTime == 0) ? 0.0166f : (float) ((now - lastNanoTime) / 1_000_000_000.0);
        lastNanoTime = now;
        dt = Mth.clamp(dt, 0.001f, 0.1f);

        // Player interpolated position
        double px = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double py = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double pz = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        float bodyYaw = entity.getYRot();
        float eyeHeight = entity.getEyeHeight();

        // Initialize first shot
        if (!initialized || trackStart == null) {
            setupShot(0, px, py, pz, bodyYaw, eyeHeight);
        }

        // Shot duration: 6.5s per shot
        double shotDurationSec = 6.5;
        shotProgress += dt / shotDurationSec;

        // Cut / Transition to next shot when current finishes
        if (shotProgress >= 1.0) {
            int nextShot = (currentShotIndex + 1) % 3;
            setupShot(nextShot, px, py, pz, bodyYaw, eyeHeight);
        }

        // Smooth cubic ease-in-out interpolation for cinematic dolly movement
        double t = Mth.clamp(shotProgress, 0.0, 1.0);
        double smoothT = t * t * (3.0 - 2.0 * t); // Smoothstep

        double targetCamX = trackStart.x + (trackEnd.x - trackStart.x) * smoothT;
        double targetCamY = trackStart.y + (trackEnd.y - trackStart.y) * smoothT;
        double targetCamZ = trackStart.z + (trackEnd.z - trackStart.z) * smoothT;

        Vec3 desiredCamPos = new Vec3(targetCamX, targetCamY, targetCamZ);
        Vec3 focusPoint = trackTarget != null ? trackTarget : new Vec3(px, py + 1.2, pz);

        // Raycast collision detection if enabled
        if (KiemConfig.afkRaycastEnabled && level != null) {
            ClipContext clipContext = new ClipContext(
                    focusPoint,
                    desiredCamPos,
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity
            );
            BlockHitResult hit = level.clip(clipContext);
            if (hit.getType() != HitResult.Type.MISS) {
                Vec3 hitPos = hit.getLocation();
                Vec3 hitToFocus = focusPoint.subtract(hitPos).normalize();
                desiredCamPos = hitPos.add(hitToFocus.scale(0.25));
                targetCamX = desiredCamPos.x;
                targetCamY = desiredCamPos.y;
                targetCamZ = desiredCamPos.z;
            }
        }

        // Frame dampening
        float posFactor = 1.0f - (float) Math.exp(-14.0f * dt);
        smoothedCamX += (targetCamX - smoothedCamX) * posFactor;
        smoothedCamY += (targetCamY - smoothedCamY) * posFactor;
        smoothedCamZ += (targetCamZ - smoothedCamZ) * posFactor;

        // Calculate rotation angles to point camera precisely at the player's focal point
        double dx = focusPoint.x - smoothedCamX;
        double dy = focusPoint.y - smoothedCamY;
        double dz = focusPoint.z - smoothedCamZ;
        double horizDist = Math.sqrt(dx * dx + dz * dz);

        float calcYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        float calcPitch = (float) -(Mth.atan2(dy, horizDist) * (180.0 / Math.PI));

        setRotFunc.accept(calcYaw, calcPitch);
        setPosFunc.accept(new Vec3(smoothedCamX, smoothedCamY, smoothedCamZ));
    }

    /**
     * FOV zoom factor during AFK (creates telephoto compression like 45 FOV)
     */
    public static float getFovMultiplier() {
        if (!afkActive) return 1.0f;
        return currentShotFovMultiplier;
    }
}
