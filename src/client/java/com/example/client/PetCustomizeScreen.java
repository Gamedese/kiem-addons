package com.example.client;

import com.example.client.pet.GroundPetManager;
import com.example.client.pet.ShoulderPetManager;
import com.example.client.pet.ShoulderPetType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Locale;

/**
 * Omuz yoldasi ozellestirme ekrani (Omuz Tarafi, Yerde Gezme, Sikilma Suresi, Boyut ve Etkilesimler).
 */
public final class PetCustomizeScreen extends Screen {
    private static final int ACCENT = 0xFFFF66B2;
    private static final int ACCENT_BG = 0x33FF66B2;
    private static final int TEXT = 0xFFF5EFEA;
    private static final int MUTED = 0xFF9E9298;
    private static final int BORDER = 0xFF3A3548;

    private static final Identifier PREVIEW_HAMSTER = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_hamster.png");
    private static final Identifier PREVIEW_HAMSTER_HD = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_hamster_hd.png");
    private static final Identifier PREVIEW_OCTOPUS = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_octopus.png");
    private static final Identifier PREVIEW_DRAGON = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_dragon.png");
    private static final Identifier PREVIEW_CURSE = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_pet_inventory_curse.png");
    private static final Identifier PREVIEW_GARGOYLE = Identifier.fromNamespaceAndPath("modid", "textures/gui/preview_aura_gargoyle.png");

    private final Screen parent;
    private final ShoulderPetType targetPet;
    private int panelX, panelY, panelW, panelH, leftX, leftW, bodyY, bodyH, viewX, viewW;

    public PetCustomizeScreen(Screen parent, ShoulderPetType targetPet) {
        super(Component.literal(KiemLang.get("pets.custom.title")));
        this.parent = parent;
        this.targetPet = targetPet == ShoulderPetType.OFF ? ShoulderPetManager.getSelectedPet() : targetPet;
    }

    @Override
    protected void init() {
        panelW = Math.min(780, width - 24);
        panelH = Math.min(410, height - 16);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
        int margin = 18;
        int innerW = panelW - margin * 2;
        leftX = panelX + margin;
        leftW = (int) ((innerW - 14) * 0.62f);
        viewX = leftX + leftW + 14;
        viewW = panelX + panelW - margin - viewX;
        bodyY = panelY + 50;
        bodyH = panelH - 96;

        int ctrlX = leftX + 12;
        int ctrlW = leftW - 24;
        int halfW = (ctrlW - 8) / 2;
        int cy = bodyY + 12;
        int rowStep = 31;

        // 1. Omuz Tarafi [ Sag Omuz ] [ Sol Omuz ] (Inventory Curse omuzlara sabit capraz sarilir)
        if (targetPet != ShoulderPetType.INVENTORY_CURSE) {
            addRenderableWidget(new PetToggleButton(ctrlX, cy, halfW, 22,
                    () -> KiemLang.get("pets.side.right"),
                    () -> !ShoulderPetManager.isLeftShoulder(),
                    () -> ShoulderPetManager.setLeftShoulder(false)));
            addRenderableWidget(new PetToggleButton(ctrlX + halfW + 8, cy, halfW, 22,
                    () -> KiemLang.get("pets.side.left"),
                    ShoulderPetManager::isLeftShoulder,
                    () -> ShoulderPetManager.setLeftShoulder(true)));
            cy += rowStep;
        }

        // 2. Pet tipine ozel kontroller
        if (targetPet == ShoulderPetType.OCTOPUS) {
            // Ruh Hali Kilidi (Otomatik / Hep Mutlu / Hep Kizgin)
            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.octo_mood") + ": " + octoMoodLabel(KiemConfig.octoMoodMode),
                    () -> !"AUTO".equalsIgnoreCase(KiemConfig.octoMoodMode),
                    () -> {
                        if ("AUTO".equalsIgnoreCase(KiemConfig.octoMoodMode)) {
                            KiemConfig.octoMoodMode = "HAPPY";
                        } else if ("HAPPY".equalsIgnoreCase(KiemConfig.octoMoodMode)) {
                            KiemConfig.octoMoodMode = "ANGRY";
                        } else {
                            KiemConfig.octoMoodMode = "AUTO";
                        }
                        KiemConfig.save();
                    }));
            cy += rowStep;

            // Pet Boyutu (0.70 .. 1.40)
            addRenderableWidget(new PetSlider(ctrlX, cy, ctrlW, 22,
                    (KiemConfig.petScale - 0.70f) / 0.70f,
                    v -> {
                        KiemConfig.petScale = 0.70f + (float) v * 0.70f;
                        KiemConfig.save();
                    },
                    () -> String.format(Locale.ROOT, "%s: %.2fx", KiemLang.get("pets.custom.scale"), KiemConfig.petScale)));
            cy += rowStep;

            // Reaktif Animasyonlar (Uyku / Savas / Parasut)
            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.octo_reactive") + ": " + onOff(KiemConfig.octoReactiveAnim),
                    () -> KiemConfig.octoReactiveAnim,
                    () -> {
                        KiemConfig.octoReactiveAnim = !KiemConfig.octoReactiveAnim;
                        KiemConfig.save();
                    }));
        } else if (targetPet == ShoulderPetType.INVENTORY_CURSE) {
            // Lanetli Envanter Kurdu: Inverted Spear & Uyku
            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemConfig.inventoryCurseWithSword ? KiemLang.get("pets.worm.sword_on") : KiemLang.get("pets.worm.sword_off"),
                    () -> KiemConfig.inventoryCurseWithSword,
                    () -> {
                        KiemConfig.inventoryCurseWithSword = !KiemConfig.inventoryCurseWithSword;
                        KiemConfig.save();
                    }));
            cy += rowStep;

            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.sleep") + ": " + onOff(KiemConfig.petSleepEnabled),
                    () -> KiemConfig.petSleepEnabled,
                    () -> {
                        KiemConfig.petSleepEnabled = !KiemConfig.petSleepEnabled;
                        KiemConfig.save();
                    }));
        } else if (targetPet == ShoulderPetType.HAMSTER_HD) {
            // Fluffy Hamster: Tam yer/omuz kontrol seti
            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.mode") + ": " + modeLabel(KiemConfig.petMode),
                    () -> !"SHOULDER".equalsIgnoreCase(KiemConfig.petMode),
                    () -> {
                        if ("AUTO".equalsIgnoreCase(KiemConfig.petMode)) {
                            KiemConfig.petMode = "GROUND";
                            KiemConfig.petGroundRoam = true;
                        } else if ("GROUND".equalsIgnoreCase(KiemConfig.petMode)) {
                            KiemConfig.petMode = "SHOULDER";
                            KiemConfig.petGroundRoam = false;
                            GroundPetManager.reset();
                        } else {
                            KiemConfig.petMode = "AUTO";
                            KiemConfig.petGroundRoam = true;
                            GroundPetManager.reset();
                        }
                        KiemConfig.save();
                    }));
            cy += rowStep;

            addRenderableWidget(new PetSlider(ctrlX, cy, ctrlW, 22,
                    (KiemConfig.petBoredSeconds - 5) / 25.0,
                    v -> {
                        KiemConfig.petBoredSeconds = 5 + (int) Math.round(v * 25.0);
                        KiemConfig.save();
                    },
                    () -> KiemLang.get("pets.custom.bored_time") + ": " + KiemConfig.petBoredSeconds + "s"));
            cy += rowStep;

            addRenderableWidget(new PetSlider(ctrlX, cy, ctrlW, 22,
                    (KiemConfig.petScale - 0.70f) / 0.70f,
                    v -> {
                        KiemConfig.petScale = 0.70f + (float) v * 0.70f;
                        KiemConfig.save();
                    },
                    () -> String.format(Locale.ROOT, "%s: %.2fx", KiemLang.get("pets.custom.scale"), KiemConfig.petScale)));
            cy += rowStep;

            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.eat_seed") + ": " + onOff(KiemConfig.petEatSeed),
                    () -> KiemConfig.petEatSeed,
                    () -> {
                        KiemConfig.petEatSeed = !KiemConfig.petEatSeed;
                        KiemConfig.save();
                    }));
            cy += rowStep;

            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.dry_shake") + ": " + onOff(KiemConfig.petDryShake),
                    () -> KiemConfig.petDryShake,
                    () -> {
                        KiemConfig.petDryShake = !KiemConfig.petDryShake;
                        KiemConfig.save();
                    }));
            cy += rowStep;

            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.sleep") + ": " + onOff(KiemConfig.petSleepEnabled),
                    () -> KiemConfig.petSleepEnabled,
                    () -> {
                        KiemConfig.petSleepEnabled = !KiemConfig.petSleepEnabled;
                        KiemConfig.save();
                    }));
        } else {
            // Dragon, Classic Hamster & Void Gargoyle: Boyut, Silkinme (Dragon) ve Uyku
            addRenderableWidget(new PetSlider(ctrlX, cy, ctrlW, 22,
                    (KiemConfig.petScale - 0.70f) / 0.70f,
                    v -> {
                        KiemConfig.petScale = 0.70f + (float) v * 0.70f;
                        KiemConfig.save();
                    },
                    () -> String.format(Locale.ROOT, "%s: %.2fx", KiemLang.get("pets.custom.scale"), KiemConfig.petScale)));
            cy += rowStep;

            if (targetPet == ShoulderPetType.DRAGON) {
                addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                        () -> KiemLang.get("pets.custom.dry_shake") + ": " + onOff(KiemConfig.petDryShake),
                        () -> KiemConfig.petDryShake,
                        () -> {
                            KiemConfig.petDryShake = !KiemConfig.petDryShake;
                            KiemConfig.save();
                        }));
                cy += rowStep;
            }

            addRenderableWidget(new PetToggleButton(ctrlX, cy, ctrlW, 22,
                    () -> KiemLang.get("pets.custom.sleep") + ": " + onOff(KiemConfig.petSleepEnabled),
                    () -> KiemConfig.petSleepEnabled,
                    () -> {
                        KiemConfig.petSleepEnabled = !KiemConfig.petSleepEnabled;
                        KiemConfig.save();
                    }));
        }

        // Footer: Back & Defaults
        int footerY = panelY + panelH - 34;
        int footerW = Math.min(164, (innerW - 10) / 2);
        addRenderableWidget(new PetActionButton(leftX, footerY, footerW, 24, KiemLang.get("ui.back"), this::onClose));
        addRenderableWidget(new PetActionButton(panelX + panelW - margin - footerW, footerY, footerW, 24,
                KiemLang.get("pets.custom.defaults"), () -> {
            KiemConfig.resetPetSettings();
            KiemConfig.save();
            rebuildWidgets();
        }));
    }

    private static String onOff(boolean v) {
        return v ? KiemLang.get("pets.custom.on") : KiemLang.get("pets.custom.off");
    }

    private static String modeLabel(String mode) {
        if ("SHOULDER".equalsIgnoreCase(mode)) return KiemLang.get("pets.custom.mode.shoulder");
        if ("GROUND".equalsIgnoreCase(mode)) return KiemLang.get("pets.custom.mode.ground");
        return KiemLang.get("pets.custom.mode.auto");
    }

    private static String octoMoodLabel(String mode) {
        if ("HAPPY".equalsIgnoreCase(mode)) return KiemLang.get("pets.custom.octo_mood.happy");
        if ("ANGRY".equalsIgnoreCase(mode)) return KiemLang.get("pets.custom.octo_mood.angry");
        return KiemLang.get("pets.custom.octo_mood.auto");
    }

    private static Identifier previewFor(ShoulderPetType type) {
        return switch (type) {
            case HAMSTER -> PREVIEW_HAMSTER;
            case HAMSTER_HD -> PREVIEW_HAMSTER_HD;
            case OCTOPUS -> PREVIEW_OCTOPUS;
            case DRAGON -> PREVIEW_DRAGON;
            case INVENTORY_CURSE -> PREVIEW_CURSE;
            case VOID_GARGOYLE -> PREVIEW_GARGOYLE;
            default -> PREVIEW_HAMSTER_HD;
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Panel background MUST be drawn before super.extractRenderState so widgets stay visible on top!
        BoutiqueDraw.drawBackdrop(graphics, this.width, this.height, System.currentTimeMillis());
        BoutiqueDraw.drawGlassPanel(graphics, panelX, panelY, panelW, panelH);

        String petTitle = targetPet == ShoulderPetType.OFF ? KiemLang.get("pets.none") : KiemLang.get(targetPet.titleKey);
        graphics.text(this.font, KiemLang.get("pets.custom.title") + " — " + petTitle, leftX, panelY + 16, TEXT, false);
        graphics.text(this.font, KiemLang.get("pets.custom.roam_note"), leftX, panelY + 30, MUTED, false);

        // Left & Right card frames
        BoutiqueDraw.drawGlassCard(graphics, leftX, bodyY, leftW, bodyH, false, false);
        BoutiqueDraw.drawGlassCard(graphics, viewX, bodyY, viewW, bodyH, false, false);

        // Preview image in right panel
        int imgSize = Math.min(viewW - 36, bodyH - 80);
        int imgX = viewX + (viewW - imgSize) / 2;
        int imgY = bodyY + 22;
        BoutiqueDraw.drawGlassCard(graphics, imgX - 6, imgY - 6, imgSize + 12, imgSize + 12, false, false);
        graphics.blit(previewFor(targetPet), imgX, imgY, imgX + imgSize, imgY + imgSize, 0f, 1f, 0f, 1f);

        boolean roaming = GroundPetManager.isUsingWorldRenderer(targetPet);
        String statusText = roaming ? KiemLang.get("pets.custom.state_ground") : KiemLang.get("pets.custom.state_shoulder");
        graphics.centeredText(this.font, statusText, viewX + viewW / 2, imgY + imgSize + 18, roaming ? ACCENT : TEXT);
        String sideText = ShoulderPetManager.isLeftShoulder() ? KiemLang.get("pets.side.left") : KiemLang.get("pets.side.right");
        graphics.centeredText(this.font, sideText, viewX + viewW / 2, imgY + imgSize + 34, MUTED);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            KiemCompat.setScreen(this.minecraft, this.parent);
        }
    }

    private static final class PetToggleButton extends Button {
        private final java.util.function.Supplier<String> labelFn;
        private final java.util.function.BooleanSupplier activeFn;

        PetToggleButton(int x, int y, int w, int h, java.util.function.Supplier<String> labelFn,
                        java.util.function.BooleanSupplier activeFn, Runnable onPress) {
            super(x, y, w, h, Component.literal(labelFn.get()), b -> onPress.run(), DEFAULT_NARRATION);
            this.labelFn = labelFn;
            this.activeFn = activeFn;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mx, int my, float pt) {
            boolean act = activeFn.getAsBoolean();
            BoutiqueDraw.drawPill(g, getX(), getY(), width, height, act, this.isHoveredOrFocused());
            g.centeredText(net.minecraft.client.Minecraft.getInstance().font, labelFn.get(),
                    getX() + width / 2, getY() + (height - 8) / 2, act ? 0xFF211728 : 0xFFF3ECEF);
        }
    }

    private static final class PetActionButton extends Button {
        private final String text;

        PetActionButton(int x, int y, int w, int h, String text, Runnable onPress) {
            super(x, y, w, h, Component.literal(text), b -> onPress.run(), DEFAULT_NARRATION);
            this.text = text;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mx, int my, float pt) {
            BoutiqueDraw.drawPill(g, getX(), getY(), width, height, false, this.isHoveredOrFocused());
            g.centeredText(net.minecraft.client.Minecraft.getInstance().font, text,
                    getX() + width / 2, getY() + (height - 8) / 2, 0xFFF3ECEF);
        }
    }

    private static final class PetSlider extends AbstractSliderButton {
        private final java.util.function.DoubleConsumer onUpdate;
        private final java.util.function.Supplier<String> labelFn;

        PetSlider(int x, int y, int w, int h, double initial, java.util.function.DoubleConsumer onUpdate,
                  java.util.function.Supplier<String> labelFn) {
            super(x, y, w, h, Component.literal(labelFn.get()), Math.max(0.0, Math.min(1.0, initial)));
            this.onUpdate = onUpdate;
            this.labelFn = labelFn;
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(labelFn.get()));
        }

        @Override
        protected void applyValue() {
            onUpdate.accept(this.value);
        }
    }
}
