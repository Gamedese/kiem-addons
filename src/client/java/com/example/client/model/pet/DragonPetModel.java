package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Argon Chibi Ender Dragon Shoulder Pet (dragon_hd.bbmodel v5 ile 1:1 senkronize):
 * - 512x512 (4x mikro-piksel, 128x128 UV grid) doku.
 * - Omuza alçak ve sıkı oturan kafa, yan yana ayrılmış ön patiler, Steve'in koluna/gövdesine sıfır batma (Z >= 2.05).
 * - Zincirleme (chained) 6 eklemli kuyruk iskeleti (tail_1 -> tail_2 -> tail_3 -> tail_4 -> tail_5 -> fluke).
 */
public class DragonPetModel extends PetModel {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart frontPaws;
    private final ModelPart rearLegs;
    private final ModelPart wings;
    private final ModelPart wingL;
    private final ModelPart wingR;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart tail5;
    private final ModelPart fluke;
    /** Kuyruk zinciri kökten uca (tail_1 .. fluke), harmonik dalga döngüsü için. */
    private final ModelPart[] tail;

    public DragonPetModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.frontPaws = this.body.getChild("front_paws");
        this.rearLegs = this.body.getChild("rear_legs");
        this.wings = this.body.getChild("wings");
        this.wingL = this.wings.getChild("wing_L");
        this.wingR = this.wings.getChild("wing_R");
        this.tail1 = this.body.getChild("tail_1");
        this.tail2 = this.tail1.getChild("tail_2");
        this.tail3 = this.tail2.getChild("tail_3");
        this.tail4 = this.tail3.getChild("tail_4");
        this.tail5 = this.tail4.getChild("tail_5");
        this.fluke = this.tail5.getChild("fluke");
        this.tail = new ModelPart[]{tail1, tail2, tail3, tail4, tail5, fluke};
    }

    private static float rad(float deg) {
        return deg * ((float) Math.PI / 180f);
    }

    private static void addBbCube(PartDefinition parent, String name,
                                  float pOx, float pOy, float pOz,
                                  float fx, float fy, float fz,
                                  float tx, float ty, float tz,
                                  float ox, float oy, float oz,
                                  float rx, float ry, float rz,
                                  int u, int v, float inflate) {
        CubeListBuilder builder = CubeListBuilder.create().texOffs(u, v);
        if (Math.abs(inflate) > 1e-4f) {
            builder.addBox(fx - ox, oy - ty, fz - oz, tx - fx, ty - fy, tz - fz, new CubeDeformation(inflate));
        } else {
            builder.addBox(fx - ox, oy - ty, fz - oz, tx - fx, ty - fy, tz - fz);
        }
        PartPose pose = (Math.abs(rx) > 1e-4f || Math.abs(ry) > 1e-4f || Math.abs(rz) > 1e-4f)
                ? PartPose.offsetAndRotation(ox - pOx, pOy - oy, oz - pOz, rad(-rx), rad(-ry), rad(-rz))
                : PartPose.offset(ox - pOx, pOy - oy, oz - pOz);
        parent.addOrReplaceChild(name, builder, pose);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Root anchor at shoulder perch [6.0, 24.0, 0.0] in Blockbench
        final float bOx = 6.0f, bOy = 24.0f, bOz = 0.0f;
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        // 1. Torso, Neck, Segmented Belly & Dorsal Spines (strictly Z >= 2.05 below Y=24)
        addBbCube(body, "torso_main",   bOx, bOy, bOz, 4.0f, 20.6f,  2.05f, 8.0f, 25.6f, 5.05f, 6.0f, 23.1f, 3.55f,  8f, -6f, 4f, 14, 20,  0.0f);
        addBbCube(body, "belly_plate",  bOx, bOy, bOz, 5.0f, 20.8f,  1.75f, 7.0f, 25.8f, 2.75f, 6.0f, 23.1f, 3.55f,  8f, -6f, 4f, 30, 20, -0.15f);
        addBbCube(body, "neck",         bOx, bOy, bOz, 4.5f, 23.8f,  0.20f, 7.5f, 26.8f, 3.20f, 6.0f, 25.3f, 1.70f, 10f, -4f, 3f,  0, 20, -0.10f);
        addBbCube(body, "neck_belly",   bOx, bOy, bOz, 5.0f, 24.0f, -0.35f, 7.0f, 27.0f, 0.65f, 6.0f, 25.3f, 1.70f, 10f, -4f, 3f, 38, 20, -0.18f);

        addBbCube(body, "spine_back_1", bOx, bOy, bOz, 5.5f, 24.8f,  4.4f, 6.5f, 25.8f, 5.4f, 6.0f, 25.3f, 4.9f, 12f, -6f, 4f, 60, 38, -0.18f);
        addBbCube(body, "spine_back_2", bOx, bOy, bOz, 5.5f, 23.3f,  4.6f, 6.5f, 24.3f, 5.6f, 6.0f, 23.8f, 5.1f, 10f, -6f, 4f, 60, 38, -0.18f);
        addBbCube(body, "spine_back_3", bOx, bOy, bOz, 5.5f, 21.8f,  4.7f, 6.5f, 22.8f, 5.7f, 6.0f, 22.3f, 5.2f,  8f, -6f, 4f, 60, 38, -0.18f);
        addBbCube(body, "spine_back_4", bOx, bOy, bOz, 5.5f, 20.3f,  4.8f, 6.5f, 21.3f, 5.8f, 6.0f, 20.8f, 5.3f,  6f, -6f, 4f, 60, 38, -0.18f);

        // 2. Sculpted Head Group (lowered snugly above shoulder & shifted +0.75 X to clear Steve's head)
        final float hOx = 6.85f, hOy = 25.9f, hOz = -0.8f;
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(),
                PartPose.offsetAndRotation(hOx - bOx, bOy - hOy, hOz - bOz, rad(-6f), rad(-6f), rad(5f)));

        addBbCube(head, "head_main",    hOx, hOy, hOz, 3.85f, 25.7f, -3.6f,  9.85f, 30.7f,  1.4f, hOx,   hOy,   hOz,   0f,   0f,   0f,  0,  0,  0.0f);
        addBbCube(head, "head_top",     hOx, hOy, hOz, 4.35f, 30.3f, -3.1f,  9.35f, 31.3f,  0.9f, hOx,   hOy,   hOz,   0f,   0f,   0f, 24,  0,  0.0f);
        addBbCube(head, "head_back",    hOx, hOy, hOz, 4.35f, 26.2f,  0.8f,  9.35f, 30.2f,  1.8f, hOx,   hOy,   hOz,   0f,   0f,   0f, 84,  0, -0.10f);
        addBbCube(head, "brow_R",       hOx, hOy, hOz, 3.75f, 29.5f, -3.8f,  5.75f, 30.5f, -2.8f, 4.75f, 30.0f, -3.3f, 0f,   0f,   5f, 76,  0, -0.22f);
        addBbCube(head, "brow_L",       hOx, hOy, hOz, 7.95f, 29.5f, -3.8f,  9.95f, 30.5f, -2.8f, 8.95f, 30.0f, -3.3f, 0f,   0f,  -5f, 76,  0, -0.22f);
        addBbCube(head, "snout_upper",  hOx, hOy, hOz, 5.35f, 25.5f, -4.4f,  8.35f, 27.5f, -3.4f, hOx,   hOy,   hOz,   2f,   0f,   0f, 44,  0, -0.12f);

        addBbCube(head, "hornL_base",   hOx, hOy, hOz, 8.05f, 29.7f, -0.9f, 10.05f, 31.7f,  1.1f, 9.05f, 30.3f,  0.1f, -20f, 14f, -20f,  0, 12, -0.08f);
        addBbCube(head, "hornL_mid",    hOx, hOy, hOz, 8.75f, 30.8f, -0.4f, 10.75f, 32.8f,  1.6f, 9.75f, 31.4f,  0.6f, -16f, 10f, -12f, 10, 12, -0.24f);
        addBbCube(head, "hornL_tip",    hOx, hOy, hOz, 9.35f, 31.8f,  0.0f, 10.35f, 33.8f,  1.0f, 9.85f, 32.4f,  0.5f, -10f,  6f,  -4f, 20, 12, -0.12f);

        addBbCube(head, "hornR_base",   hOx, hOy, hOz, 3.65f, 29.7f, -0.9f,  5.65f, 31.7f,  1.1f, 4.65f, 30.3f,  0.1f, -20f, -14f, 20f,  0, 12, -0.08f);
        addBbCube(head, "hornR_mid",    hOx, hOy, hOz, 2.95f, 30.8f, -0.4f,  4.95f, 32.8f,  1.6f, 3.95f, 31.4f,  0.6f, -16f, -10f, 12f, 10, 12, -0.24f);
        addBbCube(head, "hornR_tip",    hOx, hOy, hOz, 3.35f, 31.8f,  0.0f,  4.35f, 33.8f,  1.0f, 3.85f, 32.4f,  0.5f, -10f,  -6f,  4f, 20, 12, -0.12f);

        addBbCube(head, "crown_nub_L",  hOx, hOy, hOz, 7.45f, 30.9f, -1.7f,  8.45f, 31.9f, -0.7f, 7.95f, 31.4f, -1.2f, -12f,  0f,  -6f, 26, 12, -0.15f);
        addBbCube(head, "crown_nub_R",  hOx, hOy, hOz, 5.25f, 30.9f, -1.7f,  6.25f, 31.9f, -0.7f, 5.75f, 31.4f, -1.2f, -12f,  0f,   6f, 26, 12, -0.15f);

        addBbCube(head, "crest_1",      hOx, hOy, hOz, 6.35f, 30.5f, -2.8f,  7.35f, 31.5f, -1.8f, 6.85f, 31.0f, -2.3f, -10f,  0f,   0f, 26, 12, -0.20f);
        addBbCube(head, "crest_2",      hOx, hOy, hOz, 6.35f, 30.8f, -1.4f,  7.35f, 31.8f, -0.4f, 6.85f, 31.3f, -0.9f, -18f,  0f,   0f, 26, 12, -0.16f);
        addBbCube(head, "crest_3",      hOx, hOy, hOz, 6.35f, 30.2f,  0.1f,  7.35f, 32.2f,  1.1f, 6.85f, 31.2f,  0.6f, -28f,  0f,   0f, 32, 12, -0.15f);
        addBbCube(head, "crest_4",      hOx, hOy, hOz, 6.35f, 28.8f,  1.1f,  7.35f, 30.8f,  2.1f, 6.85f, 29.8f,  1.6f, -42f,  0f,   0f, 32, 12, -0.15f);
        addBbCube(head, "crest_5",      hOx, hOy, hOz, 6.35f, 27.0f,  1.4f,  7.35f, 29.0f,  2.4f, 6.85f, 28.0f,  1.9f, -58f,  0f,   0f, 32, 12, -0.18f);

        addBbCube(head, "cheek_fin_L1", hOx, hOy, hOz, 9.05f, 27.7f, -1.8f, 11.05f, 28.7f, -0.8f, 9.55f, 28.2f, -1.3f,   0f, -18f, 14f, 40, 12, -0.18f);
        addBbCube(head, "cheek_fin_L2", hOx, hOy, hOz, 9.25f, 26.6f, -2.0f, 10.25f, 27.6f, -1.0f, 9.55f, 27.1f, -1.5f,   0f, -14f,  6f, 48, 12, -0.18f);
        addBbCube(head, "cheek_fin_R1", hOx, hOy, hOz, 2.65f, 27.7f, -1.8f,  4.65f, 28.7f, -0.8f, 4.15f, 28.2f, -1.3f,   0f,  18f,-14f, 54, 12, -0.18f);
        addBbCube(head, "cheek_fin_R2", hOx, hOy, hOz, 3.45f, 26.6f, -2.0f,  4.45f, 27.6f, -1.0f, 4.15f, 27.1f, -1.5f,   0f,  14f, -6f, 48, 12, -0.18f);

        // 3. Front Paws (cleanly separated left & right on top of shoulder)
        final float pOx = 6.0f, pOy = 24.5f, pOz = 0.0f;
        PartDefinition paws = body.addOrReplaceChild("front_paws", CubeListBuilder.create(),
                PartPose.offset(pOx - bOx, bOy - pOy, pOz - bOz));

        addBbCube(paws, "arm_L",   pOx, pOy, pOz, 6.3f, 24.0f, -0.2f, 8.3f, 26.0f,  1.8f, 7.3f, 25.0f,  0.8f, -18f,  0f, -6f,  0, 30, -0.25f);
        addBbCube(paws, "paw_L",   pOx, pOy, pOz, 6.2f, 23.9f, -1.6f, 8.2f, 24.9f,  0.4f, 7.2f, 24.4f, -0.6f,   0f, -4f,  0f, 10, 30, -0.12f);
        addBbCube(paws, "claw_L1", pOx, pOy, pOz, 6.2f, 23.7f, -2.1f, 7.2f, 24.7f, -1.1f, 6.7f, 24.2f, -1.6f,   8f, -4f,  0f, 20, 30, -0.32f);
        addBbCube(paws, "claw_L2", pOx, pOy, pOz, 6.7f, 23.7f, -2.2f, 7.7f, 24.7f, -1.2f, 7.2f, 24.2f, -1.7f,   8f, -4f,  0f, 20, 30, -0.32f);
        addBbCube(paws, "claw_L3", pOx, pOy, pOz, 7.2f, 23.7f, -2.1f, 8.2f, 24.7f, -1.1f, 7.7f, 24.2f, -1.6f,   8f, -4f,  0f, 20, 30, -0.32f);

        addBbCube(paws, "arm_R",   pOx, pOy, pOz, 3.9f, 24.0f, -0.2f, 5.9f, 26.0f,  1.8f, 4.9f, 25.0f,  0.8f, -18f,  0f,  6f,  0, 30, -0.25f);
        addBbCube(paws, "paw_R",   pOx, pOy, pOz, 4.0f, 23.9f, -1.6f, 6.0f, 24.9f,  0.4f, 5.0f, 24.4f, -0.6f,   0f,  4f,  0f, 10, 30, -0.12f);
        addBbCube(paws, "claw_R1", pOx, pOy, pOz, 4.0f, 23.7f, -2.1f, 5.0f, 24.7f, -1.1f, 4.5f, 24.2f, -1.6f,   8f,  4f,  0f, 20, 30, -0.32f);
        addBbCube(paws, "claw_R2", pOx, pOy, pOz, 4.5f, 23.7f, -2.2f, 5.5f, 24.7f, -1.2f, 5.0f, 24.2f, -1.7f,   8f,  4f,  0f, 20, 30, -0.32f);
        addBbCube(paws, "claw_R3", pOx, pOy, pOz, 5.0f, 23.7f, -2.1f, 6.0f, 24.7f, -1.1f, 5.5f, 24.2f, -1.6f,   8f,  4f,  0f, 20, 30, -0.32f);

        // 4. Rear Legs (strictly behind Z >= 2.05)
        final float rOx = 6.0f, rOy = 20.5f, rOz = 3.2f;
        PartDefinition rear = body.addOrReplaceChild("rear_legs", CubeListBuilder.create(),
                PartPose.offset(rOx - bOx, bOy - rOy, rOz - bOz));

        addBbCube(rear, "thigh_L",  rOx, rOy, rOz, 6.4f, 19.0f, 2.20f, 8.4f, 22.0f, 5.20f, 7.4f, 20.5f, 3.70f, 6f,  6f,  8f, 26, 30, -0.12f);
        addBbCube(rear, "foot_L",   rOx, rOy, rOz, 6.3f, 18.2f, 2.05f, 8.3f, 19.2f, 4.05f, 7.3f, 18.7f, 3.05f, 4f,  4f,  4f, 38, 30, -0.08f);
        addBbCube(rear, "rclaw_L1", rOx, rOy, rOz, 6.4f, 17.9f, 1.85f, 7.4f, 18.9f, 2.85f, 6.9f, 18.4f, 2.35f, 6f,  4f,  4f, 20, 30, -0.30f);
        addBbCube(rear, "rclaw_L2", rOx, rOy, rOz, 7.1f, 17.9f, 1.85f, 8.1f, 18.9f, 2.85f, 7.6f, 18.4f, 2.35f, 6f,  4f,  4f, 20, 30, -0.30f);

        addBbCube(rear, "thigh_R",  rOx, rOy, rOz, 3.6f, 19.0f, 2.20f, 5.6f, 22.0f, 5.20f, 4.6f, 20.5f, 3.70f, 6f, -6f, -8f, 26, 30, -0.12f);
        addBbCube(rear, "foot_R",   rOx, rOy, rOz, 3.7f, 18.2f, 2.05f, 5.7f, 19.2f, 4.05f, 4.7f, 18.7f, 3.05f, 4f, -4f, -4f, 38, 30, -0.08f);
        addBbCube(rear, "rclaw_R1", rOx, rOy, rOz, 3.9f, 17.9f, 1.85f, 4.9f, 18.9f, 2.85f, 4.4f, 18.4f, 2.35f, 6f, -4f, -4f, 20, 30, -0.30f);
        addBbCube(rear, "rclaw_R2", rOx, rOy, rOz, 4.6f, 17.9f, 1.85f, 5.6f, 18.9f, 2.85f, 5.1f, 18.4f, 2.35f, 6f, -4f, -4f, 20, 30, -0.30f);

        // 5. Enlarged Bat Wings
        final float wOx = 6.0f, wOy = 24.4f, wOz = 4.3f;
        PartDefinition wings = body.addOrReplaceChild("wings", CubeListBuilder.create(),
                PartPose.offset(wOx - bOx, bOy - wOy, wOz - bOz));

        final float wlOx = 6.8f, wlOy = 24.6f, wlOz = 4.3f;
        PartDefinition wingL = wings.addOrReplaceChild("wing_L", CubeListBuilder.create(),
                PartPose.offsetAndRotation(wlOx - wOx, wOy - wlOy, wlOz - wOz, rad(-8f), rad(18f), rad(-8f)));

        addBbCube(wingL, "wingL_arm_in",  wlOx, wlOy, wlOz,  6.6f, 24.4f, 3.8f,  9.6f, 25.4f, 4.8f,  6.8f, 24.7f, 4.3f, 0f, 0f,  16f,  0, 48, -0.12f);
        addBbCube(wingL, "wingL_arm_out", wlOx, wlOy, wlOz,  9.2f, 25.0f, 3.8f, 13.2f, 26.0f, 4.8f,  9.4f, 25.5f, 4.3f, 0f, 0f, -14f, 10, 48, -0.14f);
        addBbCube(wingL, "wingL_thumb",   wlOx, wlOy, wlOz,  9.0f, 25.8f, 3.8f, 10.0f, 26.8f, 4.8f,  9.4f, 25.5f, 4.3f, 0f, 0f,  12f, 60, 38, -0.22f);
        addBbCube(wingL, "wingL_rib1",    wlOx, wlOy, wlOz, 12.0f, 19.2f, 3.8f, 13.0f, 25.2f, 4.8f, 12.5f, 25.0f, 4.3f, 0f, 0f,  22f, 22, 48, -0.20f);
        addBbCube(wingL, "wingL_rib2",    wlOx, wlOy, wlOz,  9.7f, 19.6f, 3.8f, 10.7f, 25.6f, 4.8f, 10.2f, 25.4f, 4.3f, 0f, 0f,  10f, 22, 48, -0.20f);
        addBbCube(wingL, "wingL_rib3",    wlOx, wlOy, wlOz,  7.6f, 20.2f, 3.8f,  8.6f, 25.2f, 4.8f,  8.1f, 25.0f, 4.3f, 0f, 0f,  -2f, 28, 48, -0.20f);
        addBbCube(wingL, "wingL_mem1",    wlOx, wlOy, wlOz,  6.6f, 20.4f, 3.8f,  9.6f, 25.4f, 4.8f,  8.1f, 25.0f, 4.3f, 0f, 0f,   2f, 34, 48, -0.38f);
        addBbCube(wingL, "wingL_mem2",    wlOx, wlOy, wlOz,  8.2f, 20.2f, 3.8f, 11.2f, 25.2f, 4.8f, 10.2f, 25.4f, 4.3f, 0f, 0f,  10f, 44, 48, -0.38f);
        addBbCube(wingL, "wingL_mem3",    wlOx, wlOy, wlOz,  9.9f, 20.6f, 3.8f, 12.9f, 24.6f, 4.8f, 12.5f, 25.0f, 4.3f, 0f, 0f,  18f, 54, 48, -0.38f);

        final float wrOx = 5.2f, wrOy = 24.6f, wrOz = 4.3f;
        PartDefinition wingR = wings.addOrReplaceChild("wing_R", CubeListBuilder.create(),
                PartPose.offsetAndRotation(wrOx - wOx, wOy - wrOy, wrOz - wOz, rad(-10f), rad(-22f), rad(10f)));

        addBbCube(wingR, "wingR_arm_in",  wrOx, wrOy, wrOz,  2.4f, 24.4f, 3.8f,  5.4f, 25.4f, 4.8f,  5.2f, 24.7f, 4.3f, 0f, 0f, -16f,  0, 48, -0.12f);
        addBbCube(wingR, "wingR_arm_out", wrOx, wrOy, wrOz, -1.2f, 25.0f, 3.8f,  2.8f, 26.0f, 4.8f,  2.6f, 25.5f, 4.3f, 0f, 0f,  14f, 10, 48, -0.14f);
        addBbCube(wingR, "wingR_thumb",   wrOx, wrOy, wrOz,  2.0f, 25.8f, 3.8f,  3.0f, 26.8f, 4.8f,  2.6f, 25.5f, 4.3f, 0f, 0f, -12f, 60, 38, -0.22f);
        addBbCube(wingR, "wingR_rib1",    wrOx, wrOy, wrOz, -1.0f, 19.2f, 3.8f,  0.0f, 25.2f, 4.8f, -0.5f, 25.0f, 4.3f, 0f, 0f, -22f, 22, 48, -0.20f);
        addBbCube(wingR, "wingR_rib2",    wrOx, wrOy, wrOz,  1.3f, 19.6f, 3.8f,  2.3f, 25.6f, 4.8f,  1.8f, 25.4f, 4.3f, 0f, 0f, -10f, 22, 48, -0.20f);
        addBbCube(wingR, "wingR_rib3",    wrOx, wrOy, wrOz,  3.4f, 20.2f, 3.8f,  4.4f, 25.2f, 4.8f,  3.9f, 25.0f, 4.3f, 0f, 0f,   2f, 28, 48, -0.20f);
        addBbCube(wingR, "wingR_mem1",    wrOx, wrOy, wrOz,  2.4f, 20.4f, 3.8f,  5.4f, 25.4f, 4.8f,  3.9f, 25.0f, 4.3f, 0f, 0f,  -2f, 34, 48, -0.38f);
        addBbCube(wingR, "wingR_mem2",    wrOx, wrOy, wrOz,  0.8f, 20.2f, 3.8f,  3.8f, 25.2f, 4.8f,  1.8f, 25.4f, 4.3f, 0f, 0f, -10f, 44, 48, -0.38f);
        addBbCube(wingR, "wingR_mem3",    wrOx, wrOy, wrOz, -0.9f, 20.6f, 3.8f,  2.1f, 24.6f, 4.8f, -0.5f, 25.0f, 4.3f, 0f, 0f, -18f, 54, 48, -0.38f);

        // 6. Chained Tail Bone Hierarchy (tail_1 -> tail_2 -> tail_3 -> tail_4 -> tail_5 -> fluke)
        final float t1Ox = 6.0f, t1Oy = 20.8f, t1Oz = 3.55f;
        PartDefinition tail1 = body.addOrReplaceChild("tail_1", CubeListBuilder.create(),
                PartPose.offsetAndRotation(t1Ox - bOx, bOy - t1Oy, t1Oz - bOz, rad(2f), 0f, rad(3f)));
        addBbCube(tail1, "tail_1_box", t1Ox, t1Oy, t1Oz, 4.5f, 18.0f, 2.05f, 7.5f, 21.0f, 5.05f, t1Ox, t1Oy, t1Oz, 0f, 0f, 0f,  0, 38, -0.25f);
        addBbCube(tail1, "tspine_1",   t1Ox, t1Oy, t1Oz, 5.5f, 19.0f, 4.35f, 6.5f, 20.0f, 5.35f, 6.0f, 19.5f, 4.85f, 0f, 0f, 0f, 60, 38, -0.20f);

        final float t2Ox = 6.0f, t2Oy = 18.4f, t2Oz = 3.55f;
        PartDefinition tail2 = tail1.addOrReplaceChild("tail_2", CubeListBuilder.create(),
                PartPose.offsetAndRotation(t2Ox - t1Ox, t1Oy - t2Oy, t2Oz - t1Oz, 0f, 0f, rad(4f)));
        addBbCube(tail2, "tail_2_box", t2Ox, t2Oy, t2Oz, 5.0f, 15.6f, 2.55f, 7.0f, 18.6f, 4.55f, t2Ox, t2Oy, t2Oz, 0f, 0f, 0f, 14, 38, -0.05f);
        addBbCube(tail2, "tspine_2",   t2Ox, t2Oy, t2Oz, 5.5f, 16.6f, 4.10f, 6.5f, 17.6f, 5.10f, 6.0f, 17.1f, 4.60f, 0f, 0f, 0f, 60, 38, -0.22f);

        final float t3Ox = 6.0f, t3Oy = 15.9f, t3Oz = 3.55f;
        PartDefinition tail3 = tail2.addOrReplaceChild("tail_3", CubeListBuilder.create(),
                PartPose.offsetAndRotation(t3Ox - t2Ox, t2Oy - t3Oy, t3Oz - t2Oz, 0f, 0f, rad(5f)));
        addBbCube(tail3, "tail_3_box", t3Ox, t3Oy, t3Oz, 5.0f, 13.1f, 2.55f, 7.0f, 16.1f, 4.55f, t3Ox, t3Oy, t3Oz, 0f, 0f, 0f, 24, 38, -0.15f);
        addBbCube(tail3, "tspine_3",   t3Ox, t3Oy, t3Oz, 5.5f, 14.1f, 4.00f, 6.5f, 15.1f, 5.00f, 6.0f, 14.6f, 4.50f, 0f, 0f, 0f, 60, 38, -0.24f);

        final float t4Ox = 6.0f, t4Oy = 13.5f, t4Oz = 3.55f;
        PartDefinition tail4 = tail3.addOrReplaceChild("tail_4", CubeListBuilder.create(),
                PartPose.offsetAndRotation(t4Ox - t3Ox, t3Oy - t4Oy, t4Oz - t3Oz, 0f, 0f, rad(6f)));
        addBbCube(tail4, "tail_4_box", t4Ox, t4Oy, t4Oz, 5.0f, 11.5f, 2.55f, 7.0f, 13.5f, 4.55f, t4Ox, t4Oy, t4Oz, 0f, 0f, 0f, 34, 38, -0.25f);
        addBbCube(tail4, "tspine_4",   t4Ox, t4Oy, t4Oz, 5.5f, 12.0f, 3.90f, 6.5f, 13.0f, 4.90f, 6.0f, 12.5f, 4.40f, 0f, 0f, 0f, 60, 38, -0.26f);

        final float t5Ox = 6.0f, t5Oy = 11.8f, t5Oz = 3.55f;
        PartDefinition tail5 = tail4.addOrReplaceChild("tail_5", CubeListBuilder.create(),
                PartPose.offsetAndRotation(t5Ox - t4Ox, t4Oy - t5Oy, t5Oz - t4Oz, 0f, 0f, rad(7f)));
        addBbCube(tail5, "tail_5_box", t5Ox, t5Oy, t5Oz, 5.5f,  8.8f, 3.05f, 6.5f, 11.8f, 4.05f, t5Ox, t5Oy, t5Oz, 0f, 0f, 0f, 44, 38,  0.05f);
        addBbCube(tail5, "tspine_5",   t5Ox, t5Oy, t5Oz, 5.5f,  9.8f, 3.70f, 6.5f, 10.8f, 4.70f, 6.0f, 10.3f, 4.20f, 0f, 0f, 0f, 60, 38, -0.28f);

        final float flOx = 6.0f, flOy = 9.0f, flOz = 3.55f;
        PartDefinition fluke = tail5.addOrReplaceChild("fluke", CubeListBuilder.create(),
                PartPose.offsetAndRotation(flOx - t5Ox, t5Oy - flOy, flOz - t5Oz, 0f, 0f, rad(5f)));
        addBbCube(fluke, "fluke_core", flOx, flOy, flOz, 5.0f, 7.3f, 2.55f, 7.0f, 9.3f, 4.55f, 6.0f, 8.3f, 3.55f,   0f, 0f,   0f, 50, 38, -0.35f);
        addBbCube(fluke, "fluke_s1",   flOx, flOy, flOz, 5.5f, 6.5f, 3.05f, 6.5f, 7.5f, 4.05f, 6.0f, 7.0f, 3.55f,   0f, 0f,   0f, 60, 38, -0.22f);
        addBbCube(fluke, "fluke_s2",   flOx, flOy, flOz, 4.6f, 7.7f, 3.05f, 5.6f, 8.7f, 4.05f, 5.1f, 8.2f, 3.55f,   0f, 0f, -25f, 60, 38, -0.22f);
        addBbCube(fluke, "fluke_s3",   flOx, flOy, flOz, 6.4f, 7.7f, 3.05f, 7.4f, 8.7f, 4.05f, 6.9f, 8.2f, 3.55f,   0f, 0f,  25f, 60, 38, -0.22f);
        addBbCube(fluke, "fluke_s4",   flOx, flOy, flOz, 5.5f, 7.7f, 3.95f, 6.5f, 8.7f, 4.95f, 6.0f, 8.2f, 4.45f, -25f, 0f,   0f, 60, 38, -0.22f);

        return LayerDefinition.create(mesh, 128, 128);
    }

    // =====================================================================================
    // PROSEDUREL OMUZ ANIMASYONU (Sadece ve Sadece Omuz Tunegi)
    // =====================================================================================

    private static final float PI = (float) Math.PI;

    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static float clamp01(float v) {
        return clamp(v, 0f, 1f);
    }

    private static float lerp(float a, float b, float k) {
        return a + (b - a) * k;
    }

    private static float smooth(float k) {
        k = clamp01(k);
        return k * k * (3f - 2f * k);
    }

    private static float hurtEnvelope(PetRenderState s) {
        if (s.hurtTime <= 0f) return 0f;
        float frac = s.age - (float) Math.floor(s.age);
        return smooth((s.hurtTime - frac) / 10f);
    }

    @Override
    public void setupAnim(PetRenderState s) {
        resetPose();

        final float t = s.age;
        final float side = s.leftShoulder ? 1f : -1f;

        // Sag omuzda bas pozunu aynala (kafa pivotu X simetrik kalsin)
        if (!s.leftShoulder) {
            head.x = -head.x;
            head.yRot = -head.yRot;
            head.zRot = -head.zRot;
        }

        // ---- Durum agirliklari ----
        final float sleep = clamp01(s.sleepProgress);
        final float awake = 1f - sleep;
        final float crouch = clamp01(s.crouchAmount) * awake;
        final float eat = clamp01(s.eatAmount) * awake;
        final float hurt = hurtEnvelope(s);
        final float swing = s.attackSwing > 0.001f ? sin(clamp01(s.attackSwing) * PI) * awake * (1f - hurt) : 0f;
        final float fly = clamp01(s.flyAmount) * awake;
        final float lsq = clamp01(s.landSquash) * awake;
        final float shake = clamp01(s.dryShake) * awake;
        final float fallSpread = (!s.onGround && s.yVel < -0.10f && fly < 0.1f)
                ? clamp01((-s.yVel - 0.10f) * 1.1f) * 0.25f * awake : 0f;
        final float ws = clamp01(s.walkSpeed) * awake;
        final float stride = s.walkPos * 0.6662f;

        // Merak kipi: sakin omuzda dururken
        final float calm = awake * (1f - hurt) * (1f - eat) * (1f - 0.6f * crouch);

        float yawOut = 0f;
        float rollOut = 0f;
        float raiseOuter = 0f;
        float raiseInner = 0f;
        float sweepOuter = 0f;
        float sweepInner = 0f;

        // 1. Dogal Nefes & Omuzda Huzurlu Dinlenme
        final float bA = sin(t * 0.065f);
        final float bS = sin(t * 0.035f);
        final float breath = lerp(bA * 0.016f, bS * 0.035f, sleep);
        body.yScale = 1f + breath;
        body.xScale = 1f + breath * 0.4f;
        body.zScale = 1f + breath * 0.4f;
        body.y -= lerp((bA * 0.5f + 0.5f) * 0.12f, (bS * 0.5f + 0.5f) * 0.06f, sleep);
        raiseOuter += breath * 1.2f;
        raiseInner += breath * 1.2f;



        // 2. Yurume / Kosma Adim Eylemsizligi
        final float step = Math.abs(sin(stride));
        final float sway = sin(stride) * ws;
        body.y -= step * ws * 0.35f;
        body.zRot += sway * 0.028f;
        body.xRot += ws * 0.040f;
        head.zRot -= sway * 0.020f;
        head.xRot -= ws * 0.030f;

        // 3. Sevimli Kafa Hareketleri & Tavsan Kafa Yatirma (Roll Tilt)
        // Durgunken 4-5 saniye sakin bakar, sonra tatlica basini yana kirar (merak bakisi)
        float tiltCycle = (t + 15f) % 180f;
        float targetTilt = 0f;
        if (tiltCycle > 40f && tiltCycle < 90f) {
            float inOut = smooth(sin((tiltCycle - 40f) / 50f * PI));
            targetTilt = inOut * 0.28f;
        } else if (tiltCycle > 120f && tiltCycle < 165f) {
            float inOut = smooth(sin((tiltCycle - 120f) / 45f * PI));
            targetTilt = -inOut * 0.22f;
        }
        rollOut += targetTilt * calm;

        // Sakin merakli etrafa bakinma
        float lookCycle = sin(t * 0.028f);
        yawOut += smooth(lookCycle) * 0.16f * calm;
        head.xRot += sin(t * 0.038f) * 0.035f * calm;

        // Ara sira sahibinin boynuna sirnasma (Nuzzle)
        float nuzzle = burst((t + 70f) % 260f, 30f) * calm;
        rollOut += nuzzle * 0.15f;
        head.xRot -= nuzzle * 0.06f;

        // 4. Sneak (Pusu Modu)
        body.y += crouch * 0.50f;
        body.xRot += crouch * 0.12f;
        head.z -= crouch * 0.60f;
        head.y += crouch * 0.16f;
        head.xRot -= crouch * 0.10f;
        raiseOuter -= crouch * 0.08f;
        raiseInner -= crouch * 0.08f;

        // 5. Yemek Meraki (isEating)
        final float sniff = sin(t * 1.8f);
        head.z -= eat * 0.55f;
        head.y += eat * sniff * 0.05f;
        head.xRot += eat * (0.09f + sniff * 0.02f);
        raiseOuter += eat * 0.06f;
        raiseInner += eat * 0.06f;
        frontPaws.xRot -= eat * (0.5f + 0.5f * sin(t * 0.6f)) * 0.08f;

        // 6. Hasar Alma / Korkma (hurtTime)
        body.y += hurt * 0.45f;
        body.x += sin(t * 2.8f) * 0.08f * hurt;
        head.z += hurt * 0.55f;
        head.y += hurt * 0.45f;
        head.xRot += hurt * 0.16f;
        raiseOuter += hurt * 0.28f;
        sweepOuter += hurt * 0.30f;
        raiseInner -= hurt * 0.10f;

        // Saldiri kabarmasi
        raiseOuter += swing * 0.20f;
        raiseInner += swing * 0.10f;
        head.xRot -= swing * 0.05f;

        // 6b. Ucus (Elytra / Creative) kanat cirpisi, Inis esnemesi & Sudan cikinca silkinme
        if (fly > 0.001f) {
            float flap = sin(t * 0.48f);
            raiseOuter += fly * (0.28f + flap * 0.34f);
            raiseInner += fly * (0.22f + flap * 0.28f);
            sweepOuter += fly * 0.22f;
            sweepInner += fly * 0.15f;
            body.xRot += fly * 0.20f;
            head.xRot -= fly * 0.14f;
        }
        if (fallSpread > 0.001f) {
            raiseOuter += fallSpread;
            raiseInner += fallSpread * 0.7f;
        }
        if (lsq > 0.001f) {
            body.yScale -= lsq * 0.10f;
            body.xScale += lsq * 0.06f;
            body.zScale += lsq * 0.06f;
            body.y += lsq * 0.35f;
            raiseOuter += lsq * 0.18f;
        }
        if (shake > 0.001f) {
            float sh = sin(t * 2.6f) * shake;
            body.zRot += sh * 0.20f;
            rollOut += sin(t * 2.6f + 0.5f) * shake * 0.26f;
            raiseOuter += shake * 0.15f;
        }

        // 7. Derin Uyku (isSleeping)
        body.y += sleep * 0.45f;
        body.xRot += sleep * 0.07f;
        head.xRot += sleep * (0.28f + bS * 0.012f);
        head.y += sleep * 0.18f;
        head.z -= sleep * 0.22f;
        rollOut += sleep * 0.04f;
        raiseOuter -= sleep * 0.05f;
        raiseInner -= sleep * 0.05f;

        // Kanatlari omuzda uygula (kapali, dinlenen omuz kanatlari)
        float raiseL = s.leftShoulder ? raiseOuter : raiseInner;
        float raiseR = s.leftShoulder ? raiseInner : raiseOuter;
        float sweepL = s.leftShoulder ? sweepOuter : sweepInner;
        float sweepR = s.leftShoulder ? sweepInner : sweepOuter;
        wingL.zRot -= raiseL;
        wingR.zRot += raiseR;
        wingL.yRot += sweepL;
        wingR.yRot -= sweepR;

        // 8. 6 Eklemli Organik Kuyruk Dalgasi
        float tailAmp = awake * (1f - 0.6f * crouch) * (1f + 0.4f * eat) * (1f - 0.4f * hurt) + sleep * 0.30f;
        for (int i = 0; i < tail.length; i++) {
            ModelPart seg = tail[i];
            float lag = i * 0.50f;
            float wave = lerp(sin(t * 0.08f - lag), sin(t * 0.035f - lag), sleep);
            seg.zRot += wave * (0.030f + 0.010f * i) * tailAmp;
            seg.zRot += sin(stride - lag) * ws * 0.025f;
            seg.xRot += (0.5f + 0.5f * sin(t * 0.055f - lag * 0.8f)) * 0.020f * tailAmp;
        }
        tail4.xRot += 0.04f;
        tail5.xRot += 0.06f;
        fluke.xRot += 0.10f;
        tail1.xRot += ws * 0.08f;

        // Pelerin takiliysa kuyrugu omuz disina dogru zarifce kavisle
        if (s.hasCape) {
            float capeSide = s.leftShoulder ? 0.08f : -0.08f;
            tail1.zRot += capeSide;
            tail2.zRot += capeSide * 0.7f;
        }

        // Kafa aci sinirlamalari (Steve'in yanagina asla batmaz)
        yawOut = clamp(yawOut, -0.05f, 0.24f);
        rollOut = clamp(rollOut, -0.26f, 0.30f);
        head.yRot -= side * yawOut;
        head.zRot += side * rollOut;
    }
}