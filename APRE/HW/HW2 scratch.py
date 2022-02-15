import pandas as pd
from sklearn import metrics
from sklearn import tree
from sklearn.model_selection import train_test_split
import matplotlib.pyplot as plt
from sklearn.feature_selection import SelectKBest
from sklearn.feature_selection import mutual_info_classif

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

acc_test = []
acc_train = []
acc_test2 = []
acc_train2 = []
n_features = [1, 3, 5, 9]  # same as mas_depth = [1, 3, 5, 9]
for i in n_features:
    X_new = SelectKBest(score_func=mutual_info_classif, k=i).fit_transform(X, Y)
    X_train, X_test, Y_train, Y_test = train_test_split(X_new, Y, test_size=0.25, random_state=0)
    X_train2, X_test2, Y_train2, Y_test2 = train_test_split(X, Y, test_size=0.25, random_state=0)

    model1 = tree.DecisionTreeClassifier()  # max_features=i
    model1 = model1.fit(X_train, Y_train)

    model2 = tree.DecisionTreeClassifier(max_depth=i)
    model2 = model2.fit(X_train2, Y_train2)

    Y_pred_train = model1.predict(X_train)
    acc_train += [metrics.accuracy_score(Y_pred_train, Y_train)]

    Y_pred = model1.predict(X_test)
    acc_test += [metrics.accuracy_score(Y_pred, Y_test)]

    Y_pred_train2 = model2.predict(X_train2)
    acc_train2 += [metrics.accuracy_score(Y_pred_train2, Y_train2)]

    Y_pred2 = model2.predict(X_test2)
    acc_test2 += [metrics.accuracy_score(Y_pred2, Y_test2)]

plt.scatter(n_features, acc_test, color="blue", label="Test")
plt.scatter(n_features, acc_train, color="green", label="Train")
plt.legend()
plt.xlabel("Number of features")
plt.ylabel("Accuracy")
plt.show()  # grafico 5-i)

plt.scatter(n_features, acc_test2, color="blue", label="Test")
plt.scatter(n_features, acc_train2, color="green", label="Train")
plt.legend()
plt.xlabel("Max depth")
plt.ylabel("Accuracy")
plt.show()  # grafico 5-ii)
