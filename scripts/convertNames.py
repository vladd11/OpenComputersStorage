# Input: directory with textures rendered by BlocksRenderer
# Renames all textures to common for app and robot (without brackets and other non-supported by FS symbols).

from os import rename, path
from glob import glob

for i in glob(path.join(input(), '*')):
    rename(i, i.lower().replace(' ', '_'))