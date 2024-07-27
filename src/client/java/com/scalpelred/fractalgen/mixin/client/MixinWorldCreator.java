package com.scalpelred.fractalgen.mixin.client;

import com.scalpelred.fractalgen.datagen.WorldPresets;
import com.scalpelred.fractalgen.gui.MandelbulbCustomizeScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreator.class)
public abstract class MixinWorldCreator {

    @Shadow public abstract WorldCreator.WorldType getWorldType();

    @Inject(method = "getLevelScreenProvider", at = @At("RETURN"), cancellable = true)
    public void injectGetLevelScreenProvider(CallbackInfoReturnable<LevelScreenProvider> info) {
        RegistryEntry<WorldPreset> preset = this.getWorldType().preset();
        if (preset != null && preset.getKey().isPresent()) {
            RegistryKey<WorldPreset> key = preset.getKey().get();

            if (key.equals(WorldPresets.MANDELBULB)) {
                info.setReturnValue(MandelbulbCustomizeScreen::new);
            }
        }
    }
}
