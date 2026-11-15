package com.example.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.SwingAnimation;
import org.joml.Quaternionfc;

import java.net.URI;

/**
 * Version-compatibility bridge for Minecraft 26.3+.
 */
public final class KiemCompat extends KiemCompatBase {
    private KiemCompat() { }

    public static void rotate(PoseStack poses, Quaternionfc quat) {
        poses.rotate(quat);
    }

    public static <S> void submitModel(
            SubmitNodeCollector collector,
            Model<? super S> model,
            S state,
            PoseStack poses,
            RenderType renderType,
            int light,
            int overlay,
            int outlineColor
    ) {
        collector.submitModel(model, state, poses, renderType, light, overlay, outlineColor);
    }

    public static <S> void submitModel(
            SubmitNodeCollector collector,
            Model<? super S> model,
            S state,
            PoseStack poses,
            Identifier texture,
            int light,
            int overlay,
            int outlineColor
    ) {
        collector.submitModel(model, state, poses, texture, light, overlay, outlineColor);
    }

    public static InputConstants.Type keyboardType() {
        return InputConstants.Type.KEYBOARD;
    }

    public static boolean isKeyDown(Window window, int keyCode) {
        return InputConstants.isKeyDown(keyCode);
    }

    public static float getAttackAnim(LivingEntity entity, float partialTick) {
        return entity.getSwingAnimation(partialTick);
    }

    public static void swingMainHand(LocalPlayer player) {
        player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
    }

    public static boolean isSwingingStart(LocalPlayer player) {
        return player.isSwinging();
    }

    public static void openUri(String url) {
        Blaze3D.openUri(URI.create(url));
    }

    public static AvatarRenderer<?> getPlayerRenderer(EntityRenderDispatcher dispatcher, AbstractClientPlayer player) {
        return (AvatarRenderer<?>) dispatcher.getRenderer(player);
    }
}
