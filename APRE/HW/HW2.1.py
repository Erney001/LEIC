import numpy as np
from math import sqrt

t = np.array([[1],
             [3],
             [2],
              [0],
              [6],
              [4],
              [5],
              [7]])

a = np.array([[1, sqrt(2), 2, 2*sqrt(2)],
             [1, sqrt(27), 27, 81*sqrt(3)],
             [1, sqrt(20), 20, 40*sqrt(5)],
              [1, sqrt(14), 14, 14*sqrt(14)],
              [1, sqrt(53), 53, 53*sqrt(53)],
              [1, sqrt(3), 3, 3*sqrt(3)],
              [1, sqrt(8), 8, 16*sqrt(2)],
              [1, sqrt(85), 85, 85*sqrt(85)]])

b = np.matmul(a.transpose(), a)
c = np.linalg.inv(b)
d = np.matmul(c, a.transpose())
e = np.matmul(d, t)

print(e)
