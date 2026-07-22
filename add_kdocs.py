import os
import re

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            if "/**" not in content and ("class " in content or "interface " in content or "object " in content):
                match = re.search(r'^((?:open |sealed |data |abstract )?(?:class|interface|object) \w+)', content, re.MULTILINE)
                if match:
                    decl_type = match.group(1).split()[-2] if len(match.group(1).split()) > 1 else match.group(1).split()[0]
                    kdoc = f"\n/**\n * This {decl_type} is responsible for {file.replace('.kt', '')} logic.\n * Part of the Clean Architecture structure.\n */\n"
                    
                    insertion_point = match.start()
                    new_content = content[:insertion_point] + kdoc + content[insertion_point:]
                    with open(path, "w") as f:
                        f.write(new_content)
