# Technical Debt

This file no longer holds a standalone debt list — the figures it used to carry (coverage gaps,
a ~51 %/38 % snapshot, references to long-since-deleted kd-tree builders) drifted out of date and
were actively misleading.

The current state lives in two places:

- **[`docs/arc42/11_risks_and_technical_debt.md`](docs/arc42/11_risks_and_technical_debt.md)** —
  a measured snapshot of risks, resolved debt, and where things stand.
- **The Backlog** (`backlog/`, via the `backlog` CLI) — the source of truth for committed and
  planned debt work. Run `backlog task list --plain` to see open items.

When this pointer and the Backlog disagree, the Backlog wins.
