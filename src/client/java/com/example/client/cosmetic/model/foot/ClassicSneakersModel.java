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
 * Classic Sneakers / Klasik Sneaker (Ayak Kozmetigi).
 * Kirik beyaz ve arduvaz-komur cift tonlu temiz alcak bilek ust kisim,
 * dolgulu dil, burun ve beyaz kaucuk taban.
 */
public class ClassicSneakersModel extends CosmeticModel {

    public ClassicSneakersModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Temiz alcak bilek sneaker ust govdesi
        root.addOrReplaceChild("sneakerUpper",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2f, 8.4f, -2f, 4f, 2.7f, 4f, new CubeDeformation(0.22f)),
                PartPose.ZERO);

        // One cikintili burun kutusu
        root.addOrReplaceChild("toeBox",
                CubeListBuilder.create().texOffs(16, 0)
                        .addBox(-2f, 9.4f, -3.9f, 4f, 1.7f, 2.1f, new CubeDeformation(0.14f)),
                PartPose.ZERO);

        // Beyaz kaucuk taban
        root.addOrReplaceChild("cupsole",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2f, 11.0f, -4.0f, 4f, 1.0f, 6.1f, new CubeDeformation(0.22f)),
                PartPose.ZERO);

        // Dolgulu dil
        root.addOrReplaceChild("tongue",
                CubeListBuilder.create().texOffs(0, 17)
                        .addBox(-1.3f, 7.9f, -2.55f, 2.6f, 1.4f, 0.7f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
    }
}
