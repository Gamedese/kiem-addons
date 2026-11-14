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
 * Ince koyu tac uzerinde turuncu-krem-siyah uclu tilki (kitsune) kulaklari.
 * UV duzeni: assets/modid/textures/cosmetic/hat_kitsune.png (64x64).
 */
public class KitsuneEarsModel extends CosmeticModel {
    private final ModelPart earL, earR;

    public KitsuneEarsModel(ModelPart root) {
        super(root);
        ModelPart band = root.getChild("band");
        this.earL = band.getChild("earL");
        this.earR = band.getChild("earR");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition band = root.addOrReplaceChild("band",
                CubeListBuilder.create().texOffs(1, 1).addBox(-4.3f, -8.6f, -0.6f, 8.6f, 1.0f, 1.2f),
                PartPose.ZERO);
        band.addOrReplaceChild("bandL",
                CubeListBuilder.create().texOffs(22, 1).addBox(3.7f, -8.2f, -0.6f, 0.9f, 3.0f, 1.2f),
                PartPose.ZERO);
        band.addOrReplaceChild("bandR",
                CubeListBuilder.create().texOffs(28, 1).addBox(-4.6f, -8.2f, -0.6f, 0.9f, 3.0f, 1.2f),
                PartPose.ZERO);

        // Sol tilki kulagi (v=8)
        PartDefinition el = band.addOrReplaceChild("earL",
                CubeListBuilder.create().texOffs(0, 8).addBox(-1.5f, -3.0f, -1.0f, 3.0f, 3.0f, 2.0f),
                PartPose.offsetAndRotation(2.6f, -8.3f, -0.2f, 0.08f, -0.15f, 0.26f));
        el.addOrReplaceChild("earLMid",
                CubeListBuilder.create().texOffs(12, 8).addBox(-1.0f, -5.0f, -1.0f, 2.0f, 2.0f, 2.0f),
                PartPose.ZERO);
        el.addOrReplaceChild("earLTip",
                CubeListBuilder.create().texOffs(22, 8).addBox(-0.5f, -6.5f, -0.5f, 1.0f, 2.0f, 1.0f),
                PartPose.ZERO);
        el.addOrReplaceChild("earLFluff",
                CubeListBuilder.create().texOffs(28, 8).addBox(-0.8f, -2.5f, -1.2f, 1.6f, 2.0f, 0.6f),
                PartPose.ZERO);

        // Sag tilki kulagi (v=16)
        PartDefinition er = band.addOrReplaceChild("earR",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1.5f, -3.0f, -1.0f, 3.0f, 3.0f, 2.0f),
                PartPose.offsetAndRotation(-2.6f, -8.3f, -0.2f, 0.08f, 0.15f, -0.26f));
        er.addOrReplaceChild("earRMid",
                CubeListBuilder.create().texOffs(12, 16).addBox(-1.0f, -5.0f, -1.0f, 2.0f, 2.0f, 2.0f),
                PartPose.ZERO);
        er.addOrReplaceChild("earRTip",
                CubeListBuilder.create().texOffs(22, 16).addBox(-0.5f, -6.5f, -0.5f, 1.0f, 2.0f, 1.0f),
                PartPose.ZERO);
        er.addOrReplaceChild("earRFluff",
                CubeListBuilder.create().texOffs(28, 16).addBox(-0.8f, -2.5f, -1.2f, 1.6f, 2.0f, 0.6f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        float t = s.ageInTicks;
        float walkWobble = sin(s.walkPos * 0.6662f) * 0.08f * s.walkSpeed;

        // Periyodik kulak segirmesi (sirayla sol/sag)
        float phase = (t + 25.0f) % 130.0f;
        float twitch = phase < 12.0f ? sin((phase / 12.0f) * (float) Math.PI) * 0.24f : 0.0f;
        boolean leftTurn = (((int) ((t + 25.0f) / 130.0f)) & 1) == 0;

        earL.zRot = 0.26f + walkWobble + (leftTurn ? twitch : 0.0f);
        earR.zRot = -0.26f - walkWobble - (!leftTurn ? twitch : 0.0f);
        earL.xRot = 0.08f + sin(t * 0.06f) * 0.03f + (leftTurn ? twitch * 0.35f : 0.0f);
        earR.xRot = 0.08f + sin(t * 0.06f + 0.5f) * 0.03f + (!leftTurn ? twitch * 0.35f : 0.0f);
    }
}
