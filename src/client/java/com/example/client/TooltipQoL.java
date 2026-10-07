package com.example.client;

import net.minecraft.world.inventory.Slot;

public class TooltipQoL {
    private static float scrollY = 0.0f;
    private static Slot lastHoveredSlot = null;

    public static float getScrollY() {
        return scrollY;
    }

    public static void scroll(float delta) {
        scrollY += delta * 24.0f;
    }

    public static void resetScroll() {
        scrollY = 0.0f;
    }

    public static void onHoveredSlotChanged(Slot newSlot) {
        if (newSlot != lastHoveredSlot) {
            lastHoveredSlot = newSlot;
            resetScroll();
        }
    }

    public static void zoom(float delta) {
        float newScale = KiemConfig.tooltipScale + (delta * 0.05f);
        if (newScale < 0.35f) newScale = 0.35f;
        if (newScale > 1.6f) newScale = 1.6f;
        KiemConfig.tooltipScale = Math.round(newScale * 100.0f) / 100.0f;
        KiemConfig.save();
    }

    public static void resetScale() {
        KiemConfig.tooltipScale = 1.0f;
        KiemConfig.save();
    }
}
