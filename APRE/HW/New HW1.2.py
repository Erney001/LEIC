import pandas as pd
import numpy as np
from sklearn.model_selection import KFold
from sklearn import neighbors
from sklearn.metrics import accuracy_score
from statistics import mean
from scipy import stats
import plotly.graph_objects as go
from plotly.subplots import make_subplots
from sklearn.naive_bayes import MultinomialNB

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

fig = make_subplots(rows=3, cols=3, subplot_titles=("Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity", "Marginal_Adhesion", "Single_Epi_Cell_Size", "Bare_Nuclei", "Bland_Chromatin", "Normal_Nucleoli", "Mitoses"))

ben = df[df["Class"] == "benign"]
mal = df[df["Class"] == "malignant"]

fig.add_trace(go.Histogram(
    x=ben["Clump_Thickness"],
    bingroup=1, name="benign", legendgroup='group1', marker_color='#0000FF'), row=1, col=1)

fig.add_trace(go.Histogram(
    x=mal["Clump_Thickness"],
    bingroup=1, name="malign", legendgroup='group2', marker_color="#FF0000"), row=1, col=1)

fig.add_trace(go.Histogram(
    x=ben["Cell_Size_Uniformity"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=1, col=2)

fig.add_trace(go.Histogram(
    x=mal["Cell_Size_Uniformity"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=1, col=2)

fig.add_trace(go.Histogram(
    x=ben["Cell_Shape_Uniformity"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=1, col=3)

fig.add_trace(go.Histogram(
    x=mal["Cell_Shape_Uniformity"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=1, col=3)

fig.add_trace(go.Histogram(
    x=ben["Marginal_Adhesion"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=2, col=1)

fig.add_trace(go.Histogram(
    x=mal["Marginal_Adhesion"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=2, col=1)

fig.add_trace(go.Histogram(
    x=ben["Single_Epi_Cell_Size"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=2, col=2)

fig.add_trace(go.Histogram(
    x=mal["Single_Epi_Cell_Size"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=2, col=2)

fig.add_trace(go.Histogram(
    x=ben["Bare_Nuclei"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=2, col=3)

fig.add_trace(go.Histogram(
    x=mal["Bare_Nuclei"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=2, col=3)

fig.add_trace(go.Histogram(
    x=ben["Bland_Chromatin"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=3, col=1)

fig.add_trace(go.Histogram(
    x=mal["Bland_Chromatin"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=3, col=1)

fig.add_trace(go.Histogram(
    x=ben["Normal_Nucleoli"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=3, col=2)

fig.add_trace(go.Histogram(
    x=mal["Normal_Nucleoli"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=3, col=2)

fig.add_trace(go.Histogram(
    x=ben["Mitoses"],
    bingroup=1, name="benign", legendgroup='group1', showlegend=False, marker_color='#0000FF'), row=3, col=3)

fig.add_trace(go.Histogram(
    x=mal["Mitoses"],
    bingroup=1, name="malign", legendgroup='group2', showlegend=False, marker_color="#FF0000"), row=3, col=3)

fig.update_layout(
    barmode="overlay",
    bargap=0.1)

#fig.show()  # Pergunta 5

Y = df["Class"]
X = df.drop(columns=["Class"])

kf10 = KFold(n_splits=10)
model = neighbors.KNeighborsClassifier(n_neighbors=3, weights='uniform', p=2)
gnb = MultinomialNB()

Y = [0 if x == "benign" else 1 for x in Y]
Y = np.ravel(pd.DataFrame(Y))

acc_knn = []
acc_gnb = []
i = 1
for train_index, test_index in kf10.split(df):
    X_train = X.iloc[train_index].loc[:, features].values
    X_test = X.iloc[test_index][features].values
    y_train = Y[train_index]
    y_test = Y[test_index]

    model.fit(X_train, y_train)
    gnb.fit(X_train, y_train)
    acc_knn += [accuracy_score(y_test, model.predict(X_test))]
    acc_gnb += [accuracy_score(y_test, gnb.predict(X_test))]
    i += 1

#print("KNN: ", mean(acc_knn), " GNB: ", mean(acc_gnb))  # Pergunta 6

#print(stats.ttest_rel(acc_knn, acc_gnb))  # Pergunta 7
