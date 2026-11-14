package com.example.client.cosmetic.model.wing;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Arcane Butterfly Kanatlari (256x256 HD):
 * Sifirdan tasarlanmis ince (0.20f kalinlik) kavisli cok-seritli vitray kelebek kanatlari.
 * Ust (4 serit) ve alt (3 serit) kanat panelleri X boyunca cakismadan yan yana kavislenir,
 * yari saydam (entityTranslucent) camgobegi/ametist dokuyla cift-saydamlik izi ve Z-fighting olusturmaz.
 */
public class ArcaneButterflyWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    // dz = 1.0 -> (1.0 - 2 * 0.10) * 0.25 = 0.20f oyun ici zarif ince kalinlik
    private static final CubeDeformation CRYSTAL_THIN = new CubeDeformation(0.0f, 0.0f, -0.10f);

    private final ModelPart root;
    private final ModelPart leftUpper;
    private final ModelPart leftLower;
    private final ModelPart rightUpper;
    private final ModelPart rightLower;

    public ArcaneButterflyWingsModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.root = root;
        this.leftUpper = root.getChild("left_upper");
        this.leftLower = root.getChild("left_lower");
        this.rightUpper = root.getChild("right_upper");
        this.rightLower = root.getChild("right_lower");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Kristal sirt tokasi (altin & ametist)
        root.addOrReplaceChild("crystal_core",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0f, -0.8f, 7.0f, 6.0f, 16.0f, 3.0f),
                PartPose.ZERO);

        // Sol Ust Kanat (+X): 4 basamakli kavisli ince vitray seridi (0.20f kalinlik)
        root.addOrReplaceChild("left_upper",
                CubeListBuilder.create()
                        .texOffs(24, 0).addBox(0.0f, -14.0f, 0.0f, 14.0f, 20.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(58, 0).addBox(14.0f, -24.0f, -0.5f, 14.0f, 30.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(92, 0).addBox(28.0f, -32.0f, -1.0f, 14.0f, 36.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(126, 0).addBox(42.0f, -30.0f, -0.5f, 12.0f, 28.0f, 1.0f, CRYSTAL_THIN),
                PartPose.offsetAndRotation(3.6f, 4.2f, 9.2f, 0.04f, -0.42f, -0.10f));

        // Sol Alt Kanat (+X): 3 basamakli kavisli ince vitray seridi (ust kanattan Z ile ayrik)
        root.addOrReplaceChild("left_lower",
                CubeListBuilder.create()
                        .texOffs(24, 42).addBox(0.0f, -2.0f, 0.0f, 12.0f, 26.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(54, 42).addBox(12.0f, -2.0f, 0.4f, 14.0f, 32.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(88, 42).addBox(26.0f, -2.0f, 0.0f, 12.0f, 22.0f, 1.0f, CRYSTAL_THIN),
                PartPose.offsetAndRotation(3.6f, 9.7f, 10.5f, 0.06f, -0.36f, 0.08f));

        // Sag Ust Kanat (-X, aynalanmis)
        root.addOrReplaceChild("right_upper",
                CubeListBuilder.create().mirror()
                        .texOffs(24, 0).addBox(-14.0f, -14.0f, 0.0f, 14.0f, 20.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(58, 0).addBox(-28.0f, -24.0f, -0.5f, 14.0f, 30.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(92, 0).addBox(-42.0f, -32.0f, -1.0f, 14.0f, 36.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(126, 0).addBox(-54.0f, -30.0f, -0.5f, 12.0f, 28.0f, 1.0f, CRYSTAL_THIN),
                PartPose.offsetAndRotation(-3.6f, 4.2f, 9.2f, 0.04f, 0.42f, 0.10f));

        // Sag Alt Kanat (-X, aynalanmis)
        root.addOrReplaceChild("right_lower",
                CubeListBuilder.create().mirror()
                        .texOffs(24, 42).addBox(-12.0f, -2.0f, 0.0f, 12.0f, 26.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(54, 42).addBox(-26.0f, -2.0f, 0.4f, 14.0f, 32.0f, 1.0f, CRYSTAL_THIN)
                        .texOffs(88, 42).addBox(-38.0f, -2.0f, 0.0f, 12.0f, 22.0f, 1.0f, CRYSTAL_THIN),
                PartPose.offsetAndRotation(-3.6f, 9.7f, 10.5f, 0.06f, 0.36f, -0.08f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float fly = s.flyAmount;
        float walk = s.walkSpeed;

        // Narin hizli kelebek titremesi (idle/walk) ve ucarken genis isiltili kanat vurusu
        float idleFlutter = sin(t * 0.48f) * (0.14f + 0.10f * walk);
        float flyBeat = sin(t * 0.62f) * (0.48f * fly);
        float upperYaw = 0.42f + idleFlutter + flyBeat + s.crouchAmount * 0.12f;

        // Alt kanat hafif faz gecikmesiyle takip eder
        float lowerFlutter = sin(t * 0.48f - 0.5f) * (0.12f + 0.08f * walk);
        float lowerFlyBeat = sin(t * 0.62f - 0.4f) * (0.40f * fly);
        float lowerYaw = 0.36f + lowerFlutter + lowerFlyBeat + s.crouchAmount * 0.10f;

        float shimmerPitch = 0.04f + sin(t * 0.31f) * 0.05f * (0.3f + fly) + 0.12f * fly;

        leftUpper.xRot = shimmerPitch;
        leftUpper.yRot = -upperYaw;
        leftUpper.zRot = -0.10f + sin(t * 0.62f) * (0.10f * fly);

        leftLower.xRot = shimmerPitch + 0.04f;
        leftLower.yRot = -lowerYaw;
        leftLower.zRot = 0.08f + sin(t * 0.62f - 0.4f) * (0.08f * fly);

        rightUpper.xRot = shimmerPitch;
        rightUpper.yRot = upperYaw;
        rightUpper.zRot = 0.10f - sin(t * 0.62f) * (0.10f * fly);

        rightLower.xRot = shimmerPitch + 0.04f;
        rightLower.yRot = lowerYaw;
        rightLower.zRot = -0.08f - sin(t * 0.62f - 0.4f) * (0.08f * fly);
    }
}
