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
 * Kizil kumas shinobi alin bandi: metalik gumus alin plakasi ve arkada dalgalanan cift serit.
 * UV duzeni: assets/modid/textures/cosmetic/hat_shinobi.png (64x64).
 */
public class ShinobiHeadbandModel extends CosmeticModel {
    private final ModelPart tailL1, tailL2, tailL3;
    private final ModelPart tailR1, tailR2, tailR3;

    public ShinobiHeadbandModel(ModelPart root) {
        super(root);
        ModelPart band = root.getChild("band");
        this.tailL1 = band.getChild("tailL1");
        this.tailL2 = tailL1.getChild("tailL2");
        this.tailL3 = tailL2.getChild("tailL3");
        this.tailR1 = band.getChild("tailR1");
        this.tailR2 = tailR1.getChild("tailR2");
        this.tailR3 = tailR2.getChild("tailR3");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition band = root.addOrReplaceChild("band",
                CubeListBuilder.create().texOffs(1, 1).addBox(-4.3f, -6.5f, -4.3f, 8.6f, 2.0f, 8.6f),
                PartPose.ZERO);

        // -Z on yuzde metalik gumus alin plakasi
        band.addOrReplaceChild("plate",
                CubeListBuilder.create().texOffs(0, 14).addBox(-2.5f, -6.5f, -4.8f, 5.0f, 2.0f, 1.0f),
                PartPose.ZERO);

        // +Z arka yuzde dugum
        band.addOrReplaceChild("knot",
                CubeListBuilder.create().texOffs(14, 14).addBox(-1.0f, -6.5f, 4.0f, 2.0f, 2.0f, 1.0f),
                PartPose.ZERO);

        // Sol ve sag 3 segmentli kuyruk seritleri
        PartDefinition tl1 = band.addOrReplaceChild("tailL1",
                CubeListBuilder.create().texOffs(0, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 3.0f, 0.5f),
                PartPose.offsetAndRotation(0.9f, -5.5f, 4.6f, 0.25f, 0.12f, 0.14f));
        PartDefinition tl2 = tl1.addOrReplaceChild("tailL2",
                CubeListBuilder.create().texOffs(8, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 3.0f, 0.5f),
                PartPose.offset(0.0f, 2.8f, 0.0f));
        tl2.addOrReplaceChild("tailL3",
                CubeListBuilder.create().texOffs(16, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 2.5f, 0.5f),
                PartPose.offset(0.0f, 2.8f, 0.0f));

        PartDefinition tr1 = band.addOrReplaceChild("tailR1",
                CubeListBuilder.create().texOffs(24, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 3.0f, 0.5f),
                PartPose.offsetAndRotation(-0.9f, -5.5f, 4.6f, 0.22f, -0.12f, -0.14f));
        PartDefinition tr2 = tr1.addOrReplaceChild("tailR2",
                CubeListBuilder.create().texOffs(32, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 3.0f, 0.5f),
                PartPose.offset(0.0f, 2.8f, 0.0f));
        tr2.addOrReplaceChild("tailR3",
                CubeListBuilder.create().texOffs(40, 20).addBox(-0.75f, 0.0f, 0.0f, 1.5f, 2.5f, 0.5f),
                PartPose.offset(0.0f, 2.8f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float speed = Math.min(1.2f, s.walkSpeed + s.flyAmount * 0.9f);
        float baseLift = 0.24f + speed * 0.55f;
        float waveFreq = 0.16f + speed * 0.18f;
        float waveAmp = 0.10f + speed * 0.18f;

        tailL1.xRot = baseLift + sin(t * waveFreq) * waveAmp;
        tailL2.xRot = 0.14f + sin(t * waveFreq - 0.9f) * (waveAmp * 1.25f);
        tailL3.xRot = 0.14f + sin(t * waveFreq - 1.8f) * (waveAmp * 1.45f);
        tailL1.zRot = 0.14f + cos(t * 0.11f) * 0.06f;

        tailR1.xRot = baseLift + sin(t * waveFreq + 0.6f) * waveAmp;
        tailR2.xRot = 0.14f + sin(t * waveFreq - 0.3f) * (waveAmp * 1.25f);
        tailR3.xRot = 0.14f + sin(t * waveFreq - 1.2f) * (waveAmp * 1.45f);
        tailR1.zRot = -0.14f - cos(t * 0.11f + 0.8f) * 0.06f;
    }
}
