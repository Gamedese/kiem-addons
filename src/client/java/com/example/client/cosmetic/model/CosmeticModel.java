package com.example.client.cosmetic.model;

import com.example.client.cosmetic.CosmeticRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.function.Function;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Tum genel kozmetik modellerinin ortak taban sinifi.
 * Minecraft EntityModel / ModelPart koordinat sistemi: +Y asagi, -Z on.
 */
public abstract class CosmeticModel extends Model<CosmeticRenderState> {

    protected CosmeticModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
    }

    protected CosmeticModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }

    protected static float sin(float v) {
        return (float) Math.sin(v);
    }

    protected static float cos(float v) {
        return (float) Math.cos(v);
    }
}
