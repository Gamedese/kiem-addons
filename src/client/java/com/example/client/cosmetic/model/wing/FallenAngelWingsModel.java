package com.example.client.cosmetic.model.wing;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Fallen Angel Wings (256x256 HD, 0.25x scale):
 * Lush, multi-tiered volumetric feathered wings modeled with Seraphim-grade
 * alternating Z-plane feather layering: sculpted shoulder coverts, layered
 * secondary & primary flight feathers in obsidian-black, deep abyssal violet,
 * and tarnished dark-gold/silver highlights.
 */
public class FallenAngelWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart leftWing;
    private final ModelPart leftOuter;
    private final ModelPart rightWing;
    private final ModelPart rightOuter;

    public FallenAngelWingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftWing = root.getChild("left_wing");
        this.leftOuter = this.leftWing.getChild("left_outer");
        this.rightWing = root.getChild("right_wing");
        this.rightOuter = this.rightWing.getChild("right_outer");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Back crest (4x scale -> centered upper back at z = 7.0..10.0)
        root.addOrReplaceChild("back_crest",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8.0f, -0.6f, 7.0f, 16.0f, 12.0f, 3.0f),
                PartPose.ZERO);

        // --- LEFT WING (+X) ---
        PartDefinition leftWing = root.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        // Sculpted obsidian & tarnished-gold shoulder bar
                        .texOffs(40, 0).addBox(-1.0f, -3.0f, -1.5f, 26.0f, 6.0f, 2.0f),
                PartPose.offsetAndRotation(7.2f, 2.4f, 9.2f, 0.06f, -0.30f, -0.54f));

        // Tier 3: Sculpted shoulder coverts (5 overlapping feathers, alternating Z)
        addFeather(leftWing, "left_covert_1", 0, 18, 0.5f, 1.4f, 0.8f, 6.0f, 14.0f, 0.44f, false);
        addFeather(leftWing, "left_covert_2", 16, 18, 5.5f, 1.4f, 1.9f, 6.0f, 15.0f, 0.31f, false);
        addFeather(leftWing, "left_covert_3", 32, 18, 10.5f, 1.4f, 0.8f, 6.0f, 16.0f, 0.18f, false);
        addFeather(leftWing, "left_covert_4", 48, 18, 15.5f, 1.4f, 1.9f, 6.0f, 15.0f, 0.06f, false);
        addFeather(leftWing, "left_covert_5", 64, 18, 20.5f, 1.4f, 0.8f, 6.0f, 14.0f, -0.04f, false);

        // Tier 2: Secondary flight feathers (5 layered feathers, alternating Z)
        addFeather(leftWing, "left_sec_1", 82, 18, 1.5f, 3.0f, 3.0f, 7.0f, 20.0f, 0.50f, false);
        addFeather(leftWing, "left_sec_2", 100, 18, 6.8f, 3.0f, 4.1f, 7.0f, 23.0f, 0.36f, false);
        addFeather(leftWing, "left_sec_3", 118, 18, 12.1f, 3.0f, 3.0f, 7.0f, 25.0f, 0.22f, false);
        addFeather(leftWing, "left_sec_4", 136, 18, 17.4f, 3.0f, 4.1f, 7.0f, 27.0f, 0.08f, false);
        addFeather(leftWing, "left_sec_5", 154, 18, 22.4f, 3.0f, 3.0f, 7.0f, 26.0f, -0.05f, false);

        // Outer wing arch
        PartDefinition leftOuter = leftWing.addOrReplaceChild("left_outer",
                CubeListBuilder.create()
                        .texOffs(98, 0).addBox(-1.0f, -2.5f, -1.0f, 34.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(24.5f, -0.5f, 2.5f, 0.0f, -0.10f, 0.76f));

        // Tier 1: Long sweeping primary flight feathers (7 fanned blades)
        addFeather(leftOuter, "left_pri_1", 0, 50, 0.5f, 1.5f, 2.0f, 7.0f, 32.0f, -0.58f, false);
        addFeather(leftOuter, "left_pri_2", 18, 50, 5.5f, 1.5f, 3.1f, 7.0f, 38.0f, -0.73f, false);
        addFeather(leftOuter, "left_pri_3", 36, 50, 10.5f, 1.5f, 2.0f, 7.0f, 43.0f, -0.88f, false);
        addFeather(leftOuter, "left_pri_4", 54, 50, 15.5f, 1.5f, 3.1f, 7.0f, 47.0f, -1.02f, false);
        addFeather(leftOuter, "left_pri_5", 72, 50, 20.5f, 1.5f, 2.0f, 7.0f, 48.0f, -1.16f, false);
        addFeather(leftOuter, "left_pri_6", 90, 50, 25.5f, 1.3f, 3.1f, 7.0f, 42.0f, -1.29f, false);
        addFeather(leftOuter, "left_pri_7", 108, 50, 30.0f, 1.0f, 2.0f, 7.0f, 35.0f, -1.42f, false);

        // --- RIGHT WING (-X, symmetrically mirrored) ---
        PartDefinition rightWing = root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().mirror()
                        .texOffs(40, 0).addBox(-25.0f, -3.0f, -1.5f, 26.0f, 6.0f, 2.0f),
                PartPose.offsetAndRotation(-7.2f, 2.4f, 9.2f, 0.06f, 0.30f, 0.54f));

        addFeather(rightWing, "right_covert_1", 0, 18, -0.5f, 1.4f, 0.8f, 6.0f, 14.0f, -0.44f, true);
        addFeather(rightWing, "right_covert_2", 16, 18, -5.5f, 1.4f, 1.9f, 6.0f, 15.0f, -0.31f, true);
        addFeather(rightWing, "right_covert_3", 32, 18, -10.5f, 1.4f, 0.8f, 6.0f, 16.0f, -0.18f, true);
        addFeather(rightWing, "right_covert_4", 48, 18, -15.5f, 1.4f, 1.9f, 6.0f, 15.0f, -0.06f, true);
        addFeather(rightWing, "right_covert_5", 64, 18, -20.5f, 1.4f, 0.8f, 6.0f, 14.0f, 0.04f, true);

        addFeather(rightWing, "right_sec_1", 82, 18, -1.5f, 3.0f, 3.0f, 7.0f, 20.0f, -0.50f, true);
        addFeather(rightWing, "right_sec_2", 100, 18, -6.8f, 3.0f, 4.1f, 7.0f, 23.0f, -0.36f, true);
        addFeather(rightWing, "right_sec_3", 118, 18, -12.1f, 3.0f, 3.0f, 7.0f, 25.0f, -0.22f, true);
        addFeather(rightWing, "right_sec_4", 136, 18, -17.4f, 3.0f, 4.1f, 7.0f, 27.0f, -0.08f, true);
        addFeather(rightWing, "right_sec_5", 154, 18, -22.4f, 3.0f, 3.0f, 7.0f, 26.0f, 0.05f, true);

        PartDefinition rightOuter = rightWing.addOrReplaceChild("right_outer",
                CubeListBuilder.create().mirror()
                        .texOffs(98, 0).addBox(-33.0f, -2.5f, -1.0f, 34.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(-24.5f, -0.5f, 2.5f, 0.0f, 0.10f, -0.76f));

        addFeather(rightOuter, "right_pri_1", 0, 50, -0.5f, 1.5f, 2.0f, 7.0f, 32.0f, 0.58f, true);
        addFeather(rightOuter, "right_pri_2", 18, 50, -5.5f, 1.5f, 3.1f, 7.0f, 38.0f, 0.73f, true);
        addFeather(rightOuter, "right_pri_3", 36, 50, -10.5f, 1.5f, 2.0f, 7.0f, 43.0f, 0.88f, true);
        addFeather(rightOuter, "right_pri_4", 54, 50, -15.5f, 1.5f, 3.1f, 7.0f, 47.0f, 1.02f, true);
        addFeather(rightOuter, "right_pri_5", 72, 50, -20.5f, 1.5f, 2.0f, 7.0f, 48.0f, 1.16f, true);
        addFeather(rightOuter, "right_pri_6", 90, 50, -25.5f, 1.3f, 3.1f, 7.0f, 42.0f, 1.29f, true);
        addFeather(rightOuter, "right_pri_7", 108, 50, -30.0f, 1.0f, 2.0f, 7.0f, 35.0f, 1.42f, true);

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void addFeather(PartDefinition parent, String name, int u, int v,
                                   float x, float y, float z, float width, float length,
                                   float zRot, boolean mirror) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder.mirror();
        }
        float boxX = mirror ? -width : 0.0f;
        builder.texOffs(u, v).addBox(boxX, 0.0f, 0.0f, width, length, 1.0f);
        parent.addOrReplaceChild(name, builder, PartPose.offsetAndRotation(x, y, z, 0.0f, 0.0f, zRot));
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float energy = Math.min(1.0f, Math.max(s.flyAmount, s.walkSpeed * 0.85f));
        float sprintSweep = Math.max(0.0f, Math.min(1.0f, (s.walkSpeed - 0.55f) * 2.2f));
        float flySweep = s.flyAmount * 0.36f;

        float idleBreath = sin(t * 0.075f) * 0.065f;
        float activeFlap = sin(t * 0.32f) * (0.29f * energy);
        float flapZ = idleBreath + activeFlap;
        float outerWave = sin(t * 0.075f - 0.55f) * 0.055f + sin(t * 0.32f - 0.65f) * (0.23f * energy);

        float sweepBackY = 0.30f + 0.12f * energy + 0.36f * sprintSweep + flySweep
                + cos(t * 0.075f) * 0.03f - s.crouchAmount * 0.10f;
        float pitchX = 0.06f + 0.20f * sprintSweep + s.flyAmount * 0.18f + s.crouchAmount * 0.15f;

        leftWing.xRot = pitchX;
        leftWing.yRot = -sweepBackY;
        leftWing.zRot = -0.54f + flapZ + s.crouchAmount * 0.13f;
        leftOuter.yRot = -0.10f - 0.14f * sprintSweep;
        leftOuter.zRot = 0.76f + outerWave;

        rightWing.xRot = pitchX;
        rightWing.yRot = sweepBackY;
        rightWing.zRot = 0.54f - flapZ - s.crouchAmount * 0.13f;
        rightOuter.yRot = 0.10f + 0.14f * sprintSweep;
        rightOuter.zRot = -0.76f - outerWave;
    }
}
