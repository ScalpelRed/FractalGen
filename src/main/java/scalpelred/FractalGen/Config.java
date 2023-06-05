package scalpelred.FractalGen;

import scalpelred.FractalGen.Mandelbulb.MandelbulbBlockSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Config {

    public static final ForgeConfigSpec.Builder Builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec Config;

    public static ForgeConfigSpec.ConfigValue<Double> MB_Scale;
    public static ForgeConfigSpec.ConfigValue<Double> MB_ShiftX;
    public static ForgeConfigSpec.ConfigValue<Double> MB_ShiftY;
    public static ForgeConfigSpec.ConfigValue<Double> MB_ShiftZ;
    public static ForgeConfigSpec.ConfigValue<Double> MB_Pow;
    public static ForgeConfigSpec.ConfigValue<Integer> MB_Iterations;
    public static ForgeConfigSpec.ConfigValue<Integer> MB_LengthChecks;
    public static ForgeConfigSpec.ConfigValue<MandelbulbBlockSettings.BlockMode> MB_BlockMode;
    public static ForgeConfigSpec.ConfigValue<String> MB_SingleBlock;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> MB_Palette;
    public static ForgeConfigSpec.ConfigValue<Integer> MB_MaxID;
    public static ForgeConfigSpec.ConfigValue<Integer> MB_MinID;
    public static ForgeConfigSpec.ConfigValue<Boolean> MB_PlaceEndPortal;

    private final static List<? extends String> defaultPalette = new ArrayList<>(Arrays.asList(
            Blocks.AIR.getRegistryName().toString(),
            Blocks.WHITE_WOOL.getRegistryName().toString(),
            Blocks.ORANGE_WOOL.getRegistryName().toString(),
            Blocks.MAGENTA_WOOL.getRegistryName().toString(),
            Blocks.LIGHT_BLUE_WOOL.getRegistryName().toString(),
            Blocks.YELLOW_WOOL.getRegistryName().toString(),
            Blocks.LIME_WOOL.getRegistryName().toString(),
            Blocks.PINK_WOOL.getRegistryName().toString(),
            Blocks.GRAY_WOOL.getRegistryName().toString(),
            Blocks.LIGHT_GRAY_WOOL.getRegistryName().toString(),
            Blocks.CYAN_WOOL.getRegistryName().toString(),
            Blocks.PURPLE_WOOL.getRegistryName().toString(),
            Blocks.BLUE_WOOL.getRegistryName().toString(),
            Blocks.BROWN_WOOL.getRegistryName().toString(),
            Blocks.GREEN_WOOL.getRegistryName().toString(),
            Blocks.RED_WOOL.getRegistryName().toString(),
            Blocks.BLACK_WOOL.getRegistryName().toString()
    ));


    static {
        Builder.push("FractalGen");

        ArrayList<String> t = Lists.newArrayList();
        t.add(Blocks.STONE.getRegistryName().toString());


        MB_Scale = Builder
                .comment("Mandelbulb scale in blocks")
                .define("mbScale", 320.0);

        MB_ShiftX = Builder
                .comment("Mandelbulb shift in blocks")
                .define("mbShiftX", 0.0);
        MB_ShiftY = Builder
                .define("mbShiftY", 0.0);
        MB_ShiftZ = Builder
                .define("mbShiftZ", 0.0);

        MB_Pow = Builder
                .comment("Mandelbulb power")
                .define("mbPow", 8.0);

        MB_Iterations = Builder
                .comment("Mandelbulb iteration count")
                .define("mbIterations", 64);
        MB_LengthChecks = Builder
                .comment("How many iterations will check if point length is greater than 2",
                        "Useful when mbBlockMode is \"IdByFinalLength\"")
                .define("mbLengthChecks", 63);

        MB_BlockMode = Builder
                .comment("Mandelbulb block selection mode",
                "    IdByFinalLength: sets block ids depending on post-iterative length of its position,",
                "    Palette: selects blocks from mbPalette array depending on post-iterative length of its position,",
                "    SingleBlock: uses the block specified in mbSingleBlock")
                .defineEnum("mbBlockMode",
                        MandelbulbBlockSettings.BlockMode.IdByFinalLength);

        MB_SingleBlock = Builder
                .define("mbSingleBlock", Blocks.STONE.getRegistryName().toString());

        MB_Palette = Builder
                .defineList("mbPalette", defaultPalette, a -> a instanceof String);

        MB_MaxID = Builder
                .comment("The highest ID to use if mbBlockMode is \"IdByFinalLength\"",
                        "Does not affect IDs of blocks that were placed without length check",
                        "DO NOT set this value too high: the world may become unstable due to large amount of block entities",
                        "NOTE: this is not a block ID, but a state ID")
                .define("mbMaxID", 32);
        MB_MinID = Builder
                .comment("The lowest ID to use if mbBlockMode is \"IdByFinalLength\"",
                        "Does not affect IDs of blocks that were placed without length check",
                        "NOTE: this is not a block ID, but a state ID")
                .define("mbMinID", 0);

        MB_PlaceEndPortal = Builder
                .comment("Will end portal be placed in the middle of fractal")
                .define("mbPlaceEndPortal", Boolean.TRUE);

        Builder.pop();
        Config = Builder.build();
    }

}

