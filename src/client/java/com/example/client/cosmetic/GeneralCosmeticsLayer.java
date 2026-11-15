package com.example.client.cosmetic;

import com.example.ExampleMod;
import com.example.client.KiemCompat;
import com.example.client.KiemConfig;
import com.example.client.cosmetic.CosmeticCatalog.FootType;
import com.example.client.cosmetic.CosmeticCatalog.HandType;
import com.example.client.cosmetic.CosmeticCatalog.HatType;
import com.example.client.cosmetic.CosmeticCatalog.NeckType;
import com.example.client.cosmetic.CosmeticCatalog.WaistType;
import com.example.client.cosmetic.CosmeticCatalog.WingType;
import com.example.client.cosmetic.model.CosmeticModel;
import com.example.client.cosmetic.model.foot.BareFeetModel;
import com.example.client.cosmetic.model.foot.ClassicSneakersModel;
import com.example.client.cosmetic.model.foot.CombatBootsModel;
import com.example.client.cosmetic.model.foot.FighterLegWrapsModel;
import com.example.client.cosmetic.model.foot.HermesBootsModel;
import com.example.client.cosmetic.model.foot.ShinobiLegWrapsModel;
import com.example.client.cosmetic.model.hand.CombatGlovesModel;
import com.example.client.cosmetic.model.hand.FighterWrapsModel;
import com.example.client.cosmetic.model.hand.SylasChainsModel;
import com.example.client.cosmetic.model.hat.BrokenHaloModel;
import com.example.client.cosmetic.model.hat.CelestialHaloModel;
import com.example.client.cosmetic.model.hat.EgirlHeadphonesModel;
import com.example.client.cosmetic.model.hat.KitsuneEarsModel;
import com.example.client.cosmetic.model.hat.KitsuneMaskModel;
import com.example.client.cosmetic.model.hat.NapoleonBicorneHatModel;
import com.example.client.cosmetic.model.hat.OniHornsModel;
import com.example.client.cosmetic.model.hat.RoninHatModel;
import com.example.client.cosmetic.model.hat.RoyalCrownModel;
import com.example.client.cosmetic.model.hat.ShinobiHeadbandModel;
import com.example.client.cosmetic.model.hat.StrawPirateHatModel;
import com.example.client.cosmetic.model.hat.TricornHatModel;
import com.example.client.cosmetic.model.hat.UshankaHatModel;
import com.example.client.cosmetic.model.hat.WitchHatModel;
import com.example.client.cosmetic.model.neck.CoolSunglassesModel;
import com.example.client.cosmetic.model.neck.CrimsonScarfModel;
import com.example.client.cosmetic.model.neck.GoldChainModel;
import com.example.client.cosmetic.model.neck.OniMenpoModel;
import com.example.client.cosmetic.model.neck.RubyChokerModel;
import com.example.client.cosmetic.model.neck.ShadowNinjaMaskModel;
import com.example.client.cosmetic.model.neck.SnowScarfModel;
import com.example.client.cosmetic.model.waist.CatTailModel;
import com.example.client.cosmetic.model.waist.DemonTailModel;
import com.example.client.cosmetic.model.waist.KatanaSashModel;
import com.example.client.cosmetic.model.waist.KitsuneTailModel;
import com.example.client.cosmetic.model.waist.TwinDaggersModel;
import com.example.client.cosmetic.model.waist.WolfTailModel;
import com.example.client.cosmetic.model.wing.SeraphimAngelWingsModel;
import com.example.client.cosmetic.model.wing.SeraphimWingsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;

/**
 * Genel kozmetik katmani (Kanat, Sapka, Boyun, El, Bel, Ayak).
 * Her slot bagimsiz olarak ayni anda takilabilir ve ilgili PlayerModel kemigine baglanir.
 * Yalnizca yerel oyuncuda (dunya ve GUI onizlemesinde) cizilir.
 */
public class GeneralCosmeticsLayer extends RenderLayer<AvatarRenderState, PlayerModel> {

    // --- Textures ---
    private static final Identifier TEX_WING_SERAPHIM = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/wing_seraphim.png");
    private static final Identifier TEX_WING_SERAPHIM_ANGEL = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/wing_seraphim_angel.png");

    private static final Identifier TEX_HAT_RONIN = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_ronin.png");
    private static final Identifier TEX_HAT_SHINOBI = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_shinobi.png");
    private static final Identifier TEX_HAT_WITCH = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_witch.png");
    private static final Identifier TEX_HAT_KITSUNE = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_kitsune.png");
    private static final Identifier TEX_HAT_CELESTIAL_HALO = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_celestial_halo.png");
    private static final Identifier TEX_HAT_ONI_HORNS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_oni_horns.png");
    private static final Identifier TEX_HAT_KITSUNE_MASK = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_kitsune_mask.png");
    private static final Identifier TEX_HAT_BROKEN_HALO = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_broken_halo.png");
    private static final Identifier TEX_HAT_CROWN = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_crown.png");
    private static final Identifier TEX_HAT_STRAW_PIRATE = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_straw_pirate.png");
    private static final Identifier TEX_HAT_USHANKA = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_ushanka.png");
    private static final Identifier TEX_HAT_TRICORN = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_tricorn.png");
    private static final Identifier TEX_HAT_BICORNE = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_bicorne.png");
    private static final Identifier TEX_HAT_EGIRL_HEADPHONES = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hat_egirl_headphones.png");

    private static final Identifier TEX_NECK_SCARF = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_scarf.png");
    private static final Identifier TEX_NECK_CHAIN = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_chain.png");
    private static final Identifier TEX_NECK_NINJA_MASK = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_ninja_mask.png");
    private static final Identifier TEX_NECK_ONI_MENPO = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_oni_menpo.png");
    private static final Identifier TEX_NECK_SUNGLASSES = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_sunglasses.png");
    private static final Identifier TEX_NECK_SNOW_SCARF = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_snow_scarf.png");
    private static final Identifier TEX_NECK_RUBY_CHOKER = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/neck_ruby_choker.png");

    private static final Identifier TEX_HAND_WRAPS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_wraps.png");
    private static final Identifier TEX_HAND_CHAINS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_chains.png");
    private static final Identifier TEX_HAND_COMBAT_GLOVES = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/hand_combat_gloves.png");

    private static final Identifier TEX_WAIST_KATANA = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_katana.png");
    private static final Identifier TEX_WAIST_TAIL = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_tail.png");
    private static final Identifier TEX_WAIST_DEMON_TAIL = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_demon_tail.png");
    private static final Identifier TEX_WAIST_TWIN_DAGGERS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_twin_daggers.png");
    private static final Identifier TEX_WAIST_CAT_TAIL = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_cat_tail.png");
    private static final Identifier TEX_WAIST_WOLF_TAIL = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/waist_wolf_tail.png");

    private static final Identifier TEX_FOOT_HERMES = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_hermes.png");
    private static final Identifier TEX_FOOT_SHINOBI = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_shinobi.png");
    private static final Identifier TEX_FOOT_FIGHTER_WRAPS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_fighter_wraps.png");
    private static final Identifier TEX_FOOT_BARE = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_bare.png");
    private static final Identifier TEX_FOOT_COMBAT_BOOTS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_combat_boots.png");
    private static final Identifier TEX_FOOT_SNEAKERS = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "textures/cosmetic/foot_sneakers.png");

    // --- Models ---
    private final SeraphimWingsModel seraphimWings = new SeraphimWingsModel(SeraphimWingsModel.createLayer().bakeRoot());
    private final SeraphimAngelWingsModel seraphimAngelWings = new SeraphimAngelWingsModel(SeraphimAngelWingsModel.createLayer().bakeRoot());

    private final RoninHatModel roninHat = new RoninHatModel(RoninHatModel.createLayer().bakeRoot());
    private final ShinobiHeadbandModel shinobiHeadband = new ShinobiHeadbandModel(ShinobiHeadbandModel.createLayer().bakeRoot());
    private final WitchHatModel witchHat = new WitchHatModel(WitchHatModel.createLayer().bakeRoot());
    private final KitsuneEarsModel kitsuneEars = new KitsuneEarsModel(KitsuneEarsModel.createLayer().bakeRoot());
    private final CelestialHaloModel celestialHalo = new CelestialHaloModel(CelestialHaloModel.createLayer().bakeRoot());
    private final OniHornsModel oniHorns = new OniHornsModel(OniHornsModel.createLayer().bakeRoot());
    private final KitsuneMaskModel kitsuneMask = new KitsuneMaskModel(KitsuneMaskModel.createLayer().bakeRoot());
    private final BrokenHaloModel brokenHalo = new BrokenHaloModel(BrokenHaloModel.createLayer().bakeRoot());
    private final RoyalCrownModel royalCrown = new RoyalCrownModel(RoyalCrownModel.createLayer().bakeRoot());
    private final StrawPirateHatModel strawPirateHat = new StrawPirateHatModel(StrawPirateHatModel.createLayer().bakeRoot());
    private final UshankaHatModel ushankaHat = new UshankaHatModel(UshankaHatModel.createLayer().bakeRoot());
    private final TricornHatModel tricornHat = new TricornHatModel(TricornHatModel.createLayer().bakeRoot());
    private final NapoleonBicorneHatModel napoleonBicorne = new NapoleonBicorneHatModel(NapoleonBicorneHatModel.createLayer().bakeRoot());
    private final EgirlHeadphonesModel egirlHeadphones = new EgirlHeadphonesModel(EgirlHeadphonesModel.createLayer().bakeRoot());

    private final CrimsonScarfModel crimsonScarf = new CrimsonScarfModel(CrimsonScarfModel.createLayer().bakeRoot());
    private final GoldChainModel goldChain = new GoldChainModel(GoldChainModel.createLayer().bakeRoot());
    private final ShadowNinjaMaskModel shadowNinjaMask = new ShadowNinjaMaskModel(ShadowNinjaMaskModel.createLayer().bakeRoot());
    private final OniMenpoModel oniMenpo = new OniMenpoModel(OniMenpoModel.createLayer().bakeRoot());
    private final CoolSunglassesModel coolSunglasses = new CoolSunglassesModel(CoolSunglassesModel.createLayer().bakeRoot());
    private final SnowScarfModel snowScarf = new SnowScarfModel(SnowScarfModel.createLayer().bakeRoot());
    private final RubyChokerModel rubyChoker = new RubyChokerModel(RubyChokerModel.createLayer().bakeRoot());

    private final FighterWrapsModel fighterWraps = new FighterWrapsModel(FighterWrapsModel.createLayer().bakeRoot());
    private final SylasChainsModel sylasChains = new SylasChainsModel(SylasChainsModel.createLayer().bakeRoot());
    private final CombatGlovesModel combatGloves = new CombatGlovesModel(CombatGlovesModel.createLayer().bakeRoot());

    private final KatanaSashModel katanaSash = new KatanaSashModel(KatanaSashModel.createLayer().bakeRoot());
    private final KitsuneTailModel kitsuneTail = new KitsuneTailModel(KitsuneTailModel.createLayer().bakeRoot());
    private final DemonTailModel demonTail = new DemonTailModel(DemonTailModel.createLayer().bakeRoot());
    private final TwinDaggersModel twinDaggers = new TwinDaggersModel(TwinDaggersModel.createLayer().bakeRoot());
    private final CatTailModel catTail = new CatTailModel(CatTailModel.createLayer().bakeRoot());
    private final WolfTailModel wolfTail = new WolfTailModel(WolfTailModel.createLayer().bakeRoot());

    private final HermesBootsModel hermesBoots = new HermesBootsModel(HermesBootsModel.createLayer().bakeRoot());
    private final ShinobiLegWrapsModel shinobiLegWraps = new ShinobiLegWrapsModel(ShinobiLegWrapsModel.createLayer().bakeRoot());
    private final FighterLegWrapsModel fighterLegWraps = new FighterLegWrapsModel(FighterLegWrapsModel.createLayer().bakeRoot());
    private final BareFeetModel bareFeet = new BareFeetModel(BareFeetModel.createLayer().bakeRoot());
    private final CombatBootsModel combatBoots = new CombatBootsModel(CombatBootsModel.createLayer().bakeRoot());
    private final ClassicSneakersModel classicSneakers = new ClassicSneakersModel(ClassicSneakersModel.createLayer().bakeRoot());

    // Separate right/left states so deferred submitModel calls never observe mutated isLeftPart
    private final CosmeticRenderState cosmeticState = new CosmeticRenderState();
    private final CosmeticRenderState leftCosmeticState = new CosmeticRenderState();

    public GeneralCosmeticsLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
        super(parent);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light,
                       AvatarRenderState state, float yRot, float xRot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || state.id != mc.player.getId()) return;
        if (state.isInvisible || state.isSpectator) return;

        WingType wing = WingType.fromName(KiemConfig.cosmeticWing);
        HatType hat = HatType.fromName(KiemConfig.cosmeticHat);
        NeckType neck = NeckType.fromName(KiemConfig.cosmeticNeck);
        HandType hand = HandType.fromName(KiemConfig.cosmeticHand);
        WaistType waist = WaistType.fromName(KiemConfig.cosmeticWaist);
        FootType foot = FootType.fromName(KiemConfig.cosmeticFoot);

        if (wing == WingType.OFF && hat == HatType.OFF && neck == NeckType.OFF
                && hand == HandType.OFF && waist == WaistType.OFF && foot == FootType.OFF) {
            return;
        }

        updateState(cosmeticState, state, mc, false);
        updateState(leftCosmeticState, state, mc, true);

        PlayerModel parentModel = getParentModel();

        if (wing != WingType.OFF) {
            switch (wing) {
                case SERAPHIM -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, seraphimAngelWings, cosmeticState, RenderTypes.entityCutout(TEX_WING_SERAPHIM_ANGEL), "WING");
                case VALKYRIE -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, seraphimWings, cosmeticState, RenderTypes.entityCutout(TEX_WING_SERAPHIM), "WING");
                default -> { }
            }
        }

        if (hat != HatType.OFF) {
            switch (hat) {
                case RONIN -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, roninHat, cosmeticState, RenderTypes.entityCutout(TEX_HAT_RONIN), "HAT");
                case SHINOBI_HEADBAND -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, shinobiHeadband, cosmeticState, RenderTypes.entityCutout(TEX_HAT_SHINOBI), "HAT");
                case WITCH -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, witchHat, cosmeticState, RenderTypes.entityCutout(TEX_HAT_WITCH), "HAT");
                case KITSUNE_EARS -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, kitsuneEars, cosmeticState, RenderTypes.entityCutout(TEX_HAT_KITSUNE), "HAT");
                case CELESTIAL_HALO -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, celestialHalo, cosmeticState, RenderTypes.entityCutout(TEX_HAT_CELESTIAL_HALO), "HAT");
                case ONI_HORNS -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, oniHorns, cosmeticState, RenderTypes.entityCutout(TEX_HAT_ONI_HORNS), "HAT");
                case KITSUNE_MASK -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, kitsuneMask, cosmeticState, RenderTypes.entityCutout(TEX_HAT_KITSUNE_MASK), "HAT");
                case BROKEN_HALO -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, brokenHalo, cosmeticState, RenderTypes.entityCutout(TEX_HAT_BROKEN_HALO), "HAT");
                case CROWN -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, royalCrown, cosmeticState, RenderTypes.entityCutout(TEX_HAT_CROWN), "HAT");
                case STRAW_PIRATE -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, strawPirateHat, cosmeticState, RenderTypes.entityCutout(TEX_HAT_STRAW_PIRATE), "HAT");
                case USHANKA_FUR -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, ushankaHat, cosmeticState, RenderTypes.entityCutout(TEX_HAT_USHANKA), "HAT");
                case TRICORN_CAPTAIN -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, tricornHat, cosmeticState, RenderTypes.entityCutout(TEX_HAT_TRICORN), "HAT");
                case NAPOLEON_BICORNE -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, napoleonBicorne, cosmeticState, RenderTypes.entityCutout(TEX_HAT_BICORNE), "HAT");
                case EGIRL_HEADPHONES -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, egirlHeadphones, cosmeticState, RenderTypes.entityCutout(TEX_HAT_EGIRL_HEADPHONES), "HAT");
                default -> { }
            }
        }

        if (neck != NeckType.OFF) {
            switch (neck) {
                case CRIMSON_SCARF -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, crimsonScarf, cosmeticState, RenderTypes.entityCutout(TEX_NECK_SCARF), "NECK");
                case GOLD_CHAIN -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, goldChain, cosmeticState, RenderTypes.entityCutout(TEX_NECK_CHAIN), "NECK");
                case SHADOW_NINJA_MASK -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, shadowNinjaMask, cosmeticState, RenderTypes.entityCutout(TEX_NECK_NINJA_MASK), "NECK");
                case ONI_MENPO -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, oniMenpo, cosmeticState, RenderTypes.entityCutout(TEX_NECK_ONI_MENPO), "NECK");
                case COOL_SUNGLASSES -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.head, coolSunglasses, cosmeticState, RenderTypes.entityTranslucent(TEX_NECK_SUNGLASSES), "NECK");
                case SNOW_SCARF -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, snowScarf, cosmeticState, RenderTypes.entityCutout(TEX_NECK_SNOW_SCARF), "NECK");
                case RUBY_CHOKER -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, rubyChoker, cosmeticState, RenderTypes.entityCutout(TEX_NECK_RUBY_CHOKER), "NECK");
                default -> { }
            }
        }

        if (waist != WaistType.OFF) {
            switch (waist) {
                case KATANA_SASH -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, katanaSash, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_KATANA), "WAIST");
                case KITSUNE_TAIL -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, kitsuneTail, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_TAIL), "WAIST");
                case DEMON_TAIL -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, demonTail, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_DEMON_TAIL), "WAIST");
                case TWIN_DAGGERS -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, twinDaggers, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_TWIN_DAGGERS), "WAIST");
                case CAT_TAIL -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, catTail, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_CAT_TAIL), "WAIST");
                case WOLF_TAIL -> submitOnBone(poseStack, collector, light, state.outlineColor,
                        parentModel.body, wolfTail, cosmeticState, RenderTypes.entityCutout(TEX_WAIST_WOLF_TAIL), "WAIST");
                default -> { }
            }
        }

        if (hand != HandType.OFF) {
            switch (hand) {
                case FIGHTER_WRAPS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightArm, parentModel.leftArm, fighterWraps, TEX_HAND_WRAPS, "HAND");
                case SYLAS_CHAINS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightArm, parentModel.leftArm, sylasChains, TEX_HAND_CHAINS, "HAND");
                case COMBAT_GLOVES -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightArm, parentModel.leftArm, combatGloves, TEX_HAND_COMBAT_GLOVES, "HAND");
                default -> { }
            }
        }

        if (foot != FootType.OFF) {
            switch (foot) {
                case HERMES_BOOTS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, hermesBoots, TEX_FOOT_HERMES, "FOOT");
                case SHINOBI_WRAPS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, shinobiLegWraps, TEX_FOOT_SHINOBI, "FOOT");
                case FIGHTER_LEG_WRAPS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, fighterLegWraps, TEX_FOOT_FIGHTER_WRAPS, "FOOT");
                case BARE_FEET -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, bareFeet, TEX_FOOT_BARE, "FOOT");
                case COMBAT_BOOTS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, combatBoots, TEX_FOOT_COMBAT_BOOTS, "FOOT");
                case CLASSIC_SNEAKERS -> submitPaired(poseStack, collector, light, state.outlineColor,
                        parentModel.rightLeg, parentModel.leftLeg, classicSneakers, TEX_FOOT_SNEAKERS, "FOOT");
                default -> { }
            }
        }
    }

    private void submitPaired(PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor,
                              ModelPart rightBone, ModelPart leftBone, CosmeticModel model, Identifier texture, String slotKey) {
        RenderType rt = RenderTypes.entityCutout(texture);
        submitOnBone(poseStack, collector, light, outlineColor, rightBone, model, cosmeticState, rt, slotKey);
        submitOnBone(poseStack, collector, light, outlineColor, leftBone, model, leftCosmeticState, rt, slotKey);
    }

    private static void updateState(CosmeticRenderState target, AvatarRenderState state, Minecraft mc, boolean isLeftPart) {
        target.ageInTicks = state.ageInTicks;
        target.walkPos = state.walkAnimationPos;
        target.walkSpeed = state.walkAnimationSpeed;
        target.crouchAmount = state.isCrouching ? 1.0f : 0.0f;
        target.flyAmount = (mc.player.isFallFlying() || mc.player.getAbilities().flying) ? 1.0f : 0.0f;
        target.swimAmount = state.swimAmount;
        target.hurtTime = (float) mc.player.hurtTime;
        target.attackSwing = KiemCompat.getAttackAnim(mc.player, 0f);
        target.isSlimArms = state.skin != null && state.skin.model() == PlayerModelType.SLIM;
        target.isLeftPart = isLeftPart;
        target.headYRot = (float) Math.toRadians(state.yRot);
        target.headXRot = (float) Math.toRadians(state.xRot);
    }

    private static void submitOnBone(PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor,
                                     ModelPart bone, CosmeticModel model, CosmeticRenderState renderState, RenderType renderType) {
        submitOnBone(poseStack, collector, light, outlineColor, bone, model, renderState, renderType, null);
    }

    private static void submitOnBone(PoseStack poseStack, SubmitNodeCollector collector, int light, int outlineColor,
                                     ModelPart bone, CosmeticModel model, CosmeticRenderState renderState, RenderType renderType,
                                     String slotKey) {
        poseStack.pushPose();
        bone.translateAndRotate(poseStack);
        if (slotKey != null) {
            float offY = KiemConfig.getCosmeticOffsetY(slotKey);
            float offZ = KiemConfig.getCosmeticOffsetZ(slotKey);
            float sc = KiemConfig.getCosmeticScale(slotKey);
            if (offY != 0.0f || offZ != 0.0f) {
                poseStack.translate(0.0f, -offY / 16.0f, -offZ / 16.0f);
            }
            if (sc != 1.0f) {
                poseStack.scale(sc, sc, sc);
            }
        }
        KiemCompat.submitModel(collector, model, renderState, poseStack, renderType,
                light, OverlayTexture.NO_OVERLAY, outlineColor);
        poseStack.popPose();
    }
}
