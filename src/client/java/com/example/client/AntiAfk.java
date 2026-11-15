package com.example.client;

import net.minecraft.client.Minecraft;

public class AntiAfk {
    private static int timerTicks = 0;
    private static int actionTicksRemaining = 0;
    private static String currentRunningAction = null;

    public static void tick(Minecraft mc) {
        if (!KiemConfig.antiAfkEnabled || mc.player == null || mc.level == null) {
            timerTicks = 0;
            if (actionTicksRemaining > 0) {
                stopAction(mc);
            }
            return;
        }

        // Handle ongoing action release
        if (actionTicksRemaining > 0) {
            actionTicksRemaining--;
            if (actionTicksRemaining <= 0) {
                stopAction(mc);
            }
            return;
        }

        timerTicks++;
        // Minutes to ticks: minutes * 60 seconds * 20 ticks
        int targetTicks = Math.max(20, KiemConfig.antiAfkMinutes * 60 * 20);
        if (timerTicks >= targetTicks) {
            timerTicks = 0;
            triggerAction(mc);
        }
    }

    private static void triggerAction(Minecraft mc) {
        if (mc.player == null || mc.options == null) return;
        currentRunningAction = KiemConfig.antiAfkAction;

        if ("SHIFT".equalsIgnoreCase(currentRunningAction)) {
            mc.options.keyShift.setDown(true);
            actionTicksRemaining = 6; // Hold shift for 6 ticks (~0.3s)
        } else if ("JUMP".equalsIgnoreCase(currentRunningAction)) {
            if (mc.player.onGround()) {
                mc.player.jumpFromGround();
            }
            mc.options.keyJump.setDown(true);
            actionTicksRemaining = 4; // Hold jump for 4 ticks
        } else if ("CLICK".equalsIgnoreCase(currentRunningAction)) {
            KiemCompat.swingMainHand(mc.player);
            mc.options.keyAttack.setDown(true);
            actionTicksRemaining = 2; // Tap attack for 2 ticks
        }
    }

    private static void stopAction(Minecraft mc) {
        if (mc.options != null) {
            if ("SHIFT".equalsIgnoreCase(currentRunningAction)) {
                mc.options.keyShift.setDown(false);
            } else if ("JUMP".equalsIgnoreCase(currentRunningAction)) {
                mc.options.keyJump.setDown(false);
            } else if ("CLICK".equalsIgnoreCase(currentRunningAction)) {
                mc.options.keyAttack.setDown(false);
            }
        }
        currentRunningAction = null;
        actionTicksRemaining = 0;
    }
}
