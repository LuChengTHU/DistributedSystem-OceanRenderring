#! /usr/bin/env python
# -*- coding: utf-8 -*-

import os


if not os.path.exists("input"):
    os.mkdir("input")

with open("input/data", "w") as f:
    lines = []
    for i in range(450):
        for j in range(800):
            lines.append(str(i) + "," + str(j) + "\n")
    f.writelines(lines)
