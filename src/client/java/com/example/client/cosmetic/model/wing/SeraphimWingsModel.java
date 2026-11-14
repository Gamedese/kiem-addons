package com.example.client.cosmetic.model.wing;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Seraphim Melek Kanatlari (256x256 HD):
 * 0.25x olcekli mikro-piksel mimarisi.
 * Klasik kavisli melek/kartal kanadi silueti:
 *   - Ic kanat (left_wing / right_wing) omuzdan (x=+-1.8f, y=0.6f, z=2.3f) ~31.5 deg yukari ve disa yukselir.
 *   - Dis kanat (left_outer / right_outer) dirsekten disa ve asagiya dogru kavislenir.
 *   - Her bir tuy katmani (Coverts, Secondaries, Primaries) Z ekseninde 0.25f kalinlikta ayri duzlemlere
 *     yerlestirilmis ve disa dogru kademeli acilarla yelpaze gibi acilmistir.
 */
public class SeraphimWingsModel extends CosmeticModel {
    private static final float MODEL_SCALE = 0.25f;

    private final ModelPart root;
    private final ModelPart leftWing;
    private final ModelPart leftOuter;
    private final ModelPart rightWing;
    private final ModelPart rightOuter;

    public SeraphimWingsModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftWing = root.getChild("left_wing");
        this.leftOuter = this.leftWing.getChild("left_outer");
        this.rightWing = root.getChild("right_wing");
        this.rightOuter = this.rightWing.getChild("right_outer");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Sirt baglanti armasi (4x olcek -> 16x12x2.5 = 4x3x0.625 oyun birimi, omuzlari birlestirir)
        root.addOrReplaceChild("back_crest",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8.0f, -0.6f, 7.0f, 16.0f, 12.0f, 3.0f),
                PartPose.ZERO);

        // --- SOL KANAT (+X) ---
        // Omuz pivotu: x = +1.8f / 0.25 = +7.2f, y = 0.6f / 0.25 = 2.4f, z = 2.3f / 0.25 = 9.2f
        // zRot = -0.55f (-31.5 deg) ile kanat omuzdan yukari ve disa dogru yukselir.
        PartDefinition leftWing = root.addOrReplaceChild("left_wing",
                CubeListBuilder.create()
                        // Omuz kemigi / ust sirt hatti (sedef beyaz & altin serit)
                        .texOffs(40, 0).addBox(-1.0f, -3.0f, -1.5f, 26.0f, 6.0f, 2.0f),
                PartPose.offsetAndRotation(7.2f, 2.4f, 9.2f, 0.06f, -0.30f, -0.55f));

        // Tier 3: Ust ortu tuyleri (Coverts) - yelpaze acili, Z'de donusumlu ayrim
        addFeather(leftWing, "left_covert_1", 0, 18, 1.0f, 1.5f, 0.8f, 6.0f, 14.0f, 0.42f, false);
        addFeather(leftWing, "left_covert_2", 16, 18, 7.0f, 1.5f, 1.9f, 6.0f, 14.0f, 0.28f, false);
        addFeather(leftWing, "left_covert_3", 32, 18, 13.0f, 1.5f, 0.8f, 6.0f, 14.0f, 0.14f, false);
        addFeather(leftWing, "left_covert_4", 48, 18, 19.0f, 1.5f, 1.9f, 6.0f, 14.0f, 0.02f, false);

        // Tier 2: Orta katman tuyleri (Secondaries) - kademeli uzunluk ve yelpaze
        addFeather(leftWing, "left_sec_1", 68, 18, 2.0f, 3.0f, 3.0f, 7.0f, 20.0f, 0.48f, false);
        addFeather(leftWing, "left_sec_2", 86, 18, 8.0f, 3.0f, 4.1f, 7.0f, 23.0f, 0.34f, false);
        addFeather(leftWing, "left_sec_3", 104, 18, 14.0f, 3.0f, 3.0f, 7.0f, 25.0f, 0.20f, false);
        addFeather(leftWing, "left_sec_4", 122, 18, 20.0f, 3.0f, 4.1f, 7.0f, 26.0f, 0.06f, false);

        // Sol Dis Kanat (Dirsekten asagi ve disa kavislenen yay)
        // zRot = +0.75f -> dunya acisi = -0.55 + 0.75 = +0.20f (+11.5 deg asagi kavis)
        PartDefinition leftOuter = leftWing.addOrReplaceChild("left_outer",
                CubeListBuilder.create()
                        // Ust kanat ucu kemeri
                        .texOffs(100, 0).addBox(-1.0f, -2.5f, -1.0f, 32.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(24.5f, -0.5f, 2.5f, 0.0f, -0.10f, 0.75f));

        // Tier 1: Uzun Primary tuyler (disa dogru yelpaze gibi acilan bicaklar, 0.25f kalinlik)
        addFeather(leftOuter, "left_pri_1", 0, 50, 1.0f, 1.5f, 2.0f, 7.0f, 32.0f, -0.60f, false);
        addFeather(leftOuter, "left_pri_2", 18, 50, 6.5f, 1.5f, 3.1f, 7.0f, 38.0f, -0.76f, false);
        addFeather(leftOuter, "left_pri_3", 36, 50, 12.0f, 1.5f, 2.0f, 7.0f, 43.0f, -0.92f, false);
        addFeather(leftOuter, "left_pri_4", 54, 50, 17.5f, 1.5f, 3.1f, 7.0f, 46.0f, -1.06f, false);
        addFeather(leftOuter, "left_pri_5", 72, 50, 23.0f, 1.5f, 2.0f, 7.0f, 42.0f, -1.20f, false);
        addFeather(leftOuter, "left_pri_6", 90, 50, 28.0f, 1.0f, 3.1f, 7.0f, 35.0f, -1.36f, false);

        // --- SAG KANAT (-X, tam simetrik aynalanmis) ---
        PartDefinition rightWing = root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().mirror()
                        .texOffs(40, 0).addBox(-25.0f, -3.0f, -1.5f, 26.0f, 6.0f, 2.0f),
                PartPose.offsetAndRotation(-7.2f, 2.4f, 9.2f, 0.06f, 0.30f, 0.55f));

        addFeather(rightWing, "right_covert_1", 0, 18, -1.0f, 1.5f, 0.8f, 6.0f, 14.0f, -0.42f, true);
        addFeather(rightWing, "right_covert_2", 16, 18, -7.0f, 1.5f, 1.9f, 6.0f, 14.0f, -0.28f, true);
        addFeather(rightWing, "right_covert_3", 32, 18, -13.0f, 1.5f, 0.8f, 6.0f, 14.0f, -0.14f, true);
        addFeather(rightWing, "right_covert_4", 48, 18, -19.0f, 1.5f, 1.9f, 6.0f, 14.0f, -0.02f, true);

        addFeather(rightWing, "right_sec_1", 68, 18, -2.0f, 3.0f, 3.0f, 7.0f, 20.0f, -0.48f, true);
        addFeather(rightWing, "right_sec_2", 86, 18, -8.0f, 3.0f, 4.1f, 7.0f, 23.0f, -0.34f, true);
        addFeather(rightWing, "right_sec_3", 104, 18, -14.0f, 3.0f, 3.0f, 7.0f, 25.0f, -0.20f, true);
        addFeather(rightWing, "right_sec_4", 122, 18, -20.0f, 3.0f, 4.1f, 7.0f, 26.0f, -0.06f, true);

        PartDefinition rightOuter = rightWing.addOrReplaceChild("right_outer",
                CubeListBuilder.create().mirror()
                        .texOffs(100, 0).addBox(-31.0f, -2.5f, -1.0f, 32.0f, 5.0f, 2.0f),
                PartPose.offsetAndRotation(-24.5f, -0.5f, 2.5f, 0.0f, 0.10f, -0.75f));

        addFeather(rightOuter, "right_pri_1", 0, 50, -1.0f, 1.5f, 2.0f, 7.0f, 32.0f, 0.60f, true);
        addFeather(rightOuter, "right_pri_2", 18, 50, -6.5f, 1.5f, 3.1f, 7.0f, 38.0f, 0.76f, true);
        addFeather(rightOuter, "right_pri_3", 36, 50, -12.0f, 1.5f, 2.0f, 7.0f, 43.0f, 0.92f, true);
        addFeather(rightOuter, "right_pri_4", 54, 50, -17.5f, 1.5f, 3.1f, 7.0f, 46.0f, 1.06f, true);
        addFeather(rightOuter, "right_pri_5", 72, 50, -23.0f, 1.5f, 2.0f, 7.0f, 42.0f, 1.20f, true);
        addFeather(rightOuter, "right_pri_6", 90, 50, -28.0f, 1.0f, 3.1f, 7.0f, 35.0f, 1.36f, true);

        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void addFeather(PartDefinition parent, String name, int u, int v,
                                   float x, float y, float z, float width, float length,
                                   float zRot, boolean mirror) {
        CubeListBuilder builder = CubeListBuilder.create();
        if (mirror) {
            builder.mirror();
        }
        float boxX = mirror ? -width : 0.0f;
        builder.texOffs(u, v).addBox(boxX, 0.0f, 0.0f, width, length, 1.0f);
        parent.addOrReplaceChild(name, builder, PartPose.offsetAndRotation(x, y, z, 0.0f, 0.0f, zRot));
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        root.xScale = MODEL_SCALE;
        root.yScale = MODEL_SCALE;
        root.zScale = MODEL_SCALE;

        float t = s.ageInTicks;
        float energy = Math.min(1.0f, Math.max(s.flyAmount, s.walkSpeed * 0.85f));
        float sprintSweep = Math.max(0.0f, Math.min(1.0f, (s.walkSpeed - 0.55f) * 2.2f));
        float flySweep = s.flyAmount * 0.35f;

        // Yumusak idle nefes alma vs enerjik ucus cirpisi
        float idleBreath = sin(t * 0.08f) * 0.06f;
        float activeFlap = sin(t * 0.34f) * (0.28f * energy);
        float flapZ = idleBreath + activeFlap;

        // Dis kanat ucunun dalga gecikmesi
        float outerWave = sin(t * 0.08f - 0.5f) * 0.05f + sin(t * 0.34f - 0.65f) * (0.22f * energy);

        // Kosarken / ucarken geriye yatirma (sweep-back)
        float sweepBackY = 0.30f + 0.12f * energy + 0.36f * sprintSweep + flySweep
                + cos(t * 0.08f) * 0.03f - s.crouchAmount * 0.10f;
        float pitchX = 0.06f + 0.20f * sprintSweep + s.flyAmount * 0.18f + s.crouchAmount * 0.15f;

        leftWing.xRot = pitchX;
        leftWing.yRot = -sweepBackY;
        leftWing.zRot = -0.55f + flapZ + s.crouchAmount * 0.12f;
        leftOuter.yRot = -0.10f - 0.14f * sprintSweep;
        leftOuter.zRot = 0.75f + outerWave;

        rightWing.xRot = pitchX;
        rightWing.yRot = sweepBackY;
        rightWing.zRot = 0.55f - flapZ - s.crouchAmount * 0.12f;
        rightOuter.yRot = 0.10f + 0.14f * sprintSweep;
        rightOuter.zRot = -0.75f - outerWave;
    }
}
