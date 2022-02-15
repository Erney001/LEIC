import matplotlib.pyplot as plt

mlps, trees, bayesians = [], [], []
data_dim = (2, 5, 10, 12, 13)

for n in data_dim:
    mlp = n * n * 3 + n * 3 + 2 * n + 2
    tree = 3**n
    bayesian = 1 + 2 * (n + (n*n - n) / 2)

    mlps += [mlp]
    trees += [tree]
    bayesians += [bayesian]

    print(n, mlp, tree, bayesian)

plt.plot(data_dim, mlps, label="MLP")
plt.plot(data_dim, trees, label="Decision Tree")
plt.plot(data_dim, bayesians, label="Bayesian classifier")
plt.xticks(data_dim)
plt.xlabel("Data dimensionality")
plt.ylabel("VC dimension")
plt.title("Gráfico 3-b)")
plt.legend()
plt.show()

mlps2, bayesians2 = [], []
data_dim2 = (2, 5, 10, 30, 100, 300, 1000)

for n in data_dim2:
    mlp = n * n * 3 + n * 3 + 2 * n + 2
    bayesian = 1 + 2 * (n + (n*n - n) / 2)

    mlps2 += [mlp]
    bayesians2 += [bayesian]

    print(n, mlp, bayesian)

plt.plot(data_dim2, mlps2, label="MLP")
plt.plot(data_dim2, bayesians2, label="Bayesian classifier")
plt.xticks(data_dim2)
plt.xlabel("Data dimensionality")
plt.ylabel("VC dimension")
plt.title("Gráfico 3-c)")
plt.legend()
plt.show()
