from sklearn.neural_network import MLPClassifier
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
import seaborn as sn
import matplotlib.pyplot as plt

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

columns = ["Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity", "Marginal_Adhesion", "Single_Epi_Cell_Size", "Bare_Nuclei", "Bland_Chromatin", "Normal_Nucleoli", "Mitoses", "Class"]
features = columns[:-1]
classes = ["benign", "malign"]

Y = df["Class"]
X = df.drop(columns=["Class"])

Y = [0 if x == "benign" else 1 for x in Y]
Y = np.ravel(pd.DataFrame(Y))

x_train, x_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, shuffle=True, random_state=0)

# que tipo de l2 regularization?
clf = MLPClassifier(alpha=1e-5, activation="relu", hidden_layer_sizes=(2, 3, 2), random_state=1)

clf.fit(x_train.values, y_train)
y_pred = clf.predict(x_test.values)

data = {"True": y_test, "Pred": y_pred}

df = pd.DataFrame(data, columns=["True", "Pred"])
confusion_matrix = pd.crosstab(df['True'], df['Pred'], rownames=['True'], colnames=['Pred'])

sn.heatmap(confusion_matrix, annot=True)
plt.title("Without early stopping")
plt.show()

clf2 = MLPClassifier(alpha=1e-5, activation="relu", hidden_layer_sizes=(2, 3, 2), random_state=1, early_stopping=True)

clf2.fit(x_train.values, y_train)
y_pred2 = clf2.predict(x_test.values)

data2 = {"True": y_test, "Pred": y_pred2}

df2 = pd.DataFrame(data2, columns=["True", "Pred"])
confusion_matrix2 = pd.crosstab(df2['True'], df2['Pred'], rownames=['True'], colnames=['Pred'])

sn.heatmap(confusion_matrix2, annot=True)
plt.title("With early stopping")
plt.show()
