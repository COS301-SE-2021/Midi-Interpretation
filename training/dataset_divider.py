import sys
from sklearn.model_selection import train_test_split
import numpy as np

"""
    Method to divide a numpy matrix X, representing a dataset with features along its columns, and a compound array Y,
    representing the classifications associated with a row in X, into Training and Testing sets in a 60/40 ratio.

    The program takes in two parameters, the names of the dataset X and classifications Y, as .npy files.
"""


def main():
    X_name = None
    Y_name = None

    # load names
    try:
        X_name = str(sys.argv[1])
        Y_name = str(sys.argv[2])
    except Exception:
        sys.exit("Usage is: python dataset_divider <dataset> <classification>")

    # load
    try:
        X = np.load(X_name)
        Y = np.load(Y_name,allow_pickle=True)
    except Exception:
        sys.exit("Invalid Training and Test set provided")

    # partition into training, testing and CV data
    X_train, X_test, Y_train, Y_test = train_test_split(X, Y, test_size=0.4, random_state=25)

    np.save("X_train.npy",X_train)
    np.save("X_test.npy", X_test)
    np.save("Y_train.npy", Y_train)
    np.save("Y_test.npy", Y_test)

if __name__ == "__main__":
    main()