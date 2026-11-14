package com.example.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

/**
 * Anti-aliased SDF-backed 9-slice glass panels, capsule pills, circular slots,
 * radial glows, and drifting sakura petals for the Sakura & Ink Boutique UI.
 */
public final class BoutiqueDraw {

    private static boolean initialized = false;

    private static Identifier texPanelGlass;
    private static Identifier texCardGlass;
    private static Identifier texCardHover;
    private static Identifier texCardActive;
    private static Identifier texPillDark;
    private static Identifier texPillHover;
    private static Identifier texPillSakura;
    private static Identifier texPillBar;
    private static Identifier texCircleSlot;
    private static Identifier texCircleActive;
    private static Identifier texRadialGlow;
    private static Identifier texPetal;

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

    public static void ensureInitialized() {
        if (initialized) return;
        initialized = true;

        texPanelGlass = createRoundedPanelTex("panel_glass",
                0xD0241A2D, 0xDC191121, 0x6EE6B0A4, 0x24FFFFFF, 14.0f);
        texCardGlass = createRoundedPanelTex("card_glass",
                0xB82D2138, 0xC8201629, 0x55DFA89E, 0x1AFFFFFF, 12.0f);
        texCardHover = createRoundedPanelTex("card_hover",
                0xCC392947, 0xD8291D34, 0xAAF0BFC7, 0x2EFFFFFF, 12.0f);
        texCardActive = createRoundedPanelTex("card_active",
                0xD642273D, 0xE22D1A2C, 0xFFF2B8C6, 0x38FFD6E2, 12.0f);

        texPillBar = createPillTex("pill_bar",
                0xC81D1526, 0x5CE0A89D, 0x16FFFFFF);
        texPillDark = createPillTex("pill_dark",
                0xAA32253E, 0x50D9A39A, 0x18FFFFFF);
        texPillHover = createPillTex("pill_hover",
                0xCC443254, 0x99F0BEC8, 0x26FFFFFF);
        texPillSakura = createPillTex("pill_sakura",
                0xFFF2C2CE, 0xFFFFDCE5, 0x44FFFFFF);

        texCircleSlot = createCircleTex("circle_slot",
                0xB0291E33, 0x6AE0A89E, 0x18FFFFFF);
        texCircleActive = createCircleTex("circle_active",
                0xD0462840, 0xFFF2B8C6, 0x38FFE0EA);

        texRadialGlow = createRadialGlowTex("radial_glow");
        texPetal = createPetalTex("petal");
    }

    public static void drawBackdrop(GuiGraphicsExtractor g, int width, int height, long timeMs) {
        ensureInitialized();
        // Rich plum-ink translucent backdrop gradient
        g.fillGradient(0, 0, width, height, 0xD618101F, 0xE622162A);

        // Soft warm sakura bloom on the right stage side
        int glowSize = Math.min(width, height) * 3 / 4;
        int gx = (int) (width * 0.72f) - glowSize / 2;
        int gy = height / 2 - glowSize / 2;
        g.blit(texRadialGlow, gx, gy, gx + glowSize, gy + glowSize, 0.0f, 1.0f, 0.0f, 1.0f);

        // Subtle animated drifting sakura petals
        drawDriftingPetals(g, width, height, timeMs);
    }

    public static void drawRadialGlow(GuiGraphicsExtractor g, int cx, int cy, int radius) {
        ensureInitialized();
        g.blit(texRadialGlow, cx - radius, cy - radius, cx + radius, cy + radius, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    public static void drawGlassPanel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        ensureInitialized();
        drawNineSlice(g, texPanelGlass, x, y, w, h, 10);
    }

    public static void drawGlassCard(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean active, boolean hover) {
        ensureInitialized();
        Identifier tex = active ? texCardActive : (hover ? texCardHover : texCardGlass);
        drawNineSlice(g, tex, x, y, w, h, 9);
    }

    public static void drawPillBar(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        ensureInitialized();
        drawPillNineSlice(g, texPillBar, x, y, w, h);
    }

    public static void drawPill(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean active, boolean hover) {
        ensureInitialized();
        Identifier tex = active ? texPillSakura : (hover ? texPillHover : texPillDark);
        drawPillNineSlice(g, tex, x, y, w, h);
    }

    public static void drawCircleSlot(GuiGraphicsExtractor g, int x, int y, int size, boolean active, boolean hover) {
        ensureInitialized();
        Identifier tex = (active || hover) ? texCircleActive : texCircleSlot;
        g.blit(tex, x, y, x + size, y + size, 0.0f, 1.0f, 0.0f, 1.0f);
    }

    private static void drawDriftingPetals(GuiGraphicsExtractor g, int width, int height, long timeMs) {
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
            g.blit(texPetal, px, py, px + sz, py + sz, 0.0f, 1.0f, 0.0f, 1.0f);
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

        // Corners
        g.blit(tex, x, y, x1, y1, 0.0f, uvC, 0.0f, uvC);
        g.blit(tex, x2, y, x + w, y1, uvMid, 1.0f, 0.0f, uvC);
        g.blit(tex, x, y2, x1, y + h, 0.0f, uvC, uvMid, 1.0f);
        g.blit(tex, x2, y2, x + w, y + h, uvMid, 1.0f, uvMid, 1.0f);

        // Edges
        if (x2 > x1) {
            g.blit(tex, x1, y, x2, y1, uvC, uvMid, 0.0f, uvC);
            g.blit(tex, x1, y2, x2, y + h, uvC, uvMid, uvMid, 1.0f);
        }
        if (y2 > y1) {
            g.blit(tex, x, y1, x1, y2, 0.0f, uvC, uvC, uvMid);
            g.blit(tex, x2, y1, x + w, y2, uvMid, 1.0f, uvC, uvMid);
        }
        // Center
        if (x2 > x1 && y2 > y1) {
            g.blit(tex, x1, y1, x2, y2, uvC, uvMid, uvC, uvMid);
        }
    }

    private static void drawPillNineSlice(GuiGraphicsExtractor g, Identifier tex, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        int cap = Math.min(h / 2, w / 2);
        float uCap = 0.25f; // 16 / 64
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

                // Crisp 1.2px rose-gold border along the edge
                float borderBand = clamp01(1.2f - Math.abs(d + 0.5f));
                if (borderBand > 0.0f) {
                    pixel = alphaOver(pixel, multiplyAlpha(borderColor, borderBand * bodyAlpha));
                }
                // Subtle top-edge inner glass highlight
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

    private static Identifier createRadialGlowTex(String key) {
        int size = 128;
        NativeImage img = new NativeImage(size, size, false);
        float half = (size - 1.0f) * 0.5f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float nx = (x - half) / half;
                float ny = (y - half) / half;
                float distSq = nx * nx + ny * ny;
                float falloff = (float) Math.exp(-distSq * 3.6f);
                int alpha = Math.max(0, Math.min(255, (int) (falloff * 78.0f)));
                int pixel = (alpha << 24) | 0x00F4B2C4;
                img.setPixel(x, y, pixel);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier createPetalTex(String key) {
        int size = 16;
        NativeImage img = new NativeImage(size, size, false);
        float c = (size - 1.0f) * 0.5f;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = (x - c) / 5.5f;
                float dy = (y - c) / 3.8f;
                float d = (float) Math.sqrt(dx * dx + dy * dy) - 1.0f;
                float a = clamp01(0.5f - d * 2.5f);
                int alpha = (int) (a * 145.0f);
                img.setPixel(x, y, (alpha << 24) | 0x00F7C6D5);
            }
        }
        return registerTex(key, img);
    }

    private static Identifier registerTex(String key, NativeImage img) {
        DynamicTexture dyn = new DynamicTexture(() -> "kiem_boutique_" + key, img);
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
