package uk.ac.manchester.tornado.qsim.math;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ComplexTensorTest {

    @Test
    public void testComplexTensorDefinitionShape() {
        // Scalar (rank 0)
        ComplexTensor tensor = new ComplexTensor(1);
        assertEquals(0, tensor.rank());
        assertEquals(1, tensor.size());
        assertEquals(1, tensor.shape().length);
        assertEquals(1, tensor.shape()[0]);

        // Vector (rank 1)
        tensor = new ComplexTensor(150);
        assertEquals(1, tensor.rank());
        assertEquals(150, tensor.size());
        assertEquals(1, tensor.shape().length);
        assertEquals(150, tensor.shape()[0]);

        // Matrix (rank 2)
        tensor = new ComplexTensor(40,180);
        assertEquals(2, tensor.rank());
        assertEquals(7200, tensor.size());
        assertEquals(2, tensor.shape().length);
        assertEquals(40, tensor.shape()[0]);
        assertEquals(180, tensor.shape()[1]);

        // Tensor (rank 3)
        tensor = new ComplexTensor(2,3,4);
        assertEquals(3, tensor.rank());
        assertEquals(24, tensor.size());
        assertEquals(3, tensor.shape().length);
        assertEquals(2, tensor.shape()[0]);
        assertEquals(3, tensor.shape()[1]);
        assertEquals(4, tensor.shape()[2]);

        // Tensor (rank 6)
        tensor = new ComplexTensor(2,3,4,5,6,7);
        assertEquals(6, tensor.rank());
        assertEquals(5040, tensor.size());
        assertEquals(6, tensor.shape().length);
        assertEquals(2, tensor.shape()[0]);
        assertEquals(3, tensor.shape()[1]);
        assertEquals(4, tensor.shape()[2]);
        assertEquals(5, tensor.shape()[3]);
        assertEquals(6, tensor.shape()[4]);
        assertEquals(7, tensor.shape()[5]);

        // Invalid definition
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor());
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(0));
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(2,2,0));
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(-5,2,2));
    }

    @Test
    public void testComplexTensorDefinitionData() {
        // Scalar (rank 0)
        ComplexTensor tensor = new ComplexTensor(populateComplexArray(1), 1);
        assertEquals(0, tensor.rank());
        assertEquals(1, tensor.size());
        assertEquals(1, tensor.shape().length);
        assertEquals(1, tensor.shape()[0]);

        // Matrix (rank 2)
        tensor = new ComplexTensor(populateComplexArray(7200), 40,180);
        assertEquals(2, tensor.rank());
        assertEquals(7200, tensor.size());
        assertEquals(2, tensor.shape().length);
        assertEquals(40, tensor.shape()[0]);
        assertEquals(180, tensor.shape()[1]);

        // Tensor (rank 6)
        tensor = new ComplexTensor(populateComplexArray(5040), 2,3,4,5,6,7);
        assertEquals(6, tensor.rank());
        assertEquals(5040, tensor.size());
        assertEquals(6, tensor.shape().length);
        assertEquals(2, tensor.shape()[0]);
        assertEquals(3, tensor.shape()[1]);
        assertEquals(4, tensor.shape()[2]);
        assertEquals(5, tensor.shape()[3]);
        assertEquals(6, tensor.shape()[4]);
        assertEquals(7, tensor.shape()[5]);

        // Invalid definition
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(null, 2,2));
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(new Complex[2], 2,2));
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(new Complex[50], 2,2));
    }

    @Test
    public void testComplexTensorDefinitionClone() {
        // Tensor (rank 6)
        ComplexTensor original = new ComplexTensor(populateComplexArray(5040), 2,3,4,5,6,7);
        ComplexTensor clone = new ComplexTensor(original);
        assertEquals(original, clone);

        // Invalid definition
        ComplexTensor invalid = null;
        assertThrows(IllegalArgumentException.class, () -> new ComplexTensor(invalid));
    }

    @Test
    public void testComplexTensorRawData() {
        Complex[] originalData = populateComplexArray(25);
        ComplexTensor tensor = new ComplexTensor(originalData, 5, 5);
        assertArrayEquals(getRealParts(originalData), tensor.getRawRealData());
        assertArrayEquals(getImagParts(originalData), tensor.getRawImagData());
    }

    @Test
    public void testComplexTensorIndexing() {
        Complex zero = new Complex(0, 0);
        Complex newElement = new Complex(5, -5);

        // Scalar (rank 0)
        ComplexTensor tensor = new ComplexTensor(1);
        assertEquals(zero, tensor.getElement(0));

        tensor.insertElement(newElement, 0);
        assertEquals(newElement, tensor.getElement(0));

        // Matrix (rank 2)
        tensor = new ComplexTensor(2,2);
        assertEquals(zero, tensor.getElement(0,0));
        assertEquals(zero, tensor.getElement(0,1));
        assertEquals(zero, tensor.getElement(1,0));
        assertEquals(zero, tensor.getElement(1,1));

        tensor.insertElement(newElement, 0,1);
        tensor.insertElement(newElement, 1,0);
        assertEquals(zero, tensor.getElement(0,0));
        assertEquals(newElement, tensor.getElement(0,1));
        assertEquals(newElement, tensor.getElement(1,0));
        assertEquals(zero, tensor.getElement(1,1));

        // Matrix (rank 3)
        Complex[] data = new Complex[] {
                new Complex(0, 0),
                new Complex(1, -7),
                new Complex(2, -6),
                new Complex(3, -5),
                new Complex(4, -4),
                new Complex(5, -3),
                new Complex(6, -2),
                new Complex(7, -1),
        };
        tensor = new ComplexTensor(data, 2,2,2);
        assertEquals(data[0], tensor.getElement(0,0,0));
        assertEquals(data[1], tensor.getElement(0,0,1));
        assertEquals(data[2], tensor.getElement(0,1,0));
        assertEquals(data[3], tensor.getElement(0,1,1));
        assertEquals(data[4], tensor.getElement(1,0,0));
        assertEquals(data[5], tensor.getElement(1,0,1));
        assertEquals(data[6], tensor.getElement(1,1,0));
        assertEquals(data[7], tensor.getElement(1,1,1));

        tensor.insertElement(newElement, 1,0,1);
        assertEquals(data[4], tensor.getElement(1,0,0));
        assertEquals(newElement, tensor.getElement(1,0,1));
        assertEquals(data[6], tensor.getElement(1,1,0));

        // Invalid operation
        ComplexTensor finalTensor = new ComplexTensor(5,5);
        assertThrows(IllegalArgumentException.class, () -> finalTensor.insertElement(null, 0,0));
        assertThrows(IndexOutOfBoundsException.class, () -> finalTensor.insertElement(newElement, -1,0));
        assertThrows(IndexOutOfBoundsException.class, () -> finalTensor.insertElement(newElement, 5,0));

        assertThrows(IndexOutOfBoundsException.class, () -> finalTensor.getElement(-1,0));
        assertThrows(IndexOutOfBoundsException.class, () -> finalTensor.getElement(5,0));
    }

    @Test
    public void testComplexTensorEquality() {
        Complex[] data = populateComplexArray(16);
        ComplexTensor a = new ComplexTensor(data, 4,4);
        ComplexTensor b = new ComplexTensor(data, 4,4);
        assertEquals(a, b);

        b.insertElement(new Complex(100, 100), 0,3);
        assertNotEquals(a, b);

        ComplexTensor c = new ComplexTensor(data, 2,2,2,2);
        assertNotEquals(a,c);
    }

    @Test
    public void testComplexTensorString() {
        ComplexTensor tensor = new ComplexTensor(2,2);
        String string = "ComplexTensor { rank: 2, shape: [2, 2], data: [0.0, 0.0, 0.0, 0.0] }";
        assertEquals(string, tensor.toString());

        tensor.insertElement(new Complex(5, -5), 1,0);
        string = "ComplexTensor { rank: 2, shape: [2, 2], data: [0.0, 0.0, 5.0 - 5.0i, 0.0] }";
        assertEquals(string, tensor.toString());
    }

    private Complex[] populateComplexArray(int length) {
        Random random = new Random();
        Complex[] complexArray = new Complex[length];
        for (int i = 0; i < length; i++)
            complexArray[i] = new Complex(random.nextInt(50), random.nextInt(50));
        return complexArray;
    }

    private float[] getRealParts(Complex[] complexArray) {
        float[] realParts = new float[complexArray.length];
        for (int i = 0; i < complexArray.length; i++)
            realParts[i] = complexArray[i].real();
        return realParts;
    }

    private float[] getImagParts(Complex[] complexArray) {
        float[] imagParts = new float[complexArray.length];
        for (int i = 0; i < complexArray.length; i++)
            imagParts[i] = complexArray[i].imag();
        return imagParts;
    }

}
