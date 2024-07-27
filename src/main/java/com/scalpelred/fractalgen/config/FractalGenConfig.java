package com.scalpelred.fractalgen.config;

import com.scalpelred.fractalgen.ComplexNumber3;
import com.scalpelred.fractalgen.FractalGen;

public class FractalGenConfig extends Config {

    /*public final ConfigEntryHandle<Double> mandelbulb_power = new ConfigEntryHandle<>(
            "mandelbulb_power", Double.class, 8.0);
    public final ConfigEntryHandle<ComplexNumber3> mandelbulb_scale = new ConfigEntryHandle<>(
            "mandelbulb_scale", ComplexNumber3.class, new ComplexNumber3(320));
    public final ConfigEntryHandle<ComplexNumber3> mandelbulb_translation = new ConfigEntryHandle<>(
            "mandelbulb_translation", ComplexNumber3.class, new ComplexNumber3(0, -64, 0));*/

    public FractalGenConfig() {
        super(FractalGen.MOD_ID);

        /*registerEntryHandle(mandelbulb_power);
        registerEntryHandle(mandelbulb_scale);
        registerEntryHandle(mandelbulb_translation);*/
    }
}
