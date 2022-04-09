import os


for path, subdirs, files in os.walk(input()):
    for name in files:
        try:
            os.link(os.path.join(path, name), name)
        except FileExistsError:
            pass