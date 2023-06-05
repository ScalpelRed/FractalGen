package scalpelred.FractalGen.Mandelbulb;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MandelbulbWorldType {

    public static final DeferredRegister<ForgeWorldPreset> CUSTOM_WORLD_TYPES
            = DeferredRegister.create(ForgeRegistries.Keys.WORLD_TYPES, "fractalgen");

    public static final RegistryObject<ForgeWorldPreset> MANDELBULB =
            CUSTOM_WORLD_TYPES.register("mandelbulb",
                    () -> new ForgeWorldPreset(new MandelbulbChunkFactory()));

    record MandelbulbChunkFactory() implements ForgeWorldPreset.IChunkGeneratorFactory{
        public ChunkGenerator createChunkGenerator(RegistryAccess registryAccess,
                                                   long seed,
                                                   String generatorSettings){

            Registry biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
            return new MandelbulbChunkGenerator(
                    registryAccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY),
                    registryAccess.registryOrThrow(Registry.BIOME_REGISTRY),
                    new FixedBiomeSource(biomeRegistry.getOrCreateHolder(Biomes.THE_VOID)));
        }

        public WorldGenSettings createSettings(RegistryAccess registryAccess, long seed,
                                               boolean generateStructures, boolean bonusChest,
                                               String generatorSettings) {

            Registry dimTypeRegistry = registryAccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            Registry defaultDimRegistry = DimensionType.defaultDimensions(registryAccess, seed);
            Registry biomeRegistry = registryAccess.registryOrThrow(Registry.BIOME_REGISTRY);
            Registry structureSetRegistry = registryAccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);

            LevelStem nether = (LevelStem)defaultDimRegistry.get(LevelStem.NETHER);
            LevelStem end = (LevelStem)defaultDimRegistry.get(LevelStem.END);
            MappedRegistry dimensions = new MappedRegistry(Registry.DIMENSION_REGISTRY,
                    Lifecycle.stable(), null);

            dimensions.register(
                    LevelStem.NETHER,
                    new LevelStem(nether.typeHolder(),
                    new MandelbulbChunkGenerator(structureSetRegistry, biomeRegistry,
                            new FixedBiomeSource(biomeRegistry.getOrCreateHolder(
                                    Biomes.CRIMSON_FOREST)))),
                    Lifecycle.stable());
            dimensions.register(
                    LevelStem.END,
                    new LevelStem(end.typeHolder(),
                            new MandelbulbChunkGenerator(structureSetRegistry, biomeRegistry,
                                    new FixedBiomeSource(biomeRegistry.getOrCreateHolder(
                                            Biomes.THE_END)))),
                    Lifecycle.stable());

            return new WorldGenSettings(seed, generateStructures, bonusChest,
                    WorldGenSettings.withOverworld(dimTypeRegistry, dimensions,
                            new MandelbulbChunkGenerator(structureSetRegistry, biomeRegistry,
                                    new FixedBiomeSource(biomeRegistry.getOrCreateHolder(
                                            Biomes.PLAINS)))));
        }
    }
}
