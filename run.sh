#!/usr/bin/env sh
set -e

cd "$(dirname "$0")"

javac -d out/classes $(find src -name '*.java')
java -cp out/classes model.controller.Main
