package com.example.client.mixin;

import com.example.client.CapeManager;
import com.example.client.KiemCompat;
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
                // Vanilla cape always uses entitySolid. Eyes/BreezeWind was causing depth-mask failure
                // and making parts of the cape invisible/transparent!
                renderType = RenderTypes.entitySolid(avatarState.skin.cape().texturePath());
                if (KiemConfig.capeTrueColor) {
                    light = 0x00F000F0; // Full emissive brightness without breaking depth
                }
            }
        }
        KiemCompat.submitModel(collector, (Model) model, state, poseStack, renderType, light, overlay, outlineColor);
    }
}
