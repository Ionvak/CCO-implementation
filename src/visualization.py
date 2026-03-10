import matplotlib.pyplot as plt
import numpy as np
import math
import json

# Enter the filepath to the "tree_data.txt" file from the source code folder
filepath = 'src/tree_data.json'

# Enter the max value of the linewidth (used for the root radius).
line_width_range = 4

# Open the "tree_data.json" file from the source code and read the exported tree
with open(filepath, 'r') as file:
    tree = json.load(file)["ArterialTree"]

params = tree["params"]
segments = tree["segments"]
num_of_segments = len(segments)
num_of_terminals = (num_of_segments+1)//2

# Find the maximum radius. Used for normalizing the radii values.
max_radius = 0
for s in segments.values():
    if(s["radius"] > max_radius): max_radius = s["radius"]

# Generate a set of points representing the circle representing the
# Perfusion area.
PRECISION = 300
perf_radius = params["perfRadius"]
phi_step = 2 * math.pi / PRECISION
xarea = list()
yarea = list()
for i in range(0, 2 * PRECISION, 2):
    phi = i * phi_step
    xarea.append(perf_radius * math.cos(phi))
    yarea.append(perf_radius * math.sin(phi))
xarea = np.array(xarea)
yarea = np.array(yarea)

# Plot the segments one by one. Adjust the linewidth according to the
# segment radius. Afterwards, Plot the perfusion circle.
#plt.subplot(2,2,i+1)
for s in segments.values():
    plt.plot( [s["proximal"][0], s["distal"][0]],
              [s["proximal"][1], s["distal"][1]],
              'r',
              lw= line_width_range * (s["radius"] / max_radius))
plt.plot(xarea, yarea, '-k')
plt.axis('equal')
plt.title(str(num_of_terminals) + " Terminal Segments")

plt.show()