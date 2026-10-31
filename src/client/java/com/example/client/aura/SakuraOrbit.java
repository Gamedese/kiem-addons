package com.example.client.aura;

/** Three independent, shallow inclined orbits with the same revolution time. */
public final class SakuraOrbit {
    public static final int COUNT = 3;
    private static final double TAU = Math.PI * 2;
    private static final float PERIOD = 80f;
    private static final double[] RADII = {0.74, 0.84, 0.79};
    private static final double[] HEIGHTS = {0.35, 0.53, 0.71};
    private static final double[] AMPLITUDES = {0.08, 0.10, 0.07};
    private static final double[] TILTS = {0.3, 2.4, 4.5};

    private SakuraOrbit() { }

    public record Sample(double x, double y, double z, float yaw) { }

    public static float period(int index) { return PERIOD; }

    public static Sample sample(float age, int index, float height) {
        double radius = RADII[index];
        double center = Math.max(0.3, height) * HEIGHTS[index];
        double amplitude = AMPLITUDES[index] * Math.min(1, height / 1.5);
        return sample(age, index, (float) center, (float) radius,
                (float) (amplitude / AMPLITUDES[index]));
    }

    /** Customization uses absolute block heights and a multiplier for the gentle bob. */
    public static Sample sample(float age, int index, float center, float radius, float bob) {
        int direction = index == 1 ? -1 : 1;
        double angle = direction * age * TAU / PERIOD + index * TAU / COUNT;
        double amplitude = AMPLITUDES[index] * bob;
        return new Sample(radius * Math.cos(angle),
                center + amplitude * Math.sin(angle + TILTS[index]),
                radius * Math.sin(angle),
                (float) (Math.PI - angle + (direction < 0 ? Math.PI : 0)));
    }
}
