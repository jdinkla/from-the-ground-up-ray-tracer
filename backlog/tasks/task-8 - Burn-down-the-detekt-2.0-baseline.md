---
id: TASK-8
title: Burn down the detekt 2.0 baseline
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:11'
updated_date: '2026-06-22 11:48'
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
- [ ] #1 detekt-baseline.xml entry count reduced (target: trending to zero)
- [ ] #2 No new findings are added to the baseline; new issues are fixed at source
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Establish detekt-green baseline (119 entries). 2. STEP 1: Remove now-stale entries (probe by deletion + detekt run): ParallelRenderer PrintStackTrace x2, TooGenericExceptionThrown ParallelRenderer; restore any that still fire. 3. STEP 2: Fix at source + delete baseline entries: TooGenericExceptionThrown (12) and TooGenericExceptionCaught (1) -> typed exceptions (IllegalArgumentException/IllegalStateException/require/check); PrintStackTrace (4) -> Logger.error/warn; MaxLineLength (2) -> reformat. Cover-first: add/confirm tests pinning throw type where reachable. 4. STEP 3 (bounded): MagicNumber in production code (commonMain/jvmMain) where named constant is clearly correct + behavior-identical; representative safe subset only; skip examples/**. 5. STEP 4: Leave LongMethod/CyclomaticComplexMethod (TASK-9), NestedBlockDepth (TASK-11), ReturnCount (readability), VariableNaming/FunctionNaming (domain math names). 6. just test green; report before->after.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
STEP 1 (stale entries): Removed 3 now-stale entries that TASK-5 already fixed at source in ParallelRenderer.kt (PrintStackTrace x2 -> Logger.warn; TooGenericExceptionThrown -> IllegalArgumentException). detekt stays green. Polynomials.kt has no exception entries in current baseline (TASK-5 already removed). Baseline 119 -> 116.
<!-- SECTION:NOTES:END -->
