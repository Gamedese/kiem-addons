package com.example.client.mixin;

import com.example.client.CapeManager;
import com.example.client.KiemConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Redirect(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
        )
    )
    private void kiem$redirectSubmitCapeModel(
        SubmitNodeCollector collector,
        Model<?> model,
        Object state,
        PoseStack poseStack,
        RenderType renderType,
        int light,
        int overlay,
        int outlineColor,
        ModelFeatureRenderer.CrumblingOverlay crumbling
    ) {
        if (CapeManager.getCurrentCape() != CapeManager.CapeType.OFF) {
            AvatarRenderState avatarState = (AvatarRenderState) state;
            if (avatarState.skin != null && avatarState.skin.cape() != null) {
                if (KiemConfig.capeTrueColor) {
                    renderType = RenderTypes.eyes(avatarState.skin.cape().texturePath());
                    light = 0x00F000F0; // Full emissive brightness
                } else {
                    renderType = RenderTypes.breezeWind(avatarState.skin.cape().texturePath(), 0.0f, 0.0f);
                }
            }
        }
        collector.submitModel((Model) model, state, poseStack, renderType, light, overlay, outlineColor, crumbling);
    }
}
