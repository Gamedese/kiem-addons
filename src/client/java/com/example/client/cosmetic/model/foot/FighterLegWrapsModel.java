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
 * Fighter Leg Wraps (256x256 HD):
 * Layered off-white & crimson athletic martial-arts wraps rising up to the knees.
 */
public class FighterLegWrapsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart tieTail1;
    private final ModelPart tieTail2;

    public FighterLegWrapsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.tieTail1 = root.getChild("tieTail1");
        this.tieTail2 = root.getChild("tieTail2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("shinWraps",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8f, 18f, -8f, 16f, 26f, 16f, new CubeDeformation(0.75f)),
                PartPose.ZERO);

        root.addOrReplaceChild("kneeBand",
                CubeListBuilder.create().texOffs(0, 46)
                        .addBox(-8f, 17f, -8f, 16f, 4f, 16f, new CubeDeformation(1.10f)),
                PartPose.ZERO);

        root.addOrReplaceChild("ankleBand",
                CubeListBuilder.create().texOffs(0, 46)
                        .addBox(-8f, 40f, -8f, 16f, 4f, 16f, new CubeDeformation(1.05f)),
                PartPose.ZERO);

        root.addOrReplaceChild("toeWrap",
                CubeListBuilder.create().texOffs(0, 70)
                        .addBox(-8f, 43f, -9.0f, 16f, 3f, 4f, new CubeDeformation(0.35f)),
                PartPose.ZERO);

        root.addOrReplaceChild("tieTail1",
                CubeListBuilder.create().texOffs(70, 0)
                        .addBox(-0.5f, 0f, -1f, 1f, 7f, 2f),
                PartPose.offsetAndRotation(8.6f, 19.5f, 0.8f, 0.24f, 0f, 0.14f));

        root.addOrReplaceChild("tieTail2",
                CubeListBuilder.create().texOffs(70, 0)
                        .addBox(-0.5f, 0f, -1f, 1f, 7f, 2f),
                PartPose.offsetAndRotation(8.6f, 19.5f, -0.8f, -0.18f, 0f, 0.20f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float side = s.isLeftPart ? 1.0f : -1.0f;
        float speed = Math.min(s.walkSpeed, 1.5f);
        float flutter = sin(s.ageInTicks * 0.14f) * (0.04f + speed * 0.14f);

        tieTail1.x = side * 8.6f;
        tieTail2.x = side * 8.6f;
        tieTail1.zRot = side * (0.14f + flutter);
        tieTail2.zRot = side * (0.20f - flutter * 0.8f);
    }
}
