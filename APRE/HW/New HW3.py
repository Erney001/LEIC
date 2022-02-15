# pergunta 2
from sklearn.neural_network import MLPClassifier
import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import confusion_matrix

df = pd.read_csv("test.csv", dtype={"Clump_Thickness": int,
                                    "Cell_Size_Uniformity": int,
                                    "Cell_Shape_Uniformity": int,
                                    "Marginal_Adhesion": int,
                                    "Single_Epi_Cell_Size": int,
                                    "Bare_Nuclei": float,
                                    "Bland_Chromatin": int,
                                    "Normal_Nucleoli": int,
                                    "Mitoses": int,
                                    "Class": str}, na_values=["?"])

df = df.dropna()

columns = ["Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity", "Marginal_Adhesion",
           "Single_Epi_Cell_Size", "Bare_Nuclei", "Bland_Chromatin", "Normal_Nucleoli", "Mitoses", "Class"]
features = columns[:-1]
classes = ["benign", "malign"]

Y = df["Class"]
X = df.drop(columns=["Class"])

Y = [0 if x == "benign" else 1 for x in Y]
Y = np.ravel(pd.DataFrame(Y))

kf = StratifiedKFold(n_splits=5, random_state=132, shuffle=True)

# l2 regularization/alpha a default
clf = MLPClassifier(activation="relu", hidden_layer_sizes=(3, 2), random_state=1)
clf2 = MLPClassifier(activation="relu", hidden_layer_sizes=(3, 2), random_state=1, early_stopping=True)

mat = [[0, 0], [0, 0]]
mat2 = [[0, 0], [0, 0]]
for train_index, test_index in kf.split(X, Y):
    x_train = X.iloc[train_index].loc[:, features].values
    x_test = X.iloc[test_index][features].values
    y_train = Y[train_index]
    y_test = Y[test_index]

    clf.fit(x_train, y_train)
    y_pred = clf.predict(x_test)

    clf2.fit(x_train, y_train)
    y_pred2 = clf2.predict(x_test)

    mat += confusion_matrix(y_test, y_pred)
    mat2 += confusion_matrix(y_test, y_pred2)

print(mat)
print(mat2)

sns.heatmap(mat, annot=True)
plt.title("Without early stopping")
plt.show()

sns.heatmap(mat2, annot=True)
plt.title("With early stopping")
plt.show()
