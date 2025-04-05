package dev.rosebud.styled_renaming.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.rosebud.styled_renaming.StyledRenaming;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringUtil;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    @Nullable
    private String itemName;

    @SuppressWarnings("all")
    public AnvilMenuMixin() {
        super(null, 0, null, null, null);
    }

    @ModifyExpressionValue(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    public MutableComponent updateResultingItemName(MutableComponent original, @Local(ordinal = 1) ItemStack stack) {
        // set the raw name component
        stack.set(StyledRenaming.RAW_NAME_COMPONENT, this.itemName);

        // replace the literal text (original) with the parsed text
        return this.parseText(this.itemName).copy();
    }

    @Inject(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;remove(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
            )
    )
    public void updateRemoveItemName(CallbackInfo ci, @Local(ordinal = 1) ItemStack stack) {
        // remove the raw name component if the item is now using its regular name again
        stack.remove(StyledRenaming.RAW_NAME_COMPONENT);
    }

    @ModifyExpressionValue(
            method = "createResult",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z")
    )
    public boolean testItemNameEquality(boolean original, @Local(ordinal = 0) ItemStack existing) {
        // name is equal if the raw text is the same, rather than the output text
        return this.itemName.equals(
                StyledRenaming.getRawName(existing, existing.getHoverName().getString()));
    }

    @ModifyExpressionValue(
            method = "setItemName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;literal(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    public MutableComponent setNewItemName(MutableComponent original) {
        // update the name displayed to the client
        return this.parseText(this.itemName).copy();
    }

    @ModifyExpressionValue(
            method = "setItemName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/AnvilMenu;validateName(Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private String validateName(@Nullable String original, String newItemName) {
        if (original != null) return original;

        String cleaned = StringUtil.filterText(newItemName);
        Component parsed = this.parseText(cleaned);

        // if the resulting text from the parsed version is under 50 characters, allow it
        return parsed.getString().length() <= 50 ? cleaned : null;
    }

    @Unique
    private Component parseText(String newItemName) {
        Component comp = (this.player.hasPermissions(2) ? TagParser.DEFAULT : TagParser.DEFAULT_SAFE)
                .parseNode(newItemName).toText();

        // remove italics if formatting is used
        boolean usesFormatting = !comp.getStyle().equals(Style.EMPTY) || !comp.getSiblings().isEmpty();
        if (usesFormatting) {
            comp = comp.copy().setStyle(comp.getStyle().withItalic(false));
        }

        return comp;
    }
}
