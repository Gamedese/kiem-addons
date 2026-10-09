package com.example.client;

import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class CapeManager {
    public enum CapeType {
        SANARA_ANIMATED("Sanara (Animated)", "46-kare akıcı döngü"),
        SANARA_STATIC("Sanara (Standard)", "1024x512 yüksek çözünürlük"),
        SANARA_V2_ANIMATED("Sanara V2 (Animated)", "240-kare 24 FPS sinematik döngü"),
        SANARA_V2_STATIC("Sanara V2 (Standard)", "1024x512 yüksek çözünürlük"),
        SANARA_V3_ANIMATED("Sanara V3 (Animated)", "240-kare 24 FPS sinematik döngü"),
        SANARA_V3_STATIC("Sanara V3 (Standard)", "1024x512 yüksek çözünürlük"),
        RIKKA_ANIMATED("Rikka (Animated)", "240-kare 24 FPS sinematik döngü"),
        RIKKA_STATIC("Rikka (Standard)", "1024x512 yüksek çözünürlük"),
        CUSTOM("Özel Pelerin", "Kullanıcı tanımlı GIF/PNG"),
        OFF("Yok", "Pelerin kapalı");

        private final String title;
        private final String subtitle;

        CapeType(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }
    }

    private static CapeType currentCape = CapeType.RIKKA_ANIMATED;

    // V1 Assets
    private static final ClientAsset.ResourceTexture STATIC_CAPE_ASSET =
            new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("modid", "cape/static/cape_static"));
    private static final ClientAsset.ResourceTexture[] ANIM_CAPE_ASSETS = new ClientAsset.ResourceTexture[46];

    // V2 Assets
    private static final ClientAsset.ResourceTexture V2_STATIC_CAPE_ASSET =
            new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("modid", "cape/v2/static/cape_static"));
    private static final ClientAsset.ResourceTexture[] V2_ANIM_CAPE_ASSETS = new ClientAsset.ResourceTexture[240];

    // V3 Assets
    private static final ClientAsset.ResourceTexture V3_STATIC_CAPE_ASSET =
            new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("modid", "cape/v3/static/cape_static"));
    private static final ClientAsset.ResourceTexture[] V3_ANIM_CAPE_ASSETS = new ClientAsset.ResourceTexture[240];

    // Rikka Assets
    private static final ClientAsset.ResourceTexture RIKKA_STATIC_CAPE_ASSET =
            new ClientAsset.ResourceTexture(Identifier.fromNamespaceAndPath("modid", "cape/rikka/static/cape_static"));
    private static final ClientAsset.ResourceTexture[] RIKKA_ANIM_CAPE_ASSETS = new ClientAsset.ResourceTexture[240];

    static {
        for (int i = 0; i < 46; i++) {
            ANIM_CAPE_ASSETS[i] = new ClientAsset.ResourceTexture(
                    Identifier.fromNamespaceAndPath("modid", "cape/anim/frame_" + i)
            );
        }
        for (int i = 0; i < 240; i++) {
            V2_ANIM_CAPE_ASSETS[i] = new ClientAsset.ResourceTexture(
                    Identifier.fromNamespaceAndPath("modid", "cape/v2/anim/frame_" + i)
            );
        }
        for (int i = 0; i < 240; i++) {
            V3_ANIM_CAPE_ASSETS[i] = new ClientAsset.ResourceTexture(
                    Identifier.fromNamespaceAndPath("modid", "cape/v3/anim/frame_" + i)
            );
        }
        for (int i = 0; i < 240; i++) {
            RIKKA_ANIM_CAPE_ASSETS[i] = new ClientAsset.ResourceTexture(
                    Identifier.fromNamespaceAndPath("modid", "cape/rikka/anim/frame_" + i)
            );
        }
        CustomCapeManager.init();
        loadConfig();
    }

    public static CapeType getCurrentCape() {
        return currentCape;
    }

    public static void setCurrentCape(CapeType type) {
        currentCape = type;
        saveConfig();
    }

    public static void setCurrentCapeDirect(CapeType type) {
        currentCape = type;
    }

    public static ClientAsset.ResourceTexture getCurrentCapeAsset() {
        if (currentCape == CapeType.OFF) {
            return null;
        }
        if (currentCape == CapeType.SANARA_STATIC) {
            return STATIC_CAPE_ASSET;
        }
        if (currentCape == CapeType.SANARA_ANIMATED) {
            long timeMs = System.currentTimeMillis();
            int step = (int) ((timeMs / 62) % 90); // 90-step pingpong
            int frameIndex = step < 46 ? step : (89 - step);
            if (frameIndex < 0) frameIndex = 0;
            if (frameIndex >= 46) frameIndex = 45;
            return ANIM_CAPE_ASSETS[frameIndex];
        }
        if (currentCape == CapeType.SANARA_V2_STATIC) {
            return V2_STATIC_CAPE_ASSET;
        }
        if (currentCape == CapeType.SANARA_V2_ANIMATED) {
            long timeMs = System.currentTimeMillis();
            int frameIndex = (int) ((timeMs / 41) % 240); // 24 FPS (41ms/frame), 240 frames
            if (frameIndex < 0) frameIndex = 0;
            if (frameIndex >= 240) frameIndex = 239;
            return V2_ANIM_CAPE_ASSETS[frameIndex];
        }
        if (currentCape == CapeType.SANARA_V3_STATIC) {
            return V3_STATIC_CAPE_ASSET;
        }
        if (currentCape == CapeType.SANARA_V3_ANIMATED) {
            long timeMs = System.currentTimeMillis();
            int frameIndex = (int) ((timeMs / 41) % 240); // 24 FPS (41ms/frame), 240 frames
            if (frameIndex < 0) frameIndex = 0;
            if (frameIndex >= 240) frameIndex = 239;
            return V3_ANIM_CAPE_ASSETS[frameIndex];
        }
        if (currentCape == CapeType.RIKKA_STATIC) {
            return RIKKA_STATIC_CAPE_ASSET;
        }
        if (currentCape == CapeType.RIKKA_ANIMATED) {
            long timeMs = System.currentTimeMillis();
            int frameIndex = (int) ((timeMs / 41) % 240); // 24 FPS (41ms/frame), 240 frames
            if (frameIndex < 0) frameIndex = 0;
            if (frameIndex >= 240) frameIndex = 239;
            return RIKKA_ANIM_CAPE_ASSETS[frameIndex];
        }
        if (currentCape == CapeType.CUSTOM) {
            return CustomCapeManager.getCurrentCapeAsset();
        }
        return null;
    }

    private static File getConfigFile() {
        return new File("config/kiem.properties");
    }

    public static void loadConfig() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Properties props = new Properties();
                props.load(reader);
                String cape = props.getProperty("cape", "SANARA_V2_ANIMATED");
                if ("ANIMATED".equals(cape)) {
                    currentCape = CapeType.SANARA_ANIMATED;
                } else if ("STATIC".equals(cape)) {
                    currentCape = CapeType.SANARA_STATIC;
                } else {
                    currentCape = CapeType.valueOf(cape);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void saveConfig() {
        KiemConfig.save();
    }
}
