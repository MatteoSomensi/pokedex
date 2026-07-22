import os
import shutil

# Directories
base_dir = "/Users/matteosomensi/MatteoProjects/pokedex"
app_dir = os.path.join(base_dir, "app")
app_src_dir = os.path.join(app_dir, "src/main/java/com/example/pokedex")

modules = ["core", "domain", "data", "feature_pokemon_list", "feature_pokemon_detail"]

# Create module directories
for mod in modules:
    os.makedirs(os.path.join(base_dir, mod, "src/main/java/com/example/pokedex", mod.replace("_", "")), exist_ok=True)
    os.makedirs(os.path.join(base_dir, mod, "src/main/res"), exist_ok=True)

# Function to create basic build.gradle.kts
def create_build_gradle(mod_name, dependencies=""):
    content = f"""plugins {{
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt.android)
  alias(libs.plugins.ksp)
}}

android {{
    namespace = "com.example.pokedex.{mod_name.replace('_', '')}"
    compileSdk = 37
    defaultConfig {{
        minSdk = 24
    }}
    compileOptions {{
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }}
    kotlinOptions {{
        jvmTarget = "17"
    }}
    buildFeatures {{
      compose = true
    }}
}}

dependencies {{
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  
  {dependencies}
}}
"""
    with open(os.path.join(base_dir, mod_name, "build.gradle.kts"), "w") as f:
        f.write(content)

# create build.gradle.kts
core_deps = """  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.iconsCore)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
"""
create_build_gradle("core", core_deps)

domain_deps = """  implementation(project(":core"))
"""
create_build_gradle("domain", domain_deps)

data_deps = """  implementation(project(":domain"))
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinx.serialization)
  implementation(libs.okhttp.logging)
  implementation(libs.kotlinx.serialization.json)
"""
# wait, Kotlin serialization plugin needed in data
data_content = f"""plugins {{
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.hilt.android)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}}

android {{
    namespace = "com.example.pokedex.data"
    compileSdk = 37
    defaultConfig {{
        minSdk = 24
    }}
    compileOptions {{
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }}
    kotlinOptions {{
        jvmTarget = "17"
    }}
}}

dependencies {{
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
  {data_deps}
}}
"""
with open(os.path.join(base_dir, "data", "build.gradle.kts"), "w") as f:
    f.write(data_content)

feature_deps = """  implementation(project(":core"))
  implementation(project(":domain"))
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.coil.compose)
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.iconsCore)
  implementation(libs.androidx.compose.ui.tooling.preview)
"""
create_build_gradle("feature_pokemon_list", feature_deps)
create_build_gradle("feature_pokemon_detail", feature_deps)

# Move files
import shutil

# Reset folders if they exist
for m in modules:
    shutil.rmtree(os.path.join(base_dir, m, "src/main/java"), ignore_errors=True)

# create com/example/pokedex in each module
for mod in modules:
    os.makedirs(os.path.join(base_dir, mod, "src/main/java/com/example/pokedex"), exist_ok=True)

# core
shutil.move(os.path.join(app_src_dir, "core"), os.path.join(base_dir, "core/src/main/java/com/example/pokedex/core"))
shutil.move(os.path.join(app_src_dir, "theme"), os.path.join(base_dir, "core/src/main/java/com/example/pokedex/theme"))

# domain
shutil.move(os.path.join(app_src_dir, "domain"), os.path.join(base_dir, "domain/src/main/java/com/example/pokedex/domain"))

# data
shutil.move(os.path.join(app_src_dir, "data"), os.path.join(base_dir, "data/src/main/java/com/example/pokedex/data"))
shutil.move(os.path.join(app_src_dir, "di"), os.path.join(base_dir, "data/src/main/java/com/example/pokedex/di"))

# feature_pokemon_list
os.makedirs(os.path.join(base_dir, "feature_pokemon_list/src/main/java/com/example/pokedex/feature"), exist_ok=True)
shutil.move(os.path.join(app_src_dir, "feature", "pokemonlist"), os.path.join(base_dir, "feature_pokemon_list/src/main/java/com/example/pokedex/feature/pokemonlist"))

# feature_pokemon_detail
os.makedirs(os.path.join(base_dir, "feature_pokemon_detail/src/main/java/com/example/pokedex/feature"), exist_ok=True)
shutil.move(os.path.join(app_src_dir, "feature", "pokemondetail"), os.path.join(base_dir, "feature_pokemon_detail/src/main/java/com/example/pokedex/feature/pokemondetail"))

# App module: keep Navigation, MainActivity, PokedexApp
# update settings.gradle.kts
with open(os.path.join(base_dir, "settings.gradle.kts"), "a") as f:
    f.write('\\ninclude(":core")\\ninclude(":domain")\\ninclude(":data")\\ninclude(":feature_pokemon_list")\\ninclude(":feature_pokemon_detail")\\n')
