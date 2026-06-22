---
id: TASK-8
title: Burn down the detekt 2.0 baseline
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:58'
labels:
  - quality
  - tooling
dependencies: []
references:
  - docs/arc42/11_risks_and_technical_debt.md
  - detekt-baseline.xml
priority: medium
ordinal: 8000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The detekt 2.0 upgrade parked existing findings in detekt-baseline.xml (125 entries) so the build stays green while new issues still fail. This replaces the old build>maxIssues=150 slack. Incrementally fix the baselined findings (MagicNumber, TooGenericExceptionCaught/Thrown, PrintStackTrace, etc.) and shrink the baseline toward empty; some overlap with the refactor tasks.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [x] #1 detekt-baseline.xml entry count reduced (target: trending to zero)
- [x] #2 No new findings are added to the baseline; new issues are fixed at source
<!-- AC:END -->



## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Establish detekt-green baseline (119 entries). 2. STEP 1: Remove now-stale entries (probe by deletion + detekt run): ParallelRenderer PrintStackTrace x2, TooGenericExceptionThrown ParallelRenderer; restore any that still fire. 3. STEP 2: Fix at source + delete baseline entries: TooGenericExceptionThrown (12) and TooGenericExceptionCaught (1) -> typed exceptions (IllegalArgumentException/IllegalStateException/require/check); PrintStackTrace (4) -> Logger.error/warn; MaxLineLength (2) -> reformat. Cover-first: add/confirm tests pinning throw type where reachable. 4. STEP 3 (bounded): MagicNumber in production code (commonMain/jvmMain) where named constant is clearly correct + behavior-identical; representative safe subset only; skip examples/**. 5. STEP 4: Leave LongMethod/CyclomaticComplexMethod (TASK-9), NestedBlockDepth (TASK-11), ReturnCount (readability), VariableNaming/FunctionNaming (domain math names). 6. just test green; report before->after.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
STEP 1 (stale entries): Removed 3 now-stale entries that TASK-5 already fixed at source in ParallelRenderer.kt (PrintStackTrace x2 -> Logger.warn; TooGenericExceptionThrown -> IllegalArgumentException). detekt stays green. Polynomials.kt has no exception entries in current baseline (TASK-5 already removed). Baseline 119 -> 116.

STEP 2 (fix at source): TooGenericExceptionThrown (12) + TooGenericExceptionCaught (1) -> typed exceptions: BRDF/BTDF/AreaLight/RayCast unsupported-op markers -> UnsupportedOperationException; PlyReader parse errors -> IllegalArgumentException; Swing 'Unknown Command' -> IllegalStateException; Swing UI broad catch kept (behavior-preserving at event-loop boundary) with @Suppress(TooGenericExceptionCaught) + rationale, and png() now logs via Logger.error too. PrintStackTrace (2 remaining after stale removal): AppProperties.kt + Png.kt -> Logger.error with context. MaxLineLength (2): InnerNode.toString + Transparent.hashCode reformatted. Introduced-then-fixed 2 new MaxLineLength in AreaLight (longer exception name) by extracting NEEDS_AREA_LIGHTING const. Cover-first: added characterization tests pinning new throw types (BrdfUnsupportedOperationTest, PerfectTransmitterUnsupportedOperationTest, RayCastTest, AreaLightUnsupportedOperationTest, PlyReader malformed-input cases). Baseline 116 -> 100.

STEP 3 (MagicNumber, bounded): Fixed 7 entries at source: Color.kt (5: 255/255.0/8/16/24) -> named companion constants MAX_CHANNEL/MAX_CHANNEL_DOUBLE/SHIFT_BYTE_1..3 (covered by ColorTest, green); SampledSingleRayRenderer.kt (2: 2500/10) -> named arguments Sampler(numSamples=2500, numSets=10) (behavior-identical, exempt via ignoreNamedArgument). STOPPED here per bounded mandate: remaining 43 MagicNumber are algorithm/math coefficients (Polynomials, Torus, Sampler concentric-disk, EnvironmentLight, Matrix, Axis, AffineTransformation), scene-default/enum data (Resolution enum heights, Camera default eye, WorldScope, BeveledBox 90deg, examples-like), or in files owned by TASK-9 (Grid)/kd-tree builders -- naming them would be noise or overlap. STEP 4 (left baselined, by design): LongMethod (8)/CyclomaticComplexMethod (4)=TASK-9; NestedBlockDepth (3)=TASK-11; ReturnCount (19)=guard-clause readability; VariableNaming (15)/FunctionNaming (1)=domain math notation (L/S/T, cos_phi, fun G). Baseline 100 -> 93. Net 119 -> 93 (26 removed).
<!-- SECTION:NOTES:END -->
