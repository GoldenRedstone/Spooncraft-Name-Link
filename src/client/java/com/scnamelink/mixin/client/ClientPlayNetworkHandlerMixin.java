package com.scnamelink.mixin.client;

import com.scnamelink.NameLinkAPI;
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
    @Inject (method = "onGameJoin(Lnet/minecraft/network/packet/s2c/play/GameJoinS2CPacket;)V",
            at = @At ("TAIL"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ChatHud mcch = MinecraftClient.getInstance().inGameHud.getChatHud();
        if (mcch != null) {
            String status = NameLinkAPI.getStatus();
            if (Objects.equals(status, "Working")) {
                MutableText message = Text.literal("SpooncraftNameLink has not finished setting " +
                                                           "up yet.");
                mcch.addMessage(message);
            } else if (Objects.equals(status, "Fallback")) {
                MutableText message = Text.literal("SpooncraftNameLink could not reach the server." +
                                                           " Using cached fallback.");
                mcch.addMessage(message);
            } else if (Objects.equals(status, "Failure")) {
                MutableText message = Text.literal("SpooncraftNameLink could not reach the server" +
                                                           " or find a fallback.");
                mcch.addMessage(message);
            }
        }
    }
}
