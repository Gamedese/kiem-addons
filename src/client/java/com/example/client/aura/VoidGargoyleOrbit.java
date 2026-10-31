package com.example.client.aura;

/** Three independent, predatory inclined orbits with distinct heights and radii. */
public final class VoidGargoyleOrbit {
    public static final int COUNT = 3;
    private static final double TAU = Math.PI * 2;
    private static final float PERIOD = 90f; // deliberate, menacing wing glide
    private static final double[] AMPLITUDES = {0.09, 0.11, 0.08};
    private static final double[] TILTS = {0.4, 2.5, 4.6};

    private VoidGargoyleOrbit() { }

    public record Sample(double x, double y, double z, float yaw, float pitch, float roll) { }

    public static float period(int index) { return PERIOD; }

    public static Sample sample(float age, int index, float center, float radius, float bob) {
        int direction = index == 1 ? -1 : 1;
        double angle = direction * age * TAU / PERIOD + index * TAU / COUNT;
        double amplitude = AMPLITUDES[index] * bob;
        double x = radius * Math.cos(angle);
        double y = center + amplitude * Math.sin(angle + TILTS[index]);
        double z = radius * Math.sin(angle);
        float yaw = (float) (Math.PI - angle + (direction < 0 ? Math.PI : 0));
        float roll = (float) (direction * 0.12f);
        float pitch = (float) (Math.cos(angle + TILTS[index]) * 0.08f * bob);
        return new Sample(x, y, z, yaw, pitch, roll);
    }
}
