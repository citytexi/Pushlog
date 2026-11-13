---
name: wiki-lint
description: Use when the user asks to check the Pushlog wiki's health — "위키 lint 돌려줘", "위키 점검", "위키 정리해줘". Finds sensitive data, contradictions, orphan pages, broken provenance, and stale content. Not for Kotlin/Gradle lint.
---

# wiki-lint

Check the wiki without changing content, then fix only what the rules allow.

## Steps

1. Run `python3 wiki/script/route.py --intent lint` and read the `required` files. Stop on exit code 3 or 4.
2. Run the machine checks. Do not write ad-hoc scripts.
   - `python3 wiki/script/lint.py` — sensitive patterns, frontmatter, links, layout, orphans, raw consistency, provenance, graph signals.
   - `python3 wiki/script/check_status.py` — source edition `status` integrity and index projection.
   Per-code handling is in `wiki/references/lint-rules.md`.
3. Read for what scripts cannot judge:
   - contradictions between pages → register in `wiki/open-questions.md`;
   - resolved open questions whose conclusion is not yet reflected in the related concept/entity pages, or whose `⚠️` markers remain;
   - data gaps worth researching;
   - stale statements contradicted by newer sources.
4. Sensitive data: never auto-fix. Report `file:line` to the user and ask how to handle it. No commit until it is handled.

## Output

Save a report from `wiki/templates/synthesis-lint.md` as `wiki/pages/synthesis/lint-YYYY-MM-DD.md` only when there are findings worth keeping, register it in `index.md`, and append `## [YYYY-MM-DD] lint | 점검 완료, N건 발견` to `wiki/log.md`. If everything is clean, just tell the user.
