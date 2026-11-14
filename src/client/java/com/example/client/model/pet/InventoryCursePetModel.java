package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Toji Fushiguro'nun Lanet Solucani (Inventory Curse / Silah Zulasi):
 * Blockbench sahnesiyle (inventory_curse.bbmodel) 1:1 senkronize, 512x512 (4x mikro-piksel)
 * dokuya sahip, sol omuzdan ensenin arkasina, sag omuzdan gogus caprazina ve bele dolanan model.
 */
public class InventoryCursePetModel extends PetModel {
    private final ModelPart wormHead;
    private final ModelPart eyeR, eyeL, mouth, hairTuft, frontPawR, frontPawL;
    private final ModelPart invertedSpear;
    private final ModelPart bodyCoil;
    private final ModelPart[] segments = new ModelPart[14];
    private final ModelPart tailProngUpper, tailProngLower;
    private final ModelPart proleg1, proleg2, proleg3;

    private static final float[] SEG_BASE_X_ROT = {
        rad(10f), rad(6f), rad(6f), rad(-10f), rad(-18f), rad(-10f), rad(-6f),
        rad(-4f), rad(0f), rad(8f), rad(0f), rad(0f), rad(-8f), rad(-12f)
    };
    private static final float[] SEG_BASE_Y_ROT = {
        rad(-20f), rad(-10f), rad(10f), rad(20f), rad(0f), rad(-8f), rad(-10f),
        rad(-8f), rad(18f), rad(35f), rad(8f), rad(-8f), rad(-28f), rad(-20f)
    };
    private static final float[] SEG_BASE_Z_ROT = {
        rad(-8f), rad(-4f), rad(4f), rad(12f), rad(22f), rad(34f), rad(40f),
        rad(38f), rad(22f), rad(8f), rad(-4f), rad(-6f), rad(-12f), rad(-22f)
    };

    public InventoryCursePetModel(ModelPart root) {
        super(root);
        wormHead = root.getChild("worm_head");
        eyeR = wormHead.getChild("eye_R");
        eyeL = wormHead.getChild("eye_L");
        mouth = wormHead.getChild("mouth");
        hairTuft = wormHead.getChild("hair_tuft");
        frontPawR = wormHead.getChild("front_paw_R");
        frontPawL = wormHead.getChild("front_paw_L");
        invertedSpear = wormHead.getChild("inverted_spear");

        bodyCoil = root.getChild("body_coil");
        for (int i = 0; i < 14; i++) {
            segments[i] = bodyCoil.getChild("seg_" + i);
        }
        tailProngUpper = bodyCoil.getChild("tail_prong_upper");
        tailProngLower = bodyCoil.getChild("tail_prong_lower");
        proleg1 = bodyCoil.getChild("proleg_1");
        proleg2 = bodyCoil.getChild("proleg_2");
        proleg3 = bodyCoil.getChild("proleg_3");
    }

    private static float rad(float deg) {
        return deg * ((float) Math.PI / 180f);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. WORM HEAD GROUP (Origin in Blockbench: [-5.5, 24.5, -1.8] -> Java: [-5.5f, -0.5f, -1.8f])
        PartDefinition head = root.addOrReplaceChild("worm_head",
                CubeListBuilder.create()
                        // head_main: from [-8.5, 22.5, -4.3] to [-2.5, 27.5, 0.7]
                        .texOffs(0, 0).addBox(-3.0f, -3.0f, -2.5f, 6f, 5f, 5f)
                        // head_dome: from [-7.5, 27.0, -3.8] to [-3.5, 28.0, 0.2]
                        .texOffs(0, 26).addBox(-2.0f, -3.5f, -2.0f, 4f, 1f, 4f),
                PartPose.offsetAndRotation(-5.5f, -0.5f, -1.8f, rad(-6f), rad(12f), rad(-8f)));

        head.addOrReplaceChild("eye_R",
                CubeListBuilder.create().texOffs(28, 0).addBox(-1.0f, -1.0f, -0.5f, 2f, 2f, 1f),
                PartPose.offsetAndRotation(-1.7f, -1.1f, -2.45f, 0f, 0f, rad(6f)));

        head.addOrReplaceChild("eye_L",
                CubeListBuilder.create().texOffs(36, 0).addBox(-1.0f, -1.0f, -0.5f, 2f, 2f, 1f),
                PartPose.offsetAndRotation(1.7f, -1.1f, -2.45f, 0f, 0f, rad(-6f)));

        head.addOrReplaceChild("mouth",
                CubeListBuilder.create().texOffs(44, 0).addBox(-1.0f, -1.0f, -0.5f, 2f, 2f, 1f),
                PartPose.offset(0.0f, 0.7f, -2.3f));

        head.addOrReplaceChild("hair_tuft",
                CubeListBuilder.create().texOffs(52, 0).addBox(-1.5f, -2.0f, -0.5f, 3f, 2f, 1f),
                PartPose.offset(0.0f, -3.4f, 0.0f));

        head.addOrReplaceChild("front_paw_R",
                CubeListBuilder.create().texOffs(62, 0).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offsetAndRotation(-1.3f, 2.3f, -1.5f, rad(-12f), 0f, 0f));

        head.addOrReplaceChild("front_paw_L",
                CubeListBuilder.create().texOffs(62, 0).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offsetAndRotation(1.3f, 2.3f, -1.5f, rad(-12f), 0f, 0f));

        // INVERTED SPEAR OF HEAVEN (Stuffed inside the mouth, hilt sticking out!)
        PartDefinition spear = head.addOrReplaceChild("inverted_spear",
                CubeListBuilder.create()
                        // spear_guard: (w=3, h=3, d=1)
                        .texOffs(76, 0).addBox(-1.5f, -1.5f, -0.7f, 3f, 3f, 1f)
                        // spear_handle: (w=2, h=2, d=6) sticking forward out of mouth
                        .texOffs(88, 0).addBox(-0.75f, -0.75f, -5.7f, 1.5f, 1.5f, 5.5f)
                        // spear_pommel ring: (w=2, h=2, d=1)
                        .texOffs(60, 0).addBox(-1.0f, -1.0f, -6.9f, 2f, 2f, 1f)
                        // spear_blade inside mouth
                        .texOffs(76, 10).addBox(-1.0f, -1.0f, 0.3f, 2f, 2f, 2f),
                PartPose.offsetAndRotation(0.0f, 0.7f, -2.7f, rad(-14f), rad(10f), rad(-5f)));

        // 2. BODY COIL GROUP (Origin at [0, 24, 0] -> Java [0, 0, 0])
        PartDefinition coil = root.addOrReplaceChild("body_coil",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // 14 Chained Segments (seg_0 to seg_13) matching Blockbench exact coordinates
        coil.addOrReplaceChild("seg_0",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(-4.2f, -1.0f, 0.8f, SEG_BASE_X_ROT[0], SEG_BASE_Y_ROT[0], SEG_BASE_Z_ROT[0]));

        coil.addOrReplaceChild("seg_1",
                CubeListBuilder.create().texOffs(36, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(-2.2f, -1.2f, 2.5f, SEG_BASE_X_ROT[1], SEG_BASE_Y_ROT[1], SEG_BASE_Z_ROT[1]));

        coil.addOrReplaceChild("seg_2",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(1.0f, -1.3f, 2.8f, SEG_BASE_X_ROT[2], SEG_BASE_Y_ROT[2], SEG_BASE_Z_ROT[2]));

        coil.addOrReplaceChild("seg_3",
                CubeListBuilder.create().texOffs(36, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(3.8f, -1.2f, 1.5f, SEG_BASE_X_ROT[3], SEG_BASE_Y_ROT[3], SEG_BASE_Z_ROT[3]));

        coil.addOrReplaceChild("seg_4",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(4.4f, 0.0f, -1.2f, SEG_BASE_X_ROT[4], SEG_BASE_Y_ROT[4], SEG_BASE_Z_ROT[4]));

        coil.addOrReplaceChild("seg_5",
                CubeListBuilder.create().texOffs(18, 12).addBox(-2f, -2.5f, -2f, 4f, 5f, 4f),
                PartPose.offsetAndRotation(3.2f, 1.9f, -2.2f, SEG_BASE_X_ROT[5], SEG_BASE_Y_ROT[5], SEG_BASE_Z_ROT[5]));

        coil.addOrReplaceChild("seg_6",
                CubeListBuilder.create().texOffs(18, 12).addBox(-2f, -2.5f, -2f, 4f, 5f, 4f),
                PartPose.offsetAndRotation(1.0f, 4.1f, -2.3f, SEG_BASE_X_ROT[6], SEG_BASE_Y_ROT[6], SEG_BASE_Z_ROT[6]));

        coil.addOrReplaceChild("seg_7",
                CubeListBuilder.create().texOffs(18, 12).addBox(-2f, -2.5f, -2f, 4f, 5f, 4f),
                PartPose.offsetAndRotation(-1.2f, 6.3f, -2.1f, SEG_BASE_X_ROT[7], SEG_BASE_Y_ROT[7], SEG_BASE_Z_ROT[7]));

        coil.addOrReplaceChild("seg_8",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(-3.2f, 8.2f, -1.0f, SEG_BASE_X_ROT[8], SEG_BASE_Y_ROT[8], SEG_BASE_Z_ROT[8]));

        coil.addOrReplaceChild("seg_9",
                CubeListBuilder.create().texOffs(36, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(-3.2f, 8.8f, 1.5f, SEG_BASE_X_ROT[9], SEG_BASE_Y_ROT[9], SEG_BASE_Z_ROT[9]));

        coil.addOrReplaceChild("seg_10",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(-1.2f, 9.0f, 2.8f, SEG_BASE_X_ROT[10], SEG_BASE_Y_ROT[10], SEG_BASE_Z_ROT[10]));

        coil.addOrReplaceChild("seg_11",
                CubeListBuilder.create().texOffs(36, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(1.5f, 9.0f, 2.8f, SEG_BASE_X_ROT[11], SEG_BASE_Y_ROT[11], SEG_BASE_Z_ROT[11]));

        coil.addOrReplaceChild("seg_12",
                CubeListBuilder.create().texOffs(0, 12).addBox(-2f, -2f, -2f, 4f, 4f, 4f),
                PartPose.offsetAndRotation(3.6f, 9.0f, 0.8f, SEG_BASE_X_ROT[12], SEG_BASE_Y_ROT[12], SEG_BASE_Z_ROT[12]));

        coil.addOrReplaceChild("seg_13",
                CubeListBuilder.create().texOffs(54, 12).addBox(-1.5f, -2f, -1.5f, 3f, 4f, 3f),
                PartPose.offsetAndRotation(3.5f, 9.2f, -1.9f, SEG_BASE_X_ROT[13], SEG_BASE_Y_ROT[13], SEG_BASE_Z_ROT[13]));

        // Split 2-Pronged Tail Tip
        coil.addOrReplaceChild("tail_prong_upper",
                CubeListBuilder.create().texOffs(68, 0).addBox(-0.5f, -1.0f, -0.5f, 1f, 2f, 1f),
                PartPose.offsetAndRotation(1.5f, 9.2f, -3.6f, 0f, rad(-12f), rad(-65f)));

        coil.addOrReplaceChild("tail_prong_lower",
                CubeListBuilder.create().texOffs(68, 0).addBox(-0.5f, -1.0f, -0.5f, 1f, 2f, 1f),
                PartPose.offsetAndRotation(1.7f, 10.6f, -3.5f, 0f, rad(-12f), rad(-105f)));

        // Underside Caterpillar Prolegs
        coil.addOrReplaceChild("proleg_1",
                CubeListBuilder.create().texOffs(62, 0).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offsetAndRotation(1.5f, 3.0f, -2.3f, 0f, 0f, rad(35f)));

        coil.addOrReplaceChild("proleg_2",
                CubeListBuilder.create().texOffs(62, 0).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offsetAndRotation(-0.5f, 5.3f, -2.3f, 0f, 0f, rad(40f)));

        coil.addOrReplaceChild("proleg_3",
                CubeListBuilder.create().texOffs(62, 0).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offsetAndRotation(-2.5f, 7.5f, -2.3f, 0f, 0f, rad(40f)));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(PetRenderState s) {
        super.setupAnim(s);
        float t = s.age;
        float walkSpeed = Math.min(s.walkSpeed, 1.25f);
        float walkCycle = s.walkPos * 1.3324f;
        float walkBounce = Math.abs(sin(s.walkPos * 0.6662f)) * walkSpeed;
        float stepJiggle = sin(walkCycle) * walkSpeed;
        float lsq = s.landSquash;
        float sleep = Math.min(1.0f, Math.max(0.0f, s.sleepProgress));
        float awake = 1.0f - sleep;
        float hurt = s.hurtTime > 0f ? Math.min(1.0f, s.hurtTime / 10.0f) : 0f;
        float swing = s.attackSwing > 0f ? sin(Math.min(1.0f, s.attackSwing) * (float) Math.PI) : 0f;
        float fly = Math.min(1.0f, Math.max(0.0f, s.flyAmount));

        // 1. Toggle Inverted Spear of Heaven visibility via Customize option (`withSword`)
        if (s.withSword) {
            invertedSpear.xScale = 1.0f;
            invertedSpear.yScale = 1.0f;
            invertedSpear.zScale = 1.0f;
        } else {
            invertedSpear.xScale = 0.0f;
            invertedSpear.yScale = 0.0f;
            invertedSpear.zScale = 0.0f;
        }

        // 2. Sleepy Breathing & Periodic Neck Nuzzle (from JJK Hidden Inventory anime!)
        float breath = sin(t * (sleep > 0.2f ? 0.05f : 0.09f));
        float nuzzle = burst(t % 160f, 28f) * awake; // Every 8s, rubs cheek affectionately toward Toji's neck!

        wormHead.x = -5.5f + (nuzzle * 0.35f) + (hurt * 0.25f);
        wormHead.y = -0.5f - (breath * 0.15f) - (walkBounce * 0.35f) + (lsq * 0.35f) + (s.crouching ? 0.5f : 0f) + (sleep * 0.45f);
        wormHead.z = -1.8f - (swing * 0.45f) + (hurt * 0.35f);

        // "Ma-ma..." two-syllable mouth murmur every 120 ticks
        float mamaPhase = (t + 30f) % 120f;
        float mama = (!s.withSword && awake > 0.3f && mamaPhase < 24f)
                ? Math.abs(sin(mamaPhase * ((float) Math.PI * 2f / 24f)))
                : 0f;

        wormHead.xRot = rad(-6f) + (breath * 0.045f) + (stepJiggle * 0.06f) - (mama * 0.08f) + (s.crouching ? 0.15f : 0f) + (sleep * 0.22f) - (swing * 0.14f) + (hurt * 0.18f);
        wormHead.yRot = rad(12f) + sin(t * 0.035f) * 0.10f * awake - (nuzzle * 0.12f);
        wormHead.zRot = rad(-8f) + (nuzzle * 0.16f) + (lsq * 0.12f) + (sleep * 0.08f);

        // 3. Asymmetric Squinted Eye Blinks (Left eye blinks 2 ticks before right eye) + Sleep close
        float blinkL = Math.max(burst(t % 90f, 10f), sleep * 0.78f);
        float blinkR = Math.max(burst((t + 2f) % 90f, 10f), sleep * 0.78f);
        eyeL.yScale = 1.0f - (blinkL * 0.65f);
        eyeL.xScale = 1.0f + (blinkL * 0.12f);
        eyeR.yScale = 1.0f - (blinkR * 0.65f);
        eyeR.xScale = 1.0f + (blinkR * 0.12f);

        // 4. Mouth Gumming & Sword-Holding Stretch
        if (s.withSword) {
            float regrip = burst((t + 80f) % 140f, 16f);
            mouth.yScale = 1.35f + 0.05f * sin(t * 0.15f);
            mouth.xScale = 1.20f;
            invertedSpear.xRot = rad(-14f) + sin(t * 0.09f - 0.6f) * 0.04f + (stepJiggle * 0.08f) - (swing * 0.28f);
            invertedSpear.z = -2.7f + (regrip * 0.55f) - (swing * 0.40f); // Periodic throat re-gulp sucking the sword slightly inward!
        } else {
            mouth.yScale = 1.0f + 0.04f * sin(t * 0.22f) + (mama * 0.35f);
            mouth.xScale = 1.0f - (mama * 0.10f);
        }

        // 5. Three Hair Sprouts Secondary Drag & Front Stub-Paws Kneading
        hairTuft.xRot = sin(t * 0.09f - 0.8f) * 0.10f + fly * sin(t * 0.65f) * 0.28f;
        hairTuft.zRot = sin(t * 0.12f) * 0.08f + fly * cos(t * 0.55f) * 0.18f;

        float knead = sin(t * 0.18f) * 0.10f + (nuzzle * sin(t * 1.1f) * 0.20f);
        frontPawL.xRot = rad(-12f) + knead;
        frontPawR.xRot = rad(-12f) - knead;

        // 6. Body Coil: Follows torso pitch on crouch + Peristaltic Wave along all 14 segments!
        bodyCoil.xRot = s.crouching ? 0.48f : 0f;
        bodyCoil.y = s.crouching ? -0.4f : 0f;
        bodyCoil.z = s.crouching ? 0.5f : 0f;

        for (int i = 0; i < 14; i++) {
            float wave = sin(t * 0.12f - i * 0.45f + s.walkPos * 0.6f);
            segments[i].xScale = 1.0f + (wave * 0.035f) + (lsq * 0.06f);
            segments[i].yScale = 1.0f - (wave * 0.025f) - (lsq * 0.08f);
            segments[i].zScale = 1.0f + (wave * 0.035f);
            segments[i].zRot = SEG_BASE_Z_ROT[i] + (wave * 0.025f);
        }

        // Dinamik Pelerin Temas Kontrolu:
        // Bel hizasinda (y=9.0) pelerin Z = 2.0 + 9.0 * sin(capeAngle) konumundadir.
        // Pelerin < 21 derece egimdeyken (dururken/yavas yururken) seg_10 ve seg_11'e (Z=4.8) temas eder -> gizlenir.
        // Kosup pelerin 21 derecenin ustune havalandiginda (veya pelerin kapaliyken) tekrar gorunur!
        boolean capeTouchingWaist = s.hasCape && s.capeAngleDeg < 21.0f;
        if (capeTouchingWaist) {
            segments[10].xScale = 0.0f;
            segments[10].yScale = 0.0f;
            segments[10].zScale = 0.0f;
            segments[11].xScale = 0.0f;
            segments[11].yScale = 0.0f;
            segments[11].zScale = 0.0f;
            // Yan gecis bogumlarini (seg_9 ve seg_12) pelerin kenarinin altina cek ki ince cizgi tasmasin
            segments[9].z = 0.1f;
            segments[12].z = -0.2f;
        } else {
            segments[9].z = 1.5f;
            segments[12].z = 0.8f;
        }

        // 7. Split Two-Pronged Tail Scissor & Proleg Grip Wave
        float tailTwitch = burst((t + 65f) % 110f, 14f) * sin(t * 1.8f) * 0.16f;
        float scissor = sin(t * 0.12f) * 0.08f + (walkBounce * 0.12f) + tailTwitch;
        tailProngUpper.zRot = rad(-65f) + scissor;
        tailProngLower.zRot = rad(-105f) - scissor;

        proleg1.zRot = rad(35f) + sin(t * 0.18f + walkCycle) * 0.12f;
        proleg2.zRot = rad(40f) + sin(t * 0.18f - 0.9f + walkCycle) * 0.12f;
        proleg3.zRot = rad(40f) + sin(t * 0.18f - 1.8f + walkCycle) * 0.12f;
    }
}
