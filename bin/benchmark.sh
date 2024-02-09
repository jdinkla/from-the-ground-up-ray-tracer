#!/usr/bin/env bash

java --version
PAGER= git show --oneline -s
#time ./gradlew run --args="--world=SpheresInABox.kt --renderer=PARALLEL --resolution=2160p"
time ./gradlew run --args="--world=World42.kt --renderer=FORK_JOIN --resolution=2160p"
