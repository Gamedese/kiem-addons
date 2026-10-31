package com.example.client.aura;

/** A continuous phase clock: changing speed rebases time instead of teleporting the orbit. */
public final class SakuraOrbitClock {
    private double anchorPhase;
    private float anchorAge;
    private float lastAge;
    private float speed = 1;
    private boolean initialized;

    public float sample(float age) {
        if (!initialized) {
            anchorAge = age;
            anchorPhase = age * (double) speed;
            initialized = true;
        }
        lastAge = age;
        return (float) (anchorPhase + (age - anchorAge) * (double) speed);
    }

    public void setSpeed(float value) {
        if (!Float.isFinite(value)) value = 1;
        value = Math.max(0.25f, Math.min(2f, value));
        if (value == speed) return;
        if (initialized) {
            anchorPhase += (lastAge - anchorAge) * (double) speed;
            anchorAge = lastAge;
        }
        speed = value;
    }

    public void reset() { initialized = false; }
}
