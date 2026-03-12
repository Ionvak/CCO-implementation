import matplotlib.pyplot as plt
import numpy as np
import math
import statistics as stats
import json

# Enter the filepath to the "tree_data.json" file from the source code folder
filepath_tree = 'src/tree_data.json'

# Enter the filepath to the "bifurcation_test.json" file from the source code folder
filepath_bif_test = 'src/bifurcation_test.json'

# Enter the max value of the linewidth (used for the root radius).
line_width_range = 4

# Open the "tree_data.json" file from the source code and read the exported tree
with open(filepath_tree, 'r') as file:
    tree = json.load(file)["ArterialTree"]

# Open the "bifurcation_test.json" file from the source code and read the bifurcation level test data
with open(filepath_bif_test, 'r') as file:
    bif_test_data = json.load(file)["Bifurcation Test Data"]

# Initialize variables used for the plotting of the tree
params = tree["params"]
segments = tree["segments"]
num_of_segments = len(segments)
num_of_terminals = (num_of_segments+1)//2

# Find the maximum radius. Used for normalizing the radii values.
max_radius = 0
for s in segments.values():
    if(s["radius"] > max_radius): max_radius = s["radius"]

# Initialize variables used for the plotting the bifurcation level test results
bif_levels = []
diameter_means = []
diameter_sdm = []
for level, radii in bif_test_data.items():
    diameters = [x * 2 * 1000 for x in radii]
    bif_levels.append(int(level))
    diameter_means.append(stats.mean(diameters))
    if(level != '0'):
        diameter_sdm.append(stats.stdev(diameters))
    else:
        diameter_sdm.append(0)

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
plt.figure
for s in segments.values():
    plt.plot( [s["proximal"][0], s["distal"][0]],
              [s["proximal"][1], s["distal"][1]],
              'r',
              lw= line_width_range * (s["radius"] / max_radius))
plt.plot(xarea, yarea, '-k')
plt.axis('equal')
plt.title(str(num_of_terminals) + " Terminal Segments")
plt.show()

# Plot the graph showing the results of the bifurcation level test
plt.figure
plt.title("Dependence of the Segment Diameters" + "\n" + "on the Bifurcation Level")
plt.xlabel("Bifurcation Level")
plt.ylabel("Mean Segment Diameter (mm)")
plt.errorbar(bif_levels, diameter_means, diameter_sdm)
plt.show()