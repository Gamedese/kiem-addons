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
 * Genis kenarli koyu mor kadife cadi sapkasi: altin serit/toka ve kivrik sivri uc.
 * UV duzeni: assets/modid/textures/cosmetic/hat_witch.png (64x64).
 */
public class WitchHatModel extends CosmeticModel {
    private final ModelPart cone1, cone2, cone3, tip;

    public WitchHatModel(ModelPart root) {
        super(root);
        ModelPart hatRoot = root.getChild("hatRoot");
        this.cone1 = hatRoot.getChild("cone1");
        this.cone2 = cone1.getChild("cone2");
        this.cone3 = cone2.getChild("cone3");
        this.tip = cone3.getChild("tip");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // Genis siperlik
        hatRoot.addOrReplaceChild("brim",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0f, -7.5f, -7.0f, 14.0f, 1.0f, 14.0f),
                PartPose.ZERO);

        // Altin serit ve on toka
        hatRoot.addOrReplaceChild("band",
                CubeListBuilder.create().texOffs(0, 15).addBox(-4.5f, -9.5f, -4.5f, 9.0f, 2.0f, 9.0f),
                PartPose.ZERO);
        hatRoot.addOrReplaceChild("buckle",
                CubeListBuilder.create().texOffs(36, 15).addBox(-1.5f, -9.5f, -5.0f, 3.0f, 2.0f, 1.0f),
                PartPose.ZERO);

        // Cok segmentli egri koni
        PartDefinition c1 = hatRoot.addOrReplaceChild("cone1",
                CubeListBuilder.create().texOffs(0, 26).addBox(-3.5f, -4.0f, -3.5f, 7.0f, 4.0f, 7.0f),
                PartPose.offsetAndRotation(0.0f, -9.5f, 0.0f, -0.06f, 0.0f, 0.0f));
        PartDefinition c2 = c1.addOrReplaceChild("cone2",
                CubeListBuilder.create().texOffs(28, 26).addBox(-2.5f, -4.0f, -2.5f, 5.0f, 4.0f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -3.8f, 0.0f, -0.18f, 0.0f, 0.12f));
        PartDefinition c3 = c2.addOrReplaceChild("cone3",
                CubeListBuilder.create().texOffs(0, 37).addBox(-1.5f, -3.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                PartPose.offsetAndRotation(0.0f, -3.6f, 0.0f, -0.32f, 0.0f, 0.20f));
        c3.addOrReplaceChild("tip",
                CubeListBuilder.create().texOffs(12, 37).addBox(-1.0f, -3.0f, -1.0f, 2.0f, 3.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, -2.8f, 0.0f, -0.42f, 0.0f, 0.24f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float sway = sin(t * 0.07f) * 0.03f;
        float walkBob = sin(s.walkPos * 0.6662f) * 0.06f * s.walkSpeed;

        cone1.xRot = -0.06f + walkBob * 0.3f;
        cone2.xRot = -0.18f + sway + walkBob * 0.6f;
        cone3.xRot = -0.32f + sin(t * 0.07f - 0.8f) * 0.05f + walkBob * 0.9f;
        tip.xRot = -0.42f + sin(t * 0.07f - 1.5f) * 0.07f + walkBob * 1.2f;

        cone2.zRot = 0.12f + cos(t * 0.05f) * 0.025f;
        cone3.zRot = 0.20f + cos(t * 0.05f - 0.6f) * 0.035f;
        tip.zRot = 0.24f + cos(t * 0.05f - 1.2f) * 0.045f;
    }
}
