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
 * Detailed gold royal crown at y = -8.2f with 8 pointed spikes and inlaid ruby/emerald voxel gems (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_crown.png
 */
public class RoyalCrownModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart crownRoot;

    public RoyalCrownModel(ModelPart root) {
        super(root);
        this.root = root;
        this.crownRoot = root.getChild("crownRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // y = -8.2f -> -32.8f in 0.25x scale
        PartDefinition crownRoot = root.addOrReplaceChild("crownRoot",
                CubeListBuilder.create()
                        // Inner crimson velvet cap
                        .texOffs(0, 0)
                        .addBox(-15.0f, -4.0f, -15.0f, 30.0f, 5.0f, 30.0f)
                        // Front & back gold band walls
                        .texOffs(0, 40)
                        .addBox(-17.0f, -5.0f, -17.5f, 34.0f, 6.0f, 2.0f)
                        .texOffs(0, 40)
                        .addBox(-17.0f, -5.0f, 15.5f, 34.0f, 6.0f, 2.0f)
                        // Left & right gold band walls (slight y/x separation to prevent z-fighting)
                        .texOffs(0, 52)
                        .addBox(-17.5f, -5.05f, -16.5f, 2.0f, 6.0f, 33.0f)
                        .texOffs(0, 52)
                        .addBox(15.5f, -5.05f, -16.5f, 2.0f, 6.0f, 33.0f),
                PartPose.offset(0.0f, -32.8f, 0.0f));

        // 8 pointed spikes (4 cardinal + 4 corners)
        float[][] spikePositions = {
                {0.0f, -16.5f, 0.0f, 1.0f},     // Front
                {0.0f, 16.5f, 0.0f, 1.0f},      // Back
                {16.5f, 0.0f, 1.5708f, 1.0f},   // Left
                {-16.5f, 0.0f, 1.5708f, 1.0f},  // Right
                {15.5f, -15.5f, 0.7854f, 0.0f}, // Front-Left
                {-15.5f, -15.5f, -0.7854f, 0.0f}, // Front-Right
                {15.5f, 15.5f, -0.7854f, 0.0f}, // Back-Left
                {-15.5f, 15.5f, 0.7854f, 0.0f}  // Back-Right
        };

        for (int i = 0; i < spikePositions.length; i++) {
            float sx = spikePositions[i][0];
            float sz = spikePositions[i][1];
            float syRot = spikePositions[i][2];
            boolean cardinal = spikePositions[i][3] > 0.5f;

            CubeListBuilder sb = CubeListBuilder.create()
                    .texOffs(0, 96)
                    .addBox(-2.0f, -10.5f, -1.0f, 4.0f, 6.0f, 2.0f)
                    .texOffs(16, 96)
                    .addBox(-1.0f, -14.2f, -1.0f, 2.0f, 4.0f, 2.0f);

            if (cardinal) {
                sb.texOffs(28, 96).addBox(-1.5f, -15.8f, -1.5f, 3.0f, 2.0f, 3.0f);
            }

            crownRoot.addOrReplaceChild("spike_" + i, sb,
                    PartPose.offsetAndRotation(sx, 0.0f, sz, 0.0f, syRot, 0.0f));
        }

        // Inlaid protruding ruby & emerald voxel gems on the 4 band faces
        crownRoot.addOrReplaceChild("gemsFB",
                CubeListBuilder.create()
                        // Front gems
                        .texOffs(44, 96).addBox(-1.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(-10.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(7.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        // Back gems
                        .texOffs(44, 96).addBox(-1.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(-10.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(7.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f),
                PartPose.ZERO);

        crownRoot.addOrReplaceChild("gemsLR",
                CubeListBuilder.create()
                        // Left gems
                        .texOffs(44, 96).addBox(-1.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(-10.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(7.5f, -3.5f, -18.0f, 3.0f, 3.0f, 1.0f)
                        // Right gems
                        .texOffs(44, 96).addBox(-1.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(-10.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f)
                        .texOffs(56, 96).addBox(7.5f, -3.5f, 17.0f, 3.0f, 3.0f, 1.0f),
                PartPose.rotation(0.0f, (float) (Math.PI / 2.0), 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        crownRoot.xRot = sin(s.walkPos * 0.6662f) * 0.015f * s.walkSpeed;
    }
}
