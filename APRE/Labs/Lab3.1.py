from sklearn import datasets
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier, plot_tree
from sklearn import metrics
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import cross_val_score
from scipy import stats
from sklearn import feature_selection

iris = datasets.load_iris()
x = iris.data
y = iris.target

x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=.2)

clf = DecisionTreeClassifier()
clf.fit(x_train, y_train)

# Ex 8
# a)
#fig = plt.figure(figsize=(25, 20))
#_ = tree.plot_tree(clf, feature_names=iris.feature_names, class_names=iris.target_names, filled=True)
#fig.show()

# b)
pred_test = clf.predict(x_test)
#print('Acc on test data: ', "{:.3f}".format(metrics.accuracy_score(pred_test, y_test)))

pred_train = clf.predict(x_train)
#print('Acc on train data: ', "{:.3f}".format(metrics.accuracy_score(pred_train, y_train)))

# c)
rf = RandomForestClassifier()
rf.fit(x_train, y_train)

scores_rf = tuple(cross_val_score(rf, x, y, scoring='accuracy', cv=10))
scores_dt = tuple(cross_val_score(clf, x, y, scoring='accuracy', cv=10))

print(scores_rf)
print(scores_dt)

print(stats.ttest_rel(scores_dt, scores_dt))  # da nan values

# Ex 9
print(feature_selection.mutual_info_classif(x, y))
