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
 * Sleek black & gold-trimmed lower-face ninja mask attached to PlayerModel.head,
 * covering nose, mouth, and chin (y = -2.2..+0.5) with trailing back tie ribbons.
 * Texture: assets/modid/textures/cosmetic/neck_ninja_mask.png (256x256)
 */
public class ShadowNinjaMaskModel extends CosmeticModel {
    private final ModelPart ribbonL;
    private final ModelPart ribbonR;

    public ShadowNinjaMaskModel(ModelPart root) {
        super(root);
        this.ribbonL = root.getChild("ribbonL");
        this.ribbonR = root.getChild("ribbonR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Main sleek black lower-face wrap covering nose, mouth, and chin (below eyes)
        root.addOrReplaceChild("maskWrap",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.2f, -2.2f, -4.25f, 8.4f, 2.7f, 8.45f),
                PartPose.ZERO);

        // Metallic gold upper trim hugging the cheekbones and nose bridge
        root.addOrReplaceChild("goldTrimTop",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-4.28f, -2.2f, -4.32f, 8.56f, 0.40f, 8.56f),
                PartPose.ZERO);

        // Contoured nose bridge piece
        root.addOrReplaceChild("noseBridge",
                CubeListBuilder.create()
                        .texOffs(0, 30)
                        .addBox(-1.2f, -2.25f, -4.40f, 2.4f, 1.0f, 0.4f),
                PartPose.ZERO);

        // Layered chin plate with gold stitching accent
        root.addOrReplaceChild("chinGuard",
                CubeListBuilder.create()
                        .texOffs(10, 30)
                        .addBox(-2.6f, -0.7f, -4.36f, 5.2f, 1.2f, 0.4f),
                PartPose.ZERO);

        // Back tie knot
        root.addOrReplaceChild("backKnot",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(-0.9f, -1.7f, 4.1f, 1.8f, 1.2f, 0.6f),
                PartPose.ZERO);

        // Trailing back tie ribbons
        root.addOrReplaceChild("ribbonL",
                CubeListBuilder.create()
                        .texOffs(10, 36)
                        .addBox(-0.5f, 0.0f, -0.2f, 1.0f, 2.8f, 0.4f),
                PartPose.offsetAndRotation(0.65f, -1.0f, 4.45f, 0.25f, 0.10f, 0.14f));

        root.addOrReplaceChild("ribbonR",
                CubeListBuilder.create()
                        .texOffs(16, 36)
                        .addBox(-0.5f, 0.0f, -0.2f, 1.0f, 2.8f, 0.4f),
                PartPose.offsetAndRotation(-0.65f, -1.0f, 4.45f, 0.22f, -0.10f, -0.14f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(1.25f, s.walkSpeed + s.flyAmount * 0.85f);
        float wave = sin(t * (0.14f + speed * 0.16f));

        ribbonL.xRot = 0.25f + speed * 0.42f + wave * (0.08f + speed * 0.14f);
        ribbonL.zRot = 0.14f + cos(t * 0.11f) * 0.05f;

        ribbonR.xRot = 0.22f + speed * 0.42f + sin(t * (0.14f + speed * 0.16f) + 0.7f) * (0.08f + speed * 0.14f);
        ribbonR.zRot = -0.14f - cos(t * 0.11f + 0.6f) * 0.05f;
    }
}
