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
 * Technoblade tarzı altın bantlı, kırmızı kadife kubbeli ve mücevherli Taç (Crown).
 * Doku: assets/modid/textures/cosmetic/hat_crown.png (64x64).
 */
public class RoyalCrownModel extends CosmeticModel {
    private final ModelPart crown;

    public RoyalCrownModel(ModelPart root) {
        super(root);
        this.crown = root.getChild("crown");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        CubeListBuilder b = CubeListBuilder.create();

        // Kirmizi kadife ic kubbe (32, 0)
        b.texOffs(32, 0).addBox(-3.8f, -10.6f, -3.8f, 7.6f, 2.4f, 7.6f);
        b.texOffs(32, 0).addBox(-2.8f, -11.4f, -2.8f, 5.6f, 1.0f, 5.6f);

        // Kalin altin kafa bandi (0, 0)
        b.texOffs(0, 0).addBox(-4.5f, -9.6f, -4.5f, 9.0f, 2.0f, 0.9f); // on
        b.texOffs(0, 0).addBox(-4.5f, -9.6f,  3.6f, 9.0f, 2.0f, 0.9f); // arka
        b.texOffs(0, 0).addBox(-4.5f, -9.6f, -3.6f, 0.9f, 2.0f, 7.2f); // sol
        b.texOffs(0, 0).addBox( 3.6f, -9.6f, -3.6f, 0.9f, 2.0f, 7.2f); // sag

        // 8 adet basamakli altin tac disi (on, arka, sag, sol + 4 kose)
        float[][] spikes = {
            {  0.0f, -4.1f, 2.4f }, {  0.0f,  4.1f, 2.0f },
            { -4.1f,  0.0f, 2.0f }, {  4.1f,  0.0f, 2.0f },
            { -3.9f, -3.9f, 2.4f }, {  3.9f, -3.9f, 2.4f },
            { -3.9f,  3.9f, 2.2f }, {  3.9f,  3.9f, 2.2f }
        };
        for (float[] sp : spikes) {
            float sx = sp[0], sz = sp[1], h = sp[2];
            float h1 = h * 0.58f;
            b.texOffs(0, 0).addBox(sx - 0.7f, -9.6f - h1, sz - 0.7f, 1.4f, h1, 1.4f);
            b.texOffs(0, 0).addBox(sx - 0.45f, -9.6f - h, sz - 0.45f, 0.9f, h - h1, 0.9f);
            b.texOffs(0, 0).addBox(sx - 0.55f, -9.6f - h - 0.75f, sz - 0.55f, 1.1f, 0.75f, 1.1f);
        }

        // Mucevherler: Yakut (0,32), Zumrut (16,32), Safir (32,32)
        b.texOffs(0, 32).addBox(-0.7f, -9.3f, -4.75f, 1.4f, 1.4f, 0.45f);
        b.texOffs(16, 32).addBox(-2.6f, -9.1f, -4.70f, 1.1f, 1.1f, 0.40f);
        b.texOffs(32, 32).addBox( 1.5f, -9.1f, -4.70f, 1.1f, 1.1f, 0.40f);

        b.texOffs(0, 32).addBox(-0.7f, -9.3f,  4.30f, 1.4f, 1.4f, 0.45f);
        b.texOffs(32, 32).addBox(-4.75f, -9.3f, -0.7f, 0.45f, 1.4f, 1.4f);
        b.texOffs(16, 32).addBox( 4.30f, -9.3f, -0.7f, 0.45f, 1.4f, 1.4f);

        root.addOrReplaceChild("crown", b, PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.crown.yRot = 0.0f;
    }
}
