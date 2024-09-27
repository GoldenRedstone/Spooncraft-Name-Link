package com.scnamelink.mixin.client;

import com.scnamelink.SpooncraftNameLinkClient;
import com.scnamelink.config.SCNameLinkConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin (ChatHud.class)
public class ChatHudMixin {
    SCNameLinkConfig CONFIG = AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();

    @ModifyArgs (method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/hud/ChatHudLine;<init>(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
    public void onReceivingMessages(Args args) {

        if ((!CONFIG.replacechat && !CONFIG.colourchat) || !CONFIG.enableMod) {
            return;
        }

        Text message = args.get(1);

        args.set(1, SpooncraftNameLinkClient.naivelyStyleText(message,CONFIG.replacechat,
                                                              CONFIG.colourchat));
    }
}
