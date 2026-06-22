---
id: TASK-16
title: Clean up arc42 docs (generation artifacts and duplicate glossary)
status: In Progress
assignee:
  - '@claude'
created_date: '2026-06-22 09:12'
updated_date: '2026-06-22 16:19'
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
- [x] #2 A single glossary file remains (duplicate removed)
<!-- AC:END -->

## Implementation Plan

<!-- SECTION:PLAN:BEGIN -->
1. Scan all arc42 chapters 01-12 for LLM generation preamble/postamble lines and remove them (plus orphaned blank lines). 2. De-duplicate the H1 in 11_risks_and_technical_debt.md. 3. Consolidate glossary: merge unique terms from 12_glossary.md into comprehensive 12-glossary.md, save as 12_glossary.md, delete 12-glossary.md. Update README.md/chapter links. 4. Fix stale scene ids in top-level README.md (World66.kt->World66b.kt, World42.kt->World74.kt). 5. Run just test; grep for dangling 12-glossary references.
<!-- SECTION:PLAN:END -->

## Implementation Notes

<!-- SECTION:NOTES:BEGIN -->
Removed generation-preamble + duplicate-H1 artifacts from chapters 01, 02, 09, 11 (each had a stray 'Now I have...'/'Let me generate...' line at L3 followed by a duplicated H1; collapsed to one clean H1 + genuine body, untouched). Glossary consolidated: small 12_glossary.md (1KB) was itself a pure generation-postamble stub describing the comprehensive file and contained ZERO actual term entries; comprehensive 12-glossary.md (9.8KB, all ~90 terms) became the surviving 12_glossary.md (verified byte-identical via diff before deleting the hyphen file -> no term lost). No dangling 12-glossary references remain; docs/arc42/README.md already linked to 12_glossary.md (now correct). Fixed top-level README scene ids: World66.kt->World66b.kt (x2, L28-29), World42.kt->World74.kt (L30); all four README example world ids now resolve to real scenes (confirmed via grep override val id in src/examples). SCOPE FLAG: chapters 03,04,05,06,07,08,10 are NOT the same shape as 01/02/09/11 -- each file is ENTIRELY a generation-postamble 'summary of what was created/covers' with no genuine arc42 body beneath the H1 (several reference a stale /Volumes/JD path to a 'real document' that was never written in). Stripping the preamble there would leave a bare H1 / empty chapter; writing real content would be rewriting prose (out of scope). Returning NEEDS-DECISION for these 7 chapters.

AC#2 met: single 12_glossary.md remains, duplicate deleted, no term lost (byte-identical diff verified), no dangling 12-glossary refs. AC#1 PARTIALLY met: artifacts removed from the 4 confirmed chapters (01,02,09,11) incl. ch.11 duplicate H1; the 7 summary-only chapters (03-08,10) still hold preamble lines pending the NEEDS-DECISION above -- left AC#1 unchecked. just test = BUILD SUCCESSFUL (detekt green; 2 pre-existing unchecked-cast warnings unrelated to docs). Diff is docs-only (README.md + docs/arc42/*).
<!-- SECTION:NOTES:END -->
