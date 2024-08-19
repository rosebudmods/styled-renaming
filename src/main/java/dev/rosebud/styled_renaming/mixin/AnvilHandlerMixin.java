package dev.rosebud.styled_renaming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilHandlerMixin extends ForgingScreenHandler {
	@Shadow
	@Nullable
	private String newItemName;

	public AnvilHandlerMixin() {
		super(null, 0, null, null);
	}

	@ModifyExpressionValue(
			method = "updateResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
			)
	)
	public MutableText updateItemName(MutableText original) {
		return this.parseText().copy();
	}

	@ModifyExpressionValue(
			method = "setNewItemName",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
			)
	)
	public MutableText setNewItemName(MutableText original) {
		return this.parseText().copy();
	}

	@Unique
	private Text parseText() {
		Text text = (this.player.hasPermissionLevel(2) ? TagParser.DEFAULT : TagParser.DEFAULT_SAFE)
				.parseNode(this.newItemName).toText();

		// remove italics if formatting is used
		boolean usesFormatting = !text.getStyle().equals(Style.EMPTY) || !text.getSiblings().isEmpty();
		if (usesFormatting) {
			text = text.copy().setStyle(text.getStyle().withItalic(false));
		}

		return text;
	}
}
