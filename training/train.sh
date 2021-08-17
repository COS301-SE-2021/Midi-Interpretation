#!/bin/bash

echo Please enter the feature size, in kb
read filesize

echo Please enter the number of dataset records
read outRecords

echo Please enter the size of the network input layer
read nPCA

python dataset_creator.py $filesize training_data/ 100 maps/genre_classifications.csv $outRecords maps/track_classifications.csv
python dataset_divider.py training_testing_sets/dataset.npy training_testing_sets/classifications.npy 
python nn_trainer.py training_testing_sets/X_train.npy training_testing_sets/Y_train.npy training_testing_sets/X_test.npy training_testing_sets/Y_test.npy $nPCA