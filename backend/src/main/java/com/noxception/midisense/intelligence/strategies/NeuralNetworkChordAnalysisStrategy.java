package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.ChordPrediction;
import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Implements the functionality of {@link ChordAnalysisStrategy} by way of an artificial neural network to generate
 * classification classes (chords) and their certainty for an input byte stream.
 *
 * This strategy makes use of network with a single hidden layer, whose exact parameterization is pre-set during
 * training. The exact values of the two weight and bias matrices are loaded from configurations at runtime.
 *
 * The activation functions used are as follows:
 * <ul>
 *     <li>Hyperbolic Tangent: between the input and hidden layer</li>
 *     <li>Sigmoid Logistic : between the hidden and classification layer</li>
 * </ul>
 *
 * The classification layer indices with greatest magnitude correspond to the most appropriate choice of chord, of which
 * a specified amount with the highest certainties are chosen
 *
 */
public class NeuralNetworkChordAnalysisStrategy implements ChordAnalysisStrategy{


    /**
     * CONSTRUCTOR
     *
     * Generates the two weight and bias elements for the network and loads their predefined training values
     *
     */
    public NeuralNetworkChordAnalysisStrategy() {

    }

    /**
     * Method to classify the most likely genres to be associated with a piece
     * @param features a byte array encoding the specific work
     * @return an array of predicted genres for the piece, with their certainties
     */
    @Override
    public ChordPrediction[] classify(byte[] features) {
        return  null;
    }

    /**Auxilliary method to propagate an input layer through to the classification layer, based on the existing values
     * of the weight matrices and bias vector
     *
     * @param x a vector (matrix) encoding the input layer observation (or whole dataset)
     * @return the vector corresponding to the classification layer
     */
    public SimpleMatrix feedForward(SimpleMatrix x){
        return null;
    }

    /**
     * @return the size of the input layer
     */
    public int getInputLayerSize() {
        return 0;//return inputLayerSize;
    }

    /**
     * @return the size of the hidden layer
     */
    public int getHiddenLayerSize() {
        return 0;//return hiddenLayerSize;
    }

    /**
     * @return the size of the output layer, corresponding to the number of classification classes
     */
    public int getClassificationClasses() {
        return 0;//return classificationClasses;
    }

    // Element-Wise Activation

    /**The single-valued sigmoid logistic value of the specified input
     *
     * @param x a real valued input
     * @return the real result of applying the sigmoid activation
     */
    private double sigmoid(double x){
        return 1.0 / (1.0 + Math.exp(-1*x));
    }


    /**The single-valued hyperbolic tangent of the specified input
     *
     * @param x a real valued input
     * @return the real result of applying the hyperbolic tangent
     */
    private double tanh(double x){
        return Math.tanh(x);
    }

    // Matrix-wise Activations

    /**The element-wise application of the sigmoid logistic value to each value in the specified container
     *
     * @param A a real valued matrix input
     * @return the real matrix result of applying the sigmoid activation to each element in A
     */
    public SimpleMatrix sigmoid(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A);
        for (int i=0; i<B.numRows(); i++){
            for (int j=0; j<B.numCols(); j++){
                B.set(i,j,sigmoid(B.get(i,j)));
            }
        }
        return B;
    }

    /**The element-wise application of the hyperbolic tangent to each value in the specified container
     *
     * @param A a real valued matrix input
     * @return the real matrix result of applying the hyperbolic tangent to each element in A
     */
    public SimpleMatrix tanh(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A);
        for (int i=0; i<B.numRows(); i++){
            for (int j=0; j<B.numCols(); j++){
                B.set(i,j,tanh(B.get(i,j)));
            }
        }
        return B;
    }

    /**
     * Nested class to implement comparison between two genre predictions based on certainty
     */
    static class sortByConfidenceReversed implements Comparator<ChordPrediction>{

        @Override
        public int compare(ChordPrediction o1, ChordPrediction o2) {
            // sorted by 3 decimal places worth of accuracy
            return (int) Math.round(100000*(o2.getCertainty()-o1.getCertainty()));
        }
    }




}
