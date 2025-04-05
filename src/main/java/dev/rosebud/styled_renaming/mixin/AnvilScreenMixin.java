package dev.rosebud.styled_renaming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.rosebud.styled_renaming.StyledRenaming;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Shadow
    private EditBox name;

    @Inject(method = "subInit", at = @At("RETURN"))
    private void setup(CallbackInfo ci) {
        this.name.setMaxLength(512);
    }

    @ModifyExpressionValue(
            method = "slotChanged",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;getString()Ljava/lang/String;")
    )
    private String getItemName(String existing, AbstractContainerMenu menu, int slotId, ItemStack stack) {
        String rawName = StyledRenaming.getRawName(stack);

        return rawName != null ? rawName : existing;
    }
}
