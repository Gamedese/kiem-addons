package com.example.client.pet;

import com.example.client.KiemCompat;
import com.example.client.KiemConfig;
import com.example.client.model.pet.HamsterHdPetModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

/**
 * Yerde gezinen client-side petleri dunya koordinatinda cizer.
 */
public final class GroundPetRenderer {
    private static final Identifier TEX_HAMSTER_HD = Identifier.fromNamespaceAndPath("modid", "textures/pet/hamster_hd.png");
    private static HamsterHdPetModel hamsterHdModel;
    private static final PetRenderState STATE = new PetRenderState();

    private GroundPetRenderer() { }

    public static void submit(AvatarRenderState avatar, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        ShoulderPetType type = ShoulderPetType.fromName(KiemConfig.shoulderPetType);
        if (!GroundPetManager.isUsingWorldRenderer(type)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || avatar.id != mc.player.getId()
                || avatar.isInvisible || avatar.isSpectator || camera == null || !camera.initialized) {
            return;
        }

        if (hamsterHdModel == null) {
            hamsterHdModel = new HamsterHdPetModel(HamsterHdPetModel.createLayer().bakeRoot());
        }

        float pt = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);

        double dx = GroundPetManager.getRenderX(pt) - avatar.x;
        double dy = GroundPetManager.getRenderY(pt) - avatar.y;
        double dz = GroundPetManager.getRenderZ(pt) - avatar.z;
        float petYaw = GroundPetManager.getRenderYaw(pt);

        STATE.age = GroundPetManager.getAge(pt);
        STATE.walkPos = GroundPetManager.getRenderWalkPos(pt);
        STATE.walkSpeed = GroundPetManager.getRenderWalkSpeed(pt);
        STATE.onGround = GroundPetManager.getState() == GroundPetManager.State.ROAMING;
        STATE.yVel = 0f;
        STATE.landSquash = 0f;
        STATE.crouching = false;
        STATE.crouchAmount = 0f;
        STATE.eatAmount = 0f;
        STATE.isEating = false;
        STATE.isSleeping = false;
        STATE.sleepProgress = 0f;
        STATE.flyAmount = 0f;
        STATE.dryShake = KiemConfig.petDryShake ? GroundPetManager.getRenderDryShake(pt) : 0f;
        STATE.swimAmount = GroundPetManager.getRenderSwimAmount(pt);
        STATE.groundRoam = true;
        STATE.hurtTime = 0f;
        STATE.attackSwing = 0f;
        STATE.leftShoulder = true;

        float s = 0.36f * KiemConfig.petScale;

        poses.pushPose();
        poses.translate(dx, dy, dz);
        KiemCompat.rotate(poses, Axis.YP.rotationDegrees(-petYaw));
        poses.scale(s, -s, -s);

        KiemCompat.submitModel(
                collector,
                hamsterHdModel,
                STATE,
                poses,
                RenderTypes.entityCutout(TEX_HAMSTER_HD),
                avatar.lightCoords,
                OverlayTexture.NO_OVERLAY,
                avatar.outlineColor
        );
        poses.popPose();
    }
}
