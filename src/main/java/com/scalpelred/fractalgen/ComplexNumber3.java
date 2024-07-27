package com.scalpelred.fractalgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class ComplexNumber3 {

    public static Codec<ComplexNumber3> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("x").forGetter(c -> c.x),
                    Codec.DOUBLE.fieldOf("y").forGetter(c -> c.y),
                    Codec.DOUBLE.fieldOf("z").forGetter(c -> c.z)
            ).apply(instance, ComplexNumber3::new));

    public double x;
    public double y;
    public double z;

    public ComplexNumber3() {
        x = y = z = 0.0;
    }

    public ComplexNumber3(double xyz) {
        x = y = z = xyz;
    }

    public ComplexNumber3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ComplexNumber3 clone() {
        return new ComplexNumber3(x, y, z);
    }

    public void add(ComplexNumber3 other) {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    public void subtract(ComplexNumber3 other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public void multiply(ComplexNumber3 other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
    }

    public void multiply(double num) {
        x *= num;
        y *= num;
        z *= num;
    }

    public void divide(ComplexNumber3 other) {
        x /= other.x;
        y /= other.y;
        z /= other.z;
    }

    public void divide(double num) {
        x /= num;
        y /= num;
        z /= num;
    }

    public void pow(double power) {
        double r = Math.pow(getRadiusSqr(), power * 0.5);
        double theta = getTheta() * power;
        double phi = getPhi() * power;

        double t = r * Math.sin(theta);
        x = t * Math.cos(phi);
        y = t * Math.sin(phi);
        z = r * Math.cos(theta);
    }

    public double getRadius() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double getRadiusSqr() {
        return x * x + y * y + z * z;
    }

    public double getTheta() {
        return Math.atan2(Math.sqrt(x * x + y * y), z);

    }

    public double getPhi() {
        return Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexNumber3 t) return t.x == x && t.y == y && t.z == z;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public static ComplexNumber3 parse(String textX, String textY, String textZ) {
        return new ComplexNumber3 (
                Double.parseDouble(textX),
                Double.parseDouble(textY),
                Double.parseDouble(textZ)
        );
    }
}
