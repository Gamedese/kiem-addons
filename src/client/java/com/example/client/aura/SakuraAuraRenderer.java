package com.example.client.aura;

import com.example.client.KiemConfig;
import com.example.client.model.aura.SakuraBloomModel;
import com.example.client.model.aura.SakuraLopModel;
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

/** Submitted before the avatar's body rotation/scale; dynamically conforms to flight/swimming angles. */
public final class SakuraAuraRenderer {
    private static final Identifier BUNNY = Identifier.fromNamespaceAndPath("modid", "textures/aura/sakura_lop.png");
    private static final Identifier PETAL = Identifier.fromNamespaceAndPath("modid", "textures/aura/sakura_petal.png");
    private static final Identifier BLOOM = Identifier.fromNamespaceAndPath("modid", "textures/aura/sakura_bloom.png");
    private static SakuraLopModel model;
    private static SakuraBloomModel bloomModel;

    private SakuraAuraRenderer() { }

    public static void submit(AvatarRenderState state, PoseStack poses, SubmitNodeCollector collector,
                              CameraRenderState camera) {
        Minecraft client = Minecraft.getInstance();
        if (!KiemConfig.sakuraAuraEnabled || client.player == null || client.level == null
                || state.id != client.player.getId() || state.isInvisible || state.isSpectator
                || client.player.isSleeping() || client.options.getCameraType().isFirstPerson()
                || camera == null || !camera.initialized) return;
        // Inventory/GUI entity previews use a synthetic camera; never draw a world trail there.
        if (camera.pos == null || camera.pos.distanceToSqr(client.gameRenderer.mainCamera().position()) > 0.01) return;
        if (KiemConfig.sakuraAuraPetals != 0) {
            submitPetals(SakuraAuraManager.petals(), state.ageInTicks, state.x, state.y, state.z,
                    state.lightCoords, camera.orientation, poses, collector);
        }
        if (KiemConfig.sakuraAuraGroundBloom != 0) {
            submitGroundFlowers(SakuraAuraManager.groundFlowers(), state.ageInTicks, state.x, state.y, state.z,
                    state.lightCoords, poses, collector);
        }
    }

    /** Explicit GUI-only entry; payload and geometry stay inside the native avatar PiP pass. */
    public static void submitPreview(AvatarRenderState state, SakuraAuraPreview.Frame frame,
                                     PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (frame.settings().density() != 0) {
            submitPetals(frame.petals(), frame.age(), 0, 0, 0, state.lightCoords,
                    camera.orientation, poses, collector);
        }
        if (frame.settings().groundBloom() != 0) {
            submitGroundFlowers(frame.groundBloom(), frame.age(), 0, 0, 0, state.lightCoords, poses, collector);
        }
    }

    private static void submitHeads(AvatarRenderState state, float age, float scale, int light, PoseStack poses,
                                    SubmitNodeCollector collector, IntFunction<SakuraOrbit.Sample> sample) {
        if (model == null) model = new SakuraLopModel();
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

            for (int i = 0; i < SakuraOrbit.COUNT; i++) {
                var orbit = sample.apply(i);
                float localY = (float) (orbit.y() - 0.9);
                poses.pushPose();
                poses.translate(orbit.x(), localY, orbit.z());
                poses.mulPose(Axis.YP.rotation(orbit.yaw()));
                poses.mulPose(Axis.ZP.rotation((float) Math.sin(age * 0.12 + i) * 0.10f));
                poses.scale(-scale, -scale, scale);
                collector.submitModel(model, Float.valueOf(age + i * 13), poses,
                        BUNNY, light, OverlayTexture.NO_OVERLAY, 0, null);
                poses.popPose();
            }
            poses.popPose();
        } else {
            for (int i = 0; i < SakuraOrbit.COUNT; i++) {
                var orbit = sample.apply(i);
                poses.pushPose();
                poses.translate(orbit.x(), orbit.y(), orbit.z());
                poses.mulPose(Axis.YP.rotation(orbit.yaw()));
                poses.mulPose(Axis.ZP.rotation((float) Math.sin(age * 0.12 + i) * 0.10f));
                poses.scale(-scale, -scale, scale);
                collector.submitModel(model, Float.valueOf(age + i * 13), poses,
                        BUNNY, light, OverlayTexture.NO_OVERLAY, 0, null);
                poses.popPose();
            }
        }
    }

    private static void submitGroundFlowers(List<SakuraAuraManager.GroundFlower> flowers, float age,
                                            double originX, double originY, double originZ, int light,
                                            PoseStack poses, SubmitNodeCollector collector) {
        if (flowers.isEmpty()) return;
        if (bloomModel == null) bloomModel = new SakuraBloomModel();

        for (var flower : flowers) {
            float elapsed = age - flower.born();
            if (elapsed < 0 || elapsed >= SakuraAuraManager.GROUND_FLOWER_LIFETIME) continue;

            float scaleMultiplier;
            float yOffset;

            // Phase 1: Blooming up & opening (ticks 0..6, ~0.3s)
            if (elapsed < SakuraAuraManager.GROUND_FLOWER_BLOOM_TICKS) {
                float t = elapsed / (float) SakuraAuraManager.GROUND_FLOWER_BLOOM_TICKS;
                scaleMultiplier = (float) Math.sin(t * Math.PI * 0.5);
                yOffset = -(1.0f - scaleMultiplier) * 0.04f;
            }
            // Phase 2: Staying on ground in full bloom (~1.5s, 30 ticks)
            else if (elapsed < SakuraAuraManager.GROUND_FLOWER_BLOOM_TICKS + SakuraAuraManager.GROUND_FLOWER_STAY_TICKS) {
                scaleMultiplier = 1.0f;
                yOffset = 0.0f;
            }
            // Phase 3: Sinking smoothly into the earth & petals softly folding (ticks 36..48)
            else {
                float sinkT = (elapsed - (SakuraAuraManager.GROUND_FLOWER_BLOOM_TICKS + SakuraAuraManager.GROUND_FLOWER_STAY_TICKS))
                        / (float) SakuraAuraManager.GROUND_FLOWER_SINK_TICKS;
                sinkT = Math.clamp(sinkT, 0.0f, 1.0f);
                // Quadratic smooth plunge down below surface
                yOffset = -(sinkT * sinkT * 0.18f);
                scaleMultiplier = 1.0f - sinkT * 0.35f;
            }

            float finalScale = flower.scale() * scaleMultiplier;
            if (finalScale <= 0.001f) continue;

            poses.pushPose();
            // Translate relative to avatar position origin:
            poses.translate(flower.position().x - originX,
                    flower.position().y - originY + yOffset,
                    flower.position().z - originZ);
            poses.mulPose(Axis.YP.rotation(flower.yaw()));
            // Vanilla model coordinates: +Y down. The enclosing pose here is world-up.
            poses.scale(-finalScale, -finalScale, finalScale);
            collector.submitModel(bloomModel, Float.valueOf(elapsed), poses,
                    BLOOM, light, OverlayTexture.NO_OVERLAY, 0, null);
            poses.popPose();
        }
    }

    private static void submitPetals(List<SakuraAuraManager.Petal> petals, float age,
                                     double originX, double originY, double originZ, int light,
                                     Quaternionfc orientation, PoseStack poses, SubmitNodeCollector collector) {
        for (var petal : petals) {
            float elapsed = age - petal.born();
            if (elapsed < 0 || elapsed >= SakuraAuraManager.PETAL_LIFETIME) continue;
            float life = elapsed / SakuraAuraManager.PETAL_LIFETIME;
            float seed = petal.seed() * 2.39996f;
            float alpha = Math.min(1f, elapsed / 2f) * (1 - life) * 0.85f;
            float size = 0.045f * (1 - life * 0.45f);
            poses.pushPose();
            poses.translate(petal.position().x - originX + Math.sin(seed + elapsed * 0.18) * life * 0.12,
                    petal.position().y - originY - elapsed * 0.009,
                    petal.position().z - originZ + Math.cos(seed + elapsed * 0.13) * life * 0.12);
            poses.mulPose(orientation);
            poses.mulPose(Axis.ZP.rotation(seed + elapsed * 0.10f));
            poses.mulPose(Axis.YP.rotation((float) Math.sin(seed + elapsed * 0.16) * 0.8f));
            poses.scale(size, size, size);
            int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
            collector.submitCustomGeometry(poses, RenderTypes.entityTranslucent(PETAL), (pose, vertices) -> {
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
