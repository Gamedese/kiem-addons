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
 * Tek, kıvrımlı ve animasyonlu Kedi Kuyruğu (Cat Tail).
 * Doku: assets/modid/textures/cosmetic/waist_cat_tail.png (64x64).
 */
public class CatTailModel extends CosmeticModel {
    private final ModelPart seg1, seg2, seg3, seg4;

    public CatTailModel(ModelPart root) {
        super(root);
        this.seg1 = root.getChild("seg1");
        this.seg2 = this.seg1.getChild("seg2");
        this.seg3 = this.seg2.getChild("seg3");
        this.seg4 = this.seg3.getChild("seg4");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition s1 = root.addOrReplaceChild("seg1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.85f, -0.85f, 0.0f, 1.7f, 1.7f, 3.8f),
                PartPose.offsetAndRotation(0.0f, 10.4f, 2.0f, -0.45f, 0.0f, 0.0f));

        PartDefinition s2 = s1.addOrReplaceChild("seg2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.75f, -0.75f, 0.0f, 1.5f, 1.5f, 3.6f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 3.5f, 0.32f, 0.0f, 0.0f));

        PartDefinition s3 = s2.addOrReplaceChild("seg3",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.65f, -0.65f, 0.0f, 1.3f, 1.3f, 3.4f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 3.3f, 0.48f, 0.0f, 0.0f));

        s3.addOrReplaceChild("seg4",
                CubeListBuilder.create().texOffs(32, 0).addBox(-0.6f, -0.6f, 0.0f, 1.2f, 1.2f, 2.8f),
                PartPose.offsetAndRotation(0.0f, 0.0f, 3.1f, 0.35f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float sway = sin(t * 0.11f) * 0.18f + sin(s.walkPos * 0.6662f) * 0.12f * s.walkSpeed;
        seg1.yRot = sway * 0.7f;
        seg2.yRot = sway * 0.9f;
        seg3.yRot = sin(t * 0.11f - 0.8f) * 0.22f;
        seg4.yRot = sin(t * 0.11f - 1.5f) * 0.26f;
        seg1.xRot = -0.45f + s.crouchAmount * 0.25f;
    }
}
