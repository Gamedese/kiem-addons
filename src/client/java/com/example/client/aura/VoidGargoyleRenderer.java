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
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.IntFunction;

/**
 * Renderer for Obsidian Void Gargoyles with fullbright emissive eyes/gullet flicker,
 * camera-facing additive molten ribbon trails, and rotating 3D magma/obsidian cubes.
 */
public final class VoidGargoyleRenderer {
    private static final Identifier GARGOYLE = Identifier.fromNamespaceAndPath("modid", "textures/aura/void_gargoyle.png");
    private static final Identifier GARGOYLE_EMISSIVE = Identifier.fromNamespaceAndPath("modid", "textures/aura/void_gargoyle_emissive.png");
    private static final Identifier MOLTEN_RIBBON = Identifier.fromNamespaceAndPath("modid", "textures/aura/molten_ribbon.png");
    private static final Identifier MAGMA_TEX = Identifier.fromNamespaceAndPath("modid", "textures/aura/magma_cube_ember.png");
    private static final Identifier OBSIDIAN_TEX = Identifier.fromNamespaceAndPath("modid", "textures/aura/obsidian_shard.png");
    private static final int FULL_BRIGHT = 0x00F000F0;

    private static VoidGargoyleModel model;
    private static VoidGargoyleModel emissiveModel;

    private VoidGargoyleRenderer() { }

    public static void submit(AvatarRenderState state, PoseStack poses, SubmitNodeCollector collector,
                              CameraRenderState camera) {
        Minecraft client = Minecraft.getInstance();
        boolean enabled = "VOID_GARGOYLE".equals(KiemConfig.activeAura) || KiemConfig.voidGargoyleAuraEnabled;
        if (!enabled || client.player == null || client.level == null
                || state.id != client.player.getId() || state.isInvisible || state.isSpectator
                || client.player.isSleeping() || client.options.getCameraType().isFirstPerson()
                || !SakuraAuraRenderer.isWorldCamera(client, camera)) return;

        float time = VoidGargoyleManager.orbitTime(state.ageInTicks);
        float partialTick = state.ageInTicks - (float) Math.floor(state.ageInTicks);
        boolean sprinting = client.player.isSprinting() || client.player.getDeltaMovement().horizontalDistanceSqr() > 0.015;
        boolean gliding = state.isFallFlying || client.player.isFallFlying();

        submitGargoyles(state, state.ageInTicks, KiemConfig.voidGargoyleScale, state.lightCoords, poses, collector,
                i -> VoidGargoyleManager.sample(time, i),
                i -> VoidGargoyleManager.getJawAngle(i, partialTick),
                i -> VoidGargoyleManager.getWingPhase(i, partialTick),
                i -> VoidGargoyleManager.getWingAmp(i),
                i -> VoidGargoyleManager.getRecoil(i),
                i -> VoidGargoyleManager.getLookYaw(i),
                i -> VoidGargoyleManager.getLookPitch(i),
                sprinting, gliding);

        Vec3 playerPos = client.player.position();
        if (KiemConfig.voidGargoyleEmbers > 0) {
            float ribbonWidth = 0.085f * KiemConfig.voidGargoyleScale;
            for (int i = 0; i < VoidGargoyleOrbit.COUNT; i++) {
                submitRibbonTrail(VoidGargoyleManager.getTrail(i), playerPos, camera.orientation, ribbonWidth, poses, collector);
            }
        }

        if (KiemConfig.voidGargoyleEmbers > 0 || KiemConfig.voidGargoyleGroundSmoke > 0) {
            submitWorldCubes(VoidGargoyleManager.getCubes(), playerPos, state.ageInTicks, state.lightCoords, poses, collector);
        }
    }

    /** Explicit GUI-only entry for Void Gargoyle customization preview. */
    public static void submitPreview(AvatarRenderState state, VoidGargoylePreview.Frame frame,
                                     PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        submitGargoyles(state, frame.age(), frame.settings().scale(), state.lightCoords, poses, collector,
                i -> frame.settings().sample(frame.time(), i),
                i -> frame.jawAngles()[i],
                i -> frame.wingPhases()[i],
                i -> 0.34f,
                i -> frame.recoils()[i],
                i -> (float) (Math.sin(frame.age() * 0.045 + i * 2.3) * 0.25),
                i -> (float) (Math.cos(frame.age() * 0.055 + i * 1.7) * 0.12),
                false, false);

        if (frame.settings().embers() > 0) {
            float ribbonWidth = 0.085f * frame.settings().scale();
            for (int i = 0; i < frame.trails().size(); i++) {
                submitRibbonTrail(frame.trails().get(i), Vec3.ZERO, camera.orientation, ribbonWidth, poses, collector);
            }
        }

        if (frame.settings().embers() != 0 || frame.settings().groundSmoke() != 0) {
            submitPreviewCubes(frame.embers(), frame.age(), state.lightCoords, poses, collector);
        }
    }

    private static void submitGargoyles(AvatarRenderState state, float age, float scale, int light, PoseStack poses,
                                        SubmitNodeCollector collector,
                                        IntFunction<VoidGargoyleOrbit.Sample> sample,
                                        IntFunction<Float> jawSupplier,
                                        IntFunction<Float> wingPhaseSupplier,
                                        IntFunction<Float> wingAmpSupplier,
                                        IntFunction<Float> recoilSupplier,
                                        IntFunction<Float> lookYawSupplier,
                                        IntFunction<Float> lookPitchSupplier,
                                        boolean sprinting, boolean gliding) {
        if (model == null) model = new VoidGargoyleModel(false);
        if (emissiveModel == null) emissiveModel = new VoidGargoyleModel(true);
        Minecraft client = Minecraft.getInstance();

        float effectiveScale = scale * 0.62f;

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
            poses.translate(0, 0.9, 0);
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
                poses.scale(-effectiveScale, -effectiveScale, effectiveScale);
                submitModelPair(age, i, jawSupplier.apply(i), wingPhaseSupplier.apply(i),
                        wingAmpSupplier.apply(i), recoilSupplier.apply(i), orbit.direction(),
                        lookYawSupplier.apply(i), lookPitchSupplier.apply(i), sprinting, gliding,
                        light, poses, collector);
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
                poses.scale(-effectiveScale, -effectiveScale, effectiveScale);
                submitModelPair(age, i, jawSupplier.apply(i), wingPhaseSupplier.apply(i),
                        wingAmpSupplier.apply(i), recoilSupplier.apply(i), orbit.direction(),
                        lookYawSupplier.apply(i), lookPitchSupplier.apply(i), sprinting, gliding,
                        light, poses, collector);
                poses.popPose();
            }
        }
    }

    private static void submitModelPair(float age, int index, float jawAngle, float wingPhase, float wingAmp,
                                        float recoil, int direction, float lookYaw, float lookPitch,
                                        boolean sprinting, boolean gliding,
                                        int light, PoseStack poses, SubmitNodeCollector collector) {
        Float modelAge = Float.valueOf(age + index * 13);
        model.setAnimationParams(jawAngle, wingPhase, wingAmp, recoil, direction, lookYaw, lookPitch, sprinting, gliding);
        collector.submitModel(model, modelAge, poses, GARGOYLE, light, OverlayTexture.NO_OVERLAY, 0, null);

        // Fullbright additive emissive layer for red eyes, deep molten gullet, and magma cracks with subtle fire flicker
        float flicker = 0.78f + 0.22f * Mth.sin(age * 0.4f + index * 2.1f);
        int c = (int) (flicker * 255);
        int emissiveColor = (0xFF << 24) | (c << 16) | (c << 8) | c;
        emissiveModel.setAnimationParams(jawAngle, wingPhase, wingAmp, recoil, direction, lookYaw, lookPitch, sprinting, gliding);
        collector.submitModel(emissiveModel, modelAge, poses, GARGOYLE_EMISSIVE, FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, emissiveColor, null);
    }

    private static void submitRibbonTrail(List<Vec3> points, Vec3 origin, Quaternionfc cameraOrientation,
                                          float maxHalfWidth, PoseStack poses, SubmitNodeCollector collector) {
        if (points == null || points.size() < 2) return;

        Vector3f camFwd = new Vector3f(0, 0, -1).rotate(cameraOrientation).normalize();
        int count = points.size();

        collector.submitCustomGeometry(poses, RenderTypes.eyes(MOLTEN_RIBBON), (pose, vertices) -> {
            Vector3f prevL = null;
            Vector3f prevR = null;
            int prevColor = 0;

            for (int i = 0; i < count; i++) {
                Vec3 p = points.get(i).subtract(origin);
                Vec3 next = i < count - 1 ? points.get(i + 1).subtract(origin) : p;
                Vec3 prev = i > 0 ? points.get(i - 1).subtract(origin) : p;
                Vector3f tangent = new Vector3f(
                        (float) (next.x - prev.x),
                        (float) (next.y - prev.y),
                        (float) (next.z - prev.z));
                if (tangent.lengthSquared() < 1e-6f) {
                    tangent.set(0, 0, 1);
                } else {
                    tangent.normalize();
                }

                Vector3f side = new Vector3f(tangent).cross(camFwd);
                if (side.lengthSquared() < 1e-6f) {
                    side.set(1, 0, 0);
                } else {
                    side.normalize();
                }

                float t = (float) i / (float) (count - 1);
                float halfW = maxHalfWidth * (1.0f - t * 0.85f);
                Vector3f curL = new Vector3f((float) p.x - side.x * halfW, (float) p.y - side.y * halfW, (float) p.z - side.z * halfW);
                Vector3f curR = new Vector3f((float) p.x + side.x * halfW, (float) p.y + side.y * halfW, (float) p.z + side.z * halfW);

                int r = (int) Mth.lerp(t, 255, 180);
                int g = (int) Mth.lerp(t, 210, 45);
                int b = (int) Mth.lerp(t, 120, 0);
                int a = (int) ((1.0f - t) * (1.0f - t) * 230);
                int curColor = (a << 24) | (r << 16) | (g << 8) | b;

                if (prevL != null) {
                    float u0 = (float) (i - 1) / (float) (count - 1);
                    float u1 = t;
                    emitRibbonVertex(vertices, pose, prevL, u0, 0f, prevColor);
                    emitRibbonVertex(vertices, pose, prevR, u0, 1f, prevColor);
                    emitRibbonVertex(vertices, pose, curR, u1, 1f, curColor);
                    emitRibbonVertex(vertices, pose, curL, u1, 0f, curColor);
                }
                prevL = curL;
                prevR = curR;
                prevColor = curColor;
            }
        });
    }

    private static void submitWorldCubes(List<VoidGargoyleManager.WorldCube> cubes, Vec3 playerPos,
                                         float age, int worldLight, PoseStack poses, SubmitNodeCollector collector) {
        for (var cube : cubes) {
            float elapsed = age - cube.bornAge;
            if (elapsed < 0 || elapsed >= cube.lifetime) continue;
            float life = elapsed / (float) cube.lifetime;
            float size = cube.size * (1.0f - life * 0.45f);
            float seed = cube.seed * 0.37f;

            Vec3 rel = cube.pos.subtract(playerPos);
            submitRotatingCube(rel, size, seed + elapsed * cube.rotSpeed, seed * 1.3f + elapsed * cube.rotSpeed * 0.7f,
                    cube.isObsidian, worldLight, poses, collector);
        }
    }

    private static void submitPreviewCubes(List<VoidGargoylePreview.PreviewEmber> embers, float age,
                                           int worldLight, PoseStack poses, SubmitNodeCollector collector) {
        for (var ember : embers) {
            float elapsed = age - ember.born();
            if (elapsed < 0 || elapsed >= VoidGargoylePreview.EMBER_LIFETIME) continue;
            float life = elapsed / (float) VoidGargoylePreview.EMBER_LIFETIME;
            float seed = ember.seed() * 0.43f;
            float baseSize = ember.isObsidian() ? 0.022f : 0.032f;
            float size = baseSize * (1.0f - life * 0.45f);

            Vec3 rel = new Vec3(
                    ember.position().x + Math.sin(seed + elapsed * 0.15) * life * 0.08,
                    ember.position().y + elapsed * 0.010,
                    ember.position().z + Math.cos(seed + elapsed * 0.15) * life * 0.08);
            submitRotatingCube(rel, size, seed + elapsed * 0.18f, seed * 1.4f + elapsed * 0.14f,
                    ember.isObsidian(), worldLight, poses, collector);
        }
    }

    private static void submitRotatingCube(Vec3 relPos, float halfSize, float rotX, float rotY,
                                           boolean isObsidian, int worldLight, PoseStack poses, SubmitNodeCollector collector) {
        poses.pushPose();
        poses.translate(relPos.x, relPos.y, relPos.z);
        poses.mulPose(Axis.YP.rotation(rotY));
        poses.mulPose(Axis.XP.rotation(rotX));
        poses.scale(halfSize, halfSize, halfSize);

        Identifier tex = isObsidian ? OBSIDIAN_TEX : MAGMA_TEX;
        int cubeLight = isObsidian ? worldLight : FULL_BRIGHT;

        collector.submitCustomGeometry(poses, RenderTypes.entityCutout(tex), (pose, vertices) -> {
            // Front (+Z)
            cubeQuad(vertices, pose, -1, -1, 1, 1, -1, 1, 1, 1, 1, -1, 1, 1, 0, 0, 1, cubeLight);
            // Back (-Z)
            cubeQuad(vertices, pose, 1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 0, 0, -1, cubeLight);
            // Left (-X)
            cubeQuad(vertices, pose, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, 1, -1, -1, 0, 0, cubeLight);
            // Right (+X)
            cubeQuad(vertices, pose, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, 1, 1, 0, 0, cubeLight);
            // Top (+Y)
            cubeQuad(vertices, pose, -1, 1, 1, 1, 1, 1, 1, 1, -1, -1, 1, -1, 0, 1, 0, cubeLight);
            // Bottom (-Y)
            cubeQuad(vertices, pose, -1, -1, -1, 1, -1, -1, 1, -1, 1, -1, -1, 1, 0, -1, 0, cubeLight);
        });
        poses.popPose();
    }

    private static void emitRibbonVertex(VertexConsumer consumer, PoseStack.Pose pose, Vector3f p,
                                         float u, float v, int color) {
        consumer.addVertex(pose, p.x, p.y, p.z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(pose, 0, 1, 0);
    }

    private static void cubeQuad(VertexConsumer consumer, PoseStack.Pose pose,
                                 float x0, float y0, float z0,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float nx, float ny, float nz, int light) {
        cubeVertex(consumer, pose, x0, y0, z0, 0f, 1f, nx, ny, nz, light);
        cubeVertex(consumer, pose, x1, y1, z1, 1f, 1f, nx, ny, nz, light);
        cubeVertex(consumer, pose, x2, y2, z2, 1f, 0f, nx, ny, nz, light);
        cubeVertex(consumer, pose, x3, y3, z3, 0f, 0f, nx, ny, nz, light);
    }

    private static void cubeVertex(VertexConsumer consumer, PoseStack.Pose pose,
                                   float x, float y, float z, float u, float v,
                                   float nx, float ny, float nz, int light) {
        consumer.addVertex(pose, x, y, z)
                .setColor(0xFFFFFFFF)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
