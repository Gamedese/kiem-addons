import com.example.client.aura.SakuraAuraPreview;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

/** Snapshot lifetime and projected containment checks; does not start Minecraft. */
public final class VerifySakuraPreview {
    private static int checks;
    public static void main(String[] args) {
        snapshots();
        containment();
        System.out.println("PASS: " + checks + " preview checks; immutable snapshots and extreme-setting projection containment.");
    }

    private static void snapshots() {
        var heights = new ArrayList<>(List.of(.63f, .954f, 1.278f));
        var radii = new ArrayList<>(List.of(.74f, .84f, .79f));
        var settings = new SakuraAuraPreview.Settings(.8f, 1, 1, 2, heights, radii);
        var before = settings.sample(23.75f, 0);
        heights.set(0, 2.2f);
        radii.set(0, 1.5f);
        var after = settings.sample(23.75f, 0);
        require(before.equals(after), "Queued geometry changed with source lists");
        try { settings.heights().set(0, 9f); throw new AssertionError("Mutable heights snapshot"); }
        catch (UnsupportedOperationException expected) { checks++; }
        try { settings.radii().set(0, 9f); throw new AssertionError("Mutable radii snapshot"); }
        catch (UnsupportedOperationException expected) { checks++; }
        var source = new ArrayList<com.example.client.aura.SakuraAuraManager.Petal>();
        var frame = new SakuraAuraPreview.Frame(10, 10, settings, source);
        require(frame.petals() != source && frame.petals().isEmpty(), "Frame keeps mutable trail list");
        try { frame.petals().clear(); throw new AssertionError("Mutable frame trail"); }
        catch (UnsupportedOperationException expected) { checks++; }
        var bloomSource = new ArrayList<com.example.client.aura.SakuraAuraManager.GroundFlower>();
        var frameWithBloom = new SakuraAuraPreview.Frame(10, 10, settings, source, bloomSource);
        require(frameWithBloom.groundBloom() != bloomSource && frameWithBloom.groundBloom().isEmpty(), "Frame keeps mutable bloom list");
        try { frameWithBloom.groundBloom().clear(); throw new AssertionError("Mutable frame bloom"); }
        catch (UnsupportedOperationException expected) { checks++; }
    }

    private static void containment() {
        int[][] sizes = {{92,140},{140,220},{260,280},{400,400}};
        for (float headScale : new float[]{.3f, .8f, 1.4f}) {
            for (float orbitRadius : new float[]{.4f,.84f,1.5f}) {
                for (float height : new float[]{.15f,1.278f,2.2f}) {
                    for (float bob : new float[]{0,1,3}) {
                        // Independent sphere/height envelope of the actual head plus gentle roll.
                        double radius = Math.max(.6, orbitRadius + headScale * .25 + .16);
                        double extent = bob * .1 + headScale * .25;
                        double minY = Math.min(-.15, height - extent - .2);
                        double maxY = Math.max(2.0, height + extent);
                        for (float yaw : new float[]{-180,-90,0,25,90,180}) {
                            for (float pitch : new float[]{-35,-8,0,30}) {
                                float p = (float)Math.toRadians(pitch);
                                var rotation = new Quaternionf().rotationZ((float)Math.PI)
                                        .mul(new Quaternionf().rotationX(p).rotateY((float)Math.toRadians(yaw)));
                                var translation = rotation.transform(new Vector3f(0,(float)(-(minY+maxY)/2),0));
                                double halfY = (maxY-minY)/2*Math.abs(Math.cos(p)) + radius*Math.abs(Math.sin(p));
                                for (int[] size : sizes) {
                                    double pixels = Math.max(1,Math.min((size[0]-16)/(2*radius),(size[1]-16)/(2*halfY)));
                                    for (int angle = 0; angle < 16; angle++) {
                                        double a = angle * Math.PI / 8;
                                        for (double y : new double[]{minY,maxY}) {
                                            var point = rotation.transform(new Vector3f((float)(radius*Math.cos(a)),(float)y,(float)(radius*Math.sin(a)))).add(translation);
                                            require(Math.abs(point.x*pixels) <= size[0]/2.0-7.99,
                                                    "Horizontal clipping at extreme settings");
                                            require(Math.abs(point.y*pixels) <= size[1]/2.0-7.99,
                                                    "Vertical clipping at extreme settings");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void require(boolean condition,String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
    }
}
