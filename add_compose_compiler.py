import os

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"
modules = ["core", "feature_pokemon_list", "feature_pokemon_detail"]

for mod in modules:
    path = os.path.join(base_dir, mod, "build.gradle.kts")
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
            
        if "alias(libs.plugins.compose.compiler)" not in content:
            content = content.replace("plugins {", "plugins {\n  alias(libs.plugins.compose.compiler)")
            
        with open(path, "w") as f:
            f.write(content)
