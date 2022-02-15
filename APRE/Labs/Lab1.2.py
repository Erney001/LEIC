import seaborn as sns
import matplotlib.pyplot as plt

iris = sns.load_dataset("iris")

#g = sns.PairGrid(iris)
#g.map(sns.scatterplot)

sns.pairplot(iris, hue="species", height=2.5)

plt.show()
