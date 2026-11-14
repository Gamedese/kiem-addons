package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * HD Pofuduk Hamster: Blockbench sahnesiyle 1:1 senkronize, 128x128 doku ve 15 kup detayli model.
 */
public class HamsterHdPetModel extends PetModel {
    private final ModelPart body, head, cheekL, cheekR, earL, earR, nose, pawL, pawR, tail;

    public HamsterHdPetModel(ModelPart root) {
        super(root);
        body = root.getChild("body");
        head = root.getChild("head");
        cheekL = head.getChild("cheekL");
        cheekR = head.getChild("cheekR");
        earL = head.getChild("earL");
        earR = head.getChild("earR");
        nose = head.getChild("nose");
        pawL = body.getChild("pawL");
        pawR = body.getChild("pawR");
        tail = body.getChild("tail");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. Tombul Govde Grubu (y = -1..-8, z-fighting olmadan kafanin altina tam oturur)
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5f, -8f, -4f, 10f, 7f, 8f),
                PartPose.ZERO);

        // Arka Pofuduk Basamak (Yan ve Arka-Capraz referansindaki 1px sirt cikintisi)
        body.addOrReplaceChild("back_puff",
                CubeListBuilder.create().texOffs(0, 38).addBox(-4f, -7f, 4f, 8f, 6f, 1f),
                PartPose.ZERO);

        // On Pofuduk Beyaz Gobek (w=8, h=5, d=1)
        body.addOrReplaceChild("belly",
                CubeListBuilder.create().texOffs(0, 20).addBox(-4f, -6.5f, -4.6f, 8f, 5f, 1f),
                PartPose.ZERO);

        // Minik Pembe Kuyruk (w=2, h=2, d=2)
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(64, 30).addBox(-1f, -1f, 0f, 2f, 2f, 2f),
                PartPose.offset(0f, -2.5f, 5.0f));

        // On Patiler (w=2, h=2, d=2)
        body.addOrReplaceChild("pawL",
                CubeListBuilder.create().texOffs(64, 20).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(1.6f, -4.3f, -4.6f));

        body.addOrReplaceChild("pawR",
                CubeListBuilder.create().texOffs(74, 20).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(-1.6f, -4.3f, -4.6f));

        // Arka Ayaklar (govdenin altindan 1px gorunen pembe tabanlar, w=3, h=1, d=4)
        body.addOrReplaceChild("footL",
                CubeListBuilder.create().texOffs(0, 30).addBox(-1.5f, -0.5f, -2f, 3f, 1f, 4f),
                PartPose.offset(3.0f, -0.5f, -2.8f));

        body.addOrReplaceChild("footR",
                CubeListBuilder.create().texOffs(16, 30).addBox(-1.5f, -0.5f, -2f, 3f, 1f, 4f),
                PartPose.offset(-3.0f, -0.5f, -2.8f));

        // 2. Kafa Grubu (w=10, h=7, d=8, pivot: 0, -8f, 0f)
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(40, 0).addBox(-5f, -7f, -4f, 10f, 7f, 8f),
                PartPose.offset(0f, -8.0f, 0f));

        // Sol ve Sag Pofuduk Yanaklar (w=3, h=4, d=3, kare mikro-piksel allikli)
        head.addOrReplaceChild("cheekL",
                CubeListBuilder.create().texOffs(22, 20).addBox(-1.5f, -2.0f, -1.5f, 3f, 4f, 3f),
                PartPose.offset(4.4f, -1.3f, -3.3f));

        head.addOrReplaceChild("cheekR",
                CubeListBuilder.create().texOffs(38, 20).addBox(-1.5f, -2.0f, -1.5f, 3f, 4f, 3f),
                PartPose.offset(-4.4f, -1.3f, -3.3f));

        // 3B Agiz Koprusu (Muzzle: w=6, h=3, d=2, yanaklarla ayni z=-4.8f duzleminde)
        head.addOrReplaceChild("muzzle",
                CubeListBuilder.create().texOffs(80, 0).addBox(-3.0f, -2.5f, -4.8f, 6f, 3f, 2f),
                PartPose.ZERO);

        // Minik Pembe Dugme Burun (w=1, h=1, d=1, agizlik ust siniriyla tam hizali)
        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(54, 26).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offset(0f, -2.0f, -4.35f));

        // Kulaklar (w=4, h=4, d=1, turuncu cerceveli ve disa dogru \ / acili)
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

        float walkSpeed = Math.min(s.walkSpeed, 1.25f);
        float walkCycle = s.walkPos * 1.3324f;
        float walkBounce = Math.abs(sin(s.walkPos * 0.6662f)) * walkSpeed;
        float stepJiggle = sin(walkCycle) * walkSpeed;

        float breath = sin(t * 0.11f);
        float idleScaleY = 0.025f * breath;
        float idleScaleXZ = -0.015f * breath;

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

        // Ayaklar omuzda sabit kalir, sadece yururken/ziplarken tatlıca esner
        root.y = -(walkBounce * 0.45f) + vertRootY + landRootY;
        root.z = sin(s.walkPos * 0.6662f) * 0.20f * walkSpeed;

        float totalSquashY = (stepJiggle * 0.08f) + vertStretchY + landBodyY;
        float totalSquashXZ = (-stepJiggle * 0.05f) + vertStretchXZ + landBodyXZ;

        body.yScale = Math.max(0.82f, Math.min(1.18f, 1f + idleScaleY + totalSquashY));
        body.xScale = Math.max(0.85f, Math.min(1.15f, 1f + idleScaleXZ + totalSquashXZ));
        body.zScale = Math.max(0.85f, Math.min(1.15f, 1f + idleScaleXZ + totalSquashXZ));
        body.zRot = sin(s.walkPos * 0.6662f) * 0.06f * walkSpeed;

        // Kafa her zaman govdenin ust sinirina (-8.0f * body.yScale) kilitli kalir, asla kopmaz!
        head.y = -8.0f * body.yScale;
        head.x = -8.0f * sin(body.zRot) * 0.5f;
        head.yScale = Math.max(0.88f, Math.min(1.12f, 1f + (totalSquashY * 0.35f) + landHeadScaleY));
        head.xScale = Math.max(0.90f, Math.min(1.10f, 1f + (totalSquashXZ * 0.35f) + landHeadScaleXZ));
        head.zScale = Math.max(0.90f, Math.min(1.10f, 1f + (totalSquashXZ * 0.35f) + landHeadScaleXZ));

        float lookYaw = sin(t * 0.037f) * 0.16f;
        head.yRot = lookYaw + sin(s.walkPos * 0.6662f) * 0.08f * walkSpeed;
        head.zRot = sin(t * 0.037f + 1.2f) * 0.04f + (body.zRot * 0.6f);
        head.xRot = sin(t * 0.05f + 2f) * 0.03f + (stepJiggle * 0.07f) + headTiltX;

        float earWobble = sin(walkCycle - 0.9f) * 0.22f * walkSpeed;
        earL.zRot = 0.28f + earWobble + earWind + landEarSpread;
        earR.zRot = -0.28f - earWobble - earWind - landEarSpread;
        earL.xRot = earWobble * 0.14f;
        earR.xRot = earWobble * 0.14f;

        float phase = (t + 40f) % 150f;
        boolean leftTurn = ((int) ((t + 40f) / 150f) & 1) == 0;
        float flick = burst(phase, 10f) * 0.32f;
        earL.zRot += leftTurn ? flick : 0f;
        earR.zRot -= leftTurn ? 0f : flick;

        float twitch = burst(t % 96f, 16f);
        float pawStep = sin(walkCycle + 0.4f) * 0.14f * walkSpeed;
        float pawFlailL = pawFlail > 0f ? sin(t * 1.5f) * 0.25f * pawFlail : 0f;
        float pawFlailR = pawFlail > 0f ? sin(t * 1.5f + (float) Math.PI) * 0.25f * pawFlail : 0f;

        pawL.xRot = -0.10f + pawStep + sin(t * 1.6f) * 0.12f * twitch + pawFlailL;
        pawR.xRot = -0.10f + pawStep + sin(t * 1.6f + (float) Math.PI) * 0.12f * twitch + pawFlailR;
        pawL.y = -4.3f + (stepJiggle * 0.18f) + (lsq * 0.15f);
        pawR.y = -4.3f + (stepJiggle * 0.18f) + (lsq * 0.15f);

        float cheekPuff = 0.02f * breath + sin(t * 2.1f) * 0.04f * twitch;
        cheekL.xScale = 1.0f + cheekPuff;
        cheekL.yScale = 1.0f + cheekPuff * 0.8f;
        cheekR.xScale = 1.0f + cheekPuff;
        cheekR.yScale = 1.0f + cheekPuff * 0.8f;

        nose.y = -2.0f + sin(t * 2.1f) * 0.06f * twitch;
        nose.xRot = sin(t * 2.4f) * 0.08f * twitch;

        tail.yRot = sin(s.walkPos * 1.33f) * 0.3f * walkSpeed + sin(t * 0.3f) * 0.3f * twitch;
        tail.xRot = stepJiggle * 0.15f;
    }
}
