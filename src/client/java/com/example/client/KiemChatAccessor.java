package com.example.client;

import net.minecraft.client.multiplayer.chat.GuiMessage;

public interface KiemChatAccessor {
    GuiMessage.Line kiem$getLineAt(double mouseX, double mouseY);
}
