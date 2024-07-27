package com.scalpelred.fractalgen.mandelbulb;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scalpelred.fractalgen.ComplexNumber3;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public class MandelbulbChunkGenerator extends ChunkGenerator {

    public static MapCodec<MandelbulbChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biomesource")
                            .forGetter(MandelbulbChunkGenerator::getBiomeSource),
                    MandelbulbSettings.CODEC.fieldOf("settings")
                            .forGetter(MandelbulbChunkGenerator::getSettings)

            ).apply(instance, instance.stable(MandelbulbChunkGenerator::new)));

    private static final List<BlockState> BLOCK_STATES
            = StreamSupport.stream(Registries.BLOCK.spliterator(), false)
            .flatMap(block -> block.getStateManager().getStates().stream()).toList();

    private final MandelbulbSettings settings;

    public MandelbulbChunkGenerator(BiomeSource biomeSource, MandelbulbSettings settings) {
        super(biomeSource);
        this.settings = settings;
    }

    public MandelbulbSettings getSettings(){
        return settings;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig,
                                                  StructureAccessor structureAccessor, Chunk chunk) {
        BlockPos.Mutable bpos = new BlockPos.Mutable();
        ComplexNumber3 chunkPos = new ComplexNumber3(
                chunk.getPos().x * 16,
                0,
                chunk.getPos().z * 16
        );
        ComplexNumber3 pos0 = new ComplexNumber3();
        for (int x = 0; x < 16; x++) {
            bpos.setX(x);
            pos0.x = chunkPos.x + x;
            for (int z = 0; z < 16; z++) {
                bpos.setZ(z);
                pos0.z = chunkPos.z + z;
                for (int y = getMinimumY(); y < getWorldHeight(); y++) {
                    bpos.setY(y);
                    pos0.y = y;
                    double r2 = settings.getFinalRadiusSqr(pos0);
                    if (!Double.isNaN(r2)) {
                        r2 = Math.sqrt(r2) * 8;
                        chunk.setBlockState(bpos, getBlockState((int)Math.round(r2)), false);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(chunk);

        /*
        BlockPos.Mutable bpos = bpos0.mutableCopy();
        ComplexNumber3 chunkPos = new ComplexNumber3(bpos0.getX(), bpos0.getY(), bpos0.getZ());
        ComplexNumber3 pos0 = chunkPos.clone();

        double power = settings.getPower();
        int iterations = settings.getIterations();
        int postIterations = settings.getPostIterations();
        ComplexNumber3 scale = settings.getScale();
        ComplexNumber3 rotation = settings.getRotation();
        ComplexNumber3 translation = settings.getTranslation();

        int yLim = getWorldHeight();
        for (int x = 0; x < 16; x++) {
            bpos.setX(bpos0.getX() + x);
            pos0.x = chunkPos.x + x;
            pos0.x -= translation.x;
            pos0.x /= scale.x;

            for (int z = 0; z < 16; z++) {
                bpos.setZ(bpos0.getZ() + z);
                pos0.z = chunkPos.z + z;
                pos0.z -= translation.z;
                pos0.z /= scale.z;

                for (int y = getMinimumY(); y < yLim; y++) {
                    bpos.setY(y);
                    pos0.y = y;
                    pos0.y -= translation.y;
                    pos0.y /= scale.y;

                    ComplexNumber3 cpos = pos0.clone();
                    boolean cond = cpos.getRadiusSqr() <= 4;
                    if (!cond) continue;
                    for (int i = 0; i < iterations; i++) {
                        cpos.pow(power);
                        cpos.add(pos0);
                        if (cpos.getRadiusSqr() > 4) {
                            cond = false;
                            break;
                        }
                    }
                    if (cond) {
                        for (int i = 0; i < postIterations; i++) {
                            cpos.pow(power);
                            cpos.add(pos0);
                        }
                        chunk.setBlockState(bpos, getBlockState((int)(cpos.getRadius() * 8)), false);
                    }
                }
            }
        }
        */
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 320;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

    }

    public static BlockState getBlockState(int index) {
        if (index < 0 || index >= BLOCK_STATES.size()) return Blocks.AIR.getDefaultState();
        return BLOCK_STATES.get(index);
    }
}
