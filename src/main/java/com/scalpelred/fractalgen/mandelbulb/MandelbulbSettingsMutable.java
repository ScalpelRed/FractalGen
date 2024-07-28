package com.scalpelred.fractalgen.mandelbulb;

import com.scalpelred.fractalgen.ComplexNumber3;
import net.minecraft.util.Identifier;

public class MandelbulbSettingsMutable {

    public double power;
    public int iterations;
    public int postIterations;
    public ComplexNumber3 scale;
    public ComplexNumber3 rotation;
    public ComplexNumber3 translation;
    public Identifier biome;

    public MandelbulbSettingsMutable(double power, int iterations, int postIterations,
                                     ComplexNumber3 scale, ComplexNumber3 rotation, ComplexNumber3 translation,
                                     Identifier biome) {
        this.power = power;
        this.iterations = iterations;
        this.postIterations = postIterations;
        this.scale = scale;
        this.rotation = rotation;
        this.translation = translation;
        this.biome = biome;
    }

    public MandelbulbSettingsMutable(MandelbulbSettings src) {
        power = src.getPower();
        iterations = src.getIterations();
        postIterations = src.getPostIterations();
        scale = src.getScale();
        rotation = src.getRotation();
        translation = src.getTranslation();
        biome = src.getBiome();
    }

    public MandelbulbSettings toImmutable() {
        return new MandelbulbSettings(power, iterations, postIterations, scale, rotation,
                translation, biome);
    }

}
