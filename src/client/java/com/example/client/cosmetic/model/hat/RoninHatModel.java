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
 * Genis kenarli inci-beyazi / fildisi Ronin samuray sapkasi (kasa).
 * UV duzeni: assets/modid/textures/cosmetic/hat_ronin.png (256x256).
 */
public class RoninHatModel extends CosmeticModel {
    private static final int SECTOR_COUNT = 16;
    private static final float SECTOR_STEP = (float) (2.0 * Math.PI / SECTOR_COUNT);
    private static final float RING_SIDE_RATIO = (float) Math.tan(Math.PI / SECTOR_COUNT);
    private static final float APEX_Y = -11.35f;
    private static final float CONE_SLOPE_RAD = -0.42f; // ~24 deg slope: clears head corners & outer skin layer completely

    private final ModelPart hatRoot;

    public RoninHatModel(ModelPart root) {
        super(root);
        this.hatRoot = root.getChild("hatRoot");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // 16 radyal egimli dilimden olusan puruzsuz yuvarlak konik samuray kasa
        for (int i = 0; i < SECTOR_COUNT; i++) {
            float yaw = i * SECTOR_STEP;
            float sectorNormalOffset = (i % 2) * 0.02f;

            PartDefinition sector = hatRoot.addOrReplaceChild("sector_" + i,
                    CubeListBuilder.create(),
                    PartPose.offsetAndRotation(0.0f, APEX_Y, 0.0f, 0.0f, yaw, 0.0f));

            sector.addOrReplaceChild("blade",
                    CubeListBuilder.create()
                            // Ic tepe dilimi
                            .texOffs(0, 40)
                            .addBox(-0.68f, -0.175f + sectorNormalOffset, -0.20f, 1.36f, 0.35f, 3.80f)
                            // Orta govde dilimi
                            .texOffs(0, 56)
                            .addBox(-1.30f, -0.165f + sectorNormalOffset, 3.10f, 2.60f, 0.35f, 3.70f)
                            // Dis siperlik kenar dilimi
                            .texOffs(0, 72)
                            .addBox(-1.90f, -0.155f + sectorNormalOffset, 6.20f, 3.80f, 0.35f, 3.70f),
                    PartPose.rotation(CONE_SLOPE_RAD, 0.0f, 0.0f));
        }

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float walkBob = sin(s.walkPos * 1.3324f) * 0.02f * s.walkSpeed;
        float idleTilt = sin(s.ageInTicks * 0.04f) * 0.01f;
        hatRoot.xRot = 0.03f + walkBob + idleTilt;
        hatRoot.zRot = cos(s.walkPos * 0.6662f) * 0.015f * s.walkSpeed;
    }
}
