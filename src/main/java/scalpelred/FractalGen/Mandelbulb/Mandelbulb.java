package scalpelred.FractalGen.Mandelbulb;

import scalpelred.FractalGen.ComplexNumber3;

public class Mandelbulb {

	public MandelbulbGeneratorSettings Params;

	public Mandelbulb(MandelbulbGeneratorSettings params) {
		Params = params;
	}

	public double IterateTrig(ComplexNumber3 c) {

		c = c.Subtract(Params.Shift).Divide(Params.Scale);

		ComplexNumber3 t = c.Clone();
		
		for (int i = 0; i < Params.Iterations; i++){
			t = t.Pow(Params.Power).Add(c.x, c.y, c.z);
			if ((i < Params.LengthChecks) && (t.LengthSquare() > 4)) return Double.NaN;
		}

		return t.LengthSquare();
	}
	
}
