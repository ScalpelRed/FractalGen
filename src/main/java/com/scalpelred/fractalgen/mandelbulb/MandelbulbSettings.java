package com.scalpelred.fractalgen.mandelbulb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.scalpelred.fractalgen.ComplexNumber3;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.stream.StreamSupport;

public class MandelbulbSettings {

    public static Codec<MandelbulbSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("power").forGetter(MandelbulbSettings::getPower),
                    Codec.INT.fieldOf("iterations").forGetter(MandelbulbSettings::getIterations),
                    Codec.INT.fieldOf("post_iterations").forGetter(MandelbulbSettings::getPostIterations),
                    ComplexNumber3.CODEC.fieldOf("scale").forGetter(MandelbulbSettings::getScale),
                    ComplexNumber3.CODEC.fieldOf("rotation").forGetter(MandelbulbSettings::getRotation),
                    ComplexNumber3.CODEC.fieldOf("translation").forGetter(MandelbulbSettings::getTranslation),
                    Identifier.CODEC.fieldOf("biome").forGetter(MandelbulbSettings::getBiome)
            ).apply(instance, MandelbulbSettings::new));

    private double power;
    private int iterations;
    private int postIterations;
    private ComplexNumber3 scale;
    private ComplexNumber3 rotation;
    private ComplexNumber3 translation;
    private Identifier biome;

    public MandelbulbSettings(double power, int iterations, int postIterations,
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

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getPostIterations() {
        return postIterations;
    }

    public void setPostIterations(int postIterations) {
        this.postIterations = postIterations;
    }

    public ComplexNumber3 getScale(){
        return scale.clone();
    }

    public void setScale(ComplexNumber3 scale) {
        this.scale = scale.clone();
    }

    public ComplexNumber3 getRotation() {
        return rotation.clone();
    }

    public void setRotation(ComplexNumber3 rotation) {
        this.rotation = rotation.clone();
    }

    public ComplexNumber3 getTranslation(){
        return translation.clone();
    }

    public void setTranslation(ComplexNumber3 translation) {
        this.translation = translation.clone();
    }

    public double getFinalRadiusSqr(ComplexNumber3 pos0) {
        pos0 = pos0.clone();
        pos0.subtract(translation);
        // TODO rotation
        pos0.divide(scale);
        if (pos0.getRadiusSqr() > 4) return Double.NaN;

        ComplexNumber3 cpos = pos0.clone();
        for (int i = 0; i < iterations; i++) {
            cpos.pow(power);
            cpos.add(pos0);
            if (cpos.getRadiusSqr() > 4) return Double.NaN;
        }
        for (int i = 0; i < postIterations; i++) {
            cpos.pow(power);
            cpos.add(pos0);
        }
        return cpos.getRadiusSqr();
    }

    public void setBiome(Identifier biome) {
        this.biome = biome;
    }

    public Identifier getBiome() {
        return biome;
    }


}
