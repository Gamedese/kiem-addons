package com.example.client.cosmetic.model.neck;

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
 * Body-attached twin gold shoulder clasps connected across the upper chest
 * by a sagging gold chain and central ruby brooch.
 * Texture: assets/modid/textures/cosmetic/neck_royal_brooch.png (256x256)
 */
public class RoyalBroochModel extends CosmeticModel {
    private final ModelPart chainGroup;
    private final ModelPart rubyCore;

    public RoyalBroochModel(ModelPart root) {
        super(root);
        this.chainGroup = root.getChild("chainGroup");
        this.rubyCore = chainGroup.getChild("brooch").getChild("rubyCore");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Twin ornate gold shoulder clasps on upper front shoulders
        root.addOrReplaceChild("claspL",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.75f, -0.75f, -0.35f, 1.5f, 1.5f, 0.7f),
                PartPose.offset(3.25f, 0.65f, -2.18f));

        root.addOrReplaceChild("claspR",
                CubeListBuilder.create()
                        .texOffs(8, 0)
                        .addBox(-0.75f, -0.75f, -0.35f, 1.5f, 1.5f, 0.7f),
                PartPose.offset(-3.25f, 0.65f, -2.18f));

        // Sagging gold chain & central ruby brooch group
        PartDefinition chain = root.addOrReplaceChild("chainGroup",
                CubeListBuilder.create(),
                PartPose.ZERO);

        chain.addOrReplaceChild("chainL1",
                CubeListBuilder.create().texOffs(0, 6)
                        .addBox(-0.5f, -0.5f, -0.3f, 1.0f, 1.0f, 0.6f, new CubeDeformation(-0.12f)),
                PartPose.offset(2.30f, 1.35f, -2.22f));

        chain.addOrReplaceChild("chainR1",
                CubeListBuilder.create().texOffs(6, 6)
                        .addBox(-0.5f, -0.5f, -0.3f, 1.0f, 1.0f, 0.6f, new CubeDeformation(-0.12f)),
                PartPose.offset(-2.30f, 1.35f, -2.22f));

        chain.addOrReplaceChild("chainL2",
                CubeListBuilder.create().texOffs(12, 6)
                        .addBox(-0.5f, -0.5f, -0.3f, 1.0f, 1.0f, 0.6f, new CubeDeformation(-0.12f)),
                PartPose.offset(1.35f, 2.05f, -2.25f));

        chain.addOrReplaceChild("chainR2",
                CubeListBuilder.create().texOffs(18, 6)
                        .addBox(-0.5f, -0.5f, -0.3f, 1.0f, 1.0f, 0.6f, new CubeDeformation(-0.12f)),
                PartPose.offset(-1.35f, 2.05f, -2.25f));

        // Central royal gold filigree brooch at (0.0, 2.45, -2.28)
        PartDefinition brooch = chain.addOrReplaceChild("brooch",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-0.9f, -0.9f, -0.25f, 1.8f, 1.8f, 0.5f),
                PartPose.offsetAndRotation(0.0f, 2.45f, -2.28f, 0.0f, 0.0f, (float) (Math.PI / 4.0)));

        // Faceted central ruby gem
        brooch.addOrReplaceChild("rubyCore",
                CubeListBuilder.create()
                        .texOffs(10, 12)
                        .addBox(-0.55f, -0.55f, -0.35f, 1.1f, 1.1f, 0.55f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.3f);
        chainGroup.y = Math.abs(sin(s.walkPos * 0.6662f)) * 0.08f * speed;
        chainGroup.xRot = -speed * 0.05f - s.crouchAmount * 0.12f;
        chainGroup.zRot = sin(s.walkPos * 0.6662f) * 0.025f * speed;

        float shimmer = 1.0f + sin(t * 0.10f) * 0.04f;
        rubyCore.xScale = shimmer;
        rubyCore.yScale = shimmer;
    }
}
