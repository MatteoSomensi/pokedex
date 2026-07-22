import os

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"
modules = ["domain", "data"]

for mod in modules:
    path = os.path.join(base_dir, mod, "build.gradle.kts")
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
            
        content = content.replace('''    buildFeatures {
      compose = true
    }''', "")
            
        with open(path, "w") as f:
            f.write(content)
