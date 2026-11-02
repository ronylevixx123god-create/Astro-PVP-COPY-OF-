#!/usr/bin/env bash
# Builds AstroNickGate.jar (only needs the server API).
# usage: bash AstroNickGate/build.sh   -> AstroNickGate/target/AstroNickGate.jar
set -euo pipefail
cd "$(dirname "$0")"
LIB=/c/Users/levir/test/.lib
JDK="/c/Program Files/Eclipse Adoptium/jdk-8.0.504.1-hotspot/bin"
rm -rf target && mkdir -p target/classes
MSYS_NO_PATHCONV=1 "$JDK/javac" -nowarn -encoding UTF-8 -source 8 -target 8 -cp "$(cygpath -w $LIB/WineSpigot-V1.9.15.jar)" -d target/classes $(find src -name '*.java')
cp src/plugin.yml target/classes/
(cd target/classes && "$JDK/jar" cf ../AstroNickGate.jar plugin.yml net)
md5sum target/AstroNickGate.jar
