package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * HD Reversible Mood Octopus (TeeTurtle Ucan / Suzulen Ruh Hali Ahtapotu):
 * 512x512 (4x mikro-piksel) dokuya sahip, pofuduk 4 katmanli kubbe kafa ve 8 adet taç yapragi koldan olusur.
 * Omza basmak yerine oyuncunun yaninda havada suzulur, nefes alir gibi hafifce genisler ve takla atarak
 * mutlu (pembe) / kizgin (mavi) cift tarafli pelus haline donusur.
 */
public class OctopusPetModel extends PetModel {
    private static final int LEGS = 8;
    /** Takla pivotunun referans yuksekligi. */
    private static final float CENTER_Y = 0.0f;

    private static final int[][] LEG_UVS = {
        {54, 0}, {70, 0}, {86, 0}, {102, 0},
        {54, 8}, {70, 8}, {86, 8}, {102, 8}
    };
    private static final int[][] TIP_UVS = {
        {54, 16}, {68, 16}, {82, 16}, {96, 16},
        {54, 22}, {68, 22}, {82, 22}, {96, 22}
    };

    private final ModelPart flip, mantle;
    private final ModelPart[] legs = new ModelPart[LEGS];
    private final ModelPart[] tips = new ModelPart[LEGS];

    public OctopusPetModel(ModelPart root) {
        super(root);
        flip = root.getChild("flip");
        mantle = flip.getChild("mantle");
        for (int i = 0; i < LEGS; i++) {
            legs[i] = flip.getChild("leg" + i);
            tips[i] = legs[i].getChild("tip");
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition flip = root.addOrReplaceChild("flip", CubeListBuilder.create(),
                PartPose.offset(0f, CENTER_Y, 0f));

        // 4 Katmanli Pofuduk Kubbe (Mantle): Etek, Yuz Gobegi, Tepe Egimi ve Dugum
        flip.addOrReplaceChild("mantle",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-6.0f, -1.0f, -6.0f, 12f, 2f, 12f)     // Tier 1: Rim
                        .texOffs(0, 16).addBox(-6.5f, -6.0f, -6.5f, 13f, 5f, 13f)    // Tier 2: Belly & Face
                        .texOffs(0, 36).addBox(-5.5f, -9.0f, -5.5f, 11f, 3f, 11f)    // Tier 3: Crown
                        .texOffs(0, 52).addBox(-3.5f, -11.0f, -3.5f, 7f, 2f, 7f),    // Tier 4: Cap
                PartPose.offset(0f, 0f, 0f));

        // 8 Taç Yapragi Kol (45 derece radyal acili, uclari yukari kivrik petal plush yapisi)
        for (int i = 0; i < LEGS; i++) {
            float deg = i * 45f;
            float a = (float) Math.toRadians(deg);
            PartDefinition leg = flip.addOrReplaceChild("leg" + i,
                    CubeListBuilder.create().texOffs(LEG_UVS[i][0], LEG_UVS[i][1])
                            .addBox(-2.0f, -0.5f, 4.0f, 4f, 2f, 4f),
                    PartPose.offsetAndRotation(0f, 0.5f, 0f, (float) Math.toRadians(10f), -a, 0f));

            leg.addOrReplaceChild("tip",
                    CubeListBuilder.create().texOffs(TIP_UVS[i][0], TIP_UVS[i][1])
                            .addBox(-1.5f, -0.5f, 0.0f, 3f, 1f, 3f),
                    PartPose.offsetAndRotation(0f, 0.5f, 8.0f, (float) Math.toRadians(-22f), 0f, 0f));
        }

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(PetRenderState s) {
        resetPose();
        float t = s.age;
        float pi = (float) Math.PI;

        float p = s.flip;
        boolean flipping = p >= 0f && p <= 1f;
        float fly = s.flyAmount;
        float sleep = s.sleepProgress;
        float hurt = Math.min(1.0f, Math.max(0.0f, s.hurtTime / 10.0f));
        float swing = s.attackSwing > 0f ? sin(s.attackSwing * pi) : 0f;
        float lsq = s.landSquash;
        float parachute = (!s.onGround && s.yVel < -0.14f && !s.isSwimming && fly < 0.1f)
                ? Math.min(0.28f, (-s.yVel - 0.14f) * 0.70f) : 0f;

        // --- 1. Havada 360 Derece Sifir Yercekimi Taklasi ---
        float jump = 0f, spin = 0f, spread = 0f;
        if (flipping) {
            float ease = p * p * (3f - 2f * p);
            jump = 7f * sin(p * pi);
            spin = -2f * pi * ease;
            spread = 0.85f * sin(p * pi) * sin(p * pi);
        }

        // --- 1b. Idle Mini Sov (Pirouette / El Sallama) ---
        float idleSpin = 0f;
        float idleJump = 0f;
        float waveArm = 0f;
        boolean canIdleShow = !flipping && !s.angry && !s.isSwimming && fly < 0.05f
                && sleep < 0.1f && s.walkSpeed < 0.05f && !s.inWaterOrRain && hurt < 0.05f;
        if (canIdleShow) {
            float cyc = t % 280f;
            int turn = (int) (t / 280f);
            if (cyc < 26f) {
                float ip = cyc / 26f;
                float iease = ip * ip * (3f - 2f * ip);
                if ((turn & 1) == 0) {
                    idleSpin = 2f * pi * iease;
                    idleJump = 2.2f * sin(ip * pi);
                } else {
                    waveArm = sin(ip * pi);
                }
            }
        }

        flip.y = CENTER_Y - jump - idleJump + sleep * 2.8f + lsq * 1.5f;
        flip.z = hurt * 1.8f - swing * 2.4f;
        flip.xRot = spin + fly * 0.45f;
        flip.yRot = idleSpin + (s.isSwimming ? t * 0.20f : 0f) + (fly * t * 0.42f);

        // --- 2. Su (Kraken Yuzusu) ve Yagmur Tepkisi ---
        float swimPulse = 0f;
        float swimStroke = 0f;
        float krakenOpen = 0f;
        if (s.inWaterOrRain || s.isSwimming) {
            float spd = s.isSwimming ? 0.24f : 0.28f;
            float swimCycle = sin(t * spd);
            swimPulse = swimCycle;
            if (s.isSwimming) {
                // Kraken itisi: kollar genisce acilip guclu sekilde geriye toplanir
                swimStroke = swimCycle > 0f ? swimCycle * 0.55f : swimCycle * 0.25f;
                krakenOpen = swimCycle < 0f ? -swimCycle * 0.35f : 0f;
            } else {
                swimStroke = swimCycle > 0f ? swimCycle * 0.40f : swimCycle * 0.12f;
                flip.xRot += 0.15f * (1f - (flipping ? sin(p * pi) : 0f));
            }
        }

        // --- 3. Sprint & Kosu Ruzgar Savrulmasi ---
        float runFactor = Math.min(1.0f, s.walkSpeed * (s.isSprinting ? 1.4f : 1.0f));
        if (!s.inWaterOrRain && !flipping && fly < 0.1f) {
            flip.xRot -= 0.16f * runFactor;
        }

        // --- 4. Kubbe Squash & Stretch ve Nefes ---
        float breathRate = sleep > 0.2f ? 0.05f : 0.10f;
        float breath = sin(t * breathRate);
        float mantleY = 1.0f + 0.05f * breath - sleep * 0.12f - lsq * 0.18f;
        float mantleXZ = 1.0f - 0.025f * breath + sleep * 0.08f + lsq * 0.14f;

        if (flipping) {
            mantleY += 0.15f * sin(p * pi * 2f);
        } else if (s.inWaterOrRain || s.isSwimming) {
            mantleY += 0.12f * swimPulse;
            mantleXZ -= 0.06f * swimPulse;
        }

        mantle.yScale = Math.max(0.75f, mantleY);
        mantle.xScale = Math.max(0.80f, mantleXZ);
        mantle.zScale = Math.max(0.80f, mantleXZ);

        // --- 5. Kafa Hareketleri, Titreme ve Sevimli Sağa Sola Bakış / Yatış ---
        if (s.angry) {
            mantle.zRot = sin(t * 1.85f) * 0.045f;
            mantle.xRot = cos(t * 2.10f) * 0.030f - swing * 0.28f;
            mantle.yScale -= 0.04f;
            mantle.xScale += 0.03f;
            mantle.zScale += 0.03f;
            mantle.yRot = sin(t * 0.12f) * 0.12f;
        } else if (s.isSwimming) {
            mantle.xRot = -0.42f;
            mantle.yRot = sin(t * 0.06f) * 0.08f;
            mantle.zRot = sin(t * 0.06f) * 0.05f;
        } else if (!flipping) {
            float awake = 1f - sleep * 0.85f;
            float lookWave = sin(t * 0.045f);
            mantle.yRot = lookWave * 0.24f * awake;
            mantle.zRot = sin(t * 0.045f - 0.35f) * 0.20f * awake + hurt * 0.12f;
            mantle.xRot = sin(t * 0.09f) * 0.04f * awake + sleep * 0.14f - swing * 0.25f;
        }

        // --- 6. 8 Kolun Dinamik Fizik Dalgasi ---
        float waveSpeed = s.angry ? 0.22f : (fly > 0.1f ? 0.38f : (s.inWaterOrRain ? 0.28f : 0.13f));
        float tremble = s.angry ? sin(t * 2.5f) * 0.05f : 0f;

        for (int i = 0; i < LEGS; i++) {
            float ph = t * waveSpeed + (i * pi / 4.0f);
            float baseWave = 0.14f * sin(ph) * (1f - sleep * 0.7f);
            float tipWave = 0.26f * sin(ph - 0.70f) * (1f - sleep * 0.7f);

            float windDrift = (runFactor > 0f) ? sin(t * 0.35f + i * 0.75f) * 0.18f * runFactor : 0f;
            float angryTuck = s.angry ? 0.08f : 0f;
            float flyCurl = fly * (0.42f + 0.14f * sin(ph));
            float sleepDroop = -sleep * 0.42f;
            float hurtFlare = hurt * 0.55f;
            float swingKick = swing * sin(t * 1.4f + i) * 0.35f;

            legs[i].xRot = (float) Math.toRadians(10f) + baseWave + spread + tremble
                    - swimStroke + krakenOpen + windDrift + angryTuck
                    + flyCurl + sleepDroop + hurtFlare + parachute + swingKick + (lsq * 0.12f);

            tips[i].xRot = (float) Math.toRadians(-22f) + tipWave + (spread * 0.75f)
                    - (swimStroke * 0.75f) + (windDrift * 1.2f) + (fly * 0.30f) - (sleep * 0.20f);

            if (i == 1 && waveArm > 0.01f) {
                legs[i].xRot += waveArm * 0.85f;
                legs[i].zRot = waveArm * sin(t * 0.85f) * 0.35f;
            }
        }
    }
}
