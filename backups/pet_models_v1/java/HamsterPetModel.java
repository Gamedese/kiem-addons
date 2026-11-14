package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Chibi hamster: tombul govde, kocaman kafa, pembe kulak/pati. UV duzeni: tools/gen_pet_textures.py (hamster()).
 */
public class HamsterPetModel extends PetModel {
    private final ModelPart body, head, earL, earR, nose, pawL, pawR, tail;

    public HamsterPetModel(ModelPart root) {
        super(root);
        body = root.getChild("body");
        head = root.getChild("head");
        earL = head.getChild("earL");
        earR = head.getChild("earR");
        nose = head.getChild("nose");
        pawL = root.getChild("pawL");
        pawR = root.getChild("pawR");
        tail = root.getChild("tail");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -4f, -1.5f, 6f, 4f, 4f),
                PartPose.ZERO);

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 8).addBox(-3.5f, -4f, -2f, 7f, 4f, 4f),
                PartPose.offset(0f, -3.5f, 0.5f));
        head.addOrReplaceChild("earL",
                CubeListBuilder.create().texOffs(0, 16).addBox(-1f, -2f, -0.5f, 2f, 2f, 1f),
                PartPose.offsetAndRotation(2.3f, -3.6f, 0f, 0f, 0f, 0.2f));
        head.addOrReplaceChild("earR",
                CubeListBuilder.create().texOffs(6, 16).addBox(-1f, -2f, -0.5f, 2f, 2f, 1f),
                PartPose.offsetAndRotation(-2.3f, -3.6f, 0f, 0f, 0f, -0.2f));
        head.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(12, 16).addBox(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f),
                PartPose.offset(0f, -1.6f, -2f));

        // On patiler (govdenin onunde, yukari kalkik)
        root.addOrReplaceChild("pawL",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(1.8f, -2.2f, -1.6f));
        root.addOrReplaceChild("pawR",
                CubeListBuilder.create().texOffs(32, 0).addBox(-1f, -1f, -1f, 2f, 2f, 2f),
                PartPose.offset(-1.8f, -2.2f, -1.6f));

        // Arka ayaklar sabit (animasyon yok)
        root.addOrReplaceChild("footL",
                CubeListBuilder.create().texOffs(24, 4).addBox(-1f, -1f, -1.5f, 2f, 1f, 3f),
                PartPose.offset(1.8f, 0f, -0.6f));
        root.addOrReplaceChild("footR",
                CubeListBuilder.create().texOffs(34, 4).addBox(-1f, -1f, -1.5f, 2f, 1f, 3f),
                PartPose.offset(-1.8f, 0f, -0.6f));

        root.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(18, 16).addBox(-0.5f, -0.5f, 0f, 1f, 1f, 1f),
                PartPose.offset(0f, -1.2f, 2.4f));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(PetRenderState s) {
        resetPose();
        float t = s.age;

        // --- 1. Adim & Yurume Fizigi (Bounce, Squash & Stretch) ---
        // Yuruyus/kosu ritmi kesintisiz ve akici devam eder
        float walkSpeed = Math.min(s.walkSpeed, 1.25f);
        // Iki adim = 1 tam periyot (adım frekansı)
        float walkCycle = s.walkPos * 1.3324f;
        // Ziplayarak yaylanma yuksekligi (yukari = -y)
        float walkBounce = Math.abs(sin(s.walkPos * 0.6662f)) * walkSpeed;
        
        // Jiggle (yaylanma dalgasi): adim yere basinca ezilme (-), tepeye cikinca uzama (+)
        float stepJiggle = sin(walkCycle) * walkSpeed;
        
        // --- 2. Nefes (Dururken yumusak marshmallow nabzi) ---
        float breath = sin(t * 0.11f);
        float idleScaleY = 0.05f * breath;
        float idleScaleXZ = -0.025f * breath;

        // --- 3. Dikey Hareket & Yercekimi / Havada Ucus Fizigi ---
        float vertRootY = 0f;
        float vertStretchY = 0f;
        float vertStretchXZ = 0f;
        float earWind = 0f;
        float headTiltX = 0f;
        float pawFlail = 0f;

        if (!s.onGround) {
            if (s.yVel > 0.05f) {
                // Yukari ziplama / havalanma: eylemsizlik baskisi ve dikey uzama
                float jump = Math.min(1.0f, s.yVel * 2.2f);
                vertRootY = jump * 0.20f; // oyuncu kalkarken pet bir an omuza baski yapar
                vertStretchY = jump * 0.16f;
                vertStretchXZ = -jump * 0.08f;
                earWind = -jump * 0.22f; // kulaklar arkaya / asagi hafif yatar
            } else if (s.yVel < -0.05f) {
                // Havada serbest dusus: omuzdan gozle gorulur sekilde havalanma (-y yukari, patiler omuzdan kesilir)
                float fall = Math.min(1.4f, -s.yVel);
                // 2.4f carpani: omuz ile pet arasinda ~2-3 piksellik net bir hava boslugu birakir
                vertRootY = -fall * 2.4f;
                vertStretchY = fall * 0.22f;
                vertStretchXZ = -fall * 0.11f;
                // Karşıdan gelen rüzgar kulakları yukarı savurur ve dalgalandırır
                earWind = (fall * 0.60f) + sin(t * 0.8f) * 0.14f * fall;
                headTiltX = fall * 0.28f; // aşağıya, düşeceği yere bakar
                pawFlail = fall;
            }
        }

        // Yere inis yaylanmasi (squash & damped harmonic bounce)
        // Art arda ziplansa bile yarida kesilmez, dogal salinimini tamamlar ve sonraki ziplamaya guc katar
        float lsq = s.landSquash;
        float landRootY = lsq * 0.45f;
        float landBodyY = -lsq * 0.35f;
        float landBodyXZ = lsq * 0.25f;
        float landHeadY = lsq * 0.30f;
        float landHeadScaleY = -lsq * 0.20f;
        float landHeadScaleXZ = lsq * 0.15f;
        float landEarSpread = lsq * 0.38f;

        // --- 4. Kok Pozisyonu: Yaylanarak ziplama, yalpalama ve dikey inis/kalkis ---
        root.y = -0.30f * (breath * 0.5f + 0.5f) - (walkBounce * 0.95f) + vertRootY + landRootY;
        root.z = sin(s.walkPos * 0.6662f) * 0.20f * walkSpeed;

        // --- 5. Govde Squash & Stretch (Jelibon Ezilme / Uzama) ---
        float totalSquashY = (stepJiggle * 0.24f) + vertStretchY + landBodyY;
        float totalSquashXZ = (-stepJiggle * 0.15f) + vertStretchXZ + landBodyXZ;

        body.yScale = Math.max(0.55f, Math.min(1.45f, 1f + idleScaleY + totalSquashY));
        body.xScale = Math.max(0.65f, Math.min(1.35f, 1f + idleScaleXZ + totalSquashXZ));
        body.zScale = Math.max(0.65f, Math.min(1.35f, 1f + idleScaleXZ + totalSquashXZ));

        // Govde adimla ritmik saga-sola yalpalar (tatli paytak waddle)
        body.zRot = sin(s.walkPos * 0.6662f) * 0.14f * walkSpeed;

        // --- 6. Kafa: Govdeden gecikmeli yaylanma, bas sallama ve inis/dusus ---
        head.y = -3.5f - 0.12f * breath + (stepJiggle * 0.16f) + landHeadY;
        head.yScale = Math.max(0.65f, Math.min(1.35f, 1f + (totalSquashY * 0.6f) + landHeadScaleY));
        head.xScale = Math.max(0.70f, Math.min(1.30f, 1f + (totalSquashXZ * 0.6f) + landHeadScaleXZ));
        head.zScale = Math.max(0.70f, Math.min(1.30f, 1f + (totalSquashXZ * 0.6f) + landHeadScaleXZ));

        // Kafa rotasyonu: etrafa bakınma + adımla ritmik onaylama + dusus/inis egimi
        float lookYaw = sin(t * 0.037f) * 0.25f;
        head.yRot = lookYaw + sin(s.walkPos * 0.6662f) * 0.12f * walkSpeed;
        head.zRot = sin(t * 0.037f + 1.2f) * 0.07f - (body.zRot * 0.5f);
        head.xRot = sin(t * 0.05f + 2f) * 0.04f + (stepJiggle * 0.16f) + headTiltX;

        // --- 7. Kulaklar: Eylemsizlik, ruzgar ve inis ezilmesi ---
        float earWobble = sin(walkCycle - 0.9f) * 0.35f * walkSpeed;
        earL.zRot = 0.2f + earWobble + earWind + landEarSpread;
        earR.zRot = -0.2f - earWobble - earWind - landEarSpread;
        earL.xRot = earWobble * 0.22f;
        earR.xRot = earWobble * 0.22f;

        // Kulak kirpmasi (arada tek kulak segirmesi)
        float phase = (t + 40f) % 150f;
        boolean leftTurn = ((int) ((t + 40f) / 150f) & 1) == 0;
        float flick = burst(phase, 10f) * 0.45f;
        earL.zRot += leftTurn ? flick : 0f;
        earR.zRot -= leftTurn ? 0f : flick;

        // --- 8. Patiler & Burun: Heyecan, pat pat adimlar ve havada cirpinma ---
        float twitch = burst(t % 96f, 16f);
        float pawStep = sin(walkCycle + 0.4f) * 0.35f * walkSpeed;
        float pawFlailL = pawFlail > 0f ? sin(t * 1.5f) * 0.35f * pawFlail : 0f;
        float pawFlailR = pawFlail > 0f ? sin(t * 1.5f + (float) Math.PI) * 0.35f * pawFlail : 0f;

        pawL.xRot = -0.35f + pawStep + sin(t * 1.6f) * 0.28f * twitch + pawFlailL;
        pawR.xRot = -0.35f + pawStep + sin(t * 1.6f + (float) Math.PI) * 0.28f * twitch + pawFlailR;
        pawL.y = -2.2f + (stepJiggle * 0.22f) + (lsq * 0.20f);
        pawR.y = -2.2f + (stepJiggle * 0.22f) + (lsq * 0.20f);

        nose.y = -1.6f + sin(t * 2.1f) * 0.12f * twitch - (totalSquashY * 0.20f);
        nose.xRot = sin(t * 2.4f) * 0.15f * twitch;

        // --- 9. Kuyruk: Adimla neseli pitirti ---
        tail.yRot = sin(s.walkPos * 1.33f) * 0.4f * walkSpeed + sin(t * 0.3f) * 0.4f * twitch;
        tail.xRot = stepJiggle * 0.22f;
    }
}
