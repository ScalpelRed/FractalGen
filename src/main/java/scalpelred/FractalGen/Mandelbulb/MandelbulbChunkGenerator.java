package scalpelred.FractalGen.Mandelbulb;

import com.mojang.logging.LogUtils;
import scalpelred.FractalGen.ComplexNumber3;
import scalpelred.FractalGen.Config;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.concurrent.Executor;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

import net.minecraft.world.level.levelgen.GenerationStep;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MandelbulbChunkGenerator extends ChunkGenerator{

    public static final Codec<MandelbulbChunkGenerator> CODEC = RecordCodecBuilder.create((c) ->
        commonCodec(c)
                .and(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY)
                        .forGetter((a) -> a.BiomeRegistry))
                .and(BiomeSource.CODEC.fieldOf("biomeSource")
                        .forGetter((a) -> a.biomeSource))
                .and(MandelbulbGeneratorSettings.CODEC.fieldOf("settings")
                        .forGetter(a -> a.settings))
                .and(MandelbulbBlockSettings.CODEC.fieldOf("blockSettings")
                        .forGetter(a -> a.blockSettings))
                .apply(c, c.stable(MandelbulbChunkGenerator::new))
    );

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Registry<Biome> BiomeRegistry;

    private final Mandelbulb fractal;
    private final MandelbulbGeneratorSettings settings;
    private final MandelbulbBlockSettings blockSettings;

    private final ChunkPos centerChunk;
    private final BlockPos portalPos;

    public MandelbulbChunkGenerator(Registry<StructureSet> structureSets,
                                    Registry<Biome> biomeRegistry,
                                    BiomeSource biome, MandelbulbGeneratorSettings settings,
                                    MandelbulbBlockSettings blockSettings) {

        super(structureSets, Optional.empty(), biome);

        this.BiomeRegistry = biomeRegistry;
        this.settings = settings;
        this.blockSettings = blockSettings;

        fractal = new Mandelbulb(settings);

        centerChunk = new ChunkPos(
                (int) settings.Shift.x / 16,
                (int) settings.Shift.z / 16);
        portalPos = new BlockPos(
                settings.Shift.x % 16,
                Math.max(Math.min(settings.Shift.y, 319), -64),
                settings.Shift.z % 16);
    }

    public MandelbulbChunkGenerator(Registry<StructureSet> structureSets,
                                    Registry<Biome> biomeRegistry,
                                    BiomeSource biome) {

        this(structureSets, biomeRegistry, biome,
                settingsFromConfig(), blockSettingsFromConfig());
    }

    public static MandelbulbGeneratorSettings settingsFromConfig() {

        ComplexNumber3 shift = new ComplexNumber3(Config.MB_ShiftX.get(),
                Config.MB_ShiftY.get(), Config.MB_ShiftZ.get());

        MandelbulbGeneratorSettings settings = new MandelbulbGeneratorSettings(
                Config.MB_Scale.get(), shift, Config.MB_Pow.get(),
                Config.MB_Iterations.get(), Config.MB_LengthChecks.get(),
                Config.MB_PlaceEndPortal.get());

        return settings;
    }

    public static MandelbulbBlockSettings blockSettingsFromConfig(){

        MandelbulbBlockSettings.BlockMode blockMode = Config.MB_BlockMode.get();

        MandelbulbBlockSettings blockSettings = new MandelbulbBlockSettings();

        switch (blockMode) {

            case SingleBlock:
                blockSettings.setSingleBlockMode(ForgeRegistries.BLOCKS.getValue(
                        new ResourceLocation(Config.MB_SingleBlock.get())).defaultBlockState());
                LOGGER.info("Mandelbulb single block is " + blockSettings.SingleBlock);
                break;

            case Palette:
                List<? extends String> palleteString = Config.MB_Palette.get();
                ArrayList<BlockState> palette = new ArrayList<>();
                for (String s : palleteString){
                    palette.add(ForgeRegistries.BLOCKS.getValue(
                            new ResourceLocation(s)).defaultBlockState());
                }
                LOGGER.info("Mandelbulb palette contains " + palette.size() + " blocks");
                if (palette.size() == 0) palette.add(Blocks.AIR.defaultBlockState());

                blockSettings.setPaletteMode(palette);
                break;

            case IdByFinalLength:
                blockSettings.setLengthMode(Config.MB_MinID.get(), Config.MB_MaxID.get());
                break;
        }

        return blockSettings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.BiomeRegistry;

    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion,
                             StructureFeatureManager structureFeatureManager,
                             ChunkAccess chunkAccess) {

        if (worldGenRegion.dimensionType().bedWorks() && settings.PlaceEndPortal){
            ChunkPos t = chunkAccess.getPos();
            if (t.x == centerChunk.x){
                if (t.z == centerChunk.z - 1) placeEndPortalInChunk(
                        portalPos.offset(0, 0, 16), chunkAccess);
                else if (t.z == centerChunk.z) placeEndPortalInChunk(
                        portalPos, chunkAccess);
            }
            else if (t.x == centerChunk.x -1){
                if (t.z == centerChunk.z -1) placeEndPortalInChunk(
                        portalPos.offset(16, 0, 16), chunkAccess);
                else if (t.z == centerChunk.z) placeEndPortalInChunk(
                        portalPos.offset(16, 0, 0), chunkAccess);
            }
        }
    }

    @Override
    public void applyBiomeDecoration(WorldGenLevel worldGenLevel, ChunkAccess chunkAccess,
                                     StructureFeatureManager structureFeatureManager) {

    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender,
                                                        StructureFeatureManager structureFeatureManager,
                                                        ChunkAccess chunkAccess) {

        BlockPos.MutableBlockPos mbp = new BlockPos.MutableBlockPos();
        for (int bx = 0; bx < 16; bx++){
            for (int by = -64; by < 320; by++){
                for (int bz = 0; bz < 16; bz++){
                    ComplexNumber3 p = new ComplexNumber3(
                            chunkAccess.getPos().x * 16 + bx, by,
                            chunkAccess.getPos().z * 16 + bz);
                    double t = fractal.IterateTrig(p);
                    if (!Double.isNaN(t)) {
                        chunkAccess.setBlockState(mbp.set(bx, by, bz),
                                blockSettings.blockByBlockMode(t / 4), false);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(chunkAccess);
    }

    private void placeEndPortalInChunk(BlockPos at, ChunkAccess chunkAccess){

        BlockPos.MutableBlockPos t;
        BlockState s = Blocks.END_PORTAL_FRAME.defaultBlockState();

        s = s.setValue(HorizontalDirectionalBlock.FACING, Direction.WEST);
        t = at.mutable().move(2, 0, -1);
        if (t.getX() < 16 && t.getX() >= 0){
            for (int i = 0; i < 3; i++){
                if (t.getZ() < 16 && t.getZ() >= 0) {
                    chunkAccess.setBlockState(t, s, false);
                }
                t.move(0, 0, 1);
            }
        }

        s = s.setValue(HorizontalDirectionalBlock.FACING, Direction.EAST);
        t = at.mutable().move(-2, 0, -1);
        if (t.getX() < 16 && t.getX() >= 0){
            for (int i = 0; i < 3; i++){
                if (t.getZ() < 16 && t.getZ() >= 0) {
                    chunkAccess.setBlockState(t, s, false);
                }
                t.move(0, 0, 1);
            }
        }

        s = s.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);
        t = at.mutable().move(-1, 0, 2);
        if (t.getZ() < 16 && t.getZ() >= 0){
            for (int i = 0; i < 3; i++){
                if (t.getX() < 16 && t.getX() >= 0) {
                    chunkAccess.setBlockState(t, s, false);
                }
                t.move(1, 0, 0);
            }
        }

        s = s.setValue(HorizontalDirectionalBlock.FACING, Direction.SOUTH);
        t = at.mutable().move(-1, 0, -2);
        if (t.getZ() < 16 && t.getZ() >= 0){
            for (int i = 0; i < 3; i++){
                if (t.getX() < 16 && t.getX() >= 0) {
                    chunkAccess.setBlockState(t, s, false);
                }
                t.move(1, 0, 0);
            }
        }
    }

    @Override
    public int getBaseHeight(int a, int b, Heightmap.Types c,
                             LevelHeightAccessor levelHeightAccessor) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int a, int b, LevelHeightAccessor levelHeightAccessor) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> a, BlockPos b) {

    }

    @Override
    public Climate.Sampler climateSampler() {
        return Climate.empty();
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long seed,
                             BiomeManager biomeManager,
                             StructureFeatureManager structureFeatureManager,
                             ChunkAccess chunkAccess, GenerationStep.Carving carving) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {

    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }
}

