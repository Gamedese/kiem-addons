package com.example.client.cosmetic.model.neck;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Koyu kirmizi lake Oni Samuray Alt Yuz Maskesi (Oni Menpo).
 * Doku: assets/modid/textures/cosmetic/neck_oni_menpo.png (64x64).
 */
public class OniMenpoModel extends CosmeticModel {
    private final ModelPart menpo;

    public OniMenpoModel(ModelPart root) {
        super(root);
        this.menpo = root.getChild("menpo");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        CubeListBuilder b = CubeListBuilder.create()
                // Ana alt yuz ve cene zirhi
                .texOffs(0, 0).addBox(-4.3f, -2.9f, -4.55f, 8.6f, 2.9f, 0.9f)
                .texOffs(0, 0).addBox(-4.2f, -0.5f, -4.45f, 8.4f, 0.9f, 2.2f)
                // Burun koruyucu cikinti
                .texOffs(0, 0).addBox(-1.1f, -3.7f, -4.85f, 2.2f, 1.3f, 0.9f)
                // Yanak yan plakalar
                .texOffs(0, 0).addBox(-4.5f, -3.2f, -4.2f, 0.8f, 3.0f, 3.2f)
                .texOffs(0, 0).addBox( 3.7f, -3.2f, -4.2f, 0.8f, 3.0f, 3.2f)
                // Ense bagcigi
                .texOffs(0, 32).addBox(-4.35f, -2.5f, -1.0f, 8.7f, 0.8f, 5.4f)
                // 4 Sivri Oni disi (32, 0)
                .texOffs(32, 0).addBox(-2.8f, -2.3f, -4.85f, 0.85f, 1.4f, 0.6f)
                .texOffs(32, 0).addBox( 1.95f, -2.3f, -4.85f, 0.85f, 1.4f, 0.6f)
                .texOffs(32, 0).addBox(-1.3f, -1.9f, -4.75f, 0.8f, 1.0f, 0.5f)
                .texOffs(32, 0).addBox( 0.5f, -1.9f, -4.75f, 0.8f, 1.0f, 0.5f);

        root.addOrReplaceChild("menpo", b, PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.menpo.yRot = 0.0f;
    }
}
