package com.example.client.model.pet;

import com.example.client.pet.PetRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Tum pet modellerinin tabani.
 * Koordinat sistemi vanilla entity modelleri gibi: +y asagi, -z on. Orijin = pet'in ayaklarinin merkezi.
 */
public abstract class PetModel extends Model<PetRenderState> {

    protected PetModel(ModelPart root) {
        super(root, id -> RenderTypes.entityCutout(id));
    }

    protected static float sin(float v) {
        return (float) Math.sin(v);
    }

    protected static float cos(float v) {
        return (float) Math.cos(v);
    }

    /** 0..1..0 zarf: phase [0, length) icinde yarim sinus, disinda 0. */
    protected static float burst(float phase, float length) {
        if (phase < 0f || phase >= length) return 0f;
        return sin(phase / length * (float) Math.PI);
    }

    /** Koşarken kücük ziplama (yukari = negatif y). */
    protected static float runBounce(PetRenderState s) {
        return Math.min(s.walkSpeed, 1f) * 0.45f * Math.abs(sin(s.walkPos * 0.6662f));
    }
}
