import re

path = "/Users/matteosomensi/MatteoProjects/pokedex/gradle/libs.versions.toml"
with open(path, "r") as f:
    content = f.read()

content = re.sub(r'detekt = "1\.24\.0"', 'detekt = "1.23.6"', content)
content = re.sub(r'ktlint = "12\.2\.0"', 'ktlint = "12.1.1"', content)

with open(path, "w") as f:
    f.write(content)
