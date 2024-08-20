package dev.rosebud.styled_renaming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.rosebud.styled_renaming.StyledRenaming;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ChatUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
	public MutableText updateResultingItemName(MutableText original, @Local(ordinal = 1) ItemStack stack) {
		stack.set(StyledRenaming.RAW_NAME_COMPONENT, this.newItemName);
		return this.parseText(this.newItemName).copy();
	}

	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;remove(Lnet/minecraft/component/DataComponentType;)Ljava/lang/Object;"))
	public void updateRemoveItemName(CallbackInfo ci, @Local(ordinal = 1) ItemStack stack) {
		stack.remove(StyledRenaming.RAW_NAME_COMPONENT);
	}

	@ModifyExpressionValue(
			method = "setNewItemName",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
			)
	)
	public MutableText setNewItemName(MutableText original) {
		return this.parseText(this.newItemName).copy();
	}

	@ModifyExpressionValue(
			method = "setNewItemName",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/screen/AnvilScreenHandler;validateName(Ljava/lang/String;)Ljava/lang/String;"
			)
	)
	private String validateName(@Nullable String original, String newItemName) {
		if (original != null) return original;

		String cleaned = ChatUtil.method_57180(newItemName);
		Text parsed = this.parseText(cleaned);

		return parsed.getString().length() <= 50 ? cleaned : null;
	}

	@Unique
	private Text parseText(String newItemName) {
		Text text = (this.player.hasPermissionLevel(2) ? TagParser.DEFAULT : TagParser.DEFAULT_SAFE)
				.parseNode(newItemName).toText();

		// remove italics if formatting is used
		boolean usesFormatting = !text.getStyle().equals(Style.EMPTY) || !text.getSiblings().isEmpty();
		if (usesFormatting) {
			text = text.copy().setStyle(text.getStyle().withItalic(false));
		}

		return text;
	}
}
