package com.example.client.cosmetic.model.neck;

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
 * Fine silver chain collar around the upper chest/neck with a 4-point
 * faceted cyan/gold star crystal pendant hanging at y = 2.5..4.5, z = -2.35.
 * Texture: assets/modid/textures/cosmetic/neck_pendant.png (64x64)
 */
public class ArcaneStarPendantModel extends CosmeticModel {
    private final ModelPart pendant;
    private final ModelPart starCore;

    public ArcaneStarPendantModel(ModelPart root) {
        super(root);
        this.pendant = root.getChild("pendant");
        this.starCore = pendant.getChild("starCore");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Fine silver chain collar around the neck (cutout alpha on front/sides/back)
        root.addOrReplaceChild("collarWrap",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0f, -0.2f, -2.0f, 8, 1, 4, new CubeDeformation(0.15f)),
                PartPose.ZERO);

        // V-drape silver chain segments from collarbone down to pendant bail
        root.addOrReplaceChild("chainLeft",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(-0.5f, -1.5f, -0.5f, 1, 3, 1, new CubeDeformation(-0.28f)),
                PartPose.offsetAndRotation(1.55f, 1.35f, -2.25f, 0.0f, 0.0f, 0.56f));

        root.addOrReplaceChild("chainRight",
                CubeListBuilder.create()
                        .texOffs(5, 6)
                        .addBox(-0.5f, -1.5f, -0.5f, 1, 3, 1, new CubeDeformation(-0.28f)),
                PartPose.offsetAndRotation(-1.55f, 1.35f, -2.25f, 0.0f, 0.0f, -0.56f));

        // 4-point faceted cyan/gold star crystal pendant centered at (0, 3.5, -2.35) -> y = 2.5..4.5
        PartDefinition p = root.addOrReplaceChild("pendant",
                CubeListBuilder.create()
                        .texOffs(10, 6)
                        .addBox(-0.5f, -1.35f, -0.5f, 1, 1, 1, new CubeDeformation(-0.22f)),
                PartPose.offset(0.0f, 3.5f, -2.35f));

        // Gold diamond bezel behind the crystal star
        p.addOrReplaceChild("goldFrame",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-1.0f, -1.0f, -0.5f, 2, 2, 1, new CubeDeformation(-0.18f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.05f, 0.0f, 0.0f, (float) (Math.PI / 4.0)));

        // Vertical & horizontal faceted cyan crystal points
        p.addOrReplaceChild("starVert",
                CubeListBuilder.create()
                        .texOffs(8, 12)
                        .addBox(-0.5f, -1.0f, -0.5f, 1, 2, 1, new CubeDeformation(-0.04f)),
                PartPose.ZERO);

        p.addOrReplaceChild("starHoriz",
                CubeListBuilder.create()
                        .texOffs(14, 12)
                        .addBox(-1.0f, -0.5f, -0.5f, 2, 1, 1, new CubeDeformation(-0.04f)),
                PartPose.ZERO);

        // Glowing central star core
        p.addOrReplaceChild("starCore",
                CubeListBuilder.create()
                        .texOffs(22, 12)
                        .addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, new CubeDeformation(0.10f)),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.25f);
        float pulse = sin(t * 0.11f);

        // Gentle arcane bob & crystal core pulse
        pendant.y = 3.5f + pulse * 0.09f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.10f * speed;
        pendant.zRot = sin(s.walkPos * 0.6662f) * 0.10f * speed + sin(t * 0.05f) * 0.03f;
        pendant.xRot = -0.04f - speed * 0.14f - s.crouchAmount * 0.28f;

        float coreScale = 1.0f + pulse * 0.07f;
        starCore.xScale = coreScale;
        starCore.yScale = coreScale;
        starCore.zScale = coreScale;
    }
}
