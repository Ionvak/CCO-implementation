import matplotlib.pyplot as plt
import numpy as np
import re
import math

filepath = '/src/tree_data.txt'
perfRadius = 0.05

with open(filepath, 'r') as file:
    xline = file.readline()
    yline = file.readline()
    xseries = re.findall(r"[-]?\d+[.]\d+(?:[eE][+-]\d+)?", xline)
    yseries = re.findall(r"[-]?\d+[.]\d+(?:[eE][+-]\d+)?", yline)
x = [float(a) for a in xseries]
y = [float(a) for a in yseries]

PRECISION = 300
phi_step = 2 * math.pi / PRECISION
xarea = list()
yarea = list()
for i in range(0, 2 * PRECISION, 2):
    phi = i * phi_step
    xarea.append(perfRadius * math.cos(phi))
    yarea.append(perfRadius * math.sin(phi))
xarea = np.array(xarea)
yarea = np.array(yarea)

for n in range(0,len(x),2):
    plt.plot([x[n], x[n+1]], [y[n], y[n+1]], 'r')
plt.plot(xarea, yarea, '-')
plt.show()