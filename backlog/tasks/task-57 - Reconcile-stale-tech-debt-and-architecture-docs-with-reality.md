---
id: TASK-57
title: Reconcile stale tech-debt and architecture docs with reality
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-24 22:35'
updated_date: '2026-06-26 21:33'
labels:
  - tech-debt
  - documentation
dependencies:
  - TASK-58
  - TASK-59
  - TASK-60
  - TASK-61
  - TASK-62
  - TASK-63
  - TASK-64
references:
  - TECH_DEBT_REPORT.md
priority: high
ordinal: 60000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
TECH_DEBT.md, docs/arc42/11_risks_and_technical_debt.md, and gemini.md describe a codebase that no longer exists. They report ~51%/38% coverage (actual 90.5%/86.6% per JaCoCo), '70+ !! operators' (actual 1), Grid/SparseGrid hit() duplication and complexity as open (resolved via GridTraversal.kt / TASK-2/TASK-3), and flag 'unpinned dependency versions / no versions.toml' as a risk when that is the deliberate, working refreshVersions mechanism (versions.properties + _ placeholders). gemini.md still calls this a 'Kotlin multi-platform project' (CLAUDE.md already notes that is outdated).

Anyone onboarding via these docs is steered toward redundant or wrong work and loses trust in the whole doc set. This is the single highest-leverage clarity fix: low risk, high gain. See TECH_DEBT_REPORT.md section P1 #1.

Locations: TECH_DEBT.md; docs/arc42/11_risks_and_technical_debt.md (sections 11.1-11.6); gemini.md.

Do NOT add a fourth tech-debt doc: fold TECH_DEBT_REPORT.md into the consolidation so it does not remain as a permanent extra document.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 docs/arc42/11_risks_and_technical_debt.md is rewritten to the current measured state: coverage figures, !! count, and the Grid/SparseGrid and ParallelRenderer items reflect their resolved status
- [ ] #2 TECH_DEBT.md is reduced to a pointer at the Backlog (the real source of truth) or removed entirely
- [ ] #3 gemini.md is refreshed (no longer describes a multi-platform project) or removed
- [ ] #4 The false-positive 'unpinned deps / no versions.toml' risk is corrected to describe refreshVersions as the deliberate mechanism
- [ ] #5 No new duplicate tech-debt document is left behind; TECH_DEBT_REPORT.md content is folded into the consolidated docs rather than kept as a standalone fourth doc
- [ ] #6 Build stays green (./gradlew clean check)
- [ ] #7 When TECH_DEBT_REPORT.md is removed or folded in, the --ref TECH_DEBT_REPORT.md links on TASK-58 through TASK-64 are either re-pointed to the surviving consolidated doc/anchor or dropped, so none dangle
<!-- AC:END -->
