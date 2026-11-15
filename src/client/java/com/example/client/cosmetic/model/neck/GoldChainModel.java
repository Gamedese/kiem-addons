package com.example.client.cosmetic.model.neck;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Kalın 3D altın zincir ve elmaslı madalyon (Gold Chain).
 * Doku: assets/modid/textures/cosmetic/neck_chain.png (64x64).
 */
public class GoldChainModel extends CosmeticModel {
    private final ModelPart chain;
    private final ModelPart pendant;

    public GoldChainModel(ModelPart root) {
        super(root);
        this.chain = root.getChild("chain");
        this.pendant = this.chain.getChild("pendant");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition c = root.addOrReplaceChild("chain",
                CubeListBuilder.create()
                        // Ense ve omuz ustu zincir
                        .texOffs(0, 0).addBox(-3.6f, -0.2f,  1.8f, 7.2f, 1.0f, 0.9f)
                        .texOffs(0, 0).addBox(-3.8f, -0.1f, -2.5f, 1.0f, 1.1f, 4.8f)
                        .texOffs(0, 0).addBox( 2.8f, -0.1f, -2.5f, 1.0f, 1.1f, 4.8f)
                        // Gogse inen U seklinde kalin altin halkalar
                        .texOffs(0, 0).addBox(-3.3f, 0.6f, -2.65f, 1.1f, 2.2f, 0.9f)
                        .texOffs(0, 0).addBox( 2.2f, 0.6f, -2.65f, 1.1f, 2.2f, 0.9f)
                        .texOffs(0, 0).addBox(-2.6f, 2.3f, -2.75f, 1.2f, 1.4f, 0.9f)
                        .texOffs(0, 0).addBox( 1.4f, 2.3f, -2.75f, 1.2f, 1.4f, 0.9f)
                        .texOffs(0, 0).addBox(-1.6f, 3.2f, -2.85f, 3.2f, 1.2f, 0.9f),
                PartPose.ZERO);

        c.addOrReplaceChild("pendant",
                CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-1.1f, 0.0f, -0.45f, 2.2f, 2.2f, 0.8f),
                PartPose.offset(0.0f, 4.1f, -2.65f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.chain.xRot = 0.0f;
        this.pendant.xRot = sin(s.walkPos * 0.6662f) * 0.12f * s.walkSpeed + s.crouchAmount * 0.18f;
    }
}
