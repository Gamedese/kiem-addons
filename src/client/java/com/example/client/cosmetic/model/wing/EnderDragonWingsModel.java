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
 * Ender Dragon Wings (256x256 HD, 0.25x scale):
 * Imposing Minecraft Ender Dragon silhouette with thick charcoal-black bone
 * segments, sculpted shoulder/wrist joints, wrist claw spur, and wide taut
 * dark-obsidian & glowing ender-purple membranes spanning cleanly between
 * fanning finger spars.
 */
public class EnderDragonWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    private static final CubeDeformation MEMBRANE_THIN = new CubeDeformation(0.0f, 0.0f, -0.25f);

    private final ModelPart root;
    private final ModelPart leftInner;
    private final ModelPart leftOuter;
    private final ModelPart leftFinger1;
    private final ModelPart leftFinger2;
    private final ModelPart leftFinger3;
    private final ModelPart rightInner;
    private final ModelPart rightOuter;
    private final ModelPart rightFinger1;
    private final ModelPart rightFinger2;
    private final ModelPart rightFinger3;

    public EnderDragonWingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftInner = root.getChild("left_inner");
        this.leftOuter = this.leftInner.getChild("left_outer");
        this.leftFinger1 = this.leftOuter.getChild("left_finger_1");
        this.leftFinger2 = this.leftOuter.getChild("left_finger_2");
        this.leftFinger3 = this.leftOuter.getChild("left_finger_3");
        this.rightInner = root.getChild("right_inner");
        this.rightOuter = this.rightInner.getChild("right_outer");
        this.rightFinger1 = this.rightOuter.getChild("right_finger_1");
        this.rightFinger2 = this.rightOuter.getChild("right_finger_2");
        this.rightFinger3 = this.rightOuter.getChild("right_finger_3");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Central spine armor plate with glowing ender-purple vertebrae
        root.addOrReplaceChild("spine_plate",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0f, -1.0f, 7.0f, 12.0f, 18.0f, 4.0f),
                PartPose.ZERO);

        // --- LEFT ENDER DRAGON WING (+X) ---
        PartDefinition leftInner = root.addOrReplaceChild("left_inner",
                CubeListBuilder.create()
                        // Thick sturdy shoulder/arm bone + shoulder joint knuckle
                        .texOffs(34, 0).addBox(-1.0f, -2.5f, -2.0f, 24.0f, 5.0f, 4.0f)
                        .texOffs(34, 10).addBox(-2.0f, -3.0f, -2.5f, 6.0f, 6.0f, 5.0f)
                        // Wide inner obsidian-purple membrane
                        .texOffs(0, 40).addBox(-1.0f, 1.0f, 1.4f, 25.0f, 26.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(4.5f, 2.6f, 9.5f, 0.05f, -0.22f, -0.48f));

        PartDefinition leftOuter = leftInner.addOrReplaceChild("left_outer",
                CubeListBuilder.create()
                        // Leading-edge wingtip spar
                        .texOffs(92, 0).addBox(-1.5f, -2.0f, -1.5f, 34.0f, 4.0f, 3.0f)
                        // Wrist joint block & upper claw spur
                        .texOffs(92, 10).addBox(-2.0f, -3.0f, -2.0f, 5.0f, 6.0f, 4.0f)
                        .texOffs(112, 10).addBox(-0.5f, -8.5f, -1.5f, 3.0f, 6.0f, 3.0f),
                PartPose.offsetAndRotation(22.5f, -0.4f, 0.4f, 0.0f, -0.10f, 0.62f));

        leftOuter.addOrReplaceChild("left_finger_1",
                CubeListBuilder.create()
                        .texOffs(170, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 30.0f, 3.0f)
                        .texOffs(54, 40).addBox(-1.0f, 0.5f, 2.4f, 16.0f, 28.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(1.5f, 0.5f, 0.0f, 0.0f, 0.0f, -0.16f));

        leftOuter.addOrReplaceChild("left_finger_2",
                CubeListBuilder.create()
                        .texOffs(184, 0).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 33.0f, 2.0f)
                        .texOffs(90, 40).addBox(-1.0f, 0.5f, 3.4f, 18.0f, 30.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(11.0f, 0.5f, 0.0f, 0.0f, 0.0f, -0.54f));

        leftOuter.addOrReplaceChild("left_finger_3",
                CubeListBuilder.create()
                        .texOffs(196, 0).addBox(-1.0f, 0.0f, -1.0f, 3.0f, 27.0f, 2.0f)
                        .texOffs(130, 40).addBox(-1.0f, 0.5f, 4.4f, 18.0f, 25.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(20.5f, 0.5f, 0.0f, 0.0f, 0.0f, -0.94f));

        // --- RIGHT ENDER DRAGON WING (-X, mirrored) ---
        PartDefinition rightInner = root.addOrReplaceChild("right_inner",
                CubeListBuilder.create().mirror()
                        .texOffs(34, 0).addBox(-23.0f, -2.5f, -2.0f, 24.0f, 5.0f, 4.0f)
                        .texOffs(34, 10).addBox(-4.0f, -3.0f, -2.5f, 6.0f, 6.0f, 5.0f)
                        .texOffs(0, 40).addBox(-24.0f, 1.0f, 1.4f, 25.0f, 26.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-4.5f, 2.6f, 9.5f, 0.05f, 0.22f, 0.48f));

        PartDefinition rightOuter = rightInner.addOrReplaceChild("right_outer",
                CubeListBuilder.create().mirror()
                        .texOffs(92, 0).addBox(-32.5f, -2.0f, -1.5f, 34.0f, 4.0f, 3.0f)
                        .texOffs(92, 10).addBox(-3.0f, -3.0f, -2.0f, 5.0f, 6.0f, 4.0f)
                        .texOffs(112, 10).addBox(-2.5f, -8.5f, -1.5f, 3.0f, 6.0f, 3.0f),
                PartPose.offsetAndRotation(-22.5f, -0.4f, 0.4f, 0.0f, 0.10f, -0.62f));

        rightOuter.addOrReplaceChild("right_finger_1",
                CubeListBuilder.create().mirror()
                        .texOffs(170, 0).addBox(-1.5f, 0.0f, -1.5f, 3.0f, 30.0f, 3.0f)
                        .texOffs(54, 40).addBox(-15.0f, 0.5f, 2.4f, 16.0f, 28.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-1.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.16f));

        rightOuter.addOrReplaceChild("right_finger_2",
                CubeListBuilder.create().mirror()
                        .texOffs(184, 0).addBox(-1.5f, 0.0f, -1.0f, 3.0f, 33.0f, 2.0f)
                        .texOffs(90, 40).addBox(-17.0f, 0.5f, 3.4f, 18.0f, 30.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-11.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.54f));

        rightOuter.addOrReplaceChild("right_finger_3",
                CubeListBuilder.create().mirror()
                        .texOffs(196, 0).addBox(-2.0f, 0.0f, -1.0f, 3.0f, 27.0f, 2.0f)
                        .texOffs(130, 40).addBox(-17.0f, 0.5f, 4.4f, 18.0f, 25.0f, 1.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-20.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.94f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float energy = Math.min(1.0f, Math.max(s.flyAmount, s.walkSpeed * 0.85f));
        float sprintSweep = Math.max(0.0f, Math.min(1.0f, (s.walkSpeed - 0.55f) * 2.2f));
        float beatFreq = 0.26f;

        // Majestic heavy dragon downbeat: inner leads, outer snaps behind, fingers flex subtly
        float idleBreath = sin(t * 0.065f) * 0.055f;
        float innerFlap = idleBreath + sin(t * beatFreq) * (0.32f * energy);
        float outerFlap = sin(t * 0.065f - 0.5f) * 0.045f + sin(t * beatFreq - 0.60f) * (0.28f * energy);
        float fingerFlex = sin(t * beatFreq - 0.95f) * (0.06f * energy);

        float sweepY = 0.22f + 0.30f * s.flyAmount + 0.26f * sprintSweep - s.crouchAmount * 0.08f;
        float pitchX = 0.05f + 0.20f * s.flyAmount + 0.14f * sprintSweep + s.crouchAmount * 0.16f;

        leftInner.xRot = pitchX;
        leftInner.yRot = -sweepY;
        leftInner.zRot = -0.48f + innerFlap + s.crouchAmount * 0.12f;
        leftOuter.yRot = -0.10f - 0.12f * energy;
        leftOuter.zRot = 0.62f + outerFlap;
        leftFinger1.zRot = -0.16f - fingerFlex * 0.5f;
        leftFinger2.zRot = -0.54f - fingerFlex;
        leftFinger3.zRot = -0.94f - fingerFlex * 1.2f;

        rightInner.xRot = pitchX;
        rightInner.yRot = sweepY;
        rightInner.zRot = 0.48f - innerFlap - s.crouchAmount * 0.12f;
        rightOuter.yRot = 0.10f + 0.12f * energy;
        rightOuter.zRot = -0.62f - outerFlap;
        rightFinger1.zRot = 0.16f + fingerFlex * 0.5f;
        rightFinger2.zRot = 0.54f + fingerFlex;
        rightFinger3.zRot = 0.94f + fingerFlex * 1.2f;
    }
}
