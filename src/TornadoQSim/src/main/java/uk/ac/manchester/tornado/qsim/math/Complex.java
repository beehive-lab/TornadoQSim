package uk.ac.manchester.tornado.qsim.math;

import java.util.Objects;

/**
 * Represents a complex number, including important arithmetic operations.
 * The code is inspired by <a href="https://introcs.cs.princeton.edu/java/97data/Complex.java.html">CS Princeton</>.
 * @author Ales Kubicek (inspired by CS Princeton)
 */
public class Complex {
    private final float real;
    private final float imag;

    /**
     * Constructs a complex number from the supplied real and imaginary parts.
     * @param real real part
     * @param imag imaginary part
     */
    public Complex(float real, float imag) {
        this.real = real;
        this.imag = imag;
    }

    /**
     * Gets the real part of the complex number.
     * @return real part of the complex number
     */
    public float real() { return real; }

    /**
     * Gets the imaginary part of the complex number.
     * @return imaginary part of the complex number
     */
    public float imag() { return imag; }

    /**
     * Adds two complex numbers.
     * @param b the other complex number to be added to this.
     * @return sum of the complex numbers (complex number).
     */
    public Complex plus(Complex b) {
        Complex a = this;
        return new Complex(a.real + b.real, a.imag + b.imag);
    }

    /**
     * Subtracts two complex numbers.
     * @param b the other complex number to be subtracted from this.
     * @return subtraction result (complex number).
     */
    public Complex minus(Complex b) {
        Complex a = this;
        return new Complex(a.real - b.real, a.imag - b.imag);
    }

    /**
     * Multiplies two complex numbers.
     * @param b the other complex number that multiplies this.
     * @return multiplication result (complex number).
     */
    public Complex times(Complex b) {
        Complex a = this;
        return new Complex(a.real * b.real - a.imag * b.imag, a.real * b.imag + a.imag * b.real);
    }

    /**
     * Divides two complex numbers
     * @param b the other complex number that divides this.
     * @return division result (complex number).
     */
    public Complex div(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    /**
     * Scales this complex number by the supplied scalar alpha.
     * @param alpha scalar that scales this.
     * @return scaled representation of this complex number.
     */
    public Complex scale(float alpha) {
        return new Complex(alpha * real, alpha * imag);
    }

    /**
     * Calculates a multiplicative inverse (= reciprocal) of this complex number.
     * @return reciprocal of this complex number
     */
    public Complex reciprocal() {
        float scale = real * real + imag * imag;
        return new Complex(real / scale, -imag / scale);
    }

    /**
     * Calculates a complex conjugate of this complex number.
     * @return conjugate of this complex number.
     */
    public Complex conjugate() {
        return new Complex(real, -imag);
    }

    /**
     * Calculates a complex exponential function (e^this).
     * @return complex exponential of this complex number.
     */
    public Complex exp() {
        float expReal = (float)(Math.exp(real) * Math.cos(imag));
        float expImag = (float)(Math.exp(real) * Math.sin(imag));
        return new Complex(expReal, expImag);
    }

    /**
     * Calculates an absolute value (modulus) of this complex number.
     * @return absolute value of this complex number.
     */
    public float abs() {
        return (float)Math.hypot(real, imag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex complex = (Complex) o;
        return complex.real == real && complex.imag == imag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }

    @Override
    public String toString() {
        if (imag == 0) return real + "";
        if (real == 0) return imag + "i";
        if (imag < 0) return real + " - " + (-imag) + "i";
        return real + " + " + imag + "i";
    }
}
