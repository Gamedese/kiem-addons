package com.example.client.cosmetic.model.hat;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Pastel pink & pearl-white cute e-girl gaming headset with heart/wing accents on earcups (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_egirl_headphones.png
 */
public class EgirlHeadphonesModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart headsetRoot;
    private final ModelPart wingL;
    private final ModelPart wingR;

    public EgirlHeadphonesModel(ModelPart root) {
        super(root);
        this.root = root;
        this.headsetRoot = root.getChild("headsetRoot");
        this.wingL = headsetRoot.getChild("earcupL").getChild("wingL");
        this.wingR = headsetRoot.getChild("earcupR").getChild("wingR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition headsetRoot = root.addOrReplaceChild("headsetRoot",
                CubeListBuilder.create()
                        // Pearl-white headband arch & pastel pink inner cushion
                        .texOffs(0, 0).addBox(-16.5f, -35.0f, -2.5f, 33.0f, 3.0f, 5.0f)
                        .texOffs(0, 12).addBox(-12.0f, -32.4f, -2.0f, 24.0f, 2.0f, 4.0f)
                        // Side sliders
                        .texOffs(0, 22).addBox(15.8f, -33.0f, -2.0f, 2.0f, 16.0f, 4.0f)
                        .texOffs(0, 22).addBox(-17.8f, -33.0f, -2.0f, 2.0f, 16.0f, 4.0f),
                PartPose.ZERO);

        // Left earcup + mini angel wing + cute heart mic
        PartDefinition cupL = headsetRoot.addOrReplaceChild("earcupL",
                CubeListBuilder.create()
                        .texOffs(16, 22).addBox(-1.5f, -6.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(52, 22).addBox(-0.4f, -5.0f, -5.0f, 3.0f, 10.0f, 10.0f)
                        .texOffs(84, 22).addBox(2.0f, -3.0f, -3.0f, 1.0f, 6.0f, 6.0f),
                PartPose.offset(17.2f, -16.0f, 0.0f));

        cupL.addOrReplaceChild("wingL",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-0.5f, -5.0f, 2.0f, 1.0f, 5.0f, 7.0f)
                        .texOffs(20, 50).addBox(-0.4f, -7.5f, 5.0f, 1.0f, 4.0f, 6.0f),
                PartPose.offsetAndRotation(1.5f, -1.0f, 3.0f, 0.12f, 0.35f, 0.20f));

        cupL.addOrReplaceChild("mic",
                CubeListBuilder.create()
                        .texOffs(38, 50).addBox(-1.0f, 2.0f, -8.0f, 2.0f, 2.0f, 6.0f)
                        .texOffs(58, 50).addBox(-1.5f, 1.5f, -10.2f, 3.0f, 3.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, 1.0f, -3.0f, 0.15f, -0.30f, 0.0f));

        // Right earcup + mini angel wing
        PartDefinition cupR = headsetRoot.addOrReplaceChild("earcupR",
                CubeListBuilder.create()
                        .texOffs(16, 22).addBox(-1.5f, -6.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(52, 22).addBox(-2.6f, -5.0f, -5.0f, 3.0f, 10.0f, 10.0f)
                        .texOffs(84, 22).addBox(-3.0f, -3.0f, -3.0f, 1.0f, 6.0f, 6.0f),
                PartPose.offset(-17.2f, -16.0f, 0.0f));

        cupR.addOrReplaceChild("wingR",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-0.5f, -5.0f, 2.0f, 1.0f, 5.0f, 7.0f)
                        .texOffs(20, 50).addBox(-0.6f, -7.5f, 5.0f, 1.0f, 4.0f, 6.0f),
                PartPose.offsetAndRotation(-1.5f, -1.0f, 3.0f, 0.12f, -0.35f, -0.20f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float flutter = sin(s.ageInTicks * 0.14f) * 0.08f + sin(s.walkPos * 0.6662f) * 0.05f * s.walkSpeed;
        wingL.yRot = 0.35f + flutter;
        wingR.yRot = -0.35f - flutter;
        headsetRoot.y = sin(s.walkPos * 1.3324f) * 0.15f * s.walkSpeed;
    }
}
