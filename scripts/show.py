from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import LinearLocator, FormatStrFormatter
import numpy as np
import re

fig = plt.figure()
ax = fig.gca(projection='3d')

X = np.arange(0, 32)
Y = np.arange(0, 32)
X, Y = np.meshgrid(X, Y) 

print(X)
print(Y)

T = 100
for i in range(1, 2):
    with open("frontend/Hdata/frame_" + str(i) + ".txt", 'r') as f:
        lines = f.readlines()
    Z = []
    for line in lines:
        lineNo, hdatas = line.split('\t')
        hdatas = hdatas.split(' ')
        for hdata in hdatas:
            hdata = re.sub(r"\(", "", hdata)
            hdata = re.sub(r"\)", "", hdata)
            #print(hdata)
            real, img = hdata.split(',')
            real = float(real)
            img = float(img)
            #print(real, img)
            Z.append(real**2 + img**2)
    
    print(len(Z))
    Z = np.array(Z).reshape([32,32])

    # Plot the surface.
    surf = ax.plot_surface(X, Y, Z, cmap=cm.coolwarm,
                       linewidth=0, antialiased=False)
    fig.colorbar(surf, shrink=0.5, aspect=5)

    plt.show()

    
'''
# Make data.
X = np.arange(-5, 5, 0.25)
Y = np.arange(-5, 5, 0.25)
X, Y = np.meshgrid(X, Y)
R = np.sqrt(X**2 + Y**2)
Z = np.sin(R)

# Plot the surface.
surf = ax.plot_surface(X, Y, Z, cmap=cm.coolwarm,
                       linewidth=0, antialiased=False)

# Customize the z axis.
ax.set_zlim(-1.01, 1.01)
ax.zaxis.set_major_locator(LinearLocator(10))
ax.zaxis.set_major_formatter(FormatStrFormatter('%.02f'))

# Add a color bar which maps values to colors.
fig.colorbar(surf, shrink=0.5, aspect=5)

plt.show()
'''
