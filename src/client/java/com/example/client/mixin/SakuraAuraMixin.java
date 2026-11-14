package com.example.client.mixin;

import com.example.client.KiemConfig;
import com.example.client.aura.SakuraAuraRenderer;
import com.example.client.aura.SakuraPreviewState;
import com.example.client.aura.VoidGargoyleRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class SakuraAuraMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"))
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void kiem$submitSakuraAura(LivingEntityRenderState state, PoseStack poses,
                                      SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (state instanceof AvatarRenderState avatar) {
            var gargoylePreview = ((SakuraPreviewState) (Object) avatar).kiem$getGargoylePreview();
            if (gargoylePreview != null) {
                var offset = ((EntityRenderer) (Object) this).getRenderOffset(state);
                poses.pushPose();
                poses.translate(-offset.x, -offset.y, -offset.z);
                VoidGargoyleRenderer.submitPreview(avatar, gargoylePreview, poses, collector, camera);
                poses.popPose();
                return;
            }

            var sakuraPreview = ((SakuraPreviewState) (Object) avatar).kiem$getSakuraPreview();
            if (sakuraPreview != null) {
                var offset = ((EntityRenderer) (Object) this).getRenderOffset(state);
                poses.pushPose();
                poses.translate(-offset.x, -offset.y, -offset.z);
                SakuraAuraRenderer.submitPreview(avatar, sakuraPreview, poses, collector, camera);
                poses.popPose();
                return;
            }

            // In-world rendering: check active trail selection (Sakura Bloom Trail)
            if (KiemConfig.sakuraAuraEnabled) {
                var offset = ((EntityRenderer) (Object) this).getRenderOffset(state);
                poses.pushPose();
                poses.translate(-offset.x, -offset.y, -offset.z);
                SakuraAuraRenderer.submit(avatar, poses, collector, camera);
                poses.popPose();
            }
        }
    }
}
