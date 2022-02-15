# pergunta 3
import pandas as pd
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import KFold
import seaborn as sns
import matplotlib.pyplot as plt

df = pd.read_csv("kin.csv", dtype={"theta1": float, "theta2": float, "theta3": float, "theta4": float, "theta5": float,
                                   "theta6": float, "theta7": float, "theta8": float, "y": float})

features = ["theta1", "theta2", "theta3", "theta4", "theta5", "theta6", "theta7", "theta8"]

Y = df["y"]
X = df.drop(columns=["y"])

kf = KFold(n_splits=5, random_state=132, shuffle=True)

clf = MLPRegressor(alpha=0, activation="relu", hidden_layer_sizes=(3, 2), random_state=1)
clf2 = MLPRegressor(alpha=6, activation="relu", hidden_layer_sizes=(3, 2), random_state=1)

res = []
res2 = []
for train_index, test_index in kf.split(X, Y):
    x_train = X.iloc[train_index].loc[:, features].values
    x_test = X.iloc[test_index][features].values
    y_train = Y[train_index]
    y_test = Y[test_index]

    clf.fit(x_train, y_train)
    tmp = list(clf.predict(x_test))

    clf2.fit(x_train, y_train)
    tmp2 = list(clf2.predict(x_test))

    for i in range(0, len(tmp), 1):
        resid = tmp[i] - y_test.iloc[i]
        resid2 = tmp2[i] - y_test.iloc[i]
        res += [resid]
        res2 += [resid2]

print(res)
sns.boxplot(x=res)
plt.title("Without regularization")
plt.show()

print(res2)
sns.boxplot(x=res2)
plt.title("With regularization")
plt.show()
