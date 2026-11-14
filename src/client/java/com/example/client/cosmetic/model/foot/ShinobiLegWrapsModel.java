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
 * Shinobi Bacak Sargilari (Ayak Kozmetigi).
 * Koyu komur rengi kaval sargilari (y = 6.0..11.8f) ve yalnizca sag bacak dis
 * uylugunda (y = 2.0..4.0f) deri kayis, kompakt alet kesesi ve kilifli kunai.
 */
public class ShinobiLegWrapsModel extends CosmeticModel {
    private final ModelPart thighGear;
    private final ModelPart kunaiSheath;

    public ShinobiLegWrapsModel(ModelPart root) {
        super(root);
        this.thighGear = root.getChild("thighGear");
        this.kunaiSheath = this.thighGear.getChild("kunaiSheath");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Ana kaval bandaj sargisi (y = 6.0..11.2f)
        root.addOrReplaceChild("shinWraps",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2f, 6f, -2f, 4f, 5f, 4f, new CubeDeformation(0.22f)),
                PartPose.ZERO);

        // Alt bilek / tabi sargisi (y = 10.6..11.9f)
        root.addOrReplaceChild("ankleTabi",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-2f, 10.8f, -2f, 4f, 1f, 4f, new CubeDeformation(0.18f)),
                PartPose.ZERO);

        // Ust sargi dugum seridi
        root.addOrReplaceChild("topBand",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2f, 6.2f, -2f, 4f, 1f, 4f, new CubeDeformation(0.30f)),
                PartPose.ZERO);

        // On tabi burun koruyucu
        root.addOrReplaceChild("toeGuard",
                CubeListBuilder.create().texOffs(18, 0)
                        .addBox(-2f, 10.8f, -2.4f, 4f, 1f, 1f, new CubeDeformation(0.10f)),
                PartPose.ZERO);

        // Sag ust uyluk ekipman grubu (!s.isLeftPart durumunda gorunur, dis taraf -X)
        PartDefinition thighGear = root.addOrReplaceChild("thighGear",
                CubeListBuilder.create().texOffs(0, 22)
                        .addBox(-2f, 2f, -2f, 4f, 2f, 4f, new CubeDeformation(0.24f)),
                PartPose.ZERO);

        // Kompakt deri alet kesesi (sag dis-on uyluk)
        thighGear.addOrReplaceChild("pouch",
                CubeListBuilder.create().texOffs(18, 4)
                        .addBox(-0.8f, -1f, -1f, 1f, 2f, 2f, new CubeDeformation(0.15f)),
                PartPose.offsetAndRotation(-2.3f, 2.7f, -0.7f, 0f, 0f, 0.06f));

        // Kilif icinde kunai (sag dis-arka uyluk)
        PartDefinition kunaiSheath = thighGear.addOrReplaceChild("kunaiSheath",
                CubeListBuilder.create().texOffs(18, 10)
                        .addBox(-0.5f, -1.5f, -0.5f, 1f, 3f, 1f, new CubeDeformation(0.10f)),
                PartPose.offsetAndRotation(-2.35f, 3.0f, 0.9f, 0.20f, 0f, 0.08f));

        // Kunai sargili kabzasi
        PartDefinition kunaiHandle = kunaiSheath.addOrReplaceChild("kunaiHandle",
                CubeListBuilder.create().texOffs(24, 10)
                        .addBox(-0.5f, -1.5f, -0.5f, 1f, 2f, 1f, new CubeDeformation(-0.15f)),
                PartPose.offset(0f, -1.8f, 0f));

        // Kunai halka basi
        kunaiHandle.addOrReplaceChild("kunaiRing",
                CubeListBuilder.create().texOffs(24, 15)
                        .addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, new CubeDeformation(-0.05f)),
                PartPose.offset(0f, -1.6f, 0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        // Uyluk kayisi, kese ve kunai yalnizca sag bacakta (!isLeftPart) gosterilir
        thighGear.visible = !s.isLeftPart;

        if (thighGear.visible) {
            float speed = Math.min(s.walkSpeed, 1.5f);
            float bounce = sin(s.walkPos * 1.3324f) * 0.04f * speed;
            kunaiSheath.xRot = 0.20f + bounce;
        }
    }
}
