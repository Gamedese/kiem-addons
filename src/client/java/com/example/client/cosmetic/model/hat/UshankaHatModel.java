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
 * Blockbench tabanli kalin kurklu ve kulak kapakli Fur Ushanka.
 * Doku: assets/modid/textures/cosmetic/hat_ushanka.png (64x64).
 */
public class UshankaHatModel extends CosmeticModel {
    private final ModelPart ushanka;

    public UshankaHatModel(ModelPart root) {
        super(root);
        this.ushanka = root.getChild("ushanka");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition u = root.addOrReplaceChild("ushanka",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.4f, -10.6f, -4.4f, 8.8f, 3.4f, 8.8f)
                        .texOffs(0, 0).addBox(-3.6f, -11.4f, -3.6f, 7.2f, 0.9f, 7.2f),
                PartPose.ZERO);

        u.addOrReplaceChild("frontVisor",
                CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-4.6f, -2.8f, -1.1f, 9.2f, 3.5f, 1.4f)
                        .texOffs(0, 32).addBox(-0.75f, -2.1f, -1.35f, 1.5f, 1.5f, 0.45f),
                PartPose.offsetAndRotation(0.0f, -7.5f, -4.4f, -0.14f, 0.0f, 0.0f));

        u.addOrReplaceChild("backFur",
                CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-4.6f, -2.0f, -0.4f, 9.2f, 3.5f, 1.2f),
                PartPose.offsetAndRotation(0.0f, -7.5f, 4.4f, 0.14f, 0.0f, 0.0f));

        u.addOrReplaceChild("flapL",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.9f, -0.2f, -2.0f, 0.9f, 6.0f, 4.0f)
                        .texOffs(32, 0).addBox(-0.2f, 0.0f, -1.7f, 0.6f, 5.6f, 3.4f)
                        .texOffs(0, 0).addBox(-0.7f, 5.7f, -0.4f, 0.5f, 1.1f, 0.8f),
                PartPose.offsetAndRotation(-4.4f, -8.0f, 0.0f, 0.0f, 0.0f, 0.10f));

        u.addOrReplaceChild("flapR",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(0.0f, -0.2f, -2.0f, 0.9f, 6.0f, 4.0f)
                        .texOffs(32, 0).addBox(-0.4f, 0.0f, -1.7f, 0.6f, 5.6f, 3.4f)
                        .texOffs(0, 0).addBox(0.2f, 5.7f, -0.4f, 0.5f, 1.1f, 0.8f),
                PartPose.offsetAndRotation(4.4f, -8.0f, 0.0f, 0.0f, 0.0f, -0.10f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.ushanka.yRot = 0.0f;
    }
}
