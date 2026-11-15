package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class ExampleModClient implements ClientModInitializer {
    public static KeyMapping menuKeyMapping;
    public static KeyMapping shoulderSwapKeyMapping;

    @Override
    public void onInitializeClient() {
        SmoothZoom.zoomKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.kiem.zoom",
                KiemCompat.keyboardType(),
                KiemConfig.zoomKey != 0 ? KiemConfig.zoomKey : InputConstants.KEY_C,
                KeyMapping.Category.MISC
        ));

        menuKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.kiem.menu",
                KiemCompat.keyboardType(),
                KiemConfig.menuKey != 0 ? KiemConfig.menuKey : InputConstants.KEY_F6,
                KeyMapping.Category.MISC
        ));

        shoulderSwapKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.kiem.shoulder_swap",
                KiemCompat.keyboardType(),
                KiemConfig.shoulderSwapKey != 0 ? KiemConfig.shoulderSwapKey : InputConstants.KEY_V,
                KeyMapping.Category.MISC
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("kiem").executes(context -> {
                Minecraft.getInstance().execute(() -> {
                    KiemCompat.setScreen(Minecraft.getInstance(), new KiemScreen());
                });
                return 1;
            }));

            dispatcher.register(ClientCommands.literal("kiemafkc").executes(context -> {
                Minecraft.getInstance().execute(AfkCinematicsManager::startCountdown);
                return 1;
            }));
        });

        UpdateChecker.checkAsync();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKeyMapping != null && menuKeyMapping.consumeClick()) {
                if (KiemCompat.getScreen(client) == null) {
                    KiemCompat.setScreen(client, new KiemScreen());
                }
            }
            while (shoulderSwapKeyMapping != null && shoulderSwapKeyMapping.consumeClick()) {
                if (KiemConfig.betterThirdPersonEnabled && KiemConfig.shoulderSurfingEnabled) {
                    KiemConfig.shoulderSide = -KiemConfig.shoulderSide;
                    KiemConfig.save();
                }
            }
            AntiAfk.tick(client);
            com.example.client.aura.SakuraAuraManager.tick(client);
            com.example.client.cosmetic.CosmeticTrailManager.tick(client);
            com.example.client.pet.GroundPetManager.tick(client);
            AfkCinematicsManager.clientTick(client);
        });
    }
}
