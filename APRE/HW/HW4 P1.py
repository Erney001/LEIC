from math import sqrt
import numpy as np
from scipy.stats import multivariate_normal

x1 = np.array([2, 4])
x2 = np.array([-1, -4])
x3 = np.array([-1, 2])
x4 = np.array([4, 0])

points = [x1, x2, x3, x4]

miu1 = x1
miu2 = x2

e1 = np.array([[1, 0], [0, 1]])
e2 = np.array([[2, 0], [0, 2]])

p1 = 0.7
p2 = 0.3

#last1 = 0
#last2 = 0
#i = 1
#while all(last1 != miu1) and all(last2 != miu2):
    #last1 = miu1
    #last2 = miu2
    #...
    #print(i)
    #i++

l1 = multivariate_normal(miu1, e1)
l2 = multivariate_normal(miu2, e2)

probs = []
for Xi in points:
    pC1 = p1 * l1.pdf(Xi)
    pC2 = p2 * l2.pdf(Xi)

    pC1norm = pC1 / (pC1 + pC2)
    pC2norm = 1 - pC1norm

    print("pC1: ", pC1norm, "pC2: ", pC2norm)
    probs += [[pC1norm, pC2norm]]

soma1 = probs[0][0] + probs[1][0] + probs[2][0] + probs[3][0]
miu1 = np.array((probs[0][0] * x1 + probs[1][0] * x2 + probs[2][0] * x3 + probs[3][0] * x4) / soma1)

e00 = (probs[0][0] * ((x1[0] - miu1[0])**2) + probs[1][0] * ((x2[0] - miu1[0])**2) + probs[2][0] * ((x3[0] - miu1[0])**2) + probs[3][0] * ((x4[0] - miu1[0])**2)) / soma1
e11 = (probs[0][0] * ((x1[1] - miu1[1])**2) + probs[1][0] * ((x2[1] - miu1[1])**2) + probs[2][0] * ((x3[1] - miu1[1])**2) + probs[3][0] * ((x4[1] - miu1[1])**2)) / soma1
e01 = (probs[0][0] * ((x1[0] - miu1[0])*(x1[1] - miu1[1])) + probs[1][0] * ((x2[0] - miu1[0])*(x2[1] - miu1[1])) + probs[2][0] * ((x3[0] - miu1[0])*(x3[1] - miu1[1])) + probs[3][0] * ((x4[0] - miu1[0])*(x4[1] - miu1[1]))) / soma1
e1 = np.array([[e00, e01], [e01, e11]])

# matriz fica diferente
#min_eig = np.min(np.real(np.linalg.eigvals(e1)))
#if min_eig < 0:
    #e1 -= 10*min_eig * np.eye(*e1.shape)

soma2 = probs[0][1] + probs[1][1] + probs[2][1] + probs[3][1]
miu2 = np.array((probs[0][1] * x1 + probs[1][1] * x2 + probs[2][1] * x3 + probs[3][1] * x4) / soma2)

e002 = (probs[0][1] * ((x1[0] - miu2[0])**2) + probs[1][1] * ((x2[0] - miu2[0])**2) + probs[2][1] * ((x3[0] - miu2[0])**2) + probs[3][1] * ((x4[0] - miu2[0])**2)) / soma2
e112 = (probs[0][1] * ((x1[1] - miu2[1])**2) + probs[1][1] * ((x2[1] - miu2[1])**2) + probs[2][1] * ((x3[1] - miu2[1])**2) + probs[3][1] * ((x4[1] - miu2[1])**2)) / soma2
e012 = (probs[0][1] * ((x1[0] - miu2[0])*(x1[1] - miu2[1])) + probs[1][1] * ((x2[0] - miu2[0])*(x2[1] - miu2[1])) + probs[2][1] * ((x3[0] - miu2[0])*(x3[1] - miu2[1])) + probs[3][1] * ((x4[0] - miu2[0])*(x4[1] - miu2[1]))) / soma2
e2 = np.array([[e002, e012], [e012, e112]])

p1 = soma1 / (soma1 + soma2)
p2 = 1 - p1

print("miu1: ", miu1, " miu2: ", miu2)
print("e1: ", e1)
print("e2: ", e2)
print("p1: ", p1, " p2: ", p2)

# com base nas distancias: x1, x3 e x4 ficam no cluster 1
for Xi in points:
    dist_miu1 = sqrt((Xi[0]-miu1[0])**2 + (Xi[1]-miu1[1])**2)
    dist_miu2 = sqrt((Xi[0]-miu2[0])**2 + (Xi[1]-miu2[1])**2)
    #print(dist_miu1, dist_miu2)

# calculo silhueta
a_x1 = (sqrt((x3[0]-x1[0])**2 + (x3[1]-x1[1])**2) + sqrt((x4[0]-x1[0])**2 + (x4[1]-x1[1])**2)) / 2
b_x1 = sqrt((x2[0]-x1[0])**2 + (x2[1]-x1[1])**2)
d1 = 1 - a_x1/b_x1

a_x2 = 0
b_x2 = (sqrt((x1[0]-x2[0])**2 + (x1[1]-x2[1])**2) + sqrt((x3[0]-x2[0])**2 + (x3[1]-x2[1])**2) + sqrt((x4[0]-x2[0])**2 + (x4[1]-x2[1])**2)) / 3
d2 = 1 - a_x2/b_x2

a_x3 = (sqrt((x3[0]-x1[0])**2 + (x3[1]-x1[1])**2) + sqrt((x4[0]-x3[0])**2 + (x4[1]-x3[1])**2)) / 2
b_x3 = sqrt((x2[0]-x3[0])**2 + (x2[1]-x3[1])**2)
d3 = 1 - a_x3/b_x3

a_x4 = (sqrt((x4[0]-x1[0])**2 + (x4[1]-x1[1])**2) + sqrt((x4[0]-x3[0])**2 + (x4[1]-x3[1])**2)) / 2
b_x4 = sqrt((x2[0]-x4[0])**2 + (x2[1]-x4[1])**2)
d4 = 1 - a_x4/b_x4

d_c1 = (d1 + d3 + d4) / 3
d_c2 = d2

#print("x1: ", a_x1, b_x1)
#print("Silhouettes points: ", d1, d2, d3, d4)
#print("Silhouettes: ", d_c1, d_c2)
