package com.example.client.mixin;

import com.example.client.KiemChatAccessor;
import com.example.client.KiemCompat;
import com.example.client.KiemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements KiemChatAccessor {

    @Shadow @Final private List<GuiMessage> allMessages;
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private int chatScrollbarPos;

    @Shadow protected abstract void refreshTrimmedMessages();
    @Shadow public abstract int getLinesPerPage();
    @Shadow public abstract int getWidth();
    @Shadow protected abstract double getScale();

    @Unique
    private static class CompactTrack {
        final Component baseComponent;
        final String cleanText;
        final int count;

        CompactTrack(Component baseComponent, String cleanText, int count) {
            this.baseComponent = baseComponent;
            this.cleanText = cleanText;
            this.count = count;
        }
    }

    @Unique
    private final java.util.Map<GuiMessage, CompactTrack> kiem$compactTrackMap = new java.util.WeakHashMap<>();

    /**
     * 1. Infinite Chat History
     * Changes the vanilla 100-message limit in queues to 16,384 when enabled.
     */
    @ModifyConstant(method = {"addMessageToQueue", "addMessageToDisplayQueue"}, constant = @Constant(intValue = 100))
    private int kiem$modifyMaxChatHistory(int original) {
        return KiemConfig.infiniteChat ? 16384 : original;
    }

    @Shadow public abstract void resetChatScroll();

    @Unique private boolean kiem$wasChatPeekActive = false;

    /**
     * 2. Chat Peek Focus Rendering
     * When holding the Chat Peek keybind during gameplay, force FOREGROUND display mode so that
     * full background, all messages with FULLY_VISIBLE alpha, and scrollbar are rendered!
     */
    @ModifyVariable(
            method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ChatComponent.DisplayMode kiem$modifyDisplayMode(ChatComponent.DisplayMode mode) {
        if (KiemConfig.isChatPeekActive()) {
            return ChatComponent.DisplayMode.FOREGROUND;
        }
        return mode;
    }

    @Inject(method = "getHeight()I", at = @At("HEAD"), cancellable = true)
    private void kiem$getChatHeight(CallbackInfoReturnable<Integer> cir) {
        if (KiemConfig.isChatPeekActive() && this.minecraft != null && this.minecraft.options != null) {
            cir.setReturnValue(ChatComponent.getHeight(this.minecraft.options.chatHeightFocused().get()));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void kiem$onTick(CallbackInfo ci) {
        boolean active = KiemConfig.isChatPeekActive();
        if (!active && kiem$wasChatPeekActive) {
            this.resetChatScroll();
        }
        kiem$wasChatPeekActive = active;
    }

    /**
     * 3. Message Compactor
     * Scans the last 5 messages in allMessages. If a match is found, removes the older message,
     * increments the count, and pushes the updated compacted message to the front (bottom of chat).
     */
    @Inject(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kiem$compactMessages(Component component, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci) {
        if (!KiemConfig.compactMessages) {
            return;
        }

        String incomingText = component.getString();
        int scanLimit = Math.min(5, this.allMessages.size());

        for (int i = 0; i < scanLimit; i++) {
            GuiMessage existingMsg = this.allMessages.get(i);
            CompactTrack track = kiem$compactTrackMap.get(existingMsg);

            String existingCleanText = (track != null) ? track.cleanText : existingMsg.content().getString();
            if (incomingText.equals(existingCleanText)) {
                // Match found in the last 5 messages!
                Component baseComp = (track != null) ? track.baseComponent : existingMsg.content();
                int newCount = (track != null ? track.count : 1) + 1;

                // Remove the older message occurrence
                this.allMessages.remove(i);
                kiem$compactTrackMap.remove(existingMsg);

                // Build compacted component with badge
                Component updatedComp = baseComp.copy().append(Component.literal(" §8(" + newCount + "x)"));
                GuiMessage compactedMsg = new GuiMessage(
                        KiemCompat.getGuiTicks(this.minecraft),
                        updatedComp,
                        signature,
                        source,
                        tag
                );

                kiem$compactTrackMap.put(compactedMsg, new CompactTrack(baseComp, incomingText, newCount));
                this.allMessages.addFirst(compactedMsg);
                this.refreshTrimmedMessages();
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void kiem$onClearMessages(boolean clearSentHistory, CallbackInfo ci) {
        kiem$compactTrackMap.clear();
    }

    /**
     * 4. Helper to find message line under cursor for Copy Chat
     */
    @Override
    public GuiMessage.Line kiem$getLineAt(double mouseX, double mouseY) {
        if (this.minecraft == null || this.minecraft.getWindow() == null) return null;
        int guiHeight = this.minecraft.getWindow().getGuiScaledHeight();
        double scale = this.getScale();
        if (scale <= 0.0) return null;

        double xRel = (mouseX - 4.0) / scale;
        if (xRel < 0.0 || xRel > (double) this.getWidth() / scale) {
            return null;
        }

        double yRel = ((double) (guiHeight - 40) - mouseY) / scale;
        if (yRel < 0.0) return null;

        int lineSlot = (int) Math.floor(yRel / 9.0);
        if (lineSlot < 0 || lineSlot >= this.getLinesPerPage()) {
            return null;
        }

        int targetIndex = lineSlot + this.chatScrollbarPos;
        if (targetIndex >= 0 && targetIndex < this.trimmedMessages.size()) {
            return this.trimmedMessages.get(targetIndex);
        }
        return null;
    }
}
