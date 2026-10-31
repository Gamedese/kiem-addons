import com.example.client.aura.SakuraOrbit;
import com.example.client.aura.SakuraOrbitClock;

/** Pure Java regression checks for live speed changes and configurable aura geometry. */
public final class VerifySakuraClock {
    private static int checks;

    public static void main(String[] args) {
        liveChanges();
        invalidSpeeds();
        resetClock();
        customGeometry();
        System.out.println("PASS: " + checks + " clock/customization checks; no jump on speed changes, rate, pause, reset, bounds and defaults.");
    }

    private static void liveChanges() {
        SakuraOrbitClock clock = new SakuraOrbitClock();
        float age = 103.375f;
        float phase = clock.sample(age);
        near(phase, age, 1e-5, "Initial phase");
        for (int step = 0; step < 300; step++) {
            float speed = 0.25f + (step % 36) * 0.05f;
            float before = clock.sample(age);
            clock.setSpeed(speed);
            float after = clock.sample(age);
            near(after, before, 1e-4, "Speed change moved phase");
            for (int bunny = 0; bunny < SakuraOrbit.COUNT; bunny++) {
                samePosition(SakuraOrbit.sample(before, bunny, 1.8f),
                        SakuraOrbit.sample(after, bunny, 1.8f), "Speed change moved bunny");
            }
            // Repeated sampling while paused must never accumulate time.
            for (int repeat = 0; repeat < 4; repeat++) {
                near(clock.sample(age), after, 1e-5, "Paused sample advanced phase");
            }
            float delta = 0.375f + (step % 5) * 0.125f;
            age += delta;
            phase = clock.sample(age);
            near(phase - after, delta * speed, 1e-4, "Wrong post-change rate");
        }
    }

    private static void invalidSpeeds() {
        float[] inputs = {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
                -4f, 0f, 10f, 0.25f, 2f};
        float[] expected = {1f, 1f, 1f, 0.25f, 0.25f, 2f, 0.25f, 2f};
        for (int i = 0; i < inputs.length; i++) {
            SakuraOrbitClock clock = new SakuraOrbitClock();
            clock.sample(10.25f);
            clock.setSpeed(inputs[i]);
            float start = clock.sample(10.25f);
            float end = clock.sample(14.25f);
            require(Float.isFinite(start) && Float.isFinite(end), "Bad speed poisoned phase");
            near(start, 10.25f, 1e-5, "Bad speed moved phase at change");
            near(end - start, expected[i] * 4, 1e-5, "Bad speed not normalized");
        }
    }

    private static void resetClock() {
        SakuraOrbitClock clock = new SakuraOrbitClock();
        clock.setSpeed(1.5f);
        near(clock.sample(100.5f), 150.75f, 1e-5, "Initial configured speed ignored");
        clock.sample(125.5f);
        clock.reset();
        near(clock.sample(2.5f), 3.75f, 1e-5, "Reset retained old world's phase");
        near(clock.sample(3.5f), 5.25f, 1e-5, "Reset lost configured speed");
    }

    private static void customGeometry() {
        float[] heights = {0.63f, 0.954f, 1.278f};
        float[] radii = {0.74f, 0.84f, 0.79f};
        double[] amplitudes = {0.08, 0.10, 0.07};
        for (int bunny = 0; bunny < SakuraOrbit.COUNT; bunny++) {
            near(SakuraOrbit.period(bunny), 80, 0, "Not a four-second orbit");
            for (int frame = 0; frame < 721; frame++) {
                float age = frame / 3f;
                var custom = SakuraOrbit.sample(age, bunny, heights[bunny], radii[bunny], 1);
                samePosition(custom, SakuraOrbit.sample(age, bunny, 1.8f), "Default geometry changed");
                for (float radius : new float[]{0.4f, 1.13f, 1.5f}) {
                    var flat = SakuraOrbit.sample(age, bunny, 1.43f, radius, 0);
                    near(flat.y(), 1.43f, 1e-7, "Zero bob does not hold absolute height");
                    near(Math.hypot(flat.x(), flat.z()), radius, 1e-7, "Custom radius ignored");
                    var bobbed = SakuraOrbit.sample(age, bunny, 1.43f, radius, 3);
                    require(Math.abs(bobbed.y() - 1.43f) <= amplitudes[bunny] * 3 + 1e-7,
                            "Custom bob exceeds amplitude");
                    near(bobbed.x(), flat.x(), 1e-7, "Bob changed X");
                    near(bobbed.z(), flat.z(), 1e-7, "Bob changed Z");
                }
            }
        }
    }

    private static void samePosition(SakuraOrbit.Sample a, SakuraOrbit.Sample b, String message) {
        near(a.x(), b.x(), 1e-6, message + " X");
        near(a.y(), b.y(), 1e-6, message + " Y");
        near(a.z(), b.z(), 1e-6, message + " Z");
    }

    private static void near(double actual, double expected, double tolerance, String message) {
        require(Double.isFinite(actual) && Math.abs(actual - expected) <= tolerance,
                message + ": " + actual + " vs " + expected);
    }

    private static void require(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
