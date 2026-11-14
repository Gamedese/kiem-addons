package com.example.client.cosmetic.model.waist;

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
 * Kemersiz, dogrudan bel arkasina (x=0, y=10.5, z=2.1) takilan
 * 4 segmentli pofuduk turuncu-beyaz uclu kitsune tilki kuyrugu.
 */
public class KitsuneTailModel extends CosmeticModel {
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;

    public KitsuneTailModel(ModelPart root) {
        super(root);
        this.tail1 = root.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition t1 = root.addOrReplaceChild("tail1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5f, -1.5f, 0f, 3f, 3f, 4f),
                PartPose.offsetAndRotation(0f, 10.5f, 2.1f, -0.35f, 0f, 0f));

        PartDefinition t2 = t1.addOrReplaceChild("tail2",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-2f, -2f, 0f, 4f, 4f, 5f),
                PartPose.offsetAndRotation(0f, 0f, 3.6f, 0.25f, 0f, 0f));

        PartDefinition t3 = t2.addOrReplaceChild("tail3",
                CubeListBuilder.create()
                        .texOffs(20, 10)
                        .addBox(-2f, -2f, 0f, 4f, 4f, 5f, new CubeDeformation(0.25f)),
                PartPose.offsetAndRotation(0f, 0f, 4.6f, 0.35f, 0f, 0f));

        t3.addOrReplaceChild("tail4",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-1.5f, -1.5f, 0f, 3f, 3f, 4f),
                PartPose.offsetAndRotation(0f, 0f, 4.7f, 0.30f, 0f, 0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.2f);
        float tuck = s.crouchAmount;

        // Gentle idle sway + faster energetic wag when moving
        float idlePhase = t * 0.09f;
        float movePhase = s.walkPos * 0.72f;

        float wag1 = sin(idlePhase) * 0.08f + sin(movePhase) * 0.16f * speed;
        float wag2 = sin(idlePhase - 0.65f) * 0.11f + sin(movePhase - 0.65f) * 0.20f * speed;
        float wag3 = sin(idlePhase - 1.30f) * 0.14f + sin(movePhase - 1.30f) * 0.24f * speed;
        float wag4 = sin(idlePhase - 1.95f) * 0.16f + sin(movePhase - 1.95f) * 0.28f * speed;

        float bob = sin(t * 0.11f) * 0.03f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.12f * speed;

        tail1.xRot = -0.35f - tuck * 0.30f + bob * 0.6f;
        tail1.yRot = wag1;

        tail2.xRot = 0.25f - tuck * 0.10f + bob * 0.4f;
        tail2.yRot = wag2;

        tail3.xRot = 0.35f + tuck * 0.08f;
        tail3.yRot = wag3;

        tail4.xRot = 0.30f + tuck * 0.05f;
        tail4.yRot = wag4;
    }
}
