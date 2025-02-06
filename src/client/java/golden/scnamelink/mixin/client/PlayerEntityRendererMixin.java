package golden.scnamelink.mixin.client;

import golden.scnamelink.SpooncraftNameLinkClient;
import golden.scnamelink.config.SCNameLinkConfig;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin (PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Unique
    SCNameLinkConfig CONFIG = AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();

    @ModifyArgs(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    protected void renderLabelIfPresent(Args args) {

        if ((!CONFIG.replacenametag && !CONFIG.colournametag) || !CONFIG.enableMod) {
            return;
        }

        PlayerEntityRenderState player = args.get(0);
        Text display_name = args.get(1);
        Text label = SpooncraftNameLinkClient.getStyledName(display_name, player.name,
                                                            CONFIG.replacenametag, CONFIG.colournametag);
        args.set(1, label);
    }
}