package com.example.client.pet;

/** Pet modellerinin setupAnim'de okudugu, katman tarafindan her kare doldurulan veri. */
public class PetRenderState {
    /** Partial tick dahil yas (tick). */
    public float age;
    public float walkPos;
    public float walkSpeed;
    public boolean crouching;

    /** Dikey hareket ve yercekimi / inis fizigi */
    public float yVel;
    public boolean onGround = true;
    public float landSquash;

    /** Ahtapot: takla ilerlemesi 0..1, takla yoksa -1. */
    public float flip = -1f;
    /** Ahtapot: o an gosterilen ruh hali (texture secimi icin de kullanilir). */
    public boolean angry;
    /** Ortam ve hareket durumlari (su/yagmur, sprint, yuzme) */
    public boolean inWaterOrRain;
    public boolean isSprinting;
    public boolean isSwimming;
    /** Toji Lanet Solucani: agzinda Inverted Spear of Heaven kilici var mi? */
    public boolean withSword;
    /** Pelerin aktif mi ve anlik X ekseni acisi (derece, vanilla PlayerCapeModel: 6 + capeLean/2 + capeFlap). */
    public boolean hasCape;
    public float capeAngleDeg;


    /** Yeni Etkileşimli Animasyon Durumları */
    public float hurtTime;        // 0..10 (oyuncu darbe aldiginda korkup boyna gomulme)
    public boolean isEating;      // Oyuncu yemek yerken meraka kapilip koklama
    public boolean isSleeping;    // AFK veya yatakta derin uyku modu
    public float sleepProgress;   // 0..1 uykuya dalma/uyanma yumusatma orani
    public float attackSwing;     // 0..1 oyuncu saldirirken kanat kabartma

    /** Ejderha omuz tunegi: hangi omuzda (sag omuzda bas pozu aynalanir) */
    public boolean leftShoulder = true;
    /** Katmanda tick tabanli yumusatilmis 0..1 gecisler (pozlar asla zipplamasin diye) */
    public float crouchAmount;
    public float eatAmount;
    public float flyAmount;
    /** Sudan cikinca/yagmurdan cikinca 0..1 silkinme yogunlugu */
    public float dryShake;
    /** Suda yuzme 0..1 gecisi */
    public float swimAmount;
    /** Yerde otonom geziyor mu (omuz yan bakisi kapatilir) */
    public boolean groundRoam;
}
