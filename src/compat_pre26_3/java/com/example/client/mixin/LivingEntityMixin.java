package com.example.client.mixin;

import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public boolean swinging;
    @Shadow public int swingTime;
    @Shadow private int getCurrentSwingDuration() { throw new AssertionError(); }

    @Inject(method = "getCurrentSwingDuration", at = @At("RETURN"), cancellable = true)
    private void kiem$modifySwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (!KiemConfig.viewmodelEnabled || KiemConfig.swingSpeed == 0.0f) return;
        Minecraft mc = Minecraft.getInstance();
        if ((Object) this != mc.player) return;

        int base = cir.getReturnValue();
        // swingSpeed is -2.0 to 1.0
        // When negative, it increases the duration so the visual swing runs in slow motion
        float multiplier = 1.0f - KiemConfig.swingSpeed;
        if (multiplier < 0.2f) multiplier = 0.2f;
        cir.setReturnValue(Math.max(1, Math.round(base * multiplier)));
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void kiem$dontSkipAnimation(InteractionHand hand, boolean fromServer, CallbackInfo ci) {
        if (!KiemConfig.viewmodelEnabled || !KiemConfig.dontSkipAnimation) return;
        Minecraft mc = Minecraft.getInstance();
        if ((Object) this != mc.player) return;

        // If the local player is currently swinging and hasn't finished the animation yet,
        // do not reset swingTime or restart the animation - let it finish its full motion!
        if (this.swinging && this.swingTime < this.getCurrentSwingDuration()) {
            ci.cancel();
        }
    }
}
