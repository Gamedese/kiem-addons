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
 * Classic 3-cornered dark navy & gold-trimmed pirate captain tricorn hat (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_tricorn.png
 */
public class TricornHatModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart hatRoot;

    public TricornHatModel(ModelPart root) {
        super(root);
        this.root = root;
        this.hatRoot = root.getChild("hatRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create()
                        // Midnight navy crown & top
                        .texOffs(0, 0).addBox(-15.0f, -9.0f, -15.0f, 30.0f, 9.0f, 30.0f)
                        .texOffs(124, 0).addBox(-12.0f, -10.8f, -12.0f, 24.0f, 2.0f, 24.0f)
                        // Base brim plate
                        .texOffs(0, 44).addBox(-20.0f, -1.0f, -20.0f, 40.0f, 2.0f, 40.0f),
                PartPose.offset(0.0f, -31.0f, 0.0f));

        // Front-pointing prow (-Z)
        hatRoot.addOrReplaceChild("prow",
                CubeListBuilder.create().texOffs(164, 44).addBox(-8.0f, -2.5f, -10.0f, 16.0f, 4.0f, 10.0f),
                PartPose.offsetAndRotation(0.0f, -0.5f, -18.5f, 0.16f, 0.0f, 0.0f));

        // Left & right upturned brim walls forming the 3-cornered silhouette
        hatRoot.addOrReplaceChild("sideWallL",
                CubeListBuilder.create()
                        .texOffs(0, 90).addBox(-1.5f, -9.0f, -19.0f, 3.0f, 9.0f, 38.0f)
                        .texOffs(176, 90).addBox(1.0f, -6.5f, -9.0f, 2.0f, 6.0f, 6.0f),
                PartPose.offsetAndRotation(18.5f, 0.0f, 0.5f, 0.0f, 0.36f, 0.28f));

        hatRoot.addOrReplaceChild("sideWallR",
                CubeListBuilder.create()
                        .texOffs(0, 90).addBox(-1.5f, -9.0f, -19.0f, 3.0f, 9.0f, 38.0f),
                PartPose.offsetAndRotation(-18.5f, 0.0f, 0.5f, 0.0f, -0.36f, -0.28f));

        // Back upturned brim wall (+Z)
        hatRoot.addOrReplaceChild("backWall",
                CubeListBuilder.create()
                        .texOffs(86, 90).addBox(-20.0f, -9.0f, -1.5f, 40.0f, 9.0f, 3.0f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 18.5f, -0.26f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        hatRoot.xRot = sin(s.walkPos * 0.6662f) * 0.02f * s.walkSpeed;
        hatRoot.zRot = cos(s.walkPos * 0.6662f) * 0.015f * s.walkSpeed;
    }
}
