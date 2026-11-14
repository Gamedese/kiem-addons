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
 * Kemersiz, dogrudan bel arkasina (y=10.5, z=2.1) baglanan 5 segmentli
 * siyah-kizil iblis kuyrugu ve ucunda ok basi (spade) ucu.
 * 0.25x olcek, 256x256 HD doku: waist_demon_tail.png.
 */
public class DemonTailModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart spade;

    public DemonTailModel(ModelPart root) {
        super(root);
        this.root = root;
        this.tail1 = root.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.tail5 = this.tail4.getChild("tail5");
        this.spade = this.tail5.getChild("spade");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Attached directly to lower back: y=10.5 -> 42.0, z=2.1 -> 8.4
        PartDefinition t1 = root.addOrReplaceChild("tail1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3f, -3f, -1f, 6, 6, 14),
                PartPose.offsetAndRotation(0f, 42.0f, 8.4f, -0.30f, 0f, 0f));

        PartDefinition t2 = t1.addOrReplaceChild("tail2",
                CubeListBuilder.create()
                        .texOffs(48, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 14),
                PartPose.offsetAndRotation(0f, 0f, 12.2f, 0.22f, 0f, 0f));

        PartDefinition t3 = t2.addOrReplaceChild("tail3",
                CubeListBuilder.create()
                        .texOffs(92, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 14),
                PartPose.offsetAndRotation(0f, 0f, 13.0f, 0.28f, 0f, 0f));

        PartDefinition t4 = t3.addOrReplaceChild("tail4",
                CubeListBuilder.create()
                        .texOffs(136, 0)
                        .addBox(-1.5f, -1.5f, 0f, 3, 3, 12),
                PartPose.offsetAndRotation(0f, 0f, 13.0f, 0.24f, 0f, 0f));

        PartDefinition t5 = t4.addOrReplaceChild("tail5",
                CubeListBuilder.create()
                        .texOffs(174, 0)
                        .addBox(-1.5f, -1.5f, 0f, 3, 3, 10),
                PartPose.offsetAndRotation(0f, 0f, 11.2f, 0.18f, 0f, 0f));

        PartDefinition spade = t5.addOrReplaceChild("spade",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-3f, -1.5f, 0f, 6, 3, 10)
                        .texOffs(40, 32)
                        .addBox(-6f, -1f, 1f, 4, 2, 6)
                        .texOffs(68, 32)
                        .addBox(2f, -1f, 1f, 4, 2, 6)
                        .texOffs(96, 32)
                        .addBox(-1.5f, -1f, 9f, 3, 2, 6),
                PartPose.offsetAndRotation(0f, 0f, 9.2f, 0.10f, 0f, 0f));

        // Dorsal ridge fin on spade
        spade.addOrReplaceChild("spadeRidge",
                CubeListBuilder.create()
                        .texOffs(120, 32)
                        .addBox(-1f, -2.5f, 1f, 2, 5, 7),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.2f);
        float tuck = s.crouchAmount;

        float idlePhase = t * 0.10f;
        float movePhase = s.walkPos * 0.78f;

        float wag1 = sin(idlePhase) * 0.10f + sin(movePhase) * 0.18f * speed;
        float wag2 = sin(idlePhase - 0.55f) * 0.14f + sin(movePhase - 0.55f) * 0.24f * speed;
        float wag3 = sin(idlePhase - 1.10f) * 0.18f + sin(movePhase - 1.10f) * 0.30f * speed;
        float wag4 = sin(idlePhase - 1.65f) * 0.22f + sin(movePhase - 1.65f) * 0.34f * speed;
        float wag5 = sin(idlePhase - 2.20f) * 0.26f + sin(movePhase - 2.20f) * 0.38f * speed;

        float bob = sin(t * 0.12f) * 0.04f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.12f * speed;

        tail1.xRot = -0.30f - tuck * 0.25f + bob * 0.5f;
        tail1.yRot = wag1;

        tail2.xRot = 0.22f - tuck * 0.08f + bob * 0.3f;
        tail2.yRot = wag2;

        tail3.xRot = 0.28f + tuck * 0.06f;
        tail3.yRot = wag3;

        tail4.xRot = 0.24f + tuck * 0.05f;
        tail4.yRot = wag4;

        tail5.xRot = 0.18f + tuck * 0.04f;
        tail5.yRot = wag5;

        spade.xRot = 0.10f + sin(idlePhase - 2.6f) * 0.08f;
        spade.yRot = sin(idlePhase - 2.6f) * 0.12f + sin(movePhase - 2.6f) * 0.18f * speed;
    }
}
