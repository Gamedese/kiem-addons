package com.example.client.mixin;

import com.example.client.AfkCinematicsManager;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void kiem$onKeyPress(long window, int action, net.minecraft.client.input.KeyEvent keyEvent, CallbackInfo ci) {
        if (action == 1 || action == 2) { // Only press or repeat, ignore release (action 0)
            AfkCinematicsManager.onUserInput("klavye tuşu: " + keyEvent.key());
        }
    }
}
