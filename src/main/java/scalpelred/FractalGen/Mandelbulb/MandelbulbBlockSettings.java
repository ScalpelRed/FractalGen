package scalpelred.FractalGen.Mandelbulb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MandelbulbBlockSettings {
    public static final Codec<MandelbulbBlockSettings> CODEC =
            RecordCodecBuilder.create(c -> c
                    .group(Codec.STRING.fieldOf("BlockMode").stable()
                            .forGetter(a -> a.CurrentBlockMode.toString()))
                    .and(BlockState.CODEC.fieldOf("SingleBlock").stable()
                            .forGetter(a -> a.SingleBlock))
                    .and(Codec.list(BlockState.CODEC).fieldOf("Palette")
                            .forGetter(a -> a.Palette))
                    .and(Codec.INT.fieldOf("MaxID").stable()
                            .forGetter(a -> a.MaxID))
                    .and(Codec.INT.fieldOf("MinID").stable()
                            .forGetter(a -> a.MinID))
                    .apply(c, c.stable(MandelbulbBlockSettings::new)));

    public MandelbulbBlockSettings.BlockMode CurrentBlockMode;
    public BlockState SingleBlock;
    public List<BlockState> Palette;
    public int MaxID;
    public int MinID;

    public MandelbulbBlockSettings() {

    }

    private MandelbulbBlockSettings(String blockMode, BlockState singleBlock,
                                    List<BlockState> palette, int maxID, int minID){
        try{
            CurrentBlockMode = MandelbulbBlockSettings.BlockMode.valueOf(blockMode);
        }
        catch (Exception e){
            CurrentBlockMode = MandelbulbBlockSettings.BlockMode.SingleBlock;
        }
        SingleBlock = singleBlock;
        Palette = palette;
        MaxID = maxID;
        MinID = minID;
    }

    public void setSingleBlockMode(BlockState block) {
        SingleBlock = block;
        Palette = new ArrayList<>();
        CurrentBlockMode = MandelbulbBlockSettings.BlockMode.SingleBlock;
    }

    public void setPaletteMode(List<BlockState> palette) {
        SingleBlock = Block.stateById(0);
        Palette = palette;
        CurrentBlockMode = MandelbulbBlockSettings.BlockMode.Palette;
    }

    public void setLengthMode(int minID, int maxID) {
        SingleBlock = Block.stateById(0);
        Palette = new ArrayList<>();
        MinID = minID;
        MaxID = maxID;
        CurrentBlockMode = MandelbulbBlockSettings.BlockMode.IdByFinalLength;
    }

    public BlockState blockByBlockMode(double d) {
        switch (CurrentBlockMode){

            case SingleBlock:
                return SingleBlock;

            case Palette:
                if (d > 1) return Blocks.AIR.defaultBlockState();
                return Palette.get((int)(d * Palette.size()));

            case IdByFinalLength:
                return Block.stateById((int)(MinID + (MaxID - MinID) * d));
            default:
                return Blocks.STONE.defaultBlockState();
        }
    }

    public enum BlockMode {
        SingleBlock,
        Palette,
        IdByFinalLength
    }
}
