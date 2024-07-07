#!/bin/bash

OUTPUT_DIR="./build/distributions/"
echo "$PWD"

./gradlew clean

rm -rf ~/.m2
./gradlew :AutoBuilder.Processor:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoBuilder.Processor/build/distributions/* $OUTPUT_DIR

rm -rf ~/.m2
./gradlew :AutoBuilder.Annotations:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoBuilder.Annotations/build/distributions/* $OUTPUT_DIR


