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
 * Blockbench tabanli altin biyeli, kuru kafali ve kirmizi tuylu Korsan Sapkasi (Tricorn).
 * Doku: assets/modid/textures/cosmetic/hat_tricorn.png (64x64).
 */
public class TricornHatModel extends CosmeticModel {
    private final ModelPart tricorn;

    public TricornHatModel(ModelPart root) {
        super(root);
        this.tricorn = root.getChild("tricorn");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition t = root.addOrReplaceChild("tricorn",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.2f, -10.6f, -4.2f, 8.4f, 3.0f, 8.4f)
                        .texOffs(0, 0).addBox(-5.8f, -8.0f, -5.8f, 11.6f, 0.6f, 11.6f),
                PartPose.ZERO);

        t.addOrReplaceChild("flapFrontL",
                CubeListBuilder.create().texOffs(32, 0).addBox(-5.8f, -3.4f, -0.55f, 5.8f, 3.4f, 1.1f),
                PartPose.offsetAndRotation(0.0f, -7.8f, -5.6f, -0.24f, 0.38f, 0.0f));

        t.addOrReplaceChild("flapFrontR",
                CubeListBuilder.create().texOffs(32, 0).addBox(0.0f, -3.4f, -0.55f, 5.8f, 3.4f, 1.1f),
                PartPose.offsetAndRotation(0.0f, -7.8f, -5.6f, -0.24f, -0.38f, 0.0f));

        t.addOrReplaceChild("flapBack",
                CubeListBuilder.create().texOffs(32, 0).addBox(-5.8f, -3.6f, -0.5f, 11.6f, 3.6f, 1.0f),
                PartPose.offsetAndRotation(0.0f, -7.8f, 5.5f, 0.24f, 0.0f, 0.0f));

        t.addOrReplaceChild("flapSideL",
                CubeListBuilder.create().texOffs(32, 0).addBox(-0.6f, -3.2f, -4.2f, 1.0f, 3.2f, 8.8f),
                PartPose.offsetAndRotation(-5.6f, -7.8f, 0.0f, 0.0f, 0.0f, 0.24f));

        t.addOrReplaceChild("flapSideR",
                CubeListBuilder.create().texOffs(32, 0).addBox(-0.4f, -3.2f, -4.2f, 1.0f, 3.2f, 8.8f),
                PartPose.offsetAndRotation(5.6f, -7.8f, 0.0f, 0.0f, 0.0f, -0.24f));

        t.addOrReplaceChild("skullBadge",
                CubeListBuilder.create().texOffs(0, 32).addBox(-1.1f, -3.0f, -0.9f, 2.2f, 2.2f, 0.7f),
                PartPose.offsetAndRotation(0.0f, -7.8f, -5.6f, -0.24f, 0.0f, 0.0f));

        t.addOrReplaceChild("feather1",
                CubeListBuilder.create()
                        .texOffs(16, 32).addBox(-0.2f, -3.2f, -1.5f, 0.7f, 3.2f, 4.5f)
                        .texOffs(16, 32).addBox(0.1f, -5.2f, 1.4f, 0.55f, 2.4f, 3.4f),
                PartPose.offsetAndRotation(4.6f, -9.8f, 0.0f, 0.31f, -0.21f, 0.28f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.tricorn.yRot = 0.0f;
    }
}
