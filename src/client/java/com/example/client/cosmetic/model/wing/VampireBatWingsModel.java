package com.example.client.cosmetic.model.wing;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Vampire Bat Wings (256x256 HD, 0.25x base scale with 1.42x span / 1.40x height):
 * Large, imposing sharp-jointed black & crimson-lined bat wings with distinct
 * Z-separated bones and dark leather membranes (zero Z-fighting).
 */
public class VampireBatWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    private static final float WING_SPAN_SCALE = 1.42f;
    private static final float WING_HEIGHT_SCALE = 1.40f;
    private static final float WING_DEPTH_SCALE = 1.15f;
    private static final CubeDeformation MEMBRANE_THIN = new CubeDeformation(0.0f, 0.0f, -0.25f);

    private final ModelPart root;
    private final ModelPart leftInner;
    private final ModelPart leftOuter;
    private final ModelPart rightInner;
    private final ModelPart rightOuter;

    public VampireBatWingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftInner = root.getChild("left_inner");
        this.leftOuter = this.leftInner.getChild("left_outer");
        this.rightInner = root.getChild("right_inner");
        this.rightOuter = this.rightInner.getChild("right_outer");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("back_crest",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0f, 1.0f, 7.5f, 10.0f, 10.0f, 3.0f),
                PartPose.ZERO);

        // --- LEFT BAT WING (+X) ---
        PartDefinition leftInner = root.addOrReplaceChild("left_inner",
                CubeListBuilder.create()
                        .texOffs(30, 0).addBox(-1.0f, -1.5f, -1.5f, 16.0f, 3.0f, 2.0f)
                        .texOffs(0, 20).addBox(0.0f, 0.5f, 1.0f, 15.0f, 18.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(4.6f, 2.4f, 9.2f, 0.05f, -0.25f, -0.45f));

        PartDefinition leftOuter = leftInner.addOrReplaceChild("left_outer",
                CubeListBuilder.create()
                        .texOffs(70, 0).addBox(-1.0f, -2.0f, -1.5f, 22.0f, 3.0f, 2.0f)
                        .texOffs(120, 0).addBox(-0.5f, -6.0f, -2.3f, 2.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(14.5f, -0.5f, 0.5f, 0.0f, -0.15f, 0.65f));

        leftOuter.addOrReplaceChild("left_finger_1",
                CubeListBuilder.create()
                        .texOffs(132, 0).addBox(0.0f, 0.0f, -1.1f, 3.0f, 24.0f, 2.0f)
                        .texOffs(36, 20).addBox(-1.0f, 0.5f, 1.5f, 14.0f, 22.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(2.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.15f));

        leftOuter.addOrReplaceChild("left_finger_2",
                CubeListBuilder.create()
                        .texOffs(144, 0).addBox(0.0f, 0.0f, -0.8f, 3.0f, 28.0f, 2.0f)
                        .texOffs(70, 20).addBox(-1.0f, 0.5f, 2.8f, 16.0f, 26.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(10.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.52f));

        leftOuter.addOrReplaceChild("left_finger_3",
                CubeListBuilder.create()
                        .texOffs(156, 0).addBox(0.0f, 0.0f, -1.2f, 3.0f, 22.0f, 2.0f)
                        .texOffs(108, 20).addBox(-1.0f, 0.5f, 4.1f, 14.0f, 20.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(19.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.92f));

        // --- RIGHT BAT WING (-X, mirrored) ---
        PartDefinition rightInner = root.addOrReplaceChild("right_inner",
                CubeListBuilder.create().mirror()
                        .texOffs(30, 0).addBox(-15.0f, -1.5f, -1.5f, 16.0f, 3.0f, 2.0f)
                        .texOffs(0, 20).addBox(-15.0f, 0.5f, 1.0f, 15.0f, 18.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-4.6f, 2.4f, 9.2f, 0.05f, 0.25f, 0.45f));

        PartDefinition rightOuter = rightInner.addOrReplaceChild("right_outer",
                CubeListBuilder.create().mirror()
                        .texOffs(70, 0).addBox(-21.0f, -2.0f, -1.5f, 22.0f, 3.0f, 2.0f)
                        .texOffs(120, 0).addBox(-1.5f, -6.0f, -2.3f, 2.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(-14.5f, -0.5f, 0.5f, 0.0f, 0.15f, -0.65f));

        rightOuter.addOrReplaceChild("right_finger_1",
                CubeListBuilder.create().mirror()
                        .texOffs(132, 0).addBox(-3.0f, 0.0f, -1.1f, 3.0f, 24.0f, 2.0f)
                        .texOffs(36, 20).addBox(-13.0f, 0.5f, 1.5f, 14.0f, 22.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.15f));

        rightOuter.addOrReplaceChild("right_finger_2",
                CubeListBuilder.create().mirror()
                        .texOffs(144, 0).addBox(-3.0f, 0.0f, -0.8f, 3.0f, 28.0f, 2.0f)
                        .texOffs(70, 20).addBox(-15.0f, 0.5f, 2.8f, 16.0f, 26.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-10.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.52f));

        rightOuter.addOrReplaceChild("right_finger_3",
                CubeListBuilder.create().mirror()
                        .texOffs(156, 0).addBox(-3.0f, 0.0f, -1.2f, 3.0f, 22.0f, 2.0f)
                        .texOffs(108, 20).addBox(-13.0f, 0.5f, 4.1f, 14.0f, 20.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-19.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.92f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float energy = Math.min(1.0f, Math.max(s.flyAmount, s.walkSpeed * 0.9f));
        float flap = sin(t * 0.11f) * 0.05f + sin(t * 0.38f) * (0.28f * energy);
        float outerFlap = sin(t * 0.11f - 0.45f) * 0.04f + sin(t * 0.38f - 0.65f) * (0.24f * energy);
        float sweepY = 0.25f + 0.32f * s.flyAmount + 0.22f * s.walkSpeed - s.crouchAmount * 0.12f;
        float pitchX = 0.05f + 0.18f * s.flyAmount + s.crouchAmount * 0.18f;

        leftInner.xScale = WING_SPAN_SCALE;
        leftInner.yScale = WING_HEIGHT_SCALE;
        leftInner.zScale = WING_DEPTH_SCALE;
        leftInner.xRot = pitchX;
        leftInner.yRot = -sweepY;
        leftInner.zRot = -0.45f + flap + s.crouchAmount * 0.15f;
        leftOuter.yRot = -0.15f - 0.14f * energy;
        leftOuter.zRot = 0.65f + outerFlap;

        rightInner.xScale = WING_SPAN_SCALE;
        rightInner.yScale = WING_HEIGHT_SCALE;
        rightInner.zScale = WING_DEPTH_SCALE;
        rightInner.xRot = pitchX;
        rightInner.yRot = sweepY;
        rightInner.zRot = 0.45f - flap - s.crouchAmount * 0.15f;
        rightOuter.yRot = 0.15f + 0.14f * energy;
        rightOuter.zRot = -0.65f - outerFlap;
    }
}
