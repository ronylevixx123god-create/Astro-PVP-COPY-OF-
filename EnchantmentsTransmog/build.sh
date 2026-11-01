#!/usr/bin/env bash
# Class-patches the CURRENT live Enchantments.jar: new CommandTransmog (+ GodlyTransmogListener). Run right before installing.
# usage: bash EnchantmentsTransmog/build.sh   -> EnchantmentsTransmog/target/Enchantments.jar
set -euo pipefail
cd "$(dirname "$0")"
P=/c/Users/levir/AstroPvP-Windows-Test/plugins
LIB=/c/Users/levir/wt-deadcmds/.lib
JDK="/c/Program Files/Eclipse Adoptium/jdk-8.0.504.1-hotspot/bin"
rm -rf target && mkdir -p target/classes
cp $P/Enchantments.jar target/Enchantments.jar
CP="$(cygpath -w $LIB/WineSpigot-V1.9.15.jar);$(cygpath -w target/Enchantments.jar)"
for j in CosmicPvPUtils.jar EnchantmentsAPI.jar; do CP="$CP;$(cygpath -w $P/$j)"; done
MSYS_NO_PATHCONV=1 "$JDK/javac" -nowarn -encoding UTF-8 -source 8 -target 8 -cp "$CP" -d target/classes $(find src -name '*.java')
(cd target/classes && "$JDK/jar" uf ../Enchantments.jar $(find org -name "*.class"))
md5sum $P/Enchantments.jar target/Enchantments.jar
