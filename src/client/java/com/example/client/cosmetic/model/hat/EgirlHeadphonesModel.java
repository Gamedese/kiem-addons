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
 * Blockbench tabanli kedi kulakli, pembe LED'li ve mikrofonlu E-Girl Kulaklik.
 * Doku: assets/modid/textures/cosmetic/hat_egirl_headphones.png (64x64).
 */
public class EgirlHeadphonesModel extends CosmeticModel {
    private final ModelPart headphones;

    public EgirlHeadphonesModel(ModelPart root) {
        super(root);
        this.headphones = root.getChild("headphones");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hp = root.addOrReplaceChild("headphones",
                CubeListBuilder.create()
                        // Kafa bandi (0,0 siyah / 32,0 pembe ic ped)
                        .texOffs(0, 0).addBox(-4.3f, -9.2f, -1.0f, 8.6f, 1.1f, 2.0f)
                        .texOffs(32, 0).addBox(-2.6f, -8.2f, -0.8f, 5.2f, 0.4f, 1.6f)
                        // Yan kollar
                        .texOffs(0, 0).addBox(-5.0f, -8.6f, -0.8f, 0.9f, 3.4f, 1.6f)
                        .texOffs(0, 0).addBox( 4.1f, -8.6f, -0.8f, 0.9f, 3.4f, 1.6f)
                        // Kulak pedleri (0,32) + pembe LED (32,0)
                        .texOffs(0, 32).addBox(-5.5f, -5.9f, -1.9f, 1.4f, 3.8f, 3.8f)
                        .texOffs(32, 0).addBox(-5.75f, -5.1f, -1.1f, 0.35f, 2.2f, 2.2f)
                        .texOffs(0, 32).addBox( 4.1f, -5.9f, -1.9f, 1.4f, 3.8f, 3.8f)
                        .texOffs(32, 0).addBox( 5.4f, -5.1f, -1.1f, 0.35f, 2.2f, 2.2f)
                        // Mikrofon ucu
                        .texOffs(32, 0).addBox(-3.8f, -2.2f, -4.4f, 0.8f, 0.7f, 0.9f),
                PartPose.ZERO);

        // Sol kedi kulagi (buyuk 3 kademeli sivri)
        hp.addOrReplaceChild("earL",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.6f, -1.7f, -0.65f, 3.2f, 1.7f, 1.3f)
                        .texOffs(0, 0).addBox(-1.1f, -2.8f, -0.55f, 2.2f, 1.2f, 1.1f)
                        .texOffs(0, 0).addBox(-0.6f, -3.7f, -0.45f, 1.2f, 1.0f, 0.9f)
                        .texOffs(32, 0).addBox(-1.0f, -1.5f, -0.75f, 2.0f, 1.3f, 0.95f)
                        .texOffs(32, 0).addBox(-0.6f, -2.4f, -0.65f, 1.2f, 1.0f, 0.85f),
                PartPose.offsetAndRotation(-3.0f, -9.0f, 0.0f, 0.0f, 0.0f, -0.24f));

        // Sag kedi kulagi (buyuk 3 kademeli sivri)
        hp.addOrReplaceChild("earR",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-1.6f, -1.7f, -0.65f, 3.2f, 1.7f, 1.3f)
                        .texOffs(0, 0).addBox(-1.1f, -2.8f, -0.55f, 2.2f, 1.2f, 1.1f)
                        .texOffs(0, 0).addBox(-0.6f, -3.7f, -0.45f, 1.2f, 1.0f, 0.9f)
                        .texOffs(32, 0).addBox(-1.0f, -1.5f, -0.75f, 2.0f, 1.3f, 0.95f)
                        .texOffs(32, 0).addBox(-0.6f, -2.4f, -0.65f, 1.2f, 1.0f, 0.85f),
                PartPose.offsetAndRotation(3.0f, -9.0f, 0.0f, 0.0f, 0.0f, 0.24f));

        // Mikrofon kolu
        hp.addOrReplaceChild("micArm",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-0.25f, -0.25f, -2.3f, 0.5f, 0.5f, 2.3f),
                PartPose.offsetAndRotation(-4.45f, -2.65f, -1.2f, 0.31f, -0.28f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.headphones.yRot = 0.0f;
    }
}
