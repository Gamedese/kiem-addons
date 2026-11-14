package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

public class KiemConfig {
    public static File getConfigFile() {
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("kiem.properties").toFile();
        } catch (Throwable t) {
            return new File("config/kiem.properties");
        }
    }

    // Language Settings (TR / EN)
    public static String language = "tr"; // "tr" or "en"
    public static boolean hasSelectedLanguage = false;

    // Chat QoL Settings
    public static boolean infiniteChat = true;
    public static boolean chatPeekEnabled = true;
    public static int chatPeekKey = InputConstants.KEY_Z; // Default GLFW_KEY_Z (90)
    public static boolean compactMessages = true;
    public static boolean copyChatEnabled = true;

    // Tooltip QoL Settings
    public static boolean tooltipScrollEnabled = true;
    public static boolean tooltipScaleEnabled = true;
    public static float tooltipScale = 1.0f;

    // Smooth Zoom Settings
    public static boolean zoomEnabled = true;
    public static int zoomKey = InputConstants.KEY_C; // Default GLFW_KEY_C (67)
    public static double zoomDefaultFactor = 4.0; // 4.0x default
    public static boolean zoomScrollEnabled = true;
    public static double zoomStep = 0.5;
    public static double zoomSpeed = 16.0; // Higher = faster transition, lower = more cinematic
    public static boolean zoomSensitivityScale = false; // Scale mouse speed with zoom (default: false)
    public static boolean zoomCinematicCamera = true; // Inertia gliding camera effect
    public static boolean zoomCinematicBars = false; // Sinematik siyah barlar (zoom yapıldığında)
    public static boolean cinematicBarsAlways = false; // Sinematik siyah barlar (her zaman açık)
    public static float cinematicBarSize = 0.12f; // Ekran yüksekliğine göre bar boyutu oranı (0.04 - 0.30)
    // Visual Stuffs: Küçük Adamlar (Mini Models)
    public static boolean selfScaleEnabled = false;
    public static float selfScaleX = 0.5f;
    public static float selfScaleY = 0.5f;
    public static float selfScaleZ = 0.5f;
    public static boolean selfScaleCamera = true; // Kamerayı Y ile birlikte aşşağı indir

    public static boolean othersScaleEnabled = false;
    public static float othersScaleX = 0.5f;
    public static float othersScaleY = 0.5f;
    public static float othersScaleZ = 0.5f;

    // Visual Stuffs: Custom Viewmodel & Vuruş Yavaşlatıcı
    public static boolean viewmodelEnabled = false;
    public static float itemSize = 0.0f; // -1.5 to 1.5
    public static boolean itemScaleSwing = true; // Also scale swing
    public static float itemPosX = 0.0f; // -1.5 to 1.5
    public static float itemPosY = 0.0f; // -1.5 to 1.5
    public static float itemPosZ = 0.0f; // -1.5 to 1.5
    public static float itemYaw = 0.0f; // -180 to 180
    public static float itemPitch = 0.0f; // -180 to 180
    public static float itemRoll = 0.0f; // -180 to 180
    public static float swingSpeed = 0.0f; // -5.0 to 1.0 (negative = slower/heavy visual motion)
    public static boolean dontSkipAnimation = false; // Animasyon başa geçmeden bitene kadar devam eder

    // Menu Hotkey
    public static int menuKey = InputConstants.KEY_F6; // Default GLFW_KEY_F6 (295)

    // Anti-AFK Settings
    public static boolean antiAfkEnabled = false;
    public static int antiAfkMinutes = 3; // 1, 2, 3, 5, 10
    public static String antiAfkAction = "SHIFT"; // "SHIFT", "JUMP", "CLICK"

    // Better Third Person Settings
    public static boolean betterThirdPersonEnabled = false;
    public static float thirdPersonSmoothness = 0.5f; // 0.0 - 1.0 (lag/soft movement)
    public static boolean thirdPersonSpeedLag = true; // Speed inertia/distance stretch
    public static boolean shoulderSurfingEnabled = true; // Shoulder offset
    public static float shoulderSide = 1.0f; // 1.0 = right, -1.0 = left
    public static int shoulderSwapKey = InputConstants.KEY_V; // Default V (customizable)
    public static float thirdPersonDistance = 4.0f; // 2.0 to 8.0 blocks

    // AFK Cinematics Settings
    public static boolean afkCinematicsEnabled = true;
    public static int afkCinematicsSeconds = 120; // 120 seconds (2 minutes default)
    public static boolean afkRaycastEnabled = true; // Toggleable wall collision check
    public static boolean afkCinematicBars = true; // Black letterbox bars during AFK

    // Custom Cape
    public static String customCapeFileName = "";
    public static boolean capeTrueColor = true; // Emissive rendering to preserve full GIF brightness and avoid directional dimming

    // Local aura (independent of shoulder pets and capes)
    public static String activeAura = "NONE"; // "NONE", "SAKURA_LOP", "VOID_GARGOYLE"
    public static boolean sakuraAuraEnabled = false;
    public static boolean voidGargoyleAuraEnabled = false;
    private static String lastActiveAura = "NONE";

    // Sakura Lop Aura settings
    private static final float[] DEFAULT_SAKURA_HEIGHTS = {0.63f, 0.954f, 1.278f};
    private static final float[] DEFAULT_SAKURA_RADII = {0.74f, 0.84f, 0.79f};
    public static float sakuraAuraScale = 0.8f;
    public static float sakuraAuraSpeed = 1f;
    public static float sakuraAuraBob = 1f;
    public static float[] sakuraAuraHeights = DEFAULT_SAKURA_HEIGHTS.clone();
    public static float[] sakuraAuraRadii = DEFAULT_SAKURA_RADII.clone();
    public static int sakuraAuraPetals = 2; // 0=off, 1=low, 2=normal, 3=high
    public static int sakuraAuraGroundBloom = 3; // 0=off, 1=sparse, 2=normal, 3=full ring

    // Obsidian Void Gargoyle Aura settings
    public static float voidGargoyleScale = 0.85f; // bounds 0.3f .. 1.5f
    public static float voidGargoyleSpeed = 1.0f; // bounds 0.25f .. 2.0f
    public static float voidGargoyleBob = 1.0f; // bounds 0.0f .. 3.0f
    private static final float[] DEFAULT_GARGOYLE_HEIGHTS = {0.70f, 1.05f, 1.40f};
    private static final float[] DEFAULT_GARGOYLE_RADII = {1.12f, 1.25f, 1.18f};
    public static float[] voidGargoyleHeights = DEFAULT_GARGOYLE_HEIGHTS.clone();
    public static float[] voidGargoyleRadii = DEFAULT_GARGOYLE_RADII.clone();
    public static int voidGargoyleEmbers = 2; // 0=Off, 1=Low, 2=Normal, 3=High
    public static int voidGargoyleGroundSmoke = 2; // 0=Off, 1=Subtle, 2=Normal, 3=Heavy
    public static int voidGargoyleJawRate = 1; // 0=Rare, 1=Normal, 2=Frequent

    public static void setActiveAura(String aura) {
        if (aura == null || aura.trim().isEmpty()) {
            activeAura = "NONE";
        } else {
            activeAura = aura.trim().toUpperCase();
        }
        syncAuraBooleans();
    }

    public static void syncAuraBooleans() {
        if (!activeAura.equals(lastActiveAura)) {
            sakuraAuraEnabled = "SAKURA_LOP".equals(activeAura);
            voidGargoyleAuraEnabled = "VOID_GARGOYLE".equals(activeAura);
            lastActiveAura = activeAura;
            return;
        }
        if (voidGargoyleAuraEnabled && !"VOID_GARGOYLE".equals(activeAura)) {
            activeAura = "VOID_GARGOYLE";
        } else if (sakuraAuraEnabled && !"SAKURA_LOP".equals(activeAura)) {
            activeAura = "SAKURA_LOP";
        } else if (!voidGargoyleAuraEnabled && "VOID_GARGOYLE".equals(activeAura)) {
            activeAura = "NONE";
        } else if (!sakuraAuraEnabled && "SAKURA_LOP".equals(activeAura)) {
            activeAura = "NONE";
        }
        sakuraAuraEnabled = "SAKURA_LOP".equals(activeAura);
        voidGargoyleAuraEnabled = "VOID_GARGOYLE".equals(activeAura);
        lastActiveAura = activeAura;
    }

    // Shoulder Pet
    public static String shoulderPetType = "OFF";
    public static String shoulderPetSide = "RIGHT";
    public static boolean inventoryCurseWithSword = true;
    public static String dragonVariant = "TOOTHLESS"; // "TOOTHLESS" or "ENDER"

    static {
        load();
    }

    public static void load() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            Properties props = new Properties();
            props.load(reader);

            language = props.getProperty("language", "tr");
            hasSelectedLanguage = Boolean.parseBoolean(props.getProperty("has_selected_language", "false"));

            infiniteChat = Boolean.parseBoolean(props.getProperty("chat.infinite_history", "true"));
            chatPeekEnabled = Boolean.parseBoolean(props.getProperty("chat.peek_enabled", "true"));
            try {
                chatPeekKey = Integer.parseInt(props.getProperty("chat.peek_key", String.valueOf(InputConstants.KEY_Z)));
            } catch (NumberFormatException e) {
                chatPeekKey = InputConstants.KEY_Z;
            }
            compactMessages = Boolean.parseBoolean(props.getProperty("chat.compact_messages", "true"));
            copyChatEnabled = Boolean.parseBoolean(props.getProperty("chat.copy_click", "true"));

            tooltipScrollEnabled = Boolean.parseBoolean(props.getProperty("tooltip.scroll_enabled", "true"));
            tooltipScaleEnabled = Boolean.parseBoolean(props.getProperty("tooltip.scale_enabled", "true"));
            try {
                tooltipScale = Float.parseFloat(props.getProperty("tooltip.scale", "1.0"));
                if (tooltipScale < 0.35f) tooltipScale = 0.35f;
                if (tooltipScale > 1.6f) tooltipScale = 1.6f;
            } catch (NumberFormatException e) {
                tooltipScale = 1.0f;
            }

            zoomEnabled = Boolean.parseBoolean(props.getProperty("zoom.enabled", "true"));
            try {
                zoomKey = Integer.parseInt(props.getProperty("zoom.key", String.valueOf(InputConstants.KEY_C)));
            } catch (NumberFormatException e) {
                zoomKey = InputConstants.KEY_C;
            }
            try {
                zoomDefaultFactor = Double.parseDouble(props.getProperty("zoom.default_factor", "4.0"));
                if (zoomDefaultFactor < 1.25) zoomDefaultFactor = 1.25;
                if (zoomDefaultFactor > 15.0) zoomDefaultFactor = 15.0;
            } catch (NumberFormatException e) {
                zoomDefaultFactor = 4.0;
            }
            zoomScrollEnabled = Boolean.parseBoolean(props.getProperty("zoom.scroll_enabled", "true"));
            try {
                zoomStep = Double.parseDouble(props.getProperty("zoom.step", "0.5"));
                if (zoomStep < 0.1) zoomStep = 0.1;
                if (zoomStep > 2.0) zoomStep = 2.0;
            } catch (NumberFormatException e) {
                zoomStep = 0.5;
            }
            try {
                zoomSpeed = Double.parseDouble(props.getProperty("zoom.speed", "16.0"));
                if (zoomSpeed < 4.0) zoomSpeed = 4.0;
                if (zoomSpeed > 40.0) zoomSpeed = 40.0;
            } catch (NumberFormatException e) {
                zoomSpeed = 16.0;
            }
            zoomSensitivityScale = Boolean.parseBoolean(props.getProperty("zoom.sensitivity_scale", "false"));
            zoomCinematicCamera = Boolean.parseBoolean(props.getProperty("zoom.cinematic_camera", "true"));
            zoomCinematicBars = Boolean.parseBoolean(props.getProperty("zoom.cinematic_bars", "false"));
            cinematicBarsAlways = Boolean.parseBoolean(props.getProperty("zoom.cinematic_always", "false"));
            try {
                cinematicBarSize = Float.parseFloat(props.getProperty("zoom.cinematic_bar_size", "0.12"));
                if (cinematicBarSize < 0.04f) cinematicBarSize = 0.04f;
                if (cinematicBarSize > 0.35f) cinematicBarSize = 0.35f;
            } catch (Exception e) {
                cinematicBarSize = 0.12f;
            }

            // Visual Stuffs: Küçük Adamlar
            selfScaleEnabled = Boolean.parseBoolean(props.getProperty("visuals.self_scale_enabled", "false"));
            try {
                selfScaleX = Float.parseFloat(props.getProperty("visuals.self_scale_x", "0.5"));
                selfScaleY = Float.parseFloat(props.getProperty("visuals.self_scale_y", "0.5"));
                selfScaleZ = Float.parseFloat(props.getProperty("visuals.self_scale_z", "0.5"));
            } catch (Exception e) {
                selfScaleX = 0.5f; selfScaleY = 0.5f; selfScaleZ = 0.5f;
            }
            selfScaleCamera = Boolean.parseBoolean(props.getProperty("visuals.self_scale_camera", "true"));

            othersScaleEnabled = Boolean.parseBoolean(props.getProperty("visuals.others_scale_enabled", "false"));
            try {
                othersScaleX = Float.parseFloat(props.getProperty("visuals.others_scale_x", "0.5"));
                othersScaleY = Float.parseFloat(props.getProperty("visuals.others_scale_y", "0.5"));
                othersScaleZ = Float.parseFloat(props.getProperty("visuals.others_scale_z", "0.5"));
            } catch (Exception e) {
                othersScaleX = 0.5f; othersScaleY = 0.5f; othersScaleZ = 0.5f;
            }

            // Visual Stuffs: Custom Viewmodel & Vuruş Yavaşlatıcı
            viewmodelEnabled = Boolean.parseBoolean(props.getProperty("visuals.viewmodel_enabled", "false"));
            try {
                itemSize = Float.parseFloat(props.getProperty("visuals.item_size", "0.0"));
                itemScaleSwing = Boolean.parseBoolean(props.getProperty("visuals.item_scale_swing", "true"));
                itemPosX = Float.parseFloat(props.getProperty("visuals.item_pos_x", "0.0"));
                itemPosY = Float.parseFloat(props.getProperty("visuals.item_pos_y", "0.0"));
                itemPosZ = Float.parseFloat(props.getProperty("visuals.item_pos_z", "0.0"));
                itemYaw = Float.parseFloat(props.getProperty("visuals.item_yaw", "0.0"));
                itemPitch = Float.parseFloat(props.getProperty("visuals.item_pitch", "0.0"));
                itemRoll = Float.parseFloat(props.getProperty("visuals.item_roll", "0.0"));
                swingSpeed = Float.parseFloat(props.getProperty("visuals.swing_speed", "0.0"));
                dontSkipAnimation = Boolean.parseBoolean(props.getProperty("visuals.dont_skip_animation", "false"));
            } catch (Exception e) {
                itemSize = 0.0f; itemScaleSwing = true;
                itemPosX = 0.0f; itemPosY = 0.0f; itemPosZ = 0.0f;
                itemYaw = 0.0f; itemPitch = 0.0f; itemRoll = 0.0f;
                swingSpeed = 0.0f; dontSkipAnimation = false;
            }

            try {
                menuKey = Integer.parseInt(props.getProperty("menu.key", String.valueOf(InputConstants.KEY_F6)));
            } catch (Exception e) {
                menuKey = InputConstants.KEY_F6;
            }

            antiAfkEnabled = Boolean.parseBoolean(props.getProperty("antiafk.enabled", "false"));
            try {
                antiAfkMinutes = Integer.parseInt(props.getProperty("antiafk.minutes", "3"));
            } catch (Exception e) {
                antiAfkMinutes = 3;
            }
            antiAfkAction = props.getProperty("antiafk.action", "SHIFT");

            // Load Better Third Person Settings
            betterThirdPersonEnabled = Boolean.parseBoolean(props.getProperty("thirdperson.enabled", "false"));
            try {
                thirdPersonSmoothness = Float.parseFloat(props.getProperty("thirdperson.smoothness", "0.5"));
            } catch (Exception e) {
                thirdPersonSmoothness = 0.5f;
            }
            thirdPersonSpeedLag = Boolean.parseBoolean(props.getProperty("thirdperson.speed_lag", "true"));
            shoulderSurfingEnabled = Boolean.parseBoolean(props.getProperty("thirdperson.shoulder_surfing", "true"));
            try {
                shoulderSide = Float.parseFloat(props.getProperty("thirdperson.shoulder_side", "1.0"));
            } catch (Exception e) {
                shoulderSide = 1.0f;
            }
            try {
                shoulderSwapKey = Integer.parseInt(props.getProperty("thirdperson.shoulder_swap_key", String.valueOf(InputConstants.KEY_V)));
            } catch (Exception e) {
                shoulderSwapKey = InputConstants.KEY_V;
            }
            try {
                thirdPersonDistance = Float.parseFloat(props.getProperty("thirdperson.distance", "4.0"));
            } catch (Exception e) {
                thirdPersonDistance = 4.0f;
            }

            // Load AFK Cinematics
            afkCinematicsEnabled = Boolean.parseBoolean(props.getProperty("afk.cinematics_enabled", "true"));
            try {
                afkCinematicsSeconds = Integer.parseInt(props.getProperty("afk.cinematics_seconds", "120"));
            } catch (Exception e) {
                afkCinematicsSeconds = 120;
            }
            afkRaycastEnabled = Boolean.parseBoolean(props.getProperty("afk.raycast_enabled", "true"));
            afkCinematicBars = Boolean.parseBoolean(props.getProperty("afk.cinematic_bars", "true"));

            // Load Recents
            String recentsStr = props.getProperty("recent_modules", "afk_cinematics,better_third_person,custom_viewmodel");
            recentModules.clear();
            if (recentsStr != null && !recentsStr.isEmpty()) {
                for (String part : recentsStr.split(",")) {
                    if (!part.trim().isEmpty() && !recentModules.contains(part.trim())) {
                        recentModules.add(part.trim());
                    }
                }
            }
            if (recentModules.isEmpty()) {
                recentModules.add("afk_cinematics");
                recentModules.add("better_third_person");
                recentModules.add("custom_viewmodel");
            }

            // Load Cape
            customCapeFileName = props.getProperty("cape.custom_name", "");
            capeTrueColor = Boolean.parseBoolean(props.getProperty("cape.true_color", "true"));
            String cape = props.getProperty("cape", "SANARA_V2_ANIMATED");
            if ("ANIMATED".equals(cape)) {
                CapeManager.setCurrentCapeDirect(CapeManager.CapeType.SANARA_ANIMATED);
            } else if ("STATIC".equals(cape)) {
                CapeManager.setCurrentCapeDirect(CapeManager.CapeType.SANARA_STATIC);
            } else {
                try {
                    CapeManager.setCurrentCapeDirect(CapeManager.CapeType.valueOf(cape));
                } catch (Exception ignored) {
                }
            }

            String loadedActive = props.getProperty("aura.active");
            sakuraAuraEnabled = Boolean.parseBoolean(props.getProperty("aura.sakura_lop", "false"));
            voidGargoyleAuraEnabled = Boolean.parseBoolean(props.getProperty("aura.void_gargoyle", "false"));

            if (loadedActive != null && !loadedActive.trim().isEmpty()) {
                activeAura = loadedActive.trim().toUpperCase();
            } else {
                if (sakuraAuraEnabled) {
                    activeAura = "SAKURA_LOP";
                } else if (voidGargoyleAuraEnabled) {
                    activeAura = "VOID_GARGOYLE";
                } else {
                    activeAura = "NONE";
                }
            }
            sakuraAuraEnabled = "SAKURA_LOP".equals(activeAura);
            voidGargoyleAuraEnabled = "VOID_GARGOYLE".equals(activeAura);
            lastActiveAura = activeAura;

            sakuraAuraScale = boundedFloat(props, "aura.sakura_lop.scale", 0.8f, 0.3f, 1.4f);
            sakuraAuraSpeed = boundedFloat(props, "aura.sakura_lop.speed", 1f, 0.25f, 2f);
            sakuraAuraBob = boundedFloat(props, "aura.sakura_lop.bob", 1f, 0f, 3f);
            for (int i = 0; i < 3; i++) {
                sakuraAuraHeights[i] = boundedFloat(props, "aura.sakura_lop.height." + i,
                        DEFAULT_SAKURA_HEIGHTS[i], 0.15f, 2.2f);
                sakuraAuraRadii[i] = boundedFloat(props, "aura.sakura_lop.radius." + i,
                        DEFAULT_SAKURA_RADII[i], 0.4f, 1.5f);
            }
            try {
                sakuraAuraPetals = Math.clamp(Integer.parseInt(props.getProperty("aura.sakura_lop.petals", "2")), 0, 3);
            } catch (NumberFormatException exception) {
                sakuraAuraPetals = 2;
            }
            try {
                sakuraAuraGroundBloom = Math.clamp(Integer.parseInt(props.getProperty("aura.sakura_lop.ground_bloom", "3")), 0, 3);
            } catch (NumberFormatException exception) {
                sakuraAuraGroundBloom = 3;
            }

            voidGargoyleScale = boundedFloat(props, "aura.void_gargoyle.scale", 0.85f, 0.3f, 1.5f);
            voidGargoyleSpeed = boundedFloat(props, "aura.void_gargoyle.speed", 1.0f, 0.25f, 2.0f);
            voidGargoyleBob = boundedFloat(props, "aura.void_gargoyle.bob", 1.0f, 0.0f, 3.0f);
            for (int i = 0; i < 3; i++) {
                voidGargoyleHeights[i] = boundedFloat(props, "aura.void_gargoyle.height." + i,
                        DEFAULT_GARGOYLE_HEIGHTS[i], 0.15f, 2.2f);
                voidGargoyleRadii[i] = boundedFloat(props, "aura.void_gargoyle.radius." + i,
                        DEFAULT_GARGOYLE_RADII[i], 0.4f, 2.2f);
            }
            try {
                voidGargoyleEmbers = Math.clamp(Integer.parseInt(props.getProperty("aura.void_gargoyle.embers", "2")), 0, 3);
            } catch (NumberFormatException exception) {
                voidGargoyleEmbers = 2;
            }
            try {
                voidGargoyleGroundSmoke = Math.clamp(Integer.parseInt(props.getProperty("aura.void_gargoyle.ground_smoke", "2")), 0, 3);
            } catch (NumberFormatException exception) {
                voidGargoyleGroundSmoke = 2;
            }
            try {
                voidGargoyleJawRate = Math.clamp(Integer.parseInt(props.getProperty("aura.void_gargoyle.jaw_rate", "1")), 0, 2);
            } catch (NumberFormatException exception) {
                voidGargoyleJawRate = 1;
            }

            // Load Shoulder Pet
            shoulderPetType = props.getProperty("pet.type", "OFF");
            shoulderPetSide = props.getProperty("pet.side", "RIGHT");
            inventoryCurseWithSword = Boolean.parseBoolean(props.getProperty("pet.inventory_curse_sword", "true"));
            dragonVariant = props.getProperty("pet.dragon_variant", "TOOTHLESS");
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        File configFile = getConfigFile();
        try {
            if (configFile.getParentFile() != null) {
                configFile.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                syncAuraBooleans();
                Properties props = new Properties();
                props.setProperty("cape", CapeManager.getCurrentCape().name());
                props.setProperty("cape.custom_name", customCapeFileName);
                props.setProperty("cape.true_color", String.valueOf(capeTrueColor));
                props.setProperty("aura.active", activeAura);
                props.setProperty("aura.sakura_lop", String.valueOf(sakuraAuraEnabled));
                props.setProperty("aura.sakura_lop.scale", String.valueOf(sakuraAuraScale));
                props.setProperty("aura.sakura_lop.speed", String.valueOf(sakuraAuraSpeed));
                props.setProperty("aura.sakura_lop.bob", String.valueOf(sakuraAuraBob));
                for (int i = 0; i < 3; i++) {
                    props.setProperty("aura.sakura_lop.height." + i, String.valueOf(sakuraAuraHeights[i]));
                    props.setProperty("aura.sakura_lop.radius." + i, String.valueOf(sakuraAuraRadii[i]));
                }
                props.setProperty("aura.sakura_lop.petals", String.valueOf(sakuraAuraPetals));
                props.setProperty("aura.sakura_lop.ground_bloom", String.valueOf(sakuraAuraGroundBloom));
                props.setProperty("aura.void_gargoyle", String.valueOf(voidGargoyleAuraEnabled));
                props.setProperty("aura.void_gargoyle.scale", String.valueOf(voidGargoyleScale));
                props.setProperty("aura.void_gargoyle.speed", String.valueOf(voidGargoyleSpeed));
                props.setProperty("aura.void_gargoyle.bob", String.valueOf(voidGargoyleBob));
                for (int i = 0; i < 3; i++) {
                    props.setProperty("aura.void_gargoyle.height." + i, String.valueOf(voidGargoyleHeights[i]));
                    props.setProperty("aura.void_gargoyle.radius." + i, String.valueOf(voidGargoyleRadii[i]));
                }
                props.setProperty("aura.void_gargoyle.embers", String.valueOf(voidGargoyleEmbers));
                props.setProperty("aura.void_gargoyle.ground_smoke", String.valueOf(voidGargoyleGroundSmoke));
                props.setProperty("aura.void_gargoyle.jaw_rate", String.valueOf(voidGargoyleJawRate));
                props.setProperty("pet.type", shoulderPetType);
                props.setProperty("pet.side", shoulderPetSide);
                props.setProperty("pet.inventory_curse_sword", String.valueOf(inventoryCurseWithSword));
                props.setProperty("pet.dragon_variant", dragonVariant);
                props.setProperty("chat.infinite_history", String.valueOf(infiniteChat));
                props.setProperty("chat.peek_enabled", String.valueOf(chatPeekEnabled));
                props.setProperty("chat.peek_key", String.valueOf(chatPeekKey));
                props.setProperty("chat.compact_messages", String.valueOf(compactMessages));
                props.setProperty("chat.copy_click", String.valueOf(copyChatEnabled));
                props.setProperty("tooltip.scroll_enabled", String.valueOf(tooltipScrollEnabled));
                props.setProperty("tooltip.scale_enabled", String.valueOf(tooltipScaleEnabled));
                props.setProperty("tooltip.scale", String.valueOf(tooltipScale));

                props.setProperty("zoom.enabled", String.valueOf(zoomEnabled));
                props.setProperty("zoom.key", String.valueOf(zoomKey));
                props.setProperty("zoom.default_factor", String.valueOf(zoomDefaultFactor));
                props.setProperty("zoom.scroll_enabled", String.valueOf(zoomScrollEnabled));
                props.setProperty("zoom.step", String.valueOf(zoomStep));
                props.setProperty("zoom.speed", String.valueOf(zoomSpeed));
                props.setProperty("zoom.sensitivity_scale", String.valueOf(zoomSensitivityScale));
                props.setProperty("zoom.cinematic_camera", String.valueOf(zoomCinematicCamera));
                props.setProperty("zoom.cinematic_bars", String.valueOf(zoomCinematicBars));
                props.setProperty("zoom.cinematic_always", String.valueOf(cinematicBarsAlways));
                props.setProperty("zoom.cinematic_bar_size", String.valueOf(cinematicBarSize));

                // Save Visual Stuffs
                props.setProperty("visuals.self_scale_enabled", String.valueOf(selfScaleEnabled));
                props.setProperty("visuals.self_scale_x", String.valueOf(selfScaleX));
                props.setProperty("visuals.self_scale_y", String.valueOf(selfScaleY));
                props.setProperty("visuals.self_scale_z", String.valueOf(selfScaleZ));
                props.setProperty("visuals.self_scale_camera", String.valueOf(selfScaleCamera));

                props.setProperty("visuals.others_scale_enabled", String.valueOf(othersScaleEnabled));
                props.setProperty("visuals.others_scale_x", String.valueOf(othersScaleX));
                props.setProperty("visuals.others_scale_y", String.valueOf(othersScaleY));
                props.setProperty("visuals.others_scale_z", String.valueOf(othersScaleZ));

                props.setProperty("visuals.viewmodel_enabled", String.valueOf(viewmodelEnabled));
                props.setProperty("visuals.item_size", String.valueOf(itemSize));
                props.setProperty("visuals.item_scale_swing", String.valueOf(itemScaleSwing));
                props.setProperty("visuals.item_pos_x", String.valueOf(itemPosX));
                props.setProperty("visuals.item_pos_y", String.valueOf(itemPosY));
                props.setProperty("visuals.item_pos_z", String.valueOf(itemPosZ));
                props.setProperty("visuals.item_yaw", String.valueOf(itemYaw));
                props.setProperty("visuals.item_pitch", String.valueOf(itemPitch));
                props.setProperty("visuals.item_roll", String.valueOf(itemRoll));
                props.setProperty("visuals.swing_speed", String.valueOf(swingSpeed));
                props.setProperty("visuals.dont_skip_animation", String.valueOf(dontSkipAnimation));

                props.setProperty("menu.key", String.valueOf(menuKey));
                props.setProperty("antiafk.enabled", String.valueOf(antiAfkEnabled));
                props.setProperty("antiafk.minutes", String.valueOf(antiAfkMinutes));
                props.setProperty("antiafk.action", antiAfkAction);

                // Save Better Third Person
                props.setProperty("thirdperson.enabled", String.valueOf(betterThirdPersonEnabled));
                props.setProperty("thirdperson.smoothness", String.valueOf(thirdPersonSmoothness));
                props.setProperty("thirdperson.speed_lag", String.valueOf(thirdPersonSpeedLag));
                props.setProperty("thirdperson.shoulder_surfing", String.valueOf(shoulderSurfingEnabled));
                props.setProperty("thirdperson.shoulder_side", String.valueOf(shoulderSide));
                props.setProperty("thirdperson.shoulder_swap_key", String.valueOf(shoulderSwapKey));
                props.setProperty("thirdperson.distance", String.valueOf(thirdPersonDistance));

                // Save AFK Cinematics
                props.setProperty("afk.cinematics_enabled", String.valueOf(afkCinematicsEnabled));
                props.setProperty("afk.cinematics_seconds", String.valueOf(afkCinematicsSeconds));
                props.setProperty("afk.raycast_enabled", String.valueOf(afkRaycastEnabled));
                props.setProperty("afk.cinematic_bars", String.valueOf(afkCinematicBars));

                // Save Language
                props.setProperty("language", language);
                props.setProperty("has_selected_language", String.valueOf(hasSelectedLanguage));

                // Save Recents
                props.setProperty("recent_modules", String.join(",", recentModules));

                props.store(writer, "Kiem Addons Settings");
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean isChatPeekActive() {
        if (!chatPeekEnabled) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || KiemCompat.getScreen(mc) != null) return false;
        return InputConstants.isKeyDown(mc.getWindow(), chatPeekKey);
    }

    // Recents Tracker for Welcome Screen
    public static java.util.List<String> recentModules = new java.util.ArrayList<>();

    public static void touchModule(String moduleId) {
        if (moduleId == null || moduleId.isEmpty()) return;
        recentModules.remove(moduleId);
        recentModules.add(0, moduleId);
        while (recentModules.size() > 5) {
            recentModules.remove(recentModules.size() - 1);
        }
        save();
    }

    private static float boundedFloat(Properties props, String key, float fallback, float min, float max) {
        try {
            float value = Float.parseFloat(props.getProperty(key, String.valueOf(fallback)));
            return Float.isFinite(value) ? Math.clamp(value, min, max) : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    /** Reset aura customization only; the caller decides whether to save. */
    public static void resetSakuraAuraSettings() {
        sakuraAuraScale = 0.8f;
        sakuraAuraSpeed = 1f;
        sakuraAuraBob = 1f;
        sakuraAuraHeights = DEFAULT_SAKURA_HEIGHTS.clone();
        sakuraAuraRadii = DEFAULT_SAKURA_RADII.clone();
        sakuraAuraPetals = 2;
        sakuraAuraGroundBloom = 3;
    }

    /** Reset Void Gargoyle aura customization only; caller decides whether to save. */
    public static void resetVoidGargoyleSettings() {
        voidGargoyleScale = 0.85f;
        voidGargoyleSpeed = 1.0f;
        voidGargoyleBob = 1.0f;
        voidGargoyleHeights = DEFAULT_GARGOYLE_HEIGHTS.clone();
        voidGargoyleRadii = DEFAULT_GARGOYLE_RADII.clone();
        voidGargoyleEmbers = 2;
        voidGargoyleGroundSmoke = 2;
        voidGargoyleJawRate = 1;
    }

    public static void resetToDefaults() {
        // Chat
        infiniteChat = true;
        chatPeekEnabled = true;
        chatPeekKey = InputConstants.KEY_Z;
        compactMessages = true;
        copyChatEnabled = true;

        // Tooltip
        tooltipScrollEnabled = true;
        tooltipScaleEnabled = true;
        tooltipScale = 1.0f;

        // Zoom
        zoomEnabled = true;
        zoomKey = InputConstants.KEY_C;
        zoomDefaultFactor = 4.0;
        zoomScrollEnabled = true;
        zoomStep = 0.5;
        zoomSpeed = 16.0;
        zoomSensitivityScale = false;
        zoomCinematicCamera = true;
        zoomCinematicBars = false;
        cinematicBarsAlways = false;
        cinematicBarSize = 0.12f;

        // Visuals: Mini Models
        selfScaleEnabled = false;
        selfScaleX = 0.5f;
        selfScaleY = 0.5f;
        selfScaleZ = 0.5f;
        selfScaleCamera = true;
        othersScaleEnabled = false;
        othersScaleX = 0.5f;
        othersScaleY = 0.5f;
        othersScaleZ = 0.5f;

        // Visuals: Viewmodel
        viewmodelEnabled = false;
        itemSize = 0.0f;
        itemScaleSwing = true;
        itemPosX = 0.0f;
        itemPosY = 0.0f;
        itemPosZ = 0.0f;
        itemYaw = 0.0f;
        itemPitch = 0.0f;
        itemRoll = 0.0f;
        swingSpeed = 0.0f;
        dontSkipAnimation = false;

        // Menu Key
        menuKey = InputConstants.KEY_F6;

        // Anti-AFK
        antiAfkEnabled = false;
        antiAfkMinutes = 3;
        antiAfkAction = "SHIFT";

        // Better Third Person
        betterThirdPersonEnabled = false;
        thirdPersonSmoothness = 0.5f;
        thirdPersonSpeedLag = true;
        shoulderSurfingEnabled = true;
        shoulderSide = 1.0f;
        shoulderSwapKey = InputConstants.KEY_V;
        thirdPersonDistance = 4.0f;

        // AFK Cinematics
        afkCinematicsEnabled = true;
        afkCinematicsSeconds = 120;
        afkRaycastEnabled = true;
        afkCinematicBars = true;

        // Cape
        CapeManager.setCurrentCape(CapeManager.CapeType.SANARA_V2_ANIMATED);

        activeAura = "NONE";
        sakuraAuraEnabled = false;
        voidGargoyleAuraEnabled = false;
        lastActiveAura = "NONE";
        resetSakuraAuraSettings();
        resetVoidGargoyleSettings();

        // Shoulder Pet
        shoulderPetType = "OFF";
        shoulderPetSide = "RIGHT";
        dragonVariant = "TOOTHLESS";

        // Reset recents to clean defaults
        recentModules.clear();
        recentModules.add("afk_cinematics");
        recentModules.add("better_third_person");
        recentModules.add("custom_viewmodel");

        save();
    }

    public static String getKeyName(int keyCode) {
        if (keyCode == InputConstants.UNKNOWN.getValue()) return "Yok";
        try {
            return InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString();
        } catch (Exception e) {
            return "Key " + keyCode;
        }
    }
}
