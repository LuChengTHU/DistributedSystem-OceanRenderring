#-*- coding: utf-8 -*- 
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

fig = plt.figure()


frameCount = 100
res = 256
step = 1024*2/res

X = np.arange(-1024, 1024, step) # 256
Y = np.arange(-1024, 1024, step)
X, Y = np.meshgrid(X, Y) 


redCount = 0 
Z = np.zeros((frameCount, res, res), dtype=np.float32)

'''for frame in range(0, frameCount):
	Z[frame] = 100+90*np.sin((X+Y)*3.14159/400+frame/3.0/3)
print(Z[1][0])
'''
'''
while True:
	partName = "frontend/OceanHeight/part-r-%05d"%redCount
	print(partName)
	try:
		partFile = open(partName, "r")
	except:
		break
	if partFile == None: 
		break 
	dataLines = partFile.readlines()
	for data in dataLines:
		dataArr = data.split('\t')
		leftDataArr = dataArr[0].split(' ')
		frameIndex = int(leftDataArr[0])
		lineIndex = int(leftDataArr[1])
		rightDataArr = map(eval, dataArr[1].split(' ')[:-1])
		Z[frameIndex-1][lineIndex] = rightDataArr
	redCount = redCount+1

print(190/np.max(Z))
Z = Z*190/np.max(Z)
print(Z.shape)
'''
if not os.path.exists("demoImg"):
	os.mkdir("demoImg")

for i in range(1, frameCount):
	print(i)
	partName = "frontend/ModelData/%d/part-r-00000"%i
	partFile = open(partName, "r")
	ballLines = partFile.readlines()
	ax = fig.add_subplot(111, projection='3d')
	#ax.plot_surface(X, Y, Z[i], cmap=plt.cm.coolwarm)
	u = []
	v = []
	x = []
	y = []
	z = []
	for tmpball in ballLines:
		ball = tmpball
		if ball.find('\t') >= 0:
			ball = ball[ball.find('\t')+1:]
		ballArr = ball.split(' ') 
		radius = float(ballArr[2])
		center = [float(ballArr[4])-1024, float(ballArr[5])-1024, float(ballArr[6])]
	
		#print(radius)
		#print(center)
	
		u.append(np.linspace(0, 2 * np.pi, 100))
		v.append(np.linspace(0, np.pi, 100))
		x.append(radius * np.outer(np.cos(u[-1]), np.sin(v[-1])) + center[0])
		y.append(radius * np.outer(np.sin(u[-1]), np.sin(v[-1])) + center[1])
		z.append(radius * np.outer(np.ones(np.size(u[-1])), np.cos(v[-1])) + center[2])

		ax.plot_surface(x[-1], y[-1], z[-1])
	ax.set_zlim(0, 1000)
	ax.set_xlim(-1024, 1024)
	ax.set_ylim(-1024, 1024)
	#plt.show()
	fig.set_size_inches(24, 13.5)
	plt.savefig("demoImg/%05d.png"%i)
	fig.delaxes(ax)
