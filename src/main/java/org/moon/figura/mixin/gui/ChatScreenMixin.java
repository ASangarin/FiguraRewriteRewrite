package org.moon.figura.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow protected EditBox input;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;sendMessage(Ljava/lang/String;)V"), method = "keyPressed")
    private void keyPressed(ChatScreen instance, String text) {
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (avatar != null && !text.isBlank()) {
            String str = avatar.chatSendMessageEvent(text);
            if (str == null)
                return;
            text = str;
        }

        instance.sendMessage(text);
    }

    @Inject(at = @At("HEAD"), method = "render")
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (avatar == null || avatar.luaRuntime == null)
            return;

        Integer color = avatar.luaRuntime.host.chatColor;
        if (color == null)
            return;

        this.input.setTextColor(color);
    }
}
