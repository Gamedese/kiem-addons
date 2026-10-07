package com.example.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BetterThirdPersonManager {

    private static Vec3 smoothedEyePos = null;
    private static float currentYaw = 0f;
    private static float currentPitch = 0f;
    private static float currentShoulder = 0f;
    private static float smoothedSpeedLag = 0f;
    private static double currentCollisionDist = -1;
    private static boolean wasDetached = false;
    private static long lastNanoTime = 0;

    public static void reset() {
        smoothedEyePos = null;
        wasDetached = false;
        lastNanoTime = 0;
        smoothedSpeedLag = 0f;
        currentCollisionDist = -1;
    }

    public static boolean shouldApply(Camera camera, Entity entity) {
        if (!KiemConfig.betterThirdPersonEnabled) return false;
        if (!camera.isDetached()) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType().isMirrored()) return false; // Vanilla front view preserved
        return entity != null;
    }

    public static void updateCamera(Camera camera, Level level, Entity entity, float partialTicks,
                                    java.util.function.BiConsumer<Float, Float> setRotFunc,
                                    java.util.function.Consumer<Vec3> setPosFunc) {
        if (!shouldApply(camera, entity)) {
            if (wasDetached) {
                reset();
            }
            return;
        }

        long now = System.nanoTime();
        float dt = (lastNanoTime == 0) ? 0.0166f : (float) ((now - lastNanoTime) / 1_000_000_000.0);
        lastNanoTime = now;
        dt = Mth.clamp(dt, 0.001f, 0.1f);

        // Player's actual eye position interpolated for this render frame
        double eyeX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double eyeY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + entity.getEyeHeight();
        double eyeZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        Vec3 exactEyePos = new Vec3(eyeX, eyeY, eyeZ);

        float targetYaw = entity.getViewYRot(partialTicks);
        float targetPitch = entity.getViewXRot(partialTicks);

        boolean isFlying = (entity instanceof net.minecraft.world.entity.LivingEntity living) && living.isFallFlying();

        // Check if we just enabled detached mode or teleported
        if (!wasDetached || smoothedEyePos == null || smoothedEyePos.distanceToSqr(exactEyePos) > 64.0) {
            smoothedEyePos = exactEyePos;
            currentYaw = targetYaw;
            currentPitch = targetPitch;
            currentShoulder = KiemConfig.shoulderSurfingEnabled ? KiemConfig.shoulderSide : 0f;
            smoothedSpeedLag = 0f;
            currentCollisionDist = KiemConfig.thirdPersonDistance;
            wasDetached = true;
        }

        // --- 1. ROTATION INERTIA & SOFT MOVEMENT ---
        float smoothness = Mth.clamp(KiemConfig.thirdPersonSmoothness, 0.0f, 1.0f);
        if (smoothness <= 0.01f) {
            currentYaw = targetYaw;
            currentPitch = targetPitch;
        } else {
            // Half-life inspired exponential decay
            float rotDecay = 28.0f - (smoothness * 22.0f); // 28 (snappy) to 6 (cinematic inertia)
            float rotLerp = 1.0f - (float) Math.exp(-rotDecay * dt);

            float deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw);
            currentYaw += deltaYaw * rotLerp;

            float deltaPitch = targetPitch - currentPitch;
            currentPitch += deltaPitch * rotLerp;
        }

        // --- 2. POSITION SMOOTHING (SEPARATING HORIZONTAL & VERTICAL) ---
        if (smoothness <= 0.01f) {
            smoothedEyePos = exactEyePos;
        } else {
            // Horizontal smoothing (X, Z) has cinematic drift/lag
            float horizDecay = 28.0f - (smoothness * 21.0f);
            float horizLerp = 1.0f - (float) Math.exp(-horizDecay * dt);
            double newX = Mth.lerp(horizLerp, smoothedEyePos.x, exactEyePos.x);
            double newZ = Mth.lerp(horizLerp, smoothedEyePos.z, exactEyePos.z);

            // Vertical smoothing (Y):
            // When flying with Elytra: smooth 3D follow feel
            // When on ground or jumping: tightly lock to eye height so camera NEVER dips into the ground!
            double newY;
            if (isFlying) {
                float vertDecay = 22.0f - (smoothness * 16.0f);
                float vertLerp = 1.0f - (float) Math.exp(-vertDecay * dt);
                newY = Mth.lerp(vertLerp, smoothedEyePos.y, exactEyePos.y);
            } else {
                float vertDecay = 50.0f; // Very fast, eliminating jump bob & floor collision jitter
                float vertLerp = 1.0f - (float) Math.exp(-vertDecay * dt);
                newY = Mth.lerp(vertLerp, smoothedEyePos.y, exactEyePos.y);
            }

            smoothedEyePos = new Vec3(newX, newY, newZ);
        }

        // --- 3. DYNAMIC SPEED LAG (Hıza Yetişememe Esnemesi) ---
        // Exclude vertical jumping impulses; use horizontal velocity on foot, total velocity when gliding
        double targetSpeed;
        if (isFlying) {
            targetSpeed = entity.getDeltaMovement().length();
        } else {
            targetSpeed = Math.hypot(entity.getDeltaMovement().x, entity.getDeltaMovement().z);
        }

        float desiredSpeedLag = (KiemConfig.thirdPersonSpeedLag) ? (float) Math.min(targetSpeed * 2.2, 3.0) : 0.0f;
        // Smoothly interpolate the distance lag so sudden speed changes don't snap
        float speedLerp = 1.0f - (float) Math.exp(-7.0f * dt);
        smoothedSpeedLag = Mth.lerp(speedLerp, smoothedSpeedLag, desiredSpeedLag);

        double baseDistance = Math.max(1.5, KiemConfig.thirdPersonDistance);
        double totalDistance = baseDistance + smoothedSpeedLag;

        // --- 4. SHOULDER SURFING OFFSET & SMOOTH GLIDE ---
        float targetShoulder = KiemConfig.shoulderSurfingEnabled ? KiemConfig.shoulderSide : 0f;
        float shoulderLerp = 1.0f - (float) Math.exp(-12.0f * dt);
        currentShoulder = Mth.lerp(shoulderLerp, currentShoulder, targetShoulder);

        double yawRad = Math.toRadians(currentYaw);
        double pitchRad = Math.toRadians(currentPitch);

        // Unit vector pointing towards where camera is looking
        double lookX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double lookY = -Math.sin(pitchRad);
        double lookZ = Math.cos(yawRad) * Math.cos(pitchRad);
        Vec3 lookDir = new Vec3(lookX, lookY, lookZ).normalize();

        // Right vector perpendicular to look yaw
        Vec3 rightDir = new Vec3(-Math.sin(yawRad - Math.PI / 2.0), 0.0, Math.cos(yawRad - Math.PI / 2.0)).normalize();
        Vec3 upDir = new Vec3(0.0, 1.0, 0.0);

        // Shoulder offset in 3D world space
        Vec3 shoulderOffset = rightDir.scale(currentShoulder * 0.70).add(upDir.scale(0.12));

        // Origin point from which distance extends
        Vec3 cameraOrigin = smoothedEyePos.add(shoulderOffset);
        Vec3 idealCamPos = cameraOrigin.subtract(lookDir.scale(totalDistance));

        // --- 5. ANTI-CLIPPING RAYCAST & SMOOTH RECOVERY ---
        double targetCollisionDist = totalDistance;
        if (level != null) {
            ClipContext clipContext = new ClipContext(
                    cameraOrigin,
                    idealCamPos,
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity
            );
            BlockHitResult hit = level.clip(clipContext);
            if (hit.getType() != HitResult.Type.MISS) {
                double hitDist = hit.getLocation().distanceTo(cameraOrigin);
                targetCollisionDist = Math.max(0.35, hitDist - 0.20);
            }
        }

        // Instant push-in on obstacle collision, smooth release when opening up
        if (currentCollisionDist <= 0 || targetCollisionDist < currentCollisionDist) {
            currentCollisionDist = targetCollisionDist;
        } else {
            float recoveryLerp = 1.0f - (float) Math.exp(-14.0f * dt);
            currentCollisionDist = Mth.lerp(recoveryLerp, (float) currentCollisionDist, (float) targetCollisionDist);
        }

        Vec3 finalCamPos = cameraOrigin.subtract(lookDir.scale(currentCollisionDist));

        // Apply rotation and position to Camera
        setRotFunc.accept(currentYaw, currentPitch);
        setPosFunc.accept(finalCamPos);
    }
}
