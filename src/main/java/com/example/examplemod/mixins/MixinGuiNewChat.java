package com.example.examplemod.mixins;

import com.example.examplemod.features.BlockedUserFilter;
import com.example.examplemod.utils.BlockedUserManager;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {


    private static final Set<String> modifiedMessages = Collections.newSetFromMap(new WeakHashMap<>());
    @Inject(method = "printChatMessage", at = @At("HEAD"), cancellable = true)
    public void onPrintChatMessage(IChatComponent component, CallbackInfo ci) {

        String raw = component.getUnformattedText();
        if (modifiedMessages.contains(raw)) return;

        Collection<String> keywords = BlockedUserManager.getFlaggedNames();
        for (String keyword : keywords) {
            if (component.toString().contains(keyword)) {
                IChatComponent modifiedText = BlockedUserFilter.testing(component, keywords, true);
                modifiedMessages.add(modifiedText.getUnformattedText());
                ((GuiNewChat) (Object) this).printChatMessage(modifiedText);
                ci.cancel();
                break;
            }
        }
    }
}
