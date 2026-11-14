package com.example.client.cosmetic.model.neck;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Sleek dark/violet tinted anime sunglasses attached to PlayerModel.head,
 * sitting cleanly over the eyes (y = -5.2..-3.5, z = -4.3).
 * Texture: assets/modid/textures/cosmetic/neck_sunglasses.png (256x256)
 */
public class CoolSunglassesModel extends CosmeticModel {
    private final ModelPart glassesGroup;

    public CoolSunglassesModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.glassesGroup = root.getChild("glassesGroup");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition group = root.addOrReplaceChild("glassesGroup",
                CubeListBuilder.create(),
                PartPose.ZERO);

        // Sleek dark metallic top brow bar
        group.addOrReplaceChild("frameTop",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.15f, -5.20f, -4.34f, 8.3f, 0.36f, 0.36f),
                PartPose.ZERO);

        // Nose bridge
        group.addOrReplaceChild("frameBridge",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(-0.75f, -4.85f, -4.36f, 1.5f, 0.42f, 0.34f),
                PartPose.ZERO);

        // Translucent dark-violet tinted anime lenses over eyes (y = -5.05..-3.55, z = -4.32)
        group.addOrReplaceChild("lensL",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(0.75f, -5.05f, -4.32f, 3.1f, 1.55f, 0.24f),
                PartPose.ZERO);

        group.addOrReplaceChild("lensR",
                CubeListBuilder.create()
                        .texOffs(10, 12)
                        .addBox(-3.85f, -5.05f, -4.32f, 3.1f, 1.55f, 0.24f),
                PartPose.ZERO);

        // Outer corner accents
        group.addOrReplaceChild("cornerL",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(3.82f, -5.18f, -4.35f, 0.42f, 0.95f, 0.38f),
                PartPose.ZERO);

        group.addOrReplaceChild("cornerR",
                CubeListBuilder.create()
                        .texOffs(6, 18)
                        .addBox(-4.24f, -5.18f, -4.35f, 0.42f, 0.95f, 0.38f),
                PartPose.ZERO);

        // Temple arms extending back along the sides of the head
        group.addOrReplaceChild("templeL",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(4.06f, -5.02f, -4.18f, 0.26f, 0.40f, 6.4f),
                PartPose.ZERO);

        group.addOrReplaceChild("templeR",
                CubeListBuilder.create()
                        .texOffs(16, 24)
                        .addBox(-4.32f, -5.02f, -4.18f, 0.26f, 0.40f, 6.4f),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        resetPose();
        // Snug fit over the eyes with a tiny subtle bounce on landing/sprint
        float speed = Math.min(1.0f, s.walkSpeed);
        glassesGroup.y = Math.abs(sin(s.walkPos * 0.6662f)) * 0.03f * speed;
    }
}
