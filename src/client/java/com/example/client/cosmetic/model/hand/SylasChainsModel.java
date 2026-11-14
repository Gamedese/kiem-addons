package com.example.client.cosmetic.model.hand;

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
 * Heavy petricite/iron wrist shackles with gold trim (y = 6.2..8.6)
 * and 10 articulated dangling broken chain links inspired by Sylas,
 * reaching near ankle/ground level when arms hang at rest.
 */
public class SylasChainsModel extends CosmeticModel {
    private static final float HALF_PI = (float) (Math.PI * 0.5);
    private static final int LINK_COUNT = 10;

    private final ModelPart root;
    private final ModelPart shackleLock;
    private final ModelPart[] links = new ModelPart[LINK_COUNT];

    public SylasChainsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.shackleLock = root.getChild("shackleLock");
        ModelPart current = this.shackleLock;
        for (int i = 0; i < LINK_COUNT; i++) {
            current = current.getChild("link" + (i + 1));
            this.links[i] = current;
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Heavy dark iron & petricite cuff body (y: 6.2..8.8)
        root.addOrReplaceChild("cuffMain",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0f, 6.5f, -2.0f, 4, 2, 4, new CubeDeformation(0.30f)),
                PartPose.ZERO);

        // Gold-trimmed upper & lower shackle rims
        root.addOrReplaceChild("trimTop",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2.0f, 6.2f, -2.0f, 4, 1, 4, new CubeDeformation(0.38f)),
                PartPose.ZERO);

        root.addOrReplaceChild("trimBottom",
                CubeListBuilder.create().texOffs(0, 15)
                        .addBox(-2.0f, 7.6f, -2.0f, 4, 1, 4, new CubeDeformation(0.38f)),
                PartPose.ZERO);

        // Outer wrist anchor lock
        PartDefinition shackleLock = root.addOrReplaceChild("shackleLock",
                CubeListBuilder.create().texOffs(0, 22)
                        .addBox(-0.5f, -1.0f, -1.0f, 1, 2, 2, new CubeDeformation(0.15f)),
                PartPose.offset(-2.45f, 7.4f, 0.0f));

        // 10 articulated hollow voxel chain links (alternating 90-deg Y rotation)
        PartDefinition current = shackleLock.addOrReplaceChild("link1",
                CubeListBuilder.create().texOffs(18, 0)
                        .addBox(-1.5f, 0.0f, -0.5f, 3, 3, 1),
                PartPose.offset(0.0f, 0.9f, 0.0f));

        for (int i = 1; i < LINK_COUNT; i++) {
            int texY = (i == LINK_COUNT - 1) ? 8 : 0;
            float rotY = (i % 2 == 1) ? HALF_PI : -HALF_PI;
            current = current.addOrReplaceChild("link" + (i + 1),
                    CubeListBuilder.create().texOffs(18, texY)
                            .addBox(-1.5f, 0.0f, -0.5f, 3, 3, 1),
                    PartPose.offsetAndRotation(0.0f, 2.05f, 0.0f, 0.0f, rotY, 0.0f));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        // Center shackle around classic (4px) or slim (3px) left/right arm
        float armCenterX = s.isLeftPart
                ? (s.isSlimArms ? 0.5f : 1.0f)
                : (s.isSlimArms ? -0.5f : -1.0f);
        root.x = armCenterX;
        root.xScale = s.isSlimArms ? 0.78f : 1.0f;

        // Place lock and dangling chain on the outer wrist
        float outerDir = s.isLeftPart ? 1.0f : -1.0f;
        shackleLock.x = outerDir * 2.45f;

        // Scale down chain links uniformly so 3x3 hollow links fit wrist proportions
        ModelPart link1 = links[0];
        link1.xScale = 0.65f;
        link1.yScale = 0.65f;
        link1.zScale = 0.65f;

        float t = s.ageInTicks;
        float walk = Math.min(1.2f, s.walkSpeed);
        float swing = sin(s.attackSwing * (float) Math.PI);
        float idleSway = sin(t * 0.09f) * 0.06f;

        if (s.isFirstPerson) {
            // In 1st person, the arm pitches forward/upward toward the crosshair, so bend link1
            // downward in camera space (gravity) and slightly outward off the bottom of the wrist.
            link1.zRot = outerDir * 2.08f + idleSway;
            link1.xRot = 0.75f + swing * 0.25f;
            for (int i = 1; i < LINK_COUNT; i++) {
                float phase = i * 0.45f;
                links[i].xRot = sin(t * 0.12f - phase) * 0.03f + swing * 0.06f;
                links[i].zRot = cos(t * 0.10f - phase) * 0.03f;
            }
            return;
        }

        float walkSway = sin(s.walkPos * 0.6662f) * 0.35f * walk;
        float flyTrail = s.flyAmount * 0.45f;

        // Heavy pendulum cascade across all 10 links
        link1.zRot = outerDir * (0.16f + walk * 0.08f) + idleSway;
        link1.xRot = walkSway * 0.45f + swing * 0.95f + flyTrail * 0.4f;

        for (int i = 1; i < LINK_COUNT; i++) {
            float phase = i * 0.55f;
            float walkAmp = 0.20f + i * 0.022f;
            float swingAmp = 0.28f + i * 0.025f;
            float flyAmp = 0.10f + i * 0.025f;
            links[i].xRot = sin(s.walkPos * 0.6662f - phase) * walkAmp * walk
                    + swing * swingAmp
                    + flyTrail * flyAmp;
            links[i].zRot = cos(t * (0.11f + i * 0.01f) + i * 0.7f) * (0.05f + i * 0.008f)
                    + (i == 1 ? outerDir * (swing * 0.25f) : 0.0f);
        }
    }
}
