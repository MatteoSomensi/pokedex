import re

path = "/Users/matteosomensi/MatteoProjects/pokedex/gradle/libs.versions.toml"
with open(path, "r") as f:
    content = f.read()

content = re.sub(r'hilt = "2\.61\.0"', 'hilt = "2.60.1"', content)
content = re.sub(r'hiltNavigationCompose = "2\.61\.0"', 'hiltNavigationCompose = "1.2.0"', content)

with open(path, "w") as f:
    f.write(content)
