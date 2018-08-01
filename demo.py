#-*- coding: utf-8 -*- 
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

fig = plt.figure()


frameCount = 30
res = 256
step = 1024*2/res

X = np.arange(-1024, 1024, step) # 256
Y = np.arange(-1024, 1024, step)
X, Y = np.meshgrid(X, Y) 


redCount = 0 
Z = np.zeros((frameCount, res, res), dtype=np.float32)
for frame in range(0, frameCount):
	Z[frame] = 100+90*np.sin((X+Y)*3.14159/400+frame/3.0/3)
print(Z[1][0])
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
'''	

print(Z.shape)

if not os.path.exists("demoImg"):
	os.mkdir("demoImg")

for i in range(1, frameCount):

	partName = "frontend/ModelData/%d/part-r-00000"%i
	partFile = open(partName, "r")
	ball = partFile.readlines()[0]
	if ball.find('\t') >= 0:
		ball = ball[ball.find('\t')+1:]
	ballArr = ball.split(' ') 
	radius = float(ballArr[2])
	center = [float(ballArr[4])-1024, float(ballArr[5])-1024, float(ballArr[6])]
	
	print(radius)
	print(center)
	
	u = np.linspace(0, 2 * np.pi, 100)
	v = np.linspace(0, np.pi, 100)
	x = radius * np.outer(np.cos(u), np.sin(v)) + center[0]
	y = radius * np.outer(np.sin(u), np.sin(v)) + center[1]
	z = radius * np.outer(np.ones(np.size(u)), np.cos(v)) + center[2]

	ax = fig.add_subplot(111, projection='3d')
	ax.plot_surface(x, y, z, color='g')
	ax.plot_surface(X, Y, Z[i], cmap=plt.get_cmap('coolwarm'))
	ax.set_zlim(0, 1000)
	#plt.show()
	fig.set_size_inches(24, 13.5)
	plt.savefig("demoImg/%05d.png"%i)
