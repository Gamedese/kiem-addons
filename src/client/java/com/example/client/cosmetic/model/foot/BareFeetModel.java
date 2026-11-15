package com.example.client.cosmetic.model.foot;

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
 * Blockbench'ten aktarilan detayli ayak modeli (egimli tarak + 5 kademeli parmak).
 */
public class BareFeetModel extends CosmeticModel {
    private final ModelPart rightToes;
    private final ModelPart leftToes;

    public BareFeetModel(ModelPart root) {
        super(root);
        this.rightToes = root.getChild("rightToes");
        this.leftToes = root.getChild("leftToes");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Topuk ve bilek tabani (y = 10.0 .. 12.0)
        root.addOrReplaceChild("ankleHeel",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.1f, 10.0f, -1.1f, 4.2f, 2.0f, 3.2f, new CubeDeformation(0.02f)),
                PartPose.ZERO);

        // On taban (y = 10.95 .. 12.0)
        root.addOrReplaceChild("forefoot",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2.05f, 10.95f, -3.2f, 4.1f, 1.05f, 2.2f),
                PartPose.ZERO);

        // Ayak tarağı / kemer egimi (-20 deg Blockbench pitch -> +20 deg Minecraft Y-down pitch)
        root.addOrReplaceChild("instepArch",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.98f, 0.0f, -2.4f, 3.96f, 0.80f, 2.4f),
                PartPose.offsetAndRotation(0.0f, 10.35f, -0.75f, (float) Math.toRadians(20.0), 0.0f, 0.0f));

        // Sag ayak 5 parmak (basparmak ic tarafta +X)
        PartDefinition rightToes = root.addOrReplaceChild("rightToes", CubeListBuilder.create(), PartPose.ZERO);
        addFiveToes(rightToes, false);

        // Sol ayak 5 parmak (basparmak ic tarafta -X)
        PartDefinition leftToes = root.addOrReplaceChild("leftToes", CubeListBuilder.create(), PartPose.ZERO);
        addFiveToes(leftToes, true);

        return LayerDefinition.create(mesh, 32, 32);
    }

    private static void addFiveToes(PartDefinition parent, boolean mirrorX) {
        addToe(parent, "big_toe",   1.05f, 2.02f, 1.02f, -4.45f, -3.15f, mirrorX);
        addToe(parent, "toe_2",     0.23f, 1.03f, 0.94f, -4.35f, -3.15f, mirrorX);
        addToe(parent, "toe_3",    -0.55f, 0.21f, 0.88f, -4.20f, -3.15f, mirrorX);
        addToe(parent, "toe_4",    -1.30f,-0.57f, 0.82f, -4.05f, -3.15f, mirrorX);
        addToe(parent, "pinky_toe",-2.02f,-1.32f, 0.76f, -3.86f, -3.15f, mirrorX);
    }

    private static void addToe(PartDefinition parent, String name,
                               float fromX, float toX, float height,
                               float fromZ, float toZ, boolean mirrorX) {
        float minX = mirrorX ? -toX : fromX;
        float width = toX - fromX;
        float depth = toZ - fromZ;
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(16, 0)
                        .addBox(minX, 12.0f - height, fromZ, width, height, depth),
                PartPose.ZERO);
    }

    @Override
    public void setupAnim(CosmeticRenderState state) {
        super.setupAnim(state);
        this.rightToes.visible = !state.isLeftPart;
        this.leftToes.visible = state.isLeftPart;
    }
}
