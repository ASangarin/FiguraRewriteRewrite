package org.moon.figura.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.moon.figura.FiguraMod;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.avatars.Badges;
import org.moon.figura.config.Config;
import org.moon.figura.lua.api.nameplate.NameplateCustomization;
import org.moon.figura.trust.TrustContainer;
import org.moon.figura.utils.TextUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V")
    private void addMessageEvent(Component message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        Avatar avatar = AvatarManager.getAvatarForPlayer(FiguraMod.getLocalPlayerUUID());
        if (avatar != null)
            avatar.chatReceivedMessageEvent(message.getString());
    }

    @ModifyVariable(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/network/chat/Component;IIZ)V", argsOnly = true)
    private Component addMessageName(Component message) {
        //get config
        int config = Config.CHAT_NAMEPLATE.asInt();

        if (config == 0 || this.minecraft.player == null)
            return message;

        //iterate over ALL online players
        for (UUID uuid : this.minecraft.player.connection.getOnlinePlayerIds()) {
            //get player
            PlayerInfo player = this.minecraft.player.connection.getPlayerInfo(uuid);
            if (player == null)
                continue;

            //get metadata
            Avatar avatar = AvatarManager.getAvatarForPlayer(uuid);
            if (avatar == null)
                continue;

            //apply customization
            Component replacement;
            NameplateCustomization custom = avatar.luaRuntime == null ? null : avatar.luaRuntime.nameplate.CHAT;
            if (custom != null && custom.getText() != null && avatar.trust.get(TrustContainer.Trust.NAMEPLATE_EDIT) == 1) {
                replacement = NameplateCustomization.applyCustomization(custom.getText().replaceAll("\n|\\\\n", ""));
            } else {
                replacement = new TextComponent(player.getProfile().getName());
            }

            //apply nameplate
            if (config > 1) {
                Component badges = Badges.fetchBadges(avatar);
                ((MutableComponent) replacement).append(badges);
            }

            //modify message
            message = TextUtils.replaceInText(message, "\\b" + player.getProfile().getName() + "\\b", replacement);
        }

        return message;
    }
}
