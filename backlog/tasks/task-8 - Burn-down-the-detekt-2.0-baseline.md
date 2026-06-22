---
id: TASK-8
title: Burn down the detekt 2.0 baseline
status: Done
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 12:02'
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

MANUAL VERIFICATION (coverage-excluded zone ui/swing/FromTheGroundUpRayTracer.kt + render pipeline): ran 'just run --world=YellowAndRedSphere.kt --tracer=AREA --renderer=SEQUENTIAL --resolution=720p' -> rendered a valid 1280x720 8-bit RGBA PNG showing the yellow + red spheres correctly, confirming the Color.toRgba/toInt named-constant change and the named-arg Sampler change preserve behavior. The Swing file shares the same exception/logging edits (IllegalStateException on unknown command, @Suppress-with-rationale broad catch, png() now logs). Verified default --world=World20.kt no longer exists in the scene set (unrelated, pre-existing rename). FINAL: baseline 119 -> 93 (-26). Categories reduced: TooGenericExceptionThrown 12->0, TooGenericExceptionCaught 1->0, PrintStackTrace 4->0, MaxLineLength 2->0, MagicNumber 50->43 (Color +SampledSingleRayRenderer). Left by design: LongMethod/CyclomaticComplexMethod (TASK-9), NestedBlockDepth (TASK-11), ReturnCount/VariableNaming/FunctionNaming + remaining MagicNumber (math/scene data). just test (clean check incl detekt) green.
<!-- SECTION:NOTES:END -->

## Final Summary

<!-- SECTION:FINAL_SUMMARY:BEGIN -->
Burned the detekt 2.0 baseline down from 119 to 93 entries (-26), every reduction fixed at SOURCE (only removed baseline entries; zero added, no re-baselining). Removed 3 now-stale ParallelRenderer entries already fixed by TASK-5; replaced all generic thrown/caught exceptions (TooGenericExceptionThrown 12 + TooGenericExceptionCaught 1) with typed exceptions carrying descriptive messages (UnsupportedOperationException/IllegalArgumentException/IllegalStateException) across brdf/btdf/AreaLight/RayCast/PlyReader/Transparent/InnerNode, plus a justified narrowly-scoped @Suppress at the Swing UI error boundary (FromTheGroundUpRayTracer render/png); routed printStackTrace (4) through Logger.error with context (AppProperties/Png/Swing); reformatted 2 MaxLineLength lines; bounded MagicNumber pass (7: Color RGBA-packing constants + named Sampler args). Behavior preserved throughout: reviewer verified every extracted Color constant is bit-exact (255, 255.0, shifts 8/16/24, masks unchanged), guarded by the unmodified frozen ColorTest + a manual render; exception swaps fire under the same guard (new types are RuntimeException subclasses so catch(RuntimeException) callers unaffected); printStackTrace->Logger preserves control flow. Cover-first tests added for each throw-type change (BrdfUnsupportedOperationTest, PerfectTransmitterUnsupportedOperationTest, RayCastTest, AreaLightUnsupportedOperationTest, +2 PlyReaderTest cases). No scope creep: LongMethod(8)+CyclomaticComplexMethod(4) left for TASK-9, NestedBlockDepth(3) for TASK-11, ReturnCount(19) guard clauses + VariableNaming(15)/FunctionNaming(1) math notation + 43 MagicNumber (algorithm coefficients / scene data) deliberately left. Verified via just test (clean check + detekt + jacoco) BUILD SUCCESSFUL. Committed as cbaaed3. Note: implementer flagged a pre-existing unrelated issue — the CLI default --world=World20.kt no longer exists in the scene set.
<!-- SECTION:FINAL_SUMMARY:END -->
