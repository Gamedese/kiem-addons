package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KiemCompat {
    private static Field MC_SCREEN_FIELD;
    private static Method MC_SET_SCREEN;
    private static Method GUI_SET_SCREEN;
    private static Method GUI_GET_SCREEN;
    private static Field GUI_HUD_FIELD;
    private static Method GUI_GET_CHAT;
    private static Method HUD_GET_CHAT;
    private static Method GUI_GET_TICKS;
    private static Method HUD_GET_TICKS;
    private static Method GUI_SET_OVERLAY;
    private static Method HUD_SET_OVERLAY;

    static {
        try {
            MC_SCREEN_FIELD = Minecraft.class.getField("screen");
        } catch (Throwable ignored) {}

        try {
            MC_SET_SCREEN = Minecraft.class.getMethod("setScreen", Screen.class);
        } catch (Throwable ignored) {}

        try {
            Class<?> guiClass = Class.forName("net.minecraft.client.gui.Gui");
            try {
                GUI_GET_SCREEN = guiClass.getMethod("screen");
            } catch (Throwable ignored) {}
            try {
                GUI_SET_SCREEN = guiClass.getMethod("setScreen", Screen.class);
            } catch (Throwable ignored) {}
            try {
                GUI_HUD_FIELD = guiClass.getField("hud");
            } catch (Throwable ignored) {}
            try {
                GUI_GET_CHAT = guiClass.getMethod("getChat");
            } catch (Throwable ignored) {}
            try {
                GUI_GET_TICKS = guiClass.getMethod("getGuiTicks");
            } catch (Throwable ignored) {}
            try {
                GUI_SET_OVERLAY = guiClass.getMethod("setOverlayMessage", Component.class, boolean.class);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}

        try {
            Class<?> hudClass = Class.forName("net.minecraft.client.gui.Hud");
            try {
                HUD_GET_CHAT = hudClass.getMethod("getChat");
            } catch (Throwable ignored) {}
            try {
                HUD_GET_TICKS = hudClass.getMethod("getGuiTicks");
            } catch (Throwable ignored) {}
            try {
                HUD_SET_OVERLAY = hudClass.getMethod("setOverlayMessage", Component.class, boolean.class);
            } catch (Throwable ignored) {}
        } catch (Throwable ignored) {}
    }

    public static Screen getScreen(Minecraft mc) {
        if (mc == null) return null;
        if (MC_SCREEN_FIELD != null) {
            try {
                return (Screen) MC_SCREEN_FIELD.get(mc);
            } catch (Throwable ignored) {}
        }
        if (mc.gui != null && GUI_GET_SCREEN != null) {
            try {
                return (Screen) GUI_GET_SCREEN.invoke(mc.gui);
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public static void setScreen(Minecraft mc, Screen screen) {
        if (mc == null) return;
        if (MC_SET_SCREEN != null) {
            try {
                MC_SET_SCREEN.invoke(mc, screen);
                return;
            } catch (Throwable ignored) {}
        }
        if (mc.gui != null && GUI_SET_SCREEN != null) {
            try {
                GUI_SET_SCREEN.invoke(mc.gui, screen);
            } catch (Throwable ignored) {}
        }
    }

    public static ChatComponent getChat(Minecraft mc) {
        if (mc == null || mc.gui == null) return null;
        if (GUI_GET_CHAT != null) {
            try {
                return (ChatComponent) GUI_GET_CHAT.invoke(mc.gui);
            } catch (Throwable ignored) {}
        }
        if (GUI_HUD_FIELD != null) {
            try {
                Object hud = GUI_HUD_FIELD.get(mc.gui);
                if (hud != null && HUD_GET_CHAT != null) {
                    return (ChatComponent) HUD_GET_CHAT.invoke(hud);
                }
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public static int getGuiTicks(Minecraft mc) {
        if (mc == null || mc.gui == null) return 0;
        if (GUI_GET_TICKS != null) {
            try {
                return (int) GUI_GET_TICKS.invoke(mc.gui);
            } catch (Throwable ignored) {}
        }
        if (GUI_HUD_FIELD != null) {
            try {
                Object hud = GUI_HUD_FIELD.get(mc.gui);
                if (hud != null && HUD_GET_TICKS != null) {
                    return (int) HUD_GET_TICKS.invoke(hud);
                }
            } catch (Throwable ignored) {}
        }
        return 0;
    }

    public static void setOverlayMessage(Minecraft mc, Component component, boolean animate) {
        if (mc == null || mc.gui == null) return;
        if (GUI_SET_OVERLAY != null) {
            try {
                GUI_SET_OVERLAY.invoke(mc.gui, component, animate);
                return;
            } catch (Throwable ignored) {}
        }
        if (GUI_HUD_FIELD != null) {
            try {
                Object hud = GUI_HUD_FIELD.get(mc.gui);
                if (hud != null && HUD_SET_OVERLAY != null) {
                    HUD_SET_OVERLAY.invoke(hud, component, animate);
                }
            } catch (Throwable ignored) {}
        }
    }
}
