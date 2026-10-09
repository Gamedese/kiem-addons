package com.example.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCapeManager {
    public static final File CAPES_DIR = new File("kiem/capes");

    public static class CustomCapeEntry {
        public final String fileName;
        public final String displayName;
        public final boolean isAnimated;
        public final int frameCount;
        public final int frameDelayMs;
        public final long lastModified;
        public final long fileSize;
        public final String idPrefix;
        public final Identifier[] frameIdentifiers;
        public final ClientAsset.ResourceTexture[] frameAssets;
        public volatile boolean allFramesLoaded;

        public CustomCapeEntry(String fileName, String displayName, boolean isAnimated, int frameCount, int frameDelayMs,
                               long lastModified, long fileSize, String idPrefix, Identifier frame0Id) {
            this.fileName = fileName;
            this.displayName = displayName;
            this.isAnimated = isAnimated;
            this.frameCount = frameCount;
            this.frameDelayMs = frameDelayMs > 0 ? frameDelayMs : 41;
            this.lastModified = lastModified;
            this.fileSize = fileSize;
            this.idPrefix = idPrefix;
            this.frameIdentifiers = new Identifier[frameCount];
            this.frameAssets = new ClientAsset.ResourceTexture[frameCount];

            Identifier texPath0 = Identifier.fromNamespaceAndPath("modid", "textures/custom_cape/" + idPrefix + "_0.png");
            ClientAsset.ResourceTexture asset0 = new ClientAsset.ResourceTexture(frame0Id, texPath0);

            this.frameIdentifiers[0] = frame0Id;
            this.frameAssets[0] = asset0;

            // Fallback: point remaining frames to frame 0 until async background loading completes
            for (int i = 1; i < frameCount; i++) {
                this.frameIdentifiers[i] = frame0Id;
                this.frameAssets[i] = asset0;
            }

            if (frameCount <= 1 || !isAnimated) {
                this.allFramesLoaded = true;
            }
        }

        public synchronized void setFrame(int index, Identifier id) {
            if (index >= 0 && index < frameCount) {
                Identifier texPath = Identifier.fromNamespaceAndPath("modid", "textures/custom_cape/" + idPrefix + "_" + index + ".png");
                this.frameIdentifiers[index] = id;
                this.frameAssets[index] = new ClientAsset.ResourceTexture(id, texPath);
            }
        }
    }

    private static final Map<String, CustomCapeEntry> capeCache = new ConcurrentHashMap<>();
    private static final List<CustomCapeEntry> loadedCapes = new ArrayList<>();
    private static int selectedIndex = 0;
    private static boolean initialized = false;

    public static File getCapesDir() {
        if (!CAPES_DIR.exists()) {
            CAPES_DIR.mkdirs();
            createInitialReadme();
        }
        return CAPES_DIR;
    }

    private static void createInitialReadme() {
        File readme = new File(CAPES_DIR, "README.txt");
        if (!readme.exists()) {
            try {
                String note = "just search for idk \"rikka gif\" download it and paste in here. any format works but 9:16 is the preferred format\n"
                        + "--------------------------------------------------\n"
                        + "kafana göre internetten \"rikka gif\" falan arat indir ve buraya yapıştır. her format çalışır ama 9:16 en iyi duranı\n";
                Files.writeString(readme.toPath(), note, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception ignored) {
            }
        }
    }

    public static void openCapesFolder() {
        File dir = getCapesDir();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(dir);
            } else {
                new ProcessBuilder("explorer.exe", dir.getAbsolutePath()).start();
            }
        } catch (Exception e) {
            try {
                new ProcessBuilder("explorer.exe", dir.getAbsolutePath()).start();
            } catch (Exception ignored) {
            }
        }
    }

    public static void init() {
        if (!initialized) {
            initialized = true;
            getCapesDir();
            scanAndLoad();
        }
    }

    public static synchronized void forceRescan() {
        capeCache.clear();
        scanAndLoad();
    }

    public static synchronized void scanAndLoad() {
        File dir = getCapesDir();
        File[] files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase(Locale.ROOT);
            return lower.endsWith(".png") || lower.endsWith(".gif");
        });

        loadedCapes.clear();
        if (files == null || files.length == 0) {
            selectedIndex = 0;
            return;
        }

        // Sort alphabetically
        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            try {
                // Check cache first to avoid re-decoding unmodified files
                CustomCapeEntry cached = capeCache.get(file.getName());
                if (cached != null && cached.lastModified == file.lastModified() && cached.fileSize == file.length()) {
                    loadedCapes.add(cached);
                    continue;
                }

                CustomCapeEntry entry = loadCapeFile(file);
                if (entry != null) {
                    capeCache.put(file.getName(), entry);
                    loadedCapes.add(entry);
                }
            } catch (Exception e) {
                System.err.println("[KiemAddons] Failed to load custom cape: " + file.getName());
                e.printStackTrace();
            }
        }

        // Restore selected cape from config if present
        if (!KiemConfig.customCapeFileName.isEmpty()) {
            for (int i = 0; i < loadedCapes.size(); i++) {
                if (loadedCapes.get(i).fileName.equalsIgnoreCase(KiemConfig.customCapeFileName)) {
                    selectedIndex = i;
                    return;
                }
            }
        }
        if (selectedIndex >= loadedCapes.size()) {
            selectedIndex = Math.max(0, loadedCapes.size() - 1);
        }
    }

    private static CustomCapeEntry loadCapeFile(File file) throws Exception {
        String name = file.getName();
        String lower = name.toLowerCase(Locale.ROOT);
        String baseName = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        String idPrefix = baseName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_.-]", "_");
        long lastMod = file.lastModified();
        long size = file.length();

        if (lower.endsWith(".png")) {
            BufferedImage raw = ImageIO.read(file);
            if (raw == null) return null;
            BufferedImage formatted = formatToCapeTemplate(raw);
            Identifier id = registerFrameTexture(idPrefix, 0, formatted);
            if (id == null) return null;
            return new CustomCapeEntry(name, baseName, false, 1, 41, lastMod, size, idPrefix, id);
        } else if (lower.endsWith(".gif")) {
            // Fast first frame decode for instant UI preview and opening
            try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
                if (!readers.hasNext()) return null;
                ImageReader reader = readers.next();
                reader.setInput(stream, false);

                int numImages = reader.getNumImages(true);
                if (numImages <= 0) {
                    reader.dispose();
                    return null;
                }

                // Parse delay from first frame metadata
                int frameDelay = 41;
                try {
                    IIOMetadata meta0 = reader.getImageMetadata(0);
                    int d = parseGifDelay(meta0);
                    if (d > 0) frameDelay = d;
                } catch (Exception ignored) {
                }

                BufferedImage frame0 = reader.read(0);
                BufferedImage formatted0 = formatToCapeTemplate(frame0);
                Identifier id0 = registerFrameTexture(idPrefix, 0, formatted0);
                if (id0 == null) {
                    reader.dispose();
                    return null;
                }

                int count = Math.min(numImages, 240); // Cap at 240 frames
                CustomCapeEntry entry = new CustomCapeEntry(name, baseName, count > 1, count, frameDelay, lastMod, size, idPrefix, id0);

                reader.dispose();

                // If animated, decode remaining frames asynchronously in background thread
                if (count > 1) {
                    CompletableFuture.runAsync(() -> loadRemainingGifFramesAsync(file, entry, count, idPrefix));
                }

                return entry;
            }
        }
        return null;
    }

    private static void loadRemainingGifFramesAsync(File file, CustomCapeEntry entry, int totalCount, String idPrefix) {
        try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return;
            ImageReader reader = readers.next();
            reader.setInput(stream, false);

            BufferedImage master = null;
            Graphics2D masterGraphics = null;

            for (int i = 0; i < totalCount; i++) {
                BufferedImage frame = reader.read(i);
                if (master == null) {
                    master = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    masterGraphics = master.createGraphics();
                }
                masterGraphics.drawImage(frame, 0, 0, null);

                // Frame 0 already registered on main thread
                if (i > 0) {
                    BufferedImage copy = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gCopy = copy.createGraphics();
                    gCopy.drawImage(master, 0, 0, null);
                    gCopy.dispose();

                    BufferedImage formatted = formatToCapeTemplate(copy);

                    final int frameIdx = i;
                    // Upload texture on Minecraft client render thread
                    Minecraft.getInstance().execute(() -> {
                        Identifier id = registerFrameTexture(idPrefix, frameIdx, formatted);
                        if (id != null) {
                            entry.setFrame(frameIdx, id);
                        }
                    });
                }
            }

            if (masterGraphics != null) {
                masterGraphics.dispose();
            }
            reader.dispose();

            entry.allFramesLoaded = true;
        } catch (Exception e) {
            System.err.println("[KiemAddons] Failed async frame loading for: " + file.getName());
            e.printStackTrace();
        }
    }

    private static BufferedImage formatToCapeTemplate(BufferedImage input) {
        int inW = input.getWidth();
        int inH = input.getHeight();

        // If already 2:1 aspect ratio (standard Minecraft cape template)
        if (inW == inH * 2) {
            if (inW == 1024 && inH == 512) {
                return input;
            }
            BufferedImage scaled = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = scaled.createGraphics();
            if (inW <= 64) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            } else {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
            g.drawImage(input, 0, 0, 1024, 512, null);
            g.dispose();
            return scaled;
        }

        // Minecraft Cape UV layout for 1024x512 template:
        // Model box is (10, 16, 1) in 64x32 layout:
        // Back face (exterior visible face): X=192..352 (width 160), Y=16..272 (height 256)
        // Front face (interior face): X=16..176 (width 160), Y=16..272 (height 256)
        // Top edge: X=16..176, Y=0..16
        // Bottom edge: X=176..336, Y=0..16
        // Right edge: X=0..16, Y=16..272
        // Left edge: X=176..192, Y=16..272
        BufferedImage cape = new BufferedImage(1024, 512, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cape.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill entire cape geometry with clean dark slate background
        g.setColor(new Color(24, 25, 34, 255));
        // Fill top/bottom edges
        g.fillRect(16, 0, 320, 16);
        // Fill main body and sides
        g.fillRect(0, 16, 352, 256);

        // Aspect-fit scaling into 160x256 back face
        int backX = 192;
        int backY = 16;
        int backW = 160;
        int backH = 256;

        double scale = Math.max((double) backW / inW, (double) backH / inH);
        int drawW = (int) (inW * scale);
        int drawH = (int) (inH * scale);
        int drawX = backX + (backW - drawW) / 2;
        int drawY = backY + (backH - drawH) / 2;

        // Clip and draw onto Back face (exterior)
        Shape oldClip = g.getClip();
        g.setClip(backX, backY, backW, backH);
        g.drawImage(input, drawX, drawY, drawW, drawH, null);
        g.setClip(oldClip);

        // Draw onto Front face (interior facing player)
        int frontX = 16;
        int frontY = 16;
        int frontW = 160;
        int frontH = 256;
        int frontDrawX = frontX + (frontW - drawW) / 2;
        int frontDrawY = frontY + (frontH - drawH) / 2;

        g.setClip(frontX, frontY, frontW, frontH);
        g.drawImage(input, frontDrawX, frontDrawY, drawW, drawH, null);
        g.setClip(oldClip);

        g.dispose();
        return cape;
    }

    private static int parseGifDelay(IIOMetadata metadata) {
        try {
            String formatName = metadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
            for (int i = 0; i < root.getLength(); i++) {
                Node node = root.item(i);
                if ("GraphicControlExtension".equalsIgnoreCase(node.getNodeName())) {
                    NamedNodeMap attr = node.getAttributes();
                    Node delayNode = attr.getNamedItem("delayTime");
                    if (delayNode != null) {
                        int delayUnits = Integer.parseInt(delayNode.getNodeValue());
                        int ms = delayUnits * 10;
                        return ms > 0 ? ms : 41;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return 41;
    }

    private static Identifier registerFrameTexture(String idPrefix, int frameIndex, BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();

            // Direct pixel transfer into NativeImage (ultra-fast, zero PNG encoding overhead)
            NativeImage nativeImage = new NativeImage(width, height, false);
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
            for (int y = 0; y < height; y++) {
                int rowOffset = y * width;
                for (int x = 0; x < width; x++) {
                    nativeImage.setPixel(x, y, pixels[rowOffset + x]);
                }
            }

            DynamicTexture dynamicTexture = new DynamicTexture(() -> "kiem_custom_" + idPrefix + "_" + frameIndex, nativeImage);
            dynamicTexture.upload();

            Identifier id = Identifier.fromNamespaceAndPath("modid", "custom_cape/" + idPrefix + "_" + frameIndex);
            Identifier texPath = Identifier.fromNamespaceAndPath("modid", "textures/custom_cape/" + idPrefix + "_" + frameIndex + ".png");

            TextureManager tm = Minecraft.getInstance().getTextureManager();
            tm.register(id, dynamicTexture);
            tm.register(texPath, dynamicTexture);

            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<CustomCapeEntry> getLoadedCapes() {
        return loadedCapes;
    }

    public static int getSelectedIndex() {
        return selectedIndex;
    }

    public static void setSelectedIndex(int index) {
        if (!loadedCapes.isEmpty()) {
            selectedIndex = Math.floorMod(index, loadedCapes.size());
            KiemConfig.customCapeFileName = loadedCapes.get(selectedIndex).fileName;
            KiemConfig.save();
        }
    }

    public static CustomCapeEntry getSelectedCape() {
        if (loadedCapes.isEmpty()) return null;
        if (selectedIndex < 0 || selectedIndex >= loadedCapes.size()) {
            selectedIndex = 0;
        }
        return loadedCapes.get(selectedIndex);
    }

    public static String getSelectedCapeName() {
        CustomCapeEntry entry = getSelectedCape();
        return entry != null ? entry.displayName : "Yok";
    }

    public static ClientAsset.ResourceTexture getCurrentCapeAsset() {
        CustomCapeEntry cape = getSelectedCape();
        if (cape == null || cape.frameCount == 0) return null;

        if (!cape.isAnimated || cape.frameCount == 1) {
            return cape.frameAssets[0];
        }

        long timeMs = System.currentTimeMillis();
        int frameIndex = (int) ((timeMs / cape.frameDelayMs) % cape.frameCount);
        if (frameIndex < 0) frameIndex = 0;
        if (frameIndex >= cape.frameCount) frameIndex = cape.frameCount - 1;

        ClientAsset.ResourceTexture asset = cape.frameAssets[frameIndex];
        return asset != null ? asset : cape.frameAssets[0];
    }
}
