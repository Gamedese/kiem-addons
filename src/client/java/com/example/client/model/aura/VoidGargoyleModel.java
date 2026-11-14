package com.example.client.model.aura;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Articulated Obsidian Void Dragon / Gargoyle model (128x128).
 * Built from scratch via Blockbench MCP to match concept art proportions:
 * arched neck, horned skull, deep molten lower jaw, dual-jointed bat wings, and forked S-curve tail.
 */
public final class VoidGargoyleModel extends Model<Float> {
    public final ModelPart body;
    public final ModelPart neck;
    public final ModelPart head;
    public final ModelPart lowerJaw;
    public final ModelPart leftWing;
    public final ModelPart leftWingOuter;
    public final ModelPart rightWing;
    public final ModelPart rightWingOuter;
    public final ModelPart tail1;
    public final ModelPart tail2;
    public final ModelPart tail3;
    public final ModelPart foreLegL;
    public final ModelPart foreLegR;
    public final ModelPart hindLegL;
    public final ModelPart hindLegR;

    private boolean hasOverride = false;
    private float overrideJaw = 0.0f;
    private float overrideWingPhase = 0.0f;
    private float overrideWingAmp = 0.36f;
    private float overrideRecoil = 0.0f;
    private int overrideDirection = 1;
    private float overrideLookYaw = 0.0f;
    private float overrideLookPitch = 0.0f;
    private boolean overrideSprinting = false;
    private boolean overrideGliding = false;

    public VoidGargoyleModel() {
        this(false);
    }

    public VoidGargoyleModel(boolean emissive) {
        super(createLayer().bakeRoot(), emissive ? RenderTypes::eyes : RenderTypes::entityCutout);
        this.body = this.root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.head = this.neck.getChild("head");
        this.lowerJaw = this.head.getChild("lower_jaw");
        this.leftWing = this.body.getChild("wing_l");
        this.leftWingOuter = this.leftWing.getChild("wing_l_outer");
        this.rightWing = this.body.getChild("wing_r");
        this.rightWingOuter = this.rightWing.getChild("wing_r_outer");
        this.tail1 = this.body.getChild("tail_1");
        this.tail2 = this.tail1.getChild("tail_2");
        this.tail3 = this.tail2.getChild("tail_3");
        this.foreLegL = this.body.getChild("fore_leg_l_r1");
        this.foreLegR = this.body.getChild("fore_leg_r_r1");
        this.hindLegL = this.body.getChild("hind_leg_l_r1");
        this.hindLegR = this.body.getChild("hind_leg_r_r1");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.3F, -2.0F, -3.5F, 4.6F, 3.8F, 7.3F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-2.0F, -1.2F, -3.7F, 4.0F, 3.2F, 2.5F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        body.addOrReplaceChild("hind_claw_r_r1",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-0.7F, -0.5F, -0.9F, 1.4F, 0.9F, 1.8F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(3.3F, -0.5F, -0.9F, 1.4F, 0.9F, 1.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 4.0F, 1.4F, -0.1396F, 0.0F, 0.0F));

        body.addOrReplaceChild("hind_leg_r_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.6F, -0.2F, -0.8F, 1.2F, 2.8F, 1.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 1.0F, 2.3F, 0.384F, 0.0F, -0.1047F));

        body.addOrReplaceChild("hind_leg_l_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.6F, -0.2F, -0.8F, 1.2F, 2.8F, 1.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 1.0F, 2.3F, 0.384F, 0.0F, 0.1047F));

        body.addOrReplaceChild("fore_claw_r_r1",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-0.7F, -0.5F, -1.3F, 1.4F, 0.9F, 1.9F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(3.5F, -0.5F, -1.3F, 1.4F, 0.9F, 1.9F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.1F, 4.0F, -3.0F, -0.1745F, 0.0F, 0.0F));

        body.addOrReplaceChild("fore_leg_r_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.6F, -0.2F, -0.7F, 1.2F, 2.8F, 1.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.1F, 1.2F, -2.5F, -0.3491F, 0.0F, -0.1396F));

        body.addOrReplaceChild("fore_leg_l_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.6F, -0.2F, -0.7F, 1.2F, 2.8F, 1.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.1F, 1.2F, -2.5F, -0.3491F, 0.0F, 0.1396F));

        body.addOrReplaceChild("spine_3_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.35F, -1.2F, -0.6F, 0.7F, 1.2F, 1.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.9F, 2.3F, 0.3142F, 0.0F, 0.0F));

        body.addOrReplaceChild("spine_2_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.4F, -1.5F, -0.7F, 0.8F, 1.5F, 1.3F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.3F, 0.3142F, 0.0F, 0.0F));

        body.addOrReplaceChild("spine_1_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.4F, -1.3F, -0.7F, 0.8F, 1.3F, 1.3F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, -1.8F, 0.3142F, 0.0F, 0.0F));

        PartDefinition neck = body.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.4F, -1.4F, -3.6F, 2.8F, 2.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.2F, -3.2F, -0.4189F, 0.0F, 0.0F));

        neck.addOrReplaceChild("neck_spine_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.35F, -1.2F, -0.7F, 0.7F, 1.2F, 1.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.3F, -2.1F, 0.3142F, 0.0F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-2.7F, -2.3F, -3.2F, 5.4F, 3.7F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-2.1F, -0.6F, -6.0F, 4.2F, 1.7F, 3.0F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(1.3F, 1.0F, -5.7F, 0.65F, 1.1F, 0.7F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(-1.95F, 1.0F, -5.7F, 0.65F, 1.1F, 0.7F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(-0.95F, 1.0F, -5.8F, 1.9F, 0.7F, 0.4F, new CubeDeformation(0.0F))
                        .texOffs(64, 56).addBox(-1.6F, 0.9F, -5.2F, 3.2F, 0.3F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.0F, -3.4F, 0.2443F, 0.0F, 0.0F));

        head.addOrReplaceChild("eye_r_r1",
                CubeListBuilder.create().texOffs(112, 32).addBox(-0.75F, -0.45F, -0.35F, 1.5F, 0.9F, 0.75F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.8F, -1.0F, -3.0F, 0.0F, 0.0F, 0.2443F));

        head.addOrReplaceChild("eye_l_r1",
                CubeListBuilder.create().texOffs(112, 32).addBox(-0.75F, -0.45F, -0.35F, 1.5F, 0.9F, 0.75F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.8F, -1.0F, -3.0F, 0.0F, 0.0F, -0.2443F));

        head.addOrReplaceChild("brow_r_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.15F, -0.4F, -0.9F, 2.25F, 0.7F, 1.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.6F, -1.6F, -2.8F, 0.0F, 0.0F, 0.3142F));

        head.addOrReplaceChild("brow_l_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.1F, -0.4F, -0.9F, 2.25F, 0.7F, 1.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.6F, -1.6F, -2.8F, 0.0F, 0.0F, -0.3142F));

        head.addOrReplaceChild("nose_ridge_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.1F, -0.5F, -2.8F, 2.2F, 0.7F, 2.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -0.6F, -3.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition hornL = head.addOrReplaceChild("horn_l",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.55F, -2.2F, -0.6F, 1.1F, 2.4F, 1.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.8F, -2.1F, -0.6F, 0.5236F, 0.0F, 0.3142F));

        hornL.addOrReplaceChild("horn_l_tip_r1",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.4F, -2.3F, -0.4F, 0.8F, 2.4F, 0.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.1F, 0.3491F, 0.0F, 0.1396F));

        PartDefinition hornR = head.addOrReplaceChild("horn_r",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.55F, -2.2F, -0.6F, 1.1F, 2.4F, 1.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.8F, -2.1F, -0.6F, 0.5236F, 0.0F, -0.3142F));

        hornR.addOrReplaceChild("horn_r_tip_r1",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.4F, -2.3F, -0.4F, 0.8F, 2.4F, 0.8F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.1F, 0.3491F, 0.0F, -0.1396F));

        head.addOrReplaceChild("lower_jaw",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-1.95F, 0.4F, -4.4F, 3.9F, 0.8F, 4.6F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(1.45F, -0.2F, -4.4F, 0.6F, 0.9F, 4.6F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-2.05F, -0.2F, -4.4F, 0.6F, 0.9F, 4.6F, new CubeDeformation(0.0F))
                        .texOffs(48, 0).addBox(-1.65F, -0.1F, -4.5F, 3.3F, 0.8F, 0.6F, new CubeDeformation(0.0F))
                        .texOffs(64, 56).addBox(-1.45F, 0.15F, -3.9F, 2.9F, 0.5F, 4.3F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(1.15F, -1.0F, -4.3F, 0.6F, 1.0F, 0.6F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(-1.75F, -1.0F, -4.3F, 0.6F, 1.0F, 0.6F, new CubeDeformation(0.0F))
                        .texOffs(96, 32).addBox(-0.85F, -0.6F, -4.4F, 1.7F, 0.6F, 0.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.4F, -1.2F, 0.0F, 0.0F, 0.0F));

        PartDefinition wingL = body.addOrReplaceChild("wing_l",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-0.1F, -0.5F, -0.4F, 6.0F, 1.0F, 0.9F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-0.1F, 0.2F, 0.05F, 6.0F, 4.0F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.1F, -1.8F, -1.2F, 0.0F, -0.2793F, 0.4538F));

        PartDefinition wingLOuter = wingL.addOrReplaceChild("wing_l_outer",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(0.0F, -0.4F, -0.35F, 6.2F, 0.8F, 0.7F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(0.0F, 0.2F, -0.05F, 6.2F, 4.4F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(5.9F, 0.0F, 0.05F, 0.0F, 0.2793F, -0.2094F));

        wingLOuter.addOrReplaceChild("wing_l_mem_tip_r1",
                CubeListBuilder.create().texOffs(0, 32).addBox(0.0F, 0.0F, -0.05F, 4.8F, 3.6F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3142F));
        wingLOuter.addOrReplaceChild("wing_l_finger3_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(0.6F, 0.0F, -0.2F, 0.6F, 4.6F, 0.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3142F));
        wingLOuter.addOrReplaceChild("wing_l_finger2_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -0.05F, -0.25F, 4.5F, 0.55F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.8378F));
        wingLOuter.addOrReplaceChild("wing_l_finger1_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -0.3F, -0.3F, 5.8F, 0.6F, 0.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2443F));
        wingLOuter.addOrReplaceChild("wing_l_thumb_r1",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.3F, -1.2F, -0.25F, 0.5F, 1.2F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.2F, -0.3F, -0.05F, -0.2094F, 0.0F, 0.2094F));

        PartDefinition wingR = body.addOrReplaceChild("wing_r",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-5.9F, -0.5F, -0.4F, 6.0F, 1.0F, 0.9F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-5.9F, 0.2F, 0.05F, 6.0F, 4.0F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.1F, -1.8F, -1.2F, 0.0F, 0.2793F, -0.4538F));

        PartDefinition wingROuter = wingR.addOrReplaceChild("wing_r_outer",
                CubeListBuilder.create()
                        .texOffs(48, 0).addBox(-6.2F, -0.4F, -0.35F, 6.2F, 0.8F, 0.7F, new CubeDeformation(0.0F))
                        .texOffs(0, 32).addBox(-6.2F, 0.2F, -0.05F, 6.2F, 4.4F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.9F, 0.0F, 0.05F, 0.0F, -0.2793F, 0.2094F));

        wingROuter.addOrReplaceChild("wing_r_mem_tip_r1",
                CubeListBuilder.create().texOffs(0, 32).addBox(-4.8F, 0.0F, -0.05F, 4.8F, 3.6F, 0.15F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3142F));
        wingROuter.addOrReplaceChild("wing_r_finger3_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.2F, 0.0F, -0.2F, 0.6F, 4.6F, 0.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3142F));
        wingROuter.addOrReplaceChild("wing_r_finger2_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-4.5F, -0.05F, -0.25F, 4.5F, 0.55F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.8378F));
        wingROuter.addOrReplaceChild("wing_r_finger1_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-5.8F, -0.3F, -0.3F, 5.8F, 0.6F, 0.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2443F));
        wingROuter.addOrReplaceChild("wing_r_thumb_r1",
                CubeListBuilder.create().texOffs(64, 32).addBox(-0.2F, -1.2F, -0.25F, 0.5F, 1.2F, 0.5F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.2F, -0.3F, -0.05F, -0.2094F, 0.0F, -0.2094F));

        PartDefinition tail1 = body.addOrReplaceChild("tail_1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.85F, -0.7F, 0.0F, 1.7F, 1.5F, 3.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -0.6F, 3.6F, -0.2094F, 0.0F, 0.0F));

        PartDefinition tail2 = tail1.addOrReplaceChild("tail_2",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.6F, -0.5F, 0.0F, 1.2F, 1.1F, 3.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.6F, -0.2443F, 0.0F, 0.0F));

        PartDefinition tail3 = tail2.addOrReplaceChild("tail_3",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.4F, -0.3F, 0.0F, 0.8F, 0.7F, 3.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 3.4F, -0.2793F, 0.0F, 0.0F));

        tail3.addOrReplaceChild("tail_fork_r_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-1.2F, -0.25F, -0.1F, 1.4F, 0.5F, 2.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.3F, 0.0F, 3.1F, 0.0F, 0.4887F, 0.0F));

        tail3.addOrReplaceChild("tail_fork_l_r1",
                CubeListBuilder.create().texOffs(48, 0).addBox(-0.2F, -0.25F, -0.1F, 1.4F, 0.5F, 2.4F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.3F, 0.0F, 3.1F, 0.0F, -0.4887F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Float age) {
        resetPose();
        float t = age != null ? age : 0.0f;
        float wingPhase = hasOverride ? overrideWingPhase : t * 0.24f;
        float wingAmp = hasOverride ? overrideWingAmp : 0.36f;
        float jawCycle = (float) Math.sin(t * 0.08f);
        float idleJaw = hasOverride ? overrideJaw : (jawCycle > 0.65f ? (jawCycle - 0.65f) * 0.55f : 0.0f);
        float recoil = hasOverride ? overrideRecoil : 0.0f;
        int dir = hasOverride ? overrideDirection : 1;
        float lookYaw = hasOverride ? overrideLookYaw : 0.0f;
        float lookPitch = hasOverride ? overrideLookPitch : 0.0f;
        boolean sprinting = hasOverride && overrideSprinting;
        boolean gliding = hasOverride && overrideGliding;

        applyOrganicPose(t, idleJaw, wingPhase, wingAmp, recoil, dir, lookYaw, lookPitch, sprinting, gliding);
    }

    public void setAnimationParams(float jawAngle, float wingPhase, float wingAmp, float recoil,
                                   int direction, float lookYaw, float lookPitch,
                                   boolean sprinting, boolean gliding) {
        this.hasOverride = true;
        this.overrideJaw = jawAngle;
        this.overrideWingPhase = wingPhase;
        this.overrideWingAmp = wingAmp;
        this.overrideRecoil = recoil;
        this.overrideDirection = direction;
        this.overrideLookYaw = lookYaw;
        this.overrideLookPitch = lookPitch;
        this.overrideSprinting = sprinting;
        this.overrideGliding = gliding;
    }

    public void clearOverride() {
        this.hasOverride = false;
    }

    private static float asymmetricFlap(float phase) {
        return (float) (Math.sin(phase) + 0.35 * Math.sin(2.0 * phase));
    }

    private void applyOrganicPose(float t, float jawAngle, float wingPhase, float wingAmp,
                                  float recoil, int direction, float lookYaw, float lookPitch,
                                  boolean sprinting, boolean gliding) {
        lowerJaw.xRot = jawAngle;

        // Asymmetric downstroke / upstroke + whip-like phase lag on outer wing
        float innerFlap = asymmetricFlap(wingPhase) * wingAmp;
        float outerFlap = asymmetricFlap(wingPhase - 0.6f) * wingAmp;
        float sweepBack = sprinting ? 0.22f : (gliding ? 0.32f : 0.0f);

        leftWing.zRot = 0.4538F - innerFlap;
        rightWing.zRot = -0.4538F + innerFlap;
        leftWing.yRot = -0.2793F - innerFlap * 0.18f + sweepBack;
        rightWing.yRot = 0.2793F + innerFlap * 0.18f - sweepBack;
        leftWingOuter.zRot = -0.2094F - outerFlap * 0.55f;
        rightWingOuter.zRot = 0.2094F + outerFlap * 0.55f;

        // Subtle vertical body bobbing counter to wing downstroke
        body.y -= innerFlap * 0.45f;
        if (sprinting) {
            body.xRot += 0.14f;
        }

        // Tuck legs backward in flight + subtle dangle with wingbeat
        float legTuck = 0.35f + (sprinting ? 0.25f : 0.0f) + outerFlap * 0.15f;
        foreLegL.xRot += legTuck;
        foreLegR.xRot += legTuck;
        hindLegL.xRot += legTuck * 0.85f;
        hindLegR.xRot += legTuck * 0.85f;

        // Neck & head: orbital spine curvature + curious look + full-body snarl recoil
        float neckBreath = (float) Math.sin(t * 0.14f) * 0.04f;
        float forwardPoise = sprinting ? 0.18f : 0.0f;
        neck.xRot += neckBreath + forwardPoise + recoil * 0.26f;
        head.xRot -= neckBreath * 0.8f - lookPitch - recoil * 0.14f;
        body.xRot -= recoil * 0.10f;

        neck.yRot += direction * 0.12f;
        head.yRot += direction * 0.14f + lookYaw;
        head.zRot += lookYaw * 0.25f;

        // 3-segment dragon tail: orbital arc curvature + dual-axis S-wave
        float tailSpeed = sprinting ? 0.28f : 0.18f;
        float tailAmp = sprinting ? 0.24f : 0.17f;
        tail1.yRot += -direction * 0.12f + (float) Math.sin(t * tailSpeed) * tailAmp;
        tail2.yRot += -direction * 0.14f + (float) Math.sin(t * tailSpeed - 0.5f) * (tailAmp * 1.25f);
        tail3.yRot += -direction * 0.16f + (float) Math.sin(t * tailSpeed - 1.0f) * (tailAmp * 1.45f);
        tail1.xRot += (float) Math.cos(t * tailSpeed) * 0.07f;
        tail2.xRot += (float) Math.cos(t * tailSpeed - 0.5f) * 0.08f;
        tail3.xRot += (float) Math.cos(t * tailSpeed - 1.0f) * 0.09f;
    }
}
