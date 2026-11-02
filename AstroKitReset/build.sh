#!/usr/bin/env bash
# Builds AstroKitReset.jar against the CURRENT live jars (Essentials, Kits = CosmicGlobalKits, CosmicEvolutionKits).
# usage: bash AstroKitReset/build.sh   -> AstroKitReset/target/AstroKitReset.jar
set -euo pipefail
cd "$(dirname "$0")"
P=/c/Users/levir/AstroPvP-Windows-Test/plugins
LIB=/c/Users/levir/test/.lib
JDK="/c/Program Files/Eclipse Adoptium/jdk-8.0.504.1-hotspot/bin"
rm -rf target && mkdir -p target/classes
CP="$(cygpath -w $LIB/WineSpigot-V1.9.15.jar)"
for j in Essentials.jar Kits.jar CosmicEvolutionKits.jar CosmicPvPUtils.jar; do CP="$CP;$(cygpath -w $P/$j)"; done
MSYS_NO_PATHCONV=1 "$JDK/javac" -nowarn -encoding UTF-8 -source 8 -target 8 -cp "$CP" -d target/classes $(find src -name '*.java')
cp src/plugin.yml target/classes/
(cd target/classes && "$JDK/jar" cf ../AstroKitReset.jar plugin.yml net)
md5sum target/AstroKitReset.jar
