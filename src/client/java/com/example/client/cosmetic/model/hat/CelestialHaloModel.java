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
 * Sleek octagonal golden celestial halo floating above the head (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_celestial_halo.png
 */
public class CelestialHaloModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    private static final int SEGMENT_COUNT = 8;
    private static final float BASE_Y = -42.0f; // -10.5f in Minecraft head units

    private final ModelPart root;
    private final ModelPart haloRoot;

    public CelestialHaloModel(ModelPart root) {
        super(root);
        this.root = root;
        this.haloRoot = root.getChild("haloRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition haloRoot = root.addOrReplaceChild("haloRoot",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, BASE_Y, 0.0f));

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            float yaw = (float) (i * Math.PI / 4.0);
            float yOffset = (i % 2) * 0.06f;

            PartDefinition seg = haloRoot.addOrReplaceChild("seg_" + i,
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-8.0f, -1.0f + yOffset, 17.0f, 16.0f, 2.0f, 2.0f)
                            .texOffs(0, 8)
                            .addBox(-7.0f, -0.5f + yOffset, 15.0f, 14.0f, 1.0f, 2.0f),
                    PartPose.rotation(0.0f, yaw, 0.0f));

            if (i % 2 == 0) {
                seg.addOrReplaceChild("finial_" + i,
                        CubeListBuilder.create()
                                .texOffs(0, 16)
                                .addBox(-2.0f, -2.0f, 19.2f, 4.0f, 4.0f, 2.0f),
                        PartPose.offsetAndRotation(0.0f, yOffset, 0.0f, 0.0f, 0.0f, (float) (Math.PI / 4.0)));

                seg.addOrReplaceChild("starlet_" + i,
                        CubeListBuilder.create()
                                .texOffs(16, 16)
                                .addBox(-1.0f, -1.0f, 12.0f, 2.0f, 2.0f, 2.0f),
                        PartPose.offsetAndRotation(0.0f, -1.5f + yOffset, 0.0f, 0.0f, 0.0f, (float) (Math.PI / 4.0)));
            }
        }

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        haloRoot.y = BASE_Y + sin(t * 0.08f) * 1.4f + sin(s.walkPos * 0.6662f) * 0.5f * s.walkSpeed;
        haloRoot.yRot = t * 0.045f;
        haloRoot.xRot = 0.05f + sin(t * 0.05f) * 0.035f;
        haloRoot.zRot = cos(t * 0.04f) * 0.03f;
    }
}
