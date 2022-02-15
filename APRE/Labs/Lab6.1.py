from sklearn import datasets
from sklearn.neural_network import MLPClassifier
from sklearn.model_selection import cross_val_score
from sklearn.datasets import fetch_california_housing

iris = datasets.load_iris()
X = iris.data
Y = iris.target

clf = MLPClassifier(solver='lbfgs', hidden_layer_sizes=(1, 5), random_state=1)

print(cross_val_score(clf, X, Y, cv=10, scoring="accuracy"))

# nao da :(
housing = fetch_california_housing()
X2 = housing.data
Y2 = housing.target

clf2 = MLPClassifier(solver='lbfgs', hidden_layer_sizes=(1, 5), random_state=1)

print(cross_val_score(clf2, housing.data, housing.target, cv=10, scoring="neg_mean_absolute_error"))
