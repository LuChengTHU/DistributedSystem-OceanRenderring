#! /usr/bin/env python
# -*- coding: utf-8 -*-

import os


if not os.path.exists("input"):
    os.mkdir("input")

with open("input/data", "w") as f:
    lines = []
    for i in range(760):
        for j in range(1280):
            lines.append(str(i) + "," + str(j) + "\n")
    f.writelines(lines)
