package dev.rosebud.styled_renaming;

import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class StyledRenaming implements ModInitializer {
    public static final DataComponentType<String> RAW_NAME_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("styled_renaming", "raw_name"),
            DataComponentType.<String>builder().codec(Codec.STRING).build()
    );

    @Override
    public void onInitialize(ModContainer mod) {}
}
