package dev.rosebud.styled_renaming.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
}
