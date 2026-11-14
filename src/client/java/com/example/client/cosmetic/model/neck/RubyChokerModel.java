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
 * Body-attached snug black velvet collar choker with an inlaid glowing ruby gem
 * at front center and delicate back tie ribbons.
 * Texture: assets/modid/textures/cosmetic/neck_ruby_choker.png (256x256)
 */
public class RubyChokerModel extends CosmeticModel {
    private final ModelPart rubyGem;
    private final ModelPart ribbonL;
    private final ModelPart ribbonR;

    public RubyChokerModel(ModelPart root) {
        super(root);
        this.rubyGem = root.getChild("rubyGem");
        this.ribbonL = root.getChild("ribbonL");
        this.ribbonR = root.getChild("ribbonR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Snug black velvet collar band around the neck
        root.addOrReplaceChild("collarBand",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.12f, -0.35f, -2.12f, 8.24f, 1.15f, 4.24f),
                PartPose.ZERO);

        // Ornate gold bezel setting at front center
        root.addOrReplaceChild("goldBezel",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-0.85f, -0.45f, -2.28f, 1.7f, 1.35f, 0.35f),
                PartPose.ZERO);

        // Inlaid glowing ruby gem at front center
        root.addOrReplaceChild("rubyGem",
                CubeListBuilder.create()
                        .texOffs(8, 10)
                        .addBox(-0.55f, -0.50f, -0.20f, 1.1f, 1.0f, 0.36f),
                PartPose.offset(0.0f, 0.22f, -2.30f));

        // Delicate back velvet tie ribbons
        root.addOrReplaceChild("ribbonL",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-0.35f, 0.0f, -0.15f, 0.7f, 2.2f, 0.3f),
                PartPose.offsetAndRotation(0.45f, 0.65f, 2.15f, 0.18f, 0.08f, 0.12f));

        root.addOrReplaceChild("ribbonR",
                CubeListBuilder.create()
                        .texOffs(4, 16)
                        .addBox(-0.35f, 0.0f, -0.15f, 0.7f, 2.2f, 0.3f),
                PartPose.offsetAndRotation(-0.45f, 0.65f, 2.15f, 0.16f, -0.08f, -0.12f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(1.25f, s.walkSpeed + s.flyAmount * 0.8f);
        float pulse = 1.0f + sin(t * 0.10f) * 0.05f;
        rubyGem.xScale = pulse;
        rubyGem.yScale = pulse;

        float wave = sin(t * (0.12f + speed * 0.15f));
        ribbonL.xRot = 0.18f + speed * 0.35f + wave * (0.06f + speed * 0.12f);
        ribbonR.xRot = 0.16f + speed * 0.35f + sin(t * (0.12f + speed * 0.15f) + 0.6f) * (0.06f + speed * 0.12f);
    }
}
