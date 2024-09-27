package com.scnamelink.mixin.client;

import com.scnamelink.SpooncraftNameLinkClient;
import com.scnamelink.config.SCNameLinkConfig;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin (PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    SCNameLinkConfig CONFIG = AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();

    @ModifyArgs (method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V", ordinal = 1))
    protected void renderLabelIfPresent(Args args) {

        if ((!CONFIG.replacenametag && !CONFIG.colournametag) || !CONFIG.enableMod) {
            return;
        }

        AbstractClientPlayerEntity player = args.get(0);
        Text display_name = args.get(1);
        Text label = SpooncraftNameLinkClient.getStyledName(display_name, player.getUuid(),
                                                            player.getName(), CONFIG.replacenametag,
                                                            CONFIG.colournametag);
        args.set(1, label);
    }
}