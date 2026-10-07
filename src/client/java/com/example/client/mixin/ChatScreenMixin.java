package com.example.client.mixin;

import com.example.client.KiemChatAccessor;
import com.example.client.KiemCompat;
import com.example.client.KiemConfig;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void kiem$onMouseClicked(MouseButtonEvent event, boolean wasHandled, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == 0 && this.minecraft != null && this.minecraft.hasControlDown() && KiemConfig.copyChatEnabled) {
            if (KiemCompat.getChat(this.minecraft) instanceof KiemChatAccessor accessor) {
                GuiMessage.Line line = accessor.kiem$getLineAt(event.x(), event.y());
                if (line != null && line.parent() != null && line.parent().content() != null) {
                    String fullText = line.parent().content().getString();
                    this.minecraft.keyboardHandler.setClipboard(fullText);
                    KiemCompat.setOverlayMessage(this.minecraft, Component.literal("§a✓ Mesaj panoya kopyalandı!"), false);
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
