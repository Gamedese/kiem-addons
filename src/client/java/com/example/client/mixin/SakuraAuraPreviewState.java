package com.example.client.mixin;

import com.example.client.aura.SakuraAuraPreview;
import com.example.client.aura.SakuraPreviewState;
import com.example.client.aura.VoidGargoylePreview;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public abstract class SakuraAuraPreviewState implements SakuraPreviewState {
    @Unique private SakuraAuraPreview.Frame kiem$sakuraPreview;
    @Unique private VoidGargoylePreview.Frame kiem$gargoylePreview;

    @Override public SakuraAuraPreview.Frame kiem$getSakuraPreview() { return kiem$sakuraPreview; }
    @Override public void kiem$setSakuraPreview(SakuraAuraPreview.Frame frame) { kiem$sakuraPreview = frame; }

    @Override public VoidGargoylePreview.Frame kiem$getGargoylePreview() { return kiem$gargoylePreview; }
    @Override public void kiem$setGargoylePreview(VoidGargoylePreview.Frame frame) { kiem$gargoylePreview = frame; }
}
