/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.fractalgen as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package scalpelred.FractalGen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ComplexNumber3 {
	public static final Codec<ComplexNumber3> CODEC
			= RecordCodecBuilder.create(c -> c
					.group(Codec.DOUBLE.fieldOf("x").stable().forGetter(a -> a.x))
					.and(Codec.DOUBLE.fieldOf("y").stable().forGetter(a -> a.y))
					.and(Codec.DOUBLE.fieldOf("z").stable().forGetter(a -> a.z))
					.apply(c, c.stable(ComplexNumber3::new)));


	public double x;
	public double y;
	public double z;

	public ComplexNumber3(){

	}

	public ComplexNumber3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ComplexNumber3 Pow(double p){

		double r = Math.pow(LengthSquare(), p / 2);
		double theta = Math.atan2(Math.sqrt(x * x + y * y), z) * p;
		double phi = Math.atan2(y, x) * p;

		ComplexNumber3 t = new ComplexNumber3();
		t.x = r * Math.sin(theta) * Math.cos(phi);
		t.y = r * Math.sin(theta) * Math.sin(phi);
		t.z = r * Math.cos(theta);

		return t;
	}

	public ComplexNumber3 Add(double dx, double dy, double dz) {
		return new ComplexNumber3(x + dx, y + dy, z + dz);
	}
	public ComplexNumber3 Subtract(double dx, double dy, double dz) {
		return new ComplexNumber3(x - dx, y - dy, z - dz);
	}

	public ComplexNumber3 Add(ComplexNumber3 d){
		return Add(d.x, d.y, d.z);
	}
	public ComplexNumber3 Subtract(ComplexNumber3 d){
		return Subtract(d.x, d.y, d.z);
	}

	public ComplexNumber3 AddX(double dx){
		return new ComplexNumber3(x + dx, y, z);
	}

	public ComplexNumber3 AddY(double dy){
		return new ComplexNumber3(x, y + dy, z);
	}

	public ComplexNumber3 AddZ(double dz){
		return new ComplexNumber3(x, y, z + dz);
	}

	public ComplexNumber3 Multiply(double s){
		return new ComplexNumber3(x * s, y * s, z * s);
	}
	
	public ComplexNumber3 Divide(double s){
		return new ComplexNumber3(x / s, y / s, z / s);
	}

	public double LengthSquare(){
		return x * x + y * y + z * z;
	}

	public ComplexNumber3 Clone(){
		return new ComplexNumber3(x, y, z);
	}

	@Override
	public String toString(){
		return "{ " + x + " " + y + " " + z + " }";
	}
}
