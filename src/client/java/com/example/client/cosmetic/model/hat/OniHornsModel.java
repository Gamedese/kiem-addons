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
 * Curved multi-segment crimson-to-black oni horns emerging from forehead/temples (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_oni_horns.png
 */
public class OniHornsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart hornL;
    private final ModelPart hornR;

    public OniHornsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.hornL = root.getChild("hornL");
        this.hornR = root.getChild("hornR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        buildHorn(root, "hornL", 1.0f);
        buildHorn(root, "hornR", -1.0f);

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void buildHorn(PartDefinition root, String name, float side) {
        // y = -7.5f (-30.0f), z = -3.8f (-15.2f)
        PartDefinition base = root.addOrReplaceChild(name,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5f, -1.5f, -3.5f, 7.0f, 2.0f, 7.0f)
                        .texOffs(0, 12)
                        .addBox(-3.0f, -6.0f, -3.0f, 6.0f, 6.0f, 6.0f),
                PartPose.offsetAndRotation(side * 9.5f, -30.0f, -15.2f, 0.28f, side * -0.14f, side * 0.24f));

        PartDefinition seg1 = base.addOrReplaceChild("seg1",
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(-2.5f, -6.0f, -2.5f, 5.0f, 6.0f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -5.5f, 0.0f, -0.18f, 0.0f, side * 0.06f));

        PartDefinition seg2 = seg1.addOrReplaceChild("seg2",
                CubeListBuilder.create()
                        .texOffs(0, 42)
                        .addBox(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 4.0f),
                PartPose.offsetAndRotation(0.0f, -5.5f, 0.0f, -0.24f, 0.0f, side * -0.10f));

        PartDefinition seg3 = seg2.addOrReplaceChild("seg3",
                CubeListBuilder.create()
                        .texOffs(0, 56)
                        .addBox(-1.5f, -5.0f, -1.5f, 3.0f, 5.0f, 3.0f),
                PartPose.offsetAndRotation(0.0f, -5.5f, 0.0f, -0.26f, 0.0f, side * -0.14f));

        seg3.addOrReplaceChild("tip",
                CubeListBuilder.create()
                        .texOffs(0, 68)
                        .addBox(-1.0f, -5.0f, -1.0f, 2.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(0.0f, -4.5f, 0.0f, -0.22f, 0.0f, side * -0.12f));
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float microFlex = sin(s.ageInTicks * 0.06f) * 0.012f;
        hornL.xRot = 0.28f + microFlex;
        hornR.xRot = 0.28f + microFlex;
    }
}
