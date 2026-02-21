import matplotlib.pyplot as plt
import numpy as np
import re
filepath = 'src/tree_data.txt'

def import_series():
    with open(filepath, 'r') as file:
        xline = file.readline()
        yline = file.readline()
        xseries = re.findall("-?[0-9]+[.][0-9]+", xline)
        yseries = re.findall("-?[0-9]+[.][0-9]+", yline)
        file.close()
    return xseries + yseries

series = np.array(import_series())
xseries = series[:len(series)//2]
x = [float(a) for a in xseries]
yseries = series[len(series)//2:]
y = [float(a) for a in yseries]

print(xseries)
print(yseries)
for n in range(0,len(series)//2,2):
    plt.plot([x[n], x[n+1]], [y[n], y[n+1]], 'r')

plt.xlim([-0.075, 0.075])
plt.ylim([-0.075, 0.075])
plt.show()