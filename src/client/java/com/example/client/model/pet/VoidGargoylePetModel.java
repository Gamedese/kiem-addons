package com.example.client.model.pet;

import com.example.client.model.aura.VoidGargoyleModel;
import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

/**
 * Shoulder-pet wrapper around the 128x128 Blockbench Void Gargoyle geometry.
 * Perches on the player's shoulder with breathing wings, curious head turns,
 * sleeping tuck when idle, and a jaw-snapping roar on player attack swings.
 */
public final class VoidGargoylePetModel extends Model<PetRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart lowerJaw;
    private final ModelPart leftWing;
    private final ModelPart leftWingOuter;
    private final ModelPart rightWing;
    private final ModelPart rightWingOuter;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart foreLegL;
    private final ModelPart foreLegR;
    private final ModelPart hindLegL;
    private final ModelPart hindLegR;

    public VoidGargoylePetModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.body = root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.lowerJaw = this.head.getChild("lower_jaw");
        this.leftWing = this.body.getChild("wing_l");
        this.leftWingOuter = this.leftWing.getChild("wing_l_outer");
        this.rightWing = this.body.getChild("wing_r");
        this.rightWingOuter = this.rightWing.getChild("wing_r_outer");
        this.tail1 = this.body.getChild("tail_1");
        this.tail2 = this.tail1.getChild("tail_2");
        this.tail3 = this.tail2.getChild("tail_3");
        this.foreLegL = this.body.getChild("fore_leg_l_r1");
        this.foreLegR = this.body.getChild("fore_leg_r_r1");
        this.hindLegL = this.body.getChild("hind_leg_l_r1");
        this.hindLegR = this.body.getChild("hind_leg_r_r1");
    }

    public static net.minecraft.client.model.geom.builders.LayerDefinition createLayer() {
        return VoidGargoyleModel.createLayer();
    }

    @Override
    public void setupAnim(PetRenderState state) {
        resetPose();
        float t = state.age;
        float sleep = state.sleepProgress;
        float awake = 1.0f - sleep;

        // Perch posture: legs firmly planted on shoulder unless airborne
        if (!state.onGround) {
            float airTuck = 0.25f;
            foreLegL.xRot += airTuck;
            foreLegR.xRot += airTuck;
            hindLegL.xRot += airTuck;
            hindLegR.xRot += airTuck;
        }

        // Breathing + airborne wing flutter
        float wingFreq = state.onGround ? 0.10f : 0.42f;
        float wingAmp = state.onGround ? 0.08f : 0.34f;
        float wingBeat = ((float) Math.sin(t * wingFreq) * wingAmp) * awake;
        float foldForPerch = state.onGround ? 0.18f : 0.0f;

        leftWing.zRot = 0.4538F - wingBeat + foldForPerch + sleep * 0.28f;
        rightWing.zRot = -0.4538F + wingBeat - foldForPerch - sleep * 0.28f;
        leftWing.yRot = -0.2793F + 0.32f + sleep * 0.25f;
        rightWing.yRot = 0.2793F - 0.32f - sleep * 0.25f;
        leftWingOuter.zRot = -0.2094F - wingBeat * 0.45f - 0.25f;
        rightWingOuter.zRot = 0.2094F + wingBeat * 0.45f + 0.25f;

        // Roar / jaw snap on player attack swing or periodic idle snarl
        float swingSnarl = state.attackSwing > 0.05f ? Mth.sin(state.attackSwing * Mth.PI) * 0.72f : 0.0f;
        float periodic = (float) Math.sin(t * 0.06f);
        float idleSnarl = periodic > 0.88f ? (periodic - 0.88f) * 4.5f : 0.0f;
        float jawAngle = Math.max(swingSnarl, idleSnarl) * awake;
        lowerJaw.xRot = jawAngle;

        // Subtle neck & head curiosity + roar recoil
        float lookYaw = (float) (Math.sin(t * 0.04f) * 0.22f) * awake;
        float lookPitch = (float) (Math.cos(t * 0.055f) * 0.10f) * awake;
        neck.xRot += (swingSnarl * 0.22f) + sleep * 0.35f;
        head.xRot += lookPitch - (swingSnarl * 0.12f) + sleep * 0.25f;
        head.yRot += lookYaw + (state.leftShoulder ? -0.10f : 0.10f);
        head.zRot += lookYaw * 0.20f;

        // Gentle hanging S-curve tail swish
        tail1.xRot = 0.15f + sleep * 0.15f;
        tail2.xRot = 0.12f;
        tail3.xRot = 0.08f;
        tail1.yRot += (float) Math.sin(t * 0.11f) * 0.14f * awake;
        tail2.yRot += (float) Math.sin(t * 0.11f - 0.55f) * 0.18f * awake;
        tail3.yRot += (float) Math.sin(t * 0.11f - 1.10f) * 0.22f * awake;
    }
}
