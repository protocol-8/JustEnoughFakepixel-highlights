package com.jef.justenoughfakepixel.mixins;

import com.jef.justenoughfakepixel.config.JefConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// thanks to odtheking, wouldnt have known how to implement this correctly LOL

@Mixin(Item.class)
public class MixinItem {

    @Inject(method = "shouldCauseReequipAnimation", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged, CallbackInfoReturnable<Boolean> ci) {
        if (JefConfig.feature == null) return;
        if (!JefConfig.feature.misc.noItemSwitchAnimation) return;
        ci.setReturnValue(false);
    }
}