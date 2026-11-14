package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * HD Pofuduk Hamster (v1.5): V1'in ikonik 512x512 mikro-piksel dokusu ve yuz ifadesi korunarak,
 * bagimsiz ayak hiyerarsisi ve canli durum animasyonlari (yemek kemirme, uyuklama, inis esnemesi, omuz bakisi) eklendi.
 */
public class HamsterHdPetModel extends PetModel {
    private final ModelPart body, head, cheekL, cheekR, earL, earR, nose, pawL, pawR, footL, footR, tail, seed;

    public HamsterHdPetModel(ModelPart root) {
        super(root);
        body = root.getChild("body");
        footL = root.getChild("footL");
        footR = root.getChild("footR");
        head = root.getChild("head");
        cheekL = head.getChild("cheekL");
        cheekR = head.getChild("cheekR");
        earL = head.getChild("earL");
        earR = head.getChild("earR");
        nose = head.getChild("nose");
        pawL = body.getChild("pawL");
        pawR = body.getChild("pawR");
        tail = body.getChild("tail");
        seed = body.getChild("seed");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. Tombul Govde Grubu (y = -1..-8)
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -8f, -4f, 10f, 7f, 8f),
                PartPose.ZERO);

        body.addOrReplaceChild("back_puff",
                CubeListBuilder.create().texOffs(0, 38).addBox(-4f, -7f, 4f, 8f, 6f, 1f),
                PartPose.ZERO);

        body.addOrReplaceChild("belly",
                CubeListBuilder.create().texOffs(0, 20).addBox(-4f, -6.5f, -4.6f, 8f, 5f, 1f),
                PartPose.ZERO);

        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(64, 30).addBox(-1f, -1f, 0f, 2f, 2f, 2f),
                PartPose.offset(0f, -2.5f, 5.0f));

        body.addOrReplaceChild("pawL",
                CubeListBuilder.create().texOffs(64, 20).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(1.6f, -4.3f, -4.6f));

        body.addOrReplaceChild("pawR",
                CubeListBuilder.create().texOffs(74, 20).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(-1.6f, -4.3f, -4.6f));

        // Aycekirdegi (yemek yerken iki pati arasinda belirir)
        body.addOrReplaceChild("seed",
                CubeListBuilder.create().texOffs(96, 30).addBox(-1f, -1.5f, -1f, 2f, 3f, 2f),
                PartPose.offset(0f, -5.8f, -5.3f));

        // Arka Ayaklar root altinda bagimsiz — govde esnerken ezilmez
        root.addOrReplaceChild("footL",
                CubeListBuilder.create().texOffs(0, 30).addBox(-1.5f, -0.5f, -2f, 3f, 1f, 4f),
                PartPose.offset(3.0f, -0.5f, -2.8f));

        root.addOrReplaceChild("footR",
                CubeListBuilder.create().texOffs(16, 30).addBox(-1.5f, -0.5f, -2f, 3f, 1f, 4f),
                PartPose.offset(-3.0f, -0.5f, -2.8f));

        // 2. Kafa Grubu
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(40, 0).addBox(-5f, -7f, -4f, 10f, 7f, 8f),
                PartPose.offset(0f, -8.0f, 0f));

        head.addOrReplaceChild("cheekL",
                CubeListBuilder.create().texOffs(22, 20).addBox(-1.5f, -2.0f, -1.5f, 3f, 4f, 3f),
                PartPose.offset(4.4f, -1.3f, -3.3f));

        head.addOrReplaceChild("cheekR",
                CubeListBuilder.create().texOffs(38, 20).addBox(-1.5f, -2.0f, -1.5f, 3f, 4f, 3f),
                PartPose.offset(-4.4f, -1.3f, -3.3f));

        head.addOrReplaceChild("muzzle",
                CubeListBuilder.create().texOffs(80, 0).addBox(-3.0f, -2.5f, -4.8f, 6f, 3f, 2f),
                PartPose.ZERO);

        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(54, 26).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offset(0f, -2.0f, -4.35f));

        head.addOrReplaceChild("earL",
                CubeListBuilder.create().texOffs(36, 30).addBox(-2f, -4f, -0.5f, 4f, 4f, 1f),
                PartPose.offsetAndRotation(3.6f, -6.2f, -0.5f, 0f, 0f, 0.28f));

        head.addOrReplaceChild("earR",
                CubeListBuilder.create().texOffs(50, 30).addBox(-2f, -4f, -0.5f, 4f, 4f, 1f),
                PartPose.offsetAndRotation(-3.6f, -6.2f, -0.5f, 0f, 0f, -0.28f));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(PetRenderState s) {
        resetPose();
        float t = s.age;
        float sleep = s.sleepProgress;
        float awake = 1f - sleep * 0.7f;
        float eat = s.eatAmount;
        float fly = s.flyAmount;
        float shake = s.dryShake;
        float swim = s.swimAmount;
        float crouch = s.crouchAmount;
        float hurt = Math.min(1f, s.hurtTime / 10f);
        float swing = s.attackSwing > 0f ? (float) Math.sin(s.attackSwing * Math.PI) : 0f;

        // Omuzda sabit dururken cesitlendirilmis idle (yuz temizleme & etrafi koklama)
        float calmFactor = Math.max(0f, 1f - Math.min(1f, s.walkSpeed * 3f)) * (1f - eat) * (1f - fly) * (1f - sleep) * (1f - swim);
        float idleSlot = t % 240f;
        float groomAmt = calmFactor * burst(idleSlot, 48f);
        float sniffAmt = calmFactor * (idleSlot > 110f ? burst(idleSlot - 110f, 44f) : 0f);

        float walkSpeed = Math.min(s.walkSpeed, 1.5f);
        float walkAmp = s.groundRoam ? 1.55f : 1.0f;
        float walkCycle = s.walkPos * 1.3324f;
        float walkBounce = Math.abs(sin(s.walkPos * 0.6662f)) * walkSpeed * (1f - swim * 0.8f);
        float stepJiggle = sin(walkCycle) * walkSpeed * (1f - swim * 0.8f);

        // Nefes alma (uyurken daha yavas ve derin)
        float breathRate = 0.11f - 0.04f * sleep;
        float breath = sin(t * breathRate);
        float idleScaleY = (0.025f + 0.015f * sleep) * breath;
        float idleScaleXZ = -(0.015f + 0.010f * sleep) * breath;

        float vertRootY = 0f;
        float vertStretchY = 0f;
        float vertStretchXZ = 0f;
        float earWind = 0f;
        float headTiltX = 0f;
        float pawFlail = 0f;

        if (!s.onGround) {
            if (s.yVel > 0.05f) {
                float jump = Math.min(1.0f, s.yVel * 2.0f);
                vertRootY = jump * 0.15f;
                vertStretchY = jump * 0.08f;
                vertStretchXZ = -jump * 0.04f;
                earWind = -jump * 0.16f;
            } else if (s.yVel < -0.05f) {
                float fall = Math.min(1.2f, -s.yVel);
                vertRootY = -fall * 0.65f;
                vertStretchY = fall * 0.10f;
                vertStretchXZ = -fall * 0.05f;
                earWind = (fall * 0.35f) + sin(t * 0.8f) * 0.10f * fall;
                headTiltX = fall * 0.14f;
                pawFlail = fall * 0.6f;
            }
        }

        float lsq = s.landSquash;
        float landRootY = lsq * 0.25f;
        float landBodyY = -lsq * 0.14f;
        float landBodyXZ = lsq * 0.10f;
        float landHeadScaleY = -lsq * 0.08f;
        float landHeadScaleXZ = lsq * 0.06f;
        float landEarSpread = lsq * 0.22f;

        // Root yuruyus sekmesi, yatay yuzme egimi & inis tepkisi alir
        float swimBob = swim * sin(t * 0.22f) * 0.12f;
        root.y = -(walkBounce * (s.groundRoam ? 0.72f : 0.45f)) + vertRootY + landRootY + crouch * 0.35f + swimBob - swim * 3.2f;
        root.z = sin(s.walkPos * 0.6662f) * 0.20f * walkSpeed * (1f - swim);
        root.xRot = swim * 1.15f;

        // Uyurken ve egilirken puf gibi hafif yayilma
        float sleepSquashY = -0.10f * sleep - 0.08f * crouch;
        float sleepSquashXZ = 0.08f * sleep + 0.06f * crouch;

        // Sudan cikinca silkinme & ucus aerodinamigi
        float shakeWave = shake * sin(t * 2.8f);
        float totalSquashY = (stepJiggle * 0.08f) + vertStretchY + landBodyY + sleepSquashY - hurt * 0.12f - shake * 0.06f;
        float totalSquashXZ = (-stepJiggle * 0.05f) + vertStretchXZ + landBodyXZ + sleepSquashXZ + hurt * 0.10f + shake * 0.08f;

        body.yScale = Math.max(0.80f, Math.min(1.18f, 1f + idleScaleY + totalSquashY));
        body.xScale = Math.max(0.85f, Math.min(1.18f, 1f + idleScaleXZ + totalSquashXZ));
        body.zScale = Math.max(0.85f, Math.min(1.18f, 1f + idleScaleXZ + totalSquashXZ));
        body.zRot = sin(s.walkPos * 0.6662f) * (s.groundRoam ? 0.14f : 0.06f) * walkSpeed * (1f - swim) + shakeWave * 0.34f;
        body.xRot = swing * 0.16f - hurt * 0.18f + fly * 0.42f;

        // Ayaklar: yerde belirgin pitipiti adim, suda hizli kurbaga/kopek yuzusu
        float footWalk = sin(walkCycle) * 0.56f * walkSpeed * walkAmp * (1f - swim);
        float paddleL = swim * sin(t * 0.75f) * 0.65f;
        float paddleR = swim * sin(t * 0.75f + (float) Math.PI) * 0.65f;
        footL.xRot = footWalk + paddleL;
        footR.xRot = -footWalk + paddleR;
        footL.y = -0.5f - Math.max(0f, -sin(walkCycle)) * 0.85f * walkSpeed * (1f - swim) - swim * 0.35f;
        footR.y = -0.5f - Math.max(0f, sin(walkCycle)) * 0.85f * walkSpeed * (1f - swim) - swim * 0.35f;

        // Kafa govde tepesine kilitli
        head.y = -8.0f * body.yScale + sleep * 0.65f;
        head.x = -8.0f * sin(body.zRot) * 0.5f;
        head.yScale = Math.max(0.88f, Math.min(1.12f, 1f + (totalSquashY * 0.35f) + landHeadScaleY));
        head.xScale = Math.max(0.90f, Math.min(1.10f, 1f + (totalSquashXZ * 0.35f) + landHeadScaleXZ));
        head.zScale = Math.max(0.90f, Math.min(1.10f, 1f + (totalSquashXZ * 0.35f) + landHeadScaleXZ));

        // Omuza gore oyuncuya merakli bakis + uyuklama + yemek kemirme + koklama/silkinme/yuzme
        float shoulderBias = s.groundRoam ? 0f : ((s.leftShoulder ? -0.18f : 0.18f) * awake * (1f - fly));
        float lookYaw = sin(t * 0.037f) * 0.16f * awake * (1f - fly) + shoulderBias;
        float nibbleHead = eat * (0.14f + sin(t * 1.4f) * 0.10f);
        float sniffTilt = sniffAmt * (-0.18f + sin(t * 1.8f) * 0.05f);
        float groomHead = groomAmt * (0.10f + sin(t * 0.9f) * 0.05f);
        head.yRot = lookYaw + sin(s.walkPos * 0.6662f) * 0.08f * walkSpeed - shakeWave * 0.42f;
        head.zRot = sin(t * 0.037f + 1.2f) * 0.05f * awake + (body.zRot * 0.6f) + sleep * (s.leftShoulder ? 0.10f : -0.10f) + shakeWave * 0.48f;
        head.xRot = sin(t * 0.05f + 2f) * 0.03f + (stepJiggle * 0.07f) + headTiltX + sleep * 0.28f + nibbleHead + sniffTilt + groomHead - swing * 0.14f - fly * 0.22f - swim * 0.95f;

        float earWobble = sin(walkCycle - 0.9f) * 0.22f * walkSpeed;
        float earSleep = sleep * 0.34f;
        float earAlert = (eat * 0.10f + swing * 0.16f + sniffAmt * 0.14f);
        earL.zRot = 0.28f + earWobble + earWind + landEarSpread + earSleep - earAlert + shakeWave * 0.35f + fly * 0.28f;
        earR.zRot = -0.28f - earWobble - earWind - landEarSpread - earSleep + earAlert + shakeWave * 0.35f - fly * 0.28f;
        earL.xRot = earWobble * 0.14f + sleep * 0.18f - fly * 0.65f;
        earR.xRot = earWobble * 0.14f + sleep * 0.18f - fly * 0.65f;

        float phase = (t + 40f) % 150f;
        boolean leftTurn = ((int) ((t + 40f) / 150f) & 1) == 0;
        float flick = burst(phase, 10f) * 0.32f * awake;
        earL.zRot += leftTurn ? flick : 0f;
        earR.zRot -= leftTurn ? 0f : flick;

        float twitch = burst(t % 96f, 16f) * awake;
        float pawStep = sin(walkCycle + 0.4f) * (s.groundRoam ? 0.38f : 0.14f) * walkSpeed * (1f - swim);
        float pawFlailL = pawFlail > 0f ? sin(t * 1.5f) * 0.25f * pawFlail : 0f;
        float pawFlailR = pawFlail > 0f ? sin(t * 1.5f + (float) Math.PI) * 0.25f * pawFlail : 0f;

        // Yemek yerken patiler agzina kalkar; idle'da yanak temizleme (grooming); suda yuzme (paddle)
        float eatPawLift = eat * 1.45f;
        float eatPawNibbleL = eat * sin(t * 1.6f) * 0.22f;
        float eatPawNibbleR = eat * sin(t * 1.6f + 0.8f) * 0.22f;
        float groomLift = groomAmt * 1.35f;
        float groomRubL = groomAmt * sin(t * 1.35f) * 0.32f;
        float groomRubR = groomAmt * sin(t * 1.35f + 1.4f) * 0.32f;

        pawL.xRot = -0.10f - pawStep + paddleL * 0.85f + sin(t * 1.6f) * 0.12f * twitch + pawFlailL - eatPawLift * 0.65f + eatPawNibbleL - groomLift * 0.72f + groomRubL - swing * 0.35f + fly * 0.45f;
        pawR.xRot = -0.10f + pawStep + paddleR * 0.85f + sin(t * 1.6f + (float) Math.PI) * 0.12f * twitch + pawFlailR - eatPawLift * 0.65f + eatPawNibbleR - groomLift * 0.72f + groomRubR - swing * 0.35f + fly * 0.45f;
        pawL.zRot = eat * 0.28f + groomAmt * 0.35f;
        pawR.zRot = -eat * 0.28f - groomAmt * 0.35f;
        pawL.x = 1.6f - eat * 0.45f + groomAmt * 0.35f;
        pawR.x = -1.6f + eat * 0.45f - groomAmt * 0.35f;
        pawL.z = -4.6f - eat * 0.45f - groomAmt * 0.25f;
        pawR.z = -4.6f - eat * 0.45f - groomAmt * 0.25f;
        pawL.y = -4.3f + (stepJiggle * 0.18f) + (lsq * 0.15f) - eatPawLift - groomLift + groomRubL * 0.4f;
        pawR.y = -4.3f + (stepJiggle * 0.18f) + (lsq * 0.15f) - eatPawLift - groomLift + groomRubR * 0.4f;

        seed.visible = eat > 0.05f;
        if (seed.visible) {
            float sc = Math.min(1f, eat * 1.25f) * 0.72f;
            seed.xScale = sc;
            seed.yScale = sc;
            seed.zScale = sc;
            seed.y = -5.8f + sin(t * 1.6f) * 0.18f;
            seed.xRot = 0.25f + sin(t * 1.6f) * 0.12f;
            seed.zRot = sin(t * 0.9f) * 0.14f;
        }

        // Yanak keseleri: yemek yerken daha dolgun ve kipir kipir!
        float cheekEat = eat * (0.14f + sin(t * 1.4f) * 0.06f);
        float cheekPuff = 0.02f * breath + sin(t * 2.1f) * 0.04f * twitch + cheekEat;
        cheekL.xScale = 1.0f + cheekPuff;
        cheekL.yScale = 1.0f + cheekPuff * 0.8f;
        cheekR.xScale = 1.0f + cheekPuff;
        cheekR.yScale = 1.0f + cheekPuff * 0.8f;

        nose.y = -2.0f + sin(t * 2.1f) * 0.06f * twitch + eat * sin(t * 1.6f) * 0.08f + sniffAmt * sin(t * 2.8f) * 0.12f;
        nose.xRot = sin(t * 2.4f) * 0.08f * twitch + sniffAmt * sin(t * 2.8f) * 0.18f;

        tail.yRot = sin(s.walkPos * 1.33f) * 0.3f * walkSpeed + sin(t * 0.3f) * 0.3f * twitch + shakeWave * 0.45f;
        tail.xRot = stepJiggle * 0.15f - sleep * 0.15f - fly * 0.35f;
    }
}
