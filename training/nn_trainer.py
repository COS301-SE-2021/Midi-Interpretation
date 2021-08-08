import sys
import numpy as np
from sklearn.metrics import accuracy_score, confusion_matrix
from sklearn.decomposition import PCA

# descent rate
a = 0.05

# components of reduction
n_pca = 20

# genres (classification classes)
ng = 100

# hidden layer scale
nh = 100

# iterative descent
ni = 100

# metric to gauge how many components of PCA should be used
expected_variance_preserved = 0.90


# ==================================

# activation functions

def sigmoid(x):
    return 1.0 / (1.0 + np.exp(-x))


def grad_sigmoid(x):
    return sigmoid(x) * (1 - sigmoid(x))


def tanh(x):
    return np.tanh(x)


def grad_tanh(x):
    return 1 - np.tanh(x) ** 2


# ==================================

# read in training and test sets / classifications

training_set_name, training_label_name, test_set_name, test_label_name = None, None, None, None
X_train, X_test, y_train, y_test = [], [], [], []
try:
    training_set_name, training_label_name, test_set_name, test_label_name = sys.argv[1], sys.argv[2], sys.argv[3], \
                                                                             sys.argv[4]
    X_train = np.load(training_set_name)
    X_test = np.load(test_set_name)
    y_train = np.load(training_label_name,allow_pickle=True)
    y_test = np.load(test_label_name,allow_pickle=True)

except (IndexError, FileNotFoundError) as e:
    sys.exit("[ERROR] Could not find specified input files for training and test sets and labels.")

try:
    n_pca = float(sys.argv[5])
except IndexError as e:
    print("[INFO] No PCA reduction specified, using {}".format(n_pca))

print(
    "[INFO] Training set has {} observations of {} features, with {} labels. Test set has {} observations of {} features, with {} labels.".format(
        X_train.shape[0], X_train.shape[1], y_train.shape[0], X_test.shape[0], X_test.shape[1], y_test.shape[0]))

# perform pca on the set, ensuring variance of over EVR

# TO BE USED IF ADAPTIVE PCA IS REQUIRED

# pca = PCA().fit(X_train)
# xv = np.cumsum(pca.explained_variance_ratio_)
# opt_components = [i for i in range(len(xv)) if xv[i] >= expected_variance_preserved][0]
# print("[PCA OPTIMISATION] EVR of >{} found for >{} components".format(expected_variance_preserved, opt_components))
# n_pca = opt_components

pca = PCA(n_components=n_pca)
pca.fit(X_train)
L = pca.components_
print(L.shape)
X_train = pca.transform(X_train)
X_test = pca.transform(X_test)

print(
    "[PCA REDUCTION] Training set now has {} observations of {} features, with {} labels. Test set now has {} observations of {} features, with {} labels.".format(
        X_train.shape[0], X_train.shape[1], y_train.shape[0], X_test.shape[0], X_test.shape[1], y_test.shape[0]))

# Transpose data for immediate calculation of whole dataset

X_train = X_train.T
X_test = X_test.T

N = X_train.shape[1]
N_test = X_test.shape[1]
D = X_train.shape[0]

# Get the actual as the indicator vector of the classifications.
"""
    Suppose there are 3 classification classes with an observation having class 0 and 2, then y_actual is [1 0 1]
"""
y_actual = None
for k in y_train:
    v = np.array([int(l in k) for l in range(ng)]).reshape((ng,1))
    y_actual = v if y_actual is None else np.concatenate((y_actual,v),axis=1)


# randomly initialise weights and biases according to gaussian dist

W1 = np.random.randn(nh, D) / 100
b1 = np.random.randn(nh, 1) / 100

W2 = np.random.randn(ng, nh) / 100
b2 = np.random.randn(ng, 1) / 100

for iteration in range(ni):

    # forward prop

    # layer 1

    z1 = np.matmul(W1, X_train) + b1
    a1 = tanh(z1)

    # layer 2

    z2 = np.matmul(W2, a1) + b2
    a2 = sigmoid(z2)

    # least squares batch cost
    J = (1 / (2 * N)) * np.sum(np.power(a2 - y_actual, 2))

    # backpropagation

    # due to activations

    dJ_dz2 = (a2 - y_actual) * grad_sigmoid(z2)
    dJ_dz1 = np.matmul(W2.T, dJ_dz2) * grad_tanh(z1)

    # due to weights

    dJ_dW2 = (1 / N) * np.matmul(dJ_dz2, a1.T)
    dJ_dW1 = (1 / N) * np.matmul(dJ_dz1, X_train.T)

    # due to biases

    dJ_db2 = (1 / N) * np.sum(dJ_dz2, axis=1, keepdims=True)
    dJ_db1 = (1 / N) * np.sum(dJ_dz1, axis=1, keepdims=True)

    # descent

    W1 = W1 - a * dJ_dW1
    W2 = W2 - a * dJ_dW2
    b1 = b1 - a * dJ_db1
    b2 = b2 - a * dJ_db2

    # forward prop for testing

    """
        From this point in time, accuracy is deemed to be the proportion of classifications where the output occurs
        within the set of expected classifications for a particular training example, across all examples.

        That is, suppose in a system of 3 classes that the following classifications are made:

            0 , 1 , 0 , 2

        Where the expectation sets are:

            [1,2] ; [0,1] ; [] ; [0,1,2]

        Then the accuracy is 2 / 4 = 50%
    """

    # training set
    z1 = np.matmul(W1, X_train) + b1
    a1 = tanh(z1)
    z2 = np.matmul(W2, a1) + b2
    a2 = sigmoid(z2)
    pred_train = np.array([np.argmax(a2[:, i]) for i in range(N)])
    successes_train = np.array([int(pred_train[i] in y_train[i]) for i in range(N)])
    train_accuracy = np.sum(successes_train) / successes_train.shape[0]

    # testing set
    z1 = np.matmul(W1, X_test) + b1
    a1 = tanh(z1)
    z2 = np.matmul(W2, a1) + b2
    a2 = sigmoid(z2)
    pred_test = np.array([np.argmax(a2[:, i]) for i in range(N_test)])
    successes_test = np.array([int(pred_test[i] in y_test[i]) for i in range(N_test)])
    test_accuracy = np.sum(successes_test) / successes_test.shape[0]

    print(
        "[TRAINING/CLASSIFICATION] Iteration: [{}] | Cost: [{:0.3f}] | Training accuracy: [{:0.3f}%] | Test accuracy: [{:0.3f}%]".format(
            iteration + 1, J, 100 * train_accuracy, 100 * test_accuracy), end="\r")

    if (iteration == (ni - 1)):
        print()

        # save the weights and biases to file
        np.save("output/W1.npy",W1)
        np.save("output/W2.npy",W2)
        np.save("output/b1.npy",b1)
        np.save("output/b2.npy",b2)
        np.save("output/L.npy", L)
