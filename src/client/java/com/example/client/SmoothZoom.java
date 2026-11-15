package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class SmoothZoom {
    public static KeyMapping zoomKeyMapping;

    private static double currentZoom = 1.0;
    private static double targetZoom = 1.0;
    private static double activeTargetZoom = 4.0;
    private static long lastTimeNano = 0;
    private static boolean wasZooming = false;
    private static float cinematicProgress = 0.0f;

    public static boolean isZoomKeyHeld() {
        if (!KiemConfig.zoomEnabled) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || KiemCompat.getScreen(mc) != null) return false;

        if (zoomKeyMapping != null && zoomKeyMapping.isDown()) {
            return true;
        }
        return KiemCompat.isKeyDown(mc.getWindow(), KiemConfig.zoomKey);
    }

    public static boolean isZooming() {
        return isZoomKeyHeld();
    }

    public static void update() {
        long now = System.nanoTime();
        if (lastTimeNano == 0) {
            lastTimeNano = now;
        }
        double dt = (now - lastTimeNano) / 1_000_000_000.0;
        lastTimeNano = now;
        if (dt > 0.1) dt = 0.1; // Clamp in case of freeze or loading screen

        boolean zooming = isZooming();

        if (zooming && !wasZooming) {
            // Started zooming: reset to configured default zoom
            activeTargetZoom = KiemConfig.zoomDefaultFactor;
            targetZoom = activeTargetZoom;
            wasZooming = true;
        } else if (!zooming && wasZooming) {
            // Released zoom: return to normal 1.0x
            targetZoom = 1.0;
            wasZooming = false;
        } else if (zooming) {
            targetZoom = activeTargetZoom;
        } else {
            targetZoom = 1.0;
        }

        // Frame-rate independent exponential smoothing
        double speed = KiemConfig.zoomSpeed;
        double factor = 1.0 - Math.exp(-speed * dt);
        currentZoom += (targetZoom - currentZoom) * factor;

        // Snapping thresholds
        if (!zooming && Math.abs(currentZoom - 1.0) < 0.005) {
            currentZoom = 1.0;
        } else if (zooming && Math.abs(currentZoom - targetZoom) < 0.002) {
            currentZoom = targetZoom;
        }

        // Cinematic Bars Progress (smooth transition)
        boolean isAfkBars = KiemConfig.afkCinematicBars && com.example.client.AfkCinematicsManager.isAfkActive();
        boolean shouldShowBars = KiemConfig.cinematicBarsAlways || (KiemConfig.zoomCinematicBars && zooming) || isAfkBars;
        float targetCinematic = shouldShowBars ? 1.0f : 0.0f;
        float cinFactor = (float) (1.0 - Math.exp(-speed * dt));
        cinematicProgress += (targetCinematic - cinematicProgress) * cinFactor;
        if (!shouldShowBars && cinematicProgress < 0.002f) {
            cinematicProgress = 0.0f;
        } else if (shouldShowBars && Math.abs(cinematicProgress - 1.0f) < 0.002f) {
            cinematicProgress = 1.0f;
        }
    }

    public static void onScroll(double amount) {
        if (!isZooming() || !KiemConfig.zoomScrollEnabled) return;

        double delta = Math.signum(amount) * KiemConfig.zoomStep;
        activeTargetZoom += delta;
        if (activeTargetZoom < 1.25) activeTargetZoom = 1.25;
        if (activeTargetZoom > 15.0) activeTargetZoom = 15.0;
        targetZoom = activeTargetZoom;
    }

    public static float getCurrentZoom() {
        return (float) currentZoom;
    }

    public static float getCinematicProgress() {
        return cinematicProgress;
    }

    public static double getActiveTargetZoom() {
        return activeTargetZoom;
    }

    public static void reset() {
        currentZoom = 1.0;
        targetZoom = 1.0;
        activeTargetZoom = KiemConfig.zoomDefaultFactor;
        wasZooming = false;
        cinematicProgress = 0.0f;
    }
}
