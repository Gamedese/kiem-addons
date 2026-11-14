package com.example.client.cosmetic;

/**
 * Genel kozmetik modellerinin animasyon durum verisi.
 * Her karede {@link GeneralCosmeticsLayer} tarafindan guncellenir.
 */
public class CosmeticRenderState {
    public float ageInTicks;
    public float walkPos;
    public float walkSpeed;
    public float crouchAmount;
    public float flyAmount;
    public float swimAmount;
    public float hurtTime;
    public float attackSwing;
    public boolean isSlimArms;
    public boolean isLeftPart;
    public boolean isFirstPerson;
    public float headYRot;
    public float headXRot;
}
