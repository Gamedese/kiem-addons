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
 * Cozy winter fur ushanka hat with plush front flap, side ear flaps, and subtle star emblem (256x256 HD, 0.25x scale).
 * Texture: assets/modid/textures/cosmetic/hat_ushanka.png
 */
public class UshankaHatModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart earFlapL;
    private final ModelPart earFlapR;

    public UshankaHatModel(ModelPart root) {
        super(root);
        this.root = root;
        ModelPart hatRoot = root.getChild("hatRoot");
        this.earFlapL = hatRoot.getChild("earFlapL");
        this.earFlapR = hatRoot.getChild("earFlapR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create()
                        // Quilted winter crown & rounded top dome
                        .texOffs(0, 0).addBox(-17.0f, -9.0f, -17.0f, 34.0f, 10.0f, 34.0f)
                        .texOffs(140, 0).addBox(-14.0f, -11.8f, -14.0f, 28.0f, 3.0f, 28.0f)
                        // Plush fur front flap & back flap
                        .texOffs(0, 48).addBox(-16.0f, -9.5f, -19.8f, 32.0f, 9.0f, 4.0f)
                        .texOffs(76, 48).addBox(-16.0f, -6.5f, 16.4f, 32.0f, 7.0f, 3.0f)
                        // Subtle brushed-brass star emblem on front flap
                        .texOffs(150, 48).addBox(-3.0f, -7.5f, -20.5f, 6.0f, 6.0f, 1.0f),
                PartPose.offset(0.0f, -25.0f, 0.0f));

        hatRoot.addOrReplaceChild("earFlapL",
                CubeListBuilder.create()
                        .texOffs(0, 66).addBox(-1.5f, 0.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(34, 66).addBox(-1.0f, 11.5f, -4.0f, 2.0f, 5.0f, 8.0f),
                PartPose.offsetAndRotation(17.6f, -2.0f, 0.0f, 0.0f, 0.0f, -0.08f));

        hatRoot.addOrReplaceChild("earFlapR",
                CubeListBuilder.create()
                        .texOffs(0, 66).addBox(-1.5f, 0.0f, -6.0f, 3.0f, 12.0f, 12.0f)
                        .texOffs(34, 66).addBox(-1.0f, 11.5f, -4.0f, 2.0f, 5.0f, 8.0f),
                PartPose.offsetAndRotation(-17.6f, -2.0f, 0.0f, 0.0f, 0.0f, 0.08f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float sway = sin(s.ageInTicks * 0.06f) * 0.02f;
        float walkFlap = Math.abs(sin(s.walkPos * 0.6662f)) * 0.08f * s.walkSpeed;
        earFlapL.zRot = -0.08f - sway - walkFlap;
        earFlapR.zRot = 0.08f + sway + walkFlap;
    }
}
