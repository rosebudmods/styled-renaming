package dev.rosebud.styled_renaming;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StyledRenaming implements ModInitializer {
    public static final ResourceLocation RAW_NAME_ID = ResourceLocation.fromNamespaceAndPath("styled_renaming", "raw_name");
    public static final DataComponentType<String> RAW_NAME_COMPONENT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            RAW_NAME_ID,
            DataComponentType.<String>builder().persistent(Codec.STRING).build()
    );

    @Override
    public void onInitialize() {
        PolymerComponent.registerDataComponent(RAW_NAME_COMPONENT);
    }

    @Nullable
    public static String getRawName(ItemStack stack) {
        return getRawName(stack, null);
    }

    public static String getRawName(ItemStack stack, String fallback) {
        String rawName = stack.get(RAW_NAME_COMPONENT);
        if (rawName != null) return rawName;

        var components = Optional.ofNullable(PolymerItemUtils.getPolymerComponents(stack));
        //? if >=1.21.5 {
        return components.flatMap(comps -> comps.get(RAW_NAME_ID).asString()).orElse(fallback);
        //?} else
        /*return components.map(comps -> comps.get(RAW_NAME_ID).getAsString()).orElse(fallback);*/
    }
}
