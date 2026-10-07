package com.example.client.mixin;

import com.example.client.KiemConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(method = "scale(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("RETURN"))
    private void kiem$scaleAvatar(AvatarRenderState state, PoseStack poseStack, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        boolean isSelf = (mc.player != null && state.id == mc.player.getId());
        if (isSelf) {
            if (KiemConfig.selfScaleEnabled) {
                poseStack.scale(KiemConfig.selfScaleX, KiemConfig.selfScaleY, KiemConfig.selfScaleZ);
            }
        } else {
            if (KiemConfig.othersScaleEnabled) {
                poseStack.scale(KiemConfig.othersScaleX, KiemConfig.othersScaleY, KiemConfig.othersScaleZ);
            }
        }
    }
}
