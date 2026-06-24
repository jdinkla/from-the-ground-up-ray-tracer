---
id: TASK-44
title: >-
  Audit: exclude intentionally-empty scene templates from the near-black SUSPECT
  list
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-23 21:31'
updated_date: '2026-06-24 09:46'
labels:
  - audit
  - tooling
dependencies: []
priority: low
ordinal: 47000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
Surfaced during TASK-39: the TASK-38 audit flags Template.kt as a near-black SUSPECT, but Template.kt is an intentionally empty scene template — it is black by design, not a defect. The audit's near-black detection should not report templates (and any other by-design-empty scaffolding) as suspects, so the SUSPECT list stays a high-signal list of genuine problems. Decide and implement an exclusion mechanism: e.g. skip scenes whose id/name matches a template convention, or let a scene opt out of the near-black check via Metadata (mirroring the preferredTracer hint added in TASK-39), or maintain a small audit ignore-list. Testable core (the audit's suspect-selection / exclusion predicate) covered cover-first; verify by rerunning ./gradlew audit and confirming Template.kt is no longer flagged while genuine near-black scenes (e.g. MultipleObjects.kt until fixed) still are. Full build incl. detekt stays green.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 The audit no longer reports intentionally-empty templates (Template.kt) as near-black SUSPECTs, via a clear exclusion mechanism (convention, Metadata opt-out, or ignore-list)
- [ ] #2 Genuine near-black scenes are still flagged (the exclusion is scoped to templates/by-design-empty scaffolding, not a blanket mute)
- [ ] #3 Testable core (suspect-selection/exclusion predicate) covered by frozen cover-first tests; verified by rerunning ./gradlew audit; full build incl. detekt green
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Choose Metadata opt-out (mirrors preferredTracer): add intentionallyEmpty: Boolean = false to Metadata + MetadataScope (commonMain) with cover-first tests. 2. Carry the flag into the audit's suspect-selection: add excludedFromNearBlack to SceneAuditResult, populated by SceneAuditor.auditOne from world.metadata.intentionallyEmpty. 3. Filter it out in AuditReport.suspects(). 4. Cover-first frozen tests in AuditReportTest: template excluded, genuine non-template near-black still flagged. 5. Annotate Template.kt (examples, excluded) with metadata { intentionallyEmpty = true }. 6. Verify: ./gradlew audit (Template.kt gone), ./gradlew clean check green.
<!-- SECTION:PLAN:END -->
