package com.example.client.cosmetic.model.foot;

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
 * Platform Boots / Klasik Bot (Ayak Kozmetigi).
 * Yuksek mat komur-siyah deri govde, kalin disli platform taban,
 * one cikintili burun ve cift metalik tokali kayislar.
 */
public class CombatBootsModel extends CosmeticModel {

    public CombatBootsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Yuksek bot govdesi (y = 6.5..11.2f)
        root.addOrReplaceChild("bootShaft",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2f, 6.5f, -2f, 4f, 4.7f, 4f, new CubeDeformation(0.26f)),
                PartPose.ZERO);

        // One cikintili bot burnu
        root.addOrReplaceChild("toeBox",
                CubeListBuilder.create().texOffs(16, 0)
                        .addBox(-2f, 9.4f, -3.8f, 4f, 1.8f, 2.0f, new CubeDeformation(0.16f)),
                PartPose.ZERO);

        // Kalin disli platform taban
        root.addOrReplaceChild("platformSole",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-2f, 11.1f, -3.95f, 4f, 1.1f, 6.1f, new CubeDeformation(0.24f)),
                PartPose.ZERO);

        // Ust tokali kayis
        root.addOrReplaceChild("upperStrap",
                CubeListBuilder.create().texOffs(0, 19)
                        .addBox(-2f, 7.2f, -2f, 4f, 0.9f, 4f, new CubeDeformation(0.32f)),
                PartPose.ZERO);

        // Alt tokali kayis
        root.addOrReplaceChild("lowerStrap",
                CubeListBuilder.create().texOffs(0, 25)
                        .addBox(-2f, 9.0f, -2f, 4f, 0.9f, 4f, new CubeDeformation(0.32f)),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
    }
}
