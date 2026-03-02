import matplotlib.pyplot as plt
import numpy as np
import re
import math

# Enter the filepath to the "tree_data.txt" file from the source code folder
filepath = '/home/melshekh/Library/Projects/CCO-implementation/src/tree_data.txt'
# Enter the perfusion radius (must be equivalent to the related attribute
# in the parameters of the tree)
perfRadius = 0.05
# Enter the max value of the linewidth (used for the root radius).
line_width_range = 4

# Open the "tree_data.txt" file from the source code and read the exported
# segment attributes. "x" is an array of the x coordinates of all the segment
# points and radii. "y" is an array of the y coordinates of all the segment points.
with open(filepath, 'r') as file:
    xline = file.readline()
    yline = file.readline()
    xseries = re.findall(r"[-]?\d+[.]\d+(?:[eE][+-]\d+)?", xline)
    yseries = re.findall(r"[-]?\d+[.]\d+(?:[eE][+-]\d+)?", yline)
x = [float(a) for a in xseries]
y = [float(a) for a in yseries]

# Find the maximum radius. Used for normalizing the radii values.
max_radius = 0
for n in range(0,len(x),3):
    if x[n+2] > max_radius:
        max_radius = x[n+2]

# Generate a set of points representing the circle representing the
# Perfusion area.
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

# Plot the segments one by one. Adjust the linewidth according to the
# segment radius. Afterwards, Plot the perfusion circle.
for n in range(0,len(x),3):
    plt.plot([x[n], x[n+1]], [y[n], y[n+1]], 'r', lw= line_width_range * (x[n+2] / max_radius))
plt.plot(xarea, yarea, '-')
plt.axis('equal')
plt.show()