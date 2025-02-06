package golden.scnamelink.mixin.client;

import golden.scnamelink.DisplayMapping;
import golden.scnamelink.SpooncraftNameLinkClient;
import net.minecraft.entity.EntityType;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin (HoverEvent.EntityContent.class)
public abstract class HoverEventMixin {
    @Final
    @Shadow
    public UUID uuid;
    @Final
    @Shadow
    public Optional<Text> name;
    @Final
    @Shadow
    public EntityType<?> entityType;
    @Shadow
    @Nullable
    private List<Text> tooltip;

    @Inject (method = "asTooltip", at = @At ("HEAD"))
    public @Nullable List<Text> asTooltip(CallbackInfoReturnable<List<Text>> cir) {
        if (this.tooltip == null) {
            this.tooltip = new ArrayList<>();
            Optional<Text> var10000 = this.name;
            List<Text> var10001 = this.tooltip;
            Objects.requireNonNull(var10001);
            var10000.ifPresent(var10001::add);
            this.tooltip.add(Text.translatable("gui.entity_tooltip.type",
                                               this.entityType.getName()));
            if (this.entityType == EntityType.PLAYER && this.name.isPresent()) {
                DisplayMapping mapping = SpooncraftNameLinkClient.getMapping(this.uuid,
                                                                             this.name.get().getString());
                if (mapping != null) {
                    this.tooltip.add(Text.translatable("gui.scnamelink.hover_nickname",
                                                       mapping.discord_nick));
                }
            }
            this.tooltip.add(Text.literal(this.uuid.toString()));
        }

        return this.tooltip;
    }
}