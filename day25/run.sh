#!/bin/sh

set -e

libs=../libs/guava-33.0.0-jre.jar

javac -cp $libs Main.java
java -cp $libs:. Main

