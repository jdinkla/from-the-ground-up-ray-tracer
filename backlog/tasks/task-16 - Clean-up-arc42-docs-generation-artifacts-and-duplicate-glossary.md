---
id: TASK-16
title: Clean up arc42 docs (generation artifacts and duplicate glossary)
status: To Do
assignee: []
created_date: '2026-06-22 09:12'
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
