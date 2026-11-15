package com.example.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Multi-theme SDF-backed 9-slice glass panels, capsule pills, circular slots,
 * radial glows, drifting sakura petals, and custom TTF font helpers.
 */
public final class BoutiqueDraw {

    public enum Theme {
        PASTEL_CREAM(
                "pastel_cream", "Pastel Cream", true,
                0xC8EDE4EA, 0xD8E3D7E0, 0x70F4B8CC, 0xFFE6C8D6,
                0xF2FBF7FA, 0xF5F3ECF1, 0x7AE0C7D6, 0x55FFFFFF,
                0xF4F7EFF5, 0xF8F2E6EE, 0x66DEC5D4, 0x44FFFFFF,
                0xFFFAEAF1, 0xFFF7E4ED, 0xFFE69DB8, 0x66FFFFFF,
                0xFFFCF0F6, 0xFFF3E2EC, 0xFFEBB6CA, 0x55FFFFFF,
                0xFFEFE4EC, 0xFFCDB4C4, 0x22FFFFFF,
                0xFFF0E4EC, 0xFFC8AEC0, 0x22FFFFFF,
                0xFFF7DCE8, 0xFFE59BB8, 0x44FFFFFF,
                0xFFF4C4D8, 0xFFE08DAE, 0x55FFFFFF,
                0xFFF5EBF1, 0xFFD9C0D0,
                0xFFF7D8E6, 0xFFDC8EAD,
                0xFF231923, 0xFF6B5968, 0xFF938190, 0xFFC85D88, 0xFF231923, 0xFF288E56
        ),
        WARM_IVORY(
                "warm_ivory", "Warm Ivory", true,
                0xC8ECE4DA, 0xD8E2D7CA, 0x70F0BC9C, 0xFFE8D0BE,
                0xF2FCF9F3, 0xF5F5EFE5, 0x7AE0CDBB, 0x55FFFFFF,
                0xF4F7F1E8, 0xF8F1E8DC, 0x66DECBB8, 0x44FFFFFF,
                0xFFFAF0E6, 0xFFF5E6D8, 0xFFDF9874, 0x66FFFFFF,
                0xFFFCF4EC, 0xFFF3E7DA, 0xFFE6B598, 0x55FFFFFF,
                0xFFF0E7DC, 0xFFCDB9A6, 0x22FFFFFF,
                0xFFF2EAE0, 0xFFC6B19D, 0x22FFFFFF,
                0xFFF7E2D2, 0xFFE29D78, 0x44FFFFFF,
                0xFFF4C8B2, 0xFFDA885E, 0x55FFFFFF,
                0xFFF5ECE2, 0xFFD5C1AE,
                0xFFF6D9C6, 0xFFD98B62,
                0xFF29201B, 0xFF726157, 0xFF9B897E, 0xFFC26942, 0xFF29201B, 0xFF2C8B57
        ),
        MATCHA_CREAM(
                "matcha_cream", "Matcha Cream", true,
                0xC8E0ECE4, 0xD8D4E2D8, 0x709ED8B3, 0xFFC6DEC9,
                0xF2F5FAF6, 0xF5EBF3ED, 0x7AC1D8C8, 0x55FFFFFF,
                0xF4ECF5EE, 0xF8E3EFE7, 0x66BED6C5, 0x44FFFFFF,
                0xFFE4F4E9, 0xFFD9EDE0, 0xFF73B98D, 0x66FFFFFF,
                0xFFEBF7EE, 0xFFE0F0E5, 0xFF9CD1AF, 0x55FFFFFF,
                0xFFE4EFE7, 0xFFB2CCB9, 0x22FFFFFF,
                0xFFE7F1EA, 0xFFACC6B3, 0x22FFFFFF,
                0xFFD4EEDC, 0xFF78BD91, 0x44FFFFFF,
                0xFFBCE6CA, 0xFF63AD7E, 0x55FFFFFF,
                0xFFEAF3EC, 0xFFBBD2C2,
                0xFFCBEBD6, 0xFF6BB385,
                0xFF1B2920, 0xFF566B5E, 0xFF7F9487, 0xFF2E8552, 0xFF1B2920, 0xFF237A48
        ),
        SAKURA_INK(
                "sakura_ink", "Sakura & Ink", false,
                0xD618101F, 0xE622162A, 0x56F4B2C4, 0xFFF7C6D5,
                0xD0241A2D, 0xDC191121, 0x6EE6B0A4, 0x24FFFFFF,
                0xB82D2138, 0xC8201629, 0x55DFA89E, 0x1AFFFFFF,
                0xD642273D, 0xE22D1A2C, 0xFFF2B8C6, 0x38FFD6E2,
                0xCC392947, 0xD8291D34, 0xAAF0BFC7, 0x2EFFFFFF,
                0xC81D1526, 0x5CE0A89D, 0x16FFFFFF,
                0xAA32253E, 0x50D9A39A, 0x18FFFFFF,
                0xCC443254, 0x99F0BEC8, 0x26FFFFFF,
                0xFFF2C2CE, 0xFFFFDCE5, 0x44FFFFFF,
                0xB0291E33, 0x6AE0A89E,
                0xD0462840, 0xFFF2B8C6,
                0xFFF7EEF2, 0xFFC5B3C0, 0xFF8B7A8A, 0xFFF4B6C6, 0xFF211728, 0xFF7EE0A8
        ),
        OBSIDIAN_GOLD(
                "obsidian_gold", "Obsidian & Gold", false,
                0xD8111218, 0xE8181A22, 0x50E6C27A, 0xFFF2D492,
                0xD4171922, 0xDF11131A, 0x6EE2BE79, 0x22FFFFFF,
                0xBE1F222E, 0xCC171923, 0x55D1AE6D, 0x18FFFFFF,
                0xDA2F281C, 0xE4221D15, 0xFFF2CF88, 0x38FFE7B3,
                0xCE282C3A, 0xDA1E212C, 0xAAE8C988, 0x28FFFFFF,
                0xCA13151D, 0x5CD4B16E, 0x14FFFFFF,
                0xAE222633, 0x50CFA968, 0x16FFFFFF,
                0xCE303546, 0x99EAD096, 0x24FFFFFF,
                0xFFF2CF88, 0xFFFFE5AE, 0x44FFFFFF,
                0xB41C1F2A, 0x6AD4B16E,
                0xD2362D1D, 0xFFF2CF88,
                0xFFF6F2EA, 0xFFC2BDB2, 0xFF8A857B, 0xFFF0C97E, 0xFF1F1A12, 0xFF7EE0A8
        ),
        MIDNIGHT_CYAN(
                "midnight_cyan", "Midnight Cyan", false,
                0xD80D1522, 0xE8121E30, 0x507AD4E6, 0xFFA5ECFA,
                0xD2121D2E, 0xDE0E1624, 0x6E7AD4E6, 0x22FFFFFF,
                0xBC18263C, 0xCC121E30, 0x556BC2D6, 0x18FFFFFF,
                0xD8163346, 0xE4102636, 0xFF9BE8F5, 0x38C7F5FF,
                0xCC1F314C, 0xD817253B, 0xAA97E2F0, 0x28FFFFFF,
                0xCA0F1928, 0x5C6FC5D8, 0x14FFFFFF,
                0xAE192940, 0x5069BDD0, 0x16FFFFFF,
                0xCE233754, 0x9995E2F2, 0x24FFFFFF,
                0xFF9BE8F5, 0xFFC8F5FD, 0x44FFFFFF,
                0xB4152236, 0x6A6FC5D8,
                0xD21A394D, 0xFF9BE8F5,
                0xFFEDF7FA, 0xFFB0C9D4, 0xFF7994A3, 0xFF8BE2F2, 0xFF0E1D28, 0xFF7EE0A8
        ),
        MONOCHROME_DARK(
                "monochrome_dark", "Obsidian Mono", false,
                0xD80A0A0D, 0xE8121216, 0x38FFFFFF, 0xFFD4D4DA,
                0xD4111115, 0xE00C0C10, 0x6EFFFFFF, 0x22FFFFFF,
                0xBE18181D, 0xCC121216, 0x48FFFFFF, 0x16FFFFFF,
                0xDA26262C, 0xE41D1D23, 0xFFF5F5F7, 0x38FFFFFF,
                0xCE212128, 0xDA18181E, 0xAAFFFFFF, 0x28FFFFFF,
                0xCA101014, 0x54FFFFFF, 0x14FFFFFF,
                0xAE1A1A20, 0x48FFFFFF, 0x16FFFFFF,
                0xCE282830, 0x99FFFFFF, 0x24FFFFFF,
                0xFFF4F4F6, 0xFFFFFFFF, 0x44FFFFFF,
                0xB416161B, 0x6AFFFFFF,
                0xD2282830, 0xFFF4F4F6,
                0xFFF4F4F6, 0xFFB8B8C0, 0xFF7A7A84, 0xFFE4E4EA, 0xFF101014, 0xFF8BE8B4
        );

        public final String id;
        public final String displayName;
        public final boolean isLight;
        public final int backdropTop;
        public final int backdropBottom;
        public final int glowColor;
        public final int petalColor;

        public final int panelTop;
        public final int panelBottom;
        public final int panelBorder;
        public final int panelHighlight;

        public final int cardTop;
        public final int cardBottom;
        public final int cardBorder;
        public final int cardHighlight;

        public final int cardActiveTop;
        public final int cardActiveBottom;
        public final int cardActiveBorder;
        public final int cardActiveHighlight;

        public final int cardHoverTop;
        public final int cardHoverBottom;
        public final int cardHoverBorder;
        public final int cardHoverHighlight;

        public final int pillBarFill;
        public final int pillBarBorder;
        public final int pillBarHighlight;

        public final int pillDarkFill;
        public final int pillDarkBorder;
        public final int pillDarkHighlight;

        public final int pillHoverFill;
        public final int pillHoverBorder;
        public final int pillHoverHighlight;

        public final int pillActiveFill;
        public final int pillActiveBorder;
        public final int pillActiveHighlight;

        public final int circleFill;
        public final int circleBorder;

        public final int circleActiveFill;
        public final int circleActiveBorder;

        public final int textPrimary;
        public final int textMuted;
        public final int textDim;
        public final int textAccent;
        public final int textOnActivePill;
        public final int textPositive;

        Theme(String id, String displayName, boolean isLight,
              int backdropTop, int backdropBottom, int glowColor, int petalColor,
              int panelTop, int panelBottom, int panelBorder, int panelHighlight,
              int cardTop, int cardBottom, int cardBorder, int cardHighlight,
              int cardActiveTop, int cardActiveBottom, int cardActiveBorder, int cardActiveHighlight,
              int cardHoverTop, int cardHoverBottom, int cardHoverBorder, int cardHoverHighlight,
              int pillBarFill, int pillBarBorder, int pillBarHighlight,
              int pillDarkFill, int pillDarkBorder, int pillDarkHighlight,
              int pillHoverFill, int pillHoverBorder, int pillHoverHighlight,
              int pillActiveFill, int pillActiveBorder, int pillActiveHighlight,
              int circleFill, int circleBorder,
              int circleActiveFill, int circleActiveBorder,
              int textPrimary, int textMuted, int textDim, int textAccent, int textOnActivePill, int textPositive) {
            this.id = id;
            this.displayName = displayName;
            this.isLight = isLight;
            this.backdropTop = backdropTop;
            this.backdropBottom = backdropBottom;
            this.glowColor = glowColor;
            this.petalColor = petalColor;
            this.panelTop = panelTop;
            this.panelBottom = panelBottom;
            this.panelBorder = panelBorder;
            this.panelHighlight = panelHighlight;
            this.cardTop = cardTop;
            this.cardBottom = cardBottom;
            this.cardBorder = cardBorder;
            this.cardHighlight = cardHighlight;
            this.cardActiveTop = cardActiveTop;
            this.cardActiveBottom = cardActiveBottom;
            this.cardActiveBorder = cardActiveBorder;
            this.cardActiveHighlight = cardActiveHighlight;
            this.cardHoverTop = cardHoverTop;
            this.cardHoverBottom = cardHoverBottom;
            this.cardHoverBorder = cardHoverBorder;
            this.cardHoverHighlight = cardHoverHighlight;
            this.pillBarFill = pillBarFill;
            this.pillBarBorder = pillBarBorder;
            this.pillBarHighlight = pillBarHighlight;
            this.pillDarkFill = pillDarkFill;
            this.pillDarkBorder = pillDarkBorder;
            this.pillDarkHighlight = pillDarkHighlight;
            this.pillHoverFill = pillHoverFill;
            this.pillHoverBorder = pillHoverBorder;
            this.pillHoverHighlight = pillHoverHighlight;
            this.pillActiveFill = pillActiveFill;
            this.pillActiveBorder = pillActiveBorder;
            this.pillActiveHighlight = pillActiveHighlight;
            this.circleFill = circleFill;
            this.circleBorder = circleBorder;
            this.circleActiveFill = circleActiveFill;
            this.circleActiveBorder = circleActiveBorder;
            this.textPrimary = textPrimary;
            this.textMuted = textMuted;
            this.textDim = textDim;
            this.textAccent = textAccent;
            this.textOnActivePill = textOnActivePill;
            this.textPositive = textPositive;
        }

        public static Theme fromId(String id) {
            for (Theme t : values()) {
                if (t.id.equalsIgnoreCase(id)) return t;
            }
            return PASTEL_CREAM;
        }
    }

    public enum FontChoice {
        POPPINS("poppins", "Poppins (Boutique)", Identifier.fromNamespaceAndPath("modid", "poppins")),
        INTER("inter", "Inter (Clean UI)", Identifier.fromNamespaceAndPath("modid", "inter")),
        JAKARTA("jakarta", "Plus Jakarta Sans", Identifier.fromNamespaceAndPath("modid", "jakarta")),
        CLASSIC("classic", "Minecraft Classic", null);

        public final String id;
        public final String displayName;
        public final Identifier fontId;

        FontChoice(String id, String displayName, Identifier fontId) {
            this.id = id;
            this.displayName = displayName;
            this.fontId = fontId;
        }

        public static FontChoice fromId(String id) {
            for (FontChoice f : values()) {
                if (f.id.equalsIgnoreCase(id)) return f;
            }
            return CLASSIC;
        }
    }

    private static final class ThemeTextures {
        Identifier panelGlass;
        Identifier cardGlass;
        Identifier cardHover;
        Identifier cardActive;
        Identifier pillDark;
        Identifier pillHover;
        Identifier pillActive;
        Identifier pillBar;
        Identifier circleSlot;
        Identifier circleActive;
        Identifier radialGlow;
        Identifier petal;
    }

    private static final Map<Theme, ThemeTextures> TEXTURE_CACHE = new HashMap<>();

    private static final int PANEL_TEX_SIZE = 64;
    private static final int PANEL_CORNER_PX = 20;
    private static final int PILL_TEX_W = 64;
    private static final int PILL_TEX_H = 32;

    private static final float[] PETAL_X = new float[18];
    private static final float[] PETAL_Y = new float[18];
    private static final float[] PETAL_SPEED = new float[18];
    private static final float[] PETAL_SIZE = new float[18];
    private static boolean petalsSeeded = false;

    private BoutiqueDraw() {}

    public static Theme theme() {
        return Theme.fromId(KiemConfig.uiTheme);
    }

    public static FontChoice fontChoice() {
        return FontChoice.fromId(KiemConfig.uiFont);
    }

    public static Component styled(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }
        FontChoice fc = fontChoice();
        if (fc.fontId == null) {
            return Component.literal(text);
        }
        Style customStyle = Style.EMPTY.withFont(new FontDescription.Resource(fc.fontId));
        boolean hasSymbol = false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 0x024F) {
                hasSymbol = true;
                break;
            }
        }
        if (!hasSymbol) {
            return Component.literal(text).setStyle(customStyle);
        }
        // Fallback Unicode icons (⚙, ✕, ✦, →, etc.) to Minecraft's default font so custom TTFs never show missing-glyph boxes.
        net.minecraft.network.chat.MutableComponent root = Component.empty();
        int start = 0;
        boolean runIsSymbol = text.charAt(0) > 0x024F;
        for (int i = 1; i <= text.length(); i++) {
            boolean curIsSymbol = (i < text.length()) ? (text.charAt(i) > 0x024F) : !runIsSymbol;
            if (i == text.length() || curIsSymbol != runIsSymbol) {
                String seg = text.substring(start, i);
                root.append(Component.literal(seg).setStyle(runIsSymbol ? Style.EMPTY : customStyle));
                start = i;
                runIsSymbol = curIsSymbol;
            }
        }
        return root;
    }

    private static boolean shouldDrawShadow(int color) {
        if (theme().isLight) return false;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return (r * 299 + g * 587 + b * 114) / 1000 >= 128;
    }

    public static void drawText(GuiGraphicsExtractor g, Font font, String text, int x, int y, int color) {
        g.text(font, styled(text), x, y, color, shouldDrawShadow(color));
    }

    public static void drawCenteredText(GuiGraphicsExtractor g, Font font, String text, int x, int y, int color) {
        int w = textWidth(font, text);
        g.text(font, styled(text), x - w / 2, y, color, shouldDrawShadow(color));
    }

    public static int textWidth(Font font, String text) {
        return font.width(styled(text));
    }

    public static String fitText(Font font, String text, int maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 4) return "";
        if (textWidth(font, text) <= maxWidth) return text;
        int ellW = textWidth(font, "..");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = sb.toString() + text.charAt(i);
            if (textWidth(font, next) + ellW > maxWidth) {
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString() + "..";
    }

    private static ThemeTextures getTextures() {
        Theme t = theme();
        return TEXTURE_CACHE.computeIfAbsent(t, BoutiqueDraw::buildThemeTextures);
    }

    private static ThemeTextures buildThemeTextures(Theme t) {
        ThemeTextures tt = new ThemeTextures();
        String p = t.id + "/";
        tt.panelGlass = createRoundedPanelTex(p + "panel_glass",
                t.panelTop, t.panelBottom, t.panelBorder, t.panelHighlight, 14.0f);
        tt.cardGlass = createRoundedPanelTex(p + "card_glass",
                t.cardTop, t.cardBottom, t.cardBorder, t.cardHighlight, 12.0f);
        tt.cardHover = createRoundedPanelTex(p + "card_hover",
                t.cardHoverTop, t.cardHoverBottom, t.cardHoverBorder, t.cardHoverHighlight, 12.0f);
        tt.cardActive = createRoundedPanelTex(p + "card_active",
                t.cardActiveTop, t.cardActiveBottom, t.cardActiveBorder, t.cardActiveHighlight, 12.0f);

        tt.pillBar = createPillTex(p + "pill_bar",
                t.pillBarFill, t.pillBarBorder, t.pillBarHighlight);
        tt.pillDark = createPillTex(p + "pill_dark",
                t.pillDarkFill, t.pillDarkBorder, t.pillDarkHighlight);
        tt.pillHover = createPillTex(p + "pill_hover",
                t.pillHoverFill, t.pillHoverBorder, t.pillHoverHighlight);
        tt.pillActive = createPillTex(p + "pill_active",
                t.pillActiveFill, t.pillActiveBorder, t.pillActiveHighlight);

        tt.circleSlot = createCircleTex(p + "circle_slot",
                t.circleFill, t.circleBorder, 0x18FFFFFF);
        tt.circleActive = createCircleTex(p + "circle_active",
                t.circleActiveFill, t.circleActiveBorder, 0x38FFFFFF);

        tt.radialGlow = createRadialGlowTex(p + "radial_glow", t.glowColor);
        tt.petal = createPetalTex(p + "petal", t.petalColor);
        return tt;
    }

    public static void drawBackdrop(GuiGraphicsExtractor g, int width, int height, long timeMs) {
        if (KiemConfig.uiBackdropTint) {
            Theme t = theme();
            ThemeTextures tt = getTextures();
            g.fillGradient(0, 0, width, height, t.backdropTop, t.backdropBottom);

            int glowSize = Math.min(width, height) * 3 / 4;
            int gx = (int) (width * 0.72f) - glowSize / 2;
            int gy = height / 2 - glowSize / 2;
            g.blit(tt.radialGlow, gx, gy, gx + glowSize, gy + glowSize, 0.0f, 1.0f, 0.0f, 1.0f);

            drawDriftingPetals(g, tt.petal, width, height, timeMs);
        } else {
            g.fill(0, 0, width, height, 0x38000000);
        }
    }

    public static void drawRadialGlow(GuiGraphicsExtractor g, int cx, int cy, int radius) {
        ThemeTextures tt = getTextures();
        g.blit(tt.radialGlow, cx - radius, cy - radius, cx + radius, cy + radius, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    public static void drawGlassPanel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        drawNineSlice(g, getTextures().panelGlass, x, y, w, h, 10);
    }

    public static void drawGlassCard(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean active, boolean hover) {
        ThemeTextures tt = getTextures();
        Identifier tex = active ? tt.cardActive : (hover ? tt.cardHover : tt.cardGlass);
        drawNineSlice(g, tex, x, y, w, h, 9);
    }

    public static void drawPillBar(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        drawPillNineSlice(g, getTextures().pillBar, x, y, w, h);
    }

    public static void drawPill(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean active, boolean hover) {
        ThemeTextures tt = getTextures();
        Identifier tex = active ? tt.pillActive : (hover ? tt.pillHover : tt.pillDark);
        drawPillNineSlice(g, tex, x, y, w, h);
    }

    public static void drawCircleSlot(GuiGraphicsExtractor g, int x, int y, int size, boolean active, boolean hover) {
        ThemeTextures tt = getTextures();
        Identifier tex = (active || hover) ? tt.circleActive : tt.circleSlot;
        g.blit(tex, x, y, x + size, y + size, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    private static void drawDriftingPetals(GuiGraphicsExtractor g, Identifier petalTex, int width, int height, long timeMs) {
        if (!petalsSeeded) {
            petalsSeeded = true;
            for (int i = 0; i < PETAL_X.length; i++) {
                PETAL_X[i] = ((i * 73 + 19) % 100) / 100.0f;
                PETAL_Y[i] = ((i * 41 + 7) % 100) / 100.0f;
                PETAL_SPEED[i] = 0.012f + (i % 5) * 0.004f;
                PETAL_SIZE[i] = 5.0f + (i % 4) * 2.0f;
            }
        }
        float t = (timeMs % 60000L) / 1000.0f;
        for (int i = 0; i < PETAL_X.length; i++) {
            float fy = (PETAL_Y[i] + t * PETAL_SPEED[i]) % 1.0f;
            float fx = (PETAL_X[i] + (float) Math.sin(t * 0.8f + i) * 0.03f + t * 0.006f) % 1.0f;
            int px = (int) (fx * width);
            int py = (int) (fy * height);
            int sz = (int) PETAL_SIZE[i];
            g.blit(petalTex, px, py, px + sz, py + sz, 0.0f, 1.0f, 0.0f, 1.0f);
        }
    }

    private static void drawNineSlice(GuiGraphicsExtractor g, Identifier tex, int x, int y, int w, int h, int screenCorner) {
        if (w <= 0 || h <= 0) return;
        int c = Math.min(screenCorner, Math.min(w / 2, h / 2));
        float uvC = (float) PANEL_CORNER_PX / (float) PANEL_TEX_SIZE;
        float uvMid = 1.0f - uvC;

        int x1 = x + c;
        int x2 = x + w - c;
        int y1 = y + c;
        int y2 = y + h - c;

        g.blit(tex, x, y, x1, y1, 0.0f, uvC, 0.0f, uvC);
        g.blit(tex, x2, y, x + w, y1, uvMid, 1.0f, 0.0f, uvC);
        g.blit(tex, x, y2, x1, y + h, 0.0f, uvC, uvMid, 1.0f);
        g.blit(tex, x2, y2, x + w, y + h, uvMid, 1.0f, uvMid, 1.0f);

        if (x2 > x1) {
            g.blit(tex, x1, y, x2, y1, uvC, uvMid, 0.0f, uvC);
            g.blit(tex, x1, y2, x2, y + h, uvC, uvMid, uvMid, 1.0f);
        }
        if (y2 > y1) {
            g.blit(tex, x, y1, x1, y2, 0.0f, uvC, uvC, uvMid);
            g.blit(tex, x2, y1, x + w, y2, uvMid, 1.0f, uvC, uvMid);
        }
        if (x2 > x1 && y2 > y1) {
            g.blit(tex, x1, y1, x2, y2, uvC, uvMid, uvC, uvMid);
        }
    }

    private static void drawPillNineSlice(GuiGraphicsExtractor g, Identifier tex, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        int cap = Math.min(h / 2, w / 2);
        float uCap = 0.25f;
        float uEnd = 1.0f - uCap;

        int x1 = x + cap;
        int x2 = x + w - cap;

        g.blit(tex, x, y, x1, y + h, 0.0f, uCap, 0.0f, 1.0f);
        if (x2 > x1) {
            g.blit(tex, x1, y, x2, y + h, uCap, uEnd, 0.0f, 1.0f);
        }
        g.blit(tex, x2, y, x + w, y + h, uEnd, 1.0f, 0.0f, 1.0f);
    }

    private static Identifier createRoundedPanelTex(String key, int topColor, int bottomColor,
                                                    int borderColor, int highlightColor, float radius) {
        int size = PANEL_TEX_SIZE;
        NativeImage img = new NativeImage(size, size, false);
        float half = (size - 2.0f) * 0.5f;
        float cx = (size - 1.0f) * 0.5f;
        float cy = (size - 1.0f) * 0.5f;

        for (int y = 0; y < size; y++) {
            float t = (float) y / (size - 1);
            int fillCol = lerpArgb(topColor, bottomColor, t);
            for (int x = 0; x < size; x++) {
                float d = sdRoundedBox(x - cx, y - cy, half, half, radius);
                float bodyAlpha = clamp01(0.5f - d);
                if (bodyAlpha <= 0.0f) {
                    img.setPixel(x, y, 0);
                    continue;
                }
                int pixel = multiplyAlpha(fillCol, bodyAlpha);
                float borderBand = clamp01(1.2f - Math.abs(d + 0.5f));
                if (borderBand > 0.0f) {
                    pixel = alphaOver(pixel, multiplyAlpha(borderColor, borderBand * bodyAlpha));
                }
                if (y <= 3 && d < -1.0f) {
                    float hl = (1.0f - y / 3.5f) * bodyAlpha;
                    pixel = alphaOver(pixel, multiplyAlpha(highlightColor, hl));
                }
                img.setPixel(x, y, pixel);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier createPillTex(String key, int fillColor, int borderColor, int highlightColor) {
        int w = PILL_TEX_W;
        int h = PILL_TEX_H;
        NativeImage img = new NativeImage(w, h, false);
        float halfW = (w - 2.0f) * 0.5f;
        float halfH = (h - 2.0f) * 0.5f;
        float radius = halfH;
        float cx = (w - 1.0f) * 0.5f;
        float cy = (h - 1.0f) * 0.5f;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float d = sdRoundedBox(x - cx, y - cy, halfW, halfH, radius);
                float bodyAlpha = clamp01(0.5f - d);
                if (bodyAlpha <= 0.0f) {
                    img.setPixel(x, y, 0);
                    continue;
                }
                int pixel = multiplyAlpha(fillColor, bodyAlpha);
                float borderBand = clamp01(1.2f - Math.abs(d + 0.5f));
                if (borderBand > 0.0f) {
                    pixel = alphaOver(pixel, multiplyAlpha(borderColor, borderBand * bodyAlpha));
                }
                if (y <= 3 && d < -1.0f) {
                    float hl = (1.0f - y / 3.5f) * bodyAlpha;
                    pixel = alphaOver(pixel, multiplyAlpha(highlightColor, hl));
                }
                img.setPixel(x, y, pixel);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier createCircleTex(String key, int fillColor, int borderColor, int highlightColor) {
        int size = 64;
        NativeImage img = new NativeImage(size, size, false);
        float cx = (size - 1.0f) * 0.5f;
        float cy = (size - 1.0f) * 0.5f;
        float r = (size - 3.0f) * 0.5f;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - cx;
                float dy = y - cy;
                float d = (float) Math.sqrt(dx * dx + dy * dy) - r;
                float bodyAlpha = clamp01(0.5f - d);
                if (bodyAlpha <= 0.0f) {
                    img.setPixel(x, y, 0);
                    continue;
                }
                int pixel = multiplyAlpha(fillColor, bodyAlpha);
                float borderBand = clamp01(1.5f - Math.abs(d + 0.75f));
                if (borderBand > 0.0f) {
                    pixel = alphaOver(pixel, multiplyAlpha(borderColor, borderBand * bodyAlpha));
                }
                if (dy < -r * 0.45f && d < -1.5f) {
                    pixel = alphaOver(pixel, multiplyAlpha(highlightColor, 0.5f * bodyAlpha));
                }
                img.setPixel(x, y, pixel);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier createRadialGlowTex(String key, int glowArgb) {
        int size = 128;
        NativeImage img = new NativeImage(size, size, false);
        float half = (size - 1.0f) * 0.5f;
        int maxA = (glowArgb >>> 24) & 0xFF;
        int rgb = glowArgb & 0x00FFFFFF;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float nx = (x - half) / half;
                float ny = (y - half) / half;
                float distSq = nx * nx + ny * ny;
                float falloff = (float) Math.exp(-distSq * 3.6f);
                int alpha = Math.max(0, Math.min(255, (int) (falloff * maxA)));
                img.setPixel(x, y, (alpha << 24) | rgb);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier createPetalTex(String key, int petalArgb) {
        int size = 16;
        NativeImage img = new NativeImage(size, size, false);
        float c = (size - 1.0f) * 0.5f;
        int rgb = petalArgb & 0x00FFFFFF;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = (x - c) / 5.5f;
                float dy = (y - c) / 3.8f;
                float d = (float) Math.sqrt(dx * dx + dy * dy) - 1.0f;
                float a = clamp01(0.5f - d * 2.5f);
                int alpha = (int) (a * 145.0f);
                img.setPixel(x, y, (alpha << 24) | rgb);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier registerTex(String key, NativeImage img) {
        DynamicTexture dyn = new DynamicTexture(() -> "kiem_boutique_" + key.replace('/', '_'), img);
        dyn.upload();
        Identifier id = Identifier.fromNamespaceAndPath("modid", "boutique_ui/" + key);
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        tm.register(id, dyn);
        return id;
    }

    private static float sdRoundedBox(float px, float py, float halfW, float halfH, float r) {
        float qx = Math.abs(px) - (halfW - r);
        float qy = Math.abs(py) - (halfH - r);
        float outX = Math.max(qx, 0.0f);
        float outY = Math.max(qy, 0.0f);
        float outside = (float) Math.sqrt(outX * outX + outY * outY);
        float inside = Math.min(Math.max(qx, qy), 0.0f);
        return outside + inside - r;
    }

    private static float clamp01(float v) {
        return v < 0.0f ? 0.0f : (v > 1.0f ? 1.0f : v);
    }

    private static int multiplyAlpha(int argb, float factor) {
        int a = (int) (((argb >>> 24) & 0xFF) * clamp01(factor));
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    private static int lerpArgb(int c1, int c2, float t) {
        int a = (int) (((c1 >>> 24) & 0xFF) + (((c2 >>> 24) & 0xFF) - ((c1 >>> 24) & 0xFF)) * t);
        int r = (int) (((c1 >>> 16) & 0xFF) + (((c2 >>> 16) & 0xFF) - ((c1 >>> 16) & 0xFF)) * t);
        int g = (int) (((c1 >>> 8) & 0xFF) + (((c2 >>> 8) & 0xFF) - ((c1 >>> 8) & 0xFF)) * t);
        int b = (int) ((c1 & 0xFF) + ((c2 & 0xFF) - (c1 & 0xFF)) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int alphaOver(int dst, int src) {
        float sa = ((src >>> 24) & 0xFF) / 255.0f;
        float da = ((dst >>> 24) & 0xFF) / 255.0f;
        float outA = sa + da * (1.0f - sa);
        if (outA <= 0.001f) return 0;

        int sr = (src >>> 16) & 0xFF, sg = (src >>> 8) & 0xFF, sb = src & 0xFF;
        int dr = (dst >>> 16) & 0xFF, dg = (dst >>> 8) & 0xFF, db = dst & 0xFF;

        int r = (int) ((sr * sa + dr * da * (1.0f - sa)) / outA);
        int g = (int) ((sg * sa + dg * da * (1.0f - sa)) / outA);
        int b = (int) ((sb * sa + db * da * (1.0f - sa)) / outA);
        int a = (int) (outA * 255.0f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
