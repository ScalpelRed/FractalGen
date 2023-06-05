package scalpelred.FractalGen;

import com.mojang.logging.LogUtils;
import scalpelred.FractalGen.Mandelbulb.MandelbulbChunkGenerator;
import scalpelred.FractalGen.Mandelbulb.MandelbulbWorldType;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("fractalgen")
public class FractalGen {


    private static final Logger LOGGER = LogUtils.getLogger();

    public FractalGen() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Config,
                "FractalGen.toml");

        MandelbulbWorldType.CUSTOM_WORLD_TYPES.register(modBus);

        LOGGER.info("Yowza! It's all so complex!");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void RegisterChunkGenerators(RegistryEvent.Register<Block> event){
            Registry.register(Registry.CHUNK_GENERATOR, "gen_mandelbulb",
                    MandelbulbChunkGenerator.CODEC);
        }

    }
}
