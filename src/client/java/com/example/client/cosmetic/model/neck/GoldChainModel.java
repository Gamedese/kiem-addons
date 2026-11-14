package com.example.client.cosmetic.model.neck;

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
 * Pure chunky blocky gold link chain (no medallion) draped cleanly across
 * the collar and upper chest (y = 0.0..3.5, z = -2.3) with stepped link voxels.
 * Texture: assets/modid/textures/cosmetic/neck_chain.png (64x64)
 */
public class GoldChainModel extends CosmeticModel {
    private final ModelPart chainDrape;

    public GoldChainModel(ModelPart root) {
        super(root);
        this.chainDrape = root.getChild("chainDrape");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Collar links wrapping the sides and back of the neck (front face transparent)
        root.addOrReplaceChild("neckBack",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0f, 0.0f, -2.0f, 8, 1, 4, new CubeDeformation(0.22f)),
                PartPose.ZERO);

        // Stepped chunky gold link drape across the upper chest (y = 0.0..3.5, z = -2.3)
        PartDefinition drape = root.addOrReplaceChild("chainDrape",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // Upper shoulder links
        drape.addOrReplaceChild("linkL1",
                CubeListBuilder.create().texOffs(0, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.16f)),
                PartPose.offset(3.1f, 0.55f, -2.28f));
        drape.addOrReplaceChild("linkR1",
                CubeListBuilder.create().texOffs(5, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.16f)),
                PartPose.offset(-3.1f, 0.55f, -2.28f));

        // Second stepped tier
        drape.addOrReplaceChild("linkL2",
                CubeListBuilder.create().texOffs(5, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.18f)),
                PartPose.offset(2.2f, 1.30f, -2.32f));
        drape.addOrReplaceChild("linkR2",
                CubeListBuilder.create().texOffs(0, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.18f)),
                PartPose.offset(-2.2f, 1.30f, -2.32f));

        // Third stepped tier
        drape.addOrReplaceChild("linkL3",
                CubeListBuilder.create().texOffs(0, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.18f)),
                PartPose.offset(1.35f, 2.05f, -2.35f));
        drape.addOrReplaceChild("linkR3",
                CubeListBuilder.create().texOffs(5, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.18f)),
                PartPose.offset(-1.35f, 2.05f, -2.35f));

        // Bottom U-curve links
        drape.addOrReplaceChild("linkBottomL",
                CubeListBuilder.create().texOffs(5, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.15f)),
                PartPose.offset(0.65f, 2.68f, -2.36f));
        drape.addOrReplaceChild("linkBottomR",
                CubeListBuilder.create().texOffs(0, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.15f)),
                PartPose.offset(-0.65f, 2.68f, -2.36f));

        // Heavy center link completing the chain (no medallion)
        drape.addOrReplaceChild("linkCenter",
                CubeListBuilder.create().texOffs(10, 6)
                        .addBox(-1.0f, -0.5f, -0.5f, 2, 1, 1, new CubeDeformation(0.14f)),
                PartPose.offset(0.0f, 2.90f, -2.38f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float speed = Math.min(s.walkSpeed, 1.3f);
        // Subtle heavy-gold bounce when moving
        chainDrape.y = Math.abs(sin(s.walkPos * 0.6662f)) * 0.12f * speed;
        chainDrape.xRot = -speed * 0.08f - s.crouchAmount * 0.16f;
        chainDrape.zRot = sin(s.walkPos * 0.6662f) * 0.03f * speed;
    }
}
