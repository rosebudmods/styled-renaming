package dev.rosebud.styled_renaming;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import java.util.Optional;

public class StyledRenaming implements ModInitializer {
    public static final Identifier RAW_NAME_ID = Identifier.of("styled_renaming", "raw_name");
    public static final DataComponentType<String> RAW_NAME_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            RAW_NAME_ID,
            DataComponentType.<String>builder().codec(Codec.STRING).build()
    );

    @Override
    public void onInitialize(ModContainer mod) {
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
        return components.map(comps -> comps.get(RAW_NAME_ID).asString()).orElse(fallback);
    }
}
