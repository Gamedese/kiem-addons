package com.example.client;

import com.example.client.aura.SakuraAuraManager;
import com.example.client.aura.SakuraAuraPreview;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** Live cosmetic editor. Retains the original menu instance and selected tab. */
public final class SakuraAuraScreen extends Screen {
    private static final int PINK = 0xFFE77BA7, TEXT = 0xFFF4EEF5, MUTED = 0xFF9296AC, BORDER = 0xFF343547;
    private static final float[] DEFAULT_HEIGHTS = {0.63f, 0.954f, 1.278f};
    private static final float[] DEFAULT_RADII = {0.74f, 0.84f, 0.79f};
    private final Screen parent;
    private final SakuraAuraPreview preview = new SakuraAuraPreview();
    private int panelX, panelY, panelW, panelH, leftX, leftW, bodyY, bodyH, viewX, viewW;
    private int viewportX0, viewportY0, viewportX1, viewportY1;
    private boolean wide, draggingPreview;
    private int page;
    private float previewYaw = 25f, previewPitch = -8f;

    public SakuraAuraScreen(Screen parent) {
        super(Component.literal(KiemLang.get("auras.custom.title")));
        this.parent = parent;
    }

    @Override protected void init() {
        draggingPreview = false;
        panelW = Math.min(900, width - 24);
        panelH = Math.min(440, height - 16);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        wide = panelW >= 760 && panelH >= 416;
        int margin = wide ? 18 : 12;
        int innerW = panelW - margin * 2;
        leftX = panelX + margin;
        leftW = (int)((innerW - (wide ? 16 : 10)) * (wide ? 0.61f : 0.64f));
        viewX = leftX + leftW + (wide ? 16 : 10);
        viewW = panelX + panelW - margin - viewX;
        bodyY = panelY + (wide ? 64 : 60);
        bodyH = panelH - (wide ? 112 : 100);
        if (wide) {
            addGeneralControls(leftX + 12, bodyY + 23, leftW - 24, true);
            for (int i = 0; i < 3; i++) addBunnyControls(i, leftX + 12, bodyY + 144 + i * 56, leftW - 24, true);
        } else {
            int gap = 4, tabW = (innerW - gap * 3) / 4;
            for (int i = 0; i < 4; i++) {
                final int selected = i;
                String label = i == 0 ? KiemLang.get("auras.custom.general") : KiemLang.get("auras.custom.bunny") + " " + i;
                PinkButton tab = new PinkButton(leftX + i * (tabW + gap), panelY + 34, tabW, 20, label, () -> {
                    page = selected;
                    rebuildWidgets();
                });
                tab.selected = page == i;
                addRenderableWidget(tab);
            }
            if (page == 0) addGeneralControls(leftX + 8, bodyY + 12, leftW - 16, false);
            else addBunnyControls(page - 1, leftX + 8, bodyY + 30, leftW - 16, false);
        }
        int footerY = panelY + panelH - 34;
        int footerW = Math.min(164, (innerW - 10) / 2);
        addRenderableWidget(new PinkButton(leftX, footerY, footerW, 24, KiemLang.get("ui.back"), this::onClose));
        addRenderableWidget(new PinkButton(panelX + panelW - margin - footerW, footerY, footerW, 24,
                KiemLang.get("auras.custom.defaults"), () -> {
            SakuraAuraManager.setSpeed(1f);
            KiemConfig.resetSakuraAuraSettings();
            KiemConfig.save();
            rebuildWidgets();
        }));
        viewportX0 = viewX + 4;
        viewportY0 = bodyY + (viewW >= 140 ? 29 : 24);
        viewportX1 = viewX + viewW - 4;
        viewportY1 = bodyY + bodyH - (wide ? 54 : 30);
        PinkButton resetView = new PinkButton(viewX + 8, bodyY + bodyH - 26, viewW - 16, 20,
                viewW >= 130 ? KiemLang.get("auras.custom.reset_view") : "↺", () -> {
            previewYaw = 25f;
            previewPitch = -8f;
        });
        resetView.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.reset_view"))));
        addRenderableWidget(resetView);
    }

    private void addGeneralControls(int x, int y, int w, boolean columns) {
        if (columns) {
            int cellW = (w - 12) / 2;
            int rightX = x + cellW + 12;
            int stride = 29;
            int widgetH = 26;

            // Row 0: Scale (Left) | Speed (Right)
            setting(x, y, cellW, widgetH, "auras.custom.scale", "x", .3, 1.4,
                    () -> KiemConfig.sakuraAuraScale, v -> KiemConfig.sakuraAuraScale = (float)v, .8);
            setting(rightX, y, cellW, widgetH, "auras.custom.speed", "x", .25, 2,
                    () -> KiemConfig.sakuraAuraSpeed, v -> SakuraAuraManager.setSpeed((float)v), 1);

            // Row 1: Bobbing (Left) | Floating Petals (Right)
            setting(x, y + stride, cellW, widgetH, "auras.custom.bob", "x", 0, 3,
                    () -> KiemConfig.sakuraAuraBob, v -> KiemConfig.sakuraAuraBob = (float)v, 1);
            int petalY = y + stride;
            PinkButton petals = new PinkButton(rightX, petalY, cellW - 25, widgetH, densityLabel(), () -> {
                KiemConfig.sakuraAuraPetals = (KiemConfig.sakuraAuraPetals + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            petals.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.petals_hint"))));
            addRenderableWidget(petals);
            resetButton(rightX + cellW - 21, petalY, widgetH, () -> {
                KiemConfig.sakuraAuraPetals = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            // Row 2: 🌸 Flower Carpet (Full Width Hero Button)
            int bloomY = y + stride * 2;
            PinkButton bloom = new PinkButton(x, bloomY, w - 25, widgetH, bloomLabel(), () -> {
                KiemConfig.sakuraAuraGroundBloom = (KiemConfig.sakuraAuraGroundBloom + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            bloom.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.bloom_hint"))));
            addRenderableWidget(bloom);
            resetButton(x + w - 21, bloomY, widgetH, () -> {
                KiemConfig.sakuraAuraGroundBloom = 3;
                KiemConfig.save();
                rebuildWidgets();
            });
        } else {
            // Narrow single-column mode: 5 stacked controls
            int stride = Math.min(34, (bodyH - 16) / 5);
            int widgetH = Math.min(26, stride - 4);

            setting(x, y, w, widgetH, "auras.custom.scale", "x", .3, 1.4,
                    () -> KiemConfig.sakuraAuraScale, v -> KiemConfig.sakuraAuraScale = (float)v, .8);
            setting(x, y + stride, w, widgetH, "auras.custom.speed", "x", .25, 2,
                    () -> KiemConfig.sakuraAuraSpeed, v -> SakuraAuraManager.setSpeed((float)v), 1);
            setting(x, y + stride * 2, w, widgetH, "auras.custom.bob", "x", 0, 3,
                    () -> KiemConfig.sakuraAuraBob, v -> KiemConfig.sakuraAuraBob = (float)v, 1);

            int petalY = y + stride * 3;
            PinkButton petals = new PinkButton(x, petalY, w - 25, widgetH, densityLabel(), () -> {
                KiemConfig.sakuraAuraPetals = (KiemConfig.sakuraAuraPetals + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            petals.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.petals_hint"))));
            addRenderableWidget(petals);
            resetButton(x + w - 21, petalY, widgetH, () -> {
                KiemConfig.sakuraAuraPetals = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            int bloomY = y + stride * 4;
            PinkButton bloom = new PinkButton(x, bloomY, w - 25, widgetH, bloomLabel(), () -> {
                KiemConfig.sakuraAuraGroundBloom = (KiemConfig.sakuraAuraGroundBloom + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            bloom.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.bloom_hint"))));
            addRenderableWidget(bloom);
            resetButton(x + w - 21, bloomY, widgetH, () -> {
                KiemConfig.sakuraAuraGroundBloom = 3;
                KiemConfig.save();
                rebuildWidgets();
            });
        }
    }

    private void addBunnyControls(int index, int x, int y, int w, boolean columns) {
        int cellW = columns ? (w - 12) / 2 : w;
        int secondX = columns ? x + cellW + 12 : x;
        int secondY = columns ? y : y + Math.min(54, (bodyH - 35) / 2);
        int widgetH = columns ? 26 : Math.min(36, (bodyH - 40) / 2);
        setting(x, y, cellW, widgetH, "auras.custom.height", "blocks", .15, 2.2,
                () -> KiemConfig.sakuraAuraHeights[index], v -> KiemConfig.sakuraAuraHeights[index] = (float)v, DEFAULT_HEIGHTS[index]);
        setting(secondX, secondY, cellW, widgetH, "auras.custom.radius", "blocks", .4, 1.5,
                () -> KiemConfig.sakuraAuraRadii[index], v -> KiemConfig.sakuraAuraRadii[index] = (float)v, DEFAULT_RADII[index]);
    }

    private void setting(int x, int y, int w, int h, String label, String unit, double min, double max,
                         DoubleSupplier getter, DoubleConsumer setter, double defaultValue) {
        SettingSlider slider = new SettingSlider(x, y, w - 25, h, label, unit, min, max, getter, setter);
        addRenderableWidget(slider);
        resetButton(x + w - 21, y, h, () -> {
            setter.accept(defaultValue);
            KiemConfig.save();
            slider.refresh();
        });
    }

    private void resetButton(int x, int y, int h, Runnable action) {
        PinkButton button = new PinkButton(x, y, 21, h, "↺", action);
        button.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.reset_setting"))));
        addRenderableWidget(button);
    }

    private String densityLabel() {
        return KiemLang.get("auras.custom.petals") + ": " + KiemLang.get("auras.custom.density." + KiemConfig.sakuraAuraPetals);
    }

    private String bloomLabel() {
        return KiemLang.get("auras.custom.ground_bloom") + ": " + KiemLang.get("auras.custom.bloom." + KiemConfig.sakuraAuraGroundBloom);
    }

    @Override public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xB80D0F17);
        card(graphics, panelX, panelY, panelW, panelH, 0xFB171923, 0xFF3A3043);
        graphics.fillGradient(panelX + 2, panelY + 2, panelX + panelW - 2, panelY + (wide ? 51 : 30), 0xFF292131, 0xFF191B26);
        graphics.fill(panelX + 18, panelY + 13, panelX + 21, panelY + (wide ? 41 : 23), PINK);
        graphics.text(font, font.plainSubstrByWidth(title.getString(), panelW - 56), panelX + 30, panelY + 13, TEXT, false);
        if (wide) graphics.text(font, KiemLang.get("auras.custom.hint"), panelX + 30, panelY + 30, MUTED, false);
        if (wide) {
            card(graphics, leftX, bodyY, leftW, 118, 0xFF1E212D, BORDER);
            section(graphics, KiemLang.get("auras.custom.general"), leftX + 12, bodyY + 8, leftW - 24);
            for (int i = 0; i < 3; i++) {
                int y = bodyY + 124 + i * 56;
                card(graphics, leftX, y, leftW, 52, 0xFF1E212D, BORDER);
                section(graphics, KiemLang.get("auras.custom.bunny") + " " + (i + 1), leftX + 12, y + 6, leftW - 24);
            }
        } else {
            card(graphics, leftX, bodyY, leftW, bodyH, 0xFF1E212D, BORDER);
            if (page > 0) section(graphics, KiemLang.get("auras.custom.bunny") + " " + page, leftX + 8, bodyY + 10, leftW - 16);
        }
        card(graphics, viewX, bodyY, viewW, bodyH, 0xFF131722, 0xFF423149);
        graphics.text(font, font.plainSubstrByWidth(KiemLang.get("auras.custom.preview"), viewW - 18), viewX + 9, bodyY + 10, TEXT, false);
        if (viewW > 170) {
            graphics.fill(viewX + viewW - 47, bodyY + 9, viewX + viewW - 42, bodyY + 14, PINK);
            graphics.text(font, KiemLang.get("auras.custom.live"), viewX + viewW - 37, bodyY + 8, PINK, false);
        }
        graphics.fillGradient(viewportX0, viewportY0, viewportX1, viewportY1, 0xFF171B29, 0xFF292033);
        preview.extract(graphics, viewportX0, viewportY0, viewportX1, viewportY1, previewYaw, previewPitch);
        if (wide) graphics.centeredText(font, KiemLang.get("auras.custom.drag_hint"), viewX + viewW / 2, bodyY + bodyH - 43, MUTED);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void section(GuiGraphicsExtractor graphics, String label, int x, int y, int w) {
        graphics.text(font, label, x, y, PINK, false);
        int start = x + font.width(label) + 10;
        if (start < x + w) graphics.fill(start, y + 4, x + w, y + 5, BORDER);
    }

    private void card(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int fill, int border) {
        graphics.fill(x + 2, y, x + w - 2, y + h, fill);
        graphics.fill(x, y + 2, x + w, y + h - 2, fill);
        graphics.fill(x + 2, y, x + w - 2, y + 1, border);
        graphics.fill(x + 2, y + h - 1, x + w - 2, y + h, border);
        graphics.fill(x, y + 2, x + 1, y + h - 2, border);
        graphics.fill(x + w - 1, y + 2, x + w, y + h - 2, border);
    }

    private boolean inViewport(double x, double y) {
        return x >= viewportX0 && x < viewportX1 && y >= viewportY0 && y < viewportY1;
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inViewport(event.x(), event.y())) {
            draggingPreview = true;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingPreview && event.button() == 0) {
            previewYaw = (previewYaw + (float)deltaX * .65f) % 360f;
            previewPitch = Math.clamp(previewPitch + (float)deltaY * .45f, -35f, 30f);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingPreview && event.button() == 0) {
            draggingPreview = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override public void onClose() {
        KiemConfig.save();
        KiemCompat.setScreen(minecraft, parent);
    }

    @Override public void removed() {
        draggingPreview = false;
        KiemConfig.save();
        super.removed();
    }

    @Override public boolean isPauseScreen() { return false; }

    private final class PinkButton extends Button {
        boolean selected;
        PinkButton(int x, int y, int w, int h, String label, Runnable action) {
            super(x, y, w, h, Component.literal(label), button -> action.run(), DEFAULT_NARRATION);
        }
        @Override protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            boolean highlight = selected || isHoveredOrFocused();
            card(graphics, getX(), getY(), getWidth(), getHeight(), highlight ? 0xFF3A283D : 0xFF292C3B, highlight ? PINK : BORDER);
            String label = font.plainSubstrByWidth(getMessage().getString(), getWidth() - 8);
            graphics.centeredText(font, label, getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, highlight ? 0xFFFFC0D8 : TEXT);
        }
    }

    private final class SettingSlider extends AbstractSliderButton {
        private final String label, unit;
        private final double min, max;
        private final DoubleSupplier getter;
        private final DoubleConsumer setter;
        SettingSlider(int x, int y, int w, int h, String label, String unit, double min, double max, DoubleSupplier getter, DoubleConsumer setter) {
            super(x, y, w, h, Component.empty(), Math.clamp((getter.getAsDouble() - min) / (max - min), 0, 1));
            this.label = label;
            this.unit = unit;
            this.min = min;
            this.max = max;
            this.getter = getter;
            this.setter = setter;
            updateMessage();
            if (label.equals("auras.custom.speed")) setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.speed_hint"))));
        }
        void refresh() {
            value = Math.clamp((getter.getAsDouble() - min) / (max - min), 0, 1);
            updateMessage();
        }
        private String valueLabel() {
            String suffix = unit.equals("blocks") ? " " + KiemLang.get("auras.custom.blocks") : unit;
            return String.format(Locale.ROOT, "%.2f", getter.getAsDouble()) + suffix;
        }
        @Override protected void updateMessage() { setMessage(Component.literal(KiemLang.get(label) + ": " + valueLabel())); }
        @Override protected void applyValue() {
            setter.accept(Math.round((min + value * (max - min)) * 100) / 100.0);
            KiemConfig.save();
        }
        @Override public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            boolean highlight = isHoveredOrFocused();
            if (highlight) card(graphics, x, y, w, h, 0xFF292A3A, 0xFF5C3C57);
            String current = valueLabel();
            int valueW = font.width(current);
            String name = font.plainSubstrByWidth(KiemLang.get(label), Math.max(12, w - valueW - 14));
            graphics.text(font, name, x + 5, y + 4, MUTED, false);
            graphics.text(font, current, x + w - 5 - valueW, y + 4, highlight ? 0xFFFFBCD6 : TEXT, false);
            int trackY = y + h - 8;
            int knobX = x + 4 + (int)((w - 8) * value);
            graphics.fill(x + 4, trackY, x + w - 4, trackY + 2, 0xFF3F4256);
            graphics.fill(x + 4, trackY, knobX, trackY + 2, PINK);
            graphics.fill(knobX - 2, trackY - 2, knobX + 3, trackY + 5, highlight ? 0xFFFFCEE0 : PINK);
        }
    }
}
