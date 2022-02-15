from scipy.io import arff
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import cross_val_score
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.model_selection import KFold
from sklearn import neighbors
from sklearn.metrics import accuracy_score
from statistics import mean

# Ex 5

data = arff.loadarff('breast.w.arff')

df = pd.DataFrame.from_records(data[0], columns=["Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity", "Marginal_Adhesion", "Single_Epi_Cell_Size", "Bare_Nuclei", "Bland_Chromatin", "Normal_Nucleoli", "Mitoses", "Class"])
#df.dropna(subset=["Class"]) not working


sns.pairplot(df, hue="Class", height=2.5)
#sns.pairplot(df, vars=["Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity"], hue="Class", height=2.5)

#plt.show()

# Ex 6

X = df.drop(columns=["Class"])
Y = df["Class"].values

#print(Y)
#Y1 = [1 if x == "b'malignant'" else 0 for x in Y]
#print(Y1)

# Doesn't work
#df_MLB = pd.DataFrame({"Class": Y})
#mlb = MultiLabelBinarizer()
#class_mlb = mlb.fit_transform(df_MLB["Class"])
#print(class_mlb)

knn_cv = KNeighborsClassifier(n_neighbors=2)

#cv_scores = cross_val_score(knn_cv, X, Y, cv=10)

#print(cv_scores)
#print("cv_scores mean:{}".format(np.mean(cv_scores)))

# Ex 6 - 2nd try

#X = np.array([[1, 2], [3, 4], [1, 2], [3, 4]])
#Y = np.array([1, 2, 3, 4])

kf = KFold(n_splits=2)

#for train_index, test_index in kf.split(X):
    #print("TRAIN:", train_index, "TEST:", test_index)
    #X_train, X_test = X[train_index], X[test_index]
    #Y_train, Y_test = Y[train_index], Y[test_index]
    #for i in X_train:
        #print("X", i)
    #for i in X_test:
        #print("t", i)
    #for i in Y_train:
        #print("Y", i)
    #for i in Y_test:
        #print("t", i)

#print(accuracy_score(Y_test, Y_pred))

###############################################################

X = df.drop(columns=["Class"])
Y = df["Class"].values
#print(X)

kf10 = KFold(n_splits=10,   shuffle=True)
model = neighbors.KNeighborsClassifier(n_neighbors=3, weights='uniform', p=2)

features = ["Clump_Thickness", "Cell_Size_Uniformity", "Cell_Shape_Uniformity", "Marginal_Adhesion", "Single_Epi_Cell_Size", "Bare_Nuclei", "Bland_Chromatin", "Normal_Nucleoli", "Mitoses", "Class"]

# pesquisar como passar de strings para ints
#Y = list(map(int, Y))
#print(Y)

print(Y)

for i in range(0, len(Y), 1):
    if Y[i] == "benign":
        Y[i] = 0
    else:
        Y[i] = 1

print(Y)

acc_knn = []
i = 1
for train_index, test_index in kf10.split(df):
    X_train = df.iloc[train_index].loc[:, features].values
    X_test = df.iloc[test_index][features].values
    y_train = df.iloc[train_index].loc[:, 'Class']
    y_test = df.loc[test_index]['Class']

    model.fit(X_train, y_train)
    acc_knn += [accuracy_score(y_test, model.predict(X_test))]
    i += 1

print("KNN: ", mean(acc_knn))
