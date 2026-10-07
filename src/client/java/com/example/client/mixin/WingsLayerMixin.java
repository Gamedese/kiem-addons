package com.example.client.mixin;

import com.example.client.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WingsLayer.class)
public abstract class WingsLayerMixin {

    @Inject(method = "getPlayerElytraTexture", at = @At("HEAD"), cancellable = true)
    private static void kiem$forceDefaultElytraTexture(HumanoidRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof AvatarRenderState avatarState) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && avatarState.id == mc.player.getId()) {
                if (CapeManager.getCurrentCape() != CapeManager.CapeType.OFF) {
                    // Our custom animated capes do not have Elytra wing UVs.
                    // Returning null causes EquipmentLayerRenderer to render the standard vanilla Elytra!
                    cir.setReturnValue(null);
                }
            }
        }
    }
}
