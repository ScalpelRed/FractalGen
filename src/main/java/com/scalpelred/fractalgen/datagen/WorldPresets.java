package com.scalpelred.fractalgen.datagen;

import com.scalpelred.fractalgen.ComplexNumber3;
import com.scalpelred.fractalgen.FractalGen;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbChunkGenerator;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbSettings;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;

import java.util.Map;

public abstract class WorldPresets {

    public static final RegistryKey<WorldPreset> MANDELBULB = FractalGen.registryKeyOf(
            "mandelbulb", RegistryKeys.WORLD_PRESET);

    public static void bootstrap(Registerable<WorldPreset> presetRegisterable) {

        RegistryEntryLookup<DimensionType> dimensionTypeRegistry
                = presetRegisterable.getRegistryLookup(RegistryKeys.DIMENSION_TYPE);
        RegistryEntryLookup<Biome> biomeRegistry = presetRegisterable.getRegistryLookup(RegistryKeys.BIOME);

        DimensionOptions overworldOptions = new DimensionOptions(
                    dimensionTypeRegistry.getOrThrow(DimensionTypes.OVERWORLD),
                    new MandelbulbChunkGenerator(
                            new FixedBiomeSource(biomeRegistry.getOrThrow(BiomeKeys.TAIGA)),
                            new MandelbulbSettings(
                                    8, 60, 4,
                                new ComplexNumber3(320),
                                new ComplexNumber3(0),
                                new ComplexNumber3(0, -64, 0),
                                BiomeKeys.TAIGA.getValue()
                    )));

        DimensionOptions netherOptions = new DimensionOptions(
                    dimensionTypeRegistry.getOrThrow(DimensionTypes.THE_NETHER),
                new MandelbulbChunkGenerator(
                        new FixedBiomeSource(biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
                        new MandelbulbSettings(
                                -2, 60, 4,
                                new ComplexNumber3(76),
                                new ComplexNumber3(0),
                                new ComplexNumber3(0, 0, 0),
                                BiomeKeys.CRIMSON_FOREST.getValue()
                    )));

        DimensionOptions theEndOptions = new DimensionOptions(
                    dimensionTypeRegistry.getOrThrow(DimensionTypes.THE_END),
                new MandelbulbChunkGenerator(
                        new FixedBiomeSource(biomeRegistry.getOrThrow(BiomeKeys.THE_END)),
                        new MandelbulbSettings(
                                5, 60, 4,
                                new ComplexNumber3(128),
                                new ComplexNumber3(0),
                                new ComplexNumber3(0, 0, 0),
                                BiomeKeys.THE_END.getValue()
                    )));

        presetRegisterable.register(MANDELBULB, new WorldPreset(Map.of(
                DimensionOptions.OVERWORLD, overworldOptions,
                DimensionOptions.NETHER, netherOptions,
                DimensionOptions.END, theEndOptions
        )));
    }
}