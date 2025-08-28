package com.example.examplemod.mixins;

import com.example.examplemod.features.esp.EntityHighlighter;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class RenderMixin {

    @Inject(method = "renderLivingLabel", at = @At("HEAD"))
    private void onRenderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        if (entityIn instanceof EntityLivingBase) {
            EntityHighlighter.getInstance().handleEntityNametag((EntityLivingBase) entityIn, str);
        }
    }
}
