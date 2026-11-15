package com.example.client.mixin;

import com.example.ExampleMod;
import com.example.client.KiemConfig;
import com.example.client.cosmetic.CosmeticCatalog.HandType;
import com.example.client.cosmetic.CosmeticRenderState;
import com.example.client.cosmetic.model.CosmeticModel;
import com.example.client.cosmetic.model.hand.CombatGlovesModel;
import com.example.client.cosmetic.model.hand.FighterWrapsModel;
import com.example.client.cosmetic.model.hand.SylasChainsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow private float mainHandHeight;
    @Shadow private float oMainHandHeight;
    @Shadow private ItemStack mainHandItem;

    @Unique
    private static final Identifier KIEM$TEX_HAND_WRAPS =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_wraps.png");
    @Unique
    private static final Identifier KIEM$TEX_HAND_CHAINS =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_chains.png");
    @Unique
    private static final Identifier KIEM$TEX_HAND_COMBAT_GLOVES =
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_combat_gloves.png");

    @Unique
    private FighterWrapsModel kiem$fighterWraps;
    @Unique
    private SylasChainsModel kiem$sylasChains;
    @Unique
    private CombatGlovesModel kiem$combatGloves;
    @Unique
    private final CosmeticRenderState kiem$rightHandState = new CosmeticRenderState();
    @Unique
    private final CosmeticRenderState kiem$leftHandState = new CosmeticRenderState();

    @Inject(method = "submitHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void kiem$hideHandsDuringAfk(float partialTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.player.LocalPlayer player, int light, CallbackInfo ci) {
        if (com.example.client.AfkCinematicsManager.isAfkActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void kiem$preventReEquipDropOnAttack(CallbackInfo ci) {
        if (!KiemConfig.viewmodelEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // When holding the same item, keep hand height at 1.0 so attack clicks don't drag the weapon down
        if (ItemStack.isSameItemSameComponents(this.mainHandItem, mc.player.getMainHandItem())) {
            this.mainHandHeight = 1.0f;
            this.oMainHandHeight = 1.0f;
        }
    }

    @ModifyVariable(
            method = {"renderArmWithItem", "submitArmWithItem"},
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 3,
            require = 0
    )
    private float kiem$fixEquipProgress(float equipProgress) {
        if (KiemConfig.viewmodelEnabled) {
            // Prevent the weapon from sinking below the screen when clicking/attacking
            return 0.0f;
        }
        return equipProgress;
    }

    @Inject(
            method = {"renderArmWithItem", "submitArmWithItem"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void kiem$applyViewmodelTransform(
            AbstractClientPlayer player,
            float tickDelta,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack stack,
            float equipProgress,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            CallbackInfo ci
    ) {
        if (!KiemConfig.viewmodelEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return;

        boolean isMainHand = (hand == InteractionHand.MAIN_HAND);
        float handSign = (player.getMainArm() == HumanoidArm.RIGHT ? 1.0f : -1.0f) * (isMainHand ? 1.0f : -1.0f);

        // Position offsets
        float x = KiemConfig.itemPosX * handSign;
        float y = KiemConfig.itemPosY;
        float z = KiemConfig.itemPosZ;
        poseStack.translate(x, y, z);

        // Rotation angles
        poseStack.mulPose(Axis.XP.rotationDegrees(KiemConfig.itemPitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(KiemConfig.itemYaw * handSign));
        poseStack.mulPose(Axis.ZP.rotationDegrees(KiemConfig.itemRoll * handSign));
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "swingArm",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
            )
    )
    private void kiem$scaleSwingTranslation(PoseStack instance, float x, float y, float z) {
        if (KiemConfig.viewmodelEnabled && KiemConfig.itemScaleSwing) {
            float scale = 1.0f + KiemConfig.itemSize * 0.5f;
            if (scale < 0.05f) scale = 0.05f;
            instance.translate(x * scale, y * scale, z * scale);
        } else {
            instance.translate(x, y, z);
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void kiem$scaleItemOnly(
            LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext context,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            CallbackInfo ci
    ) {
        if (!KiemConfig.viewmodelEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        if (entity != mc.player) return;

        if (context.firstPerson()) {
            float scale = 1.0f + KiemConfig.itemSize * 0.5f;
            if (scale < 0.05f) scale = 0.05f;
            poseStack.scale(scale, scale, scale);
        }
    }

    @Inject(method = "renderPlayerArm", at = @At("TAIL"))
    private void kiem$renderFirstPersonHandCosmeticOnArm(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            float equipProgress,
            float swingProgress,
            HumanoidArm arm,
            CallbackInfo ci
    ) {
        kiem$renderFirstPersonHandCosmetic(poseStack, collector, light, arm);
    }

    @Inject(
            method = "renderMapHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void kiem$renderFirstPersonHandCosmeticOnMap(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            HumanoidArm arm,
            CallbackInfo ci
    ) {
        kiem$renderFirstPersonHandCosmetic(poseStack, collector, light, arm);
    }

    @Unique
    private void kiem$renderFirstPersonHandCosmetic(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            HumanoidArm arm
    ) {
        if (!KiemConfig.cosmeticHandFirstPerson) return;
        HandType handType = HandType.fromName(KiemConfig.cosmeticHand);
        if (handType == HandType.OFF) return;

        LocalPlayer player = this.minecraft.player;
        if (player == null || player.isInvisible() || player.isSpectator()) return;

        boolean isLeft = (arm == HumanoidArm.LEFT);
        CosmeticRenderState state = isLeft ? kiem$leftHandState : kiem$rightHandState;
        float partialTick = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        state.ageInTicks = player.tickCount + partialTick;
        state.walkPos = player.walkAnimation.position(partialTick);
        state.walkSpeed = player.walkAnimation.speed(partialTick);
        state.crouchAmount = player.isCrouching() ? 1.0f : 0.0f;
        state.flyAmount = (player.isFallFlying() || player.getAbilities().flying) ? 1.0f : 0.0f;
        state.swimAmount = player.getSwimAmount(partialTick);
        state.hurtTime = (float) player.hurtTime;
        state.attackSwing = player.getAttackAnim(partialTick);
        state.isSlimArms = player.getSkin().model() == PlayerModelType.SLIM;
        state.isLeftPart = isLeft;
        state.isFirstPerson = true;

        AvatarRenderer<AbstractClientPlayer> avatarRenderer = this.entityRenderDispatcher.getPlayerRenderer(player);
        PlayerModel playerModel = avatarRenderer.getModel();
        ModelPart armBone = isLeft ? playerModel.leftArm : playerModel.rightArm;

        CosmeticModel model;
        Identifier texture;
        switch (handType) {
            case FIGHTER_WRAPS -> {
                if (kiem$fighterWraps == null) {
                    kiem$fighterWraps = new FighterWrapsModel(FighterWrapsModel.createLayer().bakeRoot());
                }
                model = kiem$fighterWraps;
                texture = KIEM$TEX_HAND_WRAPS;
            }
            case SYLAS_CHAINS -> {
                if (kiem$sylasChains == null) {
                    kiem$sylasChains = new SylasChainsModel(SylasChainsModel.createLayer().bakeRoot());
                }
                model = kiem$sylasChains;
                texture = KIEM$TEX_HAND_CHAINS;
            }
            case COMBAT_GLOVES -> {
                if (kiem$combatGloves == null) {
                    kiem$combatGloves = new CombatGlovesModel(CombatGlovesModel.createLayer().bakeRoot());
                }
                model = kiem$combatGloves;
                texture = KIEM$TEX_HAND_COMBAT_GLOVES;
            }
            default -> {
                return;
            }
        }

        poseStack.pushPose();
        armBone.translateAndRotate(poseStack);
        collector.submitModel(model, state, poseStack, RenderTypes.entityCutout(texture),
                light, OverlayTexture.NO_OVERLAY, 0, (ModelFeatureRenderer.CrumblingOverlay) null);
        poseStack.popPose();
    }
}
