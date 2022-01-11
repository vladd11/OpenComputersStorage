from PIL import Image
import glob

# Image.open() can also open other image types
for i in glob.glob("*.png"):
    print(i)
    img = Image.open(i)
    # WIDTH and HEIGHT are integers
    resized_img = img.resize((256, 256))
    resized_img.save(i)