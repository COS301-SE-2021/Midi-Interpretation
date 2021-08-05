package com.noxception.midisense.intelligence.strategies;

import com.noxception.midisense.intelligence.dataclass.GenrePredication;
import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.Comparator;

public class NeuralNetworkGenreAnalysisStrategy implements GenreAnalysisStrategy{

    private final int inputLayerSize = 100;
    private final int hiddenLayerSize = 100;

    // Network Weights
    private SimpleMatrix w1;
    private SimpleMatrix w2;

    // Network Biases
    private SimpleMatrix b1;
    private SimpleMatrix b2;




    public NeuralNetworkGenreAnalysisStrategy() {

        //allocate weights and biases
        w1 = new SimpleMatrix(hiddenLayerSize,inputLayerSize);
        b1 = new SimpleMatrix(hiddenLayerSize,1);

        w2 = new SimpleMatrix(classificationClasses,hiddenLayerSize);
        b2 = new SimpleMatrix(classificationClasses,1);

        //randomise : TODO: replace by loading generated values
        for(SimpleMatrix caseMatrix: new SimpleMatrix[]{w1, w2, b1, b2}){
            for(int i=0; i<caseMatrix.numRows(); i++){
                for(int j=0; j<caseMatrix.numCols(); j++){
                    caseMatrix.set(i,j,Math.random()/100);
                }
            }
        }

    }

    /**
     * Method to classify the most likely genres to be associated with a piece
     * @param features placeholder
     * @return an array of predicted genres for the piece
     */
    @Override
    public GenrePredication[] classify(byte[] features) {

        //forward propagation
        SimpleMatrix inputLayer = new SimpleMatrix(inputLayerSize,1);

        //set features : TODO fill in the necessary features from the array here

        //feed forward
        SimpleMatrix outputLayer = feedForward(inputLayer);

        //get the predictions for the classes
        GenrePredication[] totalPredictions = new GenrePredication[classificationClasses];
        for(int i=0; i<classificationClasses; i++){
            totalPredictions[i] = new GenrePredication(classes[i],outputLayer.get(i,0));
        }

        //sort the predictions
        Arrays.sort(totalPredictions,new sortByConfidence());

        //return only the specified amount
        return Arrays.copyOfRange(totalPredictions,0,totalSuggestions);

    }

    public SimpleMatrix feedForward(SimpleMatrix x){

        //layer 1 activation
        SimpleMatrix z1 = (w1.mult(x)).plus(b1);
        SimpleMatrix a1 = tanh(z1);

        //layer 2 activation
        SimpleMatrix z2 = (w2.mult(a1)).plus(b2);
        SimpleMatrix a2 = sigmoid(z2);

        return a2;
    }

    public int getInputLayerSize() {
        return inputLayerSize;
    }

    public int getHiddenLayerSize() {
        return hiddenLayerSize;
    }

    public int getClassificationClasses() {
        return classificationClasses;
    }

    public int getTotalSuggestions() {
        return totalSuggestions;
    }

    // Element-Wise Activation

    private double sigmoid(double x){
        return 1.0 / (1.0 + Math.exp(-1*x));
    }

    private double tanh(double x){
        return Math.tanh(x);
    }

    // Matrix-wise Activations

    public SimpleMatrix sigmoid(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A);
        for (int i=0; i<B.numRows(); i++){
            for (int j=0; j<B.numCols(); j++){
                B.set(i,j,sigmoid(B.get(i,j)));
            }
        }
        return B;
    }

    public SimpleMatrix tanh(SimpleMatrix A){
        SimpleMatrix B = new SimpleMatrix(A);
        for (int i=0; i<B.numRows(); i++){
            for (int j=0; j<B.numCols(); j++){
                B.set(i,j,tanh(B.get(i,j)));
            }
        }
        return B;
    }

    static class sortByConfidence implements Comparator<GenrePredication>{

        @Override
        public int compare(GenrePredication o1, GenrePredication o2) {
            return (int) Math.round(o1.getCertainty()-o2.getCertainty());
        }
    }




}
