package scalpelred.FractalGen.Mandelbulb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import scalpelred.FractalGen.ComplexNumber3;

public class MandelbulbGeneratorSettings {

    public static final Codec<MandelbulbGeneratorSettings> CODEC =
            RecordCodecBuilder.create(c -> c
                    .group(Codec.DOUBLE.fieldOf("Scale").stable()
                            .forGetter(a -> a.Scale))
                    .and(ComplexNumber3.CODEC.fieldOf("Shift").stable()
                            .forGetter(a -> a.Shift))
                    .and(Codec.DOUBLE.fieldOf("Power").stable()
                            .forGetter(a -> a.Power))
                    .and(Codec.INT.fieldOf("Iterations").stable()
                            .forGetter(a -> a.Iterations))
                    .and(Codec.INT.fieldOf("LengthChecks").stable()
                            .forGetter(a -> a.LengthChecks))
                    .and(Codec.BOOL.fieldOf("PlaceEndPortal").stable()
                            .forGetter(a -> a.PlaceEndPortal))
                    .apply(c, c.stable(MandelbulbGeneratorSettings::new))
            );

    public double Scale;
    public ComplexNumber3 Shift;
    public double Power;
    public int Iterations;
    public int LengthChecks;
    public boolean PlaceEndPortal;

    public MandelbulbGeneratorSettings(double scale, ComplexNumber3 shift,
                                       double power, int iterations, int lengthChecks,
                                       boolean placeEndPortal) {

        Scale = scale;
        Shift = shift;
        Power = power;
        Iterations = iterations;
        LengthChecks = lengthChecks;
        PlaceEndPortal = placeEndPortal;
    }

}
