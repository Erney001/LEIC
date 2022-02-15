import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from pandas.plotting import parallel_coordinates
from sklearn.tree import DecisionTreeClassifier, plot_tree
from sklearn import metrics
from sklearn.naive_bayes import GaussianNB
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis, QuadraticDiscriminantAnalysis
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.linear_model import LogisticRegression

data = pd.read_csv("HW1.csv")

train, test = train_test_split(data, test_size=0.5, stratify=data['clas'], random_state=42)

X_train = train[['y1', 'y3', 'y4', 'clas']]
y_train = train.clas
X_test = test[['y1', 'y3', 'y4', 'clas']]
y_test = test.clas

fn = ["y1", "y3", "y4", "clas"]
cn = ['A', 'B', 'C']

mod_dt = DecisionTreeClassifier(max_depth=3, random_state=1)
mod_dt.fit(X_train, y_train)
prediction = mod_dt.predict(X_test)
print('The accuracy of the Decision Tree is', "{:.3f}".format(metrics.accuracy_score(prediction, y_test)))

plt.figure(figsize=(10, 8))
plot_tree(mod_dt, feature_names=fn, class_names=cn, filled=True)

d = metrics.plot_confusion_matrix(mod_dt, X_test, y_test, display_labels=cn, cmap=plt.cm.Blues, normalize=None)
d.ax_.set_title('Decision Tree Confusion matrix, without normalization')
