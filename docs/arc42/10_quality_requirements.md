# 10. Quality Requirements

This chapter refines the five quality goals of [Chapter 1](01_introduction_and_goals.md) into
concrete, testable scenarios and lists the measures that protect them.

## 10.1 Quality tree

```
Quality
├── Performance efficiency   (goal 1)  -> parallel renderers, acceleration structures
├── Modifiability            (goal 2)  -> strategy interfaces, self-registering scenes, DSL
├── Functional correctness   (goal 3)  -> math value types, K_EPSILON, characterization tests
├── Usability                (goal 4)  -> declarative DSL, helper functions, CLI + GUI
└── Maintainability          (goal 5)  -> Detekt + JaCoCo + Kotest, cover-first rule
```

## 10.2 Quality scenarios

| # | Goal | Scenario | Response measure |
|---|------|----------|------------------|
| **PE-1** | Performance | A scene is rendered with `SEQUENTIAL` and then `FORK_JOIN`/`PARALLEL`/`VIRTUAL` on a multi-core machine | The parallel renderers reduce wall-clock time versus sequential while producing the same image |
| **PE-2** | Performance | A scene with thousands of objects or a dense mesh is rendered | A spatial acceleration structure (Grid/SparseGrid/KDTree) keeps intersection cost sub-linear in object count |
| **PE-3** | Performance | A large PLY model is loaded | Loading respects a sanity bound and fails fast (`PlyLimitExceededException`) rather than exhausting the heap |
| **MO-1** | Modifiability | A new geometric primitive is added | It is implemented by satisfying `IGeometricObject`; no change to tracers or renderers is required |
| **MO-2** | Modifiability | A new example scene is added | Creating one `WorldDefinition` object registers it automatically (ClassGraph); no list is edited |
| **FC-1** | Correctness | A ray grazes a surface tangentially | `K_EPSILON` tolerances prevent self-shadowing and spurious/near-tangent hits |
| **FC-2** | Correctness | A previously working algorithm is refactored | A frozen characterization test pins the prior behaviour and must stay green and unmodified |
| **US-1** | Usability | A newcomer writes a first scene | The `Builder.build { }` DSL plus helper functions (`p()`, `v()`, `c()`) let them describe the scene declaratively, guided by the example scenes |
| **US-2** | Usability | A user requests an invalid option | The CLI fails fast, naming the bad value and listing valid options (e.g. resolution ids) |
| **MA-1** | Maintainability | A change introduces a style/complexity violation or lowers the bar | `./gradlew check` (Detekt + tests) fails in local build and CI before merge |

## 10.3 Quality assurance measures

| Measure | Tool | Gate |
|---------|------|------|
| Unit & characterization tests | Kotest (JUnit 5 platform) | `./gradlew test` / `check` |
| Coverage tracking | JaCoCo (HTML/XML in `build/reports/jacoco`) | runs after `test`; excludes `examples/**`, CLI entry point, Swing UI |
| Static analysis | Detekt (`detekt-config.yml`) | part of `check`; relaxed subset for scene DSLs |
| Continuous integration | GitHub Actions (JDK 25) | `./gradlew build` on every push/PR to `main` |
| Manual render verification | CLI / Swing | for coverage-excluded zones (scenes, CLI glue, GUI) |

## 10.4 Notes on measurement

Coverage and static-analysis figures evolve with the codebase; the **authoritative current
values** are the generated JaCoCo and Detekt reports, not a number transcribed into prose. This
chapter therefore states *which gates exist and what they protect* rather than fixed metric
values. The open risks and accepted debt are tracked in
[Chapter 11](11_risks_and_technical_debt.md) and in the project's Backlog.
