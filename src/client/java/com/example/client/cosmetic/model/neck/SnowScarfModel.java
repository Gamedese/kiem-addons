package com.example.client.cosmetic.model.neck;

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
 * Cozy snow-white & ice-blue knit winter scarf attached to body,
 * covering the chin with head-tracking collarUpper + animated front-left tail.
 * Texture: assets/modid/textures/cosmetic/neck_snow_scarf.png (256x256)
 */
public class SnowScarfModel extends CosmeticModel {
    private final ModelPart collarUpper;
    private final ModelPart collarFront;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart fringe;

    public SnowScarfModel(ModelPart root) {
        super(root);
        this.collarUpper = root.getChild("collarUpper");
        this.collarFront = root.getChild("collarFront");
        this.tail1 = root.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
        this.fringe = tail3.getChild("fringe");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Upper snow-knit cowl covering chin and mouth (head-tracked)
        root.addOrReplaceChild("collarUpper",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.75f, -2.4f, -4.75f, 9.5f, 2.6f, 9.5f),
                PartPose.ZERO);

        // Thickened knit collar wrapping around the neck
        root.addOrReplaceChild("collarMid",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-4.35f, -0.5f, -3.55f, 8.7f, 1.9f, 6.2f),
                PartPose.ZERO);

        // Layered front drape across the collarbone
        root.addOrReplaceChild("collarFront",
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(-4.15f, 0.2f, -3.75f, 8.3f, 1.6f, 1.4f),
                PartPose.ZERO);

        // Cozy knit knot on front-left chest
        root.addOrReplaceChild("knot",
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(-1.1f, -1.0f, -0.6f, 2.2f, 2.0f, 1.2f),
                PartPose.offset(2.0f, 1.3f, -3.35f));

        // Segmented trailing scarf tail hanging down front-left chest
        PartDefinition t1 = root.addOrReplaceChild("tail1",
                CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(-1.0f, 0.0f, -0.5f, 2.0f, 3.0f, 1.0f, new CubeDeformation(0.14f)),
                PartPose.offset(2.0f, 2.1f, -3.2f));

        PartDefinition t2 = t1.addOrReplaceChild("tail2",
                CubeListBuilder.create()
                        .texOffs(10, 44)
                        .addBox(-1.0f, 0.0f, -0.5f, 2.0f, 3.0f, 1.0f, new CubeDeformation(0.10f)),
                PartPose.offset(0.0f, 2.9f, 0.0f));

        PartDefinition t3 = t2.addOrReplaceChild("tail3",
                CubeListBuilder.create()
                        .texOffs(20, 44)
                        .addBox(-1.0f, 0.0f, -0.5f, 2.0f, 2.2f, 1.0f, new CubeDeformation(0.08f)),
                PartPose.offset(0.0f, 2.9f, 0.0f));

        t3.addOrReplaceChild("fringe",
                CubeListBuilder.create()
                        .texOffs(30, 44)
                        .addBox(-1.0f, 0.0f, -0.5f, 2.0f, 1.2f, 1.0f, new CubeDeformation(-0.04f)),
                PartPose.offset(0.0f, 2.1f, 0.0f));

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();

        float t = s.ageInTicks;
        float speed = Math.min(s.walkSpeed, 1.35f);
        float breath = sin(t * 0.08f);
        float walkWave = sin(s.walkPos * 0.6662f);

        collarUpper.yRot = s.headYRot * 0.85f;
        collarUpper.xRot = s.headXRot * 0.45f;
        collarFront.y = breath * 0.04f;

        float idleSway = sin(t * 0.06f) * 0.035f;
        tail1.xRot = -0.05f - speed * 0.20f + walkWave * 0.14f * speed + idleSway - s.crouchAmount * 0.35f;
        tail1.zRot = -0.04f + sin(s.walkPos * 0.6662f + 0.4f) * 0.08f * speed;

        tail2.xRot = 0.04f + sin(s.walkPos * 0.6662f - 0.7f) * 0.18f * speed + sin(t * 0.06f - 0.6f) * 0.04f;
        tail2.zRot = sin(s.walkPos * 0.6662f - 0.3f) * 0.08f * speed;

        tail3.xRot = 0.05f + sin(s.walkPos * 0.6662f - 1.4f) * 0.22f * speed + sin(t * 0.06f - 1.2f) * 0.05f;
        tail3.zRot = sin(s.walkPos * 0.6662f - 0.9f) * 0.07f * speed;

        fringe.xRot = 0.04f + sin(s.walkPos * 0.6662f - 2.0f) * 0.20f * speed;

        if (s.flyAmount > 0.01f) {
            tail1.xRot -= s.flyAmount * 0.25f;
            tail2.xRot += s.flyAmount * 0.12f;
        }
    }
}
