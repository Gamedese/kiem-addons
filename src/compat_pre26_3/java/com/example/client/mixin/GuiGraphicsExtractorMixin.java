package com.example.client.mixin;

import com.example.client.KiemConfig;
import com.example.client.TooltipQoL;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Shadow @Final private Matrix3x2fStack pose;
    @Shadow public abstract int guiWidth();
    @Shadow public abstract int guiHeight();
    @Shadow public abstract void fill(int minX, int minY, int maxX, int maxY, int color);

    @Inject(method = "applyCursor", at = @At("HEAD"))
    private void kiem$renderCinematicBars(com.mojang.blaze3d.platform.Window window, CallbackInfo ci) {
        float progress = com.example.client.SmoothZoom.getCinematicProgress();
        if (progress <= 0.001f) return;

        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc == null) return;
        // Don't render over modal menus (like Options or KiemScreen), but allow in-game and ChatScreen
        net.minecraft.client.gui.screens.Screen screen = com.example.client.KiemCompat.getScreen(mc);
        if (screen != null && !(screen instanceof net.minecraft.client.gui.screens.ChatScreen)) {
            return;
        }

        int w = this.guiWidth();
        int h = this.guiHeight();
        int barH = Math.round(h * KiemConfig.cinematicBarSize * progress);
        if (barH > 0) {
            this.fill(0, 0, w, barH, 0xFF000000);
            this.fill(0, h - barH, w, h, 0xFF000000);
        }
    }

    @Inject(
            method = "tooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix3x2fStack;pushMatrix()Lorg/joml/Matrix3x2fStack;",
                    shift = At.Shift.AFTER
            )
    )
    private void kiem$applyTooltipTransform(
            Font font,
            List<ClientTooltipComponent> components,
            int mouseX,
            int mouseY,
            ClientTooltipPositioner positioner,
            Identifier texture,
            CallbackInfo ci
    ) {
        if (KiemConfig.tooltipScrollEnabled || KiemConfig.tooltipScaleEnabled) {
            float scale = KiemConfig.tooltipScale;
            float scrollY = TooltipQoL.getScrollY();

            if (scale != 1.0f && KiemConfig.tooltipScaleEnabled) {
                this.pose.scaleAround(scale, scale, (float) mouseX, (float) mouseY);
            }

            if (scrollY != 0.0f && KiemConfig.tooltipScrollEnabled) {
                this.pose.translate(0.0f, scrollY);
            }
        }
    }
}
