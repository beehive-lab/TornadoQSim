package uk.ac.manchester.tornado.qsim.simulator.unitary;

import org.junit.jupiter.api.Test;
import uk.ac.manchester.tornado.qsim.math.Complex;
import uk.ac.manchester.tornado.qsim.math.ComplexTensor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class UnitaryOperandTest {

    @Test
    public void testComplexMatrixProductSquare() {
        ComplexTensor matrixA = new ComplexTensor(2,2);
        ComplexTensor matrixB = new ComplexTensor(2,2);
        ComplexTensor matrixC = new ComplexTensor(2,2);
        ComplexTensor expected = new ComplexTensor(2,2);

        // 2x2 * 2x2 - test zeros
        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 2x2 * 2x2 - test data
        matrixA.insertElement(new Complex(5.2f,0),0,0);
        matrixA.insertElement(new Complex(-1.5f,8),0,1);
        matrixA.insertElement(new Complex(0,-4),1,0);
        matrixA.insertElement(new Complex(8,4),1,1);

        matrixB.insertElement(new Complex(-2,-2),0,0);
        matrixB.insertElement(new Complex(10,0),0,1);
        matrixB.insertElement(new Complex(0,0.5f),1,0);
        matrixB.insertElement(new Complex(0.5f,-10),1,1);

        expected.insertElement(new Complex(-14.4f,-11.15f),0,0);
        expected.insertElement(new Complex(131.25f,19),0,1);
        expected.insertElement(new Complex(-10,12),1,0);
        expected.insertElement(new Complex(44,-118),1,1);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 3x3 * 3x3 - test zeros
        matrixA = new ComplexTensor(3,3);
        matrixB = new ComplexTensor(3,3);
        matrixC = new ComplexTensor(3,3);
        expected = new ComplexTensor(3,3);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 3x3 * 3x3 - test data
        matrixA.insertElement(new Complex(5,-2),0,0);
        matrixA.insertElement(new Complex(7,0),0,1);
        matrixA.insertElement(new Complex(0,15),0,2);
        matrixA.insertElement(new Complex(-4,3),1,0);
        matrixA.insertElement(new Complex(0,11),1,1);
        matrixA.insertElement(new Complex(5,9),1,2);
        matrixA.insertElement(new Complex(-2,-1),2,0);
        matrixA.insertElement(new Complex(12,13),2,1);
        matrixA.insertElement(new Complex(13,12),2,2);

        matrixB.insertElement(new Complex(0,5),0,0);
        matrixB.insertElement(new Complex(0,7),0,1);
        matrixB.insertElement(new Complex(0,3),0,2);
        matrixB.insertElement(new Complex(-2,0),1,0);
        matrixB.insertElement(new Complex(-3,0),1,1);
        matrixB.insertElement(new Complex(8,0),1,2);
        matrixB.insertElement(new Complex(2,8),2,0);
        matrixB.insertElement(new Complex(4,-5),2,1);
        matrixB.insertElement(new Complex(-6,6),2,2);

        expected.insertElement(new Complex(-124,55),0,0);
        expected.insertElement(new Complex(68,95),0,1);
        expected.insertElement(new Complex(-28,-75),0,2);
        expected.insertElement(new Complex(-77,16),1,0);
        expected.insertElement(new Complex(44,-50),1,1);
        expected.insertElement(new Complex(-93,52),1,2);
        expected.insertElement(new Complex(-89,92),2,0);
        expected.insertElement(new Complex(83,-70),2,1);
        expected.insertElement(new Complex(-51,104),2,2);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
    }

    @Test
    public void testComplexMatrixProductArbitrary() {
        ComplexTensor matrixA = new ComplexTensor(2,3);
        ComplexTensor matrixB = new ComplexTensor(3,2);
        ComplexTensor matrixC = new ComplexTensor(2,2);
        ComplexTensor expected = new ComplexTensor(2,2);

        // 2x3 * 3x2 - test zeros
        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 2x3 * 3x2 - test data
        matrixA.insertElement(new Complex(4,-1),0,0);
        matrixA.insertElement(new Complex(0,5),0,1);
        matrixA.insertElement(new Complex(5,0),0,2);
        matrixA.insertElement(new Complex(-8,-2),1,0);
        matrixA.insertElement(new Complex(3,10),1,1);
        matrixA.insertElement(new Complex(1,-1),1,2);

        matrixB.insertElement(new Complex(2,2),0,0);
        matrixB.insertElement(new Complex(5,-1),0,1);
        matrixB.insertElement(new Complex(6,0),1,0);
        matrixB.insertElement(new Complex(0,-7),1,1);
        matrixB.insertElement(new Complex(8,4),2,0);
        matrixB.insertElement(new Complex(8,-4),2,1);

        expected.insertElement(new Complex(50,56),0,0);
        expected.insertElement(new Complex(94,-29),0,1);
        expected.insertElement(new Complex(18,36),1,0);
        expected.insertElement(new Complex(32,-35),1,1);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 3x2 * 2x3 - test zeros
        matrixA = new ComplexTensor(3,2);
        matrixB = new ComplexTensor(2,3);
        matrixC = new ComplexTensor(3,3);
        expected = new ComplexTensor(3,3);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 3x2 * 2x3 - test data
        matrixA.insertElement(new Complex(2,-1),0,0);
        matrixA.insertElement(new Complex(3,-2),0,1);
        matrixA.insertElement(new Complex(5,0),1,0);
        matrixA.insertElement(new Complex(4,0),1,1);
        matrixA.insertElement(new Complex(0,1),2,0);
        matrixA.insertElement(new Complex(0,-8),2,1);

        matrixB.insertElement(new Complex(0,1),0,0);
        matrixB.insertElement(new Complex(0,-1),0,1);
        matrixB.insertElement(new Complex(3,1),0,2);
        matrixB.insertElement(new Complex(-3,-1),1,0);
        matrixB.insertElement(new Complex(7,5),1,1);
        matrixB.insertElement(new Complex(4,-2),1,2);

        expected.insertElement(new Complex(-10,5),0,0);
        expected.insertElement(new Complex(30,-1),0,1);
        expected.insertElement(new Complex(15,-15),0,2);
        expected.insertElement(new Complex(-12,1),1,0);
        expected.insertElement(new Complex(28,15),1,1);
        expected.insertElement(new Complex(31,-3),1,2);
        expected.insertElement(new Complex(-9,24),2,0);
        expected.insertElement(new Complex(41,-56),2,1);
        expected.insertElement(new Complex(-17,-29),2,2);

        performMatrixProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
    }

    @Test
    public void testMatrixVectorProductSquare() {
        ComplexTensor matrix = new ComplexTensor(2,2);
        ComplexTensor vector = new ComplexTensor(2);
        ComplexTensor result = new ComplexTensor(2);
        ComplexTensor expected = new ComplexTensor(2);

        // 2x2 * 2x1 - test zeros
        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 2x2 * 2x1 - test data
        matrix.insertElement(new Complex(5,0),0,0);
        matrix.insertElement(new Complex(-1,8),0,1);
        matrix.insertElement(new Complex(0,-4),1,0);
        matrix.insertElement(new Complex(8,4),1,1);

        vector.insertElement(new Complex(-2,-2),0);
        vector.insertElement(new Complex(10,0),1);

        expected.insertElement(new Complex(-20,70),0);
        expected.insertElement(new Complex(72,48),1);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 3x3 * 3x1 - test zeros
        matrix = new ComplexTensor(3,3);
        vector = new ComplexTensor(3);
        result = new ComplexTensor(3);
        expected = new ComplexTensor(3);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 3x3 * 3x1 - test data
        matrix.insertElement(new Complex(5,-2),0,0);
        matrix.insertElement(new Complex(7,0),0,1);
        matrix.insertElement(new Complex(0,15),0,2);
        matrix.insertElement(new Complex(-4,3),1,0);
        matrix.insertElement(new Complex(0,11),1,1);
        matrix.insertElement(new Complex(5,9),1,2);
        matrix.insertElement(new Complex(-2,-1),2,0);
        matrix.insertElement(new Complex(12,13),2,1);
        matrix.insertElement(new Complex(13,12),2,2);

        vector.insertElement(new Complex(0,5),0);
        vector.insertElement(new Complex(-2,0),1);
        vector.insertElement(new Complex(4,-5),2);

        expected.insertElement(new Complex(71,85),0);
        expected.insertElement(new Complex(50,-31),1);
        expected.insertElement(new Complex(93,-53),2);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
    }

    @Test
    public void testMatrixVectorProductArbitrary() {
        ComplexTensor matrix = new ComplexTensor(2,3);
        ComplexTensor vector = new ComplexTensor(3);
        ComplexTensor result = new ComplexTensor(2);
        ComplexTensor expected = new ComplexTensor(2);

        // 2x3 * 3x1 - test zeros
        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 2x3 * 3x1 - test data
        matrix.insertElement(new Complex(4,-1),0,0);
        matrix.insertElement(new Complex(0,5),0,1);
        matrix.insertElement(new Complex(5,0),0,2);
        matrix.insertElement(new Complex(-8,-2),1,0);
        matrix.insertElement(new Complex(3,10),1,1);
        matrix.insertElement(new Complex(1,-1),1,2);

        vector.insertElement(new Complex(2,2),0);
        vector.insertElement(new Complex(5,-1),1);
        vector.insertElement(new Complex(6,0),2);

        expected.insertElement(new Complex(45,31),0);
        expected.insertElement(new Complex(19,21),1);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 3x2 * 2x1 - test zeros
        matrix = new ComplexTensor(3,2);
        vector = new ComplexTensor(2);
        result = new ComplexTensor(3);
        expected = new ComplexTensor(3);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());

        // 3x2 * 2x1 - test data
        matrix.insertElement(new Complex(2,-1),0,0);
        matrix.insertElement(new Complex(3,-2),0,1);
        matrix.insertElement(new Complex(5,0),1,0);
        matrix.insertElement(new Complex(4,0),1,1);
        matrix.insertElement(new Complex(0,1),2,0);
        matrix.insertElement(new Complex(0,-8),2,1);

        vector.insertElement(new Complex(-3,-1),0);
        vector.insertElement(new Complex(7,5),1);

        expected.insertElement(new Complex(24,2),0);
        expected.insertElement(new Complex(13,15),1);
        expected.insertElement(new Complex(41,-59),2);

        performMatrixVectorProduct(matrix, vector, result);
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), result.getRawRealData());
    }

    @Test
    public void testKroneckerProductSquare() {
        ComplexTensor matrixA = new ComplexTensor(2,2);
        ComplexTensor matrixB = new ComplexTensor(2,2);
        ComplexTensor matrixC = new ComplexTensor(4,4);
        ComplexTensor expected = new ComplexTensor(4,4);

        // 2x2 * 2x2 - test zeros
        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 2x2 * 2x2 - test data
        matrixA.insertElement(new Complex(2,-1),0,0);
        matrixA.insertElement(new Complex(3,-2),0,1);
        matrixA.insertElement(new Complex(5,0),1,0);
        matrixA.insertElement(new Complex(4,-2),1,1);

        matrixB.insertElement(new Complex(5,4),0,0);
        matrixB.insertElement(new Complex(-8,-2),0,1);
        matrixB.insertElement(new Complex(3,10),1,0);
        matrixB.insertElement(new Complex(1,-1),1,1);

        expected.insertElement(new Complex(14,3),0,0);
        expected.insertElement(new Complex(-18,4),0,1);
        expected.insertElement(new Complex(16,17),1,0);
        expected.insertElement(new Complex(1,-3),1,1);
        expected.insertElement(new Complex(23,2),0,2);
        expected.insertElement(new Complex(-28,10),0,3);
        expected.insertElement(new Complex(29,24),1,2);
        expected.insertElement(new Complex(1,-5),1,3);
        expected.insertElement(new Complex(25,20),2,0);
        expected.insertElement(new Complex(-40,-10),2,1);
        expected.insertElement(new Complex(15,50),3,0);
        expected.insertElement(new Complex(5,-5),3,1);
        expected.insertElement(new Complex(28,6),2,2);
        expected.insertElement(new Complex(-36,8),2,3);
        expected.insertElement(new Complex(32,34),3,2);
        expected.insertElement(new Complex(2,-6),3,3);

        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
    }

    @Test
    public void testKroneckerProductArbitrary() {
        ComplexTensor matrixA = new ComplexTensor(3,2);
        ComplexTensor matrixB = new ComplexTensor(2,2);
        ComplexTensor matrixC = new ComplexTensor(6,4);
        ComplexTensor expected = new ComplexTensor(6,4);

        // 3x2 * 2x2 - test zeros
        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 3x2 * 2x2 - test data
        matrixA.insertElement(new Complex(2,-1),0,0);
        matrixA.insertElement(new Complex(3,-2),0,1);
        matrixA.insertElement(new Complex(5,0),1,0);
        matrixA.insertElement(new Complex(4,1),1,1);
        matrixA.insertElement(new Complex(1,-8),2,0);
        matrixA.insertElement(new Complex(3,5),2,1);

        matrixB.insertElement(new Complex(5,4),0,0);
        matrixB.insertElement(new Complex(-8,-2),0,1);
        matrixB.insertElement(new Complex(3,10),1,0);
        matrixB.insertElement(new Complex(1,-1),1,1);

        expected.insertElement(new Complex(14,3),0,0);
        expected.insertElement(new Complex(-18,4),0,1);
        expected.insertElement(new Complex(16,17),1,0);
        expected.insertElement(new Complex(1,-3),1,1);
        expected.insertElement(new Complex(23,2),0,2);
        expected.insertElement(new Complex(-28,10),0,3);
        expected.insertElement(new Complex(29,24),1,2);
        expected.insertElement(new Complex(1,-5),1,3);
        expected.insertElement(new Complex(25,20),2,0);
        expected.insertElement(new Complex(-40,-10),2,1);
        expected.insertElement(new Complex(15,50),3,0);
        expected.insertElement(new Complex(5,-5),3,1);
        expected.insertElement(new Complex(16,31),2,2);
        expected.insertElement(new Complex(-30,-16),2,3);
        expected.insertElement(new Complex(2,43),3,2);
        expected.insertElement(new Complex(5,-3),3,3);
        expected.insertElement(new Complex(37,-36),4,0);
        expected.insertElement(new Complex(-24,62),4,1);
        expected.insertElement(new Complex(83,-14),5,0);
        expected.insertElement(new Complex(-7,-9),5,1);
        expected.insertElement(new Complex(-5,37),4,2);
        expected.insertElement(new Complex(-14,-46),4,3);
        expected.insertElement(new Complex(-41,45),5,2);
        expected.insertElement(new Complex(8,2),5,3);

        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        matrixA = new ComplexTensor(2,3);
        matrixB = new ComplexTensor(2,2);
        matrixC = new ComplexTensor(4,6);
        expected = new ComplexTensor(4,6);

        // 2x3 * 2x2 - test zeros
        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());

        // 2x3 * 2x2 - test data
        matrixA.insertElement(new Complex(2,-1),0,0);
        matrixA.insertElement(new Complex(3,-2),0,1);
        matrixA.insertElement(new Complex(5,0),0,2);
        matrixA.insertElement(new Complex(4,1),1,0);
        matrixA.insertElement(new Complex(1,-8),1,1);
        matrixA.insertElement(new Complex(3,5),1,2);

        matrixB.insertElement(new Complex(5,4),0,0);
        matrixB.insertElement(new Complex(-8,-2),0,1);
        matrixB.insertElement(new Complex(3,10),1,0);
        matrixB.insertElement(new Complex(1,-1),1,1);

        expected.insertElement(new Complex(14,3),0,0);
        expected.insertElement(new Complex(-18,4),0,1);
        expected.insertElement(new Complex(16,17),1,0);
        expected.insertElement(new Complex(1,-3),1,1);
        expected.insertElement(new Complex(23,2),0,2);
        expected.insertElement(new Complex(-28,10),0,3);
        expected.insertElement(new Complex(29,24),1,2);
        expected.insertElement(new Complex(1,-5),1,3);
        expected.insertElement(new Complex(25,20),0,4);
        expected.insertElement(new Complex(-40,-10),0,5);
        expected.insertElement(new Complex(15,50),1,4);
        expected.insertElement(new Complex(5,-5),1,5);
        expected.insertElement(new Complex(16,31),2,0);
        expected.insertElement(new Complex(-30,-16),2,1);
        expected.insertElement(new Complex(2,43),3,0);
        expected.insertElement(new Complex(5,-3),3,1);
        expected.insertElement(new Complex(37,-36),2,2);
        expected.insertElement(new Complex(-24,62),2,3);
        expected.insertElement(new Complex(83,-14),3,2);
        expected.insertElement(new Complex(-7,-9),3,3);
        expected.insertElement(new Complex(-5,37),2,4);
        expected.insertElement(new Complex(-14,-46),2,5);
        expected.insertElement(new Complex(-41,45),3,4);
        expected.insertElement(new Complex(8,2),3,5);

        performKroneckerProduct(matrixA, matrixB, matrixC);
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
        assertArrayEquals(expected.getRawRealData(), matrixC.getRawRealData());
    }

    private void performMatrixProduct(ComplexTensor matrixA, ComplexTensor matrixB, ComplexTensor matrixC) {
        UnitaryOperand.matrixProduct(matrixA.getRawRealData(), matrixA.getRawImagData(), matrixA.shape()[0],
                matrixA.shape()[1], matrixB.getRawRealData(), matrixB.getRawImagData(), matrixB.shape()[1],
                matrixC.getRawRealData(), matrixC.getRawImagData());
    }

    private void performMatrixVectorProduct(ComplexTensor matrix, ComplexTensor vector, ComplexTensor result) {
        UnitaryOperand.matrixVectorProduct(matrix.getRawRealData(), matrix.getRawImagData(), matrix.shape()[0],
                matrix.shape()[1], vector.getRawRealData(), vector.getRawImagData(), result.getRawRealData(),
                result.getRawImagData());
    }

    private void performKroneckerProduct(ComplexTensor matrixA, ComplexTensor matrixB, ComplexTensor matrixC) {
        UnitaryOperand.kroneckerProduct(matrixA.getRawRealData(), matrixA.getRawImagData(), matrixA.shape()[0],
                matrixA.shape()[1], matrixB.getRawRealData(), matrixB.getRawImagData(), matrixB.shape()[0],
                matrixB.shape()[1], matrixC.getRawRealData(), matrixC.getRawImagData());
    }
}
