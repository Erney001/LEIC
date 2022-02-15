from matplotlib.patches import Ellipse
import sklearn
from sklearn.mixture import GaussianMixture
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

points = [[2, 4], [-1, -4], [-1, 2], [4, 0]]
centroid1 = [1.565, 2.101]
centroid2 = [-0.384, -3.418]
cov1 = [[4.133, 2.606], [2.606, -1.163]]
cov2 = [[2.702, 2.169], [2.169, 2.106]]

gmm = GaussianMixture(n_components=2)
gmm.fit(points)

labels = gmm.predict(points)
frame = pd.DataFrame(points)
frame['Cluster'] = labels
frame.columns = ['Y1', 'Y2', 'Cluster']

color = ['blue', 'green']
for k in range(0, 2):
    data = frame[frame["Cluster"] == k]
    plt.scatter(data["Y1"], data["Y2"], c=color[k])
plt.show()

