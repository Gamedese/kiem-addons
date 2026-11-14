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
 * Kemersiz, dogrudan bel arkasina (y=10.5, z=2.1) takilan gri ve gumus-beyaz
 * katmanli pofuduk kurt kuyrugu.
 * 0.25x olcek, 256x256 HD doku: waist_wolf_tail.png.
 */
public class WolfTailModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;

    public WolfTailModel(ModelPart root) {
        super(root);
        this.root = root;
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
                        .addBox(-4f, -4f, 0f, 8, 8, 12),
                PartPose.offsetAndRotation(0f, 42.0f, 8.4f, -0.45f, 0f, 0f));

        PartDefinition t2 = t1.addOrReplaceChild("tail2",
                CubeListBuilder.create()
                        .texOffs(44, 0)
                        .addBox(-5f, -5f, 0f, 10, 10, 14, new CubeDeformation(0.4f)),
                PartPose.offsetAndRotation(0f, 0f, 10.5f, 0.14f, 0f, 0f));

        PartDefinition t3 = t2.addOrReplaceChild("tail3",
                CubeListBuilder.create()
                        .texOffs(96, 0)
                        .addBox(-4.5f, -4.5f, 0f, 9, 9, 14, new CubeDeformation(0.3f)),
                PartPose.offsetAndRotation(0f, 0f, 12.5f, 0.18f, 0f, 0f));

        t3.addOrReplaceChild("tail4",
                CubeListBuilder.create()
                        .texOffs(148, 0)
                        .addBox(-3f, -3f, 0f, 6, 6, 12),
                PartPose.offsetAndRotation(0f, 0f, 12.8f, 0.16f, 0f, 0f));

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

        float idle = t * 0.08f;
        float move = s.walkPos * 0.68f;

        tail1.xRot = -0.45f - tuck * 0.28f + sin(t * 0.10f) * 0.03f;
        tail1.yRot = sin(idle) * 0.07f + sin(move) * 0.14f * speed;

        tail2.xRot = 0.14f + tuck * 0.06f;
        tail2.yRot = sin(idle - 0.5f) * 0.10f + sin(move - 0.5f) * 0.18f * speed;

        tail3.xRot = 0.18f + tuck * 0.08f;
        tail3.yRot = sin(idle - 1.0f) * 0.12f + sin(move - 1.0f) * 0.22f * speed;

        tail4.xRot = 0.16f + tuck * 0.06f;
        tail4.yRot = sin(idle - 1.5f) * 0.14f + sin(move - 1.5f) * 0.25f * speed;
    }
}
