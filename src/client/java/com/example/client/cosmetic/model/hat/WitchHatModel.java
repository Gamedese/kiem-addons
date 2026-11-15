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
 * Genis 3 parcali egimli dairesel alpha-cutout siperlik, yakut tokali serit ve arkaya (-xRot -> +Z) yatkin yildizli koni.
 */
public class WitchHatModel extends CosmeticModel {
    private final ModelPart cone1, cone2, cone3, tip;

    public WitchHatModel(ModelPart root) {
        super(root);
        ModelPart hatRoot = root.getChild("hatRoot");
        this.cone1 = hatRoot.getChild("cone1");
        this.cone2 = cone1.getChild("cone2");
        this.cone3 = cone2.getChild("cone3");
        this.tip = cone3.getChild("tip");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hatRoot = root.addOrReplaceChild("hatRoot",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // 4 parcali 3D kavisli siperlik: on yukari (-16 deg), arka asagi (-24 deg), yanlar asagi (+-22 deg)
        hatRoot.addOrReplaceChild("brimFront",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0f, 0.0f, -5.0f, 8.0f, 0.0f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -6.5f, -4.0f, (float) Math.toRadians(-16.0), 0.0f, 0.0f));
        hatRoot.addOrReplaceChild("brimBack",
                CubeListBuilder.create().texOffs(0, 6).addBox(-4.0f, 0.0f, 0.0f, 8.0f, 0.0f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -6.5f, 4.0f, (float) Math.toRadians(-24.0), 0.0f, 0.0f));
        hatRoot.addOrReplaceChild("brimLeft",
                CubeListBuilder.create().texOffs(20, 0).addBox(-6.0f, 0.0f, -9.0f, 6.0f, 0.0f, 18.0f),
                PartPose.offsetAndRotation(-4.0f, -6.5f, 0.0f, (float) Math.toRadians(-10.0), 0.0f, (float) Math.toRadians(-22.0)));
        hatRoot.addOrReplaceChild("brimRight",
                CubeListBuilder.create().texOffs(34, 0).addBox(0.0f, 0.0f, -9.0f, 6.0f, 0.0f, 18.0f),
                PartPose.offsetAndRotation(4.0f, -6.5f, 0.0f, (float) Math.toRadians(-10.0), 0.0f, (float) Math.toRadians(22.0)));

        // Mor kadife taban + ince altin kemer + yakut toka (h=3)
        hatRoot.addOrReplaceChild("band",
                CubeListBuilder.create().texOffs(0, 19).addBox(-4.5f, -9.3f, -4.5f, 9.0f, 3.0f, 9.0f),
                PartPose.ZERO);

        // Arkaya (+Z) egimli yildizli koni (-xRot = -Y'yi +Z arkaya yatirir)
        PartDefinition c1 = hatRoot.addOrReplaceChild("cone1",
                CubeListBuilder.create().texOffs(0, 33).addBox(-3.5f, -4.0f, -3.5f, 7.0f, 4.0f, 7.0f),
                PartPose.offsetAndRotation(0.0f, -9.1f, 0.0f, -0.08f, 0.0f, 0.04f));

        PartDefinition c2 = c1.addOrReplaceChild("cone2",
                CubeListBuilder.create().texOffs(28, 33).addBox(-2.5f, -3.8f, -2.5f, 5.0f, 3.8f, 5.0f),
                PartPose.offsetAndRotation(0.0f, -3.7f, 0.0f, -0.18f, 0.0f, 0.08f));

        PartDefinition c3 = c2.addOrReplaceChild("cone3",
                CubeListBuilder.create().texOffs(0, 46).addBox(-1.6f, -3.0f, -1.6f, 3.2f, 3.0f, 3.2f),
                PartPose.offsetAndRotation(0.0f, -3.5f, 0.0f, -0.28f, 0.0f, 0.12f));

        c3.addOrReplaceChild("tip",
                CubeListBuilder.create().texOffs(16, 46).addBox(-0.8f, -2.8f, -0.8f, 1.6f, 2.8f, 1.6f),
                PartPose.offsetAndRotation(0.0f, -2.7f, 0.0f, -0.36f, 0.0f, 0.14f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float sway = sin(t * 0.07f) * 0.025f;
        float walkBob = sin(s.walkPos * 0.6662f) * 0.05f * s.walkSpeed;

        cone1.xRot = -0.08f - walkBob * 0.3f;
        cone2.xRot = -0.18f - sway - walkBob * 0.6f;
        cone3.xRot = -0.28f - sin(t * 0.07f - 0.7f) * 0.04f - walkBob * 0.9f;
        tip.xRot   = -0.36f - sin(t * 0.07f - 1.3f) * 0.06f - walkBob * 1.1f;

        cone1.zRot = 0.04f;
        cone2.zRot = 0.08f + cos(t * 0.05f) * 0.02f;
        cone3.zRot = 0.12f + cos(t * 0.05f - 0.5f) * 0.03f;
        tip.zRot   = 0.14f + cos(t * 0.05f - 1.0f) * 0.04f;
    }
}
