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
 * Traditional white & crimson painted Japanese fox mask tilted cleanly on the side-top of the head (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_kitsune_mask.png
 */
public class KitsuneMaskModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart maskRoot;

    public KitsuneMaskModel(ModelPart root) {
        super(root);
        this.root = root;
        this.maskRoot = root.getChild("maskRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Upper-right forehead/temple corner placement (0.25x scale -> x = +2.85f, y = -7.1f, z = -3.8f MC px)
        PartDefinition maskRoot = root.addOrReplaceChild("maskRoot",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-10.0f, -8.0f, -2.0f, 20.0f, 14.0f, 3.0f)
                        .texOffs(0, 20)
                        .addBox(-7.0f, 5.5f, -2.1f, 14.0f, 4.0f, 3.0f)
                        .texOffs(0, 30)
                        .addBox(-4.0f, 1.0f, -5.0f, 8.0f, 6.0f, 4.0f)
                        .texOffs(0, 44)
                        .addBox(-2.0f, 1.5f, -6.5f, 4.0f, 3.0f, 2.0f)
                        .texOffs(0, 64)
                        .addBox(-10.5f, -2.0f, 0.9f, 21.0f, 2.0f, 2.0f),
                PartPose.offsetAndRotation(11.4f, -28.4f, -15.2f, -0.32f, -0.38f, 0.20f));

        maskRoot.addOrReplaceChild("earL",
                CubeListBuilder.create()
                        .texOffs(0, 52)
                        .addBox(-3.0f, -7.0f, -1.5f, 6.0f, 7.0f, 2.0f)
                        .texOffs(20, 52)
                        .addBox(-2.0f, -11.8f, -1.3f, 4.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(6.5f, -7.8f, -0.4f, 0.0f, 0.0f, 0.14f));

        maskRoot.addOrReplaceChild("earR",
                CubeListBuilder.create()
                        .texOffs(0, 52)
                        .addBox(-3.0f, -7.0f, -1.5f, 6.0f, 7.0f, 2.0f)
                        .texOffs(20, 52)
                        .addBox(-2.0f, -11.8f, -1.3f, 4.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(-6.5f, -7.8f, -0.4f, 0.0f, 0.0f, -0.14f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float walkBob = sin(s.walkPos * 0.6662f) * 0.025f * s.walkSpeed;
        maskRoot.xRot = -0.32f + walkBob;
        maskRoot.yRot = -0.38f;
        maskRoot.zRot = 0.20f + sin(s.ageInTicks * 0.05f) * 0.015f;
    }
}
