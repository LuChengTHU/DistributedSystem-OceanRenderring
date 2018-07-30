#-*- coding: utf-8 -*- 
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

fig = plt.figure()


frameCount = 50
res = 256
step = 1024*2/res

X = np.arange(-1024, 1024, step) # 256
Y = np.arange(-1024, 1024, step)
X, Y = np.meshgrid(X, Y) 

redCount = 0 
Z = np.zeros((frameCount, res, res), dtype=np.float32)
while True:
	partName = "oceanFFT/output/part-r-%05d"%redCount
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
	
print(Z.shape)

if not os.path.exists("demoImg"):
	os.mkdir("demoImg")

for i in range(frameCount):
	ax = fig.add_subplot(111, projection='3d')
	ax.plot_surface(X, Y, Z[i], cmap=plt.get_cmap('coolwarm'))
	ax.set_zlim(0, 1000)
	#plt.show()
	fig.set_size_inches(24, 13.5)
	plt.savefig("demoImg/%05d.png"%i)
