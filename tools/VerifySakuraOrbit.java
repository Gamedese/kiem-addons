import com.example.client.aura.SakuraOrbit;

/** Numerical guard for visible loop jumps and tangent-facing regressions. No game required. */
public final class VerifySakuraOrbit {
    public static void main(String[] args) {
        int samples = 0;
        for (float height : new float[]{0.6f, 1.5f, 1.8f}) {
            for (int bunny = 0; bunny < SakuraOrbit.COUNT; bunny++) {
                SakuraOrbit.Sample previous = null;
                boolean rising = false, falling = false;
                double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
                double initialRadius = Math.hypot(SakuraOrbit.sample(0, bunny, height).x(),
                        SakuraOrbit.sample(0, bunny, height).z());
                for (int frame = 0; frame <= 3600; frame++) {
                    float age = frame / 3f; // 60 FPS at 20 game ticks/s; 15 closed orbits.
                    var s = SakuraOrbit.sample(age, bunny, height);
                    require(Double.isFinite(s.x() + s.y() + s.z() + s.yaw()), "Nonfinite sample");
                    require(s.y() > 0 && s.y() < height, "Head leaves the player's height range");
                    minY = Math.min(minY, s.y());
                    maxY = Math.max(maxY, s.y());
                    double radius = Math.hypot(s.x(), s.z());
                    require(Math.abs(radius - initialRadius) < 1e-6, "Orbit radius changes");
                    var nextLap = SakuraOrbit.sample(age + SakuraOrbit.period(bunny), bunny, height);
                    require(Math.abs(nextLap.y() - s.y()) < 1e-5
                            && Math.hypot(nextLap.x() - s.x(), nextLap.z() - s.z()) < 1e-5,
                            "Orbit does not close after one revolution");
                    if (previous != null) {
                        double jump = Math.abs(s.y() - previous.y());
                        require(jump < 0.005, "Discontinuous or excessive vertical motion");
                        rising |= s.y() > previous.y();
                        falling |= s.y() < previous.y();
                        double dx = s.x() - previous.x(), dz = s.z() - previous.z();
                        double facingDot = -Math.sin(s.yaw()) * dx - Math.cos(s.yaw()) * dz;
                        require(facingDot > 0, "Head faces backward along orbit");
                    }
                    previous = s;
                    samples++;
                }
                require(rising && falling, "Bunny must travel both up and down");
                require(maxY - minY <= 0.201, "Vertical excursion is too large");
            }
        }
        for (int a = 0; a < SakuraOrbit.COUNT; a++) {
            require(SakuraOrbit.period(a) == 80f, "Each head must complete a revolution in 80 ticks (4 seconds)");
            for (int b = a + 1; b < SakuraOrbit.COUNT; b++) {
                require(SakuraOrbit.period(a) == SakuraOrbit.period(b), "Heads must share the same pace");
                var first = SakuraOrbit.sample(0, a, 1.8f);
                var second = SakuraOrbit.sample(0, b, 1.8f);
                require(Math.abs(Math.hypot(first.x(), first.z()) - Math.hypot(second.x(), second.z())) > 0.02,
                        "Heads follow the same ring");
            }
        }
        System.out.println("PASS: " + samples + " samples; equal 80-tick (4-second) pace, independent rings, <=0.20 block bobbing, closed loops, facing.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
