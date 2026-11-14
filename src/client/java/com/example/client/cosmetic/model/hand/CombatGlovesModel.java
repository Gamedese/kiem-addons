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
 * Crimson & black padded fingerless martial-arts combat gloves
 * with a gold-trimmed wrist strap and metallic outer buckle.
 */
public class CombatGlovesModel extends CosmeticModel {
    private final ModelPart root;
    private final ModelPart hd;
    private final ModelPart outerBuckle;
    private final ModelPart knucklePad;

    public CombatGlovesModel(ModelPart root) {
        super(root);
        this.root = root;
        this.hd = root.getChild("hd");
        this.outerBuckle = hd.getChild("outerBuckle");
        this.knucklePad = hd.getChild("knucklePad");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 4x HD coordinate space (scaled to 0.25f in setupAnim)
        PartDefinition hd = root.addOrReplaceChild("hd",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // Black padded wrist sleeve (y: 22..32 -> 5.5..8.0)
        hd.addOrReplaceChild("wristSleeve",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-8.0f, 22.0f, -8.0f, 16, 10, 16, new CubeDeformation(0.65f)),
                PartPose.ZERO);

        // Gold-trimmed wrist strap (y: 24..28 -> 6.0..7.0)
        hd.addOrReplaceChild("goldStrap",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-8.0f, 24.0f, -8.0f, 16, 4, 16, new CubeDeformation(0.95f)),
                PartPose.ZERO);

        // Metallic gold strap buckle on outer wrist
        hd.addOrReplaceChild("outerBuckle",
                CubeListBuilder.create().texOffs(80, 0)
                        .addBox(-1.0f, 24.0f, -3.0f, 2, 4, 6),
                PartPose.offset(-9.0f, 0.0f, 0.0f));

        // Fingerless crimson & black glove body (y: 31..37 -> 7.75..9.25)
        hd.addOrReplaceChild("palmBody",
                CubeListBuilder.create().texOffs(0, 54)
                        .addBox(-8.0f, 31.0f, -8.0f, 16, 6, 16, new CubeDeformation(0.85f)),
                PartPose.ZERO);

        // Thickened crimson knuckle impact pad (y: 33..37 -> 8.25..9.25)
        hd.addOrReplaceChild("knucklePad",
                CubeListBuilder.create().texOffs(0, 80)
                        .addBox(-8.0f, 33.0f, -8.0f, 16, 4, 16, new CubeDeformation(1.25f)),
                PartPose.ZERO);

        // Front thumb guard loop
        hd.addOrReplaceChild("thumbGuard",
                CubeListBuilder.create().texOffs(80, 16)
                        .addBox(-3.0f, 30.0f, -9.8f, 6, 6, 3, new CubeDeformation(0.20f)),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float armCenterX = s.isLeftPart
                ? (s.isSlimArms ? 0.5f : 1.0f)
                : (s.isSlimArms ? -0.5f : -1.0f);
        root.x = armCenterX;
        root.xScale = s.isSlimArms ? 0.78f : 1.0f;

        hd.xScale = 0.25f;
        hd.yScale = 0.25f;
        hd.zScale = 0.25f;

        float outerDir = s.isLeftPart ? 1.0f : -1.0f;
        outerBuckle.x = outerDir * 9.0f;

        float swing = sin(s.attackSwing * (float) Math.PI);
        knucklePad.yScale = 1.0f + swing * 0.08f;
    }
}
