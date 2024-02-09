#!/usr/bin/env bash

time ./gradlew run --args="--world=$1 --renderer=PARALLEL --resolution=1080p"
