package com.jef.justenoughfakepixel.mixins;

// Credit: PolyPatcher (https://github.com/Sk1erLLC/Patcher)

import com.jef.justenoughfakepixel.config.JefConfig;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class MixinRender {

    @Inject(method = "renderEntityOnFire", at = @At("HEAD"), cancellable = true)
    private void jef$disableEntityFire(Entity entity, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (JefConfig.feature != null && JefConfig.feature.misc.disableEntityFire)
            ci.cancel();
    }
}