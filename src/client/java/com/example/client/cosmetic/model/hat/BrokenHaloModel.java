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
 * Cracked/broken tilted dark-violet & obsidian fallen halo floating above the head (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_broken_halo.png
 */
public class BrokenHaloModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    private static final float BASE_Y = -42.5f;

    private final ModelPart root;
    private final ModelPart haloRoot;
    private final ModelPart shard0;
    private final ModelPart shard1;
    private final ModelPart shard2;

    public BrokenHaloModel(ModelPart root) {
        super(root);
        this.root = root;
        this.haloRoot = root.getChild("haloRoot");
        this.shard0 = haloRoot.getChild("shard_0");
        this.shard1 = haloRoot.getChild("shard_1");
        this.shard2 = haloRoot.getChild("shard_2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition haloRoot = root.addOrReplaceChild("haloRoot",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, BASE_Y, 0.0f, 0.22f, 0.0f, -0.26f));

        for (int i = 0; i < 8; i++) {
            // Leave gaps at segments 2 and 6 for shattered look
            if (i == 2 || i == 6) {
                continue;
            }
            float yaw = (float) (i * Math.PI / 4.0);
            float yOff = (i % 2) * 0.06f;

            if (i == 3) {
                haloRoot.addOrReplaceChild("seg_3",
                        CubeListBuilder.create()
                                .texOffs(0, 8)
                                .addBox(-6.0f, 1.5f + yOff, 17.0f, 12.0f, 2.0f, 2.0f),
                        PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, yaw, 0.24f));
            } else if (i == 5) {
                haloRoot.addOrReplaceChild("seg_5",
                        CubeListBuilder.create()
                                .texOffs(0, 8)
                                .addBox(-6.0f, 1.0f + yOff, 17.0f, 12.0f, 2.0f, 2.0f),
                        PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -0.18f, yaw, 0.0f));
            } else {
                haloRoot.addOrReplaceChild("seg_" + i,
                        CubeListBuilder.create()
                                .texOffs(0, 0)
                                .addBox(-8.0f, -1.0f + yOff, 17.0f, 16.0f, 2.0f, 2.0f)
                                .texOffs(0, 16)
                                .addBox(-7.0f, -0.5f + yOff, 15.0f, 14.0f, 1.0f, 2.0f),
                        PartPose.rotation(0.0f, yaw, 0.0f));
            }
        }

        // Floating obsidian/violet shards near the shattered gaps
        haloRoot.addOrReplaceChild("shard_0",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.0f, -1.0f, -1.0f, 4.0f, 2.0f, 2.0f),
                PartPose.offsetAndRotation(17.5f, 2.0f, 2.0f, 0.4f, 0.6f, -0.3f));
        haloRoot.addOrReplaceChild("shard_1",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.0f, -1.0f, -1.0f, 4.0f, 2.0f, 2.0f),
                PartPose.offsetAndRotation(-17.0f, -2.5f, -3.0f, -0.3f, 0.2f, 0.5f));
        haloRoot.addOrReplaceChild("shard_2",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2.0f, -1.0f, -1.0f, 4.0f, 2.0f, 2.0f),
                PartPose.offsetAndRotation(-14.0f, 3.5f, 7.0f, 0.2f, -0.5f, -0.4f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        haloRoot.y = BASE_Y + sin(t * 0.06f) * 1.2f;
        haloRoot.yRot = t * 0.025f;
        haloRoot.xRot = 0.22f + sin(t * 0.04f) * 0.04f;
        haloRoot.zRot = -0.26f + cos(t * 0.035f) * 0.04f;

        shard0.y = 2.0f + sin(t * 0.11f) * 1.1f;
        shard1.y = -2.5f + cos(t * 0.09f) * 1.2f;
        shard2.y = 3.5f + sin(t * 0.13f + 1.5f) * 1.0f;
    }
}
