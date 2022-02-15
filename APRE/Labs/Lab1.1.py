import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import neighbors
from sklearn.metrics import accuracy_score

data = pd.read_csv("lab1.csv")

x1 = np.asarray(data.y1)
x = x1.reshape(-1, 1)
y = np.asarray(data.y3)

x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=.5)

classifier = neighbors.KNeighborsClassifier()

classifier.fit(x_train, y_train)
predictions = classifier.predict(x_test)

print(accuracy_score(y_test, predictions))
