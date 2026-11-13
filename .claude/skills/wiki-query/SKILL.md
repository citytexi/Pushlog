---
name: wiki-query
description: Use when the user asks a question that should be answered from the Pushlog wiki — "위키 기반으로 답해줘", "위키에서 찾아줘", product-scope questions such as "이거 만들기로 했었나", comparisons, or analysis built on wiki pages.
---

# wiki-query

Answer from `wiki/pages/` without changing pages unless the result is worth saving.

## Steps

1. Run `python3 wiki/script/route.py --intent query` from the repo root and read the `required` files (`wiki/pages/index.md`, `wiki/open-questions.md`). Stop on exit code 3 or 4.
2. Read related pages in evidence order from `wiki/conventions.md` §1: `concepts/`·`entities/` first, then `wiki/open-questions.md` (always), then `sources/` only when needed.
   - Do not cite a superseded source (`🔁` in the index, `status: superseded`) as current.
   - Any open question not marked resolved is still open.
3. Answer with `[[page]]` links as citations. Pick the format the question needs: prose, a comparison table, or slides.
4. If the analysis is reusable, save it under `wiki/pages/synthesis/` (register it in `index.md` and link it from a related concept/entity page), or under `wiki/pages/queries/` for a plain Q&A record. Then append a line to `wiki/log.md` and run `python3 wiki/script/lint.py` to 0 violations.
5. If the wiki cannot answer, say so and name the gap. Do not fill it from general knowledge; offer the research intent instead (`wiki/references/research.md`).
