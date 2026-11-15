package com.example.client.mixin;

import com.example.ExampleMod;
import com.example.client.KiemCompat;
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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final private Minecraft minecraft;

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
    private void kiem$hideHandsDuringAfk(
            float partialTicks,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            PlayerRenderState playerState,
            FirstPersonHandsAndItemsRenderState handState,
            CallbackInfo ci
    ) {
        if (com.example.client.AfkCinematicsManager.isAfkActive()) {
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "submitArmWithItem",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 3,
            require = 0
    )
    private float kiem$fixEquipProgress(float equipProgress) {
        if (KiemConfig.viewmodelEnabled) {
            return 0.0f;
        }
        return equipProgress;
    }

    @Inject(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void kiem$applyViewmodelTransform(
            PlayerRenderState playerState,
            FirstPersonHandsAndItemsRenderState handState,
            float partialTicks,
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
        LocalPlayer player = this.minecraft.player;
        if (player == null) return;

        boolean isMainHand = (hand == InteractionHand.MAIN_HAND);
        float handSign = (player.getMainArm() == HumanoidArm.RIGHT ? 1.0f : -1.0f) * (isMainHand ? 1.0f : -1.0f);

        poseStack.translate(KiemConfig.itemPosX * handSign, KiemConfig.itemPosY, KiemConfig.itemPosZ);
        poseStack.rotateDegrees(Axis.XP, KiemConfig.itemPitch);
        poseStack.rotateDegrees(Axis.YP, KiemConfig.itemYaw * handSign);
        poseStack.rotateDegrees(Axis.ZP, KiemConfig.itemRoll * handSign);
    }

    @Redirect(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            )
    )
    private void kiem$scaleItemOnly(
            ItemStackRenderState instance,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            int overlay,
            int outlineColor
    ) {
        if (KiemConfig.viewmodelEnabled) {
            float scale = 1.0f + KiemConfig.itemSize * 0.5f;
            if (scale < 0.05f) scale = 0.05f;
            poseStack.scale(scale, scale, scale);
        }
        instance.submit(poseStack, collector, light, overlay, outlineColor);
    }

    @Redirect(
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

    @Inject(method = "renderPlayerArm", at = @At("TAIL"))
    private void kiem$renderFirstPersonHandCosmeticOnArm(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int light,
            float equipProgress,
            float swingProgress,
            HumanoidArm arm,
            PlayerRenderState playerState,
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
            PlayerRenderState playerState,
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
        state.attackSwing = KiemCompat.getAttackAnim(player, partialTick);
        state.isSlimArms = player.getSkin().model() == PlayerModelType.SLIM;
        state.isLeftPart = isLeft;
        state.isFirstPerson = true;

        AvatarRenderer<?> avatarRenderer = KiemCompat.getPlayerRenderer(this.minecraft.getEntityRenderDispatcher(), player);
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
        KiemCompat.submitModel(collector, model, state, poseStack, RenderTypes.entityCutout(texture),
                light, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
