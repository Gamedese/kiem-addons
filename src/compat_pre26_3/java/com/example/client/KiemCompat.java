package com.example.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionfc;

/**
 * Version-compatibility bridge for Minecraft 26.1.2 and 26.2.
 */
public final class KiemCompat extends KiemCompatBase {
    private KiemCompat() { }

    public static void rotate(PoseStack poses, Quaternionfc quat) {
        poses.mulPose(quat);
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
        collector.submitModel(model, state, poses, renderType, light, overlay, outlineColor,
                (ModelFeatureRenderer.CrumblingOverlay) null);
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
        collector.submitModel(model, state, poses, texture, light, overlay, outlineColor,
                (ModelFeatureRenderer.CrumblingOverlay) null);
    }

    public static InputConstants.Type keyboardType() {
        return InputConstants.Type.KEYSYM;
    }

    public static boolean isKeyDown(Window window, int keyCode) {
        return InputConstants.isKeyDown(window, keyCode);
    }

    public static float getAttackAnim(LivingEntity entity, float partialTick) {
        return entity.getAttackAnim(partialTick);
    }

    public static void swingMainHand(LocalPlayer player) {
        player.swing(InteractionHand.MAIN_HAND);
    }

    public static boolean isSwingingStart(LocalPlayer player) {
        return player.swingTime == 1;
    }

    public static void openUri(String url) {
        Util.getPlatform().openUri(url);
    }

    public static AvatarRenderer<?> getPlayerRenderer(EntityRenderDispatcher dispatcher, AbstractClientPlayer player) {
        return dispatcher.getPlayerRenderer(player);
    }
}
