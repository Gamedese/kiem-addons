package com.example.client.pet;

import com.example.client.CapeManager;
import com.example.client.KiemConfig;
import com.example.client.model.pet.DragonPetModel;
import com.example.client.model.pet.HamsterHdPetModel;
import com.example.client.model.pet.HamsterPetModel;
import com.example.client.model.pet.InventoryCursePetModel;
import com.example.client.model.pet.OctopusPetModel;
import com.example.client.model.pet.VoidGargoylePetModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Omuz yoldasi katmani. Vanilla {@code ParrotOnShoulderLayer} ile ayni koordinat uzayinda calisir:
 * PoseStack oyuncu modelinin uzayindadir (y asagi, origin = omuz hattinin ustu), bu yuzden sneak/yuzme/
 * elytra sirasinda vucut donunce pet de onunla birlikte doner.
 * Yalnizca yerel oyuncuda cizilir (diger oyuncularda mod yok).
 */
public class ShoulderPetLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier TEX_HAMSTER = Identifier.fromNamespaceAndPath("modid", "textures/pet/hamster.png");
    private static final Identifier TEX_HAMSTER_HD = Identifier.fromNamespaceAndPath("modid", "textures/pet/hamster_hd.png");
    private static final Identifier TEX_DRAGON = Identifier.fromNamespaceAndPath("modid", "textures/pet/dragon.png");
    private static final Identifier TEX_DRAGON_EYES = Identifier.fromNamespaceAndPath("modid", "textures/pet/dragon_eyes.png");
    private static final Identifier TEX_OCTO_HAPPY = Identifier.fromNamespaceAndPath("modid", "textures/pet/octopus_happy.png");
    private static final Identifier TEX_OCTO_ANGRY = Identifier.fromNamespaceAndPath("modid", "textures/pet/octopus_angry.png");
    private static final Identifier TEX_INVENTORY_CURSE = Identifier.fromNamespaceAndPath("modid", "textures/pet/inventory_curse.png");
    private static final Identifier TEX_VOID_GARGOYLE = Identifier.fromNamespaceAndPath("modid", "textures/aura/void_gargoyle.png");
    private static final Identifier TEX_VOID_GARGOYLE_EMISSIVE = Identifier.fromNamespaceAndPath("modid", "textures/aura/void_gargoyle_emissive.png");

    /** Ahtapot takla suresi (tick). */
    private static final float FLIP_TICKS = 24f;

    private final HamsterPetModel hamster = new HamsterPetModel(HamsterPetModel.createLayer().bakeRoot());
    private final HamsterHdPetModel hamsterHd = new HamsterHdPetModel(HamsterHdPetModel.createLayer().bakeRoot());
    private final OctopusPetModel octopus = new OctopusPetModel(OctopusPetModel.createLayer().bakeRoot());
    private final DragonPetModel dragon = new DragonPetModel(DragonPetModel.createLayer().bakeRoot());
    private final InventoryCursePetModel inventoryCurse = new InventoryCursePetModel(InventoryCursePetModel.createLayer().bakeRoot());
    private final VoidGargoylePetModel voidGargoyle = new VoidGargoylePetModel(VoidGargoylePetModel.createLayer().bakeRoot());
    private final PetRenderState petState = new PetRenderState();



    // Ahtapot ruh hali durumu (sadece yerel oyuncu icin tek ornek yeterli)
    private boolean moodInit = false;
    private boolean moodAngry = false;
    private float flipStart = -10000f;

    // Ahtapot eylemsizlik & yayli elastik takip fizigi (Damped Spring Physics)
    private float octoLagZ = 0f;
    private float octoVelZ = 0f;
    private float octoLagY = 0f;
    private float octoVelY = 0f;
    private long lastOctoNano = 0;

    // Yercekimi, dikey eylemsizlik ve yere inis takibi
    private boolean wasOnGround = true;
    private float lastFallVel = 0f;
    private float landImpactTime = -10000f;
    private float landImpactStrength = 0f;

    // Oyuncu etkilesim, saldiri ve AFK / uyku takibi
    private float lastActiveAge = 0f;
    private float sleepProgress = 0f;

    // Tick tabanli (FPS'ten bagimsiz) yumusak durum gecisleri
    private float lastSmoothAge = -1f;
    private float crouchAmount = 0f;
    private float eatAmount = 0f;
    private float flyAmount = 0f;
    private float airSinceAge = 0f;

    /** Ustel yaklasma: rate = tick basina hiz, dt = gecen tick. */
    private static float approach(float cur, float target, float rate, float dt) {
        return cur + (target - cur) * (1f - (float) Math.exp(-rate * dt));
    }

    public ShoulderPetLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light,
                       AvatarRenderState state, float yRot, float xRot) {
        // GUI aura preview must not run this layer's live shoulder-pet physics twice.
        if (((com.example.client.aura.SakuraPreviewState) (Object) state).kiem$getSakuraPreview() != null) return;
        ShoulderPetType type = ShoulderPetType.fromName(KiemConfig.shoulderPetType);
        if (type == ShoulderPetType.OFF) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || state.id != mc.player.getId()) return;
        if (state.isInvisible || state.isSpectator) return;

        boolean left = "LEFT".equalsIgnoreCase(KiemConfig.shoulderPetSide);

        boolean onGround = mc.player.onGround();
        float yVel = (float) mc.player.getDeltaMovement().y;

        // Inis / dusus takibi: havadayken en sert dusus hizini yakala, yere dokununca inisi tetikle
        if (!wasOnGround && onGround) {
            if (lastFallVel < -0.12f) {
                landImpactStrength = Math.min(1.2f, Math.abs(lastFallVel) * 1.8f);
                landImpactTime = state.ageInTicks;
            }
            lastFallVel = 0f;
        } else if (!onGround) {
            if (yVel < 0f) {
                lastFallVel = Math.min(lastFallVel, yVel);
            }
        }
        wasOnGround = onGround;

        // Yere inis yaylanmasi: sonumlenen harmonik salinim (damped spring oscillation - 7 tick)
        float landSquash = 0f;
        if (state.ageInTicks < landImpactTime) landImpactTime = -10000f;
        if (landImpactTime > 0f) {
            float elapsed = state.ageInTicks - landImpactTime;
            if (elapsed >= 0f && elapsed < 7f) {
                float envelope = 1f - (elapsed / 7f);
                landSquash = (float) Math.sin(elapsed * 1.05f) * envelope * landImpactStrength;
            } else {
                landImpactTime = -10000f;
            }
        }

        // Oyuncu etkilesim durumlarini yakala
        boolean playerActive = state.walkAnimationSpeed > 0.05f 
                || mc.player.isUsingItem() 
                || mc.player.hurtTime > 0 
                || mc.player.getAttackAnim(0f) > 0.05f
                || Math.abs(yVel) > 0.08f;
        if (playerActive) {
            lastActiveAge = state.ageInTicks;
        }

        boolean isSleeping = mc.player.isSleeping() || (state.ageInTicks - lastActiveAge > 200f);

        // Gecen sure (tick). HUD kuklasi ayni karede tekrar cizerse dt ~ 0 olur, gecisler hizlanmaz.
        float smoothDt = state.ageInTicks - lastSmoothAge;
        if (lastSmoothAge < 0f || smoothDt < 0f || smoothDt > 5f) smoothDt = 0f;
        lastSmoothAge = state.ageInTicks;

        float targetSleep = isSleeping ? 1.0f : 0.0f;
        sleepProgress = approach(sleepProgress, targetSleep, 0.08f, smoothDt);

        boolean isEating = mc.player.isUsingItem() && mc.player.getUseItem().getUseAnimation() == net.minecraft.world.item.ItemUseAnimation.EAT;

        // Ucus: elytra, yaratici ucus veya ~1.25 sn'den uzun havada kalma (normal ziplama sayilmaz)
        boolean grounded = onGround || mc.player.isInWater() || mc.player.onClimbable();
        if (grounded || state.ageInTicks < airSinceAge) airSinceAge = state.ageInTicks;
        boolean isFlying = mc.player.isFallFlying()
                || mc.player.getAbilities().flying
                || (state.ageInTicks - airSinceAge) > 25f;

        crouchAmount = approach(crouchAmount, state.isCrouching ? 1f : 0f, 0.45f, smoothDt);
        eatAmount = approach(eatAmount, isEating ? 1f : 0f, 0.25f, smoothDt);
        flyAmount = approach(flyAmount, isFlying ? 1f : 0f, 0.15f, smoothDt);



        petState.age = state.ageInTicks;
        petState.walkPos = state.walkAnimationPos;
        petState.walkSpeed = state.walkAnimationSpeed;
        petState.crouching = state.isCrouching;
        petState.onGround = onGround;
        petState.yVel = yVel;
        petState.landSquash = 0f;
        petState.withSword = KiemConfig.inventoryCurseWithSword;
        petState.hasCape = (CapeManager.getCurrentCape() != CapeManager.CapeType.OFF)
                || (state.showCape && state.skin != null && state.skin.cape() != null);
        petState.capeAngleDeg = 6.0f + (state.capeLean / 2.0f) + state.capeFlap;

        petState.hurtTime = (float) mc.player.hurtTime;
        petState.isEating = isEating;
        petState.isSleeping = isSleeping;
        petState.sleepProgress = sleepProgress;
        petState.attackSwing = mc.player.getAttackAnim(0f);
        petState.leftShoulder = left;
        petState.crouchAmount = crouchAmount;
        petState.eatAmount = eatAmount;

        if (type == ShoulderPetType.OCTOPUS) {
            long now = System.nanoTime();
            float dt = (lastOctoNano == 0) ? 0.016f : (float) ((now - lastOctoNano) / 1_000_000_000.0);
            lastOctoNano = now;
            dt = Math.max(0.001f, Math.min(0.06f, dt));

            // Z Ekseni (Ileri / Geri): Kosarken arkada kalis (+Z), aniden durunca tatlica one suzulme (-Z)
            net.minecraft.world.phys.Vec3 vel = mc.player.getDeltaMovement();
            float yRotRad = (float) Math.toRadians(mc.player.getYRot());
            float forwardSpeed = (float) (-vel.x * Math.sin(yRotRad) + vel.z * Math.cos(yRotRad));

            // Ileri hareket edince hedef Z pozitif (arkada kalis), durunca 0
            float targetZ = Math.max(-0.15f, Math.min(0.20f, forwardSpeed * 1.0f));
            float springFreqZ = 10.0f; // Yumusak ve stabil yay
            float dampingZ = 0.58f;    // Az sonumlu yay: aniden durunca tatlica one overshoot yapip geri oturur
            float accelZ = springFreqZ * springFreqZ * (targetZ - octoLagZ) - 2.0f * dampingZ * springFreqZ * octoVelZ;
            octoVelZ += accelZ * dt;
            octoLagZ += octoVelZ * dt;
            octoLagZ = Math.max(-0.25f, Math.min(0.25f, octoLagZ));

            float maxHp = mc.player.getMaxHealth();
            float hp = mc.player.getHealth();
            boolean lowHp = (hp / Math.max(1.0f, maxHp)) <= 0.41f; // %41 can veya alti

            int blockLight = 15;
            int skyLight = 15;
            boolean isNight = false;
            boolean isRainingHere = false;
            if (mc.level != null) {
                net.minecraft.core.BlockPos pos = mc.player.blockPosition();
                blockLight = mc.level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
                skyLight = mc.level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos);
                isNight = mc.level.isDarkOutside() && (skyLight > 4) && (blockLight <= 4);
                isRainingHere = mc.level.isRainingAt(pos);
            }
            boolean isDarkCave = (blockLight <= 4) && (skyLight <= 4);
            boolean onFire = mc.player.isOnFire();
            boolean inWater = mc.player.isInWater() || mc.player.isUnderWater() || mc.player.isInWaterOrRain();

            petState.inWaterOrRain = inWater || isRainingHere;
            petState.isSprinting = mc.player.isSprinting();
            petState.isSwimming = (state.swimAmount > 0.05f) || state.isVisuallySwimming;

            boolean shouldBeAngry = lowHp || isDarkCave || isNight || onFire;
            updateMood(state.ageInTicks, shouldBeAngry);
        }

        poseStack.pushPose();

        // Her pet tipine ozel 100% bagimsiz ve izole edilmis konum, olcek ve rotasyon sistemi
        float x = 0f;
        float y = 0f;
        float z = 0f;
        float scale = 1f;
        float petFacingAngle = 0f;
        float rollAngle = 0f;

        boolean hasChestArmor = !state.chestEquipment.isEmpty();
        boolean hasCape = (CapeManager.getCurrentCape() != CapeManager.CapeType.OFF)
                || (state.showCape && state.skin != null && state.skin.cape() != null);
        boolean isCrouching = state.isCrouching;

        switch (type) {
            case HAMSTER -> {
                // Klasik Chibi Hamster: orijinal calisan duzgun degerlerine geri alindi
                x = left ? 0.38f : -0.38f;
                y = -0.01f;
                z = -0.01f;
                scale = 0.65f;
                petFacingAngle = left ? -0.10f : 0.10f;

                if (hasChestArmor) y -= 0.05f; // Zirh omuzlugunun ustune cik
                if (hasCape) z -= 0.02f;       // Pelerinle cakismayi onle
                if (isCrouching) y += 0.20f;   // Sneak ile govdeyle alcal
            }
            case HAMSTER_HD -> {
                // HD Pofuduk Chibi Hamster: tam 1 piksel asagi ve 1 piksel geri hassas kalibre
                x = left ? 0.38f : -0.38f;
                y = 0.01f;
                z = 0.00f;
                scale = 0.33f;
                petFacingAngle = left ? -0.12f : 0.12f;

                if (hasChestArmor) y -= 0.05f; // Zirh omuzlugunun ustune bas
                if (hasCape) z -= 0.02f;       // Pelerine degmez
                if (isCrouching) y += 0.20f;   // Sneak ile govdeyle alcal
            }
            case OCTOPUS -> {
                // Havada suzulen yoldas: omza basmaz, omuz hizasinda havada yaninda ucar
                float hoverBob = (float) Math.sin(state.ageInTicks * 0.085f) * 0.055f;
                float hoverSway = (float) Math.cos(state.ageInTicks * 0.055f) * 0.035f;
                x = (left ? 0.50f : -0.50f) + (left ? hoverSway : -hoverSway);
                y = -0.22f + hoverBob; // 2 piksel yukarida stabil suzulur, kosarken omuza batmaz
                z = octoLagZ;          // Damped spring eylemsizlik fizigi
                scale = 0.38f;
                petFacingAngle = left ? -0.15f : 0.15f;

                if (isCrouching) y += 0.15f;
                float bank = (float) Math.sin(state.walkAnimationPos * 0.6f) * state.walkAnimationSpeed * 0.08f;
                rollAngle = left ? -bank : bank;
            }
            case DRAGON -> {
                // Bebek Ejderha: Sadece ve sadece omuzda sabit asil tunek (orbit/ucma/parasut yok)
                x = left ? 0.35f : -0.35f;
                y = 0.0f;
                z = 0.0f;
                scale = 0.90f;
                petFacingAngle = left ? -0.06f : 0.06f;

                if (hasChestArmor) y -= 0.05f;
                if (hasCape) z -= 0.02f;
                if (isCrouching) y += 0.18f;
            }
            case INVENTORY_CURSE -> {
                // Toji Envanter Yilani: Govdeyi capraz sarar
                x = 0.0f;
                y = 0.0f;
                z = 0.0f;
                scale = 1.0f;
                petFacingAngle = 0.0f;

                if (hasChestArmor) y -= 0.02f;
                if (isCrouching) y += 0.20f;
            }
            case VOID_GARGOYLE -> {
                // Obsidian Void Gargoyle: Omuzda tuneyen minik akkor ejderha
                x = left ? 0.36f : -0.36f;
                y = -0.10f;
                z = 0.0f;
                scale = 0.44f;
                petFacingAngle = left ? -0.10f : 0.10f;

                if (hasChestArmor) y -= 0.05f;
                if (hasCape) z -= 0.03f;
                if (isCrouching) y += 0.20f;
            }
            default -> { }
        }

        poseStack.translate(x, y, z);
        if (petFacingAngle != 0f) {
            poseStack.mulPose(Axis.YP.rotation(petFacingAngle));
        }
        if (rollAngle != 0f) {
            poseStack.mulPose(Axis.ZP.rotation(rollAngle));
        }
        poseStack.scale(scale, scale, scale);

        switch (type) {
            case HAMSTER -> render(collector, hamster, poseStack, TEX_HAMSTER, light, state.outlineColor);
            case HAMSTER_HD -> render(collector, hamsterHd, poseStack, TEX_HAMSTER_HD, light, state.outlineColor);
            case OCTOPUS -> {
                Identifier tex = petState.angry ? TEX_OCTO_ANGRY : TEX_OCTO_HAPPY;
                render(collector, octopus, poseStack, tex, light, state.outlineColor);
            }
            case DRAGON -> {
                render(collector, dragon, poseStack, TEX_DRAGON, light, state.outlineColor);
                // Parlayan mor ejderha gozleri
                collector.submitModel(dragon, petState, poseStack, RenderTypes.eyes(TEX_DRAGON_EYES),
                        0x00F000F0, OverlayTexture.NO_OVERLAY, state.outlineColor,
                        (ModelFeatureRenderer.CrumblingOverlay) null);
            }
            case INVENTORY_CURSE -> render(collector, inventoryCurse, poseStack, TEX_INVENTORY_CURSE, light, state.outlineColor);
            case VOID_GARGOYLE -> {
                render(collector, voidGargoyle, poseStack, TEX_VOID_GARGOYLE, light, state.outlineColor);
                collector.submitModel(voidGargoyle, petState, poseStack, RenderTypes.eyes(TEX_VOID_GARGOYLE_EMISSIVE),
                        0x00F000F0, OverlayTexture.NO_OVERLAY, state.outlineColor,
                        (ModelFeatureRenderer.CrumblingOverlay) null);
            }
            default -> { }
        }

        poseStack.popPose();
    }

    private void render(SubmitNodeCollector collector, Model<PetRenderState> model, PoseStack poseStack,
                        Identifier tex, int light, int outline) {
        collector.submitModel(model, petState, poseStack, tex, light, OverlayTexture.NO_OVERLAY, outline, null);
    }

    /**
     * Ruh hali: can durumu, magara/gece karanligi ve alev gibi cevresel tetikleyicilere gore ahtapot takla atar.
     * Hedef degisince FLIP_TICKS suren takla baslar; yuz, takla ortasinda degisir.
     */
    private void updateMood(float age, boolean targetAngry) {
        if (!moodInit) {
            moodInit = true;
            moodAngry = targetAngry;
            flipStart = -10000f;
        }
        if (age < flipStart) flipStart = -10000f; // dunya degisti / yas sifirlandi
        boolean flipping = age - flipStart < FLIP_TICKS;
        if (!flipping && targetAngry != moodAngry) {
            moodAngry = targetAngry;
            flipStart = age;
            flipping = true;
        }
        if (flipping) {
            float p = (age - flipStart) / FLIP_TICKS;
            petState.flip = p;
            petState.angry = p >= 0.5f ? moodAngry : !moodAngry;
        } else {
            petState.flip = -1f;
            petState.angry = moodAngry;
        }
    }
}
