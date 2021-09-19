package com.noxception.midisense.intelligence;

import com.noxception.midisense.config.StandardConfig;
import com.noxception.midisense.dataclass.MockConfigurationSettings;
import com.noxception.midisense.intelligence.dataclass.GenrePredication;
import com.noxception.midisense.intelligence.strategies.NeuralNetworkGenreAnalysisStrategy;
import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NeuralNetworkTest {

    private NeuralNetworkGenreAnalysisStrategy strategy;

    @BeforeEach
    public void mountModule(){
        StandardConfig configurations = new MockConfigurationSettings();
        this.strategy = new NeuralNetworkGenreAnalysisStrategy(configurations);
    }

    @Test
    public void test_Sigmoid_IfAllZero_thenAllHalf(){
        SimpleMatrix A = new SimpleMatrix(2,2);
        A = strategy.sigmoid(A);
        for(int i=0; i<2; i++ ){
            for(int j=0; j<2; j++){
                Assertions.assertEquals(A.get(i,j),0.5);
            }
        }
    }

    @Test
    public void test_tanh_IfAllZero_thenAllZero(){
        SimpleMatrix A = new SimpleMatrix(2,2);
        A = strategy.tanh(A);
        for(int i=0; i<2; i++ ){
            for(int j=0; j<2; j++){
                Assertions.assertEquals(A.get(i,j),0);
            }
        }
    }

    @Test
    public void test_feedForward_IfAllZero_ThenAllOtherThanZero(){
        SimpleMatrix A = new SimpleMatrix(strategy.getInputLayerSize(),1);
        A = strategy.feedForward(A);
        for(int i=0; i<strategy.getClassificationClasses(); i++ ){
            Assertions.assertTrue(A.get(i,0) != 0);
        }
    }

    @Test
    public void test_classify_IfValidByteArray_ThenThreeFullSolutions(){
        byte[] testFile = new byte[]{1,2,3,4};
        GenrePredication[] predictions = strategy.classify(testFile);
        Assertions.assertTrue(predictions.length > 0);

    }


}
