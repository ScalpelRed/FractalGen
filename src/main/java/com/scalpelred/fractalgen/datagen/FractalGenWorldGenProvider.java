package com.scalpelred.fractalgen.datagen;

import com.scalpelred.fractalgen.FractalGen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.gen.WorldPreset;

import java.util.concurrent.CompletableFuture;

public class FractalGenWorldGenProvider extends FabricDynamicRegistryProvider {

    public FractalGenWorldGenProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        RegistryWrapper.Impl<WorldPreset> worldPresetRegistry
                = registries.getWrapperOrThrow(RegistryKeys.WORLD_PRESET);
        entries.addAll(worldPresetRegistry);

    }

    @Override
    public String getName() {
        return FractalGen.MOD_ID;
    }
}
