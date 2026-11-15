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
 * Sıkı siyah shinobi alt yüz maskesi (Shadow Ninja Mask).
 * Doku: assets/modid/textures/cosmetic/neck_ninja_mask.png (64x64).
 */
public class ShadowNinjaMaskModel extends CosmeticModel {
    private final ModelPart mask;
    private final ModelPart knotL, knotR;

    public ShadowNinjaMaskModel(ModelPart root) {
        super(root);
        this.mask = root.getChild("mask");
        this.knotL = this.mask.getChild("knotL");
        this.knotR = this.mask.getChild("knotR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition m = root.addOrReplaceChild("mask",
                CubeListBuilder.create()
                        // Agiz ve burun kapatan kumastan yuz maskesi
                        .texOffs(0, 0).addBox(-4.35f, -3.5f, -4.45f, 8.7f, 3.6f, 0.8f)
                        .texOffs(0, 0).addBox(-4.35f, -3.4f, -3.7f, 0.7f, 3.4f, 7.9f)
                        .texOffs(0, 0).addBox( 3.65f, -3.4f, -3.7f, 0.7f, 3.4f, 7.9f)
                        .texOffs(0, 0).addBox(-4.35f, -3.2f,  3.6f, 8.7f, 3.0f, 0.7f)
                        // Alt cene sarimi
                        .texOffs(0, 0).addBox(-4.2f, -0.3f, -4.3f, 8.4f, 0.7f, 4.0f)
                        // Koyu kirmizi yan vurgu seridi (32, 0)
                        .texOffs(32, 0).addBox(-4.45f, -2.5f, -4.55f, 8.9f, 0.7f, 0.3f),
                PartPose.ZERO);

        m.addOrReplaceChild("knotL",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.4f, 0.0f, 0.0f, 0.8f, 3.2f, 0.5f),
                PartPose.offsetAndRotation(-0.8f, -2.4f, 4.2f, 0.35f, 0.0f, 0.25f));
        m.addOrReplaceChild("knotR",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.4f, 0.0f, 0.0f, 0.8f, 2.8f, 0.5f),
                PartPose.offsetAndRotation(0.8f, -2.4f, 4.2f, 0.35f, 0.0f, -0.25f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float breeze = sin(s.ageInTicks * 0.10f) * 0.08f + s.walkSpeed * 0.25f;
        this.knotL.xRot = 0.35f + breeze;
        this.knotR.xRot = 0.35f + breeze * 0.85f;
    }
}
