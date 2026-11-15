package com.example.client.cosmetic.model.hat;

import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * Napolyon tarzı iki köşeli şapka (Bicorne) - Fransız kokartlı ve altın şeritli.
 * Doku: assets/modid/textures/cosmetic/hat_bicorne.png (64x64).
 */
public class NapoleonBicorneHatModel extends CosmeticModel {
    private final ModelPart bicorne;

    public NapoleonBicorneHatModel(ModelPart root) {
        super(root);
        this.bicorne = root.getChild("bicorne");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition b = root.addOrReplaceChild("bicorne",
                CubeListBuilder.create()
                        // Kafaya oturan ic kubbe
                        .texOffs(0, 0).addBox(-4.3f, -10.5f, -4.3f, 8.6f, 3.0f, 8.6f)
                        // On ve arka yuksek kavisli yelpaze kanatlari (iki kose yanlara uzanir)
                        .texOffs(32, 0).addBox(-7.8f, -13.2f, -3.2f, 15.6f, 5.2f, 0.9f)
                        .texOffs(32, 0).addBox(-6.2f, -14.4f, -3.1f, 12.4f, 1.4f, 0.8f)
                        .texOffs(32, 0).addBox(-7.8f, -12.6f,  2.3f, 15.6f, 4.6f, 0.9f)
                        .texOffs(32, 0).addBox(-6.2f, -13.8f,  2.2f, 12.4f, 1.4f, 0.8f)
                        // Kokart (0, 32)
                        .texOffs(0, 32).addBox(-1.2f, -13.6f, -3.7f, 2.4f, 2.4f, 0.6f),
                PartPose.ZERO);

        // Sol ve sag sivri kose kanatlari
        b.addOrReplaceChild("wingL",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, -2.5f, -2.5f, 3.2f, 3.6f, 5.0f),
                PartPose.offsetAndRotation(-7.4f, -10.2f, 0.0f, 0.0f, 0.0f, 0.18f));
        b.addOrReplaceChild("wingR",
                CubeListBuilder.create().texOffs(0, 0).addBox(-0.2f, -2.5f, -2.5f, 3.2f, 3.6f, 5.0f),
                PartPose.offsetAndRotation(7.4f, -10.2f, 0.0f, 0.0f, 0.0f, -0.18f));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CosmeticRenderState s) {
        this.bicorne.yRot = 0.0f;
    }
}
