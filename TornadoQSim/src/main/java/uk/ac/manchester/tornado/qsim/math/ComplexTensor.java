/*
 * This file is part of TornadoQSim:
 * A Java-based quantum computing framework accelerated with TornadoVM.
 *
 * URL: https://github.com/beehive-lab/TornadoQSim
 *
 * Copyright (c) 2021-2022, APT Group, Department of Computer Science,
 * The University of Manchester. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.manchester.tornado.qsim.math;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a complex tensor with arbitrary shape (number of dimensions).
 * 
 * @author Ales Kubicek
 */
public class ComplexTensor {
    private final int rank;
    private final int size;
    private final int[] shape;

    private final float[] real;
    private final float[] imag;

    /**
     * Constructs an empty complex tensor of required shape.
     * 
     * @param shape
     *            shape (dimensions) of the created tensor.
     */
    public ComplexTensor(int... shape) {
        if (!isValidShape(shape))
            throw new IllegalArgumentException("Invalid tensor shape provided.");

        rank = shape.length == 1 && shape[0] == 1 ? 0 : shape.length;
        size = calculateSize(shape);
        this.shape = shape;

        real = new float[size];
        imag = new float[size];
    }

    /**
     * Constructs a complex tensor of required shape filled with supplied complex
     * data.
     * 
     * @param data
     *            complex data.
     * @param shape
     *            shape (dimensions) of the created tensor.
     */
    public ComplexTensor(Complex[] data, int... shape) {
        this(shape);

        if (data == null || data.length != size)
            throw new IllegalArgumentException("Invalid tensor data provided.");

        int i = 0;
        for (Complex element : data) {
            real[i] = element.real();
            imag[i] = element.imag();
            i++;
        }
    }

    /**
     * Constructs a complex tensor of required shape filled with supplied complex
     * data (splitted).
     * 
     * @param realData
     *            real complex data.
     * @param imagData
     *            imaginary complex data.
     * @param shape
     *            shape (dimensions) of the created tensor.
     */
    public ComplexTensor(float[] realData, float[] imagData, int... shape) {
        if (!isValidShape(shape))
            throw new IllegalArgumentException("Invalid tensor shape provided.");

        rank = shape.length == 1 && shape[0] == 1 ? 0 : shape.length;
        size = calculateSize(shape);
        this.shape = shape;

        if (realData == null || imagData == null || realData.length != imagData.length || realData.length != size)
            throw new IllegalArgumentException("Invalid tensor data provided.");

        real = realData;
        imag = imagData;
    }

    /**
     * Constructs a complex tensor as a clone of a supplied complex tensor.
     * 
     * @param original
     *            complex tensor to be cloned.
     */
    public ComplexTensor(ComplexTensor original) {
        if (original == null)
            throw new IllegalArgumentException("Invalid original tensor provided.");

        rank = original.rank;
        size = original.size;
        shape = Arrays.copyOf(original.shape, original.shape.length);
        real = Arrays.copyOf(original.real, original.real.length);
        imag = Arrays.copyOf(original.imag, original.imag.length);
    }

    /**
     * Gets a rank of this complex tensor (eg. rank 1 = vector, rank 2 = matrix,
     * ...).
     * 
     * @return complex tensor rank.
     */
    public int rank() {
        return rank;
    }

    /**
     * Gets a total size of this complex tensor (total number of complex data
     * points).
     * 
     * @return total number of complex data points.
     */
    public int size() {
        return size;
    }

    /**
     * Gets a shape (dimensions) of this complex tensor.
     * 
     * @return complex tensor shape.
     */
    public int[] shape() {
        return shape;
    }

    /**
     * Gets all real parts of this complex tensor.
     * 
     * @return all real parts.
     */
    public float[] getRawRealData() {
        return real;
    }

    /**
     * Gets all imaginary parts of this complex tensor.
     * 
     * @return all imaginary parts.
     */
    public float[] getRawImagData() {
        return imag;
    }

    /**
     * Retrieves a single indexed complex element from this complex tensor.
     * 
     * @param indicies
     *            location within the complex tensor.
     * @return single complex number (at the supplied index).
     */
    public Complex getElement(int... indicies) {
        checkIndexBounds(indicies);
        int i = getFlatIndex(indicies);
        return new Complex(real[i], imag[i]);
    }

    /**
     * Inserts the supplied complex element into the complex tensor at specified
     * index.
     * 
     * @param element
     *            complex number to be inserted.
     * @param indicies
     *            location within the complex tensor.
     */
    public void insertElement(Complex element, int... indicies) {
        if (element == null)
            throw new IllegalArgumentException("Invalid tensor element provided.");
        checkIndexBounds(indicies);
        int i = getFlatIndex(indicies);
        real[i] = element.real();
        imag[i] = element.imag();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ComplexTensor that = (ComplexTensor) o;
        return rank == that.rank && size == that.size && Arrays.equals(shape, that.shape) && Arrays.equals(real, that.real) && Arrays.equals(imag, that.imag);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rank, size);
        result = 31 * result + Arrays.hashCode(shape);
        result = 31 * result + Arrays.hashCode(real);
        result = 31 * result + Arrays.hashCode(imag);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder finalString = new StringBuilder();
        finalString.append("ComplexTensor {" + " rank: " + rank + ", shape: " + Arrays.toString(shape) + ", data: [");
        for (int i = 0; i < size; i++)
            if (i == size - 1)
                finalString.append(new Complex(real[i], imag[i]) + "] }");
            else
                finalString.append(new Complex(real[i], imag[i]) + ", ");
        return finalString.toString();
    }

    private void checkIndexBounds(int... indicies) {
        if (!allPositiveOrZero(indicies))
            throw new IndexOutOfBoundsException("Index cannot be negative.");
        if (indicies.length != shape.length)
            throw new IndexOutOfBoundsException("Number of supplied indicies does not correspond to the tensor rank.");
        for (int i = 0; i < indicies.length; i++)
            if (indicies[i] >= shape[i])
                throw new IndexOutOfBoundsException("Supplied index does not fit the tensor shape.");
    }

    private int getFlatIndex(int... indicies) {
        if (indicies == null)
            return 0;
        // (i,j,...,y,z) index to be accessed in the flat array (indicies[0] = i)
        // (d0,d1,d2,...,dn) shape of the tensor (shape[0] = d0)
        // Formula: (i * d1*d2*...*dn) + (j * d2*...*dn) + ... + (y * dn) + z
        // Example: access (2,0) of a 3x2 matrix
        // => (i=2, j=0), (d0=3, d1=2) => (i * d1) + j = 2*2 + 0 = 4
        int flatIndex = 0;
        int dimensionFactor = 1;
        for (int i = indicies.length - 1; i >= 0; i--) {
            flatIndex += indicies[i] * dimensionFactor;
            dimensionFactor *= shape[i];
        }
        return flatIndex;
    }

    private int calculateSize(int[] shape) {
        int dataSize = 1;
        for (int value : shape)
            dataSize *= value;
        return dataSize;
    }

    private boolean allPositive(int[] numbers) {
        for (int number : numbers)
            if (number <= 0)
                return false;
        return true;
    }

    private boolean allPositiveOrZero(int[] numbers) {
        for (int number : numbers)
            if (number < 0)
                return false;
        return true;
    }

    private boolean isValidShape(int[] shape) {
        return shape != null && shape.length > 0 && allPositive(shape);
    }

}
