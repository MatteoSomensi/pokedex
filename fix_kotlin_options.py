import os

base_dir = "/Users/matteosomensi/MatteoProjects/pok"
modules = ["core", "domain", "data", "feature_pokemon_list", "feature_pokemon_detail"]

for mod in modules:
    path = os.path.join(base_dir, mod, "build.gradle.kts")
    if os.path.exists(path):
        with open(path, "r") as f:
            content = f.read()
            
        content = content.replace('''    kotlinOptions {
        jvmTarget = "17"
    }''', "")
        
        if "kotlin {" not in content:
            content += "\nkotlin {\n    jvmToolchain(17)\n}\n"
            
        with open(path, "w") as f:
            f.write(content)
