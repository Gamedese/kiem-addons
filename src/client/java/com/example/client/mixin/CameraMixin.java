package com.example.client.mixin;

import com.example.client.BetterThirdPersonManager;
import com.example.client.KiemConfig;
import com.example.client.SmoothZoom;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @org.spongepowered.asm.mixin.Shadow private net.minecraft.world.entity.Entity entity;
    @org.spongepowered.asm.mixin.Shadow private net.minecraft.world.phys.Vec3 position;
    @org.spongepowered.asm.mixin.Shadow private net.minecraft.world.level.Level level;
    @org.spongepowered.asm.mixin.Shadow private boolean detached;
    @org.spongepowered.asm.mixin.Shadow protected abstract void setPosition(double x, double y, double z);
    @org.spongepowered.asm.mixin.Shadow protected abstract void setPosition(net.minecraft.world.phys.Vec3 pos);
    @org.spongepowered.asm.mixin.Shadow protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "update", at = @At("HEAD"))
    private void kiem$onCameraUpdate(DeltaTracker deltaTracker, CallbackInfo ci) {
        SmoothZoom.update();
    }

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void kiem$onAlignWithEntity(float partialTicks, CallbackInfo ci) {
        Camera cam = (Camera) (Object) this;
        if (com.example.client.AfkCinematicsManager.isAfkActive()) {
            this.detached = true; // Force player body to render (3rd person) even if client was in first-person
            com.example.client.AfkCinematicsManager.updateCamera(cam, this.level, this.entity, partialTicks,
                    this::setRotation,
                    this::setPosition
            );
            return;
        }

        BetterThirdPersonManager.updateCamera(cam, this.level, this.entity, partialTicks,
                this::setRotation,
                this::setPosition
        );
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void kiem$onReset(CallbackInfo ci) {
        BetterThirdPersonManager.reset();
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void kiem$adjustCameraHeight(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (com.example.client.AfkCinematicsManager.isAfkActive()) {
            return;
        }
        if (KiemConfig.selfScaleEnabled && KiemConfig.selfScaleCamera) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null && this.entity == mc.player) {
                float scaleY = KiemConfig.selfScaleY;
                float baseEyeHeight = mc.player.getEyeHeight();
                double deltaY = baseEyeHeight * (scaleY - 1.0f);
                this.setPosition(this.position.x, this.position.y + deltaY, this.position.z);
            }
        }
    }

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void kiem$onCalculateFov(float tickDelta, CallbackInfoReturnable<Float> cir) {
        if (com.example.client.AfkCinematicsManager.isAfkActive()) {
            float afkZoom = com.example.client.AfkCinematicsManager.getFovMultiplier();
            if (afkZoom > 1.0001f) {
                cir.setReturnValue(cir.getReturnValue() / afkZoom);
            }
            return;
        }

        if (!KiemConfig.zoomEnabled) return;
        float zoom = SmoothZoom.getCurrentZoom();
        if (zoom > 1.0001f) {
            cir.setReturnValue(cir.getReturnValue() / zoom);
        }
    }
}
