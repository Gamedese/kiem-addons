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
 * Classic rounded straw pirate hat with vibrant red ribbon band and wide brim (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_straw_pirate.png
 */
public class StrawPirateHatModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart hatRoot;

    public StrawPirateHatModel(ModelPart root) {
        super(root);
        this.root = root;
        this.hatRoot = root.getChild("hatRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create()
                        // Wide rounded straw brim (staggered Y offsets prevent z-fighting)
                        .texOffs(0, 0).addBox(-24.0f, -1.0f, -24.0f, 48.0f, 2.0f, 48.0f)
                        .texOffs(0, 54).addBox(-18.0f, -0.94f, -28.0f, 36.0f, 2.0f, 56.0f)
                        .texOffs(0, 116).addBox(-28.0f, -0.88f, -18.0f, 56.0f, 2.0f, 36.0f)
                        // Vibrant red ribbon band
                        .texOffs(0, 158).addBox(-17.5f, -5.0f, -17.5f, 35.0f, 4.0f, 35.0f)
                        // Rounded straw dome layers
                        .texOffs(0, 200).addBox(-16.5f, -9.5f, -16.5f, 33.0f, 5.0f, 33.0f)
                        .texOffs(136, 200).addBox(-14.0f, -12.3f, -14.0f, 28.0f, 3.0f, 28.0f)
                        .texOffs(144, 158).addBox(-10.0f, -14.1f, -10.0f, 20.0f, 2.0f, 20.0f),
                PartPose.offsetAndRotation(0.0f, -24.0f, 0.0f, -0.06f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float walkBob = sin(s.walkPos * 0.6662f) * 0.025f * s.walkSpeed;
        float breeze = sin(s.ageInTicks * 0.05f) * 0.012f;
        hatRoot.xRot = -0.06f + walkBob + breeze;
        hatRoot.zRot = cos(s.walkPos * 0.6662f) * 0.015f * s.walkSpeed;
    }
}
