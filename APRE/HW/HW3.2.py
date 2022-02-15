import pandas as pd
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import train_test_split
from yellowbrick.regressor import ResidualsPlot
import matplotlib.pyplot as plt

df = pd.read_csv("kin.csv", dtype={"theta1": float, "theta2": float, "theta3": float, "theta4": float, "theta5": float,
                                   "theta6": float, "theta7": float, "theta8": float, "y": float})

Y = df["y"]
X = df.drop(columns=["y"])

x_train, x_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, shuffle=True, random_state=0)

clf = MLPRegressor(activation="relu", hidden_layer_sizes=(2, 3, 2), random_state=1)

visualizer = ResidualsPlot(clf, hist=False)
visualizer.fit(x_train.values, y_train)
visualizer.score(x_test.values, y_test)
visualizer.show()

clf2 = MLPRegressor(alpha=1e-5, activation="relu", hidden_layer_sizes=(2, 3, 2), random_state=1)

visualizer2 = ResidualsPlot(clf2, hist=False)
visualizer2.fit(x_train.values, y_train)
visualizer2.score(x_test.values, y_test)
visualizer2.show()
