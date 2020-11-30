package utils;

public class ComplexTensor {
    private int rank;
    private int[] shape;

    private double[] dataR;
    private double[] dataI;

    public ComplexTensor(int[] shape) {
        this.rank = shape.length;
        this.shape = shape;
    }
}
