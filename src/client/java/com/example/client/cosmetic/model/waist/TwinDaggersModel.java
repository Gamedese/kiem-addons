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
 * Her iki kalcaya (x = +-4.3, y = 10.2) acili sekilde takilan koyu celik ve
 * altin kabzali ikiz hancerler ve deri bel kemeri.
 * 0.25x olcek, 256x256 HD doku: waist_twin_daggers.png.
 */
public class TwinDaggersModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart leftDagger;
    private final ModelPart rightDagger;

    public TwinDaggersModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftDagger = root.getChild("leftDagger");
        this.rightDagger = root.getChild("rightDagger");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Slim dark leather waist belt + gold buckle
        PartDefinition belt = root.addOrReplaceChild("belt",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-16f, 38f, -8f, 32, 6, 16, new CubeDeformation(1.2f)),
                PartPose.ZERO);

        belt.addOrReplaceChild("buckle",
                CubeListBuilder.create()
                        .texOffs(104, 0)
                        .addBox(-3f, 38f, -10f, 6, 6, 2),
                PartPose.ZERO);

        // Left hip dagger (x = +4.3 -> +17.2, y = 10.2 -> 40.8)
        PartDefinition left = root.addOrReplaceChild("leftDagger",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(17.2f, 40.8f, 0.5f, -0.52f, 0.08f, -0.10f));
        addDaggerParts(left, false);

        // Right hip dagger (x = -4.3 -> -17.2, y = 10.2 -> 40.8)
        PartDefinition right = root.addOrReplaceChild("rightDagger",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-17.2f, 40.8f, 0.5f, -0.52f, -0.08f, 0.10f));
        addDaggerParts(right, true);

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void addDaggerParts(PartDefinition parent, boolean mirror) {
        CubeListBuilder sheath = mirror ? CubeListBuilder.create().mirror() : CubeListBuilder.create();
        parent.addOrReplaceChild("sheath",
                sheath.texOffs(0, 32)
                        .addBox(-2f, -2f, -4f, 4, 4, 24),
                PartPose.ZERO);

        parent.addOrReplaceChild("chape",
                CubeListBuilder.create()
                        .texOffs(64, 32)
                        .addBox(-2f, -2f, 20f, 4, 4, 4, new CubeDeformation(0.25f)),
                PartPose.ZERO);

        parent.addOrReplaceChild("locket",
                CubeListBuilder.create()
                        .texOffs(84, 32)
                        .addBox(-2f, -2f, -5f, 4, 4, 3, new CubeDeformation(0.30f)),
                PartPose.ZERO);

        parent.addOrReplaceChild("crossguard",
                CubeListBuilder.create()
                        .texOffs(104, 32)
                        .addBox(-5f, -3f, -7f, 10, 6, 2),
                PartPose.ZERO);

        parent.addOrReplaceChild("hilt",
                CubeListBuilder.create()
                        .texOffs(132, 32)
                        .addBox(-1.5f, -1.5f, -17f, 3, 3, 10),
                PartPose.ZERO);

        parent.addOrReplaceChild("pommel",
                CubeListBuilder.create()
                        .texOffs(164, 32)
                        .addBox(-2.5f, -2.5f, -20f, 5, 5, 3),
                PartPose.ZERO);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float speed = Math.min(s.walkSpeed, 1.2f);
        float walkBob = sin(s.walkPos * 1.3324f) * 0.05f * speed;
        float walkSwing = cos(s.walkPos * 0.6662f) * 0.05f * speed;
        float idle = sin(s.ageInTicks * 0.08f) * 0.015f;

        leftDagger.xRot = -0.52f + walkBob + idle + s.crouchAmount * 0.14f;
        leftDagger.zRot = -0.10f + walkSwing;
        leftDagger.y = 40.8f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.6f * speed;

        rightDagger.xRot = -0.52f - walkBob + idle + s.crouchAmount * 0.14f;
        rightDagger.zRot = 0.10f - walkSwing;
        rightDagger.y = 40.8f + Math.abs(cos(s.walkPos * 0.6662f)) * 0.6f * speed;
    }
}
