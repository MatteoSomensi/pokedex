import os

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"
modules = ["", "core", "domain", "data", "feature_pokemon_list", "feature_pokemon_detail", "app"]

for mod in modules:
    path = os.path.join(base_dir, mod, "build.gradle.kts")
    if os.path.exists(path):
        with open(path, "r") as f:
            lines = f.readlines()
        with open(path, "w") as f:
            for line in lines:
                if "libs.plugins.kotlin.android" not in line:
                    f.write(line)
