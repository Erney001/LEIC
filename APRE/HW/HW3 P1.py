import numpy as np
from scipy.special import softmax
from math import log2

x0 = [1, 1, 1, 1, 1]

w1 = [[1, 1, 1, 1, 1],
      [0, 0, 0, 0, 0],
      [1, 1, 1, 1, 1]]

b1 = [1, 1, 1]

w2 = [[1, 1, 1],
      [1, 1, 1]]

b2 = [1, 1]

w3 = [[0, 0],
      [0, 0]]

b3 = [0, 0]

t = [1, -1]  # ex 1
#t = [1, 0]  # ex 2

z1 = np.dot(w1, x0) + b1
x1 = np.tanh(z1)
print("z1: ", z1)
print("x1: ", x1)

z2 = np.dot(w2, x1) + b2
x2 = np.tanh(z2)
print("z2: ", z2)
print("x2: ", x2)

z3 = np.dot(w3, x2) + b3
x3 = np.tanh(z3)  # ex 1
#x3 = softmax(z3)  # ex 2
print("z3: ", z3)
print("x3: ", x3)

e = 1/2 * ((x3[0]-t[0])**2 + (x3[1]-t[1])**2)  # ex 1
#e = -1*(t[0]*log2(x3[0]) + t[1]*log2(x3[1]))  # ex 2
print("e: ", e)

temp3 = [1, 1]
d3 = np.multiply(np.subtract(x3, t), np.subtract(temp3, np.tanh(z3)))  # ex 1
#d3 = np.subtract(x3, t)  # ex 2
print("d3: ", d3)

tmp2 = np.dot(np.transpose(w3), d3)
d2 = np.multiply(tmp2, np.subtract(temp3, np.tanh(z2)))
print("d2: ", d2)

temp1 = [1, 1, 1]
tmp1 = np.dot(np.transpose(w2), d2)
d1 = np.multiply(tmp1, np.subtract(temp1, np.tanh(z1)))
print("d1: ", d1)

# w1 fica igual
#new_w1 = np.subtract(w1, 0.1*np.multiply(d1, x0))
new_b1 = np.subtract(b1, 0.1*d1)
#print(new_w1)
print("b1: ", new_b1)

# w2 fica igual
#new_w2 = np.subtract(w2, 0.1*np.multiply(d2, x1))
new_b2 = np.subtract(b2, 0.1*d2)
#print(new_w2)
print("b2: ", new_b2)

new_w3 = np.subtract(w3, 0.1*np.multiply(d3, x2))
new_b3 = np.subtract(b3, 0.1*d3)
print("w3: ", new_w3)
print("b3: ", new_b3)
