import os

base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            if "import com.example.pokedex.R" in content:
                content = content.replace("import com.example.pokedex.R", "import com.example.pokedex.core.R")
                with open(path, "w") as f:
                    f.write(content)
