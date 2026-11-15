package com.example.client;

import com.example.client.cosmetic.CosmeticCatalog.FootType;
import com.example.client.cosmetic.CosmeticCatalog.HandType;
import com.example.client.cosmetic.CosmeticCatalog.HatType;
import com.example.client.cosmetic.CosmeticCatalog.NeckType;
import com.example.client.cosmetic.CosmeticCatalog.TrailEffectType;
import com.example.client.cosmetic.CosmeticCatalog.WaistType;
import com.example.client.cosmetic.CosmeticCatalog.WingType;
import com.example.client.pet.ShoulderPetManager;
import com.example.client.pet.ShoulderPetType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KiemScreen extends Screen {

    public static final boolean ENABLE_DEV_MENU = true;

    private static final Identifier SANARA_V1_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara.png");
    private static final Identifier SANARA_V2_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara_v2.png");
    private static final Identifier SANARA_V3_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara_v3.png");
    private static final Identifier RIKKA_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_rikka.png");
    private static final Identifier RIKKA_MASCOT_HEAD =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/rikka_head.png");
    private static final Identifier RIKKA_MASCOT_BODY =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/rikka_body.png");
    private static final Identifier RIKKA_MASCOT_LEG_L =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/rikka_leg_l.png");
    private static final Identifier RIKKA_MASCOT_LEG_R =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/rikka_leg_r.png");
    private static final Identifier HAMSTER_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_hamster.png");
    private static final Identifier HAMSTER_HD_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_hamster_hd.png");
    private static final Identifier OCTOPUS_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_octopus.png");
    private static final Identifier DRAGON_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_dragon.png");
    private static final Identifier INVENTORY_CURSE_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_inventory_curse.png");

    private static final Identifier SAKURA_AURA_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_aura_sakura.png");
    private static final Identifier GARGOYLE_AURA_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_aura_gargoyle.png");

    private enum CosmeticsSub {
        CAPES("cosmetics.sub.capes"),
        WINGS("cosmetics.sub.wings"),
        HATS("cosmetics.sub.hats"),
        NECK("cosmetics.sub.neck"),
        HAND("cosmetics.sub.hand"),
        WAIST("cosmetics.sub.waist"),
        FOOT("cosmetics.sub.foot"),
        PETS("cosmetics.sub.pets"),
        TRAILS("cosmetics.sub.trails");

        final String langKey;

        CosmeticsSub(String langKey) {
            this.langKey = langKey;
        }
    }

    private record CosmeticItemDef(String id, String titleKey, String descKey, String tag, int accentColor, Identifier preview) {}

    private enum Tab {
        WELCOME("WELCOME"),
        CONFIG("FEATURES"),
        COSMETICS("WARDROBE"),
        DEV("THEMES");

        private final String label;

        Tab(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private static Tab[] getVisibleTabs() {
        return ENABLE_DEV_MENU
                ? Tab.values()
                : new Tab[] { Tab.WELCOME, Tab.CONFIG, Tab.COSMETICS };
    }

    private enum ConfigSubView {
        MAIN,
        MINI_MODELS,
        CUSTOM_VIEWMODEL,
        BETTER_THIRD_PERSON,
        AFK_CINEMATICS,
        SMOOTH_ZOOM,
        CHAT_QOL,
        TOOLTIP_QOL,
        ANTI_AFK
    }

    private Tab currentTab = Tab.WELCOME;
    private CosmeticsSub currentCosmeticsSub = CosmeticsSub.CAPES;
    private ConfigSubView configSubView = ConfigSubView.MAIN;
    private boolean listeningForKey = false;
    private boolean listeningForZoomKey = false;
    private boolean listeningForMenuKey = false;
    private boolean listeningForShoulderKey = false;
    private int selectedSanaraVersion = 3;
    private double cosmeticsSlotScroll = 0.0;
    private double cosmeticsCapesScroll = 0.0;
    private double cosmeticsPetsScroll = 0.0;
    private double cosmeticsTrailScroll = 0.0;
    private double configScroll = 0.0;
    private float cosmeticPreviewYaw = 0.0f;
    private float cosmeticPreviewPitch = 0.0f;
    private float cosmeticPreviewScale = 1.0f;
    private boolean draggingCosmeticPreview = false;
    private boolean cosmeticCustomizeOpen = false;
    private boolean draggingMascot = false;
    private float mascotDragOffsetX = 0.0f;
    private float mascotDragOffsetY = 0.0f;
    private static final int MASCOT_SIZE = 88;
    private boolean showResetConfirmModal = false;
    private boolean pendingResetWardrobe = false;
    private boolean showLanguageModal = !KiemConfig.hasSelectedLanguage;

    // Interactive slider dragging state
    private String draggingSlider = null;
    private int sliderTrackX = 0;
    private int sliderTrackW = 0;
    private float sliderMin = 0f;
    private float sliderMax = 0f;

    // Layout constants
    private static final int MARGIN_X = 18;
    private static final int MARGIN_Y = 14;
    private static final int TOP_H = 36;
    private static final int BOTTOM_H = 38;
    private static final int SECTION_GAP = 10;

    private static final int BOUTIQUE_COLS = 3;
    private static final int BOUTIQUE_GAP = 8;
    private static final int BOUTIQUE_CARD_H = 112;
    private static final int BOUTIQUE_SCROLLBAR_W = 6;

    public KiemScreen() {
        super(Component.literal("Kiem"));
    }

    @Override
    protected void init() {
        super.init();
        CapeManager.CapeType c = CapeManager.getCurrentCape();
        if (c == CapeManager.CapeType.SANARA_STATIC || c == CapeManager.CapeType.SANARA_ANIMATED) {
            selectedSanaraVersion = 1;
        } else if (c == CapeManager.CapeType.SANARA_V2_STATIC || c == CapeManager.CapeType.SANARA_V2_ANIMATED) {
            selectedSanaraVersion = 2;
        } else if (c == CapeManager.CapeType.SANARA_V3_STATIC || c == CapeManager.CapeType.SANARA_V3_ANIMATED) {
            selectedSanaraVersion = 3;
        }
        CustomCapeManager.scanAndLoad();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // --- Layout Geometry Helpers ---

    private int getMw() {
        return KiemConfig.uiFullscreen
                ? Math.max(460, this.width - 36)
                : Math.min(Math.max(460, this.width - 40), 700);
    }

    private int getMh() {
        return KiemConfig.uiFullscreen
                ? Math.max(260, this.height - 28)
                : Math.min(Math.max(260, this.height - 36), 380);
    }

    private int getMx() {
        return KiemConfig.uiFullscreen ? 18 : (this.width - getMw()) / 2;
    }

    private int getMy() {
        return KiemConfig.uiFullscreen ? 14 : (this.height - getMh()) / 2;
    }

    private int getTopY() { return getMy(); }
    private int getBottomY() { return getMy() + getMh() - BOTTOM_H; }
    private int getContentY() { return getTopY() + TOP_H + SECTION_GAP; }
    private int getContentH() { return getMh() - TOP_H - BOTTOM_H - 20; }

    private int getGalleryW() { return (int) (getMw() * 0.56); }
    private int getStageX() { return getMx() + getGalleryW() + 8; }
    private int getStageW() { return (int) (getMw() * 0.31); }
    private int getStripX() { return getStageX() + getStageW() + 8; }
    private int getStripW() { return getMw() - getGalleryW() - getStageW() - 16; }

    private int[] computeTopNavPillWidths() {
        Tab[] tabs = getVisibleTabs();
        int[] widths = new int[tabs.length];
        for (int i = 0; i < tabs.length; i++) {
            widths[i] = Math.max(54, BoutiqueDraw.textWidth(this.font, tabs[i].getLabel()) + 22);
        }
        return widths;
    }

    private int[] computeBottomRightLayout() {
        // Returns [closeX, gearX, langX, langW, playerX, playerW]
        int mx = getMx();
        int mw = getMw();
        int rightEdge = mx + mw - 10;

        int circleSize = 26;
        int gap = 6;
        int closeX = rightEdge - circleSize;
        int gearX = closeX - gap - circleSize;

        int langW = 46;
        int langX = gearX - gap - langW;

        String playerName = getPlayerDisplayName();
        int playerW = Math.min(118, Math.max(60, BoutiqueDraw.textWidth(this.font, playerName) + 24));
        int playerX = langX - gap - playerW;

        return new int[] { closeX, gearX, langX, langW, playerX, playerW };
    }

    private int computeBottomSlotsStartX(int playerPillX) {
        int mx = getMx();
        int totalSlotsW = 5 * 28 + 4 * 6; // 164
        String leftLabel = "EQUIPPED COSMETIC SLOT BAR";
        int leftEnd = mx + 14 + Math.min(185, BoutiqueDraw.textWidth(this.font, leftLabel)) + 10;
        int idealStart = mx + (getMw() - totalSlotsW) / 2;
        int maxStart = playerPillX - 10 - totalSlotsW;
        return Math.max(leftEnd, Math.min(idealStart, maxStart));
    }

    private String getPlayerDisplayName() {
        if (this.minecraft != null && this.minecraft.player != null) {
            return this.minecraft.player.getName().getString();
        }
        return "Player";
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // 1. Backdrop
        BoutiqueDraw.drawBackdrop(graphics, this.width, this.height, System.currentTimeMillis());

        boolean hasModal = showResetConfirmModal || showLanguageModal;
        int bgMouseX = hasModal ? -9999 : mouseX;
        int bgMouseY = hasModal ? -9999 : mouseY;

        int mx = getMx();
        int my = getMy();
        int mw = getMw();
        int mh = getMh();
        int topY = getTopY();
        int bottomY = getBottomY();
        int contentY = getContentY();
        int contentH = getContentH();

        // Centered floating boutique shell (only in modal mode)
        if (!KiemConfig.uiFullscreen) {
            BoutiqueDraw.drawGlassPanel(graphics, mx - 10, my - 10, mw + 20, mh + 20);
        }

        // 2. Top Header
        renderTopHeader(graphics, mx, topY, mw, TOP_H, bgMouseX, bgMouseY);

        // 3. Middle Content Area
        if (currentTab == Tab.WELCOME) {
            renderWelcomeTab(graphics, mx, contentY, mw, contentH, bgMouseX, bgMouseY);
        } else if (currentTab == Tab.COSMETICS) {
            renderCosmeticsTab(graphics, mx, contentY, mw, contentH, bgMouseX, bgMouseY);
        } else if (ENABLE_DEV_MENU && currentTab == Tab.DEV) {
            renderDevTab(graphics, mx, contentY, mw, contentH, bgMouseX, bgMouseY);
        } else {
            renderConfigTab(graphics, mx, contentY, mw, contentH, bgMouseX, bgMouseY);
        }

        // 4. Bottom Slot & Status Bar
        renderBottomBar(graphics, mx, bottomY, mw, BOTTOM_H, bgMouseX, bgMouseY);

        // 4.5 Bottom-Right Animated Rikka Mascot
        if (KiemConfig.uiShowMascot) {
            renderRikkaMascot(graphics, mouseX, mouseY);
        }

        // 5. Reset Confirmation Modal Overlay
        if (showResetConfirmModal) {
            renderResetConfirmModal(graphics, mouseX, mouseY);
        }

        // 6. Language Selection Modal Overlay
        if (showLanguageModal) {
            renderLanguageSelectModal(graphics, mouseX, mouseY);
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void renderTopHeader(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.Theme theme = BoutiqueDraw.theme();
        // Left title: KIEM ADDONS | <Theme Display Name>
        int textY = y + (h - 8) / 2;
        String brand = "KIEM ADDONS";
        String sep = " | ";
        String sub = theme.displayName + " Boutique";

        int cx = x + 4;
        BoutiqueDraw.drawText(graphics, this.font, brand, cx, textY, theme.textPrimary);
        cx += BoutiqueDraw.textWidth(this.font, brand);
        BoutiqueDraw.drawText(graphics, this.font, sep, cx, textY, theme.textDim);
        cx += BoutiqueDraw.textWidth(this.font, sep);
        BoutiqueDraw.drawText(graphics, this.font, sub, cx, textY, theme.textAccent);

        // Right pill bar with WELCOME, FEATURES, WARDROBE (and DEV if enabled)
        Tab[] tabs = getVisibleTabs();
        int[] pillWidths = computeTopNavPillWidths();
        int pillGap = 6;
        int pillH = 22;
        int pad = 4;
        int totalPillsW = 0;
        for (int pw : pillWidths) totalPillsW += pw;
        totalPillsW += (tabs.length - 1) * pillGap;

        int barW = totalPillsW + pad * 2;
        int barH = pillH + pad * 2;
        int barX = x + w - barW;
        int barY = y + (h - barH) / 2;

        BoutiqueDraw.drawPillBar(graphics, barX, barY, barW, barH);

        int px = barX + pad;
        int py = barY + pad;
        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            int pw = pillWidths[i];
            boolean active = (currentTab == t);
            boolean hover = mouseX >= px && mouseX <= px + pw && mouseY >= py && mouseY <= py + pillH;
            BoutiqueDraw.drawPill(graphics, px, py, pw, pillH, active, hover);
            int txtColor = active ? theme.textOnActivePill : (hover ? theme.textPrimary : theme.textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, t.getLabel(), px + pw / 2, py + 7, txtColor);
            if (t == Tab.WELCOME && KiemConfig.updateCheckerEnabled && UpdateChecker.hasUpdate()) {
                graphics.fill(px + pw - 6, py + 2, px + pw - 2, py + 6, theme.textPositive);
            }
            px += pw + pillGap;
        }
    }

    private void renderBottomBar(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.drawGlassPanel(graphics, x, y, w, h);

        int[] rightLayout = computeBottomRightLayout();
        int closeX = rightLayout[0];
        int gearX = rightLayout[1];
        int langX = rightLayout[2];
        int langW = rightLayout[3];
        int playerX = rightLayout[4];
        int playerW = rightLayout[5];

        // Left label
        int slotsStartX = computeBottomSlotsStartX(playerX);
        int maxLabelW = Math.max(40, slotsStartX - (x + 14) - 8);
        drawBoundedText(graphics, "EQUIPPED COSMETIC SLOT BAR", x + 14, y + (h - 8) / 2, maxLabelW, BoutiqueDraw.theme().textMuted);

        // Center 5 circular slots
        int slotSize = 28;
        int slotGap = 6;
        int slotY = y + (h - slotSize) / 2;

        boolean capeEquipped = CapeManager.getCurrentCape() != CapeManager.CapeType.OFF;
        boolean wingEquipped = !"OFF".equalsIgnoreCase(KiemConfig.cosmeticWing);
        boolean hatEquipped = !"OFF".equalsIgnoreCase(KiemConfig.cosmeticHat);
        boolean accEquipped = !"OFF".equalsIgnoreCase(KiemConfig.cosmeticNeck)
                || !"OFF".equalsIgnoreCase(KiemConfig.cosmeticHand)
                || !"OFF".equalsIgnoreCase(KiemConfig.cosmeticWaist)
                || !"OFF".equalsIgnoreCase(KiemConfig.cosmeticFoot);
        boolean petTrailEquipped = ShoulderPetManager.getSelectedPet() != ShoulderPetType.OFF
                || KiemConfig.sakuraAuraEnabled
                || !"OFF".equalsIgnoreCase(KiemConfig.cosmeticTrailEffect);

        boolean[] slotEquipped = { capeEquipped, wingEquipped, hatEquipped, accEquipped, petTrailEquipped };
        String[] slotGlyphs = { "C", "W", "H", "A", "P" };
        boolean[] slotSelected = {
                currentTab == Tab.COSMETICS && currentCosmeticsSub == CosmeticsSub.CAPES,
                currentTab == Tab.COSMETICS && currentCosmeticsSub == CosmeticsSub.WINGS,
                currentTab == Tab.COSMETICS && currentCosmeticsSub == CosmeticsSub.HATS,
                currentTab == Tab.COSMETICS && (currentCosmeticsSub == CosmeticsSub.NECK
                        || currentCosmeticsSub == CosmeticsSub.HAND
                        || currentCosmeticsSub == CosmeticsSub.WAIST
                        || currentCosmeticsSub == CosmeticsSub.FOOT),
                currentTab == Tab.COSMETICS && (currentCosmeticsSub == CosmeticsSub.PETS
                        || currentCosmeticsSub == CosmeticsSub.TRAILS)
        };

        for (int i = 0; i < 5; i++) {
            int sx = slotsStartX + i * (slotSize + slotGap);
            boolean hover = mouseX >= sx && mouseX <= sx + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize;
            boolean active = slotEquipped[i] || slotSelected[i];
            BoutiqueDraw.drawCircleSlot(graphics, sx, slotY, slotSize, active, hover);
            int glyphColor = slotEquipped[i] ? BoutiqueDraw.theme().textAccent : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, slotGlyphs[i], sx + slotSize / 2, slotY + 10, glyphColor);
            if (slotEquipped[i]) {
                // Small active sakura dot at bottom-right of slot
                graphics.fill(sx + slotSize - 7, slotY + slotSize - 7, sx + slotSize - 3, slotY + slotSize - 3, BoutiqueDraw.theme().textAccent);
            }
        }

        // Right: Player Name Pill
        int pillH = 22;
        int pillY = y + (h - pillH) / 2;
        BoutiqueDraw.drawPill(graphics, playerX, pillY, playerW, pillH, false, false);
        int dotX = playerX + 8;
        int dotY = pillY + (pillH - 5) / 2;
        graphics.fill(dotX, dotY, dotX + 5, dotY + 5, BoutiqueDraw.theme().textPositive);
        drawBoundedText(graphics, getPlayerDisplayName(), dotX + 9, pillY + 7, playerW - 22, BoutiqueDraw.theme().textPrimary);

        // Right: Language Flag Pill
        boolean langHover = mouseX >= langX && mouseX <= langX + langW && mouseY >= pillY && mouseY <= pillY + pillH;
        BoutiqueDraw.drawPill(graphics, langX, pillY, langW, pillH, false, langHover);
        if ("en".equalsIgnoreCase(KiemConfig.language)) {
            drawFlagUK(graphics, langX + 7, pillY + 6);
            BoutiqueDraw.drawText(graphics, this.font, "EN", langX + 26, pillY + 7, langHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        } else {
            drawFlagTR(graphics, langX + 7, pillY + 6);
            BoutiqueDraw.drawText(graphics, this.font, "TR", langX + 26, pillY + 7, langHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        }

        // Right: Circular Settings / Reset button (⚙)
        int circleSize = 26;
        int circleY = y + (h - circleSize) / 2;
        boolean gearHover = mouseX >= gearX && mouseX <= gearX + circleSize && mouseY >= circleY && mouseY <= circleY + circleSize;
        BoutiqueDraw.drawCircleSlot(graphics, gearX, circleY, circleSize, false, gearHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "⚙", gearX + circleSize / 2, circleY + 9, gearHover ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);

        // Right: Circular Close button (✕)
        boolean closeHover = mouseX >= closeX && mouseX <= closeX + circleSize && mouseY >= circleY && mouseY <= circleY + circleSize;
        BoutiqueDraw.drawCircleSlot(graphics, closeX, circleY, circleSize, false, closeHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "✕", closeX + circleSize / 2, circleY + 9, closeHover ? 0xFFFF8899 : BoutiqueDraw.theme().textMuted);
    }

    // ==================== COSMETICS (WARDROBE) TAB ====================

    private void renderCosmeticsTab(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        int galleryW = getGalleryW();
        int stageX = getStageX();
        int stageW = getStageW();
        int stripX = getStripX();
        int stripW = getStripW();

        // 1. Left/Center Main Gallery Panel
        BoutiqueDraw.drawGlassPanel(graphics, x, y, galleryW, h);
        BoutiqueDraw.drawText(graphics, this.font, "COSMETIC GALLERY", x + 14, y + 11, BoutiqueDraw.theme().textPrimary);

        // 9 Category Pills Strip
        CosmeticsSub[] subs = CosmeticsSub.values();
        int stripPadX = x + 12;
        int stripAvailW = galleryW - 24;
        int catGap = 3;
        int catW = (stripAvailW - (subs.length - 1) * catGap) / subs.length;
        int catH = 18;
        int catY = y + 26;

        for (int i = 0; i < subs.length; i++) {
            CosmeticsSub sub = subs[i];
            int bx = stripPadX + i * (catW + catGap);
            boolean active = (currentCosmeticsSub == sub);
            boolean hover = mouseX >= bx && mouseX <= bx + catW && mouseY >= catY && mouseY <= catY + catH;
            BoutiqueDraw.drawPill(graphics, bx, catY, catW, catH, active, hover);
            String label = BoutiqueDraw.fitText(this.font, KiemLang.get(sub.langKey), Math.max(8, catW - 6));
            int txtColor = active ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, label, bx + catW / 2, catY + 5, txtColor);
        }

        // Gallery viewport & footer
        int vpX = x + 12;
        int vpY = catY + catH + 8;
        int vpW = galleryW - 24;
        int footerH = 34;
        int vpH = (y + h - 10) - vpY - footerH - 6;

        if (currentCosmeticsSub == CosmeticsSub.CAPES) {
            renderCapesSubView(graphics, vpX, vpY, vpW, vpH, footerH, mouseX, mouseY);
        } else if (currentCosmeticsSub == CosmeticsSub.PETS) {
            renderPetsSubView(graphics, vpX, vpY, vpW, vpH, footerH, mouseX, mouseY);
        } else if (currentCosmeticsSub == CosmeticsSub.TRAILS) {
            renderTrailsSubView(graphics, vpX, vpY, vpW, vpH, footerH, mouseX, mouseY);
        } else {
            renderCosmeticSlotSubView(graphics, vpX, vpY, vpW, vpH, footerH, mouseX, mouseY);
        }

        // 2. Right Open 3D Character Stage
        renderCosmeticsPlayerPreview(graphics, stageX, y, stageW, h, mouseX, mouseY);

        // 3. Far-Right Slim EQUIPPED ITEMS Strip
        renderEquippedItemsStrip(graphics, stripX, y, stripW, h, mouseX, mouseY);
    }

    private void renderCosmeticsPlayerPreview(GuiGraphicsExtractor graphics, int px, int py, int pw, int ph, int mouseX, int mouseY) {
        int stageCx = px + pw / 2;
        int stageCy = py + (ph - 26) / 2;
        int glowRadius = Math.max(64, Math.min(pw, ph) * 2 / 3);
        BoutiqueDraw.drawRadialGlow(graphics, stageCx, stageCy, glowRadius);

        int x0 = px + 4;
        int y0 = py + 8;
        int x1 = px + pw - 4;
        int y1 = py + ph - 34;

        if (this.minecraft != null && this.minecraft.player != null) {
            var renderer = this.minecraft.getEntityRenderDispatcher().getRenderer(this.minecraft.player);
            var fresh = renderer.createRenderState(this.minecraft.player, 1.0f);
            if (fresh instanceof AvatarRenderState avatar) {
                avatar.x = avatar.y = avatar.z = 0;
                avatar.passengerOffset = null;
                avatar.shadowPieces.clear();
                avatar.outlineColor = 0;
                avatar.bodyRot = 180;
                avatar.yRot = 0;
                avatar.xRot = 0;

                float pitchRadians = (float) Math.toRadians(cosmeticPreviewPitch);
                float yawRadians = (float) Math.toRadians(cosmeticPreviewYaw);
                Quaternionf view = new Quaternionf().rotationX(pitchRadians).rotateY(yawRadians);
                Quaternionf rotation = new Quaternionf().rotationZ((float) Math.PI).mul(view);
                Vector3f translation = rotation.transform(new Vector3f(0.0F, -avatar.boundingBoxHeight * 0.5F, 0.0F));
                int baseScale = Math.max(42, (int) (ph * 0.34f));
                float scale = baseScale * cosmeticPreviewScale;
                graphics.entity(avatar, scale, translation, rotation, view, x0, y0, x1, y1);
            }
        } else {
            BoutiqueDraw.drawCenteredText(graphics, this.font, "3D", px + pw / 2, py + ph / 2 - 4, BoutiqueDraw.theme().textDim);
        }

        // Floating Reset View, Reset Wardrobe & Customize pills at bottom of stage
        int gap = 4;
        int totalW = pw - 12;
        int resetW = (totalW - 2 * gap) / 3;
        int resetWardrobeW = (totalW - 2 * gap) / 3;
        int custW = totalW - 2 * gap - resetW - resetWardrobeW;
        int btnH = 20;
        int resetX = px + 6;
        int resetWardrobeX = resetX + resetW + gap;
        int custX = resetWardrobeX + resetWardrobeW + gap;
        int btnY = py + ph - 26;

        boolean resetHover = mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= btnY && mouseY <= btnY + btnH;
        BoutiqueDraw.drawPill(graphics, resetX, btnY, resetW, btnH, false, resetHover);
        String resetLabel = BoutiqueDraw.fitText(this.font, KiemLang.get("auras.custom.reset_view"), resetW - 6);
        BoutiqueDraw.drawCenteredText(graphics, this.font, resetLabel, resetX + resetW / 2, btnY + 6,
                resetHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

        boolean resetWardrobeHover = mouseX >= resetWardrobeX && mouseX <= resetWardrobeX + resetWardrobeW
                && mouseY >= btnY && mouseY <= btnY + btnH;
        BoutiqueDraw.drawPill(graphics, resetWardrobeX, btnY, resetWardrobeW, btnH, false, resetWardrobeHover);
        String resetWardrobeLabel = BoutiqueDraw.fitText(this.font, KiemLang.get("wardrobe.reset_btn"), resetWardrobeW - 6);
        BoutiqueDraw.drawCenteredText(graphics, this.font, resetWardrobeLabel, resetWardrobeX + resetWardrobeW / 2, btnY + 6,
                resetWardrobeHover ? 0xFFFF8899 : 0xFFFF9AA8);

        boolean custHover = mouseX >= custX && mouseX <= custX + custW && mouseY >= btnY && mouseY <= btnY + btnH;
        BoutiqueDraw.drawPill(graphics, custX, btnY, custW, btnH, cosmeticCustomizeOpen, custHover);
        String custLabel = BoutiqueDraw.fitText(this.font, "Customize", custW - 6);
        BoutiqueDraw.drawCenteredText(graphics, this.font, custLabel, custX + custW / 2, btnY + 6,
                cosmeticCustomizeOpen ? BoutiqueDraw.theme().textOnActivePill
                        : (custHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        if (cosmeticCustomizeOpen) {
            String slotKey = getActiveAdjustSlot();
            int popW = pw - 12;
            int popH = 92;
            int popX = px + 6;
            int popY = btnY - popH - 6;
            BoutiqueDraw.drawGlassPanel(graphics, popX, popY, popW, popH);

            BoutiqueDraw.drawText(graphics, this.font, "ADJUST: " + slotKey, popX + 8, popY + 7, BoutiqueDraw.theme().textAccent);
            int rstW = 38;
            int rstX = popX + popW - rstW - 6;
            int rstY = popY + 5;
            boolean rstHover = mouseX >= rstX && mouseX <= rstX + rstW && mouseY >= rstY && mouseY <= rstY + 14;
            BoutiqueDraw.drawPill(graphics, rstX, rstY, rstW, 14, false, rstHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, "Reset", rstX + rstW / 2, rstY + 3,
                    rstHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

            float offY = KiemConfig.getCosmeticOffsetY(slotKey);
            float offZ = KiemConfig.getCosmeticOffsetZ(slotKey);
            float sc = KiemConfig.getCosmeticScale(slotKey);

            drawAdjustRow(graphics, popX + 8, popY + 24, popW - 16, "Y (Up/Dn)", String.format(java.util.Locale.ROOT, "%+.2f", offY), mouseX, mouseY);
            drawAdjustRow(graphics, popX + 8, popY + 46, popW - 16, "Z (Fwd/Bk)", String.format(java.util.Locale.ROOT, "%+.2f", offZ), mouseX, mouseY);
            drawAdjustRow(graphics, popX + 8, popY + 68, popW - 16, "Scale", String.format(java.util.Locale.ROOT, "%.2fx", sc), mouseX, mouseY);
        }
    }

    private String getActiveAdjustSlot() {
        return switch (currentCosmeticsSub) {
            case WINGS -> "WING";
            case HATS -> "HAT";
            case NECK -> "NECK";
            case HAND -> "HAND";
            case WAIST -> "WAIST";
            case FOOT -> "FOOT";
            default -> "HAT";
        };
    }

    private void drawAdjustRow(GuiGraphicsExtractor graphics, int rx, int ry, int rw, String label, String valText, int mouseX, int mouseY) {
        BoutiqueDraw.drawText(graphics, this.font, BoutiqueDraw.fitText(this.font, label, rw - 78), rx, ry + 5, BoutiqueDraw.theme().textMuted);
        int btnS = 16;
        int plusX = rx + rw - btnS;
        int minusX = plusX - btnS - 36;
        boolean mHov = mouseX >= minusX && mouseX <= minusX + btnS && mouseY >= ry + 1 && mouseY <= ry + 1 + btnS;
        boolean pHov = mouseX >= plusX && mouseX <= plusX + btnS && mouseY >= ry + 1 && mouseY <= ry + 1 + btnS;
        BoutiqueDraw.drawPill(graphics, minusX, ry + 1, btnS, btnS, false, mHov);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "-", minusX + btnS / 2, ry + 5,
                mHov ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        BoutiqueDraw.drawCenteredText(graphics, this.font, valText, minusX + btnS + 18, ry + 5, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawPill(graphics, plusX, ry + 1, btnS, btnS, false, pHov);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "+", plusX + btnS / 2, ry + 5,
                pHov ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
    }

    private void renderEquippedItemsStrip(GuiGraphicsExtractor graphics, int sx, int sy, int sw, int sh, int mouseX, int mouseY) {
        BoutiqueDraw.drawGlassPanel(graphics, sx, sy, sw, sh);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "EQUIPPED", sx + sw / 2, sy + 10, BoutiqueDraw.theme().textPrimary);

        int padX = 7;
        int cardX = sx + padX;
        int cardW = sw - padX * 2;
        int startY = sy + 26;
        int gap = 6;
        int cardH = Math.max(32, (sh - 34 - 4 * gap) / 5);

        String capeVal = getEquippedCapeShortName();
        String wingVal = findSlotTitle(CosmeticsSub.WINGS, KiemConfig.cosmeticWing);
        String hatVal = findSlotTitle(CosmeticsSub.HATS, KiemConfig.cosmeticHat);
        ShoulderPetType pet = ShoulderPetManager.getSelectedPet();
        String petVal = (pet == ShoulderPetType.OFF) ? null : KiemLang.get(pet.titleKey);
        String auraVal = getEquippedAuraShortName();

        String[] labels = { "CAPE", "WING", "HAT", "PET", "AURA" };
        String[] values = { capeVal, wingVal, hatVal, petVal, auraVal };

        for (int i = 0; i < 5; i++) {
            int cy = startY + i * (cardH + gap);
            boolean equipped = values[i] != null;
            boolean hover = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cy && mouseY <= cy + cardH;
            BoutiqueDraw.drawGlassCard(graphics, cardX, cy, cardW, cardH, equipped, hover);

            drawBoundedText(graphics, labels[i], cardX + 7, cy + 6, cardW - 22, BoutiqueDraw.theme().textAccent);
            if (equipped) {
                BoutiqueDraw.drawText(graphics, this.font, "✦", cardX + cardW - 13, cy + 6, BoutiqueDraw.theme().textAccent);
            }
            String emptyLabel = "en".equalsIgnoreCase(KiemConfig.language) ? "Empty" : "Boş";
            String displayVal = equipped ? values[i] : emptyLabel;
            int valColor = equipped ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textDim;
            drawBoundedText(graphics, displayVal, cardX + 7, cy + cardH - 14, cardW - 14, valColor);
        }
    }

    private String getEquippedCapeShortName() {
        CapeManager.CapeType c = CapeManager.getCurrentCape();
        if (c == CapeManager.CapeType.OFF) return null;
        if (c == CapeManager.CapeType.CUSTOM) return CustomCapeManager.getSelectedCapeName();
        return c.getTitle();
    }

    private String getEquippedAuraShortName() {
        if (KiemConfig.sakuraAuraEnabled) return KiemLang.get("trails.sakura.title");
        if (!"OFF".equalsIgnoreCase(KiemConfig.cosmeticTrailEffect)) {
            for (CosmeticItemDef item : getTrailItems()) {
                if (item.id.equalsIgnoreCase(KiemConfig.cosmeticTrailEffect)) {
                    return KiemLang.get(item.titleKey);
                }
            }
            return KiemConfig.cosmeticTrailEffect;
        }
        return null;
    }

    private String findSlotTitle(CosmeticsSub sub, String equippedId) {
        if (equippedId == null || "OFF".equalsIgnoreCase(equippedId)) return null;
        for (CosmeticItemDef item : getSlotItems(sub)) {
            if (item.id.equalsIgnoreCase(equippedId)) {
                return KiemLang.get(item.titleKey);
            }
        }
        return null;
    }

    private static Identifier guiTex(String path) {
        return Identifier.fromNamespaceAndPath("modid", "textures/gui/" + path);
    }

    private CosmeticItemDef[] getSlotItems(CosmeticsSub sub) {
        return switch (sub) {
            case WINGS -> new CosmeticItemDef[] {
                    new CosmeticItemDef(WingType.SERAPHIM.id, "cosmetic.wing.seraphim.title", "cosmetic.wing.seraphim.desc", "WING", 0xFFF4F6FA, guiTex("preview_cosmetic_wing_seraphim_angel.png")),
                    new CosmeticItemDef(WingType.VALKYRIE.id, "cosmetic.wing.valkyrie.title", "cosmetic.wing.valkyrie.desc", "WING", 0xFFF3C669, guiTex("preview_cosmetic_wing_seraphim.png"))
            };
            case HATS -> new CosmeticItemDef[] {
                    new CosmeticItemDef(HatType.RONIN.id, "cosmetic.hat.ronin.title", "cosmetic.hat.ronin.desc", "HAT", 0xFFD9A66C, guiTex("preview_cosmetic_hat_ronin.png")),
                    new CosmeticItemDef(HatType.SHINOBI_HEADBAND.id, "cosmetic.hat.shinobi_headband.title", "cosmetic.hat.shinobi_headband.desc", "HAT", 0xFF6FA8DC, guiTex("preview_cosmetic_hat_shinobi.png")),
                    new CosmeticItemDef(HatType.WITCH.id, "cosmetic.hat.witch.title", "cosmetic.hat.witch.desc", "HAT", 0xFFA66CE0, guiTex("preview_cosmetic_hat_witch.png")),
                    new CosmeticItemDef(HatType.KITSUNE_EARS.id, "cosmetic.hat.kitsune_ears.title", "cosmetic.hat.kitsune_ears.desc", "HAT", 0xFFFF8C69, guiTex("preview_cosmetic_hat_kitsune.png")),
                    new CosmeticItemDef(HatType.CELESTIAL_HALO.id, "cosmetic.hat.celestial_halo.title", "cosmetic.hat.celestial_halo.desc", "HAT", 0xFFF7D468, guiTex("preview_cosmetic_hat_celestial_halo.png")),
                    new CosmeticItemDef(HatType.ONI_HORNS.id, "cosmetic.hat.oni_horns.title", "cosmetic.hat.oni_horns.desc", "HAT", 0xFFD94A4A, guiTex("preview_cosmetic_hat_oni_horns.png")),
                    new CosmeticItemDef(HatType.KITSUNE_MASK.id, "cosmetic.hat.kitsune_mask.title", "cosmetic.hat.kitsune_mask.desc", "HAT", 0xFFF08C64, guiTex("preview_cosmetic_hat_kitsune_mask.png")),
                    new CosmeticItemDef(HatType.BROKEN_HALO.id, "cosmetic.hat.broken_halo.title", "cosmetic.hat.broken_halo.desc", "HAT", 0xFFB090D8, guiTex("preview_cosmetic_hat_broken_halo.png")),
                    new CosmeticItemDef(HatType.CROWN.id, "cosmetic.hat.royal_crown.title", "cosmetic.hat.royal_crown.desc", "HAT", 0xFFF5C94C, guiTex("preview_cosmetic_hat_royal_crown.png")),
                    new CosmeticItemDef(HatType.STRAW_PIRATE.id, "cosmetic.hat.straw_pirate.title", "cosmetic.hat.straw_pirate.desc", "HAT", 0xFFE8B858, guiTex("preview_cosmetic_hat_straw_pirate.png")),
                    new CosmeticItemDef(HatType.USHANKA_FUR.id, "cosmetic.hat.ushanka_fur.title", "cosmetic.hat.ushanka_fur.desc", "HAT", 0xFF9EB0C8, guiTex("preview_cosmetic_hat_ushanka_fur.png")),
                    new CosmeticItemDef(HatType.TRICORN_CAPTAIN.id, "cosmetic.hat.tricorn_captain.title", "cosmetic.hat.tricorn_captain.desc", "HAT", 0xFFC89B54, guiTex("preview_cosmetic_hat_tricorn_captain.png")),
                    new CosmeticItemDef(HatType.NAPOLEON_BICORNE.id, "cosmetic.hat.napoleon_bicorne.title", "cosmetic.hat.napoleon_bicorne.desc", "HAT", 0xFF4B6FA8, guiTex("preview_cosmetic_hat_tricorn_captain.png")),
                    new CosmeticItemDef(HatType.EGIRL_HEADPHONES.id, "cosmetic.hat.egirl_headphones.title", "cosmetic.hat.egirl_headphones.desc", "HAT", 0xFFFF88CC, guiTex("preview_cosmetic_hat_egirl_headphones.png"))
            };
            case NECK -> new CosmeticItemDef[] {
                    new CosmeticItemDef(NeckType.CRIMSON_SCARF.id, "cosmetic.neck.crimson_scarf.title", "cosmetic.neck.crimson_scarf.desc", "NECK", 0xFFE04848, guiTex("preview_cosmetic_neck_scarf.png")),
                    new CosmeticItemDef(NeckType.GOLD_CHAIN.id, "cosmetic.neck.gold_chain.title", "cosmetic.neck.gold_chain.desc", "NECK", 0xFFF2C94C, guiTex("preview_cosmetic_neck_chain.png")),
                    new CosmeticItemDef(NeckType.SHADOW_NINJA_MASK.id, "cosmetic.neck.shadow_ninja_mask.title", "cosmetic.neck.shadow_ninja_mask.desc", "NECK", 0xFF6B7796, guiTex("preview_cosmetic_neck_shadow_ninja_mask.png")),
                    new CosmeticItemDef(NeckType.ONI_MENPO.id, "cosmetic.neck.oni_menpo.title", "cosmetic.neck.oni_menpo.desc", "NECK", 0xFFD94A4A, guiTex("preview_cosmetic_neck_oni_menpo.png")),
                    new CosmeticItemDef(NeckType.COOL_SUNGLASSES.id, "cosmetic.neck.cool_sunglasses.title", "cosmetic.neck.cool_sunglasses.desc", "NECK", 0xFF64B5F6, guiTex("preview_cosmetic_neck_cool_sunglasses.png")),
                    new CosmeticItemDef(NeckType.SNOW_SCARF.id, "cosmetic.neck.snow_scarf.title", "cosmetic.neck.snow_scarf.desc", "NECK", 0xFFB8E2FF, guiTex("preview_cosmetic_neck_snow_scarf.png")),
                    new CosmeticItemDef(NeckType.RUBY_CHOKER.id, "cosmetic.neck.ruby_choker.title", "cosmetic.neck.ruby_choker.desc", "NECK", 0xFFE63E62, guiTex("preview_cosmetic_neck_ruby_choker.png"))
            };
            case HAND -> new CosmeticItemDef[] {
                    new CosmeticItemDef(HandType.FIGHTER_WRAPS.id, "cosmetic.hand.fighter_wraps.title", "cosmetic.hand.fighter_wraps.desc", "HAND", 0xFFDADDE8, guiTex("preview_cosmetic_hand_wraps.png")),
                    new CosmeticItemDef(HandType.SYLAS_CHAINS.id, "cosmetic.hand.sylas_chains.title", "cosmetic.hand.sylas_chains.desc", "HAND", 0xFF79A6D2, guiTex("preview_cosmetic_hand_chains.png")),
                    new CosmeticItemDef(HandType.COMBAT_GLOVES.id, "cosmetic.hand.combat_gloves.title", "cosmetic.hand.combat_gloves.desc", "HAND", 0xFFD27A64, guiTex("preview_cosmetic_hand_combat_gloves.png"))
            };
            case WAIST -> new CosmeticItemDef[] {
                    new CosmeticItemDef(WaistType.KATANA_SASH.id, "cosmetic.waist.katana_sash.title", "cosmetic.waist.katana_sash.desc", "WAIST", 0xFFD46A6A, guiTex("preview_cosmetic_waist_katana.png")),
                    new CosmeticItemDef(WaistType.KITSUNE_TAIL.id, "cosmetic.waist.kitsune_tail.title", "cosmetic.waist.kitsune_tail.desc", "WAIST", 0xFFFF9A62, guiTex("preview_cosmetic_waist_tail.png")),
                    new CosmeticItemDef(WaistType.DEMON_TAIL.id, "cosmetic.waist.demon_tail.title", "cosmetic.waist.demon_tail.desc", "WAIST", 0xFFE04C5A, guiTex("preview_cosmetic_waist_demon_tail.png")),
                    new CosmeticItemDef(WaistType.TWIN_DAGGERS.id, "cosmetic.waist.twin_daggers.title", "cosmetic.waist.twin_daggers.desc", "WAIST", 0xFF8FB8DE, guiTex("preview_cosmetic_waist_twin_daggers.png")),
                    new CosmeticItemDef(WaistType.CAT_TAIL.id, "cosmetic.waist.cat_tail.title", "cosmetic.waist.cat_tail.desc", "WAIST", 0xFFB87CFF, guiTex("preview_cosmetic_waist_nekomata_tails.png")),
                    new CosmeticItemDef(WaistType.WOLF_TAIL.id, "cosmetic.waist.wolf_tail.title", "cosmetic.waist.wolf_tail.desc", "WAIST", 0xFFA2AAB8, guiTex("preview_cosmetic_waist_wolf_tail.png"))
            };
            case FOOT -> new CosmeticItemDef[] {
                    new CosmeticItemDef(FootType.HERMES_BOOTS.id, "cosmetic.foot.hermes_boots.title", "cosmetic.foot.hermes_boots.desc", "FOOT", 0xFFF5C96B, guiTex("preview_cosmetic_foot_hermes.png")),
                    new CosmeticItemDef(FootType.SHINOBI_WRAPS.id, "cosmetic.foot.shinobi_wraps.title", "cosmetic.foot.shinobi_wraps.desc", "FOOT", 0xFF6B7B99, guiTex("preview_cosmetic_foot_shinobi.png")),
                    new CosmeticItemDef(FootType.FIGHTER_LEG_WRAPS.id, "cosmetic.foot.fighter_leg_wraps.title", "cosmetic.foot.fighter_leg_wraps.desc", "FOOT", 0xFFD0B89E, guiTex("preview_cosmetic_foot_fighter_leg_wraps.png")),
                    new CosmeticItemDef(FootType.BARE_FEET.id, "cosmetic.foot.bare_feet.title", "cosmetic.foot.bare_feet.desc", "FOOT", 0xFFE2B290, guiTex("preview_cosmetic_foot_bare.png")),
                    new CosmeticItemDef(FootType.COMBAT_BOOTS.id, "cosmetic.foot.combat_boots.title", "cosmetic.foot.combat_boots.desc", "FOOT", 0xFF4A4E58, guiTex("preview_cosmetic_foot_combat_boots.png")),
                    new CosmeticItemDef(FootType.CLASSIC_SNEAKERS.id, "cosmetic.foot.classic_sneakers.title", "cosmetic.foot.classic_sneakers.desc", "FOOT", 0xFFE8E6E0, guiTex("preview_cosmetic_foot_sneakers.png"))
            };
            default -> new CosmeticItemDef[0];
        };
    }

    private CosmeticItemDef[] getTrailItems() {
        return new CosmeticItemDef[] {
                new CosmeticItemDef(TrailEffectType.JUMP_MAGIC_CIRCLE.id, "cosmetic.trail.jump_magic_circle.title", "cosmetic.trail.jump_magic_circle.desc", "TRAIL", 0xFF64D2FF, guiTex("preview_trail_jump_magic_circle.png")),
                new CosmeticItemDef(TrailEffectType.SHADOW_SMOKE.id, "cosmetic.trail.shadow_smoke.title", "cosmetic.trail.shadow_smoke.desc", "TRAIL", 0xFF9C64E8, guiTex("preview_trail_shadow_smoke.png")),
                new CosmeticItemDef(TrailEffectType.SNOWFLAKE_AURA.id, "cosmetic.trail.snowflake_aura.title", "cosmetic.trail.snowflake_aura.desc", "TRAIL", 0xFFAEE8FF, guiTex("preview_trail_snowflake_aura.png")),
                new CosmeticItemDef(TrailEffectType.CRIMSON_PETALS.id, "cosmetic.trail.crimson_petals.title", "cosmetic.trail.crimson_petals.desc", "TRAIL", 0xFFF04868, guiTex("preview_trail_crimson_petals.png"))
        };
    }

    private String getEquippedSlotId(CosmeticsSub sub) {
        return switch (sub) {
            case WINGS -> KiemConfig.cosmeticWing;
            case HATS -> KiemConfig.cosmeticHat;
            case NECK -> KiemConfig.cosmeticNeck;
            case HAND -> KiemConfig.cosmeticHand;
            case WAIST -> KiemConfig.cosmeticWaist;
            case FOOT -> KiemConfig.cosmeticFoot;
            default -> "OFF";
        };
    }

    private void setEquippedSlotId(CosmeticsSub sub, String id) {
        switch (sub) {
            case WINGS -> KiemConfig.cosmeticWing = id;
            case HATS -> KiemConfig.cosmeticHat = id;
            case NECK -> KiemConfig.cosmeticNeck = id;
            case HAND -> KiemConfig.cosmeticHand = id;
            case WAIST -> KiemConfig.cosmeticWaist = id;
            case FOOT -> KiemConfig.cosmeticFoot = id;
            default -> { }
        }
        KiemConfig.save();
    }

    private int getHandFirstPersonToggleWidth() {
        String onLabel = KiemLang.get("cosmetics.hand.first_person") + ": " + KiemLang.get("ui.on");
        String offLabel = KiemLang.get("cosmetics.hand.first_person") + ": " + KiemLang.get("ui.off");
        int textW = Math.max(BoutiqueDraw.textWidth(this.font, onLabel), BoutiqueDraw.textWidth(this.font, offLabel));
        return Math.min(180, Math.max(120, textW + 20));
    }

    private int getWaistDisableCapeToggleWidth() {
        String onLabel = KiemLang.get("cosmetic.waist.disable_cape.on");
        String offLabel = KiemLang.get("cosmetic.waist.disable_cape.off");
        int textW = Math.max(BoutiqueDraw.textWidth(this.font, onLabel), BoutiqueDraw.textWidth(this.font, offLabel));
        return Math.min(180, Math.max(120, textW + 20));
    }

    private int getBoutiqueGridMaxScroll(int itemCount, int vpH) {
        int totalRows = (itemCount + BOUTIQUE_COLS - 1) / BOUTIQUE_COLS;
        int contentH = totalRows * BOUTIQUE_CARD_H + Math.max(0, totalRows - 1) * BOUTIQUE_GAP;
        return Math.max(0, contentH - vpH);
    }

    private int getTrailMaxScroll(int trailCount, int vpH) {
        int sakuraH = 72;
        int totalRows = (trailCount + BOUTIQUE_COLS - 1) / BOUTIQUE_COLS;
        int gridH = totalRows * BOUTIQUE_CARD_H + Math.max(0, totalRows - 1) * BOUTIQUE_GAP;
        int contentH = sakuraH + BOUTIQUE_GAP + gridH;
        return Math.max(0, contentH - vpH);
    }

    private void drawVerticalScrollbar(GuiGraphicsExtractor graphics, int x, int top, int height, double scroll, int maxScroll) {
        if (maxScroll <= 0) return;
        int trackX = x - 4;
        graphics.fill(trackX, top, trackX + 3, top + height, 0x22FFFFFF);
        int contentH = height + maxScroll;
        int thumbH = Math.max(20, (int) ((height / (float) contentH) * height));
        int thumbY = top + (int) Math.round((scroll / maxScroll) * (height - thumbH));
        graphics.fill(trackX, thumbY, trackX + 3, thumbY + thumbH, BoutiqueDraw.theme().textAccent);
    }

    private void drawBoutiqueCard(GuiGraphicsExtractor graphics, int cx, int cy, int cardW, int cardH,
                                  Identifier preview, String title, boolean equipped, boolean hasCustomize, int mouseX, int mouseY) {
        boolean cardHover = mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH;
        BoutiqueDraw.drawGlassCard(graphics, cx, cy, cardW, cardH, equipped, cardHover);

        // Top-right ✦ sparkle
        BoutiqueDraw.drawText(graphics, this.font, "✦", cx + cardW - 14, cy + 7, equipped ? BoutiqueDraw.theme().textAccent : 0x55E8B8B0);

        // Centered 44x44 preview icon
        int iconSize = 44;
        int ix = cx + (cardW - iconSize) / 2;
        int iy = cy + 12;
        graphics.blit(preview, ix, iy, ix + iconSize, iy + iconSize, 0.0f, 1.0f, 0.0f, 1.0f);

        // Centered title
        String boundedTitle = BoutiqueDraw.fitText(this.font, title, cardW - 14);
        BoutiqueDraw.drawCenteredText(graphics, this.font, boundedTitle, cx + cardW / 2, cy + 62, BoutiqueDraw.theme().textPrimary);

        // Bottom pill(s)
        int btnH = 18;
        int btnY = cy + cardH - btnH - 9;
        if (hasCustomize) {
            int custW = 22;
            int gap = 4;
            int equipW = cardW - 18 - custW - gap;
            int equipX = cx + 9;
            int custX = equipX + equipW + gap;

            boolean eqHover = mouseX >= equipX && mouseX <= equipX + equipW && mouseY >= btnY && mouseY <= btnY + btnH;
            BoutiqueDraw.drawPill(graphics, equipX, btnY, equipW, btnH, equipped, eqHover);
            String eqLabel = BoutiqueDraw.fitText(this.font, KiemLang.get(equipped ? "cosmetics.equipped" : "cosmetics.equip"), equipW - 6);
            BoutiqueDraw.drawCenteredText(graphics, this.font, eqLabel, equipX + equipW / 2, btnY + 5,
                    equipped ? BoutiqueDraw.theme().textOnActivePill : (eqHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

            boolean custHover = mouseX >= custX && mouseX <= custX + custW && mouseY >= btnY && mouseY <= btnY + btnH;
            BoutiqueDraw.drawPill(graphics, custX, btnY, custW, btnH, false, custHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, "⚙", custX + custW / 2, btnY + 5,
                    custHover ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);
        } else {
            int btnW = Math.min(92, cardW - 18);
            int btnX = cx + (cardW - btnW) / 2;
            boolean btnHover = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
            BoutiqueDraw.drawPill(graphics, btnX, btnY, btnW, btnH, equipped, btnHover);
            String eqLabel = BoutiqueDraw.fitText(this.font, KiemLang.get(equipped ? "cosmetics.equipped" : "cosmetics.equip"), btnW - 8);
            BoutiqueDraw.drawCenteredText(graphics, this.font, eqLabel, btnX + btnW / 2, btnY + 5,
                    equipped ? BoutiqueDraw.theme().textOnActivePill : (btnHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        }
    }

    private void renderCosmeticSlotSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int vpH, int footerH, int mouseX, int mouseY) {
        CosmeticItemDef[] items = getSlotItems(currentCosmeticsSub);
        String activeId = getEquippedSlotId(currentCosmeticsSub);

        int gridW = w - BOUTIQUE_SCROLLBAR_W;
        int cardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
        int viewportBottom = y + vpH;

        int maxScroll = getBoutiqueGridMaxScroll(items.length, vpH);
        cosmeticsSlotScroll = Math.max(0.0, Math.min(cosmeticsSlotScroll, maxScroll));
        int scrollPx = (int) Math.round(cosmeticsSlotScroll);

        boolean inViewport = mouseX >= x && mouseX <= x + gridW && mouseY >= y && mouseY <= viewportBottom;
        int hoverX = inViewport ? mouseX : -9999;
        int hoverY = inViewport ? mouseY : -9999;

        String activeTitle = KiemLang.get("cosmetics.slot.none");
        for (CosmeticItemDef item : items) {
            if (item.id.equalsIgnoreCase(activeId)) {
                activeTitle = KiemLang.get(item.titleKey);
                break;
            }
        }

        graphics.enableScissor(x, y, x + gridW, viewportBottom);
        for (int i = 0; i < items.length; i++) {
            CosmeticItemDef item = items[i];
            boolean equipped = item.id.equalsIgnoreCase(activeId);
            int col = i % BOUTIQUE_COLS;
            int row = i / BOUTIQUE_COLS;
            int cx = x + col * (cardW + BOUTIQUE_GAP);
            int cy = y + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP) - scrollPx;
            if (cy + BOUTIQUE_CARD_H < y || cy > viewportBottom) continue;
            drawBoutiqueCard(graphics, cx, cy, cardW, BOUTIQUE_CARD_H, item.preview, KiemLang.get(item.titleKey), equipped, false, hoverX, hoverY);
        }
        graphics.disableScissor();

        drawVerticalScrollbar(graphics, x + w, y, vpH, cosmeticsSlotScroll, maxScroll);

        // Footer bar
        int infoY = viewportBottom + 6;
        BoutiqueDraw.drawGlassCard(graphics, x, infoY, w, footerH, false, false);

        int textMaxW = w - 24;
        if (currentCosmeticsSub == CosmeticsSub.HAND) {
            int toggleW = getHandFirstPersonToggleWidth();
            int toggleH = 20;
            int toggleX = x + w - toggleW - 8;
            int toggleY = infoY + (footerH - toggleH) / 2;
            textMaxW = w - toggleW - 24;

            boolean fpOn = KiemConfig.cosmeticHandFirstPerson;
            boolean toggleHover = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
            String label = KiemLang.get("cosmetics.hand.first_person") + ": " + KiemLang.get(fpOn ? "ui.on" : "ui.off");
            BoutiqueDraw.drawPill(graphics, toggleX, toggleY, toggleW, toggleH, fpOn, toggleHover);
            String boundedLbl = BoutiqueDraw.fitText(this.font, label, toggleW - 10);
            BoutiqueDraw.drawCenteredText(graphics, this.font, boundedLbl, toggleX + toggleW / 2, toggleY + 6,
                    fpOn ? BoutiqueDraw.theme().textOnActivePill : (toggleHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        } else if (currentCosmeticsSub == CosmeticsSub.WAIST) {
            int toggleW = getWaistDisableCapeToggleWidth();
            int toggleH = 20;
            int toggleX = x + w - toggleW - 8;
            int toggleY = infoY + (footerH - toggleH) / 2;
            textMaxW = w - toggleW - 24;

            boolean hideCape = KiemConfig.cosmeticWaistHideCape;
            boolean toggleHover = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
            String label = KiemLang.get(hideCape ? "cosmetic.waist.disable_cape.on" : "cosmetic.waist.disable_cape.off");
            BoutiqueDraw.drawPill(graphics, toggleX, toggleY, toggleW, toggleH, hideCape, toggleHover);
            String boundedLbl = BoutiqueDraw.fitText(this.font, label, toggleW - 10);
            BoutiqueDraw.drawCenteredText(graphics, this.font, boundedLbl, toggleX + toggleW / 2, toggleY + 6,
                    hideCape ? BoutiqueDraw.theme().textOnActivePill : (toggleHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        }

        drawBoundedText(graphics, KiemLang.get("cosmetics.slot.active") + ": " + activeTitle, x + 12, infoY + 6, textMaxW, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("cosmetics.slot.hint"), x + 12, infoY + 19, textMaxW, BoutiqueDraw.theme().textMuted);
    }

    private void renderPetsSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int vpH, int footerH, int mouseX, int mouseY) {
        ShoulderPetType[] petTypes = {
                ShoulderPetType.HAMSTER,
                ShoulderPetType.HAMSTER_HD,
                ShoulderPetType.OCTOPUS,
                ShoulderPetType.DRAGON,
                ShoulderPetType.INVENTORY_CURSE
        };
        Identifier[] petPreviews = {
                HAMSTER_PREVIEW,
                HAMSTER_HD_PREVIEW,
                OCTOPUS_PREVIEW,
                DRAGON_PREVIEW,
                INVENTORY_CURSE_PREVIEW
        };

        int gridW = w - BOUTIQUE_SCROLLBAR_W;
        int cardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
        int viewportBottom = y + vpH;

        int maxScroll = getBoutiqueGridMaxScroll(petTypes.length, vpH);
        cosmeticsPetsScroll = Math.max(0.0, Math.min(cosmeticsPetsScroll, maxScroll));
        int scrollPx = (int) Math.round(cosmeticsPetsScroll);

        boolean inViewport = mouseX >= x && mouseX <= x + gridW && mouseY >= y && mouseY <= viewportBottom;
        int hoverX = inViewport ? mouseX : -9999;
        int hoverY = inViewport ? mouseY : -9999;

        graphics.enableScissor(x, y, x + gridW, viewportBottom);
        for (int i = 0; i < petTypes.length; i++) {
            int col = i % BOUTIQUE_COLS;
            int row = i / BOUTIQUE_COLS;
            int cx = x + col * (cardW + BOUTIQUE_GAP);
            int cy = y + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP) - scrollPx;
            if (cy + BOUTIQUE_CARD_H < y || cy > viewportBottom) continue;
            boolean equipped = ShoulderPetManager.isEquipped(petTypes[i]);
            drawBoutiqueCard(graphics, cx, cy, cardW, BOUTIQUE_CARD_H, petPreviews[i], KiemLang.get(petTypes[i].titleKey), equipped, true, hoverX, hoverY);
        }
        graphics.disableScissor();

        drawVerticalScrollbar(graphics, x + w, y, vpH, cosmeticsPetsScroll, maxScroll);

        // Footer bar
        int infoY = viewportBottom + 6;
        BoutiqueDraw.drawGlassCard(graphics, x, infoY, w, footerH, false, false);
        ShoulderPetType sel = ShoulderPetManager.getSelectedPet();
        String currentName = sel == ShoulderPetType.OFF ? KiemLang.get("pets.none") : KiemLang.get(sel.titleKey);
        String sideLabel = ShoulderPetManager.isLeftShoulder() ? KiemLang.get("pets.side.left") : KiemLang.get("pets.side.right");
        drawBoundedText(graphics, KiemLang.get("pets.active_selection") + ": " + currentName + "  (" + sideLabel + ")", x + 12, infoY + 6, w - 24, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("pets.info_desc"), x + 12, infoY + 19, w - 24, BoutiqueDraw.theme().textMuted);
    }

    private void renderTrailsSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int vpH, int footerH, int mouseX, int mouseY) {
        CosmeticItemDef[] trailItems = getTrailItems();
        String activeTrailId = KiemConfig.cosmeticTrailEffect;

        int gridW = w - BOUTIQUE_SCROLLBAR_W;
        int viewportBottom = y + vpH;

        int maxScroll = getTrailMaxScroll(trailItems.length, vpH);
        cosmeticsTrailScroll = Math.max(0.0, Math.min(cosmeticsTrailScroll, maxScroll));
        int scrollPx = (int) Math.round(cosmeticsTrailScroll);

        boolean inViewport = mouseX >= x && mouseX <= x + gridW && mouseY >= y && mouseY <= viewportBottom;
        int hoverX = inViewport ? mouseX : -9999;
        int hoverY = inViewport ? mouseY : -9999;

        graphics.enableScissor(x, y, x + gridW, viewportBottom);

        // Featured Sakura Bloom Trail Banner
        int sakuraH = 72;
        int sakuraY = y - scrollPx;
        if (sakuraY + sakuraH >= y && sakuraY <= viewportBottom) {
            boolean active1 = KiemConfig.sakuraAuraEnabled;
            boolean hoverCard = hoverX >= x && hoverX <= x + gridW && hoverY >= sakuraY && hoverY <= sakuraY + sakuraH;
            BoutiqueDraw.drawGlassCard(graphics, x, sakuraY, gridW, sakuraH, active1, hoverCard);

            int thumbSize = 54;
            int imgX = x + 9;
            int imgY = sakuraY + (sakuraH - thumbSize) / 2;
            graphics.blit(SAKURA_AURA_PREVIEW, imgX, imgY, imgX + thumbSize, imgY + thumbSize, 0.0f, 1.0f, 0.0f, 1.0f);

            int rightX = imgX + thumbSize + 10;
            int rightW = gridW - thumbSize - 28;
            drawBoundedText(graphics, KiemLang.get("trails.sakura.title"), rightX, sakuraY + 11, rightW, BoutiqueDraw.theme().textPrimary);

            int btnW = Math.max(84, (rightW - 8) / 2);
            int btnH = 20;
            int btnY = sakuraY + sakuraH - btnH - 10;
            int custX = rightX + btnW + 8;

            boolean hover1 = hoverX >= rightX && hoverX <= rightX + btnW && hoverY >= btnY && hoverY <= btnY + btnH;
            BoutiqueDraw.drawPill(graphics, rightX, btnY, btnW, btnH, active1, hover1);
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get(active1 ? "auras.disable" : "auras.enable"),
                    rightX + btnW / 2, btnY + 6, active1 ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textPrimary);

            boolean custHover1 = hoverX >= custX && hoverX <= custX + btnW && hoverY >= btnY && hoverY <= btnY + btnH;
            BoutiqueDraw.drawPill(graphics, custX, btnY, btnW, btnH, false, custHover1);
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.customize"),
                    custX + btnW / 2, btnY + 6, custHover1 ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textPrimary);
        }

        // 3-column Trail Effect Cards
        int cardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
        int gridStartY = sakuraY + sakuraH + BOUTIQUE_GAP;
        for (int i = 0; i < trailItems.length; i++) {
            CosmeticItemDef item = trailItems[i];
            boolean equipped = item.id.equalsIgnoreCase(activeTrailId);
            int col = i % BOUTIQUE_COLS;
            int row = i / BOUTIQUE_COLS;
            int cx = x + col * (cardW + BOUTIQUE_GAP);
            int cy = gridStartY + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP);
            if (cy + BOUTIQUE_CARD_H < y || cy > viewportBottom) continue;
            drawBoutiqueCard(graphics, cx, cy, cardW, BOUTIQUE_CARD_H, item.preview, KiemLang.get(item.titleKey), equipped, false, hoverX, hoverY);
        }

        graphics.disableScissor();

        drawVerticalScrollbar(graphics, x + w, y, vpH, cosmeticsTrailScroll, maxScroll);

        // Footer bar
        int infoY = viewportBottom + 6;
        BoutiqueDraw.drawGlassCard(graphics, x, infoY, w, footerH, false, false);
        drawBoundedText(graphics, KiemLang.get("auras.local"), x + 12, infoY + 6, w - 24, BoutiqueDraw.theme().textMuted);
        drawBoundedText(graphics, KiemLang.get("auras.compatible"), x + 12, infoY + 19, w - 24, BoutiqueDraw.theme().textDim);
    }

    private void renderCapesSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int vpH, int footerH, int mouseX, int mouseY) {
        int gridW = w - BOUTIQUE_SCROLLBAR_W;
        int cardGap = 8;
        int cardW = (gridW - 2 * cardGap) / 3;
        int cardH = 228;
        int viewportBottom = y + vpH;

        int maxScroll = Math.max(0, cardH - vpH);
        cosmeticsCapesScroll = Math.max(0.0, Math.min(cosmeticsCapesScroll, maxScroll));
        int scrollPx = (int) Math.round(cosmeticsCapesScroll);

        boolean inViewport = mouseX >= x && mouseX <= x + gridW && mouseY >= y && mouseY <= viewportBottom;
        int hoverX = inViewport ? mouseX : -9999;
        int hoverY = inViewport ? mouseY : -9999;

        int card1X = x;
        int card1Y = y - scrollPx;
        int card2X = card1X + cardW + cardGap;
        int card3X = card2X + cardW + cardGap;

        CapeManager.CapeType current = CapeManager.getCurrentCape();

        graphics.enableScissor(x, y, x + gridW, viewportBottom);

        // ----------------- CARD 1: SANARA -----------------
        boolean isThisVersionStatic = false;
        boolean isThisVersionAnim = false;
        if (selectedSanaraVersion == 1) {
            isThisVersionStatic = (current == CapeManager.CapeType.SANARA_STATIC);
            isThisVersionAnim = (current == CapeManager.CapeType.SANARA_ANIMATED);
        } else if (selectedSanaraVersion == 2) {
            isThisVersionStatic = (current == CapeManager.CapeType.SANARA_V2_STATIC);
            isThisVersionAnim = (current == CapeManager.CapeType.SANARA_V2_ANIMATED);
        } else if (selectedSanaraVersion == 3) {
            isThisVersionStatic = (current == CapeManager.CapeType.SANARA_V3_STATIC);
            isThisVersionAnim = (current == CapeManager.CapeType.SANARA_V3_ANIMATED);
        }

        boolean hasThisVersionActive = (isThisVersionStatic || isThisVersionAnim);
        boolean card1Hover = hoverX >= card1X && hoverX <= card1X + cardW && hoverY >= card1Y && hoverY <= card1Y + cardH;
        BoutiqueDraw.drawGlassCard(graphics, card1X, card1Y, cardW, cardH, hasThisVersionActive, card1Hover);

        int pillW = 18;
        int pillH = 14;
        int pillGap = 2;
        int pillsTotalW = 3 * pillW + 2 * pillGap; // 58
        int p1X = card1X + cardW - 8 - pillsTotalW;
        int p2X = p1X + pillW + pillGap;
        int p3X = p2X + pillW + pillGap;
        int pillY = card1Y + 8;

        drawBoundedText(graphics, "SANARA", card1X + 8, card1Y + 11, p1X - (card1X + 10), BoutiqueDraw.theme().textPrimary);

        boolean p1Hover = hoverX >= p1X && hoverX <= p1X + pillW && hoverY >= pillY && hoverY <= pillY + pillH;
        boolean p2Hover = hoverX >= p2X && hoverX <= p2X + pillW && hoverY >= pillY && hoverY <= pillY + pillH;
        boolean p3Hover = hoverX >= p3X && hoverX <= p3X + pillW && hoverY >= pillY && hoverY <= pillY + pillH;

        BoutiqueDraw.drawPill(graphics, p1X, pillY, pillW, pillH, selectedSanaraVersion == 1, p1Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "V1", p1X + pillW / 2, pillY + 3,
                selectedSanaraVersion == 1 ? BoutiqueDraw.theme().textOnActivePill : (p1Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        BoutiqueDraw.drawPill(graphics, p2X, pillY, pillW, pillH, selectedSanaraVersion == 2, p2Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "V2", p2X + pillW / 2, pillY + 3,
                selectedSanaraVersion == 2 ? BoutiqueDraw.theme().textOnActivePill : (p2Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        BoutiqueDraw.drawPill(graphics, p3X, pillY, pillW, pillH, selectedSanaraVersion == 3, p3Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "V3", p3X + pillW / 2, pillY + 3,
                selectedSanaraVersion == 3 ? BoutiqueDraw.theme().textOnActivePill : (p3Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        int imgW = cardW - 16;
        int imgH = 138;
        int img1X = card1X + 8;
        int img1Y = card1Y + 26;
        Identifier activePreview = (selectedSanaraVersion == 1) ? SANARA_V1_PREVIEW
                : (selectedSanaraVersion == 2 ? SANARA_V2_PREVIEW : SANARA_V3_PREVIEW);
        graphics.blit(activePreview, img1X, img1Y, img1X + imgW, img1Y + imgH, 0.0f, 1.0f, 0.0f, 1.0f);

        String verLabel = "V" + selectedSanaraVersion;
        int verTagW = 24;
        int verTagH = 14;
        BoutiqueDraw.drawPill(graphics, img1X + imgW - verTagW - 4, img1Y + imgH - verTagH - 4, verTagW, verTagH, false, false);
        BoutiqueDraw.drawCenteredText(graphics, this.font, verLabel, img1X + imgW - verTagW / 2 - 4, img1Y + imgH - verTagH + 3, BoutiqueDraw.theme().textPrimary);

        int btnY = img1Y + imgH + 8;
        int btnW = (imgW - 4) / 2;
        int btnH = 20;
        int btn1X = img1X;
        int btn2X = img1X + btnW + 4;

        boolean b1Hover = hoverX >= btn1X && hoverX <= btn1X + btnW && hoverY >= btnY && hoverY <= btnY + btnH;
        boolean b2Hover = hoverX >= btn2X && hoverX <= btn2X + btnW && hoverY >= btnY && hoverY <= btnY + btnH;

        BoutiqueDraw.drawPill(graphics, btn1X, btnY, btnW, btnH, isThisVersionStatic, b1Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, "STANDARD", btnW - 4), btn1X + btnW / 2, btnY + 6,
                isThisVersionStatic ? BoutiqueDraw.theme().textOnActivePill : (b1Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        BoutiqueDraw.drawPill(graphics, btn2X, btnY, btnW, btnH, isThisVersionAnim, b2Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, "ANIMATED", btnW - 4), btn2X + btnW / 2, btnY + 6,
                isThisVersionAnim ? BoutiqueDraw.theme().textOnActivePill : (b2Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        int footerY = btnY + btnH + 7;
        if (hasThisVersionActive) {
            boolean unequipHover = hoverX >= card1X + 12 && hoverX <= card1X + cardW - 12 && hoverY >= footerY && hoverY <= footerY + 14;
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.unequip"), card1X + cardW / 2, footerY + 2, unequipHover ? 0xFFFF7788 : BoutiqueDraw.theme().textAccent);
        } else if (current != CapeManager.CapeType.OFF) {
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.other_active"), card1X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
        } else {
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.inactive"), card1X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
        }

        // ----------------- CARD 2: RIKKA -----------------
        boolean isRikkaStatic = (current == CapeManager.CapeType.RIKKA_STATIC);
        boolean isRikkaAnim = (current == CapeManager.CapeType.RIKKA_ANIMATED);
        boolean rikkaActive = (isRikkaStatic || isRikkaAnim);
        boolean card2Hover = hoverX >= card2X && hoverX <= card2X + cardW && hoverY >= card1Y && hoverY <= card1Y + cardH;

        BoutiqueDraw.drawGlassCard(graphics, card2X, card1Y, cardW, cardH, rikkaActive, card2Hover);
        BoutiqueDraw.drawText(graphics, this.font, "RIKKA", card2X + 8, card1Y + 11, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawPill(graphics, card2X + cardW - 34, card1Y + 8, 26, 14, true, false);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "NEW", card2X + cardW - 21, card1Y + 11, BoutiqueDraw.theme().textOnActivePill);

        int img2X = card2X + 8;
        int img2Y = card1Y + 26;
        graphics.blit(RIKKA_PREVIEW, img2X, img2Y, img2X + imgW, img2Y + imgH, 0.0f, 1.0f, 0.0f, 1.0f);

        BoutiqueDraw.drawPill(graphics, img2X + imgW - verTagW - 4, img2Y + imgH - verTagH - 4, verTagW, verTagH, false, false);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "V1", img2X + imgW - verTagW / 2 - 4, img2Y + imgH - verTagH + 3, BoutiqueDraw.theme().textPrimary);

        int btn2_1X = img2X;
        int btn2_2X = img2X + btnW + 4;
        boolean b2_1Hover = hoverX >= btn2_1X && hoverX <= btn2_1X + btnW && hoverY >= btnY && hoverY <= btnY + btnH;
        boolean b2_2Hover = hoverX >= btn2_2X && hoverX <= btn2_2X + btnW && hoverY >= btnY && hoverY <= btnY + btnH;

        BoutiqueDraw.drawPill(graphics, btn2_1X, btnY, btnW, btnH, isRikkaStatic, b2_1Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, "STANDARD", btnW - 4), btn2_1X + btnW / 2, btnY + 6,
                isRikkaStatic ? BoutiqueDraw.theme().textOnActivePill : (b2_1Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        BoutiqueDraw.drawPill(graphics, btn2_2X, btnY, btnW, btnH, isRikkaAnim, b2_2Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, "ANIMATED", btnW - 4), btn2_2X + btnW / 2, btnY + 6,
                isRikkaAnim ? BoutiqueDraw.theme().textOnActivePill : (b2_2Hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        if (rikkaActive) {
            boolean unequipHover2 = hoverX >= card2X + 12 && hoverX <= card2X + cardW - 12 && hoverY >= footerY && hoverY <= footerY + 14;
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.unequip"), card2X + cardW / 2, footerY + 2, unequipHover2 ? 0xFFFF7788 : BoutiqueDraw.theme().textAccent);
        } else if (current != CapeManager.CapeType.OFF) {
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.other_active"), card2X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
        } else {
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.inactive"), card2X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
        }

        // ----------------- CARD 3: CUSTOM CAPE -----------------
        boolean isCustomActive = (current == CapeManager.CapeType.CUSTOM);
        boolean card3Hover = hoverX >= card3X && hoverX <= card3X + cardW && hoverY >= card1Y && hoverY <= card1Y + cardH;
        BoutiqueDraw.drawGlassCard(graphics, card3X, card1Y, cardW, cardH, isCustomActive, card3Hover);

        String folderLabel = KiemLang.get("cosmetics.open_folder");
        int refreshPillW = 18;
        int folderPillW = Math.min(54, Math.max(42, BoutiqueDraw.textWidth(this.font, folderLabel) + 12));
        int refreshPillX = card3X + cardW - 8 - refreshPillW;
        int folderPillX = refreshPillX - folderPillW - 3;
        int customPillY = card1Y + 8;

        drawBoundedText(graphics, KiemLang.get("cosmetics.custom_title"), card3X + 8, card1Y + 11, folderPillX - (card3X + 12), BoutiqueDraw.theme().textPrimary);

        boolean folderHover = hoverX >= folderPillX && hoverX <= folderPillX + folderPillW && hoverY >= customPillY && hoverY <= customPillY + pillH;
        boolean refreshHover = hoverX >= refreshPillX && hoverX <= refreshPillX + refreshPillW && hoverY >= customPillY && hoverY <= customPillY + pillH;

        BoutiqueDraw.drawPill(graphics, folderPillX, customPillY, folderPillW, pillH, false, folderHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, folderLabel, folderPillW - 6),
                folderPillX + folderPillW / 2, customPillY + 3, folderHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

        BoutiqueDraw.drawPill(graphics, refreshPillX, customPillY, refreshPillW, pillH, false, refreshHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "🔄", refreshPillX + refreshPillW / 2, customPillY + 3, refreshHover ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);

        int img3X = card3X + 8;
        int img3Y = card1Y + 26;
        CustomCapeManager.CustomCapeEntry activeCustom = CustomCapeManager.getSelectedCape();
        java.util.List<CustomCapeManager.CustomCapeEntry> capes = CustomCapeManager.getLoadedCapes();

        if (capes.isEmpty()) {
            BoutiqueDraw.drawGlassCard(graphics, img3X, img3Y, imgW, imgH, false, false);
            BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, KiemLang.get("cosmetics.empty_folder"), imgW - 10), img3X + imgW / 2, img3Y + 28, BoutiqueDraw.theme().textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, KiemLang.get("cosmetics.empty_sub"), imgW - 10), img3X + imgW / 2, img3Y + 46, BoutiqueDraw.theme().textDim);
            BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, KiemLang.get("cosmetics.empty_hint"), imgW - 10), img3X + imgW / 2, img3Y + 62, BoutiqueDraw.theme().textDim);

            int openBtnW = imgW - 16;
            int openBtnH = 20;
            int openBtnX = img3X + 8;
            int openBtnY = img3Y + 92;
            boolean openHover = hoverX >= openBtnX && hoverX <= openBtnX + openBtnW && hoverY >= openBtnY && hoverY <= openBtnY + openBtnH;
            BoutiqueDraw.drawPill(graphics, openBtnX, openBtnY, openBtnW, openBtnH, false, openHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, KiemLang.get("cosmetics.open_folder_btn"), openBtnW - 8),
                    openBtnX + openBtnW / 2, openBtnY + 6, openHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        } else if (activeCustom != null) {
            Identifier previewTex = activeCustom.frameIdentifiers[0];
            float u0 = 192.0f / 1024.0f;
            float u1 = 352.0f / 1024.0f;
            float v0 = 16.0f / 512.0f;
            float v1 = 272.0f / 512.0f;
            graphics.blit(previewTex, img3X, img3Y, img3X + imgW, img3Y + imgH, u0, u1, v0, v1);

            int selBarH = 18;
            int selBarY = img3Y + imgH - selBarH - 4;
            int arrowW = 16;
            int leftArrowX = img3X + 4;
            int rightArrowX = img3X + imgW - arrowW - 4;
            int selMidW = rightArrowX - (leftArrowX + arrowW);
            int selMidX = leftArrowX + arrowW;

            boolean leftArrowHover = hoverX >= leftArrowX && hoverX <= leftArrowX + arrowW && hoverY >= selBarY && hoverY <= selBarY + selBarH;
            boolean rightArrowHover = hoverX >= rightArrowX && hoverX <= rightArrowX + arrowW && hoverY >= selBarY && hoverY <= selBarY + selBarH;

            BoutiqueDraw.drawPillBar(graphics, img3X + 2, selBarY, imgW - 4, selBarH);
            BoutiqueDraw.drawCenteredText(graphics, this.font, "◀", leftArrowX + arrowW / 2, selBarY + 5, leftArrowHover ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, "▶", rightArrowX + arrowW / 2, selBarY + 5, rightArrowHover ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);

            String capeLabel = activeCustom.displayName;
            if (BoutiqueDraw.textWidth(this.font, capeLabel) > selMidW - 6) {
                capeLabel = BoutiqueDraw.fitText(this.font, capeLabel, selMidW - 14);
            }
            BoutiqueDraw.drawCenteredText(graphics, this.font, capeLabel, selMidX + selMidW / 2, selBarY + 5, BoutiqueDraw.theme().textPrimary);

            String badge = activeCustom.isAnimated ? ("GIF • " + activeCustom.frameCount + "F") : "PNG";
            int badgeW = BoutiqueDraw.textWidth(this.font, badge) + 10;
            int badgeH = 14;
            BoutiqueDraw.drawPill(graphics, img3X + imgW - badgeW - 4, img3Y + 4, badgeW, badgeH, false, false);
            BoutiqueDraw.drawCenteredText(graphics, this.font, badge, img3X + imgW - badgeW / 2 - 4, img3Y + 7, BoutiqueDraw.theme().textPrimary);

            int equipBtnW = imgW;
            int equipBtnH = 20;
            int equipBtnX = img3X;
            int equipBtnY = img3Y + imgH + 8;
            boolean equipHover = hoverX >= equipBtnX && hoverX <= equipBtnX + equipBtnW && hoverY >= equipBtnY && hoverY <= equipBtnY + equipBtnH;
            boolean customEquipped = isCustomActive && activeCustom.fileName.equalsIgnoreCase(KiemConfig.customCapeFileName);
            BoutiqueDraw.drawPill(graphics, equipBtnX, equipBtnY, equipBtnW, equipBtnH, customEquipped, equipHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get(customEquipped ? "cosmetics.equipped" : "cosmetics.equip"),
                    equipBtnX + equipBtnW / 2, equipBtnY + 6, customEquipped ? BoutiqueDraw.theme().textOnActivePill : (equipHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

            if (isCustomActive) {
                boolean unequipHover3 = hoverX >= card3X + 12 && hoverX <= card3X + cardW - 12 && hoverY >= footerY && hoverY <= footerY + 14;
                BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.unequip"), card3X + cardW / 2, footerY + 2, unequipHover3 ? 0xFFFF7788 : BoutiqueDraw.theme().textAccent);
            } else if (current != CapeManager.CapeType.OFF) {
                BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.other_active"), card3X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
            } else {
                BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("cosmetics.inactive"), card3X + cardW / 2, footerY + 2, BoutiqueDraw.theme().textDim);
            }
        }

        graphics.disableScissor();
        drawVerticalScrollbar(graphics, x + w, y, vpH, cosmeticsCapesScroll, maxScroll);

        // Bottom Info Card & True Color Toggle
        int infoY = viewportBottom + 6;
        BoutiqueDraw.drawGlassCard(graphics, x, infoY, w, footerH, false, false);
        String currentName;
        if (current == CapeManager.CapeType.OFF) {
            currentName = KiemLang.get("cosmetics.none");
        } else if (current == CapeManager.CapeType.CUSTOM) {
            currentName = KiemLang.get("cosmetics.custom_cape_prefix") + " (" + CustomCapeManager.getSelectedCapeName() + ")";
        } else {
            currentName = current.getTitle();
        }
        int toggleW = 114;
        int toggleH = 20;
        int toggleX = x + w - toggleW - 8;
        int toggleY = infoY + (footerH - toggleH) / 2;
        int infoTextMaxW = w - toggleW - 24;
        drawBoundedText(graphics, KiemLang.get("cosmetics.active_selection") + ": " + currentName, x + 12, infoY + 6, infoTextMaxW, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("cosmetics.info_desc"), x + 12, infoY + 19, infoTextMaxW, BoutiqueDraw.theme().textMuted);

        boolean toggleHover = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
        BoutiqueDraw.drawPill(graphics, toggleX, toggleY, toggleW, toggleH, KiemConfig.capeTrueColor, toggleHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get(KiemConfig.capeTrueColor ? "cosmetics.true_color_on" : "cosmetics.true_color_off"),
                toggleX + toggleW / 2, toggleY + 6, KiemConfig.capeTrueColor ? BoutiqueDraw.theme().textOnActivePill : (toggleHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
    }

    // ==================== WELCOME TAB ====================

    private record HeroLinkRect(int x, int y, int w, int h, String label, Identifier icon, String url, boolean showLabel) {}

    private HeroLinkRect[] computeHeroLinkRects(int leftEnd, int resetBtnX, int keyBoxY, int keyBoxH) {
        String[] labels = { "Discord", "GitHub", "Modrinth", "CurseForge" };
        Identifier[] icons = {
                guiTex("icon_discord.png"),
                guiTex("icon_github.png"),
                guiTex("icon_modrinth.png"),
                guiTex("icon_curseforge.png")
        };
        String[] urls = {
                UpdateChecker.DISCORD_URL,
                UpdateChecker.GITHUB_URL,
                UpdateChecker.MODRINTH_URL,
                UpdateChecker.CURSEFORGE_URL
        };
        int availW = resetBtnX - leftEnd - 16;
        int gap = 5;
        int iconSize = 12;
        int[] widths = new int[4];
        int totalFullW = 3 * gap;
        for (int i = 0; i < 4; i++) {
            widths[i] = iconSize + 4 + BoutiqueDraw.textWidth(this.font, labels[i]) + 12;
            totalFullW += widths[i];
        }
        boolean showLabels = availW >= totalFullW;
        int compactW = 22;
        int totalW = showLabels ? totalFullW : (4 * compactW + 3 * gap);
        int startX = leftEnd + 8 + Math.max(0, (availW - totalW) / 2);

        HeroLinkRect[] rects = new HeroLinkRect[4];
        int cx = startX;
        for (int i = 0; i < 4; i++) {
            int pw = showLabels ? widths[i] : compactW;
            rects[i] = new HeroLinkRect(cx, keyBoxY, pw, keyBoxH, labels[i], icons[i], urls[i], showLabels);
            cx += pw + gap;
        }
        return rects;
    }

    private void renderWelcomeTab(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.Theme theme = BoutiqueDraw.theme();
        BoutiqueDraw.drawGlassPanel(graphics, x, y, w, h);

        int ix = x + 16;
        int iy = y + 14;
        int iw = w - 32;
        int curBaseY = iy;

        // Hero Header Banner
        int heroH = 72;
        BoutiqueDraw.drawGlassCard(graphics, ix, curBaseY, iw, heroH, false, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("welcome.hero_title"), ix + 14, curBaseY + 12, theme.textAccent);

        // Top-right version / update pill inside Hero
        boolean hasUpdate = KiemConfig.updateCheckerEnabled && UpdateChecker.hasUpdate();
        String verText = hasUpdate
                ? KiemLang.get("welcome.update_prefix") + " v" + UpdateChecker.getLatestVersion() + "!"
                : "v" + UpdateChecker.getCurrentVersion() + " · " + KiemLang.get("welcome.latest");
        int verPillW = Math.max(88, BoutiqueDraw.textWidth(this.font, verText) + 18);
        int verPillH = 18;
        int verPillX = ix + iw - verPillW - 14;
        int verPillY = curBaseY + 9;
        boolean verHover = hasUpdate && mouseX >= verPillX && mouseX <= verPillX + verPillW && mouseY >= verPillY && mouseY <= verPillY + verPillH;
        BoutiqueDraw.drawPill(graphics, verPillX, verPillY, verPillW, verPillH, hasUpdate, verHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, verText, verPillX + verPillW / 2, verPillY + 5,
                hasUpdate ? theme.textOnActivePill : theme.textPositive);

        drawBoundedText(graphics, KiemLang.get("welcome.hero_desc"), ix + 14, curBaseY + 28, iw - 28, theme.textPrimary);

        // Keybind Pill inside Hero Banner
        int keyBoxX = ix + 14;
        int keyBoxY = curBaseY + 44;
        int keyBoxH = 20;
        String menuKeyName = KiemConfig.getKeyName(KiemConfig.menuKey);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("welcome.menu_key"), keyBoxX, keyBoxY + 6, theme.textMuted);
        int labelW = BoutiqueDraw.textWidth(this.font, KiemLang.get("welcome.menu_key")) + 8;
        int btnW = 82;
        boolean keyHover = mouseX >= keyBoxX + labelW && mouseX <= keyBoxX + labelW + btnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH;
        drawKeybindButton(graphics, keyBoxX + labelW, keyBoxY, btnW, keyBoxH, menuKeyName, listeningForMenuKey, keyHover);

        // Reset to Defaults Button inside Hero Banner
        int resetBtnW = 136;
        int resetBtnX = ix + iw - resetBtnW - 14;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH;
        BoutiqueDraw.drawPill(graphics, resetBtnX, keyBoxY, resetBtnW, keyBoxH, false, resetHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("welcome.reset_all"), resetBtnX + resetBtnW / 2, keyBoxY + 6,
                resetHover ? 0xFFFF8899 : 0xFFFF9AA8);

        // Center Community Links (Discord, GitHub, Modrinth, CurseForge) between Keybind and Reset
        int leftEnd = keyBoxX + labelW + btnW;
        HeroLinkRect[] linkRects = computeHeroLinkRects(leftEnd, resetBtnX, keyBoxY, keyBoxH);
        for (HeroLinkRect lr : linkRects) {
            boolean lHover = mouseX >= lr.x && mouseX <= lr.x + lr.w && mouseY >= lr.y && mouseY <= lr.y + lr.h;
            BoutiqueDraw.drawPill(graphics, lr.x, lr.y, lr.w, lr.h, false, lHover);
            int iconSize = 12;
            int iconY = lr.y + (lr.h - iconSize) / 2;
            if (lr.showLabel) {
                int startX = lr.x + 6;
                graphics.blit(lr.icon, startX, iconY, startX + iconSize, iconY + iconSize, 0.0f, 1.0f, 0.0f, 1.0f);
                BoutiqueDraw.drawText(graphics, this.font, lr.label, startX + iconSize + 4, lr.y + 6,
                        lHover ? theme.textPrimary : theme.textMuted);
            } else {
                int iconX = lr.x + (lr.w - iconSize) / 2;
                graphics.blit(lr.icon, iconX, iconY, iconX + iconSize, iconY + iconSize, 0.0f, 1.0f, 0.0f, 1.0f);
            }
        }

        // What's New Section Header
        int featY = curBaseY + heroH + 10;
        String whatsNewHeader = KiemLang.get("welcome.whats_new");
        BoutiqueDraw.drawText(graphics, this.font, whatsNewHeader, ix, featY, theme.textAccent);
        int titleW = BoutiqueDraw.textWidth(this.font, whatsNewHeader);
        graphics.fill(ix + titleW + 10, featY + 4, ix + iw, featY + 5, theme.panelBorder);

        int listY = featY + 14;
        int navBtnH = 24;
        int availListH = (y + h - 12) - listY - navBtnH - 8;
        int itemGap = 6;
        int itemH = Math.min(44, Math.max(32, (availListH - 2 * itemGap) / 3));
        int goW = 92;
        int goX = ix + iw - goW - 12;

        for (int i = 0; i < 3; i++) {
            int curY = listY + i * (itemH + itemGap);
            String title = KiemLang.get("welcome.wn." + (i + 1) + ".title");
            String desc = KiemLang.get("welcome.wn." + (i + 1) + ".desc");
            String btnText = KiemLang.get("welcome.wn." + (i + 1) + ".btn");

            boolean cardHover = mouseX >= ix && mouseX <= ix + iw && mouseY >= curY && mouseY <= curY + itemH;
            BoutiqueDraw.drawGlassCard(graphics, ix, curY, iw, itemH, false, cardHover);
            int maxMetaW = goX - (ix + 14) - 8;
            drawBoundedText(graphics, title, ix + 14, curY + Math.max(5, itemH / 2 - 12), maxMetaW, theme.textPrimary);
            drawBoundedText(graphics, desc, ix + 14, curY + Math.max(17, itemH / 2 + 1), maxMetaW, theme.textDim);

            int btnCurY = curY + (itemH - 20) / 2;
            BoutiqueDraw.drawPill(graphics, goX, btnCurY, goW, 20, cardHover, cardHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, BoutiqueDraw.fitText(this.font, btnText, goW - 8),
                    goX + goW / 2, btnCurY + 6, cardHover ? theme.textOnActivePill : theme.textMuted);
        }

        // Quick Bottom Nav Bar (Features & Wardrobe)
        int navY = listY + 3 * (itemH + itemGap) + 2;
        int navBtnW = (iw - 8) / 2;

        int b1X = ix;
        boolean b1Hover = mouseX >= b1X && mouseX <= b1X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH;
        BoutiqueDraw.drawPill(graphics, b1X, navY, navBtnW, navBtnH, false, b1Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("welcome.nav_features"), b1X + navBtnW / 2, navY + 8,
                b1Hover ? theme.textPrimary : theme.textMuted);

        int b2X = b1X + navBtnW + 8;
        boolean b2Hover = mouseX >= b2X && mouseX <= b2X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH;
        BoutiqueDraw.drawPill(graphics, b2X, navY, navBtnW, navBtnH, true, b2Hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("welcome.nav_cosmetics"), b2X + navBtnW / 2, navY + 8, theme.textOnActivePill);
    }

    // ==================== CONFIG (FEATURES) TAB ====================

    private void renderConfigTab(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.drawGlassPanel(graphics, x, y, w, h);

        int ix = x + 16;
        int iy = y + 14;
        int iw = w - 32;
        int ih = h - 28;

        if (configSubView == ConfigSubView.MAIN) {
            renderConfigMainView(graphics, ix, iy, iw, ih, mouseX, mouseY);
        } else {
            // Subview header bar (fixed, above scissored body)
            int headerH = 26;
            int bodyY = iy + headerH;
            int bodyH = ih - headerH;
            int maxScroll = getConfigSubViewMaxScroll(bodyH);
            configScroll = Math.max(0.0, Math.min(configScroll, maxScroll));
            int scrollPx = (int) Math.round(configScroll);

            int bodyW = maxScroll > 0 ? iw - 8 : iw;
            boolean inBody = mouseX >= ix && mouseX <= ix + bodyW && mouseY >= bodyY && mouseY <= bodyY + bodyH;
            int bodyMouseX = inBody ? mouseX : -9999;
            int bodyMouseY = inBody ? mouseY : -9999;

            renderConfigSubViewHeader(graphics, ix, iy, iw, mouseX, mouseY);

            graphics.enableScissor(ix, bodyY, ix + bodyW, bodyY + bodyH);
            int sy = bodyY - scrollPx;
            if (configSubView == ConfigSubView.MINI_MODELS) {
                renderMiniModelsView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
                renderCustomViewmodelView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.AFK_CINEMATICS) {
                renderAfkCinematicsView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
                renderBetterThirdPersonView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.SMOOTH_ZOOM) {
                renderSmoothZoomView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.CHAT_QOL) {
                renderChatQoLView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.TOOLTIP_QOL) {
                renderTooltipQoLView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            } else if (configSubView == ConfigSubView.ANTI_AFK) {
                renderAntiAfkView(graphics, ix, sy, bodyW, bodyMouseX, bodyMouseY);
            }
            graphics.disableScissor();

            drawVerticalScrollbar(graphics, ix + iw, bodyY, bodyH, configScroll, maxScroll);
        }
    }

    private int getConfigSubViewContentHeight() {
        return switch (configSubView) {
            case MINI_MODELS -> 220;
            case CUSTOM_VIEWMODEL -> 230;
            case BETTER_THIRD_PERSON -> 260;
            case AFK_CINEMATICS -> 230;
            case SMOOTH_ZOOM -> 342;
            case CHAT_QOL -> 228;
            case TOOLTIP_QOL -> 170;
            case ANTI_AFK -> 228;
            default -> 0;
        };
    }

    private int getConfigSubViewMaxScroll(int bodyH) {
        return Math.max(0, getConfigSubViewContentHeight() - bodyH);
    }

    private void renderConfigSubViewHeader(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int backBtnW = 60;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        BoutiqueDraw.drawPill(graphics, x, y, backBtnW, backBtnH, false, backHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6,
                backHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

        String modTitle = switch (configSubView) {
            case MINI_MODELS -> KiemLang.get("mod.mini_models.title");
            case CUSTOM_VIEWMODEL -> KiemLang.get("mod.viewmodel.title");
            case BETTER_THIRD_PERSON -> KiemLang.get("mod.better_third_person.title");
            case AFK_CINEMATICS -> KiemLang.get("mod.afk_cinematics.title");
            case SMOOTH_ZOOM -> KiemLang.get("mod.smooth_zoom.title");
            case CHAT_QOL -> KiemLang.get("mod.chat_qol.title");
            case TOOLTIP_QOL -> KiemLang.get("mod.tooltip_qol.title");
            case ANTI_AFK -> KiemLang.get("mod.anti_afk.title");
            default -> "";
        };

        int titleX = x + backBtnW + 12;
        BoutiqueDraw.drawText(graphics, this.font, modTitle, titleX, y + 6, BoutiqueDraw.theme().textPrimary);

        boolean hasReset = (configSubView == ConfigSubView.MINI_MODELS
                || configSubView == ConfigSubView.CUSTOM_VIEWMODEL
                || configSubView == ConfigSubView.BETTER_THIRD_PERSON);
        boolean hasMasterToggle = (configSubView == ConfigSubView.CUSTOM_VIEWMODEL);

        int rightLimit = x + w;
        if (hasReset) {
            int resetBtnW = 62;
            int resetBtnX = x + w - resetBtnW;
            boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= y && mouseY <= y + backBtnH;
            BoutiqueDraw.drawPill(graphics, resetBtnX, y, resetBtnW, backBtnH, false, resetHover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, y + 6,
                    resetHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
            rightLimit = resetBtnX - 8;
        }
        if (hasMasterToggle) {
            int toggleW = 56;
            int toggleH = 18;
            int tX = rightLimit - toggleW;
            int tY = y + 1;
            boolean tHover = mouseX >= tX && mouseX <= tX + toggleW && mouseY >= tY && mouseY <= tY + toggleH;
            drawToggleButton(graphics, tX, tY, toggleW, toggleH, KiemConfig.viewmodelEnabled, tHover);
            rightLimit = tX - 8;
        }

        int headerLineX = titleX + BoutiqueDraw.textWidth(this.font, modTitle) + 10;
        if (rightLimit - 10 > headerLineX) {
            graphics.fill(headerLineX, y + 10, rightLimit - 4, y + 11, BoutiqueDraw.theme().panelBorder);
        }
    }

    private void renderConfigMainView(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("ui.modules"), x, y, BoutiqueDraw.theme().textPrimary);
        int titleW = BoutiqueDraw.textWidth(this.font, KiemLang.get("ui.modules"));
        graphics.fill(x + titleW + 10, y + 4, x + w, y + 5, BoutiqueDraw.theme().panelBorder);

        int colGap = 10;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;

        int startY = y + 16;
        int cardGap = 8;
        int cardH = Math.min(58, Math.max(44, (h - 16 - 3 * cardGap) / 4));
        int badgeW = 78;
        int badgeH = 20;

        // Left Column
        renderModuleCard(graphics, leftColX, startY, colW, cardH, badgeW, badgeH,
                "🧍 " + KiemLang.get("mod.mini_models.title"),
                KiemLang.get("mod.mini_models.desc") + ((KiemConfig.selfScaleEnabled || KiemConfig.othersScaleEnabled) ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.selfScaleEnabled || KiemConfig.othersScaleEnabled, mouseX, mouseY);

        renderModuleCard(graphics, leftColX, startY + (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "🗡 " + KiemLang.get("mod.viewmodel.title"),
                KiemLang.get("mod.viewmodel.desc") + (KiemConfig.viewmodelEnabled ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.viewmodelEnabled, mouseX, mouseY);

        renderModuleCard(graphics, leftColX, startY + 2 * (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "🎥 " + KiemLang.get("mod.better_third_person.title"),
                KiemLang.get("mod.better_third_person.desc") + (KiemConfig.betterThirdPersonEnabled ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.betterThirdPersonEnabled, mouseX, mouseY);

        renderModuleCard(graphics, leftColX, startY + 3 * (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "🎬 " + KiemLang.get("mod.afk_cinematics.title"),
                KiemLang.get("mod.afk_cinematics.desc") + (KiemConfig.afkCinematicsEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.afkCinematicsSeconds + "s]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.afkCinematicsEnabled, mouseX, mouseY);

        // Right Column
        renderModuleCard(graphics, rightColX, startY, colW, cardH, badgeW, badgeH,
                "🔍 " + KiemLang.get("mod.smooth_zoom.title"),
                KiemLang.get("mod.smooth_zoom.desc") + (KiemConfig.zoomEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.zoomDefaultFactor + "x]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.zoomEnabled, mouseX, mouseY);

        renderModuleCard(graphics, rightColX, startY + (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "💬 " + KiemLang.get("mod.chat_qol.title"),
                KiemLang.get("mod.chat_qol.desc"),
                false, mouseX, mouseY);

        renderModuleCard(graphics, rightColX, startY + 2 * (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "📜 " + KiemLang.get("mod.tooltip_qol.title"),
                KiemLang.get("mod.tooltip_qol.desc"),
                false, mouseX, mouseY);

        renderModuleCard(graphics, rightColX, startY + 3 * (cardH + cardGap), colW, cardH, badgeW, badgeH,
                "🤖 " + KiemLang.get("mod.anti_afk.title"),
                KiemLang.get("mod.anti_afk.desc") + (KiemConfig.antiAfkEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.antiAfkMinutes + "m / " + KiemConfig.antiAfkAction + "]" : " [" + KiemLang.get("ui.off") + "]"),
                KiemConfig.antiAfkEnabled, mouseX, mouseY);
    }

    private void renderModuleCard(GuiGraphicsExtractor graphics, int cx, int cy, int cw, int ch,
                                  int badgeW, int badgeH, String title, String desc, boolean enabled,
                                  int mouseX, int mouseY) {
        boolean hover = mouseX >= cx && mouseX <= cx + cw && mouseY >= cy && mouseY <= cy + ch;
        BoutiqueDraw.drawGlassCard(graphics, cx, cy, cw, ch, enabled, hover);
        int bX = cx + cw - badgeW - 10;
        int bY = cy + (ch - badgeH) / 2;
        int maxTextW = bX - (cx + 12) - 8;
        drawBoundedText(graphics, title, cx + 12, cy + Math.max(8, ch / 2 - 12), maxTextW, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, desc, cx + 12, cy + Math.max(22, ch / 2 + 2), maxTextW, enabled ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);
        BoutiqueDraw.drawPill(graphics, bX, bY, badgeW, badgeH, hover, hover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.customize"), bX + badgeW / 2, bY + 6,
                hover ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textAccent);
    }

    private void renderMiniModelsView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int colGap = 12;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;
        int rowH = 42;
        int rowGap = 6;

        int r0Y = y;
        BoutiqueDraw.drawGlassCard(graphics, leftColX, r0Y, colW, 36, KiemConfig.selfScaleEnabled, false);
        int toggleW = 56;
        int toggleH = 18;
        int t0X = leftColX + colW - toggleW - 10;
        int t0Y = r0Y + (36 - toggleH) / 2;
        int maxSelfTextW = t0X - (leftColX + 10) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.self_title"), leftColX + 10, r0Y + 8, maxSelfTextW, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.self_desc"), leftColX + 10, r0Y + 20, maxSelfTextW, BoutiqueDraw.theme().textDim);
        boolean t0Hover = mouseX >= t0X && mouseX <= t0X + toggleW && mouseY >= t0Y && mouseY <= t0Y + toggleH;
        drawToggleButton(graphics, t0X, t0Y, toggleW, toggleH, KiemConfig.selfScaleEnabled, t0Hover);

        int r1Y = r0Y + 36 + rowGap;
        drawCustomCheckbox(graphics, leftColX, r1Y, colW, 34, KiemLang.get("mod.mini_models.camera_title"), KiemLang.get("mod.mini_models.camera_desc"), KiemConfig.selfScaleCamera, mouseX, mouseY);

        int r2Y = r1Y + 34 + rowGap;
        drawCustomSlider(graphics, "self_x", leftColX, r2Y, colW, rowH, KiemLang.get("mod.mini_models.x_title"), KiemLang.get("mod.mini_models.x_sub"), KiemConfig.selfScaleX, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        int r3Y = r2Y + rowH + rowGap;
        drawCustomSlider(graphics, "self_y", leftColX, r3Y, colW, rowH, KiemLang.get("mod.mini_models.y_title"), KiemLang.get("mod.mini_models.y_sub"), KiemConfig.selfScaleY, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "self_z", leftColX, r4Y, colW, rowH, KiemLang.get("mod.mini_models.z_title"), KiemLang.get("mod.mini_models.z_sub"), KiemConfig.selfScaleZ, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        int r0RightH = 36 + 34 + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, rightColX, r0Y, colW, r0RightH, KiemConfig.othersScaleEnabled, false);
        int t1X = rightColX + colW - toggleW - 12;
        int t1Y = r0Y + (r0RightH - toggleH) / 2;
        int maxOthersTextW = t1X - (rightColX + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.others_title"), rightColX + 12, r0Y + 22, maxOthersTextW, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.others_desc"), rightColX + 12, r0Y + 38, maxOthersTextW, BoutiqueDraw.theme().textDim);
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.othersScaleEnabled, t1Hover);

        drawCustomSlider(graphics, "others_x", rightColX, r2Y, colW, rowH, KiemLang.get("mod.mini_models.x_title"), KiemLang.get("mod.mini_models.x_sub"), KiemConfig.othersScaleX, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);
        drawCustomSlider(graphics, "others_y", rightColX, r3Y, colW, rowH, KiemLang.get("mod.mini_models.y_title"), KiemLang.get("mod.mini_models.y_sub"), KiemConfig.othersScaleY, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);
        drawCustomSlider(graphics, "others_z", rightColX, r4Y, colW, rowH, KiemLang.get("mod.mini_models.z_title"), KiemLang.get("mod.mini_models.z_sub"), KiemConfig.othersScaleZ, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);
    }

    private void renderCustomViewmodelView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int colGap = 12;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;
        int rowH = 41;
        int rowGap = 5;

        int r1Y = y;
        drawCustomSlider(graphics, "vm_size", leftColX, r1Y, colW, rowH, KiemLang.get("mod.viewmodel.size_title"), KiemLang.get("mod.viewmodel.size_sub"), KiemConfig.itemSize, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        int r2Y = r1Y + rowH + rowGap;
        drawCustomCheckbox(graphics, leftColX, r2Y, colW, rowH, KiemLang.get("mod.viewmodel.scale_swing_title"), KiemLang.get("mod.viewmodel.scale_swing_sub"), KiemConfig.itemScaleSwing, mouseX, mouseY);

        int r3Y = r2Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_x", leftColX, r3Y, colW, rowH, KiemLang.get("mod.viewmodel.x_title"), KiemLang.get("mod.viewmodel.x_sub"), KiemConfig.itemPosX, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_y", leftColX, r4Y, colW, rowH, KiemLang.get("mod.viewmodel.y_title"), KiemLang.get("mod.viewmodel.y_sub"), KiemConfig.itemPosY, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        int r5Y = r4Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_z", leftColX, r5Y, colW, rowH, KiemLang.get("mod.viewmodel.z_title"), KiemLang.get("mod.viewmodel.z_sub"), KiemConfig.itemPosZ, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        drawCustomSlider(graphics, "vm_yaw", rightColX, r1Y, colW, rowH, KiemLang.get("mod.viewmodel.yaw_title"), KiemLang.get("mod.viewmodel.yaw_sub"), KiemConfig.itemYaw, -180.0f, 180.0f, "%.1f", mouseX, mouseY);
        drawCustomSlider(graphics, "vm_pitch", rightColX, r2Y, colW, rowH, KiemLang.get("mod.viewmodel.pitch_title"), KiemLang.get("mod.viewmodel.pitch_sub"), KiemConfig.itemPitch, -180.0f, 180.0f, "%.1f", mouseX, mouseY);
        drawCustomSlider(graphics, "vm_roll", rightColX, r3Y, colW, rowH, KiemLang.get("mod.viewmodel.roll_title"), KiemLang.get("mod.viewmodel.roll_sub"), KiemConfig.itemRoll, -180.0f, 180.0f, "%.1f", mouseX, mouseY);
        drawCustomSlider(graphics, "vm_speed", rightColX, r4Y, colW, rowH, KiemLang.get("mod.viewmodel.speed_title"), KiemLang.get("mod.viewmodel.speed_sub"), KiemConfig.swingSpeed, -5.00f, 1.00f, "%.2f", mouseX, mouseY);
        drawCustomCheckbox(graphics, rightColX, r5Y, colW, rowH, KiemLang.get("mod.viewmodel.noskip_title"), KiemLang.get("mod.viewmodel.noskip_sub"), KiemConfig.dontSkipAnimation, mouseX, mouseY);
    }

    private void renderBetterThirdPersonView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 38;
        int rowGap = 5;
        int toggleW = 56;
        int toggleH = 18;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.betterThirdPersonEnabled, false);
        int t1X = x + w - toggleW - 12;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        int maxR1W = t1X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.row1_title"), x + 12, r1Y + 8, maxR1W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.row1_desc"), x + 12, r1Y + 22, maxR1W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.betterThirdPersonEnabled,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, KiemConfig.shoulderSurfingEnabled, false);
        int t2X = x + w - toggleW - 12;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        int maxR2W = t2X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_title"), x + 12, r2Y + 8, maxR2W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_desc"), x + 12, r2Y + 22, maxR2W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.shoulderSurfingEnabled,
                mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH);

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, false, false);
        int keyBtnW = 74;
        int keyBtnX = x + w - keyBtnW - 12;
        int keyBtnY = r3Y + (rowH - toggleH) / 2;
        boolean keyH = mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        drawKeybindButton(graphics, keyBtnX, keyBtnY, keyBtnW, toggleH, KiemConfig.getKeyName(KiemConfig.shoulderSwapKey), listeningForShoulderKey, keyH);

        int pillW = 46;
        int pillGap = 4;
        int pillRightX = keyBtnX - pillW - 8;
        int pillLeftX = pillRightX - pillW - pillGap;
        boolean isRight = KiemConfig.shoulderSide > 0;
        boolean leftH = mouseX >= pillLeftX && mouseX <= pillLeftX + pillW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        boolean rightH = mouseX >= pillRightX && mouseX <= pillRightX + pillW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;

        int maxR3W = pillLeftX - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.swap_title"), x + 12, r3Y + 8, maxR3W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_desc"), x + 12, r3Y + 22, maxR3W, BoutiqueDraw.theme().textMuted);

        BoutiqueDraw.drawPill(graphics, pillLeftX, keyBtnY, pillW, toggleH, !isRight, leftH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.better_third_person.left"), pillLeftX + pillW / 2, keyBtnY + 5, !isRight ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textMuted);

        BoutiqueDraw.drawPill(graphics, pillRightX, keyBtnY, pillW, toggleH, isRight, rightH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.better_third_person.right"), pillRightX + pillW / 2, keyBtnY + 5, isRight ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textMuted);

        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "tp_smoothness", x, r4Y, w, rowH, KiemLang.get("mod.better_third_person.smooth_title"), KiemLang.get("mod.better_third_person.smooth_sub"), KiemConfig.thirdPersonSmoothness, 0.0f, 1.0f, "%.2f", mouseX, mouseY);

        int r5Y = r4Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r5Y, w, rowH, KiemConfig.thirdPersonSpeedLag, false);
        int t5X = x + w - toggleW - 12;
        int t5Y = r5Y + (rowH - toggleH) / 2;
        int maxR5W = t5X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.lag_title"), x + 12, r5Y + 8, maxR5W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.lag_desc"), x + 12, r5Y + 22, maxR5W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t5X, t5Y, toggleW, toggleH, KiemConfig.thirdPersonSpeedLag,
                mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH);

        int r6Y = r5Y + rowH + rowGap;
        drawCustomSlider(graphics, "tp_distance", x, r6Y, w, rowH, KiemLang.get("mod.better_third_person.dist_title"), KiemLang.get("mod.better_third_person.dist_sub"), KiemConfig.thirdPersonDistance, 2.0f, 8.0f, "%.1f", mouseX, mouseY);
    }

    private void renderTooltipQoLView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.tooltipScrollEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.scroll_title"), x + 14, r1Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.scroll_desc"), x + 14, r1Y + 28, BoutiqueDraw.theme().textMuted);
        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.tooltipScrollEnabled,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, KiemConfig.tooltipScaleEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.scale_title"), x + 14, r2Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.scale_desc"), x + 14, r2Y + 28, BoutiqueDraw.theme().textMuted);
        int t2X = x + w - toggleW - 14;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.tooltipScaleEnabled,
                mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH);

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, false, false);
        int currentPct = Math.round(KiemConfig.tooltipScale * 100.0f);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.current_scale") + currentPct + "%", x + 14, r3Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.tooltip_qol.scale_reset_desc"), x + 14, r3Y + 28, BoutiqueDraw.theme().textMuted);

        int resetBtnW = 90;
        int resetBtnX = x + w - resetBtnW - 14;
        int resetBtnY = r3Y + (rowH - toggleH) / 2;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + toggleH;
        BoutiqueDraw.drawPill(graphics, resetBtnX, resetBtnY, resetBtnW, toggleH, false, resetHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.tooltip_qol.reset_100"), resetBtnX + resetBtnW / 2, resetBtnY + 7,
                resetHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
    }

    private void renderChatQoLView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.infiniteChat, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.infinite_title"), x + 14, r1Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.infinite_desc"), x + 14, r1Y + 28, BoutiqueDraw.theme().textMuted);
        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.infiniteChat,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, KiemConfig.chatPeekEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.peek_title"), x + 14, r2Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.peek_desc"), x + 14, r2Y + 28, BoutiqueDraw.theme().textMuted);
        int t2X = x + w - toggleW - 14;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.chatPeekEnabled,
                mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH);

        int keyBtnW = 76;
        int keyBtnX = t2X - keyBtnW - 8;
        int keyBtnY = t2Y;
        drawKeybindButton(graphics, keyBtnX, keyBtnY, keyBtnW, toggleH, KiemConfig.getKeyName(KiemConfig.chatPeekKey), listeningForKey,
                mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH);

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, KiemConfig.compactMessages, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.compact_title"), x + 14, r3Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.compact_desc"), x + 14, r3Y + 28, BoutiqueDraw.theme().textMuted);
        int t3X = x + w - toggleW - 14;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.compactMessages,
                mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH);

        int r4Y = r3Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r4Y, w, rowH, KiemConfig.copyChatEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.copy_title"), x + 14, r4Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.chat_qol.copy_desc"), x + 14, r4Y + 28, BoutiqueDraw.theme().textMuted);
        int t4X = x + w - toggleW - 14;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.copyChatEnabled,
                mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH);
    }

    private void renderSmoothZoomView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 34;
        int rowGap = 4;
        int toggleW = 56;
        int toggleH = 18;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.zoomEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.row1_title"), x + 14, r1Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.row1_desc"), x + 14, r1Y + 19, BoutiqueDraw.theme().textMuted);
        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.zoomEnabled,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, false, false);
        String zoomStr = String.format(java.util.Locale.ROOT, "%.1fx", KiemConfig.zoomDefaultFactor);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.key_title") + zoomStr, x + 14, r2Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.key_desc"), x + 14, r2Y + 19, BoutiqueDraw.theme().textMuted);

        int keyBtnW = 76;
        int keyBtnX = x + w - keyBtnW - 14;
        int keyBtnY = r2Y + (rowH - toggleH) / 2;
        drawKeybindButton(graphics, keyBtnX, keyBtnY, keyBtnW, toggleH, KiemConfig.getKeyName(KiemConfig.zoomKey), listeningForZoomKey,
                mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH);

        int btnStepW = 20;
        int btnPlusX = keyBtnX - btnStepW - 8;
        int btnMinusX = btnPlusX - btnStepW - 4;
        boolean minusHover = mouseX >= btnMinusX && mouseX <= btnMinusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        boolean plusHover = mouseX >= btnPlusX && mouseX <= btnPlusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        BoutiqueDraw.drawPill(graphics, btnMinusX, keyBtnY, btnStepW, toggleH, false, minusHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "-", btnMinusX + btnStepW / 2, keyBtnY + 5, minusHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        BoutiqueDraw.drawPill(graphics, btnPlusX, keyBtnY, btnStepW, toggleH, false, plusHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "+", btnPlusX + btnStepW / 2, keyBtnY + 5, plusHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, KiemConfig.zoomScrollEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.scroll_title"), x + 14, r3Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.scroll_desc"), x + 14, r3Y + 19, BoutiqueDraw.theme().textMuted);
        int t3X = x + w - toggleW - 14;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.zoomScrollEnabled,
                mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH);

        int r4Y = r3Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r4Y, w, rowH, KiemConfig.zoomCinematicCamera, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.cinematic_title"), x + 14, r4Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.cinematic_desc"), x + 14, r4Y + 19, BoutiqueDraw.theme().textMuted);
        int t4X = x + w - toggleW - 14;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.zoomCinematicCamera,
                mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH);

        int r5Y = r4Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r5Y, w, rowH, KiemConfig.zoomCinematicBars, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.bars_title"), x + 14, r5Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.bars_desc"), x + 14, r5Y + 19, BoutiqueDraw.theme().textMuted);
        int t5X = x + w - toggleW - 14;
        int t5Y = r5Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t5X, t5Y, toggleW, toggleH, KiemConfig.zoomCinematicBars,
                mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH);

        int r6Y = r5Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r6Y, w, rowH, KiemConfig.cinematicBarsAlways, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.bars_always_title"), x + 14, r6Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.bars_always_desc"), x + 14, r6Y + 19, BoutiqueDraw.theme().textMuted);
        int t6X = x + w - toggleW - 14;
        int t6Y = r6Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t6X, t6Y, toggleW, toggleH, KiemConfig.cinematicBarsAlways,
                mouseX >= t6X && mouseX <= t6X + toggleW && mouseY >= t6Y && mouseY <= t6Y + toggleH);

        int r7Y = r6Y + rowH + rowGap;
        drawCustomSlider(graphics, "zoom_cinematic_size", x, r7Y, w, rowH, KiemLang.get("mod.smooth_zoom.bar_size_title"), KiemLang.get("mod.smooth_zoom.bar_size_desc"), KiemConfig.cinematicBarSize * 100.0f, 4.0f, 30.0f, "%.0f%%", mouseX, mouseY);

        int r8Y = r7Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r8Y, w, rowH, KiemConfig.zoomSensitivityScale, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.sens_title"), x + 14, r8Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.sens_desc"), x + 14, r8Y + 19, BoutiqueDraw.theme().textMuted);
        int t8X = x + w - toggleW - 14;
        int t8Y = r8Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t8X, t8Y, toggleW, toggleH, KiemConfig.zoomSensitivityScale,
                mouseX >= t8X && mouseX <= t8X + toggleW && mouseY >= t8Y && mouseY <= t8Y + toggleH);

        int r9Y = r8Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r9Y, w, rowH, false, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.speed_title"), x + 14, r9Y + 6, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.smooth_zoom.speed_desc"), x + 14, r9Y + 19, BoutiqueDraw.theme().textMuted);

        int resetBtnW = 60;
        int resetBtnX = x + w - resetBtnW - 14;
        int r9BtnsY = r9Y + (rowH - toggleH) / 2;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH;
        BoutiqueDraw.drawPill(graphics, resetBtnX, r9BtnsY, resetBtnW, toggleH, false, resetHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, r9BtnsY + 5,
                resetHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);

        int spW = 46;
        int spGap = 4;
        int sp3X = resetBtnX - spW - 8;
        int sp2X = sp3X - spW - spGap;
        int sp1X = sp2X - spW - spGap;

        boolean sp1Active = Math.abs(KiemConfig.zoomSpeed - 8.0) < 1.0;
        boolean sp2Active = Math.abs(KiemConfig.zoomSpeed - 16.0) < 1.0;
        boolean sp3Active = Math.abs(KiemConfig.zoomSpeed - 26.0) < 1.0;

        BoutiqueDraw.drawPill(graphics, sp1X, r9BtnsY, spW, toggleH, sp1Active, mouseX >= sp1X && mouseX <= sp1X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.smooth_zoom.slow"), sp1X + spW / 2, r9BtnsY + 5, sp1Active ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textMuted);

        BoutiqueDraw.drawPill(graphics, sp2X, r9BtnsY, spW, toggleH, sp2Active, mouseX >= sp2X && mouseX <= sp2X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.smooth_zoom.normal"), sp2X + spW / 2, r9BtnsY + 5, sp2Active ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textMuted);

        BoutiqueDraw.drawPill(graphics, sp3X, r9BtnsY, spW, toggleH, sp3Active, mouseX >= sp3X && mouseX <= sp3X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.smooth_zoom.fast"), sp3X + spW / 2, r9BtnsY + 5, sp3Active ? BoutiqueDraw.theme().textOnActivePill : BoutiqueDraw.theme().textMuted);
    }

    private void renderAfkCinematicsView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 40;
        int rowGap = 6;
        int toggleW = 56;
        int toggleH = 18;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.afkCinematicsEnabled, false);
        int t1X = x + w - toggleW - 12;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        int maxR1W = t1X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.row1_title"), x + 12, r1Y + 8, maxR1W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.row1_desc"), x + 12, r1Y + 22, maxR1W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.afkCinematicsEnabled,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, false, false);
        int pillW = 46;
        int pillGap = 5;
        int[] secOptions = {30, 60, 120, 300};
        String[] secLabels = {"30s", "1m", "2m", "5m"};
        int totalSecW = secOptions.length * pillW + (secOptions.length - 1) * pillGap;
        int pillsStartX = x + w - totalSecW - 12;
        int pillsY = r2Y + (rowH - toggleH) / 2;
        int maxR2W = pillsStartX - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.time_title"), x + 12, r2Y + 8, maxR2W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.time_desc"), x + 12, r2Y + 22, maxR2W, BoutiqueDraw.theme().textMuted);

        for (int i = 0; i < secOptions.length; i++) {
            int s = secOptions[i];
            int pX = pillsStartX + i * (pillW + pillGap);
            boolean active = (KiemConfig.afkCinematicsSeconds == s);
            boolean hover = mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH;
            BoutiqueDraw.drawPill(graphics, pX, pillsY, pillW, toggleH, active, hover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, secLabels[i], pX + pillW / 2, pillsY + 5,
                    active ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        }

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, KiemConfig.afkCinematicBars, false);
        int t3X = x + w - toggleW - 12;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        int maxR3W = t3X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.bars_title"), x + 12, r3Y + 8, maxR3W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.bars_desc"), x + 12, r3Y + 22, maxR3W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.afkCinematicBars,
                mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH);

        int r4Y = r3Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r4Y, w, rowH, KiemConfig.afkRaycastEnabled, false);
        int t4X = x + w - toggleW - 12;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        int maxR4W = t4X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.raycast_title"), x + 12, r4Y + 8, maxR4W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.raycast_desc"), x + 12, r4Y + 22, maxR4W, BoutiqueDraw.theme().textMuted);
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.afkRaycastEnabled,
                mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH);

        int r5Y = r4Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r5Y, w, rowH, false, false);
        int testBtnW = 120;
        int testBtnX = x + w - testBtnW - 12;
        int testBtnY = r5Y + (rowH - 22) / 2;
        int maxR5W = testBtnX - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.test_title"), x + 12, r5Y + 8, maxR5W, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.test_desc"), x + 12, r5Y + 22, maxR5W, BoutiqueDraw.theme().textMuted);

        boolean testHover = mouseX >= testBtnX && mouseX <= testBtnX + testBtnW && mouseY >= testBtnY && mouseY <= testBtnY + 22;
        BoutiqueDraw.drawPill(graphics, testBtnX, testBtnY, testBtnW, 22, true, testHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("mod.afk_cinematics.test_btn"), testBtnX + testBtnW / 2, testBtnY + 7, BoutiqueDraw.theme().textOnActivePill);
    }

    private void renderAntiAfkView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        int r1Y = y;
        BoutiqueDraw.drawGlassCard(graphics, x, r1Y, w, rowH, KiemConfig.antiAfkEnabled, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.row1_title"), x + 14, r1Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.row1_desc"), x + 14, r1Y + 28, BoutiqueDraw.theme().textMuted);
        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.antiAfkEnabled,
                mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH);

        int r2Y = r1Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r2Y, w, rowH, false, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.interval_title"), x + 14, r2Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.interval_desc"), x + 14, r2Y + 28, BoutiqueDraw.theme().textMuted);

        int pillW = 42;
        int pillGap = 5;
        int[] mins = {1, 2, 3, 5, 10};
        int totalPillsW = mins.length * pillW + (mins.length - 1) * pillGap;
        int pillsStartX = x + w - totalPillsW - 14;
        int pillsY = r2Y + (rowH - toggleH) / 2;

        for (int i = 0; i < mins.length; i++) {
            int m = mins[i];
            int pX = pillsStartX + i * (pillW + pillGap);
            boolean active = (KiemConfig.antiAfkMinutes == m);
            boolean hover = mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH;
            BoutiqueDraw.drawPill(graphics, pX, pillsY, pillW, toggleH, active, hover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, m + " " + KiemLang.get("mod.anti_afk.min"), pX + pillW / 2, pillsY + 7,
                    active ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        }

        int r3Y = r2Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r3Y, w, rowH, false, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.action_title"), x + 14, r3Y + 12, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.action_desc"), x + 14, r3Y + 28, BoutiqueDraw.theme().textMuted);

        String[] actions = {"SHIFT", "JUMP", "CLICK"};
        String[] actionLabels = {"Shift", "Jump", "Click"};
        int actW = 54;
        int actGap = 6;
        int totalActsW = actions.length * actW + (actions.length - 1) * actGap;
        int actsStartX = x + w - totalActsW - 14;
        int actsY = r3Y + (rowH - toggleH) / 2;

        for (int i = 0; i < actions.length; i++) {
            String act = actions[i];
            String lbl = actionLabels[i];
            int aX = actsStartX + i * (actW + actGap);
            boolean active = act.equalsIgnoreCase(KiemConfig.antiAfkAction);
            boolean hover = mouseX >= aX && mouseX <= aX + actW && mouseY >= actsY && mouseY <= actsY + toggleH;
            BoutiqueDraw.drawPill(graphics, aX, actsY, actW, toggleH, active, hover);
            BoutiqueDraw.drawCenteredText(graphics, this.font, lbl, aX + actW / 2, actsY + 7,
                    active ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
        }

        int r4Y = r3Y + rowH + rowGap;
        BoutiqueDraw.drawGlassCard(graphics, x, r4Y, w, rowH, false, false);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get("mod.anti_afk.status_title"), x + 14, r4Y + 12, BoutiqueDraw.theme().textPrimary);
        String infoStr;
        if ("en".equalsIgnoreCase(KiemConfig.language)) {
            infoStr = KiemConfig.antiAfkEnabled ? "Active: Triggers " + KiemConfig.antiAfkAction + " every " + KiemConfig.antiAfkMinutes + " min." : "Disabled: No automatic action is performed.";
        } else {
            infoStr = KiemConfig.antiAfkEnabled ? "Aktif: Her " + KiemConfig.antiAfkMinutes + " dakikada bir " + KiemConfig.antiAfkAction + " tetikleniyor." : "Devre dışı: Otomatik eylem yürütülmüyor.";
        }
        BoutiqueDraw.drawText(graphics, this.font, infoStr, x + 14, r4Y + 28, KiemConfig.antiAfkEnabled ? BoutiqueDraw.theme().textAccent : BoutiqueDraw.theme().textMuted);

        int resetW = 76;
        int resetX = x + w - resetW - 14;
        int resetY = r4Y + (rowH - toggleH) / 2;
        boolean resetH = mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + toggleH;
        BoutiqueDraw.drawPill(graphics, resetX, resetY, resetW, toggleH, false, resetH);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.reset"), resetX + resetW / 2, resetY + 7,
                resetH ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
    }

    // ==================== SLIDERS, CHECKBOXES, BUTTONS, MODALS ====================

    private void drawCustomSlider(
            GuiGraphicsExtractor graphics,
            String id,
            int x, int y, int w, int h,
            String title, String subtitle,
            float value, float min, float max,
            String format,
            double mouseX, double mouseY
    ) {
        BoutiqueDraw.drawGlassCard(graphics, x, y, w, h, false, false);

        int trackW = 76;
        int trackH = 4;
        int trackX = x + w - trackW - 14;
        int trackY = y + 14;

        String valStr = String.format(java.util.Locale.ROOT, format, value);
        int valW = BoutiqueDraw.textWidth(this.font, "[ " + valStr + " ]");
        int maxTitleW = trackX - valW - 12 - (x + 10);
        drawBoundedText(graphics, title, x + 10, y + 8, maxTitleW, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, "[ " + valStr + " ]", trackX - valW - 8, y + 8, BoutiqueDraw.theme().textAccent);

        if (subtitle != null && !subtitle.isEmpty()) {
            drawBoundedText(graphics, subtitle, x + 10, y + 23, trackX - (x + 10) - 8, BoutiqueDraw.theme().textDim);
        }

        String minStr = String.format(java.util.Locale.ROOT, format, min);
        String maxStr = String.format(java.util.Locale.ROOT, format, max);
        BoutiqueDraw.drawText(graphics, this.font, minStr, trackX, trackY + 8, BoutiqueDraw.theme().textMuted);
        int maxW = BoutiqueDraw.textWidth(this.font, maxStr);
        BoutiqueDraw.drawText(graphics, this.font, maxStr, trackX + trackW - maxW, trackY + 8, BoutiqueDraw.theme().textMuted);

        graphics.fill(trackX, trackY, trackX + trackW, trackY + trackH, 0x44FFFFFF);

        float pct = (value - min) / (max - min);
        pct = net.minecraft.util.Mth.clamp(pct, 0.0f, 1.0f);
        int fillW = (int) (pct * trackW);
        graphics.fill(trackX, trackY, trackX + fillW, trackY + trackH, BoutiqueDraw.theme().textAccent);

        int thumbW = 8;
        int thumbH = 12;
        int thumbX = trackX + fillW - thumbW / 2;
        int thumbY = trackY + trackH / 2 - thumbH / 2;
        boolean isDragging = id.equals(draggingSlider);
        boolean thumbHover = mouseX >= trackX - 4 && mouseX <= trackX + trackW + 4 && mouseY >= trackY - 8 && mouseY <= trackY + trackH + 8;
        graphics.fill(thumbX, thumbY, thumbX + thumbW, thumbY + thumbH, (thumbHover || isDragging) ? 0xFFFFFFFF : BoutiqueDraw.theme().textAccent);
    }

    private void drawCustomCheckbox(
            GuiGraphicsExtractor graphics,
            int x, int y, int w, int h,
            String title, String subtitle,
            boolean checked,
            double mouseX, double mouseY
    ) {
        BoutiqueDraw.drawGlassCard(graphics, x, y, w, h, checked, false);
        int boxSize = 18;
        int boxX = x + w - boxSize - 14;
        int maxCheckTextW = boxX - (x + 10) - 6;

        drawBoundedText(graphics, title, x + 10, y + 8, maxCheckTextW, BoutiqueDraw.theme().textPrimary);
        if (subtitle != null && !subtitle.isEmpty()) {
            drawBoundedText(graphics, subtitle, x + 10, y + 22, maxCheckTextW, BoutiqueDraw.theme().textDim);
        }
        int boxY = y + (h - boxSize) / 2;
        boolean hover = mouseX >= boxX && mouseX <= boxX + boxSize && mouseY >= boxY && mouseY <= boxY + boxSize;
        BoutiqueDraw.drawPill(graphics, boxX, boxY, boxSize, boxSize, checked, hover);
        if (checked) {
            BoutiqueDraw.drawCenteredText(graphics, this.font, "✓", boxX + boxSize / 2, boxY + 5, BoutiqueDraw.theme().textOnActivePill);
        }
    }

    private void setSliderValue(String id, float val) {
        switch (id) {
            case "self_x" -> { KiemConfig.selfScaleX = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }
            case "self_y" -> { KiemConfig.selfScaleY = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }
            case "self_z" -> { KiemConfig.selfScaleZ = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }
            case "others_x" -> { KiemConfig.othersScaleX = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }
            case "others_y" -> { KiemConfig.othersScaleY = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }
            case "others_z" -> { KiemConfig.othersScaleZ = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("mini_models"); }

            case "vm_size" -> { KiemConfig.itemSize = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_x" -> { KiemConfig.itemPosX = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_y" -> { KiemConfig.itemPosY = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_z" -> { KiemConfig.itemPosZ = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_yaw" -> { KiemConfig.itemYaw = Math.round(val * 10.0f) / 10.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_pitch" -> { KiemConfig.itemPitch = Math.round(val * 10.0f) / 10.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_roll" -> { KiemConfig.itemRoll = Math.round(val * 10.0f) / 10.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "vm_speed" -> { KiemConfig.swingSpeed = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("custom_viewmodel"); }
            case "zoom_cinematic_size" -> { KiemConfig.cinematicBarSize = Math.round(val) / 100.0f; KiemConfig.touchModule("smooth_zoom"); }
            case "tp_smoothness" -> { KiemConfig.thirdPersonSmoothness = Math.round(val * 100.0f) / 100.0f; KiemConfig.touchModule("better_third_person"); }
            case "tp_distance" -> { KiemConfig.thirdPersonDistance = Math.round(val * 10.0f) / 10.0f; KiemConfig.touchModule("better_third_person"); }
        }
    }

    private boolean checkSliderClick(String id, int x, int y, int w, int h, float min, float max, double mouseX, double mouseY) {
        int trackW = 76;
        int trackX = x + w - trackW - 14;
        if (mouseX >= trackX - 8 && mouseX <= trackX + trackW + 8 && mouseY >= y && mouseY <= y + h) {
            draggingSlider = id;
            sliderTrackX = trackX;
            sliderTrackW = trackW;
            sliderMin = min;
            sliderMax = max;
            float pct = (float) ((mouseX - trackX) / (double) trackW);
            pct = net.minecraft.util.Mth.clamp(pct, 0.0f, 1.0f);
            float val = min + pct * (max - min);
            setSliderValue(id, val);
            return true;
        }
        return false;
    }

    private static class RecentModuleMeta {
        final String title;
        final String desc;
        final Tab tab;
        final ConfigSubView configSub;

        RecentModuleMeta(String title, String desc, Tab tab, ConfigSubView configSub) {
            this.title = title;
            this.desc = desc;
            this.tab = tab;
            this.configSub = configSub;
        }
    }

    private RecentModuleMeta getModuleMeta(String moduleId) {
        if ("afk_cinematics".equals(moduleId)) {
            return new RecentModuleMeta("🎬 " + KiemLang.get("mod.afk_cinematics.title"), KiemLang.get("mod.afk_cinematics.desc"), Tab.CONFIG, ConfigSubView.AFK_CINEMATICS);
        } else if ("better_third_person".equals(moduleId)) {
            return new RecentModuleMeta("🎥 " + KiemLang.get("mod.better_third_person.title"), KiemLang.get("mod.better_third_person.desc"), Tab.CONFIG, ConfigSubView.BETTER_THIRD_PERSON);
        } else if ("custom_viewmodel".equals(moduleId)) {
            return new RecentModuleMeta("🗡 " + KiemLang.get("mod.viewmodel.title"), KiemLang.get("mod.viewmodel.desc"), Tab.CONFIG, ConfigSubView.CUSTOM_VIEWMODEL);
        } else if ("mini_models".equals(moduleId)) {
            return new RecentModuleMeta("🧍 " + KiemLang.get("mod.mini_models.title"), KiemLang.get("mod.mini_models.desc"), Tab.CONFIG, ConfigSubView.MINI_MODELS);
        } else if ("smooth_zoom".equals(moduleId)) {
            return new RecentModuleMeta("🔍 " + KiemLang.get("mod.smooth_zoom.title"), KiemLang.get("mod.smooth_zoom.desc"), Tab.CONFIG, ConfigSubView.SMOOTH_ZOOM);
        } else if ("chat_qol".equals(moduleId)) {
            return new RecentModuleMeta("💬 " + KiemLang.get("mod.chat_qol.title"), KiemLang.get("mod.chat_qol.desc"), Tab.CONFIG, ConfigSubView.CHAT_QOL);
        } else if ("tooltip_qol".equals(moduleId)) {
            return new RecentModuleMeta("📜 " + KiemLang.get("mod.tooltip_qol.title"), KiemLang.get("mod.tooltip_qol.desc"), Tab.CONFIG, ConfigSubView.TOOLTIP_QOL);
        } else if ("anti_afk".equals(moduleId)) {
            return new RecentModuleMeta("🤖 " + KiemLang.get("mod.anti_afk.title"), KiemLang.get("mod.anti_afk.desc"), Tab.CONFIG, ConfigSubView.ANTI_AFK);
        } else if ("cosmetics".equals(moduleId)) {
            return new RecentModuleMeta("✨ " + KiemLang.get("mod.cosmetics.title"), KiemLang.get("mod.cosmetics.desc"), Tab.COSMETICS, ConfigSubView.MAIN);
        }
        return new RecentModuleMeta("⚡ " + moduleId, "Kiem Addons module.", Tab.CONFIG, ConfigSubView.MAIN);
    }

    // ==================== DEV MENU TAB (REMOVABLE) ====================

    private void renderDevTab(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        BoutiqueDraw.Theme theme = BoutiqueDraw.theme();
        BoutiqueDraw.FontChoice activeFont = BoutiqueDraw.fontChoice();
        BoutiqueDraw.drawGlassPanel(graphics, x, y, w, h);

        int padX = 14;
        int innerW = w - padX * 2;
        int curY = y + 12;

        // 1. Themes (2x3 grid of BoutiqueDraw.Theme.values())
        String themeHeader = "THEMES";
        BoutiqueDraw.drawText(graphics, this.font, themeHeader, x + padX, curY, theme.textAccent);
        int thW = BoutiqueDraw.textWidth(this.font, themeHeader);
        graphics.fill(x + padX + thW + 8, curY + 4, x + w - padX, curY + 5, theme.panelBorder);
        curY += 16;

        BoutiqueDraw.Theme[] themes = BoutiqueDraw.Theme.values();
        int tCols = 4;
        int tGapX = 8;
        int tGapY = 8;
        int tCardW = (innerW - tGapX * (tCols - 1)) / tCols;
        int tCardH = 38;
        for (int i = 0; i < themes.length; i++) {
            BoutiqueDraw.Theme th = themes[i];
            int col = i % tCols;
            int row = i / tCols;
            int cx = x + padX + col * (tCardW + tGapX);
            int cy = curY + row * (tCardH + tGapY);

            boolean active = (theme == th);
            boolean hover = mouseX >= cx && mouseX <= cx + tCardW && mouseY >= cy && mouseY <= cy + tCardH;
            BoutiqueDraw.drawGlassCard(graphics, cx, cy, tCardW, tCardH, active, hover);

            // Swatch box
            int swX = cx + 10;
            int swY = cy + (tCardH - 20) / 2;
            graphics.fill(swX - 1, swY - 1, swX + 25, swY + 21, th.panelBorder);
            graphics.fill(swX, swY, swX + 12, swY + 20, th.panelTop);
            graphics.fill(swX + 12, swY, swX + 24, swY + 20, th.pillActiveFill);

            int tx = swX + 32;
            int maxTW = tCardW - 48;
            drawBoundedText(graphics, th.displayName, tx, cy + 7, maxTW, active ? theme.textAccent : theme.textPrimary);
            String modeTag = th.isLight ? "(Light)" : "(Dark)";
            drawBoundedText(graphics, modeTag, tx, cy + 21, maxTW, theme.textMuted);
        }

        int tRows = (themes.length + tCols - 1) / tCols;
        curY += tRows * tCardH + (tRows - 1) * tGapY + 14;

        // 2. Fonts (grid of BoutiqueDraw.FontChoice.values())
        String fontHeader = "FONTS";
        BoutiqueDraw.drawText(graphics, this.font, fontHeader, x + padX, curY, theme.textAccent);
        int fhW = BoutiqueDraw.textWidth(this.font, fontHeader);
        graphics.fill(x + padX + fhW + 8, curY + 4, x + w - padX, curY + 5, theme.panelBorder);
        curY += 16;

        BoutiqueDraw.FontChoice[] fonts = BoutiqueDraw.FontChoice.values();
        int fCols = 2;
        int fGapX = 8;
        int fGapY = 6;
        int fPillW = (innerW - fGapX * (fCols - 1)) / fCols;
        int fPillH = 22;
        for (int i = 0; i < fonts.length; i++) {
            BoutiqueDraw.FontChoice fc = fonts[i];
            int col = i % fCols;
            int row = i / fCols;
            int fx = x + padX + col * (fPillW + fGapX);
            int fy = curY + row * (fPillH + fGapY);

            boolean active = (activeFont == fc);
            boolean hover = mouseX >= fx && mouseX <= fx + fPillW && mouseY >= fy && mouseY <= fy + fPillH;
            BoutiqueDraw.drawPill(graphics, fx, fy, fPillW, fPillH, active, hover);

            String label = BoutiqueDraw.fitText(this.font, fc.displayName, fPillW - 14);
            int txtCol = active ? theme.textOnActivePill : (hover ? theme.textPrimary : theme.textMuted);
            BoutiqueDraw.drawCenteredText(graphics, this.font, label, fx + fPillW / 2, fy + 7, txtCol);
        }

        int fRows = (fonts.length + fCols - 1) / fCols;
        curY += fRows * fPillH + (fRows - 1) * fGapY + 14;

        // 3. Window & Backdrop Mode
        String winHeader = "WINDOW & BACKDROP";
        BoutiqueDraw.drawText(graphics, this.font, winHeader, x + padX, curY, theme.textAccent);
        int whW = BoutiqueDraw.textWidth(this.font, winHeader);
        graphics.fill(x + padX + whW + 8, curY + 4, x + w - padX, curY + 5, theme.panelBorder);
        curY += 16;

        int wGap = 6;
        int wBtnW = (innerW - wGap * 3) / 4;
        int wBtnH = 24;
        int modalX = x + padX;
        int fullX = modalX + wBtnW + wGap;
        int tintX = fullX + wBtnW + wGap;
        int mascotX = tintX + wBtnW + wGap;

        boolean modalActive = !KiemConfig.uiFullscreen;
        boolean modalHover = mouseX >= modalX && mouseX <= modalX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH;
        BoutiqueDraw.drawPill(graphics, modalX, curY, wBtnW, wBtnH, modalActive, modalHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "Centered Modal", modalX + wBtnW / 2, curY + 8,
                modalActive ? theme.textOnActivePill : (modalHover ? theme.textPrimary : theme.textMuted));

        boolean fullActive = KiemConfig.uiFullscreen;
        boolean fullHover = mouseX >= fullX && mouseX <= fullX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH;
        BoutiqueDraw.drawPill(graphics, fullX, curY, wBtnW, wBtnH, fullActive, fullHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "Fullscreen", fullX + wBtnW / 2, curY + 8,
                fullActive ? theme.textOnActivePill : (fullHover ? theme.textPrimary : theme.textMuted));

        boolean tintActive = KiemConfig.uiBackdropTint;
        boolean tintHover = mouseX >= tintX && mouseX <= tintX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH;
        BoutiqueDraw.drawPill(graphics, tintX, curY, wBtnW, wBtnH, tintActive, tintHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "Tint: " + (tintActive ? "ON" : "OFF"),
                tintX + wBtnW / 2, curY + 8,
                tintActive ? theme.textOnActivePill : (tintHover ? theme.textPrimary : theme.textMuted));

        boolean mascotActive = KiemConfig.uiShowMascot;
        boolean mascotHover = mouseX >= mascotX && mouseX <= mascotX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH;
        BoutiqueDraw.drawPill(graphics, mascotX, curY, wBtnW, wBtnH, mascotActive, mascotHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, "Mascot: " + (mascotActive ? "ON" : "OFF"),
                mascotX + wBtnW / 2, curY + 8,
                mascotActive ? theme.textOnActivePill : (mascotHover ? theme.textPrimary : theme.textMuted));
    }

    private boolean handleDevClick(int x, int y, int w, int h, double mouseX, double mouseY) {
        int padX = 14;
        int innerW = w - padX * 2;
        int curY = y + 12 + 16;

        // 1. Themes
        BoutiqueDraw.Theme[] themes = BoutiqueDraw.Theme.values();
        int tCols = 4;
        int tGapX = 8;
        int tGapY = 8;
        int tCardW = (innerW - tGapX * (tCols - 1)) / tCols;
        int tCardH = 38;
        for (int i = 0; i < themes.length; i++) {
            int col = i % tCols;
            int row = i / tCols;
            int cx = x + padX + col * (tCardW + tGapX);
            int cy = curY + row * (tCardH + tGapY);
            if (mouseX >= cx && mouseX <= cx + tCardW && mouseY >= cy && mouseY <= cy + tCardH) {
                KiemConfig.uiTheme = themes[i].id;
                KiemConfig.save();
                return true;
            }
        }

        int tRows = (themes.length + tCols - 1) / tCols;
        curY += tRows * tCardH + (tRows - 1) * tGapY + 14 + 16;

        // 2. Fonts
        BoutiqueDraw.FontChoice[] fonts = BoutiqueDraw.FontChoice.values();
        int fCols = 2;
        int fGapX = 8;
        int fGapY = 6;
        int fPillW = (innerW - fGapX * (fCols - 1)) / fCols;
        int fPillH = 22;
        for (int i = 0; i < fonts.length; i++) {
            int col = i % fCols;
            int row = i / fCols;
            int fx = x + padX + col * (fPillW + fGapX);
            int fy = curY + row * (fPillH + fGapY);
            if (mouseX >= fx && mouseX <= fx + fPillW && mouseY >= fy && mouseY <= fy + fPillH) {
                KiemConfig.uiFont = fonts[i].id;
                KiemConfig.save();
                return true;
            }
        }

        int fRows = (fonts.length + fCols - 1) / fCols;
        curY += fRows * fPillH + (fRows - 1) * fGapY + 14 + 16;

        // 3. Window & Backdrop Mode
        int wGap = 6;
        int wBtnW = (innerW - wGap * 3) / 4;
        int wBtnH = 24;
        int modalX = x + padX;
        int fullX = modalX + wBtnW + wGap;
        int tintX = fullX + wBtnW + wGap;
        int mascotX = tintX + wBtnW + wGap;

        if (mouseX >= modalX && mouseX <= modalX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH) {
            KiemConfig.uiFullscreen = false;
            KiemConfig.save();
            return true;
        }
        if (mouseX >= fullX && mouseX <= fullX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH) {
            KiemConfig.uiFullscreen = true;
            KiemConfig.save();
            return true;
        }
        if (mouseX >= tintX && mouseX <= tintX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH) {
            KiemConfig.uiBackdropTint = !KiemConfig.uiBackdropTint;
            KiemConfig.save();
            return true;
        }
        if (mouseX >= mascotX && mouseX <= mascotX + wBtnW && mouseY >= curY && mouseY <= curY + wBtnH) {
            KiemConfig.uiShowMascot = !KiemConfig.uiShowMascot;
            KiemConfig.save();
            return true;
        }

        return false;
    }

    private int getMascotX() {
        int range = Math.max(0, this.width - MASCOT_SIZE - 8);
        return Math.round(net.minecraft.util.Mth.clamp(KiemConfig.uiMascotX, 0.0f, 1.0f) * range) + 4;
    }

    private int getMascotY() {
        int range = Math.max(0, this.height - MASCOT_SIZE - 4);
        return Math.round(net.minecraft.util.Mth.clamp(KiemConfig.uiMascotY, 0.0f, 1.0f) * range) + 2;
    }

    private boolean isOverMascot(double mx, double my) {
        if (!KiemConfig.uiShowMascot) return false;
        int rx = getMascotX();
        int ry = getMascotY();
        return mx >= rx + 18 && mx <= rx + MASCOT_SIZE - 14 && my >= ry + 6 && my <= ry + MASCOT_SIZE;
    }

    private void renderRikkaMascot(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int size = MASCOT_SIZE;
        int rx = getMascotX();
        int ry = getMascotY();

        double t = System.currentTimeMillis() / 1000.0;
        float breathY = (float) (Math.sin(t * 2.2) * 0.4);

        float legPhase = (float) Math.sin(t * 4.0);
        float legLAngle = legPhase * 0.15f;
        float legRAngle = -legPhase * 0.15f;
        float legLY = legPhase * 1.2f;
        float legRY = -legPhase * 1.2f;

        float headAngle = (float) Math.sin(t * 2.0) * 0.11f;

        drawMascotPart(graphics, RIKKA_MASCOT_LEG_L, rx, ry + breathY + legLY, size, 96f / 256f, 182f / 256f, legLAngle);
        drawMascotPart(graphics, RIKKA_MASCOT_LEG_R, rx, ry + breathY + legRY, size, 126f / 256f, 182f / 256f, legRAngle);
        drawMascotPart(graphics, RIKKA_MASCOT_BODY, rx, ry + breathY, size, 126f / 256f, 150f / 256f, 0.0f);
        drawMascotPart(graphics, RIKKA_MASCOT_HEAD, rx, ry + breathY, size, 126f / 256f, 115f / 256f, headAngle);
    }

    private void drawMascotPart(GuiGraphicsExtractor graphics, Identifier tex, float x, float y, int size,
                                float pivotNormX, float pivotNormY, float angleRad) {
        var pose = graphics.pose();
        pose.pushMatrix();
        float px = x + size * pivotNormX;
        float py = y + size * pivotNormY;
        pose.translate(px, py);
        if (angleRad != 0.0f) {
            pose.rotate(angleRad);
        }
        pose.translate(-size * pivotNormX, -size * pivotNormY);
        graphics.blit(tex, 0, 0, size, size, 0.0f, 1.0f, 0.0f, 1.0f);
        pose.popMatrix();
    }

    private void resetWardrobeExceptPetsAndTrails() {
        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
        KiemConfig.cosmeticWing = "NONE";
        KiemConfig.cosmeticHat = "NONE";
        KiemConfig.cosmeticNeck = "NONE";
        KiemConfig.cosmeticHand = "NONE";
        KiemConfig.cosmeticWaist = "NONE";
        KiemConfig.cosmeticFoot = "NONE";
        KiemConfig.cosmeticAdjustments.clear();
        cosmeticCustomizeOpen = false;
        KiemConfig.save();
    }

    private void renderResetConfirmModal(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.fill(0, 0, this.width, this.height, 0xB0000000);

        int popW = 320;
        int popH = 135;
        int popX = (this.width - popW) / 2;
        int popY = (this.height - popH) / 2;

        BoutiqueDraw.drawGlassPanel(graphics, popX, popY, popW, popH);

        String keyPrefix = pendingResetWardrobe ? "reset_wardrobe_modal." : "reset_modal.";
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get(keyPrefix + "title"), popX + 16, popY + 16, 0xFFFF8899);
        graphics.fill(popX + 16, popY + 30, popX + popW - 16, popY + 31, BoutiqueDraw.theme().panelBorder);

        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get(keyPrefix + "desc1"), popX + 16, popY + 40, BoutiqueDraw.theme().textPrimary);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get(keyPrefix + "desc2"), popX + 16, popY + 54, BoutiqueDraw.theme().textMuted);
        BoutiqueDraw.drawText(graphics, this.font, KiemLang.get(keyPrefix + "desc3"), popX + 16, popY + 68, BoutiqueDraw.theme().textDim);

        int btnW = 90;
        int btnH = 24;
        int cancelX = popX + popW - 16 - btnW;
        int confirmX = cancelX - 10 - btnW;
        int btnY = popY + popH - 36;

        boolean confirmHover = mouseX >= confirmX && mouseX <= confirmX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean cancelHover = mouseX >= cancelX && mouseX <= cancelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        BoutiqueDraw.drawPill(graphics, confirmX, btnY, btnW, btnH, true, confirmHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.confirm"), confirmX + btnW / 2, btnY + 8, BoutiqueDraw.theme().textOnActivePill);

        BoutiqueDraw.drawPill(graphics, cancelX, btnY, btnW, btnH, false, cancelHover);
        BoutiqueDraw.drawCenteredText(graphics, this.font, KiemLang.get("ui.cancel"), cancelX + btnW / 2, btnY + 8,
                cancelHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
    }

    private void renderLanguageSelectModal(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.fill(0, 0, this.width, this.height, 0xD0000000);

        int popW = 400;
        int popH = 154;
        int popX = (this.width - popW) / 2;
        int popY = (this.height - popH) / 2;

        BoutiqueDraw.drawGlassPanel(graphics, popX, popY, popW, popH);

        BoutiqueDraw.drawText(graphics, this.font, "✦ " + KiemLang.get("lang.modal_title"), popX + 16, popY + 16, BoutiqueDraw.theme().textAccent);
        graphics.fill(popX + 16, popY + 30, popX + popW - 16, popY + 31, BoutiqueDraw.theme().panelBorder);

        drawBoundedText(graphics, KiemLang.get("lang.modal_desc"), popX + 16, popY + 40, popW - 32, BoutiqueDraw.theme().textPrimary);
        drawBoundedText(graphics, KiemLang.get("lang.modal_desc2"), popX + 16, popY + 56, popW - 32, BoutiqueDraw.theme().textMuted);

        int btnW = 160;
        int btnH = 32;
        int trBtnX = popX + 24;
        int enBtnX = popX + popW - 24 - btnW;
        int btnY = popY + popH - 46;

        boolean trHover = mouseX >= trBtnX && mouseX <= trBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean enHover = mouseX >= enBtnX && mouseX <= enBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        boolean isTrActive = "tr".equalsIgnoreCase(KiemConfig.language);
        boolean isEnActive = "en".equalsIgnoreCase(KiemConfig.language);

        BoutiqueDraw.drawPill(graphics, trBtnX, btnY, btnW, btnH, isTrActive, trHover);
        drawFlagTR(graphics, trBtnX + 22, btnY + 10);
        BoutiqueDraw.drawText(graphics, this.font, "Türkçe (TR)", trBtnX + 46, btnY + 12,
                isTrActive ? BoutiqueDraw.theme().textOnActivePill : (trHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));

        BoutiqueDraw.drawPill(graphics, enBtnX, btnY, btnW, btnH, isEnActive, enHover);
        drawFlagUK(graphics, enBtnX + 22, btnY + 10);
        BoutiqueDraw.drawText(graphics, this.font, "English (EN)", enBtnX + 46, btnY + 12,
                isEnActive ? BoutiqueDraw.theme().textOnActivePill : (enHover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted));
    }

    private void drawToggleButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean active, boolean hover) {
        BoutiqueDraw.drawPill(graphics, x, y, w, h, active, hover);
        String label = active ? KiemLang.get("ui.on") : KiemLang.get("ui.off");
        int textColor = active ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textMuted);
        BoutiqueDraw.drawCenteredText(graphics, this.font, label, x + w / 2, y + (h - 8) / 2, textColor);
    }

    private void drawKeybindButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, String keyText, boolean listening, boolean hover) {
        BoutiqueDraw.drawPill(graphics, x, y, w, h, listening, hover);
        String text = listening ? KiemLang.get("ui.press_key") : "[ " + keyText + " ]";
        int textColor = listening ? BoutiqueDraw.theme().textOnActivePill : (hover ? BoutiqueDraw.theme().textPrimary : BoutiqueDraw.theme().textAccent);
        BoutiqueDraw.drawCenteredText(graphics, this.font, text, x + w / 2, y + (h - 8) / 2, textColor);
    }

    private void drawBoundedText(GuiGraphicsExtractor graphics, String text, int x, int y, int maxWidth, int color) {
        if (text == null || text.isEmpty() || maxWidth <= 0) return;
        String fitted = BoutiqueDraw.fitText(this.font, text, maxWidth);
        if (!fitted.isEmpty()) {
            BoutiqueDraw.drawText(graphics, this.font, fitted, x, y, color);
        }
    }

    private void drawFlagTR(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 16, y + 11, 0xFFE30A17);
        graphics.fill(x, y, x + 16, y + 1, 0x55000000);
        graphics.fill(x, y + 10, x + 16, y + 11, 0x55000000);
        graphics.fill(x, y, x + 1, y + 11, 0x55000000);
        graphics.fill(x + 15, y, x + 16, y + 11, 0x55000000);
        graphics.fill(x + 3, y + 2, x + 8, y + 9, 0xFFFFFFFF);
        graphics.fill(x + 5, y + 3, x + 9, y + 8, 0xFFE30A17);
        graphics.fill(x + 9, y + 4, x + 12, y + 7, 0xFFFFFFFF);
        graphics.fill(x + 10, y + 3, x + 11, y + 8, 0xFFFFFFFF);
    }

    private void drawFlagUK(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 16, y + 11, 0xFF00247D);
        graphics.fill(x, y, x + 16, y + 1, 0x55000000);
        graphics.fill(x, y + 10, x + 16, y + 11, 0x55000000);
        graphics.fill(x, y, x + 1, y + 11, 0x55000000);
        graphics.fill(x + 15, y, x + 16, y + 11, 0x55000000);
        graphics.fill(x + 1, y + 1, x + 4, y + 3, 0xFFFFFFFF);
        graphics.fill(x + 12, y + 1, x + 15, y + 3, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 8, x + 4, y + 10, 0xFFFFFFFF);
        graphics.fill(x + 12, y + 8, x + 15, y + 10, 0xFFFFFFFF);
        graphics.fill(x + 6, y + 1, x + 10, y + 10, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 4, x + 15, y + 7, 0xFFFFFFFF);
        graphics.fill(x + 7, y + 1, x + 9, y + 10, 0xFFC8102E);
        graphics.fill(x + 1, y + 5, x + 15, y + 6, 0xFFC8102E);
    }

    // ==================== INPUT HANDLING ====================

    private void clearKeyListening() {
        listeningForKey = false;
        listeningForZoomKey = false;
        listeningForMenuKey = false;
        listeningForShoulderKey = false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean wasHandled) {
        if (event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();

            // 1. Language Modal
            if (showLanguageModal) {
                int popW = 400;
                int popH = 154;
                int popX = (this.width - popW) / 2;
                int popY = (this.height - popH) / 2;
                int btnW = 160;
                int btnH = 32;
                int trBtnX = popX + 24;
                int enBtnX = popX + popW - 24 - btnW;
                int btnY = popY + popH - 46;

                if (mouseX >= trBtnX && mouseX <= trBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemConfig.language = "tr";
                    KiemConfig.hasSelectedLanguage = true;
                    KiemConfig.save();
                    showLanguageModal = false;
                    return true;
                }
                if (mouseX >= enBtnX && mouseX <= enBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemConfig.language = "en";
                    KiemConfig.hasSelectedLanguage = true;
                    KiemConfig.save();
                    showLanguageModal = false;
                    return true;
                }
                return true;
            }

            // 2. Reset Confirm Modal
            if (showResetConfirmModal) {
                int popW = 320;
                int popH = 135;
                int popX = (this.width - popW) / 2;
                int popY = (this.height - popH) / 2;
                int btnW = 90;
                int btnH = 24;
                int cancelX = popX + popW - 16 - btnW;
                int confirmX = cancelX - 10 - btnW;
                int btnY = popY + popH - 36;

                if (mouseX >= confirmX && mouseX <= confirmX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    if (pendingResetWardrobe) {
                        resetWardrobeExceptPetsAndTrails();
                    } else {
                        KiemConfig.resetToDefaults();
                    }
                    pendingResetWardrobe = false;
                    showResetConfirmModal = false;
                    return true;
                }
                if (mouseX >= cancelX && mouseX <= cancelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    pendingResetWardrobe = false;
                    showResetConfirmModal = false;
                    return true;
                }
                if (mouseX < popX || mouseX > popX + popW || mouseY < popY || mouseY > popY + popH) {
                    pendingResetWardrobe = false;
                    showResetConfirmModal = false;
                    return true;
                }
                return true;
            }

            if (isOverMascot(mouseX, mouseY)) {
                draggingMascot = true;
                mascotDragOffsetX = (float) (mouseX - getMascotX());
                mascotDragOffsetY = (float) (mouseY - getMascotY());
                return true;
            }

            int mx = getMx();
            int mw = getMw();
            int topY = getTopY();
            int bottomY = getBottomY();
            int contentY = getContentY();
            int contentH = getContentH();

            // 3. Top Header Pill Bar Clicks
            Tab[] tabs = getVisibleTabs();
            int[] pillWidths = computeTopNavPillWidths();
            int pillGap = 6;
            int pillH = 22;
            int pad = 4;
            int totalPillsW = 0;
            for (int pw : pillWidths) totalPillsW += pw;
            totalPillsW += (tabs.length - 1) * pillGap;
            int barW = totalPillsW + pad * 2;
            int barH = pillH + pad * 2;
            int barX = mx + mw - barW;
            int barY = topY + (TOP_H - barH) / 2;
            int px = barX + pad;
            int py = barY + pad;

            for (int i = 0; i < tabs.length; i++) {
                int pw = pillWidths[i];
                if (mouseX >= px && mouseX <= px + pw && mouseY >= py && mouseY <= py + pillH) {
                    currentTab = tabs[i];
                    configSubView = ConfigSubView.MAIN;
                    configScroll = 0.0;
                    clearKeyListening();
                    return true;
                }
                px += pw + pillGap;
            }

            // 4. Bottom Slot & Status Bar Clicks
            int[] rightLayout = computeBottomRightLayout();
            int closeX = rightLayout[0];
            int gearX = rightLayout[1];
            int langX = rightLayout[2];
            int langW = rightLayout[3];
            int playerX = rightLayout[4];

            int circleSize = 26;
            int circleY = bottomY + (BOTTOM_H - circleSize) / 2;
            if (mouseX >= closeX && mouseX <= closeX + circleSize && mouseY >= circleY && mouseY <= circleY + circleSize) {
                this.onClose();
                return true;
            }
            if (mouseX >= gearX && mouseX <= gearX + circleSize && mouseY >= circleY && mouseY <= circleY + circleSize) {
                pendingResetWardrobe = false;
                showResetConfirmModal = true;
                return true;
            }
            int bPillH = 22;
            int bPillY = bottomY + (BOTTOM_H - bPillH) / 2;
            if (mouseX >= langX && mouseX <= langX + langW && mouseY >= bPillY && mouseY <= bPillY + bPillH) {
                KiemConfig.language = "tr".equalsIgnoreCase(KiemConfig.language) ? "en" : "tr";
                KiemConfig.hasSelectedLanguage = true;
                KiemConfig.save();
                return true;
            }

            int slotsStartX = computeBottomSlotsStartX(playerX);
            int slotSize = 28;
            int slotGap = 6;
            int slotY = bottomY + (BOTTOM_H - slotSize) / 2;
            CosmeticsSub[] slotTargets = {
                    CosmeticsSub.CAPES,
                    CosmeticsSub.WINGS,
                    CosmeticsSub.HATS,
                    CosmeticsSub.NECK,
                    CosmeticsSub.PETS
            };
            for (int i = 0; i < 5; i++) {
                int sx = slotsStartX + i * (slotSize + slotGap);
                if (mouseX >= sx && mouseX <= sx + slotSize && mouseY >= slotY && mouseY <= slotY + slotSize) {
                    currentTab = Tab.COSMETICS;
                    currentCosmeticsSub = slotTargets[i];
                    cosmeticsSlotScroll = 0.0;
                    cosmeticsCapesScroll = 0.0;
                    cosmeticsPetsScroll = 0.0;
                    cosmeticsTrailScroll = 0.0;
                    clearKeyListening();
                    return true;
                }
            }

            // 5. Tab Content Clicks
            if (currentTab == Tab.WELCOME) {
                return handleWelcomeClick(mx, contentY, mw, contentH, mouseX, mouseY);
            } else if (currentTab == Tab.COSMETICS) {
                return handleCosmeticsClick(mx, contentY, mw, contentH, mouseX, mouseY);
            } else if (ENABLE_DEV_MENU && currentTab == Tab.DEV) {
                return handleDevClick(mx, contentY, mw, contentH, mouseX, mouseY);
            } else if (currentTab == Tab.CONFIG) {
                return handleConfigClick(mx, contentY, mw, contentH, mouseX, mouseY);
            }
        }
        return super.mouseClicked(event, wasHandled);
    }

    private boolean handleWelcomeClick(int x, int y, int w, int h, double mouseX, double mouseY) {
        int ix = x + 16;
        int iy = y + 14;
        int iw = w - 32;
        int curBaseY = iy;

        boolean hasUpdate = KiemConfig.updateCheckerEnabled && UpdateChecker.hasUpdate();
        if (hasUpdate) {
            String verText = KiemLang.get("welcome.update_prefix") + " v" + UpdateChecker.getLatestVersion() + "!";
            int verPillW = Math.max(88, BoutiqueDraw.textWidth(this.font, verText) + 18);
            int verPillH = 18;
            int verPillX = ix + iw - verPillW - 14;
            int verPillY = curBaseY + 9;
            if (mouseX >= verPillX && mouseX <= verPillX + verPillW && mouseY >= verPillY && mouseY <= verPillY + verPillH) {
                net.minecraft.util.Util.getPlatform().openUri(UpdateChecker.getDownloadUrl());
                return true;
            }
        }

        int keyBoxX = ix + 14;
        int keyBoxY = curBaseY + 44;
        int keyBoxH = 20;
        int labelW = BoutiqueDraw.textWidth(this.font, KiemLang.get("welcome.menu_key")) + 8;
        int btnW = 82;
        if (mouseX >= keyBoxX + labelW && mouseX <= keyBoxX + labelW + btnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH) {
            listeningForMenuKey = !listeningForMenuKey;
            return true;
        }

        int resetBtnW = 136;
        int resetBtnX = ix + iw - resetBtnW - 14;
        if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH) {
            pendingResetWardrobe = false;
            showResetConfirmModal = true;
            return true;
        }

        int leftEnd = keyBoxX + labelW + btnW;
        for (HeroLinkRect lr : computeHeroLinkRects(leftEnd, resetBtnX, keyBoxY, keyBoxH)) {
            if (mouseX >= lr.x && mouseX <= lr.x + lr.w && mouseY >= lr.y && mouseY <= lr.y + lr.h) {
                net.minecraft.util.Util.getPlatform().openUri(lr.url);
                return true;
            }
        }

        int featY = curBaseY + 72 + 10;
        int listY = featY + 14;
        int navBtnH = 24;
        int availListH = (y + h - 12) - listY - navBtnH - 8;
        int itemGap = 6;
        int itemH = Math.min(44, Math.max(32, (availListH - 2 * itemGap) / 3));

        for (int i = 0; i < 3; i++) {
            int curY = listY + i * (itemH + itemGap);
            if (mouseX >= ix && mouseX <= ix + iw && mouseY >= curY && mouseY <= curY + itemH) {
                if (i == 0) {
                    currentTab = Tab.DEV;
                } else if (i == 1) {
                    currentTab = Tab.COSMETICS;
                    currentCosmeticsSub = CosmeticsSub.HATS;
                } else {
                    currentTab = Tab.COSMETICS;
                    currentCosmeticsSub = CosmeticsSub.PETS;
                }
                configSubView = ConfigSubView.MAIN;
                configScroll = 0.0;
                clearKeyListening();
                return true;
            }
        }

        int navY = listY + 3 * (itemH + itemGap) + 2;
        int navBtnW = (iw - 8) / 2;
        int b1X = ix;
        if (mouseX >= b1X && mouseX <= b1X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH) {
            currentTab = Tab.CONFIG;
            configSubView = ConfigSubView.MAIN;
            return true;
        }
        int b2X = b1X + navBtnW + 8;
        if (mouseX >= b2X && mouseX <= b2X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH) {
            currentTab = Tab.COSMETICS;
            configSubView = ConfigSubView.MAIN;
            return true;
        }
        return false;
    }

    private boolean handleCosmeticsClick(int x, int y, int w, int h, double mouseX, double mouseY) {
        int galleryW = getGalleryW();
        int stageX = getStageX();
        int stageW = getStageW();
        int stripX = getStripX();
        int stripW = getStripW();

        // 1. Category Pills
        CosmeticsSub[] subs = CosmeticsSub.values();
        int stripPadX = x + 12;
        int stripAvailW = galleryW - 24;
        int catGap = 3;
        int catW = (stripAvailW - (subs.length - 1) * catGap) / subs.length;
        int catH = 18;
        int catY = y + 26;

        for (int i = 0; i < subs.length; i++) {
            int bx = stripPadX + i * (catW + catGap);
            if (mouseX >= bx && mouseX <= bx + catW && mouseY >= catY && mouseY <= catY + catH) {
                currentCosmeticsSub = subs[i];
                cosmeticsSlotScroll = 0.0;
                cosmeticsCapesScroll = 0.0;
                cosmeticsPetsScroll = 0.0;
                cosmeticsTrailScroll = 0.0;
                return true;
            }
        }

        // 2. 3D Stage Drag, Reset View, Reset Wardrobe & Customize
        int btnGap = 4;
        int totalW = stageW - 12;
        int resetBtnW = (totalW - 2 * btnGap) / 3;
        int resetWardrobeBtnW = (totalW - 2 * btnGap) / 3;
        int custBtnW = totalW - 2 * btnGap - resetBtnW - resetWardrobeBtnW;
        int resetBtnH = 20;
        int resetBtnX = stageX + 6;
        int resetWardrobeBtnX = resetBtnX + resetBtnW + btnGap;
        int custBtnX = resetWardrobeBtnX + resetWardrobeBtnW + btnGap;
        int resetBtnY = y + h - 26;
        if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + resetBtnH) {
            cosmeticPreviewYaw = 0.0f;
            cosmeticPreviewPitch = 0.0f;
            cosmeticPreviewScale = 1.0f;
            return true;
        }
        if (mouseX >= resetWardrobeBtnX && mouseX <= resetWardrobeBtnX + resetWardrobeBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + resetBtnH) {
            pendingResetWardrobe = true;
            showResetConfirmModal = true;
            return true;
        }
        if (mouseX >= custBtnX && mouseX <= custBtnX + custBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + resetBtnH) {
            cosmeticCustomizeOpen = !cosmeticCustomizeOpen;
            return true;
        }
        if (cosmeticCustomizeOpen) {
            int popW = stageW - 12;
            int popH = 92;
            int popX = stageX + 6;
            int popY = resetBtnY - popH - 6;
            if (mouseX >= popX && mouseX <= popX + popW && mouseY >= popY && mouseY <= popY + popH) {
                String slotKey = getActiveAdjustSlot();
                int rstW = 38;
                int rstX = popX + popW - rstW - 6;
                int rstY = popY + 5;
                if (mouseX >= rstX && mouseX <= rstX + rstW && mouseY >= rstY && mouseY <= rstY + 14) {
                    KiemConfig.resetCosmeticAdjustment(slotKey);
                    KiemConfig.save();
                    return true;
                }
                int rx = popX + 8;
                int rw = popW - 16;
                int btnS = 16;
                int plusX = rx + rw - btnS;
                int minusX = plusX - btnS - 36;
                float offY = KiemConfig.getCosmeticOffsetY(slotKey);
                float offZ = KiemConfig.getCosmeticOffsetZ(slotKey);
                float sc = KiemConfig.getCosmeticScale(slotKey);
                int[] rowYs = { popY + 24, popY + 46, popY + 68 };
                for (int r = 0; r < 3; r++) {
                    int ry = rowYs[r];
                    if (mouseY >= ry + 1 && mouseY <= ry + 1 + btnS) {
                        if (mouseX >= minusX && mouseX <= minusX + btnS) {
                            if (r == 0) offY -= 0.25f;
                            else if (r == 1) offZ -= 0.25f;
                            else sc -= 0.05f;
                            KiemConfig.setCosmeticAdjustment(slotKey, offY, offZ, sc);
                            KiemConfig.save();
                            return true;
                        }
                        if (mouseX >= plusX && mouseX <= plusX + btnS) {
                            if (r == 0) offY += 0.25f;
                            else if (r == 1) offZ += 0.25f;
                            else sc += 0.05f;
                            KiemConfig.setCosmeticAdjustment(slotKey, offY, offZ, sc);
                            KiemConfig.save();
                            return true;
                        }
                    }
                }
                return true;
            }
        }
        if (mouseX >= stageX + 4 && mouseX < stageX + stageW - 4 && mouseY >= y + 8 && mouseY < y + h - 34) {
            draggingCosmeticPreview = true;
            return true;
        }

        // 3. Far-Right EQUIPPED Strip Clicks
        int padX = 7;
        int cardX = stripX + padX;
        int cardW = stripW - padX * 2;
        int startY = y + 26;
        int gap = 6;
        int cardH = Math.max(32, (h - 34 - 4 * gap) / 5);
        CosmeticsSub[] stripTargets = {
                CosmeticsSub.CAPES,
                CosmeticsSub.WINGS,
                CosmeticsSub.HATS,
                CosmeticsSub.PETS,
                CosmeticsSub.TRAILS
        };
        for (int i = 0; i < 5; i++) {
            int cy = startY + i * (cardH + gap);
            if (mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cy && mouseY <= cy + cardH) {
                currentCosmeticsSub = stripTargets[i];
                cosmeticsSlotScroll = 0.0;
                cosmeticsCapesScroll = 0.0;
                cosmeticsPetsScroll = 0.0;
                cosmeticsTrailScroll = 0.0;
                return true;
            }
        }

        // 4. Gallery Viewport & Footer Clicks
        int vpX = x + 12;
        int vpY = catY + catH + 8;
        int vpW = galleryW - 24;
        int footerH = 34;
        int vpH = (y + h - 10) - vpY - footerH - 6;
        int viewportBottom = vpY + vpH;
        int gridW = vpW - BOUTIQUE_SCROLLBAR_W;

        if (currentCosmeticsSub == CosmeticsSub.CAPES) {
            int cardGap = 8;
            int cW = (gridW - 2 * cardGap) / 3;
            int scrollPx = (int) Math.round(cosmeticsCapesScroll);
            int card1X = vpX;
            int card1Y = vpY - scrollPx;
            int card2X = card1X + cW + cardGap;
            int card3X = card2X + cW + cardGap;

            if (mouseX >= vpX && mouseX <= vpX + gridW && mouseY >= vpY && mouseY <= viewportBottom) {
                // Card 1 (SANARA)
                int pillW = 18;
                int pillH = 14;
                int pillGap = 2;
                int pillsTotalW = 3 * pillW + 2 * pillGap;
                int p1X = card1X + cW - 8 - pillsTotalW;
                int p2X = p1X + pillW + pillGap;
                int p3X = p2X + pillW + pillGap;
                int pillY = card1Y + 8;

                if (mouseX >= p1X && mouseX <= p1X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) { selectedSanaraVersion = 1; return true; }
                if (mouseX >= p2X && mouseX <= p2X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) { selectedSanaraVersion = 2; return true; }
                if (mouseX >= p3X && mouseX <= p3X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) { selectedSanaraVersion = 3; return true; }

                int imgW = cW - 16;
                int imgH = 138;
                int btnY = card1Y + 26 + imgH + 8;
                int btnW = (imgW - 4) / 2;
                int btnH = 20;
                int btn1X = card1X + 8;
                int btn2X = btn1X + btnW + 4;
                int footerY = btnY + btnH + 7;

                if (mouseX >= btn1X && mouseX <= btn1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.CapeType target = (selectedSanaraVersion == 1) ? CapeManager.CapeType.SANARA_STATIC
                            : (selectedSanaraVersion == 2 ? CapeManager.CapeType.SANARA_V2_STATIC : CapeManager.CapeType.SANARA_V3_STATIC);
                    CapeManager.setCurrentCape(CapeManager.getCurrentCape() == target ? CapeManager.CapeType.OFF : target);
                    return true;
                }
                if (mouseX >= btn2X && mouseX <= btn2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.CapeType target = (selectedSanaraVersion == 1) ? CapeManager.CapeType.SANARA_ANIMATED
                            : (selectedSanaraVersion == 2 ? CapeManager.CapeType.SANARA_V2_ANIMATED : CapeManager.CapeType.SANARA_V3_ANIMATED);
                    CapeManager.setCurrentCape(CapeManager.getCurrentCape() == target ? CapeManager.CapeType.OFF : target);
                    return true;
                }
                if (mouseX >= card1X + 12 && mouseX <= card1X + cW - 12 && mouseY >= footerY && mouseY <= footerY + 14) {
                    CapeManager.CapeType c = CapeManager.getCurrentCape();
                    if (c == CapeManager.CapeType.SANARA_STATIC || c == CapeManager.CapeType.SANARA_ANIMATED
                            || c == CapeManager.CapeType.SANARA_V2_STATIC || c == CapeManager.CapeType.SANARA_V2_ANIMATED
                            || c == CapeManager.CapeType.SANARA_V3_STATIC || c == CapeManager.CapeType.SANARA_V3_ANIMATED) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                        return true;
                    }
                }

                // Card 2 (RIKKA)
                int btn2_1X = card2X + 8;
                int btn2_2X = btn2_1X + btnW + 4;
                if (mouseX >= btn2_1X && mouseX <= btn2_1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.setCurrentCape(CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_STATIC
                            ? CapeManager.CapeType.OFF : CapeManager.CapeType.RIKKA_STATIC);
                    return true;
                }
                if (mouseX >= btn2_2X && mouseX <= btn2_2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.setCurrentCape(CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_ANIMATED
                            ? CapeManager.CapeType.OFF : CapeManager.CapeType.RIKKA_ANIMATED);
                    return true;
                }
                if (mouseX >= card2X + 12 && mouseX <= card2X + cW - 12 && mouseY >= footerY && mouseY <= footerY + 14) {
                    if (CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_STATIC || CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_ANIMATED) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                        return true;
                    }
                }

                // Card 3 (CUSTOM CAPE)
                String folderLabel = KiemLang.get("cosmetics.open_folder");
                int refreshPillW = 18;
                int folderPillW = Math.min(54, Math.max(42, BoutiqueDraw.textWidth(this.font, folderLabel) + 12));
                int refreshPillX = card3X + cW - 8 - refreshPillW;
                int folderPillX = refreshPillX - folderPillW - 3;
                int customPillY = card1Y + 8;

                if (mouseX >= folderPillX && mouseX <= folderPillX + folderPillW && mouseY >= customPillY && mouseY <= customPillY + pillH) {
                    CustomCapeManager.openCapesFolder();
                    return true;
                }
                if (mouseX >= refreshPillX && mouseX <= refreshPillX + refreshPillW && mouseY >= customPillY && mouseY <= customPillY + pillH) {
                    CustomCapeManager.forceRescan();
                    return true;
                }

                int img3X = card3X + 8;
                int img3Y = card1Y + 26;
                java.util.List<CustomCapeManager.CustomCapeEntry> capes = CustomCapeManager.getLoadedCapes();
                if (capes.isEmpty()) {
                    int openBtnW = imgW - 16;
                    int openBtnH = 20;
                    int openBtnX = img3X + 8;
                    int openBtnY = img3Y + 92;
                    if (mouseX >= openBtnX && mouseX <= openBtnX + openBtnW && mouseY >= openBtnY && mouseY <= openBtnY + openBtnH) {
                        CustomCapeManager.openCapesFolder();
                        return true;
                    }
                } else {
                    int selBarH = 18;
                    int selBarY = img3Y + imgH - selBarH - 4;
                    int arrowW = 16;
                    int leftArrowX = img3X + 4;
                    int rightArrowX = img3X + imgW - arrowW - 4;
                    if (mouseX >= leftArrowX && mouseX <= leftArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH) {
                        CustomCapeManager.setSelectedIndex(CustomCapeManager.getSelectedIndex() - 1);
                        return true;
                    }
                    if (mouseX >= rightArrowX && mouseX <= rightArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH) {
                        CustomCapeManager.setSelectedIndex(CustomCapeManager.getSelectedIndex() + 1);
                        return true;
                    }

                    int equipBtnW = imgW;
                    int equipBtnH = 20;
                    int equipBtnX = img3X;
                    int equipBtnY = img3Y + imgH + 8;
                    if (mouseX >= equipBtnX && mouseX <= equipBtnX + equipBtnW && mouseY >= equipBtnY && mouseY <= equipBtnY + equipBtnH) {
                        CustomCapeManager.CustomCapeEntry activeCustom = CustomCapeManager.getSelectedCape();
                        if (activeCustom != null) {
                            if (CapeManager.getCurrentCape() == CapeManager.CapeType.CUSTOM && activeCustom.fileName.equalsIgnoreCase(KiemConfig.customCapeFileName)) {
                                CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                            } else {
                                KiemConfig.customCapeFileName = activeCustom.fileName;
                                CapeManager.setCurrentCape(CapeManager.CapeType.CUSTOM);
                                KiemConfig.save();
                            }
                        }
                        return true;
                    }
                    if (mouseX >= card3X + 12 && mouseX <= card3X + cW - 12 && mouseY >= footerY && mouseY <= footerY + 14) {
                        if (CapeManager.getCurrentCape() == CapeManager.CapeType.CUSTOM) {
                            CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                            return true;
                        }
                    }
                }
            }

            // Footer True Color Toggle
            int infoY = viewportBottom + 6;
            int toggleW = 114;
            int toggleH = 20;
            int toggleX = vpX + vpW - toggleW - 8;
            int toggleY = infoY + (footerH - toggleH) / 2;
            if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
                KiemConfig.capeTrueColor = !KiemConfig.capeTrueColor;
                KiemConfig.save();
                return true;
            }
        } else if (currentCosmeticsSub == CosmeticsSub.TRAILS) {
            if (mouseX >= vpX && mouseX <= vpX + gridW && mouseY >= vpY && mouseY <= viewportBottom) {
                int scrollPx = (int) Math.round(cosmeticsTrailScroll);
                int sakuraH = 72;
                int sakuraY = vpY - scrollPx;
                int thumbSize = 54;
                int rightX = vpX + 9 + thumbSize + 10;
                int rightW = gridW - thumbSize - 28;
                int btnW = Math.max(84, (rightW - 8) / 2);
                int btnH = 20;
                int btnY = sakuraY + sakuraH - btnH - 10;
                int custX = rightX + btnW + 8;

                if (mouseX >= rightX && mouseX <= rightX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    boolean active1 = KiemConfig.sakuraAuraEnabled;
                    KiemConfig.setActiveAura(active1 ? "NONE" : "SAKURA_LOP");
                    KiemConfig.save();
                    return true;
                }
                if (mouseX >= custX && mouseX <= custX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemCompat.setScreen(this.minecraft, new SakuraAuraScreen(this));
                    return true;
                }

                CosmeticItemDef[] trailItems = getTrailItems();
                int gridCardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
                int gridStartY = sakuraY + sakuraH + BOUTIQUE_GAP;
                for (int i = 0; i < trailItems.length; i++) {
                    int col = i % BOUTIQUE_COLS;
                    int row = i / BOUTIQUE_COLS;
                    int cx = vpX + col * (gridCardW + BOUTIQUE_GAP);
                    int cy = gridStartY + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP);
                    if (mouseX >= cx && mouseX <= cx + gridCardW && mouseY >= cy && mouseY <= cy + BOUTIQUE_CARD_H) {
                        CosmeticItemDef item = trailItems[i];
                        boolean equipped = item.id.equalsIgnoreCase(KiemConfig.cosmeticTrailEffect);
                        KiemConfig.cosmeticTrailEffect = equipped ? "OFF" : item.id;
                        KiemConfig.save();
                        return true;
                    }
                }
            }
        } else if (currentCosmeticsSub == CosmeticsSub.PETS) {
            ShoulderPetType[] petTypes = {
                    ShoulderPetType.HAMSTER,
                    ShoulderPetType.HAMSTER_HD,
                    ShoulderPetType.OCTOPUS,
                    ShoulderPetType.DRAGON,
                    ShoulderPetType.INVENTORY_CURSE
            };
            int gridCardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
            int scrollPx = (int) Math.round(cosmeticsPetsScroll);

            if (mouseX >= vpX && mouseX <= vpX + gridW && mouseY >= vpY && mouseY <= viewportBottom) {
                for (int i = 0; i < petTypes.length; i++) {
                    int col = i % BOUTIQUE_COLS;
                    int row = i / BOUTIQUE_COLS;
                    int cx = vpX + col * (gridCardW + BOUTIQUE_GAP);
                    int cy = vpY + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP) - scrollPx;
                    if (mouseX >= cx && mouseX <= cx + gridCardW && mouseY >= cy && mouseY <= cy + BOUTIQUE_CARD_H) {
                        int btnH = 18;
                        int btnY = cy + BOUTIQUE_CARD_H - btnH - 9;
                        int custW = 22;
                        int gapPx = 4;
                        int equipW = gridCardW - 18 - custW - gapPx;
                        int equipX = cx + 9;
                        int custX = equipX + equipW + gapPx;

                        if (mouseX >= custX && mouseX <= custX + custW && mouseY >= btnY && mouseY <= btnY + btnH) {
                            KiemCompat.setScreen(this.minecraft, new PetCustomizeScreen(this, petTypes[i]));
                            return true;
                        }
                        ShoulderPetManager.togglePet(petTypes[i]);
                        return true;
                    }
                }
            }
        } else {
            CosmeticItemDef[] items = getSlotItems(currentCosmeticsSub);
            String activeId = getEquippedSlotId(currentCosmeticsSub);
            int gridCardW = (gridW - (BOUTIQUE_COLS - 1) * BOUTIQUE_GAP) / BOUTIQUE_COLS;
            int scrollPx = (int) Math.round(cosmeticsSlotScroll);

            if (mouseX >= vpX && mouseX <= vpX + gridW && mouseY >= vpY && mouseY <= viewportBottom) {
                for (int i = 0; i < items.length; i++) {
                    int col = i % BOUTIQUE_COLS;
                    int row = i / BOUTIQUE_COLS;
                    int cx = vpX + col * (gridCardW + BOUTIQUE_GAP);
                    int cy = vpY + row * (BOUTIQUE_CARD_H + BOUTIQUE_GAP) - scrollPx;
                    if (mouseX >= cx && mouseX <= cx + gridCardW && mouseY >= cy && mouseY <= cy + BOUTIQUE_CARD_H) {
                        CosmeticItemDef item = items[i];
                        boolean equipped = item.id.equalsIgnoreCase(activeId);
                        setEquippedSlotId(currentCosmeticsSub, equipped ? "OFF" : item.id);
                        return true;
                    }
                }
            }

            int infoY = viewportBottom + 6;
            if (currentCosmeticsSub == CosmeticsSub.HAND) {
                int toggleW = getHandFirstPersonToggleWidth();
                int toggleH = 20;
                int toggleX = vpX + vpW - toggleW - 8;
                int toggleY = infoY + (footerH - toggleH) / 2;
                if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
                    KiemConfig.cosmeticHandFirstPerson = !KiemConfig.cosmeticHandFirstPerson;
                    KiemConfig.save();
                    return true;
                }
            } else if (currentCosmeticsSub == CosmeticsSub.WAIST) {
                int toggleW = getWaistDisableCapeToggleWidth();
                int toggleH = 20;
                int toggleX = vpX + vpW - toggleW - 8;
                int toggleY = infoY + (footerH - toggleH) / 2;
                if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
                    KiemConfig.cosmeticWaistHideCape = !KiemConfig.cosmeticWaistHideCape;
                    KiemConfig.save();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleConfigClick(int x, int y, int w, int h, double mouseX, double mouseY) {
        int ix = x + 16;
        int iy = y + 14;
        int iw = w - 32;
        int ih = h - 28;

        if (configSubView == ConfigSubView.MAIN) {
            int colGap = 10;
            int colW = (iw - colGap) / 2;
            int leftColX = ix;
            int rightColX = ix + colW + colGap;
            int startY = iy + 16;
            int cardGap = 8;
            int cardH = Math.min(58, Math.max(44, (ih - 16 - 3 * cardGap) / 4));

            ConfigSubView[] leftViews = { ConfigSubView.MINI_MODELS, ConfigSubView.CUSTOM_VIEWMODEL, ConfigSubView.BETTER_THIRD_PERSON, ConfigSubView.AFK_CINEMATICS };
            ConfigSubView[] rightViews = { ConfigSubView.SMOOTH_ZOOM, ConfigSubView.CHAT_QOL, ConfigSubView.TOOLTIP_QOL, ConfigSubView.ANTI_AFK };

            for (int i = 0; i < 4; i++) {
                int cy = startY + i * (cardH + cardGap);
                if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= cy && mouseY <= cy + cardH) {
                    configSubView = leftViews[i];
                    configScroll = 0.0;
                    clearKeyListening();
                    return true;
                }
                if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= cy && mouseY <= cy + cardH) {
                    configSubView = rightViews[i];
                    configScroll = 0.0;
                    clearKeyListening();
                    return true;
                }
            }
            return false;
        }

        // Subview Header Clicks (Back, Reset, Master Toggle)
        int backBtnW = 60;
        int backBtnH = 20;
        if (mouseX >= ix && mouseX <= ix + backBtnW && mouseY >= iy && mouseY <= iy + backBtnH) {
            configSubView = ConfigSubView.MAIN;
            configScroll = 0.0;
            clearKeyListening();
            return true;
        }

        boolean hasReset = (configSubView == ConfigSubView.MINI_MODELS
                || configSubView == ConfigSubView.CUSTOM_VIEWMODEL
                || configSubView == ConfigSubView.BETTER_THIRD_PERSON);
        int rightLimit = ix + iw;
        if (hasReset) {
            int resetBtnW = 62;
            int resetBtnX = ix + iw - resetBtnW;
            if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= iy && mouseY <= iy + backBtnH) {
                resetCurrentConfigSubView();
                return true;
            }
            rightLimit = resetBtnX - 8;
        }
        if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
            int toggleW = 56;
            int toggleH = 18;
            int tX = rightLimit - toggleW;
            int tY = iy + 1;
            if (mouseX >= tX && mouseX <= tX + toggleW && mouseY >= tY && mouseY <= tY + toggleH) {
                KiemConfig.viewmodelEnabled = !KiemConfig.viewmodelEnabled;
                KiemConfig.save();
                return true;
            }
        }

        // Scissored Body Clicks
        int headerH = 26;
        int bodyY = iy + headerH;
        int bodyH = ih - headerH;
        if (mouseY < bodyY || mouseY > bodyY + bodyH) return false;

        int maxScroll = getConfigSubViewMaxScroll(bodyH);
        int bodyW = maxScroll > 0 ? iw - 8 : iw;
        int sy = bodyY - (int) Math.round(configScroll);

        if (configSubView == ConfigSubView.MINI_MODELS) {
            int colGap = 12;
            int colW = (bodyW - colGap) / 2;
            int leftColX = ix;
            int rightColX = ix + colW + colGap;
            int rowH = 42;
            int rowGap = 6;
            int r0Y = sy;
            int toggleW = 56;
            int toggleH = 18;
            int t0X = leftColX + colW - toggleW - 10;
            int t0Y = r0Y + (36 - toggleH) / 2;
            if (mouseX >= t0X && mouseX <= t0X + toggleW && mouseY >= t0Y && mouseY <= t0Y + toggleH) {
                KiemConfig.selfScaleEnabled = !KiemConfig.selfScaleEnabled;
                KiemConfig.save();
                return true;
            }
            int r1Y = r0Y + 36 + rowGap;
            if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= r1Y && mouseY <= r1Y + 34) {
                KiemConfig.selfScaleCamera = !KiemConfig.selfScaleCamera;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + 34 + rowGap;
            if (checkSliderClick("self_x", leftColX, r2Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
            int r3Y = r2Y + rowH + rowGap;
            if (checkSliderClick("self_y", leftColX, r3Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
            int r4Y = r3Y + rowH + rowGap;
            if (checkSliderClick("self_z", leftColX, r4Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;

            int r0RightH = 36 + 34 + rowGap;
            int t1X = rightColX + colW - toggleW - 12;
            int t1Y = r0Y + (r0RightH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.othersScaleEnabled = !KiemConfig.othersScaleEnabled;
                KiemConfig.save();
                return true;
            }
            if (checkSliderClick("others_x", rightColX, r2Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
            if (checkSliderClick("others_y", rightColX, r3Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
            if (checkSliderClick("others_z", rightColX, r4Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
        } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
            int colGap = 12;
            int colW = (bodyW - colGap) / 2;
            int leftColX = ix;
            int rightColX = ix + colW + colGap;
            int rowH = 41;
            int rowGap = 5;
            int r1Y = sy;
            if (checkSliderClick("vm_size", leftColX, r1Y, colW, rowH, -1.50f, 1.50f, mouseX, mouseY)) return true;
            int r2Y = r1Y + rowH + rowGap;
            if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= r2Y && mouseY <= r2Y + rowH) {
                KiemConfig.itemScaleSwing = !KiemConfig.itemScaleSwing;
                KiemConfig.save();
                return true;
            }
            int r3Y = r2Y + rowH + rowGap;
            if (checkSliderClick("vm_x", leftColX, r3Y, colW, rowH, -1.50f, 1.50f, mouseX, mouseY)) return true;
            int r4Y = r3Y + rowH + rowGap;
            if (checkSliderClick("vm_y", leftColX, r4Y, colW, rowH, -1.50f, 1.50f, mouseX, mouseY)) return true;
            int r5Y = r4Y + rowH + rowGap;
            if (checkSliderClick("vm_z", leftColX, r5Y, colW, rowH, -1.50f, 1.50f, mouseX, mouseY)) return true;

            if (checkSliderClick("vm_yaw", rightColX, r1Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
            if (checkSliderClick("vm_pitch", rightColX, r2Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
            if (checkSliderClick("vm_roll", rightColX, r3Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
            if (checkSliderClick("vm_speed", rightColX, r4Y, colW, rowH, -5.00f, 1.00f, mouseX, mouseY)) return true;
            if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r5Y && mouseY <= r5Y + rowH) {
                KiemConfig.dontSkipAnimation = !KiemConfig.dontSkipAnimation;
                KiemConfig.save();
                return true;
            }
        } else if (configSubView == ConfigSubView.AFK_CINEMATICS) {
            int rowH = 40;
            int rowGap = 6;
            int toggleW = 56;
            int toggleH = 18;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 12;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.afkCinematicsEnabled = !KiemConfig.afkCinematicsEnabled;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int pillW = 46;
            int pillGap = 5;
            int[] secOptions = {30, 60, 120, 300};
            int totalSecW = secOptions.length * pillW + (secOptions.length - 1) * pillGap;
            int pillsStartX = ix + bodyW - totalSecW - 12;
            int pillsY = r2Y + (rowH - toggleH) / 2;
            for (int i = 0; i < secOptions.length; i++) {
                int pX = pillsStartX + i * (pillW + pillGap);
                if (mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH) {
                    KiemConfig.afkCinematicsSeconds = secOptions[i];
                    KiemConfig.save();
                    return true;
                }
            }
            int r3Y = r2Y + rowH + rowGap;
            int t3X = ix + bodyW - toggleW - 12;
            int t3Y = r3Y + (rowH - toggleH) / 2;
            if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                KiemConfig.afkCinematicBars = !KiemConfig.afkCinematicBars;
                KiemConfig.save();
                return true;
            }
            int r4Y = r3Y + rowH + rowGap;
            int t4X = ix + bodyW - toggleW - 12;
            int t4Y = r4Y + (rowH - toggleH) / 2;
            if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                KiemConfig.afkRaycastEnabled = !KiemConfig.afkRaycastEnabled;
                KiemConfig.save();
                return true;
            }
            int r5Y = r4Y + rowH + rowGap;
            int testBtnW = 120;
            int testBtnX = ix + bodyW - testBtnW - 12;
            int testBtnY = r5Y + (rowH - 22) / 2;
            if (mouseX >= testBtnX && mouseX <= testBtnX + testBtnW && mouseY >= testBtnY && mouseY <= testBtnY + 22) {
                AfkCinematicsManager.startAfk();
                this.onClose();
                return true;
            }
        } else if (configSubView == ConfigSubView.CHAT_QOL) {
            int rowH = 50;
            int rowGap = 8;
            int toggleW = 60;
            int toggleH = 22;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 14;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.infiniteChat = !KiemConfig.infiniteChat;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int t2X = ix + bodyW - toggleW - 14;
            int t2Y = r2Y + (rowH - toggleH) / 2;
            if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                KiemConfig.chatPeekEnabled = !KiemConfig.chatPeekEnabled;
                KiemConfig.save();
                return true;
            }
            int keyBtnW = 76;
            int keyBtnX = t2X - keyBtnW - 8;
            if (mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                listeningForKey = !listeningForKey;
                return true;
            }
            int r3Y = r2Y + rowH + rowGap;
            int t3X = ix + bodyW - toggleW - 14;
            int t3Y = r3Y + (rowH - toggleH) / 2;
            if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                KiemConfig.compactMessages = !KiemConfig.compactMessages;
                KiemConfig.save();
                return true;
            }
            int r4Y = r3Y + rowH + rowGap;
            int t4X = ix + bodyW - toggleW - 14;
            int t4Y = r4Y + (rowH - toggleH) / 2;
            if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                KiemConfig.copyChatEnabled = !KiemConfig.copyChatEnabled;
                KiemConfig.save();
                return true;
            }
        } else if (configSubView == ConfigSubView.TOOLTIP_QOL) {
            int rowH = 50;
            int rowGap = 8;
            int toggleW = 60;
            int toggleH = 22;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 14;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.tooltipScrollEnabled = !KiemConfig.tooltipScrollEnabled;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int t2X = ix + bodyW - toggleW - 14;
            int t2Y = r2Y + (rowH - toggleH) / 2;
            if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                KiemConfig.tooltipScaleEnabled = !KiemConfig.tooltipScaleEnabled;
                KiemConfig.save();
                return true;
            }
            int r3Y = r2Y + rowH + rowGap;
            int resetBtnW = 90;
            int resetBtnX = ix + bodyW - resetBtnW - 14;
            int resetBtnY = r3Y + (rowH - toggleH) / 2;
            if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + toggleH) {
                TooltipQoL.resetScale();
                return true;
            }
        } else if (configSubView == ConfigSubView.SMOOTH_ZOOM) {
            int rowH = 34;
            int rowGap = 4;
            int toggleW = 56;
            int toggleH = 18;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 14;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.zoomEnabled = !KiemConfig.zoomEnabled;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int keyBtnW = 76;
            int keyBtnX = ix + bodyW - keyBtnW - 14;
            int keyBtnY = r2Y + (rowH - toggleH) / 2;
            if (mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                listeningForZoomKey = !listeningForZoomKey;
                return true;
            }
            int btnStepW = 20;
            int btnPlusX = keyBtnX - btnStepW - 8;
            int btnMinusX = btnPlusX - btnStepW - 4;
            if (mouseX >= btnMinusX && mouseX <= btnMinusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                KiemConfig.zoomDefaultFactor = Math.max(1.5, Math.round((KiemConfig.zoomDefaultFactor - 0.5) * 10.0) / 10.0);
                KiemConfig.save();
                return true;
            }
            if (mouseX >= btnPlusX && mouseX <= btnPlusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                KiemConfig.zoomDefaultFactor = Math.min(10.0, Math.round((KiemConfig.zoomDefaultFactor + 0.5) * 10.0) / 10.0);
                KiemConfig.save();
                return true;
            }
            int r3Y = r2Y + rowH + rowGap;
            int t3X = ix + bodyW - toggleW - 14;
            int t3Y = r3Y + (rowH - toggleH) / 2;
            if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                KiemConfig.zoomScrollEnabled = !KiemConfig.zoomScrollEnabled;
                KiemConfig.save();
                return true;
            }
            int r4Y = r3Y + rowH + rowGap;
            int t4X = ix + bodyW - toggleW - 14;
            int t4Y = r4Y + (rowH - toggleH) / 2;
            if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                KiemConfig.zoomCinematicCamera = !KiemConfig.zoomCinematicCamera;
                KiemConfig.save();
                return true;
            }
            int r5Y = r4Y + rowH + rowGap;
            int t5X = ix + bodyW - toggleW - 14;
            int t5Y = r5Y + (rowH - toggleH) / 2;
            if (mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH) {
                KiemConfig.zoomCinematicBars = !KiemConfig.zoomCinematicBars;
                KiemConfig.save();
                return true;
            }
            int r6Y = r5Y + rowH + rowGap;
            int t6X = ix + bodyW - toggleW - 14;
            int t6Y = r6Y + (rowH - toggleH) / 2;
            if (mouseX >= t6X && mouseX <= t6X + toggleW && mouseY >= t6Y && mouseY <= t6Y + toggleH) {
                KiemConfig.cinematicBarsAlways = !KiemConfig.cinematicBarsAlways;
                KiemConfig.save();
                return true;
            }
            int r7Y = r6Y + rowH + rowGap;
            if (checkSliderClick("zoom_cinematic_size", ix, r7Y, bodyW, rowH, 4.0f, 30.0f, mouseX, mouseY)) return true;

            int r8Y = r7Y + rowH + rowGap;
            int t8X = ix + bodyW - toggleW - 14;
            int t8Y = r8Y + (rowH - toggleH) / 2;
            if (mouseX >= t8X && mouseX <= t8X + toggleW && mouseY >= t8Y && mouseY <= t8Y + toggleH) {
                KiemConfig.zoomSensitivityScale = !KiemConfig.zoomSensitivityScale;
                KiemConfig.save();
                return true;
            }
            int r9Y = r8Y + rowH + rowGap;
            int resetBtnW = 60;
            int resetBtnX = ix + bodyW - resetBtnW - 14;
            int r9BtnsY = r9Y + (rowH - toggleH) / 2;
            if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) {
                KiemConfig.zoomEnabled = true;
                KiemConfig.zoomKey = com.mojang.blaze3d.platform.InputConstants.KEY_C;
                KiemConfig.zoomDefaultFactor = 4.0;
                KiemConfig.zoomScrollEnabled = true;
                KiemConfig.zoomCinematicCamera = true;
                KiemConfig.zoomCinematicBars = false;
                KiemConfig.cinematicBarsAlways = false;
                KiemConfig.cinematicBarSize = 0.12f;
                KiemConfig.zoomSensitivityScale = false;
                KiemConfig.zoomSpeed = 16.0;
                if (SmoothZoom.zoomKeyMapping != null) {
                    SmoothZoom.zoomKeyMapping.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(com.mojang.blaze3d.platform.InputConstants.KEY_C));
                }
                KiemConfig.save();
                return true;
            }
            int spW = 46;
            int spGap = 4;
            int sp3X = resetBtnX - spW - 8;
            int sp2X = sp3X - spW - spGap;
            int sp1X = sp2X - spW - spGap;
            if (mouseX >= sp1X && mouseX <= sp1X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) { KiemConfig.zoomSpeed = 8.0; KiemConfig.save(); return true; }
            if (mouseX >= sp2X && mouseX <= sp2X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) { KiemConfig.zoomSpeed = 16.0; KiemConfig.save(); return true; }
            if (mouseX >= sp3X && mouseX <= sp3X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) { KiemConfig.zoomSpeed = 26.0; KiemConfig.save(); return true; }
        } else if (configSubView == ConfigSubView.ANTI_AFK) {
            int rowH = 50;
            int rowGap = 8;
            int toggleW = 60;
            int toggleH = 22;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 14;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.antiAfkEnabled = !KiemConfig.antiAfkEnabled;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int pillW = 42;
            int pillGap = 5;
            int[] mins = {1, 2, 3, 5, 10};
            int totalPillsW = mins.length * pillW + (mins.length - 1) * pillGap;
            int pillsStartX = ix + bodyW - totalPillsW - 14;
            int pillsY = r2Y + (rowH - toggleH) / 2;
            for (int i = 0; i < mins.length; i++) {
                int pX = pillsStartX + i * (pillW + pillGap);
                if (mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH) {
                    KiemConfig.antiAfkMinutes = mins[i];
                    KiemConfig.save();
                    return true;
                }
            }
            int r3Y = r2Y + rowH + rowGap;
            String[] actions = {"SHIFT", "JUMP", "CLICK"};
            int actW = 54;
            int actGap = 6;
            int totalActsW = actions.length * actW + (actions.length - 1) * actGap;
            int actsStartX = ix + bodyW - totalActsW - 14;
            int actsY = r3Y + (rowH - toggleH) / 2;
            for (int i = 0; i < actions.length; i++) {
                int aX = actsStartX + i * (actW + actGap);
                if (mouseX >= aX && mouseX <= aX + actW && mouseY >= actsY && mouseY <= actsY + toggleH) {
                    KiemConfig.antiAfkAction = actions[i];
                    KiemConfig.save();
                    return true;
                }
            }
            int r4Y = r3Y + rowH + rowGap;
            int resetW = 76;
            int resetX = ix + bodyW - resetW - 14;
            int resetY = r4Y + (rowH - toggleH) / 2;
            if (mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + toggleH) {
                KiemConfig.antiAfkEnabled = false;
                KiemConfig.antiAfkMinutes = 3;
                KiemConfig.antiAfkAction = "SHIFT";
                KiemConfig.save();
                return true;
            }
        } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
            int rowH = 38;
            int rowGap = 5;
            int toggleW = 56;
            int toggleH = 18;
            int r1Y = sy;
            int t1X = ix + bodyW - toggleW - 12;
            int t1Y = r1Y + (rowH - toggleH) / 2;
            if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                KiemConfig.betterThirdPersonEnabled = !KiemConfig.betterThirdPersonEnabled;
                KiemConfig.save();
                return true;
            }
            int r2Y = r1Y + rowH + rowGap;
            int t2X = ix + bodyW - toggleW - 12;
            int t2Y = r2Y + (rowH - toggleH) / 2;
            if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                KiemConfig.shoulderSurfingEnabled = !KiemConfig.shoulderSurfingEnabled;
                KiemConfig.save();
                return true;
            }
            int r3Y = r2Y + rowH + rowGap;
            int keyBtnW = 74;
            int keyBtnX = ix + bodyW - keyBtnW - 12;
            int keyBtnY = r3Y + (rowH - toggleH) / 2;
            if (mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                listeningForShoulderKey = !listeningForShoulderKey;
                return true;
            }
            int pillW = 46;
            int pillGap = 4;
            int pillRightX = keyBtnX - pillW - 8;
            int pillLeftX = pillRightX - pillW - pillGap;
            if (mouseX >= pillLeftX && mouseX <= pillLeftX + pillW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                KiemConfig.shoulderSide = -1.0f;
                KiemConfig.save();
                return true;
            }
            if (mouseX >= pillRightX && mouseX <= pillRightX + pillW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                KiemConfig.shoulderSide = 1.0f;
                KiemConfig.save();
                return true;
            }
            int r4Y = r3Y + rowH + rowGap;
            if (checkSliderClick("tp_smoothness", ix, r4Y, bodyW, rowH, 0.0f, 1.0f, mouseX, mouseY)) return true;
            int r5Y = r4Y + rowH + rowGap;
            int t5X = ix + bodyW - toggleW - 12;
            int t5Y = r5Y + (rowH - toggleH) / 2;
            if (mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH) {
                KiemConfig.thirdPersonSpeedLag = !KiemConfig.thirdPersonSpeedLag;
                KiemConfig.save();
                return true;
            }
            int r6Y = r5Y + rowH + rowGap;
            if (checkSliderClick("tp_distance", ix, r6Y, bodyW, rowH, 2.0f, 8.0f, mouseX, mouseY)) return true;
        }
        return false;
    }

    private void resetCurrentConfigSubView() {
        if (configSubView == ConfigSubView.MINI_MODELS) {
            KiemConfig.selfScaleEnabled = false;
            KiemConfig.selfScaleX = 1.0f;
            KiemConfig.selfScaleY = 1.0f;
            KiemConfig.selfScaleZ = 1.0f;
            KiemConfig.selfScaleCamera = true;
            KiemConfig.othersScaleEnabled = false;
            KiemConfig.othersScaleX = 1.0f;
            KiemConfig.othersScaleY = 1.0f;
            KiemConfig.othersScaleZ = 1.0f;
            KiemConfig.save();
        } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
            KiemConfig.viewmodelEnabled = false;
            KiemConfig.itemSize = 0.0f;
            KiemConfig.itemScaleSwing = false;
            KiemConfig.itemPosX = 0.0f;
            KiemConfig.itemPosY = 0.0f;
            KiemConfig.itemPosZ = 0.0f;
            KiemConfig.itemYaw = 0.0f;
            KiemConfig.itemPitch = 0.0f;
            KiemConfig.itemRoll = 0.0f;
            KiemConfig.swingSpeed = 0.0f;
            KiemConfig.dontSkipAnimation = false;
            KiemConfig.save();
        } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
            KiemConfig.betterThirdPersonEnabled = false;
            KiemConfig.shoulderSurfingEnabled = true;
            KiemConfig.shoulderSide = 1.0f;
            KiemConfig.thirdPersonSmoothness = 0.5f;
            KiemConfig.thirdPersonSpeedLag = true;
            KiemConfig.thirdPersonDistance = 4.0f;
            KiemConfig.save();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!showResetConfirmModal && !showLanguageModal) {
            int mx = getMx();
            int contentY = getContentY();
            int contentH = getContentH();

            if (currentTab == Tab.COSMETICS) {
                int galleryW = getGalleryW();
                int stageX = getStageX();
                int stageW = getStageW();

                if (mouseX >= stageX + 4 && mouseX < stageX + stageW - 4
                        && mouseY >= contentY + 8 && mouseY < contentY + contentH - 34) {
                    cosmeticPreviewScale = net.minecraft.util.Mth.clamp(cosmeticPreviewScale + (float) scrollY * 0.1f, 0.5f, 2.2f);
                    return true;
                }

                int vpX = mx + 12;
                int vpY = contentY + 26 + 18 + 8;
                int vpW = galleryW - 24;
                int footerH = 34;
                int vpH = (contentY + contentH - 10) - vpY - footerH - 6;
                int viewportBottom = vpY + vpH;

                if (mouseX >= vpX && mouseX <= vpX + vpW && mouseY >= vpY && mouseY <= viewportBottom) {
                    if (currentCosmeticsSub == CosmeticsSub.CAPES) {
                        int maxScroll = Math.max(0, 228 - vpH);
                        cosmeticsCapesScroll = Math.max(0.0, Math.min(cosmeticsCapesScroll - scrollY * 24.0, maxScroll));
                        return true;
                    } else if (currentCosmeticsSub == CosmeticsSub.TRAILS) {
                        int maxScroll = getTrailMaxScroll(getTrailItems().length, vpH);
                        cosmeticsTrailScroll = Math.max(0.0, Math.min(cosmeticsTrailScroll - scrollY * 24.0, maxScroll));
                        return true;
                    } else if (currentCosmeticsSub == CosmeticsSub.PETS) {
                        int maxScroll = getBoutiqueGridMaxScroll(5, vpH);
                        cosmeticsPetsScroll = Math.max(0.0, Math.min(cosmeticsPetsScroll - scrollY * 24.0, maxScroll));
                        return true;
                    } else {
                        int maxScroll = getBoutiqueGridMaxScroll(getSlotItems(currentCosmeticsSub).length, vpH);
                        cosmeticsSlotScroll = Math.max(0.0, Math.min(cosmeticsSlotScroll - scrollY * 24.0, maxScroll));
                        return true;
                    }
                }
            } else if (currentTab == Tab.CONFIG && configSubView != ConfigSubView.MAIN) {
                int mw = getMw();
                int ix = mx + 16;
                int iy = contentY + 14;
                int iw = mw - 32;
                int ih = contentH - 28;
                int bodyY = iy + 26;
                int bodyH = ih - 26;
                if (mouseX >= ix && mouseX <= ix + iw && mouseY >= bodyY && mouseY <= bodyY + bodyH) {
                    int maxScroll = getConfigSubViewMaxScroll(bodyH);
                    if (maxScroll > 0) {
                        configScroll = Math.max(0.0, Math.min(configScroll - scrollY * 24.0, maxScroll));
                        return true;
                    }
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingMascot && event.button() == 0) {
            float maxX = Math.max(1.0f, this.width - MASCOT_SIZE - 8);
            float maxY = Math.max(1.0f, this.height - MASCOT_SIZE - 4);
            KiemConfig.uiMascotX = net.minecraft.util.Mth.clamp((float) (event.x() - mascotDragOffsetX - 4.0) / maxX, 0.0f, 1.0f);
            KiemConfig.uiMascotY = net.minecraft.util.Mth.clamp((float) (event.y() - mascotDragOffsetY - 2.0) / maxY, 0.0f, 1.0f);
            return true;
        }
        if (draggingCosmeticPreview && event.button() == 0) {
            cosmeticPreviewYaw = (cosmeticPreviewYaw + (float) deltaX * 0.65f) % 360f;
            cosmeticPreviewPitch = net.minecraft.util.Mth.clamp(cosmeticPreviewPitch + (float) deltaY * 0.45f, -45f, 45f);
            return true;
        }
        if (draggingSlider != null && sliderTrackW > 0) {
            float pct = (float) ((event.x() - sliderTrackX) / (double) sliderTrackW);
            pct = net.minecraft.util.Mth.clamp(pct, 0.0f, 1.0f);
            float val = sliderMin + pct * (sliderMax - sliderMin);
            setSliderValue(draggingSlider, val);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingMascot && event.button() == 0) {
            draggingMascot = false;
            KiemConfig.save();
            return true;
        }
        if (draggingCosmeticPreview && event.button() == 0) {
            draggingCosmeticPreview = false;
            return true;
        }
        if (draggingSlider != null) {
            draggingSlider = null;
            KiemConfig.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listeningForMenuKey) {
            if (event.key() == 256) {
                listeningForMenuKey = false;
            } else {
                KiemConfig.menuKey = event.key();
                if (ExampleModClient.menuKeyMapping != null) {
                    ExampleModClient.menuKeyMapping.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(event.key()));
                }
                KiemConfig.save();
                listeningForMenuKey = false;
            }
            return true;
        }
        if (listeningForKey) {
            if (event.key() == 256) {
                listeningForKey = false;
            } else {
                KiemConfig.chatPeekKey = event.key();
                KiemConfig.save();
                listeningForKey = false;
            }
            return true;
        }
        if (listeningForZoomKey) {
            if (event.key() == 256) {
                listeningForZoomKey = false;
            } else {
                KiemConfig.zoomKey = event.key();
                if (SmoothZoom.zoomKeyMapping != null) {
                    SmoothZoom.zoomKeyMapping.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(event.key()));
                }
                KiemConfig.save();
                listeningForZoomKey = false;
            }
            return true;
        }
        if (listeningForShoulderKey) {
            if (event.key() == 256) {
                listeningForShoulderKey = false;
            } else {
                KiemConfig.shoulderSwapKey = event.key();
                if (ExampleModClient.shoulderSwapKeyMapping != null) {
                    ExampleModClient.shoulderSwapKeyMapping.setKey(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(event.key()));
                }
                KiemConfig.save();
                listeningForShoulderKey = false;
            }
            return true;
        }
        return super.keyPressed(event);
    }
}
