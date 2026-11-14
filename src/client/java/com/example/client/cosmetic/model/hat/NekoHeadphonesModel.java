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
 * Sleek dark gaming headphones with glowing cyan/pink earcups and glowing cat ears (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_neko_headphones.png
 */
public class NekoHeadphonesModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart headsetRoot;
    private final ModelPart catEarL;
    private final ModelPart catEarR;

    public NekoHeadphonesModel(ModelPart root) {
        super(root);
        this.root = root;
        this.headsetRoot = root.getChild("headsetRoot");
        this.catEarL = headsetRoot.getChild("catEarL");
        this.catEarR = headsetRoot.getChild("catEarR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition headsetRoot = root.addOrReplaceChild("headsetRoot",
                CubeListBuilder.create()
                        // Top headband arch & inner cushion
                        .texOffs(0, 0).addBox(-16.5f, -35.5f, -3.0f, 33.0f, 3.0f, 6.0f)
                        .texOffs(0, 12).addBox(-13.0f, -32.8f, -2.5f, 26.0f, 2.0f, 5.0f)
                        // Side slider arms
                        .texOffs(0, 22).addBox(15.8f, -33.5f, -2.5f, 2.0f, 16.0f, 5.0f)
                        .texOffs(0, 22).addBox(-17.8f, -33.5f, -2.5f, 2.0f, 16.0f, 5.0f),
                PartPose.ZERO);

        // Left earcup + boom mic
        PartDefinition cupL = headsetRoot.addOrReplaceChild("earcupL",
                CubeListBuilder.create()
                        .texOffs(20, 22).addBox(-1.5f, -6.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(56, 22).addBox(-0.4f, -5.0f, -5.0f, 3.0f, 10.0f, 10.0f)
                        .texOffs(88, 22).addBox(2.0f, -4.0f, -4.0f, 1.0f, 8.0f, 8.0f),
                PartPose.offset(17.2f, -16.0f, 0.0f));

        cupL.addOrReplaceChild("mic",
                CubeListBuilder.create()
                        .texOffs(0, 68).addBox(-1.0f, 2.0f, -9.0f, 2.0f, 2.0f, 7.0f)
                        .texOffs(22, 68).addBox(-1.5f, 1.5f, -11.2f, 3.0f, 3.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, 1.0f, -3.0f, 0.16f, -0.32f, 0.0f));

        // Right earcup
        headsetRoot.addOrReplaceChild("earcupR",
                CubeListBuilder.create()
                        .texOffs(20, 22).addBox(-1.5f, -6.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(56, 22).addBox(-2.6f, -5.0f, -5.0f, 3.0f, 10.0f, 10.0f)
                        .texOffs(88, 22).addBox(-3.0f, -4.0f, -4.0f, 1.0f, 8.0f, 8.0f),
                PartPose.offset(-17.2f, -16.0f, 0.0f));

        // Glowing cat ears on headband
        headsetRoot.addOrReplaceChild("catEarL",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-4.0f, -9.0f, -2.0f, 8.0f, 9.0f, 4.0f)
                        .texOffs(28, 50).addBox(-2.0f, -11.8f, -1.5f, 4.0f, 3.0f, 3.0f)
                        .texOffs(46, 50).addBox(-2.5f, -7.5f, -2.3f, 5.0f, 6.0f, 1.0f),
                PartPose.offsetAndRotation(10.5f, -35.2f, 0.0f, 0.05f, 0.0f, 0.20f));

        headsetRoot.addOrReplaceChild("catEarR",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(-4.0f, -9.0f, -2.0f, 8.0f, 9.0f, 4.0f)
                        .texOffs(28, 50).addBox(-2.0f, -11.8f, -1.5f, 4.0f, 3.0f, 3.0f)
                        .texOffs(46, 50).addBox(-2.5f, -7.5f, -2.3f, 5.0f, 6.0f, 1.0f),
                PartPose.offsetAndRotation(-10.5f, -35.2f, 0.0f, 0.05f, 0.0f, -0.20f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float walkBounce = sin(s.walkPos * 0.6662f) * 0.03f * s.walkSpeed;
        catEarL.zRot = 0.20f + walkBounce;
        catEarR.zRot = -0.20f - walkBounce;
        headsetRoot.y = sin(s.walkPos * 1.3324f) * 0.15f * s.walkSpeed;
    }
}
