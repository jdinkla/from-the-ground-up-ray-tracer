---
id: TASK-16
title: Clean up arc42 docs (generation artifacts and duplicate glossary)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 16:16'
labels:
  - docs
dependencies: []
references:
  - docs/arc42/
priority: low
ordinal: 16000
---

## Description

<!-- SECTION:DESCRIPTION:BEGIN -->
The arc42 chapters contain LLM-generation preamble lines (e.g. "Now I have comprehensive information to generate..." at the top of chapters 09, 10, 11) and chapter 11 has a duplicated H1. There are also two glossary files (12_glossary.md and 12-glossary.md). Remove the preamble artifacts, de-duplicate the H1, and consolidate the glossary into a single file.
<!-- SECTION:DESCRIPTION:END -->

## Acceptance Criteria
<!-- AC:BEGIN -->
- [ ] #1 No generation-preamble or duplicated-heading artifacts remain in the arc42 chapters
- [ ] #2 A single glossary file remains (duplicate removed)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Scan all arc42 chapters 01-12 for LLM generation preamble/postamble lines and remove them (plus orphaned blank lines). 2. De-duplicate the H1 in 11_risks_and_technical_debt.md. 3. Consolidate glossary: merge unique terms from 12_glossary.md into comprehensive 12-glossary.md, save as 12_glossary.md, delete 12-glossary.md. Update README.md/chapter links. 4. Fix stale scene ids in top-level README.md (World66.kt->World66b.kt, World42.kt->World74.kt). 5. Run just test; grep for dangling 12-glossary references.
<!-- SECTION:PLAN:END -->
