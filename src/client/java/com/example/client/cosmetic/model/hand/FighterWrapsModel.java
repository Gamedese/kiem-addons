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
 * Crimson and off-white athletic fighter hand/wrist wraps (y = 5.0..9.6)
 * with segmented trailing ribbon tails on the outer wrist.
 */
public class FighterWrapsModel extends CosmeticModel {
    private final ModelPart root;
    private final ModelPart knot;
    private final ModelPart segA1;
    private final ModelPart segA2;
    private final ModelPart segA3;
    private final ModelPart segB1;
    private final ModelPart segB2;

    public FighterWrapsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.knot = root.getChild("knot");
        this.segA1 = knot.getChild("segA1");
        this.segA2 = segA1.getChild("segA2");
        this.segA3 = segA2.getChild("segA3");
        this.segB1 = knot.getChild("segB1");
        this.segB2 = segB1.getChild("segB2");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Base forearm wrap (y: 4.8..7.2)
        root.addOrReplaceChild("forearmWrap",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0f, 5.0f, -2.0f, 4, 2, 4, new CubeDeformation(0.20f)),
                PartPose.ZERO);

        // Mid wrist bandage (y: 6.3..8.9)
        root.addOrReplaceChild("wristWrap",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2.0f, 6.6f, -2.0f, 4, 2, 4, new CubeDeformation(0.30f)),
                PartPose.ZERO);

        // Thick knuckle guard (y: 7.84..9.56)
        root.addOrReplaceChild("knuckleWrap",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0f, 8.2f, -2.0f, 4, 1, 4, new CubeDeformation(0.36f)),
                PartPose.ZERO);

        // Front thumb strap
        root.addOrReplaceChild("thumbStrap",
                CubeListBuilder.create().texOffs(20, 0)
                        .addBox(-0.5f, 7.8f, -2.42f, 1, 2, 1, new CubeDeformation(0.05f)),
                PartPose.ZERO);

        // Outer wrist tie knot + 2 segmented trailing ribbons
        PartDefinition knot = root.addOrReplaceChild("knot",
                CubeListBuilder.create().texOffs(20, 6)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.15f)),
                PartPose.offset(-2.35f, 7.2f, 0.0f));

        PartDefinition segA1 = knot.addOrReplaceChild("segA1",
                CubeListBuilder.create().texOffs(20, 10)
                        .addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1),
                PartPose.offset(0.0f, 0.3f, 0.25f));
        PartDefinition segA2 = segA1.addOrReplaceChild("segA2",
                CubeListBuilder.create().texOffs(20, 14)
                        .addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1),
                PartPose.offset(0.0f, 1.85f, 0.0f));
        segA2.addOrReplaceChild("segA3",
                CubeListBuilder.create().texOffs(20, 18)
                        .addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1),
                PartPose.offset(0.0f, 1.85f, 0.0f));

        PartDefinition segB1 = knot.addOrReplaceChild("segB1",
                CubeListBuilder.create().texOffs(26, 10)
                        .addBox(-0.5f, 0.0f, -0.5f, 1, 2, 1),
                PartPose.offset(0.0f, 0.3f, -0.35f));
        segB1.addOrReplaceChild("segB2",
                CubeListBuilder.create().texOffs(26, 14)
                        .addBox(-0.5f, 0.0f, -0.5f, 1, 1, 1),
                PartPose.offset(0.0f, 1.85f, 0.0f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        // Center wrap around classic (4px) or slim (3px) left/right arm
        float armCenterX = s.isLeftPart
                ? (s.isSlimArms ? 0.5f : 1.0f)
                : (s.isSlimArms ? -0.5f : -1.0f);
        root.x = armCenterX;
        root.xScale = s.isSlimArms ? 0.78f : 1.0f;

        // Place knot on the outer side of whichever arm this is attached to
        float outerDir = s.isLeftPart ? 1.0f : -1.0f;
        knot.x = outerDir * 2.35f;

        float t = s.ageInTicks;
        float energy = Math.min(1.3f, s.walkSpeed + s.flyAmount * 0.9f + s.attackSwing * 1.4f);
        float breeze = 0.08f + energy * 0.32f;
        float speed = 0.18f + energy * 0.25f;

        // Primary ribbon cascade (outward flare + trailing flutter)
        float swingWhip = sin(s.attackSwing * (float) Math.PI) * 0.85f;
        segA1.zRot = outerDir * (0.20f + 0.12f * energy) + sin(t * speed) * breeze * 0.45f;
        segA1.xRot = 0.18f + energy * 0.35f + swingWhip * 0.6f + cos(t * speed * 0.8f) * breeze * 0.35f;

        segA2.zRot = outerDir * 0.08f + sin(t * speed - 0.9f) * breeze * 0.75f;
        segA2.xRot = 0.15f + energy * 0.28f + swingWhip * 0.45f + cos(t * speed * 0.8f - 0.9f) * breeze * 0.55f;

        segA3.zRot = sin(t * speed - 1.8f) * breeze * 0.95f;
        segA3.xRot = 0.12f + swingWhip * 0.3f + cos(t * speed * 0.8f - 1.8f) * breeze * 0.75f;

        // Secondary shorter ribbon with offset phase
        segB1.zRot = outerDir * (0.28f + 0.10f * energy) + sin(t * speed + 1.1f) * breeze * 0.5f;
        segB1.xRot = -0.10f + energy * 0.28f + swingWhip * 0.5f + cos(t * speed * 0.85f + 1.1f) * breeze * 0.4f;

        segB2.zRot = sin(t * speed + 0.2f) * breeze * 0.85f;
        segB2.xRot = 0.14f + cos(t * speed * 0.85f + 0.2f) * breeze * 0.65f;
    }
}
