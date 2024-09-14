package com.scnamelink.mixin.client;

import com.scnamelink.SpooncraftNameLinkClient;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @ModifyArgs (
            method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHudLine;<init>(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V")
    )
    public void onReceivingMessages(Args args) {

        if (!true) {
            return;
        }

        Text message = args.get(1);

        args.set(1, SpooncraftNameLinkClient.naivelyStyleText(message));
    }

//    @Inject(method = "getTextStyleAt", at = @At(value = "RETURN"), cancellable = true)
//    public void modifyHoverEvent(double x, double y, CallbackInfoReturnable<Style> cir) {
//        Style style = cir.getReturnValue();
//        if (!(boolean) ChatTools.CONFIG.get("general.ChatTools.Enabled")) {
//            cir.setReturnValue(style);
//        }
//        if (!(boolean) ChatTools.CONFIG.get("general.PreviewClickEvents.Enabled")) {
//            cir.setReturnValue(style);
//        }
//        cir.setReturnValue(ClickEventsPreviewer.work(style));
//    }
}

// addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V
//void addMessage(Text t, MessageSignatureData msd, MessageIndicator mi)
// LChatHudLine;<init>(ILText;LMessageSignatureData;LMessageIndicator;)V
//void ChatHudLine<init>(Text t, MessageSignatureData msd, MessageIndicator mi)
