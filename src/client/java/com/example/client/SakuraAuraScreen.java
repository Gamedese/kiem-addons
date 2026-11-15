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
    private final Screen parent;
    private final SakuraAuraPreview preview = new SakuraAuraPreview();
    private int panelX, panelY, panelW, panelH, leftX, leftW, bodyY, bodyH, viewX, viewW;
    private int viewportX0, viewportY0, viewportX1, viewportY1;
    private boolean wide, draggingPreview;
    private float previewYaw = 25f, previewPitch = -8f;

    public SakuraAuraScreen(Screen parent) {
        super(Component.literal(KiemLang.get("trails.sakura.title") + " • " + KiemLang.get("pets.custom.btn")));
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
            addGeneralControls(leftX + 12, bodyY + 28, leftW - 24, true);
        } else {
            addGeneralControls(leftX + 8, bodyY + 26, leftW - 16, false);
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
        BoutiqueDraw.Theme t = BoutiqueDraw.theme();
        BoutiqueDraw.drawBackdrop(graphics, this.width, this.height, System.currentTimeMillis());
        BoutiqueDraw.drawGlassPanel(graphics, panelX, panelY, panelW, panelH);
        graphics.fill(panelX + 18, panelY + 13, panelX + 21, panelY + (wide ? 41 : 23), t.textAccent);
        BoutiqueDraw.drawText(graphics, font, BoutiqueDraw.fitText(font, title.getString(), panelW - 56), panelX + 30, panelY + 13, t.textPrimary);
        if (wide) BoutiqueDraw.drawText(graphics, font, KiemLang.get("auras.custom.hint"), panelX + 30, panelY + 30, t.textMuted);
        if (wide) {
            BoutiqueDraw.drawGlassCard(graphics, leftX, bodyY, leftW, bodyH, false, false);
            section(graphics, t, KiemLang.get("auras.custom.general"), leftX + 12, bodyY + 10, leftW - 24);
        } else {
            BoutiqueDraw.drawGlassCard(graphics, leftX, bodyY, leftW, bodyH, false, false);
            section(graphics, t, KiemLang.get("auras.custom.general"), leftX + 8, bodyY + 10, leftW - 16);
        }
        BoutiqueDraw.drawGlassCard(graphics, viewX, bodyY, viewW, bodyH, false, false);
        BoutiqueDraw.drawText(graphics, font, BoutiqueDraw.fitText(font, KiemLang.get("auras.custom.preview"), viewW - 18), viewX + 9, bodyY + 10, t.textPrimary);
        if (viewW > 170) {
            graphics.fill(viewX + viewW - 47, bodyY + 9, viewX + viewW - 42, bodyY + 14, t.textAccent);
            BoutiqueDraw.drawText(graphics, font, KiemLang.get("auras.custom.live"), viewX + viewW - 37, bodyY + 8, t.textAccent);
        }
        graphics.fillGradient(viewportX0, viewportY0, viewportX1, viewportY1, 0xFF171B29, 0xFF292033);
        preview.extract(graphics, viewportX0, viewportY0, viewportX1, viewportY1, previewYaw, previewPitch);
        if (wide) BoutiqueDraw.drawCenteredText(graphics, font, KiemLang.get("auras.custom.drag_hint"), viewX + viewW / 2, bodyY + bodyH - 43, t.textMuted);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void section(GuiGraphicsExtractor graphics, BoutiqueDraw.Theme t, String label, int x, int y, int w) {
        BoutiqueDraw.drawText(graphics, font, label, x, y, t.textAccent);
        int start = x + BoutiqueDraw.textWidth(font, label) + 10;
        if (start < x + w) graphics.fill(start, y + 4, x + w, y + 5, t.cardBorder);
    }

    private boolean inViewport(double x, double y) {
        return x >= viewportX0 && x < viewportX1 && y >= viewportY0 && y < viewportY1;
    }

    @Override public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT && inViewport(event.x(), event.y())) {
            draggingPreview = true;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingPreview && event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT) {
            previewYaw = (previewYaw + (float)deltaX * .65f) % 360f;
            previewPitch = Math.clamp(previewPitch + (float)deltaY * .45f, -35f, 30f);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingPreview && event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT) {
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
            BoutiqueDraw.Theme t = BoutiqueDraw.theme();
            BoutiqueDraw.drawPill(graphics, getX(), getY(), getWidth(), getHeight(), selected, isHoveredOrFocused());
            String label = BoutiqueDraw.fitText(font, getMessage().getString(), getWidth() - 8);
            BoutiqueDraw.drawCenteredText(graphics, font, label, getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, selected ? t.textOnActivePill : t.textPrimary);
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
            BoutiqueDraw.Theme t = BoutiqueDraw.theme();
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            boolean highlight = isHoveredOrFocused();
            if (highlight) BoutiqueDraw.drawGlassCard(graphics, x, y, w, h, false, true);
            String current = valueLabel();
            int valueW = BoutiqueDraw.textWidth(font, current);
            String name = BoutiqueDraw.fitText(font, KiemLang.get(label), Math.max(12, w - valueW - 14));
            BoutiqueDraw.drawText(graphics, font, name, x + 5, y + 4, t.textMuted);
            BoutiqueDraw.drawText(graphics, font, current, x + w - 5 - valueW, y + 4, highlight ? t.textAccent : t.textPrimary);
            int trackY = y + h - 8;
            int knobX = x + 4 + (int)((w - 8) * value);
            graphics.fill(x + 4, trackY, x + w - 4, trackY + 2, t.isLight ? 0x44000000 : 0xFF3F4256);
            graphics.fill(x + 4, trackY, knobX, trackY + 2, t.textAccent);
            graphics.fill(knobX - 2, trackY - 2, knobX + 3, trackY + 5, highlight ? t.textPrimary : t.textAccent);
        }
    }
}
