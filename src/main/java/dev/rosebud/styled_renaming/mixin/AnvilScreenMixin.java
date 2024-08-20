package dev.rosebud.styled_renaming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.rosebud.styled_renaming.StyledRenaming;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Shadow
    private TextFieldWidget nameField;

    @Inject(method = "setup", at = @At("RETURN"))
    private void setup(CallbackInfo ci) {
        this.nameField.setMaxLength(512);
    }

    @ModifyExpressionValue(
            method = "onSlotUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;getString()Ljava/lang/String;")
    )
    private String getItemName(String existing, ScreenHandler handler, int slotId, ItemStack stack) {
        String rawName = stack.get(StyledRenaming.RAW_NAME_COMPONENT);

        return rawName != null ? rawName : existing;
    }
}
