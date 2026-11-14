package com.example.client.cosmetic.model.neck;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Fierce black & gold samurai menpo lower-face armor mask attached to PlayerModel.head,
 * featuring sculpted cheek plates, nose guard, throat-guard (yodarekake), and white fangs at z = -4.3.
 * Texture: assets/modid/textures/cosmetic/neck_oni_menpo.png (256x256)
 */
public class OniMenpoModel extends CosmeticModel {
    private final ModelPart throatGuard;

    public OniMenpoModel(ModelPart root) {
        super(root);
        this.throatGuard = root.getChild("throatGuard");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Main lacquered black iron menpo shell (below eyes: y = -2.2..+0.5)
        root.addOrReplaceChild("menpoBase",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.25f, -2.2f, -4.28f, 8.5f, 2.7f, 8.5f),
                PartPose.ZERO);

        // Aggressive samurai nose bridge
        root.addOrReplaceChild("noseGuard",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-1.1f, -2.25f, -4.46f, 2.2f, 1.1f, 0.5f),
                PartPose.ZERO);

        // Sculpted flared cheek armor plates
        root.addOrReplaceChild("cheekPlateL",
                CubeListBuilder.create()
                        .texOffs(8, 16)
                        .addBox(-1.4f, -0.75f, -0.35f, 2.8f, 1.5f, 0.7f),
                PartPose.offsetAndRotation(2.85f, -1.35f, -4.15f, 0.0f, -0.28f, -0.10f));

        root.addOrReplaceChild("cheekPlateR",
                CubeListBuilder.create()
                        .texOffs(20, 16)
                        .addBox(-1.4f, -0.75f, -0.35f, 2.8f, 1.5f, 0.7f),
                PartPose.offsetAndRotation(-2.85f, -1.35f, -4.15f, 0.0f, 0.28f, 0.10f));

        // Recessed fierce mouth-plate
        root.addOrReplaceChild("mouthGrimace",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-2.4f, -1.25f, -4.34f, 4.8f, 1.1f, 0.35f),
                PartPose.ZERO);

        // White sculpted oni fangs jutting out at z = -4.3
        root.addOrReplaceChild("fangL",
                CubeListBuilder.create()
                        .texOffs(14, 24)
                        .addBox(-0.35f, -0.75f, -0.35f, 0.7f, 0.95f, 0.65f),
                PartPose.offsetAndRotation(1.45f, -0.70f, -4.32f, -0.18f, 0.0f, 0.14f));

        root.addOrReplaceChild("fangR",
                CubeListBuilder.create()
                        .texOffs(20, 24)
                        .addBox(-0.35f, -0.75f, -0.35f, 0.7f, 0.95f, 0.65f),
                PartPose.offsetAndRotation(-1.45f, -0.70f, -4.32f, -0.18f, 0.0f, -0.14f));

        // Gold-laced samurai throat-guard (yodarekake) below chin
        root.addOrReplaceChild("throatGuard",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-3.1f, 0.0f, -0.25f, 6.2f, 1.0f, 0.5f),
                PartPose.offsetAndRotation(0.0f, 0.45f, -4.15f, -0.12f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        float speed = Math.min(1.2f, s.walkSpeed);
        throatGuard.xRot = -0.12f - speed * 0.10f - s.headXRot * 0.18f;
    }
}
