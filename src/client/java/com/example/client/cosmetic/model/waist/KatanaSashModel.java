package com.example.client.cosmetic.model.waist;

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
 * Bel kusagi (obi) ve sol kalcada acili katana kini.
 * Govdeye (PlayerModel.body) baglanir: +Y asagi, -Z on, +X sol kalca.
 */
public class KatanaSashModel extends CosmeticModel {
    private final ModelPart sash;
    private final ModelPart sashTailLong;
    private final ModelPart sashTailShort;
    private final ModelPart katanaGroup;

    public KatanaSashModel(ModelPart root) {
        super(root);
        this.sash = root.getChild("sash");
        this.sashTailLong = this.sash.getChild("sashTailLong");
        this.sashTailShort = this.sash.getChild("sashTailShort");
        this.katanaGroup = root.getChild("katanaGroup");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition sash = root.addOrReplaceChild("sash",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4f, 9.5f, -2f, 8f, 2f, 4f, new CubeDeformation(0.32f)),
                PartPose.ZERO);

        sash.addOrReplaceChild("sashTailLong",
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-1f, 0f, -0.5f, 2f, 4f, 1f),
                PartPose.offsetAndRotation(2.3f, 11.4f, -2.35f, 0.06f, 0f, -0.08f));

        sash.addOrReplaceChild("sashTailShort",
                CubeListBuilder.create()
                        .texOffs(8, 8)
                        .addBox(-1f, 0f, -0.5f, 2f, 3f, 1f),
                PartPose.offsetAndRotation(1.5f, 11.4f, -2.30f, 0.04f, 0f, 0.06f));

        PartDefinition katana = root.addOrReplaceChild("katanaGroup",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(4.4f, 10.5f, 0.2f, -0.45f, 0.08f, -0.05f));

        // Black lacquer scabbard (saya)
        katana.addOrReplaceChild("saya",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-0.5f, -0.5f, -2.0f, 1f, 1f, 12f, new CubeDeformation(0.05f)),
                PartPose.ZERO);

        // Gold scabbard tip (kojiri)
        katana.addOrReplaceChild("kojiri",
                CubeListBuilder.create()
                        .texOffs(28, 16)
                        .addBox(-0.5f, -0.5f, 9.0f, 1f, 1f, 2f, new CubeDeformation(0.12f)),
                PartPose.ZERO);

        // Gold scabbard mouth collar (koiguchi)
        katana.addOrReplaceChild("koiguchi",
                CubeListBuilder.create()
                        .texOffs(28, 20)
                        .addBox(-0.5f, -0.5f, -2.5f, 1f, 1f, 1f, new CubeDeformation(0.14f)),
                PartPose.ZERO);

        // Square handguard (tsuba)
        katana.addOrReplaceChild("tsuba",
                CubeListBuilder.create()
                        .texOffs(36, 16)
                        .addBox(-1f, -1f, -3.2f, 2f, 2f, 1f, new CubeDeformation(-0.15f)),
                PartPose.ZERO);

        // Crimson/black wrapped hilt (tsuka)
        katana.addOrReplaceChild("tsuka",
                CubeListBuilder.create()
                        .texOffs(0, 32)
                        .addBox(-0.5f, -0.5f, -8.2f, 1f, 1f, 5f, new CubeDeformation(0.08f)),
                PartPose.ZERO);

        // Gold pommel cap (kashira)
        katana.addOrReplaceChild("kashira",
                CubeListBuilder.create()
                        .texOffs(14, 32)
                        .addBox(-0.5f, -0.5f, -9.2f, 1f, 1f, 1f, new CubeDeformation(0.13f)),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float speed = Math.min(s.walkSpeed, 1.2f);
        float walkBob = sin(s.walkPos * 1.3324f) * 0.045f * speed;
        float walkSwing = cos(s.walkPos * 0.6662f) * 0.04f * speed;
        float idleBreath = sin(s.ageInTicks * 0.08f) * 0.012f;

        katanaGroup.xRot = -0.45f + walkBob + idleBreath + s.crouchAmount * 0.12f;
        katanaGroup.zRot = -0.05f + walkSwing;
        katanaGroup.y = 10.5f + Math.abs(sin(s.walkPos * 0.6662f)) * 0.18f * speed;

        float fabricSway = sin(s.ageInTicks * 0.10f) * 0.03f + sin(s.walkPos * 0.6662f) * 0.12f * speed;
        sashTailLong.xRot = 0.06f + Math.abs(fabricSway) * 0.5f + s.crouchAmount * 0.15f;
        sashTailLong.zRot = -0.08f + fabricSway * 0.6f;

        sashTailShort.xRot = 0.04f + Math.abs(fabricSway) * 0.35f + s.crouchAmount * 0.10f;
        sashTailShort.zRot = 0.06f - fabricSway * 0.45f;
    }
}
