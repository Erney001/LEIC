from sklearn import datasets
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import neighbors
from sklearn.naive_bayes import GaussianNB
from sklearn.model_selection import KFold
import numpy as np
import pandas as pd
from sklearn.model_selection import cross_val_score
from statistics import mean

iris = datasets.load_iris()
x = iris.data
y = iris.target

x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=.2, random_state=0)

classifier = neighbors.KNeighborsClassifier()
classifier.fit(x_train, y_train)
predictions = classifier.predict(x_test)

#print(accuracy_score(y_test, predictions))

gnb = GaussianNB()
y_pred = gnb.fit(x_train, y_train).predict(x_test)

#print(accuracy_score(y_test, y_pred))

#####################################################################################################

#iris = datasets.load_iris(as_frame=True)
#iris_df = iris.frame
#X = df.iloc[:, :-1]
#y = df.iloc[:, -1]

kf10 = KFold(n_splits=10,   shuffle=True)
model = neighbors.KNeighborsClassifier()

iris = datasets.load_iris(return_X_y=False)
iris_df = pd.DataFrame(data=iris.data, columns=iris.feature_names)
features = iris['feature_names']
iris_df['target'] = iris.target
iris_df["target_name"] = iris_df['target'].map({i: name for i, name in enumerate(iris.target_names)})

acc_knn = []
acc_gnb = []
i = 1
for train_index, test_index in kf10.split(iris_df):
    X_train = iris_df.iloc[train_index].loc[:, features].values
    X_test = iris_df.iloc[test_index][features].values
    y_train = iris_df.iloc[train_index].loc[:, 'target']
    y_test = iris_df.loc[test_index]['target']

    # Train the model
    model.fit(X_train, y_train)  # Training the model
    gnb.fit(X_train, y_train)
    acc_knn += [accuracy_score(y_test, model.predict(X_test))]
    acc_gnb += [accuracy_score(y_test, gnb.predict(X_test))]
    #print(f"Accuracy for the fold no. {i} on the test set using KNN: {accuracy_score(y_test, model.predict(X_test))}")
    #print(f"Accuracy for the fold no. {i} on the test set using NB: {accuracy_score(y_test, gnb.predict(X_test))}")
    i += 1

print("KNN: ", mean(acc_knn), ", GNB: ", mean(acc_gnb))

for train_index, test_index in kf10.split(iris_df):
    break;
    #X_train = iris_df.iloc[train_index].loc[:, data]
    #X_test = iris_df.iloc[test_index][data]
    #y_train = iris_df.iloc[train_index].loc[:, 'target']
    #y_test = iris_df.loc[test_index]['target']
    #classifier.fit(X_train, y_train)
    #pred_values = classifier.predict(X_test)
    #acc = accuracy_score(pred_values, y_test)
    #print(acc)

    #print(np.take(iris.data, train_index), np.take(iris.data, test_index))
    #X_train, X_test = iris.iloc[train_index, :], iris.iloc[test_index, :]
    #y_train, y_test = y[train_index], y[test_index]


#result = cross_val_score(classifier, x, y, cv=kf10)
