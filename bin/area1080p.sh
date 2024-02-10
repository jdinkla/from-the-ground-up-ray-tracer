#!/usr/bin/env bash

time ./gradlew run --args="--world=$1 --tracer=AREA --renderer=PARALLEL --resolution=1080p"
