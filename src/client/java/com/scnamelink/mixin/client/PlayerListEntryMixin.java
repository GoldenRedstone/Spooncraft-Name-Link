package com.scnamelink.mixin.client;

import com.mojang.authlib.GameProfile;
import com.scnamelink.SpooncraftNameLinkClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    @Final
    private GameProfile profile;

    @Shadow
    @Nullable
    private Text displayName;

    @Inject (at = @At ("RETURN"), method = "getDisplayName", cancellable = true)
    public void replaceDisplayName(CallbackInfoReturnable<Text> cir) {
        if (true) {
            Text label = SpooncraftNameLinkClient.getStyledName(displayName, profile.getId(),
                                                                Text.literal(profile.getName()));
            cir.setReturnValue(label);
        }
    }
}