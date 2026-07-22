import re

path = "/Users/matteosomensi/MatteoProjects/pokedex/gradle/libs.versions.toml"
with open(path, "r") as f:
    content = f.read()

content = re.sub(r'androidGradlePlugin = "9\.1\.1"', 'androidGradlePlugin = "9.1.0"', content)
content = re.sub(r'kotlin = "2\.3\.11"', 'kotlin = "2.3.10"', content)
content = re.sub(r'ksp = "2\.3\.11"', 'ksp = "2.3.10"', content)
content = re.sub(r'composeAlpha = "1\.12\.0"', 'composeAlpha = "1.12.0-alpha03"', content)
content = re.sub(r'androidxComposeBom = "2026\.4\.0"', 'androidxComposeBom = "2026.03.01"', content)

with open(path, "w") as f:
    f.write(content)
