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

/**
 * Demon Dragon Kanatlari (256x256 HD):
 * 0.25x olcekli mimariyle obsidyen iskelet kaburgalari (z=[-2,0]) ve koyu derimsi
 * kizil-siyah ejderha zarlari Z ekseninde ayri duzlemlere yerlestirilmistir:
 *   - inner zar : z = 0.5f -> [0.8, 2.2]
 *   - mid zar   : z = 2.0f -> [2.3, 3.7]
 *   - outer zar : z = 3.5f -> [3.8, 5.2]
 * Zarlar eklem noktalarinda birbirini orterek (bridge/overlap) kanat cirparken dahi
 * arada bosluk veya kopuk ucgen olusmasini tamamen engeller.
 */
public class DemonDragonWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;
    // dz = 2.0 -> (2.0 - 2 * 0.30) * 0.25 = 0.35f oyun ici kalinlik
    private static final CubeDeformation MEMBRANE_THIN = new CubeDeformation(0.0f, 0.0f, -0.30f);

    private final ModelPart root;
    private final ModelPart leftInner;
    private final ModelPart leftMid;
    private final ModelPart leftOuter;
    private final ModelPart rightInner;
    private final ModelPart rightMid;
    private final ModelPart rightOuter;

    public DemonDragonWingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftInner = root.getChild("left_inner");
        this.leftMid = this.leftInner.getChild("left_mid");
        this.leftOuter = this.leftMid.getChild("left_outer");

        this.rightInner = root.getChild("right_inner");
        this.rightMid = this.rightInner.getChild("right_mid");
        this.rightOuter = this.rightMid.getChild("right_outer");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Sirt omurga cikintisi (4x olcek)
        root.addOrReplaceChild("spine_crest",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0f, -2.0f, 6.5f, 8.0f, 24.0f, 6.0f),
                PartPose.ZERO);

        // --- SOL KANAT (+X) ---
        // Omurgaya sikica bagli ic pivot: x = +0.5f / 0.25 = +2.0f, y = 0.75f / 0.25 = 3.0f
        PartDefinition leftInner = root.addOrReplaceChild("left_inner",
                CubeListBuilder.create()
                        // Obsidyen ic kemik (omurga icine -1.0'dan baslar, bosluksuz)
                        .texOffs(32, 0).addBox(-1.0f, -3.0f, -2.0f, 30.0f, 6.0f, 2.0f)
                        // Ic kizil-siyah zar (x=-2..32 ile hem omurgaya hem mid eklemine koprusuz baglanir, z=[0.8, 2.2])
                        .texOffs(0, 44).addBox(-2.0f, 2.0f, 0.5f, 34.0f, 36.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(2.0f, 3.0f, 9.0f, 0.05f, -0.30f, -0.34f));

        PartDefinition leftMid = leftInner.addOrReplaceChild("left_mid",
                CubeListBuilder.create()
                        // Obsidyen on kol kemigi (z = [-2.0, 0.0])
                        .texOffs(100, 0).addBox(0.0f, -3.0f, -2.0f, 32.0f, 6.0f, 2.0f)
                        // Eklem pencesi / mahmuz
                        .texOffs(172, 0).addBox(-2.0f, -13.0f, -2.5f, 6.0f, 10.0f, 3.0f)
                        // Dikey parmak kaburgasi
                        .texOffs(194, 0).addBox(28.0f, 3.0f, -1.5f, 4.0f, 39.0f, 2.0f)
                        // Orta kanat zari (x=-5..34 ile inner-mid eklemini kesintisiz orter, z=[2.3, 3.7])
                        .texOffs(74, 44).addBox(-5.0f, 2.0f, 2.0f, 39.0f, 42.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(28.0f, 0.0f, 0.0f, 0.0f, -0.16f, 0.40f));

        leftMid.addOrReplaceChild("left_outer",
                CubeListBuilder.create()
                        // Ust parmak kaburgasi (z = [-2.0, 0.0])
                        .texOffs(32, 12).addBox(0.0f, -2.0f, -2.0f, 40.0f, 4.0f, 2.0f)
                        // Tek parca dis genis zar (x=-5..40 ile mid-outer eklemini tamamen orter, z=[3.8, 5.2])
                        // Kopuk ucgen parca yok; alt tirtikli kenarlar doku uzerinden kesintisiz sekillendirilir
                        .texOffs(158, 44).addBox(-5.0f, 1.0f, 3.5f, 45.0f, 48.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(31.0f, -0.5f, 0.0f, 0.0f, -0.14f, 0.26f));

        // --- SAG KANAT (-X, aynalanmis) ---
        PartDefinition rightInner = root.addOrReplaceChild("right_inner",
                CubeListBuilder.create().mirror()
                        .texOffs(32, 0).addBox(-29.0f, -3.0f, -2.0f, 30.0f, 6.0f, 2.0f)
                        .texOffs(0, 44).addBox(-32.0f, 2.0f, 0.5f, 34.0f, 36.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-2.0f, 3.0f, 9.0f, 0.05f, 0.30f, 0.34f));

        PartDefinition rightMid = rightInner.addOrReplaceChild("right_mid",
                CubeListBuilder.create().mirror()
                        .texOffs(100, 0).addBox(-32.0f, -3.0f, -2.0f, 32.0f, 6.0f, 2.0f)
                        .texOffs(172, 0).addBox(-4.0f, -13.0f, -2.5f, 6.0f, 10.0f, 3.0f)
                        .texOffs(194, 0).addBox(-32.0f, 3.0f, -1.5f, 4.0f, 39.0f, 2.0f)
                        .texOffs(74, 44).addBox(-34.0f, 2.0f, 2.0f, 39.0f, 42.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-28.0f, 0.0f, 0.0f, 0.0f, 0.16f, -0.40f));

        rightMid.addOrReplaceChild("right_outer",
                CubeListBuilder.create().mirror()
                        .texOffs(32, 12).addBox(-40.0f, -2.0f, -2.0f, 40.0f, 4.0f, 2.0f)
                        .texOffs(158, 44).addBox(-40.0f, 1.0f, 3.5f, 45.0f, 48.0f, 2.0f, MEMBRANE_THIN),
                PartPose.offsetAndRotation(-31.0f, -0.5f, 0.0f, 0.0f, 0.14f, -0.26f));

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
        float crouch = s.crouchAmount;

        // Bosta agir ve tehditkar yavas esneme; ucarken guclu derin vuruslar
        float idleFlex = sin(t * 0.055f) * 0.07f;
        float flyBeat = sin(t * 0.28f) * (0.42f * fly) + sin(t * 0.22f) * (0.12f * walk);

        // Egilince iceri dogru hafifce sarilma (crouch curl)
        float yawBase = 0.30f + 0.18f * fly - 0.22f * crouch + cos(t * 0.055f) * 0.03f;
        float innerZ = -0.34f + idleFlex + flyBeat + 0.16f * crouch;
        float midZ = 0.40f + sin(t * 0.055f - 0.5f) * 0.06f + sin(t * 0.28f - 0.6f) * (0.28f * fly) + 0.20f * crouch;
        float outerZ = 0.26f + sin(t * 0.28f - 1.1f) * (0.24f * fly) + 0.22f * crouch;

        float pitchX = 0.05f + 0.20f * fly + 0.18f * crouch;

        leftInner.xRot = pitchX;
        leftInner.yRot = -yawBase;
        leftInner.zRot = innerZ;
        leftMid.yRot = -0.16f + 0.16f * crouch;
        leftMid.zRot = midZ;
        leftOuter.yRot = -0.14f + 0.18f * crouch;
        leftOuter.zRot = outerZ;

        rightInner.xRot = pitchX;
        rightInner.yRot = yawBase;
        rightInner.zRot = -innerZ;
        rightMid.yRot = 0.16f - 0.16f * crouch;
        rightMid.zRot = -midZ;
        rightOuter.yRot = 0.14f - 0.18f * crouch;
        rightOuter.zRot = -outerZ;
    }
}
