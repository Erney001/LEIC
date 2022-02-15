import math
import numpy as np
import scipy.stats as stats
from scipy.stats import multivariate_normal
from sklearn.metrics import f1_score

x = np.array([[0.2, 0.4], [-0.1, -0.4], [-0.1, 0.2], [0.8, 0.8]])

#print(np.cov(x))

y1 = [0.6, 0.1, 0.2, 0.1, 0.3, -0.1, -0.3, 0.2, 0.4, -0.2]
y2 = ['A', 'B', 'A', 'C', 'B', 'C', 'C', 'B', 'A', 'C']
y3 = [0.2, -0.1, -0.1, 0.8, 0.1, 0.2, -0.1, 0.5, -0.4, 0.4]
y4 = [0.4, -0.4, 0.2, 0.8, 0.3, -0.2, 0.2, 0.6, -0.7, 0.3]

soma1 = 0

for i in range(0, 4, 1):
    soma1 += 1/3 * (y3[i]-0.20) * (y4[i]-0.25)

#print(soma1)

soma2 = 0

for i in range(4, 10, 1):
    soma2 += 1/5 * (y3[i]-0.117) * (y3[i]-0.117)

#print(soma2)


def norm(x, det, miu, cov):
    a = 1 / (2 * math.pi * math.sqrt(det))
    b = (x - miu)
    c = b.transpose()
    d = np.dot(c, cov)
    e = np.dot(d, b)
    f = math.exp(-0.5*e)
    res = a * f
    return res


x = np.array([0.2, 0.4])
miu = np.array([0.2, 0.25])
cov = np.array([[0.18, 0.18], [0.18, 0.25]])

#print(norm(x, 0.0126, miu, cov))

var0 = multivariate_normal([0.2, 0.25], [[0.18, 0.18], [0.18, 0.25]])
var1 = multivariate_normal([0.117, 0.083], [[0.1097, 0.1223], [0.1223, 0.2137]])

#print(pY3Y4)

#pY10 = stats.norm.pdf(0.6, loc=0.25, scale=0.238)
#pY11 = stats.norm.pdf(0.6, loc=0.05, scale=0.362)

#print(pY1)

#p_letra = 2/4

#print("C = 0: ", pY3Y4*pY10*p_letra, " C = 1: ", pY3Y4*pY11*p_letra)

rows, cols = (10, 21)
arr = [['-']*cols]*rows
th = [0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1]
l = []
norms = [0.371, 0.756, 0.487, 0.372, 0.471, 0.865, 0.880, 0.462, 0.563, 0.841]

for i in range(0, 10, 1):
    if i < 4:
        if y2[i] == 'A':
            p = 2/4
        else:
            p = 1/4
    else:
        if y2[i] == 'A':
            p = 1/6
        elif y2[i] == 'B':
            p = 2/6
        else:
            p = 3/6

    p0 = 4/10
    p1 = 6/10

    pY10 = stats.norm.pdf(y1[i], loc=0.25, scale=0.238)
    pY11 = stats.norm.pdf(y1[i], loc=0.05, scale=0.288)

    pY3Y40 = var0.pdf([y3[i], y4[i]])
    pY3Y41 = var1.pdf([y3[i], y4[i]])

    c0 = p0 * pY3Y40 * pY10 * p
    c1 = p1 * pY3Y41 * pY11 * p

    predicted = 1 if c0 < c1 else 0

    # questao 2
    #print("Para x", i+1, "- C = 0: ", c0, " C = 1: ", c1, " Predicted class: ", predicted)

    som = c0 + c1
    normalized = round(c1/som, 3)

    normalized_c0 = c0 / som
    normalized_c1 = c1 / som

    pred = 1 if normalized_c0 < normalized_c1 else 0
    #print("Para x", i+1, "- C = 0: ", normalized_c0, " C = 1: ", normalized_c1, " Predicted class: ", pred)

    #threshold = 0.7
    #pred = 0 if normalized <= threshold else 1

    # questao 4
    #print("Normalized C1: ", normalized, " Pred: ", pred)

    tt = []
    for u in range(0, 10, 1):  # ou 21 em vez de 10
        #threshold = th[u]
        threshold = norms[u]
        pred = 0 if normalized < threshold else 1
        #print(threshold, normalized, pred)
        tt += [pred]

    #print(tt)
    l += [tt]

true = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]

for i in range(0, 10, 1):  # ou 21 em vez de 10
    to_analyze = []
    for u in range(0, 10, 1):
        to_analyze += [l[u][i]]

    TP, TN, FP, FN = 0, 0, 0, 0
    for j in range(0, 10, 1):
        if to_analyze[j] == 0 and true[j] == 0:
            TN += 1
        if to_analyze[j] == 1 and true[j] == 1:
            TP += 1
        if to_analyze[j] == 0 and true[j] == 1:
            FN += 1
        if to_analyze[j] == 1 and true[j] == 0:
            FP += 1

    FPR = FP / (FP + TN)
    TPR = TP / (TP + FN)

    print("To analyze: ", to_analyze, " FPR: ", FPR, " TPR: ", TPR, " with threshold ", norms[i])
    #print("To analyze: ", to_analyze, " FPR: ", FPR, " TPR: ", TPR, " with threshold ", th[i])
