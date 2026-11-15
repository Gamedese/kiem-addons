package com.example.client.cosmetic.model.waist;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * İki kalçada simetrik kınlı ve çelik bıçaklı İkili Hançerler (Twin Daggers).
 * Doku: assets/modid/textures/cosmetic/waist_twin_daggers.png (64x64).
 */
public class TwinDaggersModel extends CosmeticModel {
    private final ModelPart belt;

    public TwinDaggersModel(ModelPart root) {
        super(root);
        this.belt = root.getChild("belt");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition b = root.addOrReplaceChild("belt",
                CubeListBuilder.create()
                        // Bel kemeri (0,0 deri)
                        .texOffs(0, 0).addBox(-4.4f, 9.8f, -2.4f, 8.8f, 1.5f, 4.8f)
                        // Altin kemer tokasi (32,0)
                        .texOffs(32, 0).addBox(-1.1f, 9.6f, -2.6f, 2.2f, 1.9f, 0.4f),
                PartPose.ZERO);

        addDagger(b, "daggerL", -4.7f, -0.35f);
        addDagger(b, "daggerR",  4.7f,  0.35f);

        return LayerDefinition.create(mesh, 64, 64);
    }

    private static void addDagger(PartDefinition parent, String name, float x, float zRot) {
        parent.addOrReplaceChild(name,
                CubeListBuilder.create()
                        // Kabza topuzu & sargili tutamac (32,0 altin)
                        .texOffs(32, 0).addBox(-0.6f, -3.4f, -0.5f, 1.2f, 0.8f, 1.0f)
                        .texOffs(0, 0).addBox(-0.45f, -2.6f, -0.4f, 0.9f, 2.2f, 0.8f)
                        // Altin koruma (crossguard)
                        .texOffs(32, 0).addBox(-0.55f, -0.4f, -1.3f, 1.1f, 0.6f, 2.6f)
                        // Celik bicak & kin (0,32 celik)
                        .texOffs(0, 32).addBox(-0.35f, 0.2f, -0.55f, 0.7f, 5.2f, 1.1f)
                        .texOffs(32, 0).addBox(-0.4f, 5.4f, -0.45f, 0.8f, 0.9f, 0.9f),
                PartPose.offsetAndRotation(x, 10.6f, 0.4f, 0.42f, 0.0f, zRot));
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.belt.xRot = s.crouchAmount * 0.06f;
    }
}
