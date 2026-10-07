package com.example.client.mixin;

import com.example.client.KiemCompat;
import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void kiem$onMouseScroll(long window, double xoffset, double yoffset, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.getWindow() != null && window == this.minecraft.getWindow().handle()) {
            if (com.example.client.SmoothZoom.isZooming() && KiemConfig.zoomScrollEnabled) {
                double scrollAmount = (yoffset != 0.0) ? yoffset : xoffset;
                if (scrollAmount != 0.0) {
                    com.example.client.SmoothZoom.onScroll(scrollAmount);
                    ci.cancel();
                    return;
                }
            }

            if (KiemConfig.isChatPeekActive()) {
                double scrollAmount = (yoffset != 0.0) ? yoffset : xoffset;
                if (scrollAmount != 0.0 && this.minecraft != null && KiemCompat.getChat(this.minecraft) != null) {
                    double mult = this.minecraft.hasShiftDown() ? 1.0 : 7.0;
                    int delta = (int) (Math.signum(scrollAmount) * mult);
                    KiemCompat.getChat(this.minecraft).scrollChat(delta);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"))
    private void kiem$scaleZoomSensitivity(double timeDelta, CallbackInfo ci) {
        if (Math.abs(this.accumulatedDX) > 0.05 || Math.abs(this.accumulatedDY) > 0.05) {
            com.example.client.AfkCinematicsManager.onUserInput("fare hareketi: " + String.format("%.2f", accumulatedDX) + "/" + String.format("%.2f", accumulatedDY));
        }

        if (!KiemConfig.zoomEnabled || !KiemConfig.zoomSensitivityScale) return;
        float zoom = com.example.client.SmoothZoom.getCurrentZoom();
        if (zoom > 1.0001f) {
            // Use square-root scaling so mouse movement remains responsive and doesn't feel sluggish
            double divisor = Math.sqrt(zoom);
            this.accumulatedDX /= divisor;
            this.accumulatedDY /= divisor;
        }
    }

    @Inject(method = "onButton", at = @At("HEAD"))
    private void kiem$onMouseButton(long window, net.minecraft.client.input.MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
        com.example.client.AfkCinematicsManager.onUserInput("fare tuşu");
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "turnPlayer",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z")
    )
    private boolean kiem$redirectSmoothCamera(net.minecraft.client.Options options) {
        if (com.example.client.SmoothZoom.isZooming() && KiemConfig.zoomCinematicCamera) {
            return true;
        }
        return options.smoothCamera;
    }
}
