---
name: wiki-ingest
description: Use when the user asks to ingest a source into the Pushlog wiki — "이 파일 ingest해줘", "위키에 넣어줘", "소스 추가", or drops a file/text under wiki/raw/. Handles one source interactively or several in batch ("배치로", "한꺼번에").
---

# wiki-ingest

Absorb `wiki/raw/` sources into `wiki/pages/`. The data contract is `wiki/conventions.md` and the procedure is `wiki/references/ingest-checklist.md`. This skill only fixes the order and the gates; do not restate their rules here.

## Steps

1. Route first. Run `python3 wiki/script/route.py --intent ingest` from the repo root and read every `required` file. Stop on exit code 3 or 4 (see `wiki/CLAUDE.md`).
2. If the source is not in `wiki/raw/` yet, save it there first. `wiki/raw/` is otherwise read-only: never edit or delete existing raw files.
3. Run `python3 wiki/script/ingest_cache.py --list`. Nothing listed means nothing to ingest — stop.
4. For each source, one at a time, follow `ingest-checklist.md`:
   - **Analyze before writing.** Write down entity/concept candidates, edition relations (`status`, `superseded_by`, `scope`), contradictions with existing pages, open questions it resolves, and name collisions. Single mode: discuss this with the user. Batch mode: record it and skip the discussion, never the analysis.
   - **Create in fixed order:** `sources/src-<stem>.md` → `entities/` → `concepts/` → (if any) `synthesis/` → `overview.md` → `index.md` → `wiki/log.md`.
   - Register contradictions in `wiki/open-questions.md` and mark the page per `conventions.md` §8.
   - Run `python3 wiki/script/ingest_cache.py --record <raw path>` right after finishing that source.
5. Run `python3 wiki/script/lint.py` and fix until there are 0 violations. Ingest is not done before that.

## Report

Single mode: say which pages were created/updated and which open questions were added or resolved. Batch mode: one summary at the end — "N sources, M pages created/updated, K contradictions". Ask mid-way only when a judgment is genuinely the user's.

## Rules

- Internal links use `[[page-name]]` only.
- Wiki pages never link to implementation docs (`docs/`, code). Dependency runs implementation → wiki only.
- Write page content in Korean.
