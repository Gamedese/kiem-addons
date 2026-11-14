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
 * Kemersiz, dogrudan bel arkasina takilan zit fazda salinan
 * siyah ve beyaz uclu ikiz Nekomata kedi kuyruklari.
 * 0.25x olcek, 256x256 HD doku: waist_nekomata_tails.png.
 */
public class NekomataTailsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart left1;
    private final ModelPart left2;
    private final ModelPart left3;
    private final ModelPart left4;
    private final ModelPart right1;
    private final ModelPart right2;
    private final ModelPart right3;
    private final ModelPart right4;

    public NekomataTailsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.left1 = root.getChild("left1");
        this.left2 = this.left1.getChild("left2");
        this.left3 = this.left2.getChild("left3");
        this.left4 = this.left3.getChild("left4");

        this.right1 = root.getChild("right1");
        this.right2 = this.right1.getChild("right2");
        this.right3 = this.right2.getChild("right3");
        this.right4 = this.right3.getChild("right4");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Left tail
        PartDefinition l1 = root.addOrReplaceChild("left1",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 12),
                PartPose.offsetAndRotation(2.0f, 42.0f, 8.4f, -0.32f, 0.28f, 0f));

        PartDefinition l2 = l1.addOrReplaceChild("left2",
                CubeListBuilder.create()
                        .texOffs(40, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 13, new CubeDeformation(0.2f)),
                PartPose.offsetAndRotation(0f, 0f, 11.2f, 0.34f, 0.14f, 0f));

        PartDefinition l3 = l2.addOrReplaceChild("left3",
                CubeListBuilder.create()
                        .texOffs(80, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 13, new CubeDeformation(0.3f)),
                PartPose.offsetAndRotation(0f, 0f, 12.2f, 0.40f, -0.06f, 0f));

        l3.addOrReplaceChild("left4",
                CubeListBuilder.create()
                        .texOffs(120, 0)
                        .addBox(-2f, -2f, 0f, 4, 4, 11),
                PartPose.offsetAndRotation(0f, 0f, 12.2f, 0.36f, -0.10f, 0f));

        // Right tail (mirrored)
        PartDefinition r1 = root.addOrReplaceChild("right1",
                CubeListBuilder.create().mirror()
                        .texOffs(0, 32)
                        .addBox(-2f, -2f, 0f, 4, 4, 12),
                PartPose.offsetAndRotation(-2.0f, 42.0f, 8.4f, -0.32f, -0.28f, 0f));

        PartDefinition r2 = r1.addOrReplaceChild("right2",
                CubeListBuilder.create().mirror()
                        .texOffs(40, 32)
                        .addBox(-2f, -2f, 0f, 4, 4, 13, new CubeDeformation(0.2f)),
                PartPose.offsetAndRotation(0f, 0f, 11.2f, 0.34f, -0.14f, 0f));

        PartDefinition r3 = r2.addOrReplaceChild("right3",
                CubeListBuilder.create().mirror()
                        .texOffs(80, 32)
                        .addBox(-2f, -2f, 0f, 4, 4, 13, new CubeDeformation(0.3f)),
                PartPose.offsetAndRotation(0f, 0f, 12.2f, 0.40f, 0.06f, 0f));

        r3.addOrReplaceChild("right4",
                CubeListBuilder.create().mirror()
                        .texOffs(120, 32)
                        .addBox(-2f, -2f, 0f, 4, 4, 11),
                PartPose.offsetAndRotation(0f, 0f, 12.2f, 0.36f, 0.10f, 0f));

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

        float idleL = t * 0.09f;
        float idleR = t * 0.09f + (float) Math.PI;
        float moveL = s.walkPos * 0.72f;
        float moveR = s.walkPos * 0.72f + (float) Math.PI;

        left1.xRot = -0.32f - tuck * 0.25f + sin(idleL) * 0.05f;
        left1.yRot = 0.28f + sin(idleL) * 0.09f + sin(moveL) * 0.14f * speed;
        left2.xRot = 0.34f + sin(idleL - 0.6f) * 0.06f;
        left2.yRot = 0.14f + sin(idleL - 0.6f) * 0.13f + sin(moveL - 0.6f) * 0.18f * speed;
        left3.xRot = 0.40f + sin(idleL - 1.2f) * 0.07f;
        left3.yRot = -0.06f + sin(idleL - 1.2f) * 0.16f + sin(moveL - 1.2f) * 0.22f * speed;
        left4.xRot = 0.36f + sin(idleL - 1.8f) * 0.08f;
        left4.yRot = -0.10f + sin(idleL - 1.8f) * 0.18f + sin(moveL - 1.8f) * 0.24f * speed;

        right1.xRot = -0.32f - tuck * 0.25f + sin(idleR) * 0.05f;
        right1.yRot = -0.28f + sin(idleR) * 0.09f + sin(moveR) * 0.14f * speed;
        right2.xRot = 0.34f + sin(idleR - 0.6f) * 0.06f;
        right2.yRot = -0.14f + sin(idleR - 0.6f) * 0.13f + sin(moveR - 0.6f) * 0.18f * speed;
        right3.xRot = 0.40f + sin(idleR - 1.2f) * 0.07f;
        right3.yRot = 0.06f + sin(idleR - 1.2f) * 0.16f + sin(moveR - 1.2f) * 0.22f * speed;
        right4.xRot = 0.36f + sin(idleR - 1.8f) * 0.08f;
        right4.yRot = 0.10f + sin(idleR - 1.8f) * 0.18f + sin(moveR - 1.8f) * 0.24f * speed;
    }
}
