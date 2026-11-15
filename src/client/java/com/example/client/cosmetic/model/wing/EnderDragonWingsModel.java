package com.example.client.cosmetic.model.wing;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Genis aciklikli, 3 eklemli heybetli Ender Dragon Kanatlari.
 * Doku: assets/modid/textures/cosmetic/wing_ender_dragon.png (256x128).
 */
public class EnderDragonWingsModel extends CosmeticModel {
    private static final float WING_SCALE = 0.88f;

    private final ModelPart rightWing, leftWing;
    private final ModelPart rightElbow, leftElbow;
    private final ModelPart rightOuter, leftOuter;
    private final ModelPart rightInnerMembrane, leftInnerMembrane;
    private final ModelPart rightOuterMembrane, leftOuterMembrane;

    public EnderDragonWingsModel(ModelPart root) {
        super(root);
        this.rightWing = root.getChild("rightWing");
        this.leftWing = root.getChild("leftWing");
        this.rightElbow = this.rightWing.getChild("rightElbow");
        this.leftElbow = this.leftWing.getChild("leftElbow");
        this.rightOuter = this.rightElbow.getChild("rightOuter");
        this.leftOuter = this.leftElbow.getChild("leftOuter");
        this.rightInnerMembrane = this.rightElbow.getChild("rightInnerMembrane");
        this.leftInnerMembrane = this.leftElbow.getChild("leftInnerMembrane");
        this.rightOuterMembrane = this.rightOuter.getChild("rightOuterMembrane");
        this.leftOuterMembrane = this.leftOuter.getChild("leftOuterMembrane");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        buildWing(root, "right", 1.0f, false);
        buildWing(root, "left", -1.0f, true);

        return LayerDefinition.create(mesh, 256, 128);
    }

    private static void buildWing(PartDefinition root, String side, float sign, boolean mirror) {
        // 1. Omuz Koku (omuzdan yukari-disa acilir, kol ve kilicla cakismaz)
        PartDefinition wing = root.addOrReplaceChild(side + "Wing",
                CubeListBuilder.create().texOffs(0, 0).mirror(mirror)
                        .addBox(-1.2f, -1.2f, -1.2f, 2.4f, 2.4f, 2.4f),
                PartPose.offsetAndRotation(sign * 1.9f, 2.2f, 3.05f, 0.04f, sign * -0.32f, sign * -0.18f));

        wing.addOrReplaceChild(side + "Arm",
                CubeListBuilder.create().texOffs(16, 0).mirror(mirror)
                        .addBox(mirror ? -9.5f : 0.0f, -0.8f, -0.8f, 9.5f, 1.6f, 1.6f),
                PartPose.ZERO);

        // 2. Dirsek & On Kol (yukari uzanan kemik + bilek eklemi + boynuz + ic zar)
        PartDefinition elbow = wing.addOrReplaceChild(side + "Elbow",
                CubeListBuilder.create().texOffs(0, 0).mirror(mirror)
                        .addBox(-1.1f, -1.1f, -1.1f, 2.2f, 2.2f, 2.2f)
                        .texOffs(16, 0).mirror(mirror)
                        .addBox(-0.8f, -12.0f, -0.8f, 1.6f, 12.0f, 1.6f)
                        .texOffs(0, 0).mirror(mirror)
                        .addBox(-1.4f, -13.4f, -1.4f, 2.8f, 2.8f, 2.8f),
                PartPose.offsetAndRotation(sign * 9.2f, 0.0f, 0.0f, 0.0f, sign * -0.14f, sign * 0.22f));

        elbow.addOrReplaceChild(side + "Horn",
                CubeListBuilder.create().texOffs(32, 0).mirror(mirror)
                        .addBox(-0.55f, -3.6f, -0.55f, 1.1f, 3.6f, 1.1f),
                PartPose.offsetAndRotation(0.0f, -13.0f, 0.0f, -0.38f, 0.0f, sign * -0.20f));

        elbow.addOrReplaceChild(side + "InnerMembrane",
                CubeListBuilder.create().texOffs(0, 32).mirror(mirror)
                        .addBox(mirror ? 0.0f : -48.0f, 0.0f, 0.0f, 48.0f, 48.0f, 0.0f),
                PartPose.offset(0.0f, -11.6f, 0.05f));

        // 3. Dis Kanat Yelpazesi (22 birim ana kemik + uzun parmaklar + genis tirtikli zar)
        PartDefinition outer = elbow.addOrReplaceChild(side + "Outer",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, -11.5f, 0.0f, 0.0f, sign * -0.18f, 0.0f));

        outer.addOrReplaceChild(side + "LeadBone",
                CubeListBuilder.create().texOffs(16, 0).mirror(mirror)
                        .addBox(mirror ? -22.0f : 0.0f, -0.75f, -0.75f, 22.0f, 1.5f, 1.5f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, sign * 0.28f));

        outer.addOrReplaceChild(side + "LeadKnuckle",
                CubeListBuilder.create().texOffs(0, 0).mirror(mirror)
                        .addBox(-1.05f, -1.05f, -1.05f, 2.1f, 2.1f, 2.1f),
                PartPose.offsetAndRotation(sign * 10.5f, 3.0f, 0.0f, 0.0f, 0.0f, sign * 0.28f));

        outer.addOrReplaceChild(side + "MidFinger",
                CubeListBuilder.create().texOffs(16, 0).mirror(mirror)
                        .addBox(-0.65f, 0.0f, -0.65f, 1.3f, 18.5f, 1.3f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, sign * -0.62f));

        outer.addOrReplaceChild(side + "InnerFinger",
                CubeListBuilder.create().texOffs(16, 0).mirror(mirror)
                        .addBox(-0.65f, 0.0f, -0.65f, 1.3f, 19.0f, 1.3f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, sign * -0.15f));

        outer.addOrReplaceChild(side + "OuterMembrane",
                CubeListBuilder.create().texOffs(64, 32).mirror(mirror)
                        .addBox(mirror ? -88.0f : 0.0f, 0.0f, 0.0f, 88.0f, 76.0f, 0.0f),
                PartPose.offset(0.0f, 0.0f, 0.05f));
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float idleBreath = sin(t * 0.065f) * 0.055f;
        float walkFlap = sin(s.walkPos * 0.6662f) * 0.09f * s.walkSpeed;
        float airFlap = sin(t * 0.24f) * 0.38f * s.flyAmount;
        float flap = idleBreath + walkFlap + airFlap;
        float crouchOffset = s.crouchAmount * 0.20f;

        rightWing.xScale = WING_SCALE;
        rightWing.yScale = WING_SCALE;
        rightWing.zScale = WING_SCALE;
        leftWing.xScale = WING_SCALE;
        leftWing.yScale = WING_SCALE;
        leftWing.zScale = WING_SCALE;

        rightInnerMembrane.xScale = 0.25f;
        rightInnerMembrane.yScale = 0.25f;
        rightInnerMembrane.zScale = 0.25f;
        leftInnerMembrane.xScale = 0.25f;
        leftInnerMembrane.yScale = 0.25f;
        leftInnerMembrane.zScale = 0.25f;

        rightOuterMembrane.xScale = 0.25f;
        rightOuterMembrane.yScale = 0.25f;
        rightOuterMembrane.zScale = 0.25f;
        leftOuterMembrane.xScale = 0.25f;
        leftOuterMembrane.yScale = 0.25f;
        leftOuterMembrane.zScale = 0.25f;

        rightWing.yRot = -0.32f - flap;
        leftWing.yRot  =  0.32f + flap;
        rightWing.xRot =  0.04f + crouchOffset;
        leftWing.xRot  =  0.04f + crouchOffset;

        rightElbow.yRot = -0.14f - flap * 0.35f;
        leftElbow.yRot  =  0.14f + flap * 0.35f;

        rightOuter.yRot = -0.18f - flap * 0.65f;
        leftOuter.yRot  =  0.18f + flap * 0.65f;
    }
}
