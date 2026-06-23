# justfile for from-the-ground-up-ray-tracer — common workflow shortcuts
# Run `just --list` to see all targets.
# Render example: just run "--world=AreaShadedSpheres.kt --tracer=AREA --resolution=1080p"

# --- Dev ---

[group('dev')]
[doc("Show available commands")]
default:
    @just --list --unsorted

[group('dev')]
[doc("Show available commands (alias for default)")]
help:
    @just --list

[group('dev')]
[doc("Compile, test and run detekt — the full check (what CI runs)")]
build:
    ./gradlew build

[group('dev')]
[doc("Clean, then run the full check (tests + detekt)")]
test:
    ./gradlew clean check

[group('dev')]
[doc('Run unit tests only; pass --tests "<FQN>" for a single class')]
unit *ARGS:
    ./gradlew test {{ ARGS }}

[group('dev')]
[doc("Run detekt static analysis")]
lint:
    ./gradlew detekt

[group('dev')]
[doc("Generate the JaCoCo coverage report (HTML in build/reports/jacoco)")]
coverage:
    ./gradlew jacocoTestReport

[group('dev')]
[doc("Audit example scenes: class coverage, multiplicity, suspect renders (build/reports/scene-audit.md)")]
audit:
    ./gradlew audit

[group('dev')]
[doc("Remove build outputs")]
clean:
    ./gradlew clean

[group('dev')]
[doc("Bump dependency versions (see versions.properties)")]
refresh-versions:
    ./gradlew refreshVersions

# --- Run ---

[group('run')]
[doc("Launch the interactive Swing GUI")]
swing:
    ./gradlew swing

[group('run')]
[doc('Render a scene, e.g. just run "--world=AreaShadedSpheres.kt --tracer=AREA"')]
run *ARGS:
    ./gradlew run --args="{{ ARGS }}"
