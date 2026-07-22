#!/bin/bash
set -e

# 1. Create module directories
mkdir -p core/src/main/java/com/example/pokedex/core
mkdir -p core/src/main/res
mkdir -p domain/src/main/java/com/example/pokedex/domain
mkdir -p data/src/main/java/com/example/pokedex/data
mkdir -p feature/pokemon/src/main/java/com/example/pokedex/feature/pokemon
feature/pokemon/src/main/res

# 2. Settings gradle
cat << 'SETTING' >> settings.gradle.kts
include(":core")
include(":domain")
include(":data")
include(":feature:pokemon")
SETTING

# 3. Create basic build.gradle.kts for each module
# We need to figure out what dependencies they need.
# I will write a script to generate them.
