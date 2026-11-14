package com.example.client.cosmetic.model.foot;

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
 * Hermes Kanatli Cizmeler (Ayak Kozmetigi).
 * Inci beyazi ve cilali altin voxel cizme konclari (y = 7.0..12.15f) ile
 * dis ayak bileginde yuruyus, kosu ve ucusta cirpan katmanli kanatlar.
 */
public class HermesBootsModel extends CosmeticModel {
    private final ModelPart wingAnchor;
    private final ModelPart featherUpper;
    private final ModelPart featherMid;
    private final ModelPart featherLower;

    public HermesBootsModel(ModelPart root) {
        super(root);
        this.wingAnchor = root.getChild("wingAnchor");
        this.featherUpper = this.wingAnchor.getChild("featherUpper");
        this.featherMid = this.wingAnchor.getChild("featherMid");
        this.featherLower = this.wingAnchor.getChild("featherLower");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Ana cizme govdesi: bacak alt kismini sarar (y = 7.0..12.15f)
        root.addOrReplaceChild("bootBase",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2f, 7f, -2f, 4f, 5f, 4f, new CubeDeformation(0.22f)),
                PartPose.ZERO);

        // Ust altin yaka seridi
        root.addOrReplaceChild("collar",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-2f, 7f, -2f, 4f, 1f, 4f, new CubeDeformation(0.36f)),
                PartPose.ZERO);

        // Bilek altin kemer detayi
        root.addOrReplaceChild("ankleStrap",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2f, 9f, -2f, 4f, 1f, 4f, new CubeDeformation(0.30f)),
                PartPose.ZERO);

        // On burun ve taban cikintisi
        root.addOrReplaceChild("toeCap",
                CubeListBuilder.create().texOffs(18, 0)
                        .addBox(-2f, 10.1f, -2.45f, 4f, 2f, 1f, new CubeDeformation(0.10f)),
                PartPose.ZERO);

        // Dis ayak bilegi kanat kok noktasi (X konumu setupAnim icinde sol/sag bacaga gore ayarlanir)
        PartDefinition wingAnchor = root.addOrReplaceChild("wingAnchor",
                CubeListBuilder.create().texOffs(20, 4)
                        .addBox(-0.5f, -1.5f, -0.5f, 1f, 2f, 2f),
                PartPose.offset(2.3f, 8.7f, 0.2f));

        // Katmanli tuy kanatlar: ust, orta, alt
        wingAnchor.addOrReplaceChild("featherUpper",
                CubeListBuilder.create().texOffs(20, 10)
                        .addBox(-0.5f, -1.0f, 0f, 1f, 1f, 4f),
                PartPose.offsetAndRotation(0f, -1.4f, 0.8f, 0.36f, 0f, 0f));

        wingAnchor.addOrReplaceChild("featherMid",
                CubeListBuilder.create().texOffs(20, 16)
                        .addBox(-0.5f, -0.5f, 0f, 1f, 1f, 3f),
                PartPose.offsetAndRotation(0f, -0.5f, 1.0f, 0.14f, 0f, 0f));

        wingAnchor.addOrReplaceChild("featherLower",
                CubeListBuilder.create().texOffs(20, 21)
                        .addBox(-0.5f, -0.5f, 0f, 1f, 1f, 2f),
                PartPose.offsetAndRotation(0f, 0.4f, 0.8f, -0.06f, 0f, 0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float side = s.isLeftPart ? 1.0f : -1.0f;
        wingAnchor.x = side * 2.3f;

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.6f);
        float fly = s.flyAmount;

        // Yurume, kosu ve ucus durumuna gore kanat cirpma frekansi ve genligi
        float freq = 0.16f + speed * 0.34f + fly * 0.48f;
        float amp = 0.06f + speed * 0.28f + fly * 0.42f;
        float baseTilt = 0.24f + speed * 0.16f + fly * 0.22f;
        float wave = sin(t * freq);
        float flap = baseTilt + wave * amp;

        // Sol bacakta +zRot, sag bacakta -zRot kanadi disari ve yukari acar
        wingAnchor.yRot = side * (0.32f + speed * 0.08f);
        wingAnchor.zRot = side * flap;

        // Tuy katmanlarinda ikincil dalgalanma
        float trail = cos(t * freq - 0.5f) * (0.04f + speed * 0.10f + fly * 0.14f);
        featherUpper.xRot = 0.36f + trail;
        featherMid.xRot = 0.14f + trail * 0.7f;
        featherLower.xRot = -0.06f + trail * 0.4f;
    }
}
