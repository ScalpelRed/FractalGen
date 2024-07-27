package com.scalpelred.fractalgen.mandelbulb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scalpelred.fractalgen.ComplexNumber3;

public class MandelbulbSettingsMutable {

    public double power;
    public int iterations;
    public int postIterations;
    public ComplexNumber3 scale;
    public ComplexNumber3 rotation;
    public ComplexNumber3 translation;

    public MandelbulbSettingsMutable(double power, int iterations, int postIterations,
                                     ComplexNumber3 scale, ComplexNumber3 rotation, ComplexNumber3 translation) {
        this.power = power;
        this.iterations = iterations;
        this.postIterations = postIterations;
        this.scale = scale;
        this.rotation = rotation;
        this.translation = translation;
    }

    public MandelbulbSettingsMutable(MandelbulbSettings src) {
        power = src.getPower();
        iterations = src.getIterations();
        postIterations = src.getPostIterations();
        scale = src.getScale();
        rotation = src.getRotation();
        translation = src.getTranslation();
    }

    public MandelbulbSettings toImmutable() {
        return new MandelbulbSettings(power, iterations, postIterations, scale, rotation,
                translation);
    }

}
