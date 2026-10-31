package com.example.client.aura;

import com.example.client.KiemConfig;
import com.example.client.model.aura.VoidGargoyleModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;

import java.util.List;
import java.util.function.IntFunction;

/** Renderer for Obsidian Void Gargoyles with full elytra flight & swim conforming rotations. */
public final class VoidGargoyleRenderer {
    private static final Identifier GARGOYLE = Identifier.fromNamespaceAndPath("modid", "textures/aura/void_gargoyle.png");
    private static final Identifier EMBER_TEX = Identifier.fromNamespaceAndPath("modid", "textures/aura/sakura_petal.png");
    private static VoidGargoyleModel model;

    private VoidGargoyleRenderer() { }

    public static void submit(AvatarRenderState state, PoseStack poses, SubmitNodeCollector collector,
                              CameraRenderState camera) {
        Minecraft client = Minecraft.getInstance();
        boolean enabled = "VOID_GARGOYLE".equals(KiemConfig.activeAura) || KiemConfig.voidGargoyleAuraEnabled;
        if (!enabled || client.player == null || client.level == null
                || state.id != client.player.getId() || state.isInvisible || state.isSpectator
                || client.player.isSleeping() || client.options.getCameraType().isFirstPerson()
                || camera == null || !camera.initialized) return;

        // Inventory/GUI entity previews use a synthetic camera; never draw a world aura there.
        if (camera.pos == null || camera.pos.distanceToSqr(client.gameRenderer.mainCamera().position()) > 0.01) return;

        float time = VoidGargoyleManager.orbitTime(state.ageInTicks);
        boolean moving = client.player.getDeltaMovement().horizontalDistanceSqr() > 0.0009;

        submitGargoyles(state, state.ageInTicks, KiemConfig.voidGargoyleScale, state.lightCoords, poses, collector,
                i -> VoidGargoyleManager.sample(time, i),
                i -> VoidGargoyleManager.getJawAngle(i, 1.0f),
                i -> VoidGargoyleManager.getWingFlap(i, state.ageInTicks, moving));
    }

    /** Explicit GUI-only entry for Void Gargoyle customization preview. */
    public static void submitPreview(AvatarRenderState state, VoidGargoylePreview.Frame frame,
                                     PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        submitGargoyles(state, frame.age(), frame.settings().scale(), state.lightCoords, poses, collector,
                i -> frame.settings().sample(frame.time(), i),
                i -> frame.jawAngles()[i],
                i -> frame.wingFlaps()[i]);

        if (frame.settings().embers() != 0 || frame.settings().groundSmoke() != 0) {
            submitPreviewEmbers(frame.embers(), frame.age(), 0, 0, 0, state.lightCoords,
                    camera.orientation, poses, collector);
        }
    }

    private static void submitGargoyles(AvatarRenderState state, float age, float scale, int light, PoseStack poses,
                                        SubmitNodeCollector collector,
                                        IntFunction<VoidGargoyleOrbit.Sample> sample,
                                        IntFunction<Float> jawSupplier,
                                        IntFunction<Float> wingSupplier) {
        if (model == null) model = new VoidGargoyleModel();
        Minecraft client = Minecraft.getInstance();

        boolean elytraFlying = (state != null && state.isFallFlying)
                || (client.player != null && client.player.isFallFlying());
        float flyScale = (state != null && state.isFallFlying) ? state.fallFlyingScale() : 0.0f;
        if (elytraFlying && flyScale < 0.1f) {
            flyScale = 1.0f;
        }
        boolean swimming = (state != null) && ((state.swimAmount > 0.05f) || state.isVisuallySwimming);
        boolean flying = elytraFlying || swimming;

        if (flying) {
            poses.pushPose();
            // Pivot around center of player torso:
            poses.translate(0, 0.9, 0);
            // Align with player body yaw:
            float bodyRot = (state != null) ? state.bodyRot : (client.player != null ? client.player.yBodyRot : 0.0f);
            float xRot = (state != null) ? state.xRot : (client.player != null ? client.player.getXRot() : 0.0f);
            poses.mulPose(Axis.YP.rotationDegrees(180.0f - bodyRot));
            if (elytraFlying) {
                float pitchAngle = flyScale * (-90.0f - xRot);
                poses.mulPose(Axis.XP.rotationDegrees(pitchAngle));
                if (state != null && state.shouldApplyFlyingYRot) {
                    poses.mulPose(Axis.YP.rotation(state.flyingYRot));
                }
            } else {
                float target = (state != null && state.isInWater) ? (-90.0f - xRot) : -90.0f;
                float swimAmount = (state != null) ? state.swimAmount : 1.0f;
                float pitchAngle = Mth.lerp(swimAmount, 0.0f, target);
                poses.mulPose(Axis.XP.rotationDegrees(pitchAngle));
            }

            for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
                var orbit = sample.apply(i);
                float localY = (float) (orbit.y() - 0.9);
                poses.pushPose();
                poses.translate(orbit.x(), localY, orbit.z());
                poses.mulPose(Axis.YP.rotation(orbit.yaw()));
                poses.mulPose(Axis.ZP.rotation(orbit.roll()));
                poses.mulPose(Axis.XP.rotation(orbit.pitch()));
                poses.scale(-scale, -scale, scale);
                model.setJawAndWings(jawSupplier.apply(i), wingSupplier.apply(i));
                collector.submitModel(model, Float.valueOf(age + i * 13), poses,
                        GARGOYLE, light, OverlayTexture.NO_OVERLAY, 0, null);
                poses.popPose();
            }
            poses.popPose();
        } else {
            for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
                var orbit = sample.apply(i);
                poses.pushPose();
                poses.translate(orbit.x(), orbit.y(), orbit.z());
                poses.mulPose(Axis.YP.rotation(orbit.yaw()));
                poses.mulPose(Axis.ZP.rotation(orbit.roll()));
                poses.mulPose(Axis.XP.rotation(orbit.pitch()));
                poses.scale(-scale, -scale, scale);
                model.setJawAndWings(jawSupplier.apply(i), wingSupplier.apply(i));
                collector.submitModel(model, Float.valueOf(age + i * 13), poses,
                        GARGOYLE, light, OverlayTexture.NO_OVERLAY, 0, null);
                poses.popPose();
            }
        }
    }

    private static void submitPreviewEmbers(List<VoidGargoylePreview.PreviewEmber> embers, float age,
                                            double originX, double originY, double originZ, int light,
                                            Quaternionfc orientation, PoseStack poses, SubmitNodeCollector collector) {
        for (var ember : embers) {
            float elapsed = age - ember.born();
            if (elapsed < 0 || elapsed >= VoidGargoylePreview.EMBER_LIFETIME) continue;
            float life = elapsed / (float) VoidGargoylePreview.EMBER_LIFETIME;
            float seed = ember.seed() * 2.39996f;
            float alpha = Math.min(1f, elapsed / 3f) * (1 - life) * 0.90f;
            float size = ember.isSmoke() ? (0.055f * (1 + life * 0.8f)) : (0.040f * (1 - life * 0.4f));

            poses.pushPose();
            poses.translate(ember.position().x - originX + Math.sin(seed + elapsed * 0.15) * life * 0.08,
                    ember.position().y - originY + elapsed * 0.012,
                    ember.position().z - originZ + Math.cos(seed + elapsed * 0.15) * life * 0.08);
            poses.mulPose(orientation);
            poses.mulPose(Axis.ZP.rotation(seed + elapsed * 0.12f));
            poses.scale(size, size, size);

            int baseColor = ember.isSmoke() ? 0x221E26 : (life < 0.45f ? 0xFF6600 : 0xFF2200);
            int color = ((int) (alpha * 255) << 24) | (baseColor & 0x00FFFFFF);

            collector.submitCustomGeometry(poses, RenderTypes.entityTranslucent(EMBER_TEX), (pose, vertices) -> {
                vertex(vertices, pose, -1, -1, 0, 1, color, light);
                vertex(vertices, pose, 1, -1, 1, 1, color, light);
                vertex(vertices, pose, 1, 1, 1, 0, color, light);
                vertex(vertices, pose, -1, 1, 0, 0, color, light);
            });
            poses.popPose();
        }
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose,
                               float x, float y, float u, float v, int color, int light) {
        consumer.addVertex(pose, x, y, 0)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 0, 1);
    }
}
