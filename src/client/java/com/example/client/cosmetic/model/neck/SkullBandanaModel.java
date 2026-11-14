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
 * Head-attached black face gaiter/bandana with crisp white skull-jaw pixel art at z = -4.25.
 * Texture: assets/modid/textures/cosmetic/neck_skull_bandana.png (256x256)
 */
public class SkullBandanaModel extends CosmeticModel {
    private final ModelPart bandanaHem;

    public SkullBandanaModel(ModelPart root) {
        super(root);
        this.bandanaHem = root.getChild("bandanaHem");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Snug black gaiter wrap covering nose, mouth, chin, and sides/back of lower head (y = -2.2..+0.5)
        root.addOrReplaceChild("gaiterWrap",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.2f, -2.2f, -4.22f, 8.4f, 2.7f, 8.4f),
                PartPose.ZERO);

        // Crisp front-face skull-jaw panel at z = -4.25
        root.addOrReplaceChild("skullFront",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-4.0f, -2.2f, -4.28f, 8.0f, 2.7f, 0.25f),
                PartPose.ZERO);

        // Slight draped gaiter hem below the chin
        root.addOrReplaceChild("bandanaHem",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-3.2f, 0.0f, -0.2f, 6.4f, 0.75f, 0.4f),
                PartPose.offsetAndRotation(0.0f, 0.45f, -4.15f, -0.14f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        float speed = Math.min(1.2f, s.walkSpeed);
        bandanaHem.xRot = -0.14f - speed * 0.12f + sin(s.ageInTicks * 0.08f) * 0.025f;
    }
}
