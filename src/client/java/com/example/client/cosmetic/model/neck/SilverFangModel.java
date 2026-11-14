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
 * Body-attached fine silver necklace chain with a curved ivory/silver
 * wolf fang pendant resting on the upper chest.
 * Texture: assets/modid/textures/cosmetic/neck_silver_fang.png (256x256)
 */
public class SilverFangModel extends CosmeticModel {
    private final ModelPart pendant;

    public SilverFangModel(ModelPart root) {
        super(root);
        this.pendant = root.getChild("pendant");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Fine silver chain collar around the neck (cutout alpha on front/sides/back)
        root.addOrReplaceChild("neckChain",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.05f, -0.15f, -2.08f, 8.1f, 0.85f, 4.16f),
                PartPose.ZERO);

        // V-drape silver chain segments from collarbone to pendant bail
        root.addOrReplaceChild("chainL",
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-0.4f, -1.35f, -0.4f, 0.8f, 2.7f, 0.8f, new CubeDeformation(-0.22f)),
                PartPose.offsetAndRotation(1.35f, 1.35f, -2.18f, 0.0f, 0.0f, 0.50f));

        root.addOrReplaceChild("chainR",
                CubeListBuilder.create()
                        .texOffs(6, 8)
                        .addBox(-0.4f, -1.35f, -0.4f, 0.8f, 2.7f, 0.8f, new CubeDeformation(-0.22f)),
                PartPose.offsetAndRotation(-1.35f, 1.35f, -2.18f, 0.0f, 0.0f, -0.50f));

        // Wolf fang pendant resting on chest at (0, 2.65, -2.26)
        PartDefinition p = root.addOrReplaceChild("pendant",
                CubeListBuilder.create()
                        .texOffs(12, 8)
                        .addBox(-0.55f, -0.45f, -0.35f, 1.1f, 0.8f, 0.7f),
                PartPose.offset(0.0f, 2.65f, -2.26f));

        // Upper ivory fang body
        p.addOrReplaceChild("fangUpper",
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .addBox(-0.45f, 0.25f, -0.30f, 0.9f, 1.1f, 0.6f),
                PartPose.ZERO);

        // Curved tapered wolf fang tip
        p.addOrReplaceChild("fangTip",
                CubeListBuilder.create()
                        .texOffs(8, 14)
                        .addBox(-0.30f, 0.0f, -0.22f, 0.6f, 1.0f, 0.44f),
                PartPose.offsetAndRotation(0.05f, 1.25f, -0.02f, -0.10f, 0.0f, -0.18f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.25f);
        pendant.y = 2.65f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.08f * speed;
        pendant.zRot = sin(s.walkPos * 0.6662f) * 0.11f * speed + sin(t * 0.05f) * 0.03f;
        pendant.xRot = -0.04f - speed * 0.14f - s.crouchAmount * 0.26f;
    }
}
