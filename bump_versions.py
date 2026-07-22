import re

path = "/Users/matteosomensi/MatteoProjects/pokedex/gradle/libs.versions.toml"
with open(path, "r") as f:
    lines = f.readlines()

new_lines = []
in_versions = False

for line in lines:
    if line.strip() == "[versions]":
        in_versions = True
        new_lines.append(line)
        continue
    elif line.strip().startswith("["):
        in_versions = False
        
    if in_versions and "=" in line:
        # Bump the minor version number if it matches X.Y.Z
        match = re.search(r'"(\d+)\.(\d+)\.?(\d+)?(-.*)?"', line)
        if match:
            major = match.group(1)
            minor = int(match.group(2))
            patch = match.group(3) if match.group(3) else "0"
            suffix = match.group(4) if match.group(4) else ""
            
            # special case for kotlin/ksp
            if "kotlin =" in line or "ksp =" in line:
                new_version = "2.3.11" # just bump patch to be safe
            elif "androidGradlePlugin" in line:
                new_version = "9.1.1"
            elif "composeBom" in line:
                new_version = "2026.07.00"
            elif "mockk" in line:
                new_version = "1.14.0"
            elif "hilt" in line:
                new_version = "2.61.0"
            elif "-rc" in suffix or "-alpha" in suffix:
                new_version = f"{major}.{minor}.{patch}" # remove suffix to make it stable
            else:
                new_version = f"{major}.{minor + 1}.0"
            
            line = re.sub(r'"(\d+)\.(\d+)\.?(\d+)?(-.*)?"', f'"{new_version}"', line)
            
    new_lines.append(line)

with open(path, "w") as f:
    f.writelines(new_lines)
