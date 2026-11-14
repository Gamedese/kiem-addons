package com.example.client;

import com.example.client.pet.ShoulderPetManager;
import com.example.client.pet.ShoulderPetType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class KiemScreen extends Screen {

    private static final Identifier SANARA_V1_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara.png");
    private static final Identifier SANARA_V2_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara_v2.png");
    private static final Identifier SANARA_V3_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_sanara_v3.png");
    private static final Identifier RIKKA_PREVIEW =
            Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_rikka.png");
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
        CAPES,
        PETS,
        TRAILS
    }

    private enum Tab {
        WELCOME("Welcome"),
        CONFIG("Features"),
        COSMETICS("Cosmetics");

        private final String label;

        Tab(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
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
    private boolean showResetConfirmModal = false;
    private boolean showLanguageModal = !KiemConfig.hasSelectedLanguage;

    // Interactive slider dragging state
    private String draggingSlider = null;
    private int sliderTrackX = 0;
    private int sliderTrackW = 0;
    private float sliderMin = 0f;
    private float sliderMax = 0f;

    // Palette inspired by Nebula modern dashboard (clean dark/slate theme)
    private static final int COLOR_BG = 0xEE12131A;         // Main dark glass background
    private static final int COLOR_SIDEBAR = 0xFF181A24;    // Sidebar panel
    private static final int COLOR_TOPBAR = 0xFF181A24;     // Top header bar
    private static final int COLOR_CARD_BG = 0xFF1B1D28;    // Card base
    private static final int COLOR_CARD_HOVER = 0xFF242738; // Card hover
    private static final int COLOR_CARD_ACTIVE = 0xFF231E2F;// Active card accent bg
    private static final int COLOR_ACCENT = 0xFFE05688;     // Nebula pink accent
    private static final int COLOR_ACCENT_BG = 0x33E05688;  // Soft pink pill accent
    private static final int COLOR_TEXT_WHITE = 0xFFF0F2F8;
    private static final int COLOR_TEXT_MUTED = 0xFF8A90A5;
    private static final int COLOR_TEXT_DIM = 0xFF5A6075;
    private static final int COLOR_BORDER = 0xFF2C3040;
    private static final int COLOR_ACTIVE_BORDER = 0xFFE05688;

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

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Full screen dimmed backdrop
        graphics.fill(0, 0, this.width, this.height, 0x88000000);

        int modalW = Math.min(740, this.width - 20);
        int modalH = Math.min(430, this.height - 20);
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        int sidebarW = 140;
        int topBarH = 40;

        // Modal base background
        drawRoundedRect(graphics, modalX, modalY, modalW, modalH, COLOR_BG, COLOR_BORDER);

        // Sidebar background
        drawRoundedRect(graphics, modalX, modalY, sidebarW, modalH, COLOR_SIDEBAR, COLOR_BORDER);

        // Topbar background
        drawRoundedRect(graphics, modalX + sidebarW, modalY, modalW - sidebarW, topBarH, COLOR_TOPBAR, COLOR_BORDER);

        boolean hasModal = showResetConfirmModal || showLanguageModal;
        int bgMouseX = hasModal ? -9999 : mouseX;
        int bgMouseY = hasModal ? -9999 : mouseY;

        // 1. Sidebar Brand
        graphics.text(this.font, "✦ Kiem", modalX + 18, modalY + 16, COLOR_TEXT_WHITE, false);

        // 2. Sidebar Section: MAIN
        int tabStartY = modalY + 50;
        graphics.text(this.font, KiemLang.get("sidebar.main"), modalX + 18, tabStartY, COLOR_TEXT_DIM, false);

        // Tab: Welcome
        int welTabY = tabStartY + 14;
        boolean welActive = (currentTab == Tab.WELCOME);
        boolean welHover = bgMouseX >= modalX + 12 && bgMouseX <= modalX + sidebarW - 12 && bgMouseY >= welTabY && bgMouseY <= welTabY + 24;
        if (welActive) {
            drawPill(graphics, modalX + 12, welTabY, sidebarW - 24, 24, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.text(this.font, "• " + KiemLang.get("tab.welcome"), modalX + 22, welTabY + 8, COLOR_ACCENT, false);
        } else {
            if (welHover) {
                drawPill(graphics, modalX + 12, welTabY, sidebarW - 24, 24, 0x1AFFFFFF, 0);
            }
            graphics.text(this.font, "  " + KiemLang.get("tab.welcome"), modalX + 22, welTabY + 8, welHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, false);
        }

        // Sidebar Section: CONFIGURATION
        int configSectionY = welTabY + 34;
        graphics.text(this.font, KiemLang.get("sidebar.config"), modalX + 18, configSectionY, COLOR_TEXT_DIM, false);

        // Tab: Features
        int featTabY = configSectionY + 14;
        boolean featActive = (currentTab == Tab.CONFIG);
        boolean featHover = bgMouseX >= modalX + 12 && bgMouseX <= modalX + sidebarW - 12 && bgMouseY >= featTabY && bgMouseY <= featTabY + 24;
        if (featActive) {
            drawPill(graphics, modalX + 12, featTabY, sidebarW - 24, 24, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.text(this.font, "• " + KiemLang.get("tab.features"), modalX + 22, featTabY + 8, COLOR_ACCENT, false);
        } else {
            if (featHover) {
                drawPill(graphics, modalX + 12, featTabY, sidebarW - 24, 24, 0x1AFFFFFF, 0);
            }
            graphics.text(this.font, "  " + KiemLang.get("tab.features"), modalX + 22, featTabY + 8, featHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, false);
        }

        // Tab: Cosmetics
        int cosTabY = featTabY + 28;
        boolean cosActive = (currentTab == Tab.COSMETICS);
        boolean cosHover = bgMouseX >= modalX + 12 && bgMouseX <= modalX + sidebarW - 12 && bgMouseY >= cosTabY && bgMouseY <= cosTabY + 24;
        if (cosActive) {
            drawPill(graphics, modalX + 12, cosTabY, sidebarW - 24, 24, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.text(this.font, "• " + KiemLang.get("tab.cosmetics"), modalX + 22, cosTabY + 8, COLOR_ACCENT, false);
        } else {
            if (cosHover) {
                drawPill(graphics, modalX + 12, cosTabY, sidebarW - 24, 24, 0x1AFFFFFF, 0);
            }
            graphics.text(this.font, "  " + KiemLang.get("tab.cosmetics"), modalX + 22, cosTabY + 8, cosHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, false);
        }

        // Language toggle button above close button
        int langBtnY = modalY + modalH - 56;
        boolean langHover = bgMouseX >= modalX + 12 && bgMouseX <= modalX + sidebarW - 12 && bgMouseY >= langBtnY && bgMouseY <= langBtnY + 20;
        drawPill(graphics, modalX + 12, langBtnY, sidebarW - 24, 20, langHover ? 0x2AFFFFFF : 0x18FFFFFF, langHover ? COLOR_ACCENT : COLOR_BORDER);
        if ("en".equalsIgnoreCase(KiemConfig.language)) {
            drawFlagUK(graphics, modalX + 20, langBtnY + 4);
            graphics.text(this.font, "English", modalX + 42, langBtnY + 6, langHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, false);
        } else {
            drawFlagTR(graphics, modalX + 20, langBtnY + 4);
            graphics.text(this.font, "Türkçe", modalX + 42, langBtnY + 6, langHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED, false);
        }

        // Close button at bottom of sidebar
        int closeBtnY = modalY + modalH - 30;
        boolean closeHover = bgMouseX >= modalX + 12 && bgMouseX <= modalX + sidebarW - 12 && bgMouseY >= closeBtnY && bgMouseY <= closeBtnY + 20;
        drawPill(graphics, modalX + 12, closeBtnY, sidebarW - 24, 20, closeHover ? 0x33FF4444 : 0x1AFFFFFF, 0);
        graphics.centeredText(this.font, KiemLang.get("ui.close"), modalX + sidebarW / 2, closeBtnY + 6, closeHover ? 0xFFFF7777 : COLOR_TEXT_MUTED);

        // 3. Topbar Breadcrumb Header
        int topHeaderX = modalX + sidebarW + 20;
        String currentSection = currentTab == Tab.WELCOME ? "Main" : (currentTab == Tab.CONFIG ? KiemLang.get("tab.features") : KiemLang.get("tab.cosmetics"));
        String currentSub;
        if (currentTab == Tab.WELCOME) {
            currentSub = KiemLang.get("tab.welcome");
        } else if (currentTab == Tab.COSMETICS) {
            currentSub = KiemLang.get(switch (currentCosmeticsSub) {
                case CAPES -> "cosmetics.sub.capes";
                case PETS -> "cosmetics.sub.pets";
                case TRAILS -> "cosmetics.sub.trails";
            });
        } else {
            if (configSubView == ConfigSubView.MAIN) {
                currentSub = KiemLang.get("ui.modules");
            } else if (configSubView == ConfigSubView.MINI_MODELS) {
                currentSub = KiemLang.get("mod.mini_models.title");
            } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
                currentSub = KiemLang.get("mod.viewmodel.title");
            } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
                currentSub = KiemLang.get("mod.better_third_person.title");
            } else if (configSubView == ConfigSubView.AFK_CINEMATICS) {
                currentSub = KiemLang.get("mod.afk_cinematics.title");
            } else if (configSubView == ConfigSubView.SMOOTH_ZOOM) {
                currentSub = KiemLang.get("mod.smooth_zoom.title");
            } else if (configSubView == ConfigSubView.CHAT_QOL) {
                currentSub = KiemLang.get("mod.chat_qol.title");
            } else if (configSubView == ConfigSubView.TOOLTIP_QOL) {
                currentSub = KiemLang.get("mod.tooltip_qol.title");
            } else {
                currentSub = KiemLang.get("mod.anti_afk.title");
            }
        }
        graphics.text(this.font, currentSection, topHeaderX, modalY + 15, COLOR_TEXT_WHITE, false);
        int secWidth = this.font.width(currentSection);
        graphics.text(this.font, "|  " + currentSub, topHeaderX + secWidth + 8, modalY + 15, COLOR_TEXT_MUTED, false);

        // 4. Content Area
        int contentX = modalX + sidebarW + 20;
        int contentY = modalY + topBarH + 20;
        int contentW = modalW - sidebarW - 40;

        if (currentTab == Tab.WELCOME) {
            renderWelcomeTab(graphics, contentX, contentY, contentW, bgMouseX, bgMouseY);
        } else if (currentTab == Tab.COSMETICS) {
            renderCosmeticsTab(graphics, contentX, contentY, contentW, bgMouseX, bgMouseY);
        } else {
            renderConfigTab(graphics, contentX, contentY, contentW, bgMouseX, bgMouseY);
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

    private void renderCosmeticsTab(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Sub-tabs (Capes / Pets / Auras)
        int subBtnW = 96;
        int subBtnH = 18;
        int subGap = 6;
        int sub1X = x;
        int sub2X = sub1X + subBtnW + subGap;
        int sub3X = sub2X + subBtnW + subGap;
        int subY = y - 1;

        boolean sub1Hover = mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;
        boolean sub2Hover = mouseX >= sub2X && mouseX <= sub2X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;

        boolean isCapes = (currentCosmeticsSub == CosmeticsSub.CAPES);
        boolean isPets = (currentCosmeticsSub == CosmeticsSub.PETS);

        drawPill(graphics, sub1X, subY, subBtnW, subBtnH, isCapes ? COLOR_ACCENT_BG : (sub1Hover ? 0x2AFFFFFF : 0x18FFFFFF), isCapes ? COLOR_ACCENT : (sub1Hover ? 0x66FFFFFF : COLOR_BORDER));
        graphics.centeredText(this.font, KiemLang.get("cosmetics.sub.capes"), sub1X + subBtnW / 2, subY + 5, isCapes ? COLOR_ACCENT : (sub1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));

        drawPill(graphics, sub2X, subY, subBtnW, subBtnH, isPets ? COLOR_ACCENT_BG : (sub2Hover ? 0x2AFFFFFF : 0x18FFFFFF), isPets ? COLOR_ACCENT : (sub2Hover ? 0x66FFFFFF : COLOR_BORDER));
        graphics.centeredText(this.font, KiemLang.get("cosmetics.sub.pets"), sub2X + subBtnW / 2, subY + 5, isPets ? COLOR_ACCENT : (sub2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));

        boolean isTrails = currentCosmeticsSub == CosmeticsSub.TRAILS;
        boolean sub3Hover = mouseX >= sub3X && mouseX <= sub3X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH;
        drawPill(graphics, sub3X, subY, subBtnW, subBtnH, isTrails ? COLOR_ACCENT_BG : (sub3Hover ? 0x2AFFFFFF : 0x18FFFFFF), isTrails ? COLOR_ACCENT : (sub3Hover ? 0x66FFFFFF : COLOR_BORDER));
        graphics.centeredText(this.font, KiemLang.get("cosmetics.sub.trails"), sub3X + subBtnW / 2, subY + 5, isTrails ? COLOR_ACCENT : (sub3Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));

        int dividerX = sub3X + subBtnW + 12;
        graphics.fill(dividerX, y + 8, x + w, y + 9, COLOR_BORDER);

        if (isCapes) {
            renderCapesSubView(graphics, x, y, w, mouseX, mouseY);
        } else if (isPets) {
            renderPetsSubView(graphics, x, y, w, mouseX, mouseY);
        } else {
            renderTrailsSubView(graphics, x, y, w, mouseX, mouseY);
        }
    }

    private void renderTrailsSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int cardW = 180;
        int cardH = 236;
        int card1X = x;
        int card1Y = y + 18;

        // --- CARD 1: SAKURA BLOOM TRAIL ---
        boolean active1 = KiemConfig.sakuraAuraEnabled;
        drawRoundedRect(graphics, card1X, card1Y, cardW, cardH, active1 ? COLOR_CARD_ACTIVE : COLOR_CARD_BG, active1 ? COLOR_ACTIVE_BORDER : COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("trails.sakura.title"), card1X + 12, card1Y + 11, COLOR_TEXT_WHITE, false);
        int img1X = card1X + 8;
        int img1Y = card1Y + 26;
        int img1W = cardW - 16;
        drawRoundedRect(graphics, img1X - 1, img1Y - 1, img1W + 2, 147, 0xFF12131A, COLOR_BORDER);
        graphics.blit(SAKURA_AURA_PREVIEW, img1X, img1Y, img1X + img1W, img1Y + 145, 0.0f, 1.0f, 0.0f, 1.0f);
        int btn1Y = card1Y + 179;
        boolean hover1 = mouseX >= img1X && mouseX <= img1X + img1W && mouseY >= btn1Y && mouseY <= btn1Y + 22;
        drawPill(graphics, img1X, btn1Y, img1W, 22, active1 ? COLOR_ACCENT_BG : (hover1 ? 0x2EFFFFFF : 0x18FFFFFF), active1 ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get(active1 ? "auras.disable" : "auras.enable"), card1X + cardW / 2, btn1Y + 7, active1 ? COLOR_ACCENT : COLOR_TEXT_WHITE);
        int cust1Y = btn1Y + 27;
        boolean custHover1 = mouseX >= img1X && mouseX <= img1X + img1W && mouseY >= cust1Y && mouseY <= cust1Y + 22;
        drawPill(graphics, img1X, cust1Y, img1W, 22, custHover1 ? 0x44E05688 : 0x18FFFFFF, custHover1 ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), card1X + cardW / 2, cust1Y + 7, custHover1 ? COLOR_ACCENT : COLOR_TEXT_WHITE);

        int infoY = card1Y + cardH + 8;
        graphics.text(this.font, KiemLang.get("auras.local"), x, infoY, COLOR_TEXT_MUTED, false);
        graphics.text(this.font, KiemLang.get("auras.compatible"), x, infoY + 12, COLOR_TEXT_MUTED, false);
    }

    private void renderCapesSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        int cardW = 176;
        int cardH = 236;
        int cardGap = 12;

        int card1X = x;
        int card1Y = y + 18;

        int card2X = card1X + cardW + cardGap;
        int card2Y = card1Y;

        CapeManager.CapeType current = CapeManager.getCurrentCape();

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
        int card1Bg = hasThisVersionActive ? COLOR_CARD_ACTIVE : COLOR_CARD_BG;
        int card1Border = hasThisVersionActive ? COLOR_ACTIVE_BORDER : COLOR_BORDER;
        drawRoundedRect(graphics, card1X, card1Y, cardW, cardH, card1Bg, card1Border);

        // Header: "SANARA" + [ V1 ] [ V2 ] [ V3 ]
        graphics.text(this.font, "SANARA", card1X + 14, card1Y + 11, COLOR_TEXT_WHITE, false);

        int pillW = 20;
        int pillH = 13;
        int pillGap = 4;
        int pillsTotalW = 3 * pillW + 2 * pillGap; // 68
        int p1X = card1X + cardW - 12 - pillsTotalW;
        int p2X = p1X + pillW + pillGap;
        int p3X = p2X + pillW + pillGap;
        int pillY = card1Y + 9;

        boolean p1Hover = mouseX >= p1X && mouseX <= p1X + pillW && mouseY >= pillY && mouseY <= pillY + pillH;
        boolean p2Hover = mouseX >= p2X && mouseX <= p2X + pillW && mouseY >= pillY && mouseY <= pillY + pillH;
        boolean p3Hover = mouseX >= p3X && mouseX <= p3X + pillW && mouseY >= pillY && mouseY <= pillY + pillH;

        // Draw V1 pill
        int p1Bg = (selectedSanaraVersion == 1) ? COLOR_ACCENT_BG : (p1Hover ? 0x2AFFFFFF : 0x18FFFFFF);
        int p1Bdr = (selectedSanaraVersion == 1) ? COLOR_ACCENT : (p1Hover ? 0x66FFFFFF : 0x33FFFFFF);
        int p1Txt = (selectedSanaraVersion == 1) ? COLOR_ACCENT : (p1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        drawPill(graphics, p1X, pillY, pillW, pillH, p1Bg, p1Bdr);
        graphics.centeredText(this.font, "V1", p1X + pillW / 2, pillY + 3, p1Txt);

        // Draw V2 pill
        int p2Bg = (selectedSanaraVersion == 2) ? COLOR_ACCENT_BG : (p2Hover ? 0x2AFFFFFF : 0x18FFFFFF);
        int p2Bdr = (selectedSanaraVersion == 2) ? COLOR_ACCENT : (p2Hover ? 0x66FFFFFF : 0x33FFFFFF);
        int p2Txt = (selectedSanaraVersion == 2) ? COLOR_ACCENT : (p2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        drawPill(graphics, p2X, pillY, pillW, pillH, p2Bg, p2Bdr);
        graphics.centeredText(this.font, "V2", p2X + pillW / 2, pillY + 3, p2Txt);

        // Draw V3 pill
        int p3Bg = (selectedSanaraVersion == 3) ? COLOR_ACCENT_BG : (p3Hover ? 0x2AFFFFFF : 0x18FFFFFF);
        int p3Bdr = (selectedSanaraVersion == 3) ? COLOR_ACCENT : (p3Hover ? 0x66FFFFFF : 0x33FFFFFF);
        int p3Txt = (selectedSanaraVersion == 3) ? COLOR_ACCENT : (p3Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        drawPill(graphics, p3X, pillY, pillW, pillH, p3Bg, p3Bdr);
        graphics.centeredText(this.font, "V3", p3X + pillW / 2, pillY + 3, p3Txt);

        // Preview image
        int imgW = cardW - 24; // 152
        int imgH = 145;
        int img1X = card1X + 12;
        int img1Y = card1Y + 26;
        drawRoundedRect(graphics, img1X - 1, img1Y - 1, imgW + 2, imgH + 2, 0xFF12131A, 0xFF2A2E40);

        Identifier activePreview = (selectedSanaraVersion == 1) ? SANARA_V1_PREVIEW
                : (selectedSanaraVersion == 2 ? SANARA_V2_PREVIEW : SANARA_V3_PREVIEW);
        graphics.blit(activePreview, img1X, img1Y, img1X + imgW, img1Y + imgH, 0.0f, 1.0f, 0.0f, 1.0f);

        // Version badge overlay on preview bottom-right
        String verLabel = "V" + selectedSanaraVersion;
        int verTagW = 22;
        int verTagH = 13;
        drawPill(graphics, img1X + imgW - verTagW - 4, img1Y + imgH - verTagH - 4, verTagW, verTagH, 0x9912131A, 0x44FFFFFF);
        graphics.centeredText(this.font, verLabel, img1X + imgW - verTagW / 2 - 4, img1Y + imgH - verTagH + 3, COLOR_TEXT_WHITE);

        // Standard / Animated buttons
        int btnY = img1Y + imgH + 8;
        int btnW = (imgW - 6) / 2;
        int btnH = 22;
        int btn1X = img1X;
        int btn2X = img1X + btnW + 6;

        boolean b1Hover = mouseX >= btn1X && mouseX <= btn1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean b2Hover = mouseX >= btn2X && mouseX <= btn2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        // Standard Button
        if (isThisVersionStatic) {
            drawPill(graphics, btn1X, btnY, btnW, btnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, "STANDARD", btn1X + btnW / 2, btnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, btn1X, btnY, btnW, btnH, b1Hover ? 0x2EFFFFFF : 0x18FFFFFF, b1Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, "STANDARD", btn1X + btnW / 2, btnY + 7, b1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        // Animated Button
        if (isThisVersionAnim) {
            drawPill(graphics, btn2X, btnY, btnW, btnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, "ANIMATED", btn2X + btnW / 2, btnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, btn2X, btnY, btnW, btnH, b2Hover ? 0x2EFFFFFF : 0x18FFFFFF, b2Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, "ANIMATED", btn2X + btnW / 2, btnY + 7, b2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        // Footer: status / unequip
        int footerY = btnY + btnH + 6;
        if (hasThisVersionActive) {
            boolean unequipHover = mouseX >= card1X + 16 && mouseX <= card1X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.centeredText(this.font, KiemLang.get("cosmetics.unequip"), card1X + cardW / 2, footerY + 2, unequipHover ? 0xFFFF6666 : COLOR_TEXT_DIM);
        } else if (current != CapeManager.CapeType.OFF) {
            graphics.centeredText(this.font, KiemLang.get("cosmetics.other_active"), card1X + cardW / 2, footerY + 2, 0xFF70768C);
        } else {
            graphics.centeredText(this.font, KiemLang.get("cosmetics.inactive"), card1X + cardW / 2, footerY + 2, COLOR_TEXT_DIM);
        }

        // ----------------- CARD 2: RIKKA -----------------
        boolean isRikkaStatic = (current == CapeManager.CapeType.RIKKA_STATIC);
        boolean isRikkaAnim = (current == CapeManager.CapeType.RIKKA_ANIMATED);
        boolean rikkaActive = (isRikkaStatic || isRikkaAnim);

        int card2Bg = rikkaActive ? COLOR_CARD_ACTIVE : COLOR_CARD_BG;
        int card2Border = rikkaActive ? COLOR_ACTIVE_BORDER : COLOR_BORDER;
        drawRoundedRect(graphics, card2X, card2Y, cardW, cardH, card2Bg, card2Border);

        graphics.text(this.font, "RIKKA", card2X + 14, card2Y + 11, COLOR_TEXT_WHITE, false);
        drawPill(graphics, card2X + cardW - 36, card2Y + 9, 24, 13, 0x33FFB03A, 0xFFFFB03A);
        graphics.centeredText(this.font, "NEW", card2X + cardW - 24, card2Y + 12, 0xFFFFB03A);

        int img2X = card2X + 12;
        int img2Y = card2Y + 26;
        drawRoundedRect(graphics, img2X - 1, img2Y - 1, imgW + 2, imgH + 2, 0xFF12131A, 0xFF2A2E40);
        graphics.blit(RIKKA_PREVIEW, img2X, img2Y, img2X + imgW, img2Y + imgH, 0.0f, 1.0f, 0.0f, 1.0f);

        // Version badge overlay on preview bottom-right
        drawPill(graphics, img2X + imgW - verTagW - 4, img2Y + imgH - verTagH - 4, verTagW, verTagH, 0x9912131A, 0x44FFFFFF);
        graphics.centeredText(this.font, "V1", img2X + imgW - verTagW / 2 - 4, img2Y + imgH - verTagH + 3, COLOR_TEXT_WHITE);

        int btn2_1X = img2X;
        int btn2_2X = img2X + btnW + 6;

        boolean b2_1Hover = mouseX >= btn2_1X && mouseX <= btn2_1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean b2_2Hover = mouseX >= btn2_2X && mouseX <= btn2_2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        // Rikka Standard Button
        if (isRikkaStatic) {
            drawPill(graphics, btn2_1X, btnY, btnW, btnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, "STANDARD", btn2_1X + btnW / 2, btnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, btn2_1X, btnY, btnW, btnH, b2_1Hover ? 0x2EFFFFFF : 0x18FFFFFF, b2_1Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, "STANDARD", btn2_1X + btnW / 2, btnY + 7, b2_1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        // Rikka Animated Button
        if (isRikkaAnim) {
            drawPill(graphics, btn2_2X, btnY, btnW, btnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, "ANIMATED", btn2_2X + btnW / 2, btnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, btn2_2X, btnY, btnW, btnH, b2_2Hover ? 0x2EFFFFFF : 0x18FFFFFF, b2_2Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, "ANIMATED", btn2_2X + btnW / 2, btnY + 7, b2_2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        // Rikka Footer
        if (rikkaActive) {
            boolean unequipHover2 = mouseX >= card2X + 16 && mouseX <= card2X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.centeredText(this.font, KiemLang.get("cosmetics.unequip"), card2X + cardW / 2, footerY + 2, unequipHover2 ? 0xFFFF6666 : COLOR_TEXT_DIM);
        } else if (current != CapeManager.CapeType.OFF) {
            graphics.centeredText(this.font, KiemLang.get("cosmetics.other_active"), card2X + cardW / 2, footerY + 2, 0xFF70768C);
        } else {
            graphics.centeredText(this.font, KiemLang.get("cosmetics.inactive"), card2X + cardW / 2, footerY + 2, COLOR_TEXT_DIM);
        }

        // ----------------- CARD 3: ÖZEL PELERİN (CUSTOM CAPE) -----------------
        int card3X = card2X + cardW + cardGap;
        if (card3X + cardW <= x + w + 10) {
            boolean isCustomActive = (current == CapeManager.CapeType.CUSTOM);
            int card3Bg = isCustomActive ? COLOR_CARD_ACTIVE : COLOR_CARD_BG;
            int card3Border = isCustomActive ? COLOR_ACTIVE_BORDER : COLOR_BORDER;
            drawRoundedRect(graphics, card3X, card1Y, cardW, cardH, card3Bg, card3Border);

            // Header: "ÖZEL PELERİN" on left, [ 📂 Klasör ] and [ 🔄 ] pills on right
            graphics.text(this.font, KiemLang.get("cosmetics.custom_title"), card3X + 12, card1Y + 11, COLOR_TEXT_WHITE, false);

            pillH = 13;
            int refreshPillW = 16;
            int folderPillW = 46;
            int refreshPillX = card3X + cardW - 12 - refreshPillW;
            int folderPillX = refreshPillX - folderPillW - 4;
            int customPillY = card1Y + 9;

            boolean folderHover = mouseX >= folderPillX && mouseX <= folderPillX + folderPillW && mouseY >= customPillY && mouseY <= customPillY + pillH;
            boolean refreshHover = mouseX >= refreshPillX && mouseX <= refreshPillX + refreshPillW && mouseY >= customPillY && mouseY <= customPillY + pillH;

            drawPill(graphics, folderPillX, customPillY, folderPillW, pillH, folderHover ? 0x33FFFFFF : 0x18FFFFFF, folderHover ? 0xFF667085 : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("cosmetics.open_folder"), folderPillX + folderPillW / 2, customPillY + 3, folderHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

            drawPill(graphics, refreshPillX, customPillY, refreshPillW, pillH, refreshHover ? 0x33FFFFFF : 0x18FFFFFF, refreshHover ? 0xFF667085 : COLOR_BORDER);
            graphics.centeredText(this.font, "🔄", refreshPillX + refreshPillW / 2, customPillY + 3, refreshHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

            // Preview Area (Image Preview or Empty Folder notice)
            int img3X = card3X + 12;
            int img3Y = card1Y + 26;
            drawRoundedRect(graphics, img3X - 1, img3Y - 1, imgW + 2, imgH + 2, 0xFF12131A, 0xFF2A2E40);

            CustomCapeManager.CustomCapeEntry activeCustom = CustomCapeManager.getSelectedCape();
            java.util.List<CustomCapeManager.CustomCapeEntry> capes = CustomCapeManager.getLoadedCapes();

            if (capes.isEmpty()) {
                // Empty state: helpful placeholder
                graphics.centeredText(this.font, KiemLang.get("cosmetics.empty_folder"), img3X + imgW / 2, img3Y + 32, 0xFF8A90A6);
                graphics.centeredText(this.font, KiemLang.get("cosmetics.empty_sub"), img3X + imgW / 2, img3Y + 52, 0xFF555A70);
                graphics.centeredText(this.font, KiemLang.get("cosmetics.empty_hint"), img3X + imgW / 2, img3Y + 68, 0xFF555A70);

                int openBtnW = imgW - 20;
                int openBtnH = 22;
                int openBtnX = img3X + 10;
                int openBtnY = img3Y + 98;
                boolean openHover = mouseX >= openBtnX && mouseX <= openBtnX + openBtnW && mouseY >= openBtnY && mouseY <= openBtnY + openBtnH;
                drawPill(graphics, openBtnX, openBtnY, openBtnW, openBtnH, openHover ? 0x44E05688 : 0x22FFFFFF, openHover ? COLOR_ACCENT : COLOR_BORDER);
                graphics.centeredText(this.font, KiemLang.get("cosmetics.open_folder_btn"), openBtnX + openBtnW / 2, openBtnY + 7, openHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
            } else if (activeCustom != null) {
                // Cape texture preview (Back face) - Static first frame preview (ultra-fast)
                Identifier previewTex = activeCustom.frameIdentifiers[0];
                float u0 = 192.0f / 1024.0f;
                float u1 = 352.0f / 1024.0f;
                float v0 = 16.0f / 512.0f;
                float v1 = 272.0f / 512.0f;
                graphics.blit(previewTex, img3X, img3Y, img3X + imgW, img3Y + imgH, u0, u1, v0, v1);

                // Selector bar at bottom of preview: < Name (1/3) >
                int selBarH = 16;
                int selBarY = img3Y + imgH - selBarH - 4;
                int arrowW = 16;
                int leftArrowX = img3X + 4;
                int rightArrowX = img3X + imgW - arrowW - 4;
                int selMidW = rightArrowX - (leftArrowX + arrowW);
                int selMidX = leftArrowX + arrowW;

                boolean leftArrowHover = mouseX >= leftArrowX && mouseX <= leftArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH;
                boolean rightArrowHover = mouseX >= rightArrowX && mouseX <= rightArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH;

                drawRoundedRect(graphics, img3X + 2, selBarY - 1, imgW - 4, selBarH + 2, 0xB310121A, 0x44FFFFFF);
                graphics.centeredText(this.font, "◀", leftArrowX + arrowW / 2, selBarY + 4, leftArrowHover ? COLOR_ACCENT : COLOR_TEXT_MUTED);
                graphics.centeredText(this.font, "▶", rightArrowX + arrowW / 2, selBarY + 4, rightArrowHover ? COLOR_ACCENT : COLOR_TEXT_MUTED);

                String capeLabel = activeCustom.displayName;
                if (this.font.width(capeLabel) > selMidW - 6) {
                    capeLabel = this.font.plainSubstrByWidth(capeLabel, selMidW - 14) + "...";
                }
                graphics.centeredText(this.font, capeLabel, selMidX + selMidW / 2, selBarY + 4, COLOR_TEXT_WHITE);

                // Badge top-right of preview: e.g. "GIF • 45F" or "PNG"
                String badge = activeCustom.isAnimated ? ("GIF • " + activeCustom.frameCount + "F") : "PNG";
                int badgeW = this.font.width(badge) + 8;
                int badgeH = 12;
                drawPill(graphics, img3X + imgW - badgeW - 4, img3Y + 4, badgeW, badgeH, 0x9910121A, 0x44FFFFFF);
                graphics.centeredText(this.font, badge, img3X + imgW - badgeW / 2 - 4, img3Y + 3, COLOR_TEXT_WHITE);

                // Equip / Active Button
                int equipBtnW = imgW;
                int equipBtnH = 22;
                int equipBtnX = img3X;
                int equipBtnY = img3Y + imgH + 8;
                boolean equipHover = mouseX >= equipBtnX && mouseX <= equipBtnX + equipBtnW && mouseY >= equipBtnY && mouseY <= equipBtnY + equipBtnH;

                if (isCustomActive && activeCustom.fileName.equalsIgnoreCase(KiemConfig.customCapeFileName)) {
                    drawPill(graphics, equipBtnX, equipBtnY, equipBtnW, equipBtnH, COLOR_ACCENT_BG, COLOR_ACCENT);
                    graphics.centeredText(this.font, KiemLang.get("cosmetics.equipped"), equipBtnX + equipBtnW / 2, equipBtnY + 7, COLOR_ACCENT);
                } else {
                    drawPill(graphics, equipBtnX, equipBtnY, equipBtnW, equipBtnH, equipHover ? 0x2EFFFFFF : 0x18FFFFFF, equipHover ? 0xFF4A526A : COLOR_BORDER);
                    graphics.centeredText(this.font, KiemLang.get("cosmetics.equip"), equipBtnX + equipBtnW / 2, equipBtnY + 7, equipHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
                }

                // Custom Footer
                if (isCustomActive) {
                    boolean unequipHover3 = mouseX >= card3X + 16 && mouseX <= card3X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14;
                    graphics.centeredText(this.font, KiemLang.get("cosmetics.unequip"), card3X + cardW / 2, footerY + 2, unequipHover3 ? 0xFFFF6666 : COLOR_TEXT_DIM);
                } else if (current != CapeManager.CapeType.OFF) {
                    graphics.centeredText(this.font, KiemLang.get("cosmetics.other_active"), card3X + cardW / 2, footerY + 2, 0xFF70768C);
                } else {
                    graphics.centeredText(this.font, KiemLang.get("cosmetics.inactive"), card3X + cardW / 2, footerY + 2, COLOR_TEXT_DIM);
                }
            }
        }

        // Bottom Info Card & Controls
        int infoY = card1Y + cardH + 14;
        int bottomCardH = 44;
        drawRoundedRect(graphics, x, infoY, w, bottomCardH, 0xFF1A1C26, COLOR_BORDER);
        String currentName;
        if (current == CapeManager.CapeType.OFF) {
            currentName = KiemLang.get("cosmetics.none");
        } else if (current == CapeManager.CapeType.CUSTOM) {
            currentName = KiemLang.get("cosmetics.custom_cape_prefix") + " (" + CustomCapeManager.getSelectedCapeName() + ")";
        } else {
            currentName = current.getTitle();
        }
        graphics.text(this.font, KiemLang.get("cosmetics.active_selection") + ": " + currentName, x + 14, infoY + 9, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("cosmetics.info_desc"), x + 14, infoY + 22, COLOR_TEXT_MUTED, false);

        // True Color Toggle Button
        int toggleW = 126;
        int toggleH = 22;
        int toggleX = x + w - toggleW - 12;
        int toggleY = infoY + 11;
        boolean toggleHover = mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH;
        if (KiemConfig.capeTrueColor) {
            drawPill(graphics, toggleX, toggleY, toggleW, toggleH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, KiemLang.get("cosmetics.true_color_on"), toggleX + toggleW / 2, toggleY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, toggleX, toggleY, toggleW, toggleH, toggleHover ? 0x2EFFFFFF : 0x18FFFFFF, toggleHover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("cosmetics.true_color_off"), toggleX + toggleW / 2, toggleY + 7, toggleHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }
    }

    private void renderPetsSubView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        ShoulderPetType[] petTypes = {
                ShoulderPetType.HAMSTER,
                ShoulderPetType.HAMSTER_HD,
                ShoulderPetType.OCTOPUS,
                ShoulderPetType.DRAGON,
                ShoulderPetType.INVENTORY_CURSE,
                ShoulderPetType.VOID_GARGOYLE
        };
        Identifier[] petPreviews = {
                HAMSTER_PREVIEW,
                HAMSTER_HD_PREVIEW,
                OCTOPUS_PREVIEW,
                DRAGON_PREVIEW,
                INVENTORY_CURSE_PREVIEW,
                GARGOYLE_AURA_PREVIEW
        };
        int cols = 3;
        int colGap = 10;
        int rowGap = 8;
        int cardW = (w - ((cols - 1) * colGap)) / cols;
        int cardH = 114;
        int startY = y + 18;

        for (int i = 0; i < petTypes.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int cx = x + col * (cardW + colGap);
            int cy = startY + row * (cardH + rowGap);
            renderPetCard(graphics, cx, cy, cardW, cardH, petTypes[i],
                    KiemLang.get(petTypes[i].titleKey), petPreviews[i], mouseX, mouseY);
        }

        // Bottom Info Card & Controls
        int infoY = startY + (cardH * 2 + rowGap) + 14;
        int bottomCardH = 44;
        drawRoundedRect(graphics, x, infoY, w, bottomCardH, 0xFF1A1C26, COLOR_BORDER);

        ShoulderPetType sel = ShoulderPetManager.getSelectedPet();
        String currentName = sel == ShoulderPetType.OFF ? KiemLang.get("pets.none") : KiemLang.get(sel.titleKey);
        graphics.text(this.font, KiemLang.get("pets.active_selection") + ": " + currentName, x + 14, infoY + 9, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("pets.info_desc"), x + 14, infoY + 22, COLOR_TEXT_MUTED, false);

        // Shoulder Side Selection Pills [ Sağ Omuz ] [ Sol Omuz ] + Sword Customize Pill
        boolean isLeft = ShoulderPetManager.isLeftShoulder();
        int sideBtnW = 88;
        int sideBtnH = 22;
        int sideBtnGap = 6;
        int side1X = x + w - (sideBtnW * 2 + sideBtnGap) - 12;
        int side2X = side1X + sideBtnW + sideBtnGap;
        int sideBtnY = infoY + 11;

        int swordBtnW = 104;
        int swordBtnX = side1X - swordBtnW - 6;

        if (sel == ShoulderPetType.INVENTORY_CURSE) {
            boolean swordHover = mouseX >= swordBtnX && mouseX <= swordBtnX + swordBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH;
            String swordLabel = KiemConfig.inventoryCurseWithSword ? KiemLang.get("pets.worm.sword_on") : KiemLang.get("pets.worm.sword_off");
            if (KiemConfig.inventoryCurseWithSword) {
                drawPill(graphics, swordBtnX, sideBtnY, swordBtnW, sideBtnH, COLOR_ACCENT_BG, COLOR_ACCENT);
                graphics.centeredText(this.font, swordLabel, swordBtnX + swordBtnW / 2, sideBtnY + 7, COLOR_ACCENT);
            } else {
                drawPill(graphics, swordBtnX, sideBtnY, swordBtnW, sideBtnH, swordHover ? 0x2EFFFFFF : 0x18FFFFFF, swordHover ? 0xFF4A526A : COLOR_BORDER);
                graphics.centeredText(this.font, swordLabel, swordBtnX + swordBtnW / 2, sideBtnY + 7, swordHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
            }
        }

        boolean s1Hover = mouseX >= side1X && mouseX <= side1X + sideBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH;
        boolean s2Hover = mouseX >= side2X && mouseX <= side2X + sideBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH;

        if (!isLeft) {
            drawPill(graphics, side1X, sideBtnY, sideBtnW, sideBtnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, KiemLang.get("pets.side.right"), side1X + sideBtnW / 2, sideBtnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, side1X, sideBtnY, sideBtnW, sideBtnH, s1Hover ? 0x2EFFFFFF : 0x18FFFFFF, s1Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("pets.side.right"), side1X + sideBtnW / 2, sideBtnY + 7, s1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        if (isLeft) {
            drawPill(graphics, side2X, sideBtnY, sideBtnW, sideBtnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, KiemLang.get("pets.side.left"), side2X + sideBtnW / 2, sideBtnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, side2X, sideBtnY, sideBtnW, sideBtnH, s2Hover ? 0x2EFFFFFF : 0x18FFFFFF, s2Hover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("pets.side.left"), side2X + sideBtnW / 2, sideBtnY + 7, s2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }
    }

    private void renderPetCard(GuiGraphicsExtractor graphics, int cardX, int cardY, int cardW, int cardH,
                               ShoulderPetType type, String title, Identifier preview, int mouseX, int mouseY) {
        boolean equipped = ShoulderPetManager.isEquipped(type);
        boolean anyEquipped = ShoulderPetManager.getSelectedPet() != ShoulderPetType.OFF;

        int cardBg = equipped ? COLOR_CARD_ACTIVE : COLOR_CARD_BG;
        int cardBorder = equipped ? COLOR_ACTIVE_BORDER : COLOR_BORDER;
        drawRoundedRect(graphics, cardX, cardY, cardW, cardH, cardBg, cardBorder);

        // Left square preview box
        int imgSize = cardH - 20;
        int imgX = cardX + 10;
        int imgY = cardY + 10;
        drawRoundedRect(graphics, imgX - 1, imgY - 1, imgSize + 2, imgSize + 2, 0xFF12131A, 0xFF2A2E40);
        graphics.blit(preview, imgX, imgY, imgX + imgSize, imgY + imgSize, 0.0f, 1.0f, 0.0f, 1.0f);

        // Right column: Title + Equip Button + Footer status
        int rightX = imgX + imgSize + 10;
        int rightW = cardW - imgSize - 28;
        drawBoundedText(graphics, title, rightX, cardY + 14, rightW, COLOR_TEXT_WHITE);

        int btnY = cardY + 44;
        int btnH = 22;
        boolean btnHover = mouseX >= rightX && mouseX <= rightX + rightW && mouseY >= btnY && mouseY <= btnY + btnH;

        if (equipped) {
            drawPill(graphics, rightX, btnY, rightW, btnH, COLOR_ACCENT_BG, COLOR_ACCENT);
            graphics.centeredText(this.font, KiemLang.get("cosmetics.equipped"), rightX + rightW / 2, btnY + 7, COLOR_ACCENT);
        } else {
            drawPill(graphics, rightX, btnY, rightW, btnH, btnHover ? 0x2EFFFFFF : 0x18FFFFFF, btnHover ? 0xFF4A526A : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("cosmetics.equip"), rightX + rightW / 2, btnY + 7, btnHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        int footerY = btnY + btnH + 10;
        if (equipped) {
            boolean unequipHover = mouseX >= rightX && mouseX <= rightX + rightW && mouseY >= footerY && mouseY <= footerY + 14;
            graphics.centeredText(this.font, KiemLang.get("cosmetics.unequip"), rightX + rightW / 2, footerY + 2, unequipHover ? 0xFFFF6666 : COLOR_TEXT_DIM);
        } else if (anyEquipped) {
            drawBoundedText(graphics, KiemLang.get("cosmetics.inactive"), rightX, footerY + 2, rightW, 0xFF70768C);
        } else {
            drawBoundedText(graphics, KiemLang.get("cosmetics.inactive"), rightX, footerY + 2, rightW, COLOR_TEXT_DIM);
        }
    }

    private void renderMiniModelsView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.mini_models.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);

        // Reset Button
        int resetBtnW = 60;
        int resetBtnX = x + w - resetBtnW;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, resetBtnX, y, resetBtnW, backBtnH, resetHover ? 0x44E05688 : 0x1AFFFFFF, resetHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, y + 6, resetHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, resetBtnX - 10, y + 11, COLOR_BORDER);

        int colGap = 12;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;
        int startY = y + 26;
        int rowH = 42;
        int rowGap = 6;

        // === LEFT COLUMN: KENDİNİ KÜÇÜLT (SELF) ===
        // Header Row: Kendini Küçült Toggle
        int r0Y = startY;
        drawRoundedRect(graphics, leftColX, r0Y, colW, 36, COLOR_CARD_BG, COLOR_BORDER);
        int toggleW = 56;
        int toggleH = 18;
        int t0X = leftColX + colW - toggleW - 10;
        int t0Y = r0Y + (36 - toggleH) / 2;
        int maxSelfTextW = t0X - (leftColX + 10) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.self_title"), leftColX + 10, r0Y + 8, maxSelfTextW, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.self_desc"), leftColX + 10, r0Y + 20, maxSelfTextW, COLOR_TEXT_DIM);
        boolean t0Hover = mouseX >= t0X && mouseX <= t0X + toggleW && mouseY >= t0Y && mouseY <= t0Y + toggleH;
        drawToggleButton(graphics, t0X, t0Y, toggleW, toggleH, KiemConfig.selfScaleEnabled, t0Hover);

        // Row 1: Kamerayı Etkile Checkbox
        int r1Y = r0Y + 36 + rowGap;
        drawCustomCheckbox(graphics, leftColX, r1Y, colW, 34, KiemLang.get("mod.mini_models.camera_title"), KiemLang.get("mod.mini_models.camera_desc"), KiemConfig.selfScaleCamera, mouseX, mouseY);

        // Row 2: X Ölçeği
        int r2Y = r1Y + 34 + rowGap;
        drawCustomSlider(graphics, "self_x", leftColX, r2Y, colW, rowH, KiemLang.get("mod.mini_models.x_title"), KiemLang.get("mod.mini_models.x_sub"), KiemConfig.selfScaleX, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        // Row 3: Y Ölçeği
        int r3Y = r2Y + rowH + rowGap;
        drawCustomSlider(graphics, "self_y", leftColX, r3Y, colW, rowH, KiemLang.get("mod.mini_models.y_title"), KiemLang.get("mod.mini_models.y_sub"), KiemConfig.selfScaleY, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        // Row 4: Z Ölçeği
        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "self_z", leftColX, r4Y, colW, rowH, KiemLang.get("mod.mini_models.z_title"), KiemLang.get("mod.mini_models.z_sub"), KiemConfig.selfScaleZ, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        // === RIGHT COLUMN: DİĞERLERİNİ KÜÇÜLT (OTHERS) ===
        // Header Row: Diğerlerini Küçült Toggle (tall card matching left col height)
        int r0RightH = 36 + 34 + rowGap;
        drawRoundedRect(graphics, rightColX, r0Y, colW, r0RightH, COLOR_CARD_BG, COLOR_BORDER);
        int t1X = rightColX + colW - toggleW - 12;
        int t1Y = r0Y + (r0RightH - toggleH) / 2;
        int maxOthersTextW = t1X - (rightColX + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.others_title"), rightColX + 12, r0Y + 22, maxOthersTextW, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.others_desc"), rightColX + 12, r0Y + 38, maxOthersTextW, COLOR_TEXT_DIM);
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.othersScaleEnabled, t1Hover);

        // Row 2: X Ölçeği
        drawCustomSlider(graphics, "others_x", rightColX, r2Y, colW, rowH, KiemLang.get("mod.mini_models.x_title"), KiemLang.get("mod.mini_models.x_sub"), KiemConfig.othersScaleX, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        // Row 3: Y Ölçeği
        drawCustomSlider(graphics, "others_y", rightColX, r3Y, colW, rowH, KiemLang.get("mod.mini_models.y_title"), KiemLang.get("mod.mini_models.y_sub"), KiemConfig.othersScaleY, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);

        // Row 4: Z Ölçeği
        drawCustomSlider(graphics, "others_z", rightColX, r4Y, colW, rowH, KiemLang.get("mod.mini_models.z_title"), KiemLang.get("mod.mini_models.z_sub"), KiemConfig.othersScaleZ, 0.10f, 2.00f, "%.2fx", mouseX, mouseY);
    }

    private void renderCustomViewmodelView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.viewmodel.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);

        // Master Toggle Button
        int toggleW = 56;
        int toggleH = 18;
        int resetBtnW = 60;
        int resetBtnX = x + w - resetBtnW;
        int tX = resetBtnX - toggleW - 8;
        int tY = y + 1;

        boolean tHover = mouseX >= tX && mouseX <= tX + toggleW && mouseY >= tY && mouseY <= tY + toggleH;
        drawToggleButton(graphics, tX, tY, toggleW, toggleH, KiemConfig.viewmodelEnabled, tHover);

        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, resetBtnX, y, resetBtnW, backBtnH, resetHover ? 0x44E05688 : 0x1AFFFFFF, resetHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, y + 6, resetHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int colGap = 12;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;
        int startY = y + 26;
        int rowH = 41;
        int rowGap = 5;

        // === LEFT COLUMN: SIZE & POSITION ===
        // Row 1: Size
        int r1Y = startY;
        drawCustomSlider(graphics, "vm_size", leftColX, r1Y, colW, rowH, KiemLang.get("mod.viewmodel.size_title"), KiemLang.get("mod.viewmodel.size_sub"), KiemConfig.itemSize, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        // Row 2: Scale Swing Checkbox
        int r2Y = r1Y + rowH + rowGap;
        drawCustomCheckbox(graphics, leftColX, r2Y, colW, rowH, KiemLang.get("mod.viewmodel.scale_swing_title"), KiemLang.get("mod.viewmodel.scale_swing_sub"), KiemConfig.itemScaleSwing, mouseX, mouseY);

        // Row 3: X
        int r3Y = r2Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_x", leftColX, r3Y, colW, rowH, KiemLang.get("mod.viewmodel.x_title"), KiemLang.get("mod.viewmodel.x_sub"), KiemConfig.itemPosX, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        // Row 4: Y
        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_y", leftColX, r4Y, colW, rowH, KiemLang.get("mod.viewmodel.y_title"), KiemLang.get("mod.viewmodel.y_sub"), KiemConfig.itemPosY, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        // Row 5: Z
        int r5Y = r4Y + rowH + rowGap;
        drawCustomSlider(graphics, "vm_z", leftColX, r5Y, colW, rowH, KiemLang.get("mod.viewmodel.z_title"), KiemLang.get("mod.viewmodel.z_sub"), KiemConfig.itemPosZ, -1.50f, 1.50f, "%.2f", mouseX, mouseY);

        // === RIGHT COLUMN: ROTATION & SPEED ===
        // Row 1: Yaw
        drawCustomSlider(graphics, "vm_yaw", rightColX, r1Y, colW, rowH, KiemLang.get("mod.viewmodel.yaw_title"), KiemLang.get("mod.viewmodel.yaw_sub"), KiemConfig.itemYaw, -180.0f, 180.0f, "%.1f", mouseX, mouseY);

        // Row 2: Pitch
        drawCustomSlider(graphics, "vm_pitch", rightColX, r2Y, colW, rowH, KiemLang.get("mod.viewmodel.pitch_title"), KiemLang.get("mod.viewmodel.pitch_sub"), KiemConfig.itemPitch, -180.0f, 180.0f, "%.1f", mouseX, mouseY);

        // Row 3: Roll
        drawCustomSlider(graphics, "vm_roll", rightColX, r3Y, colW, rowH, KiemLang.get("mod.viewmodel.roll_title"), KiemLang.get("mod.viewmodel.roll_sub"), KiemConfig.itemRoll, -180.0f, 180.0f, "%.1f", mouseX, mouseY);

        // Row 4: Speed (Ağır Çekim)
        drawCustomSlider(graphics, "vm_speed", rightColX, r4Y, colW, rowH, KiemLang.get("mod.viewmodel.speed_title"), KiemLang.get("mod.viewmodel.speed_sub"), KiemConfig.swingSpeed, -5.00f, 1.00f, "%.2f", mouseX, mouseY);

        // Row 5: Don't Skip Animation Checkbox
        drawCustomCheckbox(graphics, rightColX, r5Y, colW, rowH, KiemLang.get("mod.viewmodel.noskip_title"), KiemLang.get("mod.viewmodel.noskip_sub"), KiemConfig.dontSkipAnimation, mouseX, mouseY);
    }

    private void drawCustomSlider(
            GuiGraphicsExtractor graphics,
            String id,
            int x, int y, int w, int h,
            String title, String subtitle,
            float value, float min, float max,
            String format,
            double mouseX, double mouseY
    ) {
        drawRoundedRect(graphics, x, y, w, h, COLOR_CARD_BG, COLOR_BORDER);

        // Slider track on far right
        int trackW = 76;
        int trackH = 4;
        int trackX = x + w - trackW - 14;
        int trackY = y + 14;

        // Title on left (bounded to prevent overlap with track)
        graphics.text(this.font, title, x + 10, y + 8, COLOR_TEXT_WHITE, false);

        // Value text shown cleanly above/beside track on the right
        String valStr = String.format(java.util.Locale.ROOT, format, value);
        int valW = this.font.width("[ " + valStr + " ]");
        graphics.text(this.font, "[ " + valStr + " ]", trackX - valW - 8, y + 8, 0xFF3898EC, false);

        // Subtitle below title
        if (subtitle != null && !subtitle.isEmpty()) {
            graphics.text(this.font, subtitle, x + 10, y + 23, COLOR_TEXT_DIM, false);
        }

        // Min & Max text below the track
        String minStr = String.format(java.util.Locale.ROOT, format, min);
        String maxStr = String.format(java.util.Locale.ROOT, format, max);
        graphics.text(this.font, minStr, trackX, trackY + 8, COLOR_TEXT_MUTED, false);
        int maxW = this.font.width(maxStr);
        graphics.text(this.font, maxStr, trackX + trackW - maxW, trackY + 8, COLOR_TEXT_MUTED, false);

        // Track background
        graphics.fill(trackX, trackY, trackX + trackW, trackY + trackH, 0xFF282B38);

        // Progress fill
        float pct = (value - min) / (max - min);
        pct = net.minecraft.util.Mth.clamp(pct, 0.0f, 1.0f);
        int fillW = (int) (pct * trackW);
        graphics.fill(trackX, trackY, trackX + fillW, trackY + trackH, 0xFF3898EC);

        // Thumb
        int thumbW = 8;
        int thumbH = 12;
        int thumbX = trackX + fillW - thumbW / 2;
        int thumbY = trackY + trackH / 2 - thumbH / 2;
        boolean isDragging = id.equals(draggingSlider);
        boolean thumbHover = mouseX >= trackX - 4 && mouseX <= trackX + trackW + 4 && mouseY >= trackY - 8 && mouseY <= trackY + trackH + 8;
        graphics.fill(thumbX, thumbY, thumbX + thumbW, thumbY + thumbH, (thumbHover || isDragging) ? 0xFF58B0FC : 0xFF3898EC);
    }

    private void drawCustomCheckbox(
            GuiGraphicsExtractor graphics,
            int x, int y, int w, int h,
            String title, String subtitle,
            boolean checked,
            double mouseX, double mouseY
    ) {
        int boxSize = 16;
        int boxX = x + w - boxSize - 16;
        int maxCheckTextW = boxX - (x + 10) - 6;

        drawBoundedText(graphics, title, x + 10, y + 8, maxCheckTextW, COLOR_TEXT_WHITE);
        if (subtitle != null && !subtitle.isEmpty()) {
            drawBoundedText(graphics, subtitle, x + 10, y + 22, maxCheckTextW, COLOR_TEXT_DIM);
        }
        int boxY = y + (h - boxSize) / 2;
        boolean hover = mouseX >= boxX && mouseX <= boxX + boxSize && mouseY >= boxY && mouseY <= boxY + boxSize;

        drawRoundedRect(graphics, boxX, boxY, boxSize, boxSize, 0xFF141620, checked ? 0xFF3898EC : (hover ? 0xFF58B0FC : COLOR_BORDER));
        if (checked) {
            graphics.centeredText(this.font, "✓", boxX + boxSize / 2, boxY + 4, 0xFF3898EC);
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
        int trackH = 4;
        int trackX = x + w - trackW - 14;
        int trackY = y + 14;
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

    private void renderWelcomeTab(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Hero Header Banner
        int heroH = 74;
        drawRoundedRect(graphics, x, y, w, heroH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("welcome.hero_title"), x + 16, y + 14, COLOR_ACCENT, false);
        graphics.text(this.font, KiemLang.get("welcome.hero_desc"), x + 16, y + 28, COLOR_TEXT_WHITE, false);

        // Keybind Pill inside Hero Banner (Left side)
        int keyBoxX = x + 16;
        int keyBoxY = y + 46;
        int keyBoxH = 20;
        String menuKeyName = KiemConfig.getKeyName(KiemConfig.menuKey);
        graphics.text(this.font, KiemLang.get("welcome.menu_key"), keyBoxX, keyBoxY + 5, COLOR_TEXT_MUTED, false);
        int labelW = this.font.width(KiemLang.get("welcome.menu_key")) + 8;
        int btnW = 90;
        boolean keyHover = mouseX >= keyBoxX + labelW && mouseX <= keyBoxX + labelW + btnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH;
        drawKeybindButton(graphics, keyBoxX + labelW, keyBoxY, btnW, keyBoxH, menuKeyName, listeningForMenuKey, keyHover);

        // Reset to Defaults Button (Right side of Hero Banner)
        int resetBtnW = 138;
        int resetBtnX = x + w - resetBtnW - 14;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= keyBoxY && mouseY <= keyBoxY + keyBoxH;
        int resetBg = resetHover ? 0x55FF4444 : 0x26FF4444;
        int resetBorder = resetHover ? 0xFFFF6666 : 0x88FF4444;
        int resetText = resetHover ? 0xFFFFFFFF : 0xFFFF8888;
        drawPill(graphics, resetBtnX, keyBoxY, resetBtnW, keyBoxH, resetBg, resetBorder);
        graphics.centeredText(this.font, KiemLang.get("welcome.reset_all"), resetBtnX + resetBtnW / 2, keyBoxY + 6, resetText);

        // Recent Modules Section Header
        int featY = y + heroH + 10;
        graphics.text(this.font, KiemLang.get("welcome.recent_title"), x, featY, COLOR_TEXT_WHITE, false);
        int titleW = this.font.width(KiemLang.get("welcome.recent_title"));
        graphics.fill(x + titleW + 10, featY + 5, x + w, featY + 6, COLOR_BORDER);

        int listY = featY + 14;
        int itemH = 44;
        int itemGap = 6;
        int goW = 76;
        int goX = x + w - goW - 12;

        // Display up to 3 recent modules
        java.util.List<String> recents = KiemConfig.recentModules;
        int count = Math.min(3, recents.size());

        for (int i = 0; i < 3; i++) {
            int curY = listY + i * (itemH + itemGap);
            String modId = (i < count) ? recents.get(i) : (i == 0 ? "afk_cinematics" : (i == 1 ? "better_third_person" : "custom_viewmodel"));
            RecentModuleMeta meta = getModuleMeta(modId);

            boolean cardHover = mouseX >= x && mouseX <= x + w && mouseY >= curY && mouseY <= curY + itemH;
            drawRoundedRect(graphics, x, curY, w, itemH, cardHover ? COLOR_CARD_HOVER : COLOR_CARD_BG, cardHover ? COLOR_ACCENT : COLOR_BORDER);
            graphics.text(this.font, meta.title, x + 14, curY + 9, COLOR_TEXT_WHITE, false);
            graphics.text(this.font, meta.desc, x + 14, curY + 23, COLOR_TEXT_DIM, false);

            int btnCurY = curY + (itemH - 20) / 2;
            drawPill(graphics, goX, btnCurY, goW, 20, cardHover ? 0x55E05688 : 0x22FFFFFF, cardHover ? COLOR_ACCENT : COLOR_BORDER);
            graphics.centeredText(this.font, KiemLang.get("ui.edit"), goX + goW / 2, btnCurY + 6, cardHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        }

        // Quick Bottom Nav Bar (2 buttons: Features & Cosmetics)
        int navY = listY + 3 * (itemH + itemGap) + 4;
        int navBtnW = (w - 8) / 2;
        int navBtnH = 24;

        int b1X = x;
        boolean b1Hover = mouseX >= b1X && mouseX <= b1X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH;
        drawPill(graphics, b1X, navY, navBtnW, navBtnH, b1Hover ? 0x55E05688 : 0x22FFFFFF, b1Hover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("welcome.nav_features"), b1X + navBtnW / 2, navY + 7, b1Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int b2X = b1X + navBtnW + 8;
        boolean b2Hover = mouseX >= b2X && mouseX <= b2X + navBtnW && mouseY >= navY && mouseY <= navY + navBtnH;
        drawPill(graphics, b2X, navY, navBtnW, navBtnH, b2Hover ? 0x55E05688 : 0x22FFFFFF, b2Hover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("welcome.nav_cosmetics"), b2X + navBtnW / 2, navY + 7, b2Hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
    }

    private void renderResetConfirmModal(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Darkened full-screen backdrop overlay
        graphics.fill(0, 0, this.width, this.height, 0xB0000000);

        int popW = 320;
        int popH = 135;
        int popX = (this.width - popW) / 2;
        int popY = (this.height - popH) / 2;

        // Modal container with vivid border & elevated card background
        drawRoundedRect(graphics, popX, popY, popW, popH, 0xFE181924, 0xFFE05688);

        // Header warning icon & title
        graphics.text(this.font, KiemLang.get("reset_modal.title"), popX + 16, popY + 16, 0xFFFF6666, false);
        graphics.fill(popX + 16, popY + 30, popX + popW - 16, popY + 31, 0x44FFFFFF);

        // Descriptive prompt
        graphics.text(this.font, KiemLang.get("reset_modal.desc1"), popX + 16, popY + 40, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("reset_modal.desc2"), popX + 16, popY + 54, COLOR_TEXT_MUTED, false);
        graphics.text(this.font, KiemLang.get("reset_modal.desc3"), popX + 16, popY + 68, 0xFF888899, false);

        // Buttons row
        int btnW = 90;
        int btnH = 24;
        int cancelX = popX + popW - 16 - btnW;
        int confirmX = cancelX - 10 - btnW;
        int btnY = popY + popH - 36;

        boolean confirmHover = mouseX >= confirmX && mouseX <= confirmX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean cancelHover = mouseX >= cancelX && mouseX <= cancelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        // [Onayla / Sıfırla] Button (Red / Pink Warning Pill)
        int confBg = confirmHover ? 0xCCFF4444 : 0x77FF4444;
        int confBorder = confirmHover ? 0xFFFF7777 : 0xFFFF4444;
        drawPill(graphics, confirmX, btnY, btnW, btnH, confBg, confBorder);
        graphics.centeredText(this.font, KiemLang.get("ui.confirm"), confirmX + btnW / 2, btnY + 7, 0xFFFFFFFF);

        // [İptal] Button (Subtle Slate Pill)
        int cancBg = cancelHover ? 0x33FFFFFF : 0x1AFFFFFF;
        int cancBorder = cancelHover ? 0x66FFFFFF : COLOR_BORDER;
        drawPill(graphics, cancelX, btnY, btnW, btnH, cancBg, cancBorder);
        graphics.centeredText(this.font, KiemLang.get("ui.cancel"), cancelX + btnW / 2, btnY + 7, cancelHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
    }

    private void renderLanguageSelectModal(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Darkened full-screen backdrop overlay
        graphics.fill(0, 0, this.width, this.height, 0xD0000000);

        int popW = 400;
        int popH = 154;
        int popX = (this.width - popW) / 2;
        int popY = (this.height - popH) / 2;

        // Modal container with prominent border
        drawRoundedRect(graphics, popX, popY, popW, popH, 0xFE181924, COLOR_ACCENT);

        // Title
        graphics.text(this.font, "✦ " + KiemLang.get("lang.modal_title"), popX + 16, popY + 16, COLOR_ACCENT, false);
        graphics.fill(popX + 16, popY + 30, popX + popW - 16, popY + 31, 0x33FFFFFF);

        // Descriptions (safely bounded)
        drawBoundedText(graphics, KiemLang.get("lang.modal_desc"), popX + 16, popY + 40, popW - 32, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("lang.modal_desc2"), popX + 16, popY + 56, popW - 32, COLOR_TEXT_MUTED);

        // Language Buttons Row
        int btnW = 160;
        int btnH = 32;
        int trBtnX = popX + 24;
        int enBtnX = popX + popW - 24 - btnW;
        int btnY = popY + popH - 46;

        boolean trHover = mouseX >= trBtnX && mouseX <= trBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        boolean enHover = mouseX >= enBtnX && mouseX <= enBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;

        boolean isTrActive = "tr".equalsIgnoreCase(KiemConfig.language);
        boolean isEnActive = "en".equalsIgnoreCase(KiemConfig.language);

        // Turkish Button with Pixel Flag
        int trBg = isTrActive ? 0x55E05688 : (trHover ? 0x33FFFFFF : 0x1AFFFFFF);
        int trBdr = isTrActive ? COLOR_ACCENT : (trHover ? 0x88FFFFFF : COLOR_BORDER);
        drawPill(graphics, trBtnX, btnY, btnW, btnH, trBg, trBdr);
        drawFlagTR(graphics, trBtnX + 22, btnY + 10);
        graphics.text(this.font, "Türkçe (TR)", trBtnX + 46, btnY + 12, isTrActive ? COLOR_ACCENT : (trHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED), false);

        // English Button with Pixel Flag
        int enBg = isEnActive ? 0x55E05688 : (enHover ? 0x33FFFFFF : 0x1AFFFFFF);
        int enBdr = isEnActive ? COLOR_ACCENT : (enHover ? 0x88FFFFFF : COLOR_BORDER);
        drawPill(graphics, enBtnX, btnY, btnW, btnH, enBg, enBdr);
        drawFlagUK(graphics, enBtnX + 22, btnY + 10);
        graphics.text(this.font, "English (EN)", enBtnX + 46, btnY + 12, isEnActive ? COLOR_ACCENT : (enHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED), false);
    }

    private void renderConfigTab(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        if (configSubView == ConfigSubView.MAIN) {
            renderConfigMainView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.MINI_MODELS) {
            renderMiniModelsView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
            renderCustomViewmodelView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.AFK_CINEMATICS) {
            renderAfkCinematicsView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
            renderBetterThirdPersonView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.SMOOTH_ZOOM) {
            renderSmoothZoomView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.CHAT_QOL) {
            renderChatQoLView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.TOOLTIP_QOL) {
            renderTooltipQoLView(graphics, x, y, w, mouseX, mouseY);
        } else if (configSubView == ConfigSubView.ANTI_AFK) {
            renderAntiAfkView(graphics, x, y, w, mouseX, mouseY);
        }
    }

    private void renderConfigMainView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        graphics.text(this.font, KiemLang.get("ui.modules"), x, y, COLOR_TEXT_WHITE, false);
        int titleW = this.font.width(KiemLang.get("ui.modules"));
        graphics.fill(x + titleW + 10, y + 5, x + w, y + 6, COLOR_BORDER);

        int colGap = 10;
        int colW = (w - colGap) / 2;
        int leftColX = x;
        int rightColX = x + colW + colGap;

        int startY = y + 16;
        int cardH = 58;
        int cardGap = 8;
        int badgeW = 76;
        int badgeH = 20;

        // ================= LEFT COLUMN =================
        // Card 1: Küçük Adamlar (Mini Models)
        int l1Y = startY;
        boolean l1Hover = mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l1Y && mouseY <= l1Y + cardH;
        drawRoundedRect(graphics, leftColX, l1Y, colW, cardH, l1Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, l1Hover ? COLOR_ACCENT : COLOR_BORDER);
        int maxTextWL1 = (leftColX + colW - badgeW - 12) - (leftColX + 12) - 8;
        drawBoundedText(graphics, "🧍 " + KiemLang.get("mod.mini_models.title"), leftColX + 12, l1Y + 12, maxTextWL1, COLOR_TEXT_WHITE);
        String miniStatus = (KiemConfig.selfScaleEnabled || KiemConfig.othersScaleEnabled) ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.mini_models.desc") + miniStatus, leftColX + 12, l1Y + 28, maxTextWL1, (KiemConfig.selfScaleEnabled || KiemConfig.othersScaleEnabled) ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bL1X = leftColX + colW - badgeW - 10;
        int bL1Y = l1Y + (cardH - badgeH) / 2;
        drawPill(graphics, bL1X, bL1Y, badgeW, badgeH, l1Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bL1X + badgeW / 2, bL1Y + 6, COLOR_ACCENT);

        // Card 2: Custom Viewmodel & Vuruş Yavaşlatıcı
        int l2Y = l1Y + cardH + cardGap;
        boolean l2Hover = mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l2Y && mouseY <= l2Y + cardH;
        drawRoundedRect(graphics, leftColX, l2Y, colW, cardH, l2Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, l2Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "🗡 " + KiemLang.get("mod.viewmodel.title"), leftColX + 12, l2Y + 12, maxTextWL1, COLOR_TEXT_WHITE);
        String vmStatus = KiemConfig.viewmodelEnabled ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.viewmodel.desc") + vmStatus, leftColX + 12, l2Y + 28, maxTextWL1, KiemConfig.viewmodelEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bL2X = leftColX + colW - badgeW - 10;
        int bL2Y = l2Y + (cardH - badgeH) / 2;
        drawPill(graphics, bL2X, bL2Y, badgeW, badgeH, l2Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bL2X + badgeW / 2, bL2Y + 6, COLOR_ACCENT);

        // Card 3: Better Third Person
        int l3Y = l2Y + cardH + cardGap;
        boolean l3Hover = mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l3Y && mouseY <= l3Y + cardH;
        drawRoundedRect(graphics, leftColX, l3Y, colW, cardH, l3Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, l3Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "🎥 " + KiemLang.get("mod.better_third_person.title"), leftColX + 12, l3Y + 12, maxTextWL1, COLOR_TEXT_WHITE);
        String tpStatus = KiemConfig.betterThirdPersonEnabled ? " [" + KiemLang.get("ui.on") + "]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.desc") + tpStatus, leftColX + 12, l3Y + 28, maxTextWL1, KiemConfig.betterThirdPersonEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bL3X = leftColX + colW - badgeW - 10;
        int bL3Y = l3Y + (cardH - badgeH) / 2;
        drawPill(graphics, bL3X, bL3Y, badgeW, badgeH, l3Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bL3X + badgeW / 2, bL3Y + 6, COLOR_ACCENT);

        // Card 4: AFK Sinematik Kamera
        int l4Y = l3Y + cardH + cardGap;
        boolean l4Hover = mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l4Y && mouseY <= l4Y + cardH;
        drawRoundedRect(graphics, leftColX, l4Y, colW, cardH, l4Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, l4Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "🎬 " + KiemLang.get("mod.afk_cinematics.title"), leftColX + 12, l4Y + 12, maxTextWL1, COLOR_TEXT_WHITE);
        String afkCinStatus = KiemConfig.afkCinematicsEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.afkCinematicsSeconds + "s]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.desc") + afkCinStatus, leftColX + 12, l4Y + 28, maxTextWL1, KiemConfig.afkCinematicsEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bL4X = leftColX + colW - badgeW - 10;
        int bL4Y = l4Y + (cardH - badgeH) / 2;
        drawPill(graphics, bL4X, bL4Y, badgeW, badgeH, l4Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bL4X + badgeW / 2, bL4Y + 6, COLOR_ACCENT);

        // ================= RIGHT COLUMN =================
        int maxTextWR = (rightColX + colW - badgeW - 12) - (rightColX + 12) - 8;

        // Card 5: Smooth Zoom
        int r1Y = startY;
        boolean r1Hover = mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r1Y && mouseY <= r1Y + cardH;
        drawRoundedRect(graphics, rightColX, r1Y, colW, cardH, r1Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, r1Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "🔍 " + KiemLang.get("mod.smooth_zoom.title"), rightColX + 12, r1Y + 12, maxTextWR, COLOR_TEXT_WHITE);
        String zoomStatus = KiemConfig.zoomEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.zoomDefaultFactor + "x]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.smooth_zoom.desc") + zoomStatus, rightColX + 12, r1Y + 28, maxTextWR, KiemConfig.zoomEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bR1X = rightColX + colW - badgeW - 10;
        int bR1Y = r1Y + (cardH - badgeH) / 2;
        drawPill(graphics, bR1X, bR1Y, badgeW, badgeH, r1Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bR1X + badgeW / 2, bR1Y + 6, COLOR_ACCENT);

        // Card 6: Chat QoL
        int r2Y = r1Y + cardH + cardGap;
        boolean r2Hover = mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r2Y && mouseY <= r2Y + cardH;
        drawRoundedRect(graphics, rightColX, r2Y, colW, cardH, r2Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, r2Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "💬 " + KiemLang.get("mod.chat_qol.title"), rightColX + 12, r2Y + 12, maxTextWR, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.chat_qol.desc"), rightColX + 12, r2Y + 28, maxTextWR, COLOR_TEXT_MUTED);
        int bR2X = rightColX + colW - badgeW - 10;
        int bR2Y = r2Y + (cardH - badgeH) / 2;
        drawPill(graphics, bR2X, bR2Y, badgeW, badgeH, r2Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bR2X + badgeW / 2, bR2Y + 6, COLOR_ACCENT);

        // Card 7: Tooltip QoL
        int r3Y = r2Y + cardH + cardGap;
        boolean r3Hover = mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r3Y && mouseY <= r3Y + cardH;
        drawRoundedRect(graphics, rightColX, r3Y, colW, cardH, r3Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, r3Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "📜 " + KiemLang.get("mod.tooltip_qol.title"), rightColX + 12, r3Y + 12, maxTextWR, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.tooltip_qol.desc"), rightColX + 12, r3Y + 28, maxTextWR, COLOR_TEXT_MUTED);
        int bR3X = rightColX + colW - badgeW - 10;
        int bR3Y = r3Y + (cardH - badgeH) / 2;
        drawPill(graphics, bR3X, bR3Y, badgeW, badgeH, r3Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bR3X + badgeW / 2, bR3Y + 6, COLOR_ACCENT);

        // Card 8: Anti-AFK
        int r4Y = r3Y + cardH + cardGap;
        boolean r4Hover = mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r4Y && mouseY <= r4Y + cardH;
        drawRoundedRect(graphics, rightColX, r4Y, colW, cardH, r4Hover ? COLOR_CARD_HOVER : COLOR_CARD_BG, r4Hover ? COLOR_ACCENT : COLOR_BORDER);
        drawBoundedText(graphics, "🤖 " + KiemLang.get("mod.anti_afk.title"), rightColX + 12, r4Y + 12, maxTextWR, COLOR_TEXT_WHITE);
        String afkStatus = KiemConfig.antiAfkEnabled ? " [" + KiemLang.get("ui.on") + " - " + KiemConfig.antiAfkMinutes + "m / " + KiemConfig.antiAfkAction + "]" : " [" + KiemLang.get("ui.off") + "]";
        drawBoundedText(graphics, KiemLang.get("mod.anti_afk.desc") + afkStatus, rightColX + 12, r4Y + 28, maxTextWR, KiemConfig.antiAfkEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED);
        int bR4X = rightColX + colW - badgeW - 10;
        int bR4Y = r4Y + (cardH - badgeH) / 2;
        drawPill(graphics, bR4X, bR4Y, badgeW, badgeH, r4Hover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("ui.customize"), bR4X + badgeW / 2, bR4Y + 6, COLOR_ACCENT);
    }

    private void renderBetterThirdPersonView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.better_third_person.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);

        // Reset Button
        int resetBtnW = 60;
        int resetBtnX = x + w - resetBtnW;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, resetBtnX, y, resetBtnW, backBtnH, resetHover ? 0x44E05688 : 0x1AFFFFFF, resetHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, y + 6, resetHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, resetBtnX - 10, y + 11, COLOR_BORDER);

        int rowStartY = y + 26;
        int rowH = 38;
        int rowGap = 5;
        int toggleW = 56;
        int toggleH = 18;

        // Row 1: Ana Şalter (Açık / Kapalı)
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t1X = x + w - toggleW - 12;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        int maxR1W = t1X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.row1_title"), x + 12, r1Y + 8, maxR1W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.row1_desc"), x + 12, r1Y + 22, maxR1W, COLOR_TEXT_MUTED);
        boolean t1H = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.betterThirdPersonEnabled, t1H);

        // Row 2: Shoulder Surfing (Omuz Modu)
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t2X = x + w - toggleW - 12;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        int maxR2W = t2X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_title"), x + 12, r2Y + 8, maxR2W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_desc"), x + 12, r2Y + 22, maxR2W, COLOR_TEXT_MUTED);
        boolean t2H = mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH;
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.shoulderSurfingEnabled, t2H);

        // Row 3: Omuz Değiştirme Kısayolu & Yön Seçimi
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);

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
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.swap_title"), x + 12, r3Y + 8, maxR3W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.shoulder_desc"), x + 12, r3Y + 22, maxR3W, COLOR_TEXT_MUTED);

        drawPill(graphics, pillLeftX, keyBtnY, pillW, toggleH, !isRight ? 0x55E05688 : (leftH ? 0x22FFFFFF : 0x11FFFFFF), !isRight ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.better_third_person.left"), pillLeftX + pillW / 2, keyBtnY + 5, !isRight ? COLOR_ACCENT : COLOR_TEXT_MUTED);

        drawPill(graphics, pillRightX, keyBtnY, pillW, toggleH, isRight ? 0x55E05688 : (rightH ? 0x22FFFFFF : 0x11FFFFFF), isRight ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.better_third_person.right"), pillRightX + pillW / 2, keyBtnY + 5, isRight ? COLOR_ACCENT : COLOR_TEXT_MUTED);

        // Row 4: Kamera Yumuşaklığı (Smoothness / Delay)
        int r4Y = r3Y + rowH + rowGap;
        drawCustomSlider(graphics, "tp_smoothness", x, r4Y, w, rowH, KiemLang.get("mod.better_third_person.smooth_title"), KiemLang.get("mod.better_third_person.smooth_sub"), KiemConfig.thirdPersonSmoothness, 0.0f, 1.0f, "%.2f", mouseX, mouseY);

        // Row 5: Hız Esnemesi (Dynamic Speed Lag)
        int r5Y = r4Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r5Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t5X = x + w - toggleW - 12;
        int t5Y = r5Y + (rowH - toggleH) / 2;
        int maxR5W = t5X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.lag_title"), x + 12, r5Y + 8, maxR5W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.better_third_person.lag_desc"), x + 12, r5Y + 22, maxR5W, COLOR_TEXT_MUTED);
        boolean t5H = mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH;
        drawToggleButton(graphics, t5X, t5Y, toggleW, toggleH, KiemConfig.thirdPersonSpeedLag, t5H);

        // Row 6: Kamera Mesafesi (Distance)
        int r6Y = r5Y + rowH + rowGap;
        drawCustomSlider(graphics, "tp_distance", x, r6Y, w, rowH, KiemLang.get("mod.better_third_person.dist_title"), KiemLang.get("mod.better_third_person.dist_sub"), KiemConfig.thirdPersonDistance, 2.0f, 8.0f, "%.1f", mouseX, mouseY);
    }

    private void renderTooltipQoLView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.tooltip_qol.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);
        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, x + w, y + 11, COLOR_BORDER);

        int rowStartY = y + 28;
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        // --- Row 1: Tooltip Kaydırma (Scroll) ---
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.scroll_title"), x + 14, r1Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.scroll_desc"), x + 14, r1Y + 28, COLOR_TEXT_MUTED, false);

        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.tooltipScrollEnabled, t1Hover);

        // --- Row 2: Ctrl ile Zoom / Boyutlandırma ---
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.scale_title"), x + 14, r2Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.scale_desc"), x + 14, r2Y + 28, COLOR_TEXT_MUTED, false);

        int t2X = x + w - toggleW - 14;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        boolean t2Hover = mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH;
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.tooltipScaleEnabled, t2Hover);

        // --- Row 3: Mevcut Ölçek & Sıfırlama ---
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int currentPct = Math.round(KiemConfig.tooltipScale * 100.0f);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.current_scale") + currentPct + "%", x + 14, r3Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.tooltip_qol.scale_reset_desc"), x + 14, r3Y + 28, COLOR_TEXT_MUTED, false);

        int resetBtnW = 90;
        int resetBtnX = x + w - resetBtnW - 14;
        int resetBtnY = r3Y + (rowH - toggleH) / 2;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + toggleH;
        drawPill(graphics, resetBtnX, resetBtnY, resetBtnW, toggleH, resetHover ? 0x44E05688 : 0x1AFFFFFF, resetHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.tooltip_qol.reset_100"), resetBtnX + resetBtnW / 2, resetBtnY + 7, resetHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
    }

    private void renderChatQoLView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.chat_qol.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);
        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, x + w, y + 11, COLOR_BORDER);

        int rowStartY = y + 28;
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        // --- Row 1: Sınırsız Chat Logu ---
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.infinite_title"), x + 14, r1Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.infinite_desc"), x + 14, r1Y + 28, COLOR_TEXT_MUTED, false);

        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.infiniteChat, t1Hover);

        // --- Row 2: Chat Peek ---
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.peek_title"), x + 14, r2Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.peek_desc"), x + 14, r2Y + 28, COLOR_TEXT_MUTED, false);

        int t2X = x + w - toggleW - 14;
        int t2Y = r2Y + (rowH - toggleH) / 2;
        boolean t2Hover = mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH;
        drawToggleButton(graphics, t2X, t2Y, toggleW, toggleH, KiemConfig.chatPeekEnabled, t2Hover);

        int keyBtnW = 76;
        int keyBtnX = t2X - keyBtnW - 8;
        int keyBtnY = t2Y;
        boolean keyBtnHover = mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        drawKeybindButton(graphics, keyBtnX, keyBtnY, keyBtnW, toggleH, KiemConfig.getKeyName(KiemConfig.chatPeekKey), listeningForKey, keyBtnHover);

        // --- Row 3: Mesaj Birleştirici ---
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.compact_title"), x + 14, r3Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.compact_desc"), x + 14, r3Y + 28, COLOR_TEXT_MUTED, false);

        int t3X = x + w - toggleW - 14;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        boolean t3Hover = mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH;
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.compactMessages, t3Hover);

        // --- Row 4: Sohbet Kopyalama ---
        int r4Y = r3Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r4Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.copy_title"), x + 14, r4Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.chat_qol.copy_desc"), x + 14, r4Y + 28, COLOR_TEXT_MUTED, false);

        int t4X = x + w - toggleW - 14;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        boolean t4Hover = mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH;
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.copyChatEnabled, t4Hover);
    }

    private void renderSmoothZoomView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.smooth_zoom.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);
        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, x + w, y + 11, COLOR_BORDER);

        int rowStartY = y + 24;
        int rowH = 34;
        int rowGap = 4;
        int toggleW = 56;
        int toggleH = 18;

        // --- Row 1: Smooth Zoom Genel Toggle ---
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.row1_title"), x + 14, r1Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.row1_desc"), x + 14, r1Y + 19, COLOR_TEXT_MUTED, false);

        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.zoomEnabled, t1Hover);

        // --- Row 2: Tuş Ataması & Varsayılan Zoom ---
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        String zoomStr = String.format(java.util.Locale.ROOT, "%.1fx", KiemConfig.zoomDefaultFactor);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.key_title") + zoomStr, x + 14, r2Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.key_desc"), x + 14, r2Y + 19, COLOR_TEXT_MUTED, false);

        // Keybind button
        int keyBtnW = 76;
        int keyBtnX = x + w - keyBtnW - 14;
        int keyBtnY = r2Y + (rowH - toggleH) / 2;
        boolean keyBtnHover = mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        drawKeybindButton(graphics, keyBtnX, keyBtnY, keyBtnW, toggleH, KiemConfig.getKeyName(KiemConfig.zoomKey), listeningForZoomKey, keyBtnHover);

        // Plus / Minus buttons for default zoom
        int btnStepW = 20;
        int btnPlusX = keyBtnX - btnStepW - 8;
        int btnMinusX = btnPlusX - btnStepW - 4;
        boolean minusHover = mouseX >= btnMinusX && mouseX <= btnMinusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        boolean plusHover = mouseX >= btnPlusX && mouseX <= btnPlusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH;
        drawPill(graphics, btnMinusX, keyBtnY, btnStepW, toggleH, minusHover ? 0x44E05688 : 0x1AFFFFFF, minusHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, "-", btnMinusX + btnStepW / 2, keyBtnY + 4, minusHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
        drawPill(graphics, btnPlusX, keyBtnY, btnStepW, toggleH, plusHover ? 0x44E05688 : 0x1AFFFFFF, plusHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, "+", btnPlusX + btnStepW / 2, keyBtnY + 4, plusHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        // --- Row 3: Mouse Tekerleğiyle Zoom ---
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.scroll_title"), x + 14, r3Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.scroll_desc"), x + 14, r3Y + 19, COLOR_TEXT_MUTED, false);

        int t3X = x + w - toggleW - 14;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        boolean t3Hover = mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH;
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.zoomScrollEnabled, t3Hover);

        // --- Row 4: Sinematik Kamera (Buz Pateni / Atalet) ---
        int r4Y = r3Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r4Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.cinematic_title"), x + 14, r4Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.cinematic_desc"), x + 14, r4Y + 19, COLOR_TEXT_MUTED, false);

        int t4X = x + w - toggleW - 14;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        boolean t4Hover = mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH;
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.zoomCinematicCamera, t4Hover);

        // --- Row 5: Sinematik Mod (Zoom Sırasında) ---
        int r5Y = r4Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r5Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.bars_title"), x + 14, r5Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.bars_desc"), x + 14, r5Y + 19, COLOR_TEXT_MUTED, false);

        int t5X = x + w - toggleW - 14;
        int t5Y = r5Y + (rowH - toggleH) / 2;
        boolean t5Hover = mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH;
        drawToggleButton(graphics, t5X, t5Y, toggleW, toggleH, KiemConfig.zoomCinematicBars, t5Hover);

        // --- Row 6: Sinematik Mod Her Zaman (Always) ---
        int r6Y = r5Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r6Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.bars_always_title"), x + 14, r6Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.bars_always_desc"), x + 14, r6Y + 19, COLOR_TEXT_MUTED, false);

        int t6X = x + w - toggleW - 14;
        int t6Y = r6Y + (rowH - toggleH) / 2;
        boolean t6Hover = mouseX >= t6X && mouseX <= t6X + toggleW && mouseY >= t6Y && mouseY <= t6Y + toggleH;
        drawToggleButton(graphics, t6X, t6Y, toggleW, toggleH, KiemConfig.cinematicBarsAlways, t6Hover);

        // --- Row 7: Sinematik Bar Boyutu (Slider) ---
        int r7Y = r6Y + rowH + rowGap;
        drawCustomSlider(graphics, "zoom_cinematic_size", x, r7Y, w, rowH, KiemLang.get("mod.smooth_zoom.bar_size_title"), KiemLang.get("mod.smooth_zoom.bar_size_desc"), KiemConfig.cinematicBarSize * 100.0f, 4.0f, 30.0f, "%.0f%%", mouseX, mouseY);

        // --- Row 8: Hassasiyet Ölçekleme ---
        int r8Y = r7Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r8Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.sens_title"), x + 14, r8Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.sens_desc"), x + 14, r8Y + 19, COLOR_TEXT_MUTED, false);

        int t8X = x + w - toggleW - 14;
        int t8Y = r8Y + (rowH - toggleH) / 2;
        boolean t8Hover = mouseX >= t8X && mouseX <= t8X + toggleW && mouseY >= t8Y && mouseY <= t8Y + toggleH;
        drawToggleButton(graphics, t8X, t8Y, toggleW, toggleH, KiemConfig.zoomSensitivityScale, t8Hover);

        // --- Row 9: Geçiş Hızı & Ayarları Sıfırla ---
        int r9Y = r8Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r9Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.speed_title"), x + 14, r9Y + 6, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.smooth_zoom.speed_desc"), x + 14, r9Y + 19, COLOR_TEXT_MUTED, false);

        int resetBtnW = 60;
        int resetBtnX = x + w - resetBtnW - 14;
        int r9BtnsY = r9Y + (rowH - toggleH) / 2;
        boolean resetHover = mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH;
        drawPill(graphics, resetBtnX, r9BtnsY, resetBtnW, toggleH, resetHover ? 0x44E05688 : 0x1AFFFFFF, resetHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.reset"), resetBtnX + resetBtnW / 2, r9BtnsY + 5, resetHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        // Speed pills: Yavaş (8.0), Doğal (16.0), Hızlı (26.0)
        int spW = 46;
        int spGap = 4;
        int sp3X = resetBtnX - spW - 8;
        int sp2X = sp3X - spW - spGap;
        int sp1X = sp2X - spW - spGap;

        boolean sp1Active = Math.abs(KiemConfig.zoomSpeed - 8.0) < 1.0;
        boolean sp2Active = Math.abs(KiemConfig.zoomSpeed - 16.0) < 1.0;
        boolean sp3Active = Math.abs(KiemConfig.zoomSpeed - 26.0) < 1.0;

        boolean sp1Hover = mouseX >= sp1X && mouseX <= sp1X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH;
        boolean sp2Hover = mouseX >= sp2X && mouseX <= sp2X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH;
        boolean sp3Hover = mouseX >= sp3X && mouseX <= sp3X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH;

        drawPill(graphics, sp1X, r9BtnsY, spW, toggleH, sp1Active ? 0x55E05688 : (sp1Hover ? 0x22FFFFFF : 0x11FFFFFF), sp1Active ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.smooth_zoom.slow"), sp1X + spW / 2, r9BtnsY + 5, sp1Active ? COLOR_ACCENT : COLOR_TEXT_MUTED);

        drawPill(graphics, sp2X, r9BtnsY, spW, toggleH, sp2Active ? 0x55E05688 : (sp2Hover ? 0x22FFFFFF : 0x11FFFFFF), sp2Active ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.smooth_zoom.normal"), sp2X + spW / 2, r9BtnsY + 5, sp2Active ? COLOR_ACCENT : COLOR_TEXT_MUTED);

        drawPill(graphics, sp3X, r9BtnsY, spW, toggleH, sp3Active ? 0x55E05688 : (sp3Hover ? 0x22FFFFFF : 0x11FFFFFF), sp3Active ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("mod.smooth_zoom.fast"), sp3X + spW / 2, r9BtnsY + 5, sp3Active ? COLOR_ACCENT : COLOR_TEXT_MUTED);
    }

    private void renderAfkCinematicsView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.afk_cinematics.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);
        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, x + w, y + 11, COLOR_BORDER);

        int rowStartY = y + 26;
        int rowH = 40;
        int rowGap = 6;
        int toggleW = 56;
        int toggleH = 18;

        // Row 1: Ana Şalter (Master Enable/Disable)
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t1X = x + w - toggleW - 12;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        int maxR1W = t1X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.row1_title"), x + 12, r1Y + 8, maxR1W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.row1_desc"), x + 12, r1Y + 22, maxR1W, COLOR_TEXT_MUTED);
        boolean t1H = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.afkCinematicsEnabled, t1H);

        // Row 2: Hareketsizlik Süresi (30s, 60s, 120s, 300s)
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int pillW = 46;
        int pillGap = 5;
        int[] secOptions = {30, 60, 120, 300};
        String[] secLabels = {"30s", "1m", "2m", "5m"};
        int totalSecW = secOptions.length * pillW + (secOptions.length - 1) * pillGap;
        int pillsStartX = x + w - totalSecW - 12;
        int pillsY = r2Y + (rowH - toggleH) / 2;
        int maxR2W = pillsStartX - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.time_title"), x + 12, r2Y + 8, maxR2W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.time_desc"), x + 12, r2Y + 22, maxR2W, COLOR_TEXT_MUTED);

        for (int i = 0; i < secOptions.length; i++) {
            int s = secOptions[i];
            int pX = pillsStartX + i * (pillW + pillGap);
            boolean active = (KiemConfig.afkCinematicsSeconds == s);
            boolean hover = mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH;
            int pBg = active ? 0x55E05688 : (hover ? 0x22FFFFFF : 0x11FFFFFF);
            int pBdr = active ? COLOR_ACCENT : COLOR_BORDER;
            drawPill(graphics, pX, pillsY, pillW, toggleH, pBg, pBdr);
            graphics.centeredText(this.font, secLabels[i], pX + pillW / 2, pillsY + 6, active ? COLOR_ACCENT : (hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));
        }

        // Row 3: Sinematik Siyah Barlar
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t3X = x + w - toggleW - 12;
        int t3Y = r3Y + (rowH - toggleH) / 2;
        int maxR3W = t3X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.bars_title"), x + 12, r3Y + 8, maxR3W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.bars_desc"), x + 12, r3Y + 22, maxR3W, COLOR_TEXT_MUTED);
        boolean t3H = mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH;
        drawToggleButton(graphics, t3X, t3Y, toggleW, toggleH, KiemConfig.afkCinematicBars, t3H);

        // Row 4: Duvar Çarpışma Kontrolü (Raycast)
        int r4Y = r3Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r4Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int t4X = x + w - toggleW - 12;
        int t4Y = r4Y + (rowH - toggleH) / 2;
        int maxR4W = t4X - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.raycast_title"), x + 12, r4Y + 8, maxR4W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.raycast_desc"), x + 12, r4Y + 22, maxR4W, COLOR_TEXT_MUTED);
        boolean t4H = mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH;
        drawToggleButton(graphics, t4X, t4Y, toggleW, toggleH, KiemConfig.afkRaycastEnabled, t4H);

        // Row 5: Önizleme & Test Butonu
        int r5Y = r4Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r5Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        int testBtnW = 120;
        int testBtnX = x + w - testBtnW - 12;
        int testBtnY = r5Y + (rowH - 22) / 2;
        int maxR5W = testBtnX - (x + 12) - 6;
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.test_title"), x + 12, r5Y + 8, maxR5W, COLOR_TEXT_WHITE);
        drawBoundedText(graphics, KiemLang.get("mod.afk_cinematics.test_desc"), x + 12, r5Y + 22, maxR5W, COLOR_TEXT_MUTED);

        boolean testHover = mouseX >= testBtnX && mouseX <= testBtnX + testBtnW && mouseY >= testBtnY && mouseY <= testBtnY + 22;
        drawPill(graphics, testBtnX, testBtnY, testBtnW, 22, testHover ? 0x55E05688 : 0x33E05688, COLOR_ACCENT);
        graphics.centeredText(this.font, KiemLang.get("mod.afk_cinematics.test_btn"), testBtnX + testBtnW / 2, testBtnY + 7, COLOR_ACCENT);
    }

    private void renderAntiAfkView(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        // Back Button
        int backBtnW = 58;
        int backBtnH = 20;
        boolean backHover = mouseX >= x && mouseX <= x + backBtnW && mouseY >= y && mouseY <= y + backBtnH;
        drawPill(graphics, x, y, backBtnW, backBtnH, backHover ? 0x44E05688 : 0x22FFFFFF, backHover ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.back"), x + backBtnW / 2, y + 6, backHover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);

        int titleX = x + backBtnW + 12;
        String modTitle = KiemLang.get("mod.anti_afk.title");
        graphics.text(this.font, modTitle, titleX, y + 6, COLOR_TEXT_WHITE, false);
        int headerLineX = titleX + this.font.width(modTitle) + 10;
        graphics.fill(headerLineX, y + 10, x + w, y + 11, COLOR_BORDER);

        int rowStartY = y + 28;
        int rowH = 50;
        int rowGap = 8;
        int toggleW = 60;
        int toggleH = 22;

        // --- Row 1: Enable / Disable ---
        int r1Y = rowStartY;
        drawRoundedRect(graphics, x, r1Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.row1_title"), x + 14, r1Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.row1_desc"), x + 14, r1Y + 28, COLOR_TEXT_MUTED, false);

        int t1X = x + w - toggleW - 14;
        int t1Y = r1Y + (rowH - toggleH) / 2;
        boolean t1Hover = mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH;
        drawToggleButton(graphics, t1X, t1Y, toggleW, toggleH, KiemConfig.antiAfkEnabled, t1Hover);

        // --- Row 2: Zaman Seçimi (1, 2, 3, 5, 10 dakika) ---
        int r2Y = r1Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r2Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.interval_title"), x + 14, r2Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.interval_desc"), x + 14, r2Y + 28, COLOR_TEXT_MUTED, false);

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
            int pBg = active ? 0x55E05688 : (hover ? 0x22FFFFFF : 0x11FFFFFF);
            int pBdr = active ? COLOR_ACCENT : COLOR_BORDER;
            drawPill(graphics, pX, pillsY, pillW, toggleH, pBg, pBdr);
            graphics.centeredText(this.font, m + " " + KiemLang.get("mod.anti_afk.min"), pX + pillW / 2, pillsY + 6, active ? COLOR_ACCENT : (hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));
        }

        // --- Row 3: Eylem Seçimi (Shift, Jump, Click) ---
        int r3Y = r2Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r3Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.action_title"), x + 14, r3Y + 12, COLOR_TEXT_WHITE, false);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.action_desc"), x + 14, r3Y + 28, COLOR_TEXT_MUTED, false);

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
            int aBg = active ? 0x55E05688 : (hover ? 0x22FFFFFF : 0x11FFFFFF);
            int aBdr = active ? COLOR_ACCENT : COLOR_BORDER;
            drawPill(graphics, aX, actsY, actW, toggleH, aBg, aBdr);
            graphics.centeredText(this.font, lbl, aX + actW / 2, actsY + 6, active ? COLOR_ACCENT : (hover ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED));
        }

        // --- Row 4: Sıfırlama & Bilgi ---
        int r4Y = r3Y + rowH + rowGap;
        drawRoundedRect(graphics, x, r4Y, w, rowH, COLOR_CARD_BG, COLOR_BORDER);
        graphics.text(this.font, KiemLang.get("mod.anti_afk.status_title"), x + 14, r4Y + 12, COLOR_TEXT_WHITE, false);
        String infoStr;
        if ("en".equalsIgnoreCase(KiemConfig.language)) {
            infoStr = KiemConfig.antiAfkEnabled ? "Active: Triggers " + KiemConfig.antiAfkAction + " every " + KiemConfig.antiAfkMinutes + " min." : "Disabled: No automatic action is performed.";
        } else {
            infoStr = KiemConfig.antiAfkEnabled ? "Aktif: Her " + KiemConfig.antiAfkMinutes + " dakikada bir " + KiemConfig.antiAfkAction + " tetikleniyor." : "Devre dışı: Otomatik eylem yürütülmüyor.";
        }
        graphics.text(this.font, infoStr, x + 14, r4Y + 28, KiemConfig.antiAfkEnabled ? 0xFF58B0FC : COLOR_TEXT_MUTED, false);

        int resetW = 76;
        int resetX = x + w - resetW - 14;
        int resetY = r4Y + (rowH - toggleH) / 2;
        boolean resetH = mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + toggleH;
        drawPill(graphics, resetX, resetY, resetW, toggleH, resetH ? 0x44E05688 : 0x1AFFFFFF, resetH ? COLOR_ACCENT : COLOR_BORDER);
        graphics.centeredText(this.font, KiemLang.get("ui.reset"), resetX + resetW / 2, resetY + 6, resetH ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED);
    }

    private void drawToggleButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, boolean active, boolean hover) {
        int bg = active ? (hover ? 0x55E05688 : 0x33E05688) : (hover ? 0x22FFFFFF : 0x11FFFFFF);
        int border = active ? COLOR_ACCENT : COLOR_BORDER;
        int textColor = active ? COLOR_TEXT_WHITE : COLOR_TEXT_MUTED;
        drawPill(graphics, x, y, w, h, bg, border);
        String label = active ? KiemLang.get("ui.on") : KiemLang.get("ui.off");
        graphics.centeredText(this.font, label, x + w / 2, y + (h - 8) / 2, textColor);
    }

    private void drawKeybindButton(GuiGraphicsExtractor graphics, int x, int y, int w, int h, String keyText, boolean listening, boolean hover) {
        int bg = listening ? 0x55E05688 : (hover ? 0x22FFFFFF : 0x181A24);
        int border = listening ? COLOR_ACCENT : COLOR_BORDER;
        int textColor = listening ? COLOR_ACCENT : COLOR_TEXT_WHITE;
        drawPill(graphics, x, y, w, h, bg, border);
        String text = listening ? KiemLang.get("ui.press_key") : "[ " + keyText + " ]";
        graphics.centeredText(this.font, text, x + w / 2, y + (h - 8) / 2, textColor);
    }

    private void drawBoundedText(GuiGraphicsExtractor graphics, String text, int x, int y, int maxWidth, int color) {
        if (text == null || text.isEmpty() || maxWidth <= 0) return;
        if (this.font.width(text) <= maxWidth) {
            graphics.text(this.font, text, x, y, color, false);
        } else {
            String dots = "...";
            int dotsW = this.font.width(dots);
            int available = maxWidth - dotsW;
            if (available <= 0) return;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (this.font.width(sb.toString() + ch) > available) break;
                sb.append(ch);
            }
            graphics.text(this.font, sb.toString() + dots, x, y, color, false);
        }
    }

    private void drawFlagTR(GuiGraphicsExtractor graphics, int x, int y) {
        // 16x11 Red background
        graphics.fill(x, y, x + 16, y + 11, 0xFFE30A17);
        // Border subtle outline
        graphics.fill(x, y, x + 16, y + 1, 0x55000000);
        graphics.fill(x, y + 10, x + 16, y + 11, 0x55000000);
        graphics.fill(x, y, x + 1, y + 11, 0x55000000);
        graphics.fill(x + 15, y, x + 16, y + 11, 0x55000000);
        // White Crescent outer
        graphics.fill(x + 3, y + 2, x + 8, y + 9, 0xFFFFFFFF);
        // Red Crescent cut
        graphics.fill(x + 5, y + 3, x + 9, y + 8, 0xFFE30A17);
        // White Star
        graphics.fill(x + 9, y + 4, x + 12, y + 7, 0xFFFFFFFF);
        graphics.fill(x + 10, y + 3, x + 11, y + 8, 0xFFFFFFFF);
    }

    private void drawFlagUK(GuiGraphicsExtractor graphics, int x, int y) {
        // 16x11 Royal Blue background
        graphics.fill(x, y, x + 16, y + 11, 0xFF00247D);
        // Border subtle outline
        graphics.fill(x, y, x + 16, y + 1, 0x55000000);
        graphics.fill(x, y + 10, x + 16, y + 11, 0x55000000);
        graphics.fill(x, y, x + 1, y + 11, 0x55000000);
        graphics.fill(x + 15, y, x + 16, y + 11, 0x55000000);
        // White diagonal corners
        graphics.fill(x + 1, y + 1, x + 4, y + 3, 0xFFFFFFFF);
        graphics.fill(x + 12, y + 1, x + 15, y + 3, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 8, x + 4, y + 10, 0xFFFFFFFF);
        graphics.fill(x + 12, y + 8, x + 15, y + 10, 0xFFFFFFFF);
        // White broad cross
        graphics.fill(x + 6, y, x + 10, y + 11, 0xFFFFFFFF);
        graphics.fill(x, y + 4, x + 16, y + 7, 0xFFFFFFFF);
        // Red St George cross
        graphics.fill(x + 7, y, x + 9, y + 11, 0xFFCF142B);
        graphics.fill(x, y + 5, x + 16, y + 6, 0xFFCF142B);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean wasHandled) {
        if (event.button() == 0) { // Left click
            double mouseX = event.x();
            double mouseY = event.y();

            // Intercept modal clicks first if Language Selection Modal is active
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

                // Turkish select
                if (mouseX >= trBtnX && mouseX <= trBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemConfig.language = "tr";
                    KiemConfig.hasSelectedLanguage = true;
                    KiemConfig.save();
                    showLanguageModal = false;
                    return true;
                }

                // English select
                if (mouseX >= enBtnX && mouseX <= enBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemConfig.language = "en";
                    KiemConfig.hasSelectedLanguage = true;
                    KiemConfig.save();
                    showLanguageModal = false;
                    return true;
                }

                // Consume all clicks while language modal is open
                return true;
            }

            // Intercept modal clicks if Reset Confirmation Modal is active
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

                // [Sıfırla / Onayla] click
                if (mouseX >= confirmX && mouseX <= confirmX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    KiemConfig.resetToDefaults();
                    showResetConfirmModal = false;
                    return true;
                }

                // [İptal] click
                if (mouseX >= cancelX && mouseX <= cancelX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    showResetConfirmModal = false;
                    return true;
                }

                // Click outside modal dialog to dismiss
                if (mouseX < popX || mouseX > popX + popW || mouseY < popY || mouseY > popY + popH) {
                    showResetConfirmModal = false;
                    return true;
                }

                // Consume all other clicks while modal is open
                return true;
            }

            int modalW = Math.min(740, this.width - 20);
            int modalH = Math.min(430, this.height - 20);
            int modalX = (this.width - modalW) / 2;
            int modalY = (this.height - modalH) / 2;
            int sidebarW = 140;

            // Tab clicks
            int tabStartY = modalY + 50;
            int welTabY = tabStartY + 14;
            if (mouseX >= modalX + 12 && mouseX <= modalX + sidebarW - 12 && mouseY >= welTabY && mouseY <= welTabY + 24) {
                currentTab = Tab.WELCOME;
                configSubView = ConfigSubView.MAIN;
                listeningForKey = false;
                listeningForZoomKey = false;
                listeningForMenuKey = false;
                listeningForShoulderKey = false;
                return true;
            }

            int configSectionY = welTabY + 34;
            int featTabY = configSectionY + 14;
            if (mouseX >= modalX + 12 && mouseX <= modalX + sidebarW - 12 && mouseY >= featTabY && mouseY <= featTabY + 24) {
                currentTab = Tab.CONFIG;
                configSubView = ConfigSubView.MAIN;
                listeningForKey = false;
                listeningForZoomKey = false;
                listeningForMenuKey = false;
                listeningForShoulderKey = false;
                return true;
            }

            int cosTabY = featTabY + 28;
            if (mouseX >= modalX + 12 && mouseX <= modalX + sidebarW - 12 && mouseY >= cosTabY && mouseY <= cosTabY + 24) {
                currentTab = Tab.COSMETICS;
                configSubView = ConfigSubView.MAIN;
                listeningForKey = false;
                listeningForZoomKey = false;
                listeningForMenuKey = false;
                listeningForShoulderKey = false;
                return true;
            }

            // Language switch button click above close button
            int langBtnY = modalY + modalH - 56;
            if (mouseX >= modalX + 12 && mouseX <= modalX + sidebarW - 12 && mouseY >= langBtnY && mouseY <= langBtnY + 20) {
                if ("tr".equalsIgnoreCase(KiemConfig.language)) {
                    KiemConfig.language = "en";
                } else {
                    KiemConfig.language = "tr";
                }
                KiemConfig.hasSelectedLanguage = true;
                KiemConfig.save();
                return true;
            }

            // Close button click
            int closeBtnY = modalY + modalH - 30;
            if (mouseX >= modalX + 12 && mouseX <= modalX + sidebarW - 12 && mouseY >= closeBtnY && mouseY <= closeBtnY + 20) {
                this.onClose();
                return true;
            }

            // In Welcome tab:
            if (currentTab == Tab.WELCOME) {
                int contentX = modalX + sidebarW + 20;
                int contentY = modalY + 40 + 20;
                int contentW = modalW - sidebarW - 40;

                // Keybind button click inside Hero Banner
                int keyBoxX = contentX + 16;
                int keyBoxY = contentY + 46;
                int labelW = this.font.width("Menü Kısayolu:") + 8;
                int btnW = 90;
                int btnH = 20;
                if (mouseX >= keyBoxX + labelW && mouseX <= keyBoxX + labelW + btnW && mouseY >= keyBoxY && mouseY <= keyBoxY + btnH) {
                    listeningForMenuKey = !listeningForMenuKey;
                    return true;
                }

                // Reset to Defaults Button Click inside Hero Banner
                int resetBtnW = 126;
                int resetBtnX = contentX + contentW - resetBtnW - 14;
                if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= keyBoxY && mouseY <= keyBoxY + btnH) {
                    showResetConfirmModal = true;
                    return true;
                }

                // Dynamic Recent Modules Cards Click
                int featY = contentY + 74 + 10;
                int listY = featY + 14;
                int itemH = 44;
                int itemGap = 6;
                java.util.List<String> recents = KiemConfig.recentModules;
                int count = Math.min(3, recents.size());

                for (int i = 0; i < 3; i++) {
                    int curY = listY + i * (itemH + itemGap);
                    if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= curY && mouseY <= curY + itemH) {
                        String modId = (i < count) ? recents.get(i) : (i == 0 ? "afk_cinematics" : (i == 1 ? "better_third_person" : "custom_viewmodel"));
                        RecentModuleMeta meta = getModuleMeta(modId);
                        currentTab = meta.tab;
                        configSubView = meta.configSub;
                        listeningForKey = false;
                        listeningForZoomKey = false;
                        listeningForMenuKey = false;
                        listeningForShoulderKey = false;
                        return true;
                    }
                }

                // Quick Bottom Nav Bar clicks (2 buttons: Features & Cosmetics)
                int navY = listY + 3 * (itemH + itemGap) + 4;
                int navBtnW = (contentW - 8) / 2;
                int navBtnH = 24;

                int b1X = contentX;
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
            }

            // In Cosmetics tab:
            if (currentTab == Tab.COSMETICS) {
                int contentX = modalX + sidebarW + 20;
                int contentY = modalY + 40 + 20;

                // Sub-tabs (Capes / Pets / Auras) click
                int subBtnW = 96;
                int subBtnH = 18;
                int subGap = 6;
                int sub1X = contentX;
                int sub2X = sub1X + subBtnW + subGap;
                int sub3X = sub2X + subBtnW + subGap;
                int subY = contentY - 1;

                if (mouseX >= sub1X && mouseX <= sub1X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    currentCosmeticsSub = CosmeticsSub.CAPES;
                    return true;
                }
                if (mouseX >= sub2X && mouseX <= sub2X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    currentCosmeticsSub = CosmeticsSub.PETS;
                    return true;
                }
                if (mouseX >= sub3X && mouseX <= sub3X + subBtnW && mouseY >= subY && mouseY <= subY + subBtnH) {
                    currentCosmeticsSub = CosmeticsSub.TRAILS;
                    return true;
                }
                if (currentCosmeticsSub == CosmeticsSub.TRAILS) {
                    int cardW = 180;
                    int card1X = contentX;
                    int cardY = contentY + 18;
                    int btnY = cardY + 179;
                    int custY = btnY + 27;

                    // Card 1: Sakura Bloom Trail
                    int btn1X = card1X + 8;
                    int img1W = cardW - 16;
                    if (mouseX >= btn1X && mouseX <= btn1X + img1W && mouseY >= btnY && mouseY <= btnY + 22) {
                        boolean active1 = KiemConfig.sakuraAuraEnabled;
                        KiemConfig.setActiveAura(active1 ? "NONE" : "SAKURA_LOP");
                        KiemConfig.save();
                        return true;
                    }
                    if (mouseX >= btn1X && mouseX <= btn1X + img1W && mouseY >= custY && mouseY <= custY + 22) {
                        KiemCompat.setScreen(this.minecraft, new SakuraAuraScreen(this));
                        return true;
                    }
                }

                if (currentCosmeticsSub == CosmeticsSub.CAPES) {
                    int cardW = 176;
                    int cardGap = 12;
                    int card1X = contentX;
                    int card1Y = contentY + 18;
                    int card2X = card1X + cardW + cardGap;

                // --- CARD 1 (SANARA) ---
                int pillW = 20;
                int pillH = 13;
                int pillGap = 4;
                int pillsTotalW = 3 * pillW + 2 * pillGap; // 68
                int p1X = card1X + cardW - 12 - pillsTotalW;
                int p2X = p1X + pillW + pillGap;
                int p3X = p2X + pillW + pillGap;
                int pillY = card1Y + 9;

                // Pill clicks
                if (mouseX >= p1X && mouseX <= p1X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                    selectedSanaraVersion = 1;
                    return true;
                }
                if (mouseX >= p2X && mouseX <= p2X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                    selectedSanaraVersion = 2;
                    return true;
                }
                if (mouseX >= p3X && mouseX <= p3X + pillW && mouseY >= pillY && mouseY <= pillY + pillH) {
                    selectedSanaraVersion = 3;
                    return true;
                }

                int imgW = cardW - 24;
                int imgH = 145;
                int img1X = card1X + 12;
                int btnY = card1Y + 26 + imgH + 8;
                int btnW = (imgW - 6) / 2;
                int btnH = 22;
                int btn1X = img1X;
                int btn2X = img1X + btnW + 6;
                int footerY = btnY + btnH + 6;

                // Sanara Standard Button Click
                if (mouseX >= btn1X && mouseX <= btn1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.CapeType target = (selectedSanaraVersion == 1) ? CapeManager.CapeType.SANARA_STATIC
                            : (selectedSanaraVersion == 2 ? CapeManager.CapeType.SANARA_V2_STATIC : CapeManager.CapeType.SANARA_V3_STATIC);
                    if (CapeManager.getCurrentCape() == target) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                    } else {
                        CapeManager.setCurrentCape(target);
                    }
                    return true;
                }

                // Sanara Animated Button Click
                if (mouseX >= btn2X && mouseX <= btn2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    CapeManager.CapeType target = (selectedSanaraVersion == 1) ? CapeManager.CapeType.SANARA_ANIMATED
                            : (selectedSanaraVersion == 2 ? CapeManager.CapeType.SANARA_V2_ANIMATED : CapeManager.CapeType.SANARA_V3_ANIMATED);
                    if (CapeManager.getCurrentCape() == target) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                    } else {
                        CapeManager.setCurrentCape(target);
                    }
                    return true;
                }

                // Sanara Unequip Click
                if (mouseX >= card1X + 16 && mouseX <= card1X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14) {
                    CapeManager.CapeType c = CapeManager.getCurrentCape();
                    if (c == CapeManager.CapeType.SANARA_STATIC || c == CapeManager.CapeType.SANARA_ANIMATED
                            || c == CapeManager.CapeType.SANARA_V2_STATIC || c == CapeManager.CapeType.SANARA_V2_ANIMATED
                            || c == CapeManager.CapeType.SANARA_V3_STATIC || c == CapeManager.CapeType.SANARA_V3_ANIMATED) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                        return true;
                    }
                }

                // --- CARD 2 (RIKKA) ---
                int btn2_1X = card2X + 12;
                int btn2_2X = btn2_1X + btnW + 6;

                // Rikka Standard Button Click
                if (mouseX >= btn2_1X && mouseX <= btn2_1X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    if (CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_STATIC) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                    } else {
                        CapeManager.setCurrentCape(CapeManager.CapeType.RIKKA_STATIC);
                    }
                    return true;
                }

                // Rikka Animated Button Click
                if (mouseX >= btn2_2X && mouseX <= btn2_2X + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    if (CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_ANIMATED) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                    } else {
                        CapeManager.setCurrentCape(CapeManager.CapeType.RIKKA_ANIMATED);
                    }
                    return true;
                }

                // Rikka Unequip Click
                if (mouseX >= card2X + 16 && mouseX <= card2X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14) {
                    if (CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_STATIC || CapeManager.getCurrentCape() == CapeManager.CapeType.RIKKA_ANIMATED) {
                        CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                        return true;
                    }
                }

                // --- CARD 3 (CUSTOM CAPE) ---
                int card3X = card2X + cardW + cardGap;
                int customPillY = card1Y + 9;
                int refreshPillW = 16;
                int folderPillW = 46;
                int refreshPillX = card3X + cardW - 12 - refreshPillW;
                int folderPillX = refreshPillX - folderPillW - 4;
                pillH = 13;

                // Folder Pill Click (open explorer)
                if (mouseX >= folderPillX && mouseX <= folderPillX + folderPillW && mouseY >= customPillY && mouseY <= customPillY + pillH) {
                    CustomCapeManager.openCapesFolder();
                    return true;
                }

                // Refresh Pill Click
                if (mouseX >= refreshPillX && mouseX <= refreshPillX + refreshPillW && mouseY >= customPillY && mouseY <= customPillY + pillH) {
                    CustomCapeManager.forceRescan();
                    return true;
                }

                int img3X = card3X + 12;
                int img3Y = card1Y + 26;
                java.util.List<CustomCapeManager.CustomCapeEntry> capes = CustomCapeManager.getLoadedCapes();

                if (capes.isEmpty()) {
                    // Empty folder: Open Folder button click
                    int openBtnW = imgW - 20;
                    int openBtnH = 22;
                    int openBtnX = img3X + 10;
                    int openBtnY = img3Y + 98;
                    if (mouseX >= openBtnX && mouseX <= openBtnX + openBtnW && mouseY >= openBtnY && mouseY <= openBtnY + openBtnH) {
                        CustomCapeManager.openCapesFolder();
                        return true;
                    }
                } else {
                    // Selector arrows
                    int selBarH = 16;
                    int selBarY = img3Y + imgH - selBarH - 4;
                    int arrowW = 16;
                    int leftArrowX = img3X + 4;
                    int rightArrowX = img3X + imgW - arrowW - 4;

                    // Left arrow (previous cape)
                    if (mouseX >= leftArrowX && mouseX <= leftArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH) {
                        CustomCapeManager.setSelectedIndex(CustomCapeManager.getSelectedIndex() - 1);
                        return true;
                    }

                    // Right arrow (next cape)
                    if (mouseX >= rightArrowX && mouseX <= rightArrowX + arrowW && mouseY >= selBarY && mouseY <= selBarY + selBarH) {
                        CustomCapeManager.setSelectedIndex(CustomCapeManager.getSelectedIndex() + 1);
                        return true;
                    }

                    // Equip Button Click
                    int equipBtnW = imgW;
                    int equipBtnH = 22;
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

                    // Unequip Click
                    if (mouseX >= card3X + 16 && mouseX <= card3X + cardW - 16 && mouseY >= footerY && mouseY <= footerY + 14) {
                        if (CapeManager.getCurrentCape() == CapeManager.CapeType.CUSTOM) {
                            CapeManager.setCurrentCape(CapeManager.CapeType.OFF);
                            return true;
                        }
                    }
                }

                // Bottom Info Card - Controls Click
                int cardH = 236;
                int infoY = card1Y + cardH + 14;

                // True Color Toggle
                int toggleW = 126;
                int toggleH = 22;
                int toggleX = contentX + (modalW - sidebarW - 40) - toggleW - 12;
                int toggleY = infoY + 11;
                if (mouseX >= toggleX && mouseX <= toggleX + toggleW && mouseY >= toggleY && mouseY <= toggleY + toggleH) {
                    KiemConfig.capeTrueColor = !KiemConfig.capeTrueColor;
                    KiemConfig.save();
                    return true;
                }
            } else if (currentCosmeticsSub == CosmeticsSub.PETS) {
                ShoulderPetType[] petTypes = {
                        ShoulderPetType.HAMSTER,
                        ShoulderPetType.HAMSTER_HD,
                        ShoulderPetType.OCTOPUS,
                        ShoulderPetType.DRAGON,
                        ShoulderPetType.INVENTORY_CURSE,
                        ShoulderPetType.VOID_GARGOYLE
                };
                int contentW = modalW - sidebarW - 40;
                int cols = 3;
                int colGap = 10;
                int rowGap = 8;
                int cardW = (contentW - ((cols - 1) * colGap)) / cols;
                int cardH = 114;
                int startY = contentY + 18;

                int imgSize = cardH - 20;
                int btnH = 22;

                for (int i = 0; i < petTypes.length; i++) {
                    int col = i % cols;
                    int row = i / cols;
                    int cx = contentX + col * (cardW + colGap);
                    int cy = startY + row * (cardH + rowGap);
                    int rightX = cx + 10 + imgSize + 10;
                    int rightW = cardW - imgSize - 28;
                    int btnY = cy + 44;
                    int footerY = btnY + btnH + 10;

                    if (mouseX >= rightX && mouseX <= rightX + rightW && mouseY >= btnY && mouseY <= btnY + btnH) {
                        ShoulderPetManager.togglePet(petTypes[i]);
                        return true;
                    }
                    if (mouseX >= rightX && mouseX <= rightX + rightW && mouseY >= footerY && mouseY <= footerY + 14) {
                        if (ShoulderPetManager.isEquipped(petTypes[i])) {
                            ShoulderPetManager.setSelectedPet(ShoulderPetType.OFF);
                            return true;
                        }
                    }
                }

                // Bottom Controls - Shoulder Side selection & Sword Customize Toggle
                int infoY = startY + (cardH * 2 + rowGap) + 14;
                int sideBtnW = 88;
                int sideBtnH = 22;
                int sideBtnGap = 6;
                int side1X = contentX + contentW - (sideBtnW * 2 + sideBtnGap) - 12;
                int side2X = side1X + sideBtnW + sideBtnGap;
                int sideBtnY = infoY + 11;
                ShoulderPetType sel = ShoulderPetManager.getSelectedPet();

                if (sel == ShoulderPetType.INVENTORY_CURSE) {
                    int swordBtnW = 104;
                    int swordBtnX = side1X - swordBtnW - 6;
                    if (mouseX >= swordBtnX && mouseX <= swordBtnX + swordBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH) {
                        KiemConfig.inventoryCurseWithSword = !KiemConfig.inventoryCurseWithSword;
                        KiemConfig.save();
                        return true;
                    }
                }

                // Right shoulder button click
                if (mouseX >= side1X && mouseX <= side1X + sideBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH) {
                    ShoulderPetManager.setLeftShoulder(false);
                    return true;
                }
                // Left shoulder button click
                if (mouseX >= side2X && mouseX <= side2X + sideBtnW && mouseY >= sideBtnY && mouseY <= sideBtnY + sideBtnH) {
                    ShoulderPetManager.setLeftShoulder(true);
                    return true;
                }
            }
        }

            // In Config tab (Features):
            if (currentTab == Tab.CONFIG) {
                int contentX = modalX + sidebarW + 20;
                int contentY = modalY + 40 + 20;
                int contentW = modalW - sidebarW - 40;

                if (configSubView == ConfigSubView.MAIN) {
                    int colGap = 10;
                    int colW = (contentW - colGap) / 2;
                    int leftColX = contentX;
                    int rightColX = contentX + colW + colGap;

                    int startY = contentY + 16;
                    int cardH = 58;
                    int cardGap = 8;

                    // Left Column Cards:
                    // Card 1: Küçük Adamlar (Mini Models)
                    int l1Y = startY;
                    if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l1Y && mouseY <= l1Y + cardH) {
                        configSubView = ConfigSubView.MINI_MODELS;
                        return true;
                    }
                    // Card 2: Custom Viewmodel & Vuruş Yavaşlatıcı
                    int l2Y = l1Y + cardH + cardGap;
                    if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l2Y && mouseY <= l2Y + cardH) {
                        configSubView = ConfigSubView.CUSTOM_VIEWMODEL;
                        return true;
                    }
                    // Card 3: Better Third Person
                    int l3Y = l2Y + cardH + cardGap;
                    if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l3Y && mouseY <= l3Y + cardH) {
                        configSubView = ConfigSubView.BETTER_THIRD_PERSON;
                        listeningForShoulderKey = false;
                        return true;
                    }
                    // Card 4: AFK Sinematik Kamera
                    int l4Y = l3Y + cardH + cardGap;
                    if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= l4Y && mouseY <= l4Y + cardH) {
                        configSubView = ConfigSubView.AFK_CINEMATICS;
                        return true;
                    }

                    // Right Column Cards:
                    // Card 5: Smooth Zoom
                    int r1Y = startY;
                    if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r1Y && mouseY <= r1Y + cardH) {
                        configSubView = ConfigSubView.SMOOTH_ZOOM;
                        listeningForZoomKey = false;
                        return true;
                    }
                    // Card 6: Chat QoL
                    int r2Y = r1Y + cardH + cardGap;
                    if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r2Y && mouseY <= r2Y + cardH) {
                        configSubView = ConfigSubView.CHAT_QOL;
                        listeningForKey = false;
                        return true;
                    }
                    // Card 7: Tooltip QoL
                    int r3Y = r2Y + cardH + cardGap;
                    if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r3Y && mouseY <= r3Y + cardH) {
                        configSubView = ConfigSubView.TOOLTIP_QOL;
                        return true;
                    }
                    // Card 8: Anti-AFK
                    int r4Y = r3Y + cardH + cardGap;
                    if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r4Y && mouseY <= r4Y + cardH) {
                        configSubView = ConfigSubView.ANTI_AFK;
                        return true;
                    }
                } else if (configSubView == ConfigSubView.MINI_MODELS) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        return true;
                    }

                    // Reset button click
                    int resetBtnW = 60;
                    int resetBtnX = contentX + contentW - resetBtnW;
                    if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
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
                        return true;
                    }

                    int colGap = 12;
                    int colW = (contentW - colGap) / 2;
                    int leftColX = contentX;
                    int rightColX = contentX + colW + colGap;
                    int startY = contentY + 26;
                    int rowH = 42;
                    int rowGap = 6;

                    // Kendini Küçült Toggle
                    int r0Y = startY;
                    int toggleW = 56;
                    int toggleH = 18;
                    int t0X = leftColX + colW - toggleW - 10;
                    int t0Y = r0Y + (36 - toggleH) / 2;
                    if (mouseX >= t0X && mouseX <= t0X + toggleW && mouseY >= t0Y && mouseY <= t0Y + toggleH) {
                        KiemConfig.selfScaleEnabled = !KiemConfig.selfScaleEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Kamerayı Etkile Checkbox
                    int r1Y = r0Y + 36 + rowGap;
                    if (mouseX >= leftColX && mouseX <= leftColX + colW && mouseY >= r1Y && mouseY <= r1Y + 34) {
                        KiemConfig.selfScaleCamera = !KiemConfig.selfScaleCamera;
                        KiemConfig.save();
                        return true;
                    }

                    // Self Sliders
                    int r2Y = r1Y + 34 + rowGap;
                    if (checkSliderClick("self_x", leftColX, r2Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;

                    int r3Y = r2Y + rowH + rowGap;
                    if (checkSliderClick("self_y", leftColX, r3Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;

                    int r4Y = r3Y + rowH + rowGap;
                    if (checkSliderClick("self_z", leftColX, r4Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;

                    // Diğerlerini Küçült Toggle
                    int r0RightH = 36 + 34 + rowGap;
                    int t1X = rightColX + colW - toggleW - 12;
                    int t1Y = r0Y + (r0RightH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.othersScaleEnabled = !KiemConfig.othersScaleEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Others Sliders
                    if (checkSliderClick("others_x", rightColX, r2Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
                    if (checkSliderClick("others_y", rightColX, r3Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;
                    if (checkSliderClick("others_z", rightColX, r4Y, colW, rowH, 0.10f, 2.00f, mouseX, mouseY)) return true;

                } else if (configSubView == ConfigSubView.CUSTOM_VIEWMODEL) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        return true;
                    }

                    // Master Toggle Button
                    int toggleW = 56;
                    int toggleH = 18;
                    int resetBtnW = 60;
                    int resetBtnX = contentX + contentW - resetBtnW;
                    int tX = resetBtnX - toggleW - 8;
                    int tY = contentY + 1;
                    if (mouseX >= tX && mouseX <= tX + toggleW && mouseY >= tY && mouseY <= tY + toggleH) {
                        KiemConfig.viewmodelEnabled = !KiemConfig.viewmodelEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Reset button click
                    if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
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
                        return true;
                    }

                    int colGap = 12;
                    int colW = (contentW - colGap) / 2;
                    int leftColX = contentX;
                    int rightColX = contentX + colW + colGap;
                    int startY = contentY + 26;
                    int rowH = 41;
                    int rowGap = 5;

                    // Left Column
                    int r1Y = startY;
                    if (checkSliderClick("vm_size", leftColX, r1Y, colW, rowH, -1.50f, 1.50f, mouseX, mouseY)) return true;

                    // Scale Swing Checkbox
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

                    // Right Column
                    if (checkSliderClick("vm_yaw", rightColX, r1Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
                    if (checkSliderClick("vm_pitch", rightColX, r2Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
                    if (checkSliderClick("vm_roll", rightColX, r3Y, colW, rowH, -180.0f, 180.0f, mouseX, mouseY)) return true;
                    if (checkSliderClick("vm_speed", rightColX, r4Y, colW, rowH, -5.00f, 1.00f, mouseX, mouseY)) return true;

                    // Don't Skip Animation Checkbox
                    if (mouseX >= rightColX && mouseX <= rightColX + colW && mouseY >= r5Y && mouseY <= r5Y + rowH) {
                        KiemConfig.dontSkipAnimation = !KiemConfig.dontSkipAnimation;
                        KiemConfig.save();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.AFK_CINEMATICS) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        return true;
                    }

                    int rowStartY = contentY + 26;
                    int rowH = 40;
                    int rowGap = 6;
                    int toggleW = 56;
                    int toggleH = 18;

                    // Row 1: Ana Şalter
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 12;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.afkCinematicsEnabled = !KiemConfig.afkCinematicsEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Timeout pills (30s, 60s, 120s, 300s)
                    int r2Y = r1Y + rowH + rowGap;
                    int pillW = 46;
                    int pillGap = 5;
                    int[] secOptions = {30, 60, 120, 300};
                    int totalSecW = secOptions.length * pillW + (secOptions.length - 1) * pillGap;
                    int pillsStartX = contentX + contentW - totalSecW - 12;
                    int pillsY = r2Y + (rowH - toggleH) / 2;

                    for (int i = 0; i < secOptions.length; i++) {
                        int s = secOptions[i];
                        int pX = pillsStartX + i * (pillW + pillGap);
                        if (mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH) {
                            KiemConfig.afkCinematicsSeconds = s;
                            KiemConfig.save();
                            return true;
                        }
                    }

                    // Row 3: Bars toggle
                    int r3Y = r2Y + rowH + rowGap;
                    int t3X = contentX + contentW - toggleW - 12;
                    int t3Y = r3Y + (rowH - toggleH) / 2;
                    if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                        KiemConfig.afkCinematicBars = !KiemConfig.afkCinematicBars;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 4: Raycast toggle
                    int r4Y = r3Y + rowH + rowGap;
                    int t4X = contentX + contentW - toggleW - 12;
                    int t4Y = r4Y + (rowH - toggleH) / 2;
                    if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                        KiemConfig.afkRaycastEnabled = !KiemConfig.afkRaycastEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 5: Test Button click
                    int r5Y = r4Y + rowH + rowGap;
                    int testBtnW = 120;
                    int testBtnX = contentX + contentW - testBtnW - 12;
                    int testBtnY = r5Y + (rowH - 22) / 2;
                    if (mouseX >= testBtnX && mouseX <= testBtnX + testBtnW && mouseY >= testBtnY && mouseY <= testBtnY + 22) {
                        AfkCinematicsManager.startAfk();
                        this.onClose();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.CHAT_QOL) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        listeningForKey = false;
                        return true;
                    }

                    int rowStartY = contentY + 28;
                    int rowH = 50;
                    int rowGap = 8;
                    int toggleW = 60;
                    int toggleH = 22;

                    // Row 1: Sınırsız Chat Logu toggle
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 14;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.infiniteChat = !KiemConfig.infiniteChat;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Chat Peek
                    int r2Y = r1Y + rowH + rowGap;
                    int t2X = contentX + contentW - toggleW - 14;
                    int t2Y = r2Y + (rowH - toggleH) / 2;
                    // Toggle
                    if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                        KiemConfig.chatPeekEnabled = !KiemConfig.chatPeekEnabled;
                        KiemConfig.save();
                        return true;
                    }
                    // Keybind button
                    int keyBtnW = 76;
                    int keyBtnX = t2X - keyBtnW - 8;
                    int keyBtnY = t2Y;
                    if (mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                        listeningForKey = !listeningForKey;
                        return true;
                    }

                    // Row 3: Mesaj Birleştirici toggle
                    int r3Y = r2Y + rowH + rowGap;
                    int t3X = contentX + contentW - toggleW - 14;
                    int t3Y = r3Y + (rowH - toggleH) / 2;
                    if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                        KiemConfig.compactMessages = !KiemConfig.compactMessages;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 4: Sohbet Kopyalama toggle
                    int r4Y = r3Y + rowH + rowGap;
                    int t4X = contentX + contentW - toggleW - 14;
                    int t4Y = r4Y + (rowH - toggleH) / 2;
                    if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                        KiemConfig.copyChatEnabled = !KiemConfig.copyChatEnabled;
                        KiemConfig.save();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.TOOLTIP_QOL) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        return true;
                    }

                    int rowStartY = contentY + 28;
                    int rowH = 50;
                    int rowGap = 8;
                    int toggleW = 60;
                    int toggleH = 22;

                    // Row 1: Tooltip Scroll toggle
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 14;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.tooltipScrollEnabled = !KiemConfig.tooltipScrollEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Tooltip Zoom toggle
                    int r2Y = r1Y + rowH + rowGap;
                    int t2X = contentX + contentW - toggleW - 14;
                    int t2Y = r2Y + (rowH - toggleH) / 2;
                    if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                        KiemConfig.tooltipScaleEnabled = !KiemConfig.tooltipScaleEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 3: Reset Scale button
                    int r3Y = r2Y + rowH + rowGap;
                    int resetBtnW = 90;
                    int resetBtnX = contentX + contentW - resetBtnW - 14;
                    int resetBtnY = r3Y + (rowH - toggleH) / 2;
                    if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= resetBtnY && mouseY <= resetBtnY + toggleH) {
                        TooltipQoL.resetScale();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.SMOOTH_ZOOM) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        listeningForZoomKey = false;
                        return true;
                    }

                    int rowStartY = contentY + 24;
                    int rowH = 34;
                    int rowGap = 4;
                    int toggleW = 56;
                    int toggleH = 18;

                    // Row 1: Smooth Zoom toggle
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 14;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.zoomEnabled = !KiemConfig.zoomEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Tuş & Zoom Default
                    int r2Y = r1Y + rowH + rowGap;
                    int keyBtnW = 76;
                    int keyBtnX = contentX + contentW - keyBtnW - 14;
                    int keyBtnY = r2Y + (rowH - toggleH) / 2;
                    // Keybind button click
                    if (mouseX >= keyBtnX && mouseX <= keyBtnX + keyBtnW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                        listeningForZoomKey = !listeningForZoomKey;
                        return true;
                    }

                    int btnStepW = 20;
                    int btnPlusX = keyBtnX - btnStepW - 8;
                    int btnMinusX = btnPlusX - btnStepW - 4;
                    // Minus click
                    if (mouseX >= btnMinusX && mouseX <= btnMinusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                        KiemConfig.zoomDefaultFactor = Math.max(1.5, Math.round((KiemConfig.zoomDefaultFactor - 0.5) * 10.0) / 10.0);
                        KiemConfig.save();
                        return true;
                    }
                    // Plus click
                    if (mouseX >= btnPlusX && mouseX <= btnPlusX + btnStepW && mouseY >= keyBtnY && mouseY <= keyBtnY + toggleH) {
                        KiemConfig.zoomDefaultFactor = Math.min(10.0, Math.round((KiemConfig.zoomDefaultFactor + 0.5) * 10.0) / 10.0);
                        KiemConfig.save();
                        return true;
                    }

                    // Row 3: Scroll Zoom toggle
                    int r3Y = r2Y + rowH + rowGap;
                    int t3X = contentX + contentW - toggleW - 14;
                    int t3Y = r3Y + (rowH - toggleH) / 2;
                    if (mouseX >= t3X && mouseX <= t3X + toggleW && mouseY >= t3Y && mouseY <= t3Y + toggleH) {
                        KiemConfig.zoomScrollEnabled = !KiemConfig.zoomScrollEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 4: Sinematik Kamera (Buz Pateni / Atalet) toggle
                    int r4Y = r3Y + rowH + rowGap;
                    int t4X = contentX + contentW - toggleW - 14;
                    int t4Y = r4Y + (rowH - toggleH) / 2;
                    if (mouseX >= t4X && mouseX <= t4X + toggleW && mouseY >= t4Y && mouseY <= t4Y + toggleH) {
                        KiemConfig.zoomCinematicCamera = !KiemConfig.zoomCinematicCamera;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 5: Sinematik Barlar (Zoom) toggle
                    int r5Y = r4Y + rowH + rowGap;
                    int t5X = contentX + contentW - toggleW - 14;
                    int t5Y = r5Y + (rowH - toggleH) / 2;
                    if (mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH) {
                        KiemConfig.zoomCinematicBars = !KiemConfig.zoomCinematicBars;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 6: Sinematik Mod Her Zaman (Always) toggle
                    int r6Y = r5Y + rowH + rowGap;
                    int t6X = contentX + contentW - toggleW - 14;
                    int t6Y = r6Y + (rowH - toggleH) / 2;
                    if (mouseX >= t6X && mouseX <= t6X + toggleW && mouseY >= t6Y && mouseY <= t6Y + toggleH) {
                        KiemConfig.cinematicBarsAlways = !KiemConfig.cinematicBarsAlways;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 7: Sinematik Bar Boyutu (Slider click)
                    int r7Y = r6Y + rowH + rowGap;
                    if (checkSliderClick("zoom_cinematic_size", contentX, r7Y, contentW, rowH, 4.0f, 30.0f, mouseX, mouseY)) {
                        return true;
                    }

                    // Row 8: Hassasiyet Ölçekleme toggle
                    int r8Y = r7Y + rowH + rowGap;
                    int t8X = contentX + contentW - toggleW - 14;
                    int t8Y = r8Y + (rowH - toggleH) / 2;
                    if (mouseX >= t8X && mouseX <= t8X + toggleW && mouseY >= t8Y && mouseY <= t8Y + toggleH) {
                        KiemConfig.zoomSensitivityScale = !KiemConfig.zoomSensitivityScale;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 9: Hız ve Sıfırla
                    int r9Y = r8Y + rowH + rowGap;
                    int resetBtnW = 60;
                    int resetBtnX = contentX + contentW - resetBtnW - 14;
                    int r9BtnsY = r9Y + (rowH - toggleH) / 2;

                    // Reset button click
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

                    // Speed pills click
                    int spW = 46;
                    int spGap = 4;
                    int sp3X = resetBtnX - spW - 8;
                    int sp2X = sp3X - spW - spGap;
                    int sp1X = sp2X - spW - spGap;

                    if (mouseX >= sp1X && mouseX <= sp1X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) {
                        KiemConfig.zoomSpeed = 8.0;
                        KiemConfig.save();
                        return true;
                    }
                    if (mouseX >= sp2X && mouseX <= sp2X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) {
                        KiemConfig.zoomSpeed = 16.0;
                        KiemConfig.save();
                        return true;
                    }
                    if (mouseX >= sp3X && mouseX <= sp3X + spW && mouseY >= r9BtnsY && mouseY <= r9BtnsY + toggleH) {
                        KiemConfig.zoomSpeed = 26.0;
                        KiemConfig.save();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.ANTI_AFK) {
                    // Back button click
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        return true;
                    }

                    int rowStartY = contentY + 28;
                    int rowH = 50;
                    int rowGap = 8;
                    int toggleW = 60;
                    int toggleH = 22;

                    // Row 1: Enable / Disable toggle
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 14;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.antiAfkEnabled = !KiemConfig.antiAfkEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Minutes (1, 2, 3, 5, 10)
                    int r2Y = r1Y + rowH + rowGap;
                    int pillW = 42;
                    int pillGap = 5;
                    int[] mins = {1, 2, 3, 5, 10};
                    int totalPillsW = mins.length * pillW + (mins.length - 1) * pillGap;
                    int pillsStartX = contentX + contentW - totalPillsW - 14;
                    int pillsY = r2Y + (rowH - toggleH) / 2;
                    for (int i = 0; i < mins.length; i++) {
                        int m = mins[i];
                        int pX = pillsStartX + i * (pillW + pillGap);
                        if (mouseX >= pX && mouseX <= pX + pillW && mouseY >= pillsY && mouseY <= pillsY + toggleH) {
                            KiemConfig.antiAfkMinutes = m;
                            KiemConfig.save();
                            return true;
                        }
                    }

                    // Row 3: Action (Shift, Jump, Click)
                    int r3Y = r2Y + rowH + rowGap;
                    String[] actions = {"SHIFT", "JUMP", "CLICK"};
                    int actW = 54;
                    int actGap = 6;
                    int totalActsW = actions.length * actW + (actions.length - 1) * actGap;
                    int actsStartX = contentX + contentW - totalActsW - 14;
                    int actsY = r3Y + (rowH - toggleH) / 2;
                    for (int i = 0; i < actions.length; i++) {
                        String act = actions[i];
                        int aX = actsStartX + i * (actW + actGap);
                        if (mouseX >= aX && mouseX <= aX + actW && mouseY >= actsY && mouseY <= actsY + toggleH) {
                            KiemConfig.antiAfkAction = act;
                            KiemConfig.save();
                            return true;
                        }
                    }

                    // Row 4: Reset
                    int r4Y = r3Y + rowH + rowGap;
                    int resetW = 76;
                    int resetX = contentX + contentW - resetW - 14;
                    int resetY = r4Y + (rowH - toggleH) / 2;
                    if (mouseX >= resetX && mouseX <= resetX + resetW && mouseY >= resetY && mouseY <= resetY + toggleH) {
                        KiemConfig.antiAfkEnabled = false;
                        KiemConfig.antiAfkMinutes = 3;
                        KiemConfig.antiAfkAction = "SHIFT";
                        KiemConfig.save();
                        return true;
                    }
                } else if (configSubView == ConfigSubView.BETTER_THIRD_PERSON) {
                    int backBtnW = 58;
                    int backBtnH = 20;
                    if (mouseX >= contentX && mouseX <= contentX + backBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        configSubView = ConfigSubView.MAIN;
                        listeningForShoulderKey = false;
                        return true;
                    }

                    // Reset button
                    int resetBtnW = 60;
                    int resetBtnX = contentX + contentW - resetBtnW;
                    if (mouseX >= resetBtnX && mouseX <= resetBtnX + resetBtnW && mouseY >= contentY && mouseY <= contentY + backBtnH) {
                        KiemConfig.betterThirdPersonEnabled = false;
                        KiemConfig.shoulderSurfingEnabled = true;
                        KiemConfig.shoulderSide = 1.0f;
                        KiemConfig.thirdPersonSmoothness = 0.5f;
                        KiemConfig.thirdPersonSpeedLag = true;
                        KiemConfig.thirdPersonDistance = 4.0f;
                        KiemConfig.save();
                        return true;
                    }

                    int rowStartY = contentY + 26;
                    int rowH = 38;
                    int rowGap = 5;
                    int toggleW = 56;
                    int toggleH = 18;

                    // Row 1: Ana Şalter
                    int r1Y = rowStartY;
                    int t1X = contentX + contentW - toggleW - 12;
                    int t1Y = r1Y + (rowH - toggleH) / 2;
                    if (mouseX >= t1X && mouseX <= t1X + toggleW && mouseY >= t1Y && mouseY <= t1Y + toggleH) {
                        KiemConfig.betterThirdPersonEnabled = !KiemConfig.betterThirdPersonEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 2: Shoulder Surfing toggle
                    int r2Y = r1Y + rowH + rowGap;
                    int t2X = contentX + contentW - toggleW - 12;
                    int t2Y = r2Y + (rowH - toggleH) / 2;
                    if (mouseX >= t2X && mouseX <= t2X + toggleW && mouseY >= t2Y && mouseY <= t2Y + toggleH) {
                        KiemConfig.shoulderSurfingEnabled = !KiemConfig.shoulderSurfingEnabled;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 3: Hotkey button & Sol/Sağ pills
                    int r3Y = r2Y + rowH + rowGap;
                    int keyBtnW = 74;
                    int keyBtnX = contentX + contentW - keyBtnW - 12;
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

                    // Row 4: Smoothness slider
                    int r4Y = r3Y + rowH + rowGap;
                    if (checkSliderClick("tp_smoothness", contentX, r4Y, contentW, rowH, 0.0f, 1.0f, mouseX, mouseY)) {
                        return true;
                    }

                    // Row 5: Speed Lag toggle
                    int r5Y = r4Y + rowH + rowGap;
                    int t5X = contentX + contentW - toggleW - 12;
                    int t5Y = r5Y + (rowH - toggleH) / 2;
                    if (mouseX >= t5X && mouseX <= t5X + toggleW && mouseY >= t5Y && mouseY <= t5Y + toggleH) {
                        KiemConfig.thirdPersonSpeedLag = !KiemConfig.thirdPersonSpeedLag;
                        KiemConfig.save();
                        return true;
                    }

                    // Row 6: Distance slider
                    int r6Y = r5Y + rowH + rowGap;
                    if (checkSliderClick("tp_distance", contentX, r6Y, contentW, rowH, 2.0f, 8.0f, mouseX, mouseY)) {
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, wasHandled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
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
            if (event.key() == 256) { // ESCAPE cancels listening
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
            if (event.key() == 256) { // ESCAPE cancels listening
                listeningForKey = false;
            } else {
                KiemConfig.chatPeekKey = event.key();
                KiemConfig.save();
                listeningForKey = false;
            }
            return true;
        }
        if (listeningForZoomKey) {
            if (event.key() == 256) { // ESCAPE cancels listening
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
            if (event.key() == 256) { // ESCAPE cancels listening
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

    private void drawRoundedRect(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int bg, int border) {
        graphics.fill(x, y, x + w, y + h, bg);
        if (border != 0) {
            graphics.fill(x, y, x + w, y + 1, border);
            graphics.fill(x, y + h - 1, x + w, y + h, border);
            graphics.fill(x, y + 1, x + 1, y + h, border);
            graphics.fill(x + w - 1, y, x + w, y + h, border);
        }
    }

    private void drawPill(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int bg, int border) {
        graphics.fill(x + 2, y, x + w - 2, y + h, bg);
        graphics.fill(x, y + 2, x + w, y + h - 2, bg);
        if (border != 0) {
            graphics.fill(x + 2, y, x + w - 2, y + 1, border);
            graphics.fill(x + 2, y + h - 1, x + w - 2, y + h, border);
            graphics.fill(x, y + 2, x + 1, y + h - 2, border);
            graphics.fill(x + w - 1, y + 2, x + w, y + h - 2, border);
        }
    }
}

