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
 * Blockbench tabanli, eklemli 3 segmentli cift melek kanadi (ic omuz + kirilimli dis kanat + alt kanat).
 * Doku: assets/modid/textures/cosmetic/wing_seraphim_angel.png (64x64).
 */
public class SeraphimAngelWingsModel extends CosmeticModel {
    private static final float WING_SCALE = 0.56f;
    private final ModelPart rightWing, leftWing;
    private final ModelPart rightOuter, leftOuter;
    private final ModelPart rightLower, leftLower;

    public SeraphimAngelWingsModel(ModelPart root) {
        super(root);
        this.rightWing = root.getChild("rightWing");
        this.leftWing = root.getChild("leftWing");
        this.rightOuter = this.rightWing.getChild("rightOuter");
        this.leftOuter = this.leftWing.getChild("leftOuter");
        this.rightLower = this.rightWing.getChild("rightLower");
        this.leftLower = this.leftWing.getChild("leftLower");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // +X (arkadan ekranin solu): south yuzunde u=0'in icte kalmasi icin mirror(false)
        PartDefinition rightWing = root.addOrReplaceChild("rightWing",
                CubeListBuilder.create().texOffs(0, 0).mirror(false)
                        .addBox(0.0f, -11.0f, 0.0f, 14.0f, 21.0f, 0.0f),
                PartPose.offsetAndRotation(1.85f, 2.5f, 2.85f, 0.06f, -0.24f, 0.0f));
        rightWing.addOrReplaceChild("rightOuter",
                CubeListBuilder.create().texOffs(13, 0).mirror(false)
                        .addBox(0.0f, -11.0f, 0.0f, 18.0f, 24.0f, 0.0f),
                PartPose.offsetAndRotation(13.5f, 0.0f, 0.25f, 0.0f, -0.20f, 0.0f));
        rightWing.addOrReplaceChild("rightLower",
                CubeListBuilder.create().texOffs(0, 20).mirror(false)
                        .addBox(0.0f, 0.0f, 0.0f, 15.0f, 23.0f, 0.0f),
                PartPose.offsetAndRotation(0.3f, 9.6f, 0.55f, 0.0f, -0.04f, 0.0f));

        // -X (arkadan ekranin sagi): south yuzunde u=0'in icte kalmasi icin mirror(true)
        PartDefinition leftWing = root.addOrReplaceChild("leftWing",
                CubeListBuilder.create().texOffs(0, 0).mirror(true)
                        .addBox(-14.0f, -11.0f, 0.0f, 14.0f, 21.0f, 0.0f),
                PartPose.offsetAndRotation(-1.85f, 2.5f, 2.85f, 0.06f, 0.24f, 0.0f));
        leftWing.addOrReplaceChild("leftOuter",
                CubeListBuilder.create().texOffs(13, 0).mirror(true)
                        .addBox(-18.0f, -11.0f, 0.0f, 18.0f, 24.0f, 0.0f),
                PartPose.offsetAndRotation(-13.5f, 0.0f, 0.25f, 0.0f, 0.20f, 0.0f));
        leftWing.addOrReplaceChild("leftLower",
                CubeListBuilder.create().texOffs(0, 20).mirror(true)
                        .addBox(-15.0f, 0.0f, 0.0f, 15.0f, 23.0f, 0.0f),
                PartPose.offsetAndRotation(-0.3f, 9.6f, 0.55f, 0.0f, 0.04f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float idleBreath = sin(t * 0.07f) * 0.045f;
        float walkFlap = sin(s.walkPos * 0.6662f) * 0.08f * s.walkSpeed;
        float airFlap = sin(t * 0.28f) * 0.32f * s.flyAmount;
        float flap = idleBreath + walkFlap + airFlap;
        float crouchOffset = s.crouchAmount * 0.18f;

        rightWing.xScale = WING_SCALE;
        rightWing.yScale = WING_SCALE;
        rightWing.zScale = WING_SCALE;
        leftWing.xScale  = WING_SCALE;
        leftWing.yScale  = WING_SCALE;
        leftWing.zScale  = WING_SCALE;

        rightWing.yRot = -0.24f - flap;
        leftWing.yRot  =  0.24f + flap;
        rightWing.xRot =  0.06f + crouchOffset;
        leftWing.xRot  =  0.06f + crouchOffset;

        rightOuter.yRot = -0.22f - flap * 0.65f;
        leftOuter.yRot  =  0.22f + flap * 0.65f;

        float lowerFlap = sin(t * 0.07f - 0.5f) * 0.03f + walkFlap * 0.4f + airFlap * 0.45f;
        rightLower.yRot = -0.04f - lowerFlap;
        leftLower.yRot  =  0.04f + lowerFlap;
    }
}
