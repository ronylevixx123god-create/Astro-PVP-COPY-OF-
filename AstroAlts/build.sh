#!/usr/bin/env bash
# Builds AstroAlts.jar against the server API + live LuckPerms (API only).
# usage: bash AstroAlts/build.sh   -> AstroAlts/target/AstroAlts.jar
set -euo pipefail
cd "$(dirname "$0")"
LIB=/c/Users/levir/test/.lib
LIVE=/c/Users/levir/AstroPvP-Windows-Test/plugins
JDK="/c/Program Files/Eclipse Adoptium/jdk-8.0.504.1-hotspot/bin"
rm -rf target && mkdir -p target/classes
MSYS_NO_PATHCONV=1 "$JDK/javac" -nowarn -encoding UTF-8 -source 8 -target 8 \
  -cp "$(cygpath -w $LIB/WineSpigot-V1.9.15.jar);$(cygpath -w $LIVE/LuckPerms-Bukkit-5.4.30.jar)" \
  -d target/classes $(find src -name '*.java')
cp resources/plugin.yml resources/config.yml target/classes/
(cd target/classes && "$JDK/jar" cf ../AstroAlts.jar plugin.yml config.yml net)
md5sum target/AstroAlts.jar
