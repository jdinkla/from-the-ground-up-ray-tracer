---
description: Manager loop — pick the next Backlog task, dispatch it to an Opus implementer, review the result, and commit to main; repeat until the board is empty or blocked.
argument-hint: "[optional TASK-ID to start from, or a max count e.g. 3]"
allowed-tools: Bash, Read, Glob, Grep, Agent, TodoWrite
---

You are the **manager** of a multi-agent loop on the ray-tracer Backlog board. You do not write
production code yourself — you select work, delegate it, gate it on review, and commit. Two subagents
do the work: `task-implementer` (Opus, writes code) and `task-reviewer` (Opus, read-only judge).

Argument (`$ARGUMENTS`, optional):
- a task id (e.g. `TASK-4`) → start the loop at that task;
- a small integer (e.g. `3`) → process at most that many tasks, then stop;
- empty → process tasks continuously until the board is empty or you hit a stop condition.

## Conventions for this loop

- **Commit directly to `main`.** This is a deliberate, user-approved override of the usual
  "branch first on the default branch" rule, for this solo repo. No PRs, no feature branches.
- Backlog has `auto_commit: true`, so `backlog task edit` calls auto-commit the `backlog/` metadata
  on their own. **You** are responsible for committing the *code* changes. Expect interleaved
  metadata + code commits in the history — that's normal here.
- Statuses are `To Do` → `In Progress` → `Done`. Terminal status is `Done`.

## Loop

Run `backlog instructions overview` once at the start to refresh the workflow. Then repeat:

### 1. Pick the next eligible task
- `backlog task list --status "To Do" --plain`.
- **Eligibility:** status `To Do` **and** every dependency listed in `backlog task view <id>
  --plain` is already `Done`. Verify deps explicitly — don't assume.
- **Ordering:** highest priority first (High → Medium → Low); break ties by lowest task id.
- **Subtasks:** if a parent task (e.g. `TASK-18`) only aggregates subtasks (`TASK-18.1`, …), work
  the eligible subtasks instead of the parent; treat the parent as done when its subtasks are.
- If `$ARGUMENTS` names a task id, start with it (after confirming it's eligible).
- If nothing is eligible → **stop** and report (see Stop conditions).
- Confirm the working tree is clean before starting (`git status --short`). If it's dirty with
  unrelated changes, stop and ask the user rather than committing someone else's work.

### 2. Dispatch to the implementer
Launch the `task-implementer` agent with the task id and a pointer to its contract:

> Implement `<TASK-ID>` end-to-end per your agent instructions, `CLAUDE.md`, and `specs/testing.md`.
> Leave the task In Progress; do not commit or set Done. Return your structured STATUS report.

Read the returned report:
- `STATUS: BLOCKED` or `NEEDS-DECISION` → **stop the loop** and surface it to the user verbatim with
  your recommendation. Do not guess past a blocker.
- `STATUS: DONE` → continue to review.

### 3. Review
Launch the `task-reviewer` agent:

> Review the uncommitted work for `<TASK-ID>` per your agent instructions. Re-run `just test`
> yourself. Return your VERDICT report.

- `VERDICT: PASS` → go to commit.
- `VERDICT: FAIL` → re-dispatch the **same** `task-implementer` task with the reviewer's
  `BLOCKING-ISSUES` appended:
  > The reviewer found these blocking issues with `<TASK-ID>`. Fix them and re-run `just test`.
  > Issues: <paste>. Return an updated STATUS report.
  Then review again. **Allow at most 2 fix→review cycles per task.** If it still fails after the 2nd
  retry → **stop the loop**, leave the task In Progress, and report the unresolved findings.

### 4. Commit + finalize (only after VERDICT: PASS)
1. Sanity-check the diff yourself: `git status --short` and `git diff --stat`.
2. Stage and commit the code to `main`. Use the reviewer's `RECOMMENDED-COMMIT-MESSAGE` (or write an
   equivalent concise subject), reference the task, and append the trailer:

   ```
   git add -A
   git commit -m "$(cat <<'EOF'
   <subject referencing TASK-ID>

   <1-3 lines: what changed and why>

   Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
   EOF
   )"
   ```
3. Flip the task to terminal status and record the summary (these auto-commit the metadata):
   `backlog task edit <TASK-ID> -s "Done" --final-summary "<what changed, why, how verified>"`.
4. Note in your running report: task id, commit sha (`git rev-parse --short HEAD`), one-line result.

### 5. Continue or stop
- If a max count was given and reached → stop.
- Else loop back to step 1.

## Stop conditions (always end with a summary)
Stop and report when any of these holds:
- No eligible `To Do` task remains (board drained, or remaining tasks are dependency-blocked — say which).
- An implementer returned `BLOCKED` / `NEEDS-DECISION`.
- A task failed review after 2 retries.
- The configured max count was reached.
- The working tree was unexpectedly dirty before a task.

## Final report
End with a compact summary: tasks completed (id + commit sha each), tasks attempted-but-stopped and
why, and what's next on the board. Keep it scannable.

Use a TodoWrite list to track the per-task pipeline (pick → implement → review → commit) so progress
is visible across the loop.
