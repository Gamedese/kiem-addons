package com.example.client.aura;

/**
 * Three independent, predatory Lissajous-spiral orbits with dynamic banking roll
 * and derivative pitch so the dragons tilt nose-up when climbing and nose-down when diving.
 */
public final class VoidGargoyleOrbit {
    public static final int COUNT = 3;
    private static final double TAU = Math.PI * 2;
    private static final float PERIOD = 90f;
    private static final double[] AMPLITUDES = {0.10, 0.12, 0.09};
    private static final double[] TILTS = {0.4, 2.5, 4.6};

    private VoidGargoyleOrbit() { }

    public record Sample(double x, double y, double z, float yaw, float pitch, float roll, int direction) { }

    public static float period(int index) { return PERIOD; }

    public static Sample sample(float age, int index, float center, float radius, float bob) {
        int direction = index == 1 ? -1 : 1;
        double angle = direction * age * TAU / PERIOD + index * TAU / COUNT;
        double amplitude = AMPLITUDES[index] * bob;
        double lissajousAmp = 0.22 * bob;
        double lissajousPhase = angle * 2.0 + TILTS[index] * 1.7;

        double x = radius * Math.cos(angle);
        double y = center + amplitude * Math.sin(angle + TILTS[index]) + lissajousAmp * Math.sin(lissajousPhase);
        double z = radius * Math.sin(angle);

        // Derivative of y with respect to angle -> nose pitches up when climbing, down when diving
        double dyDangle = amplitude * Math.cos(angle + TILTS[index]) + 2.0 * lissajousAmp * Math.cos(lissajousPhase);
        float pitch = (float) (-direction * dyDangle * 0.38f);

        float yaw = (float) (Math.PI - angle + (direction < 0 ? Math.PI : 0));
        float roll = direction * 0.35f; // ~20 deg inward banking toward the player
        return new Sample(x, y, z, yaw, pitch, roll, direction);
    }
}
