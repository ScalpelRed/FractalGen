package com.scalpelred.fractalgen;

import com.scalpelred.fractalgen.config.FractalGenConfig;
import com.scalpelred.fractalgen.mandelbulb.MandelbulbChunkGenerator;
import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FractalGen implements ModInitializer {
	public static final String MOD_ID = "fractalgen";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		if (Math.random() < 0.95) LOGGER.info("Yowza! It's all so complex!");
		else LOGGER.info("oOwOo It's all so complex!");
		Registry.register(Registries.CHUNK_GENERATOR, "mandelbulb", MandelbulbChunkGenerator.CODEC);
	}

	public static Identifier identifierOf(String name) {
		return Identifier.of(MOD_ID, name);
	}

	public static <T> RegistryKey<T> registryKeyOf(String name, RegistryKey<Registry<T>> registryKey) {
		return RegistryKey.of(registryKey, Identifier.of(MOD_ID, name));
	}
}

// TODO
// validation (general)
// validation (tab headers)
// fix formulae
// rotation
// server config
// list view in settings
// Julia set

