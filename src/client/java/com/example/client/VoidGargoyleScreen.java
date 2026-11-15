package com.example.client;

import com.example.client.aura.VoidGargoyleManager;
import com.example.client.aura.VoidGargoylePreview;
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

/** Live cosmetic editor for the Obsidian Void Gargoyle Aura. */
public final class VoidGargoyleScreen extends Screen {
    private static final float[] DEFAULT_HEIGHTS = {0.70f, 1.05f, 1.40f};
    private static final float[] DEFAULT_RADII = {1.12f, 1.25f, 1.18f};

    private final Screen parent;
    private final VoidGargoylePreview preview = new VoidGargoylePreview();
    private int panelX, panelY, panelW, panelH, leftX, leftW, bodyY, bodyH, viewX, viewW;
    private int viewportX0, viewportY0, viewportX1, viewportY1;
    private boolean wide, draggingPreview;
    private int page;
    private float previewYaw = 25f, previewPitch = -8f;

    public VoidGargoyleScreen(Screen parent) {
        super(Component.literal(KiemLang.get("auras.void_gargoyle.title")));
        this.parent = parent;
    }

    @Override
    protected void init() {
        draggingPreview = false;
        panelW = Math.min(900, width - 24);
        panelH = Math.min(440, height - 16);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        wide = panelW >= 760 && panelH >= 416;
        int margin = wide ? 18 : 12;
        int innerW = panelW - margin * 2;
        leftX = panelX + margin;
        leftW = (int) ((innerW - (wide ? 16 : 10)) * (wide ? 0.61f : 0.64f));
        viewX = leftX + leftW + (wide ? 16 : 10);
        viewW = panelX + panelW - margin - viewX;
        bodyY = panelY + (wide ? 64 : 60);
        bodyH = panelH - (wide ? 112 : 100);

        if (wide) {
            addGeneralControls(leftX + 12, bodyY + 23, leftW - 24, true);
            for (int i = 0; i < 3; i++) {
                addGargoyleControls(i, leftX + 12, bodyY + 144 + i * 56, leftW - 24, true);
            }
        } else {
            int gap = 4, tabW = (innerW - gap * 3) / 4;
            for (int i = 0; i < 4; i++) {
                final int selected = i;
                String label = i == 0 ? KiemLang.get("auras.custom.general") : KiemLang.get("auras.gargoyle.whelp") + " " + i;
                MagmaButton tab = new MagmaButton(leftX + i * (tabW + gap), panelY + 34, tabW, 20, label, () -> {
                    page = selected;
                    rebuildWidgets();
                });
                tab.selected = page == i;
                addRenderableWidget(tab);
            }
            if (page == 0) {
                addGeneralControls(leftX + 8, bodyY + 12, leftW - 16, false);
            } else {
                addGargoyleControls(page - 1, leftX + 8, bodyY + 30, leftW - 16, false);
            }
        }

        int footerY = panelY + panelH - 34;
        int footerW = Math.min(164, (innerW - 10) / 2);
        addRenderableWidget(new MagmaButton(leftX, footerY, footerW, 24, KiemLang.get("ui.back"), this::onClose));
        addRenderableWidget(new MagmaButton(panelX + panelW - margin - footerW, footerY, footerW, 24,
                KiemLang.get("auras.custom.defaults"), () -> {
            VoidGargoyleManager.setSpeed(1.0f);
            KiemConfig.resetVoidGargoyleSettings();
            KiemConfig.save();
            rebuildWidgets();
        }));

        viewportX0 = viewX + 4;
        viewportY0 = bodyY + (viewW >= 140 ? 29 : 24);
        viewportX1 = viewX + viewW - 4;
        viewportY1 = bodyY + bodyH - (wide ? 54 : 30);
        MagmaButton resetView = new MagmaButton(viewX + 8, bodyY + bodyH - 26, viewW - 16, 20,
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
            setting(x, y, cellW, widgetH, "auras.custom.scale", "x", 0.3, 1.5,
                    () -> KiemConfig.voidGargoyleScale, v -> KiemConfig.voidGargoyleScale = (float) v, 0.85);
            setting(rightX, y, cellW, widgetH, "auras.custom.speed", "x", 0.25, 2.0,
                    () -> KiemConfig.voidGargoyleSpeed, v -> VoidGargoyleManager.setSpeed((float) v), 1.0);

            // Row 1: Bobbing (Left) | Floating Embers (Right)
            setting(x, y + stride, cellW, widgetH, "auras.custom.bob", "x", 0.0, 3.0,
                    () -> KiemConfig.voidGargoyleBob, v -> KiemConfig.voidGargoyleBob = (float) v, 1.0);

            int emberY = y + stride;
            MagmaButton embers = new MagmaButton(rightX, emberY, cellW - 25, widgetH, embersLabel(), () -> {
                KiemConfig.voidGargoyleEmbers = (KiemConfig.voidGargoyleEmbers + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            embers.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.embers_hint"))));
            addRenderableWidget(embers);
            resetButton(rightX + cellW - 21, emberY, widgetH, () -> {
                KiemConfig.voidGargoyleEmbers = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            // Row 2: Ground Smoke (Left) | Jaw Snapping (Right)
            int row2Y = y + stride * 2;
            MagmaButton smoke = new MagmaButton(x, row2Y, cellW - 25, widgetH, smokeLabel(), () -> {
                KiemConfig.voidGargoyleGroundSmoke = (KiemConfig.voidGargoyleGroundSmoke + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            smoke.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.smoke_hint"))));
            addRenderableWidget(smoke);
            resetButton(x + cellW - 21, row2Y, widgetH, () -> {
                KiemConfig.voidGargoyleGroundSmoke = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            MagmaButton jaw = new MagmaButton(rightX, row2Y, cellW - 25, widgetH, jawLabel(), () -> {
                KiemConfig.voidGargoyleJawRate = (KiemConfig.voidGargoyleJawRate + 1) % 3;
                KiemConfig.save();
                rebuildWidgets();
            });
            jaw.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.jaw_hint"))));
            addRenderableWidget(jaw);
            resetButton(rightX + cellW - 21, row2Y, widgetH, () -> {
                KiemConfig.voidGargoyleJawRate = 1;
                KiemConfig.save();
                rebuildWidgets();
            });
        } else {
            // Narrow single-column mode: 6 stacked controls
            int stride = Math.min(32, (bodyH - 16) / 6);
            int widgetH = Math.min(24, stride - 4);

            setting(x, y, w, widgetH, "auras.custom.scale", "x", 0.3, 1.5,
                    () -> KiemConfig.voidGargoyleScale, v -> KiemConfig.voidGargoyleScale = (float) v, 0.85);
            setting(x, y + stride, w, widgetH, "auras.custom.speed", "x", 0.25, 2.0,
                    () -> KiemConfig.voidGargoyleSpeed, v -> VoidGargoyleManager.setSpeed((float) v), 1.0);
            setting(x, y + stride * 2, w, widgetH, "auras.custom.bob", "x", 0.0, 3.0,
                    () -> KiemConfig.voidGargoyleBob, v -> KiemConfig.voidGargoyleBob = (float) v, 1.0);

            int emberY = y + stride * 3;
            MagmaButton embers = new MagmaButton(x, emberY, w - 25, widgetH, embersLabel(), () -> {
                KiemConfig.voidGargoyleEmbers = (KiemConfig.voidGargoyleEmbers + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            embers.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.embers_hint"))));
            addRenderableWidget(embers);
            resetButton(x + w - 21, emberY, widgetH, () -> {
                KiemConfig.voidGargoyleEmbers = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            int smokeY = y + stride * 4;
            MagmaButton smoke = new MagmaButton(x, smokeY, w - 25, widgetH, smokeLabel(), () -> {
                KiemConfig.voidGargoyleGroundSmoke = (KiemConfig.voidGargoyleGroundSmoke + 1) % 4;
                KiemConfig.save();
                rebuildWidgets();
            });
            smoke.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.smoke_hint"))));
            addRenderableWidget(smoke);
            resetButton(x + w - 21, smokeY, widgetH, () -> {
                KiemConfig.voidGargoyleGroundSmoke = 2;
                KiemConfig.save();
                rebuildWidgets();
            });

            int jawY = y + stride * 5;
            MagmaButton jaw = new MagmaButton(x, jawY, w - 25, widgetH, jawLabel(), () -> {
                KiemConfig.voidGargoyleJawRate = (KiemConfig.voidGargoyleJawRate + 1) % 3;
                KiemConfig.save();
                rebuildWidgets();
            });
            jaw.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.gargoyle.jaw_hint"))));
            addRenderableWidget(jaw);
            resetButton(x + w - 21, jawY, widgetH, () -> {
                KiemConfig.voidGargoyleJawRate = 1;
                KiemConfig.save();
                rebuildWidgets();
            });
        }
    }

    private void addGargoyleControls(int index, int x, int y, int w, boolean columns) {
        int cellW = columns ? (w - 12) / 2 : w;
        int secondX = columns ? x + cellW + 12 : x;
        int secondY = columns ? y : y + Math.min(54, (bodyH - 35) / 2);
        int widgetH = columns ? 26 : Math.min(36, (bodyH - 40) / 2);

        setting(x, y, cellW, widgetH, "auras.custom.height", "blocks", 0.15, 2.2,
                () -> KiemConfig.voidGargoyleHeights[index],
                v -> KiemConfig.voidGargoyleHeights[index] = (float) v,
                DEFAULT_HEIGHTS[index]);

        setting(secondX, secondY, cellW, widgetH, "auras.custom.radius", "blocks", 0.4, 2.2,
                () -> KiemConfig.voidGargoyleRadii[index],
                v -> KiemConfig.voidGargoyleRadii[index] = (float) v,
                DEFAULT_RADII[index]);
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
        MagmaButton button = new MagmaButton(x, y, 21, h, "↺", action);
        button.setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.reset_setting"))));
        addRenderableWidget(button);
    }

    private String embersLabel() {
        return KiemLang.get("auras.gargoyle.embers") + ": " + KiemLang.get("auras.gargoyle.embers." + KiemConfig.voidGargoyleEmbers);
    }

    private String smokeLabel() {
        return KiemLang.get("auras.gargoyle.ground_smoke") + ": " + KiemLang.get("auras.gargoyle.ground_smoke." + KiemConfig.voidGargoyleGroundSmoke);
    }

    private String jawLabel() {
        return KiemLang.get("auras.gargoyle.jaw_rate") + ": " + KiemLang.get("auras.gargoyle.jaw_rate." + KiemConfig.voidGargoyleJawRate);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        BoutiqueDraw.Theme t = BoutiqueDraw.theme();
        BoutiqueDraw.drawBackdrop(graphics, this.width, this.height, System.currentTimeMillis());
        BoutiqueDraw.drawGlassPanel(graphics, panelX, panelY, panelW, panelH);
        graphics.fill(panelX + 18, panelY + 13, panelX + 21, panelY + (wide ? 41 : 23), t.textAccent);
        BoutiqueDraw.drawText(graphics, font, BoutiqueDraw.fitText(font, title.getString(), panelW - 56), panelX + 30, panelY + 13, t.textPrimary);
        if (wide) {
            BoutiqueDraw.drawText(graphics, font, KiemLang.get("auras.void_gargoyle.subtitle"), panelX + 30, panelY + 30, t.textMuted);
        }

        if (wide) {
            BoutiqueDraw.drawGlassCard(graphics, leftX, bodyY, leftW, 118, false, false);
            section(graphics, t, KiemLang.get("auras.custom.general"), leftX + 12, bodyY + 8, leftW - 24);
            for (int i = 0; i < 3; i++) {
                int y = bodyY + 124 + i * 56;
                BoutiqueDraw.drawGlassCard(graphics, leftX, y, leftW, 52, false, false);
                section(graphics, t, KiemLang.get("auras.gargoyle.whelp") + " " + (i + 1), leftX + 12, y + 6, leftW - 24);
            }
        } else {
            BoutiqueDraw.drawGlassCard(graphics, leftX, bodyY, leftW, bodyH, false, false);
            if (page > 0) {
                section(graphics, t, KiemLang.get("auras.gargoyle.whelp") + " " + page, leftX + 8, bodyY + 10, leftW - 16);
            }
        }

        BoutiqueDraw.drawGlassCard(graphics, viewX, bodyY, viewW, bodyH, false, false);
        BoutiqueDraw.drawText(graphics, font, BoutiqueDraw.fitText(font, KiemLang.get("auras.custom.preview"), viewW - 18), viewX + 9, bodyY + 10, t.textPrimary);
        if (viewW > 170) {
            graphics.fill(viewX + viewW - 47, bodyY + 9, viewX + viewW - 42, bodyY + 14, t.textAccent);
            BoutiqueDraw.drawText(graphics, font, KiemLang.get("auras.custom.live"), viewX + viewW - 37, bodyY + 8, t.textAccent);
        }

        graphics.fillGradient(viewportX0, viewportY0, viewportX1, viewportY1, 0xFF15121B, 0xFF281820);
        preview.extract(graphics, viewportX0, viewportY0, viewportX1, viewportY1, previewYaw, previewPitch);
        if (wide) {
            BoutiqueDraw.drawCenteredText(graphics, font, KiemLang.get("auras.custom.drag_hint"), viewX + viewW / 2, bodyY + bodyH - 43, t.textMuted);
        }
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void section(GuiGraphicsExtractor graphics, BoutiqueDraw.Theme t, String label, int x, int y, int w) {
        BoutiqueDraw.drawText(graphics, font, label, x, y, t.textAccent);
        int start = x + BoutiqueDraw.textWidth(font, label) + 10;
        if (start < x + w) {
            graphics.fill(start, y + 4, x + w, y + 5, t.cardBorder);
        }
    }

    private boolean inViewport(double x, double y) {
        return x >= viewportX0 && x < viewportX1 && y >= viewportY0 && y < viewportY1;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT && inViewport(event.x(), event.y())) {
            draggingPreview = true;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingPreview && event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT) {
            previewYaw = (previewYaw + (float) deltaX * 0.65f) % 360f;
            previewPitch = Math.clamp(previewPitch + (float) deltaY * 0.45f, -35f, 30f);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingPreview && event.button() == com.mojang.blaze3d.platform.InputConstants.MOUSE_BUTTON_LEFT) {
            draggingPreview = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        KiemConfig.save();
        KiemCompat.setScreen(minecraft, parent);
    }

    @Override
    public void removed() {
        draggingPreview = false;
        KiemConfig.save();
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private final class MagmaButton extends Button {
        boolean selected;

        MagmaButton(int x, int y, int w, int h, String label, Runnable action) {
            super(x, y, w, h, Component.literal(label), button -> action.run(), DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            BoutiqueDraw.Theme t = BoutiqueDraw.theme();
            BoutiqueDraw.drawPill(graphics, getX(), getY(), getWidth(), getHeight(), selected, isHoveredOrFocused());
            String label = BoutiqueDraw.fitText(font, getMessage().getString(), getWidth() - 8);
            BoutiqueDraw.drawCenteredText(graphics, font, label, getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2,
                    selected ? t.textOnActivePill : t.textPrimary);
        }
    }

    private final class SettingSlider extends AbstractSliderButton {
        private final String label, unit;
        private final double min, max;
        private final DoubleSupplier getter;
        private final DoubleConsumer setter;

        SettingSlider(int x, int y, int w, int h, String label, String unit, double min, double max,
                      DoubleSupplier getter, DoubleConsumer setter) {
            super(x, y, w, h, Component.empty(), Math.clamp((getter.getAsDouble() - min) / (max - min), 0, 1));
            this.label = label;
            this.unit = unit;
            this.min = min;
            this.max = max;
            this.getter = getter;
            this.setter = setter;
            updateMessage();
            if (label.equals("auras.custom.speed")) {
                setTooltip(Tooltip.create(Component.literal(KiemLang.get("auras.custom.speed_hint"))));
            }
        }

        void refresh() {
            value = Math.clamp((getter.getAsDouble() - min) / (max - min), 0, 1);
            updateMessage();
        }

        private String valueLabel() {
            String suffix = unit.equals("blocks") ? " " + KiemLang.get("auras.custom.blocks") : unit;
            return String.format(Locale.ROOT, "%.2f", getter.getAsDouble()) + suffix;
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(KiemLang.get(label) + ": " + valueLabel()));
        }

        @Override
        protected void applyValue() {
            setter.accept(Math.round((min + value * (max - min)) * 100) / 100.0);
            KiemConfig.save();
        }

        @Override
        public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            BoutiqueDraw.Theme t = BoutiqueDraw.theme();
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            boolean highlight = isHoveredOrFocused();
            if (highlight) {
                BoutiqueDraw.drawGlassCard(graphics, x, y, w, h, false, true);
            }
            String current = valueLabel();
            int valueW = BoutiqueDraw.textWidth(font, current);
            String name = BoutiqueDraw.fitText(font, KiemLang.get(label), Math.max(12, w - valueW - 14));
            BoutiqueDraw.drawText(graphics, font, name, x + 5, y + 4, t.textMuted);
            BoutiqueDraw.drawText(graphics, font, current, x + w - 5 - valueW, y + 4, highlight ? t.textAccent : t.textPrimary);

            int trackY = y + h - 8;
            int knobX = x + 4 + (int) ((w - 8) * value);
            graphics.fill(x + 4, trackY, x + w - 4, trackY + 2, t.isLight ? 0x44000000 : 0xFF3D2A33);
            graphics.fill(x + 4, trackY, knobX, trackY + 2, t.textAccent);
            graphics.fill(knobX - 2, trackY - 2, knobX + 3, trackY + 5, highlight ? t.textPrimary : t.textAccent);
        }
    }
}
