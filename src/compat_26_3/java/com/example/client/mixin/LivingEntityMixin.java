package com.example.client.mixin;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean isSwinging();

    @Inject(method = "getModifiedSwingDuration(Lnet/minecraft/world/item/component/SwingAnimation;)I", at = @At("RETURN"), cancellable = true)
    private void kiem$modifySwingDuration(SwingAnimation animation, CallbackInfoReturnable<Integer> cir) {
        if (!KiemConfig.viewmodelEnabled || KiemConfig.swingSpeed == 0.0f) return;
        Minecraft mc = Minecraft.getInstance();
        if ((Object) this != mc.player) return;

        int base = cir.getReturnValue();
        float multiplier = 1.0f - KiemConfig.swingSpeed;
        if (multiplier < 0.2f) multiplier = 0.2f;
        cir.setReturnValue(Math.max(1, Math.round(base * multiplier)));
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/component/SwingAnimation;Z)Z", at = @At("HEAD"), cancellable = true)
    private void kiem$dontSkipAnimation(InteractionHand hand, SwingAnimation animation, boolean fromServer, CallbackInfoReturnable<Boolean> cir) {
        if (!KiemConfig.viewmodelEnabled || !KiemConfig.dontSkipAnimation) return;
        Minecraft mc = Minecraft.getInstance();
        if ((Object) this != mc.player) return;

        if (this.isSwinging()) {
            cir.setReturnValue(false);
        }
    }
}
