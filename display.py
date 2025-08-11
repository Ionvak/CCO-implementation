import sys
import csv
import matplotlib.pyplot as plt

class Point:
    def __init__(self, x, y):
        self.x = x
        self.y = y
        
def string_to_point(point_str):
    point_obj = Point(int(point_str[1]), int(point_str[3]))
    return point_obj

    
argv = sys.argv

if(len(argv) != 3): 
    raise Exception("Script usage: python3 display.py TreeFileName OutputFileName ") 

tree_file = argv[1]
output_name = argv[2]

ftree = open(tree_file, 'r')

points = list()

csv_reader = csv.reader(ftree, delimiter=",")
for row in csv_reader:
    for entry in row:
        points.append(string_to_point(entry))
        
xpoints = list()
ypoints = list()
for point in points:
    xpoints.append(point.x)
    ypoints.append(point.y)

print(xpoints)
print(ypoints)

plt.plot(xpoints,ypoints)
plt.show()
plt.savefig(output_name,dpi=300,format="png")