package com.example.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {


    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void injectCustomCape(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        Minecraft mc = Minecraft.getInstance();

        boolean isSelf = (mc.player != null && player.getUUID().equals(mc.player.getUUID()))
                || (mc.getUser() != null && mc.getUser().getProfileId() != null && player.getUUID().equals(mc.getUser().getProfileId()));

        if (isSelf) {
            PlayerSkin original = cir.getReturnValue();
            if (original != null) {
                ClientAsset.ResourceTexture capeAsset = com.example.client.CapeManager.getCurrentCapeAsset();
                if (!"OFF".equalsIgnoreCase(com.example.client.KiemConfig.cosmeticWing)
                        || (com.example.client.KiemConfig.cosmeticWaistHideCape
                        && !com.example.client.cosmetic.CosmeticCatalog.WaistType.fromName(com.example.client.KiemConfig.cosmeticWaist).isOff())) {
                    capeAsset = null;
                }
                PlayerSkin modified = new PlayerSkin(
                        original.body(),
                        capeAsset,
                        original.elytra(),
                        original.model(),
                        original.secure()
                );
                cir.setReturnValue(modified);
            }
        }
    }
}