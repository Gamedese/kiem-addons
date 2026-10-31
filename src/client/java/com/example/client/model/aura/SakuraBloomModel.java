package com.example.client.model.aura;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * 5-petal Sakura Ground Bloom model with golden pollen center.
 * Created for the Sakura Bloom Trail ground blossom effect.
 */
public final class SakuraBloomModel extends Model<Float> {
    private final ModelPart flower;

    public SakuraBloomModel() {
        super(createLayer().bakeRoot(), RenderTypes::entityCutout);
        this.flower = root.getChild("flower");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition flower = root.addOrReplaceChild("flower",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.6F, -0.6F, -0.6F, 1.2F, 0.6F, 1.2F),
                PartPose.ZERO);

        flower.addOrReplaceChild("petal_0",
                CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -0.35F, -0.6F, 1.0F, 0.35F, 1.2F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, -0.0698F, 0.0F, 0.0F));

        flower.addOrReplaceChild("petal_1",
                CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -0.35F, -0.6F, 1.0F, 0.35F, 1.2F),
                PartPose.offsetAndRotation(-0.9511F, 0.0F, 0.309F, -0.0698F, -1.2566F, 0.0F));

        flower.addOrReplaceChild("petal_2",
                CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -0.35F, -0.6F, 1.0F, 0.35F, 1.2F),
                PartPose.offsetAndRotation(-0.5878F, 0.0F, -0.809F, -0.0698F, -2.5133F, 0.0F));

        flower.addOrReplaceChild("petal_3",
                CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -0.35F, -0.6F, 1.0F, 0.35F, 1.2F),
                PartPose.offsetAndRotation(0.5878F, 0.0F, -0.809F, -0.0698F, -3.7699F, 0.0F));

        flower.addOrReplaceChild("petal_4",
                CubeListBuilder.create().texOffs(5, 0).addBox(-0.5F, -0.35F, -0.6F, 1.0F, 0.35F, 1.2F),
                PartPose.offsetAndRotation(0.9511F, 0.0F, 0.309F, -0.0698F, -5.0265F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(Float state) {
        resetPose();
    }
}
