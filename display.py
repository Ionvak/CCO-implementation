import sys
import csv
import re
import matplotlib.pyplot as plt

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        
def string_to_point(point_str):
    nums = re.findall(r"[-+]?\d*\.\d+|[-+]?\d+", point_str)
    if len(nums) != 2:
        raise ValueError(f"Invalid point format: {point_str}")
    x = float(nums[0])
    y = float(nums[1])
    point_obj = Point(x, y)
    return point_obj


argv = sys.argv

if(len(argv) != 3): 
    raise Exception("Script usage: python3 display.py TreeFileName OutputFileName ") 

tree_file = argv[1]
output_name = argv[2]

ftree = open(tree_file, 'r')

points = list()

csv_reader = csv.reader(ftree, delimiter=";")
for row in csv_reader:
    for entry in row:
        points.append(string_to_point(entry))
        
xpoints = list()
ypoints = list()
for point in points:
    xpoints.append(point.x)
    ypoints.append(point.y)

print("X coordinates: ")
print(xpoints)
print("Y coordinates: ")
print(ypoints)

plt.plot(xpoints,ypoints)
plt.show()
plt.savefig(output_name,dpi=300,format="png")