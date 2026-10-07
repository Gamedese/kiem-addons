package com.example.client.mixin;

import com.example.client.KiemConfig;
import com.example.client.TooltipQoL;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractTooltip", at = @At("HEAD"))
    private void kiem$onExtractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        TooltipQoL.onHoveredSlotChanged(this.hoveredSlot);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void kiem$onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft != null && this.minecraft.hasControlDown() && KiemConfig.tooltipScaleEnabled) {
                TooltipQoL.zoom((float) verticalAmount);
                cir.setReturnValue(true);
                return;
            } else if (KiemConfig.tooltipScrollEnabled) {
                TooltipQoL.scroll((float) verticalAmount);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
