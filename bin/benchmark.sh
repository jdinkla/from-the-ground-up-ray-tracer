#!/usr/bin/env bash

java --version
PAGER= git show --oneline -s
time ./gradlew run --args="--world=SpheresInABox.kt --renderer=FORK_JOIN --resolution=2160p"
