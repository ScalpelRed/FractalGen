package com.scalpelred.fractalgen.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.WorldPresetTags;
import net.minecraft.world.gen.WorldPreset;

import java.util.concurrent.CompletableFuture;

public class FractalGenWorldPresetProvider extends FabricTagProvider<WorldPreset> {

    public FractalGenWorldPresetProvider(FabricDataOutput output,
                                         CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.WORLD_PRESET, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(WorldPresetTags.NORMAL).add(WorldPresets.MANDELBULB);
    }
}
