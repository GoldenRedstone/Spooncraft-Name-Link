package com.scnamelink.mixin.client;

import com.scnamelink.NameLinkAPI;
import com.scnamelink.SpooncraftNameLinkClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin (ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V", at = @At("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        if (chatHud != null && !NameLinkAPI.getStatus().equals("Success") && !NameLinkAPI.getStatus().equals("Disabled")) {
            chatHud.addMessage(SpooncraftNameLinkClient.getStatusString());
        }
    }
}