#!/bin/bash
set -e
cd "$(dirname "$0")"
CLASSPATH="sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar:src/main/java"
echo "Compiling..."
find src/main/java -name "*.java" > sources.txt
javac -cp "$CLASSPATH" -d out @sources.txt
rm sources.txt
echo "Running..."
java -cp "out:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" com.delivery.App
