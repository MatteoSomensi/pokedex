import os

path = "/Users/matteosomensi/MatteoProjects/pokedex/build.gradle.kts"
with open(path, "r") as f:
    content = f.read()

if "id(\"com.github.ben-manes.versions\")" not in content:
    content = content.replace("plugins {", "plugins {\n  id(\"com.github.ben-manes.versions\") version \"0.51.0\"")
    with open(path, "w") as f:
        f.write(content)
