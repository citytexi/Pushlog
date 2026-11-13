#!/usr/bin/env python3
"""자연어 쿼리로 문서를 찾는다 — 구현 문서(스펙·계획·ADR·아키텍처·synthesis)와 위키 페이지.

용법:
    python3 docs/script/search.py "<자연어 쿼리>" [--top N] [--scope all|docs|wiki]

코드 그래프·개념 그래프 대신 쓰는 가벼운 키워드 검색이다. 색인을 미리 만들지 않고
실행할 때마다 frontmatter와 헤딩을 읽는다 — 문서가 바뀌어도 다시 만들 것이 없다.

규약: stdlib 전용. repo 루트 = Path(__file__).resolve().parents[2].
"""
import argparse
import re
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]

DOC_ROOTS = [
    REPO_ROOT / "docs" / "superpowers" / "specs",
    REPO_ROOT / "docs" / "superpowers" / "plans",
    REPO_ROOT / "docs" / "adr",
    REPO_ROOT / "docs" / "architecture",
    REPO_ROOT / "docs" / "synthesis",
]

WIKI_ROOTS = [
    REPO_ROOT / "wiki" / "pages",
]

FRONTMATTER = re.compile(r"^---\n(.*?)\n---", re.S)

# 한 필드에서 같은 토큰이 여러 번 나와도 3회까지만 센다. 상한이 없으면
# 헤딩이 408개인 open-questions.md 같은 메타 문서가 넓이만으로 거의 모든
# 쿼리를 이긴다 — 관련성이 아니라 분량이 이긴다.
FIELD_CAP = 3


def tokenize(s):
    return re.findall(r"[a-z0-9가-힣]+", s.lower())


def _field(block, key):
    r"""frontmatter 에서 key 의 값을 뽑는다. 블록 리스트는 한 줄로 이어 붙인다.

    `\s*` 를 쓰면 개행을 넘어가 두 가지가 깨진다 — 블록 리스트
    (`related_code:` 다음 줄부터 `- item`)는 첫 항목만 잡히고, 값이 빈 필드
    (`related_adr:`)는 **다음 필드의 값을 삼킨다**. 그래서 같은 줄 공백만
    허용하고(`[ \t]*`), 들여쓰기된 이어지는 줄만 값에 포함한다.
    """
    m = re.search(rf"^{key}:[ \t]*(.*(?:\n[ \t]+.*)*)$", block, re.M)
    if not m:
        return ""
    parts = [ln.strip().lstrip("-").strip() for ln in m.group(1).splitlines()]
    return " ".join(p for p in parts if p).strip("[]").strip("\"'")


def _first_h1(text):
    m = re.search(r"^#\s+(.+)$", text, re.M)
    return m.group(1).strip() if m else ""


def parse_doc(md_path, archived):
    text = md_path.read_text(encoding="utf-8", errors="ignore")
    m = FRONTMATTER.search(text)
    block = m.group(1) if m else ""
    doc_id = _field(block, "id") or md_path.stem
    related = " ".join(
        _field(block, k) for k in ("related_code", "related_spec", "related_adr")
    )
    return {
        "id": doc_id,
        # 위키 페이지는 frontmatter에 title이 없다 — 첫 H1을 제목으로 쓴다.
        "title": _field(block, "title") or _first_h1(text),
        "tags": _field(block, "tags"),
        "code": related,
        "headings": " ".join(re.findall(r"^#{1,3}\s+(.+)$", text, re.M)),
        "archived": archived,
        "path": str(md_path),
    }


def score(query, doc):
    q = set(tokenize(query))
    if not q:
        return 0.0
    id_t = tokenize(doc["id"])
    title_t = tokenize(doc["title"])
    tag_t = tokenize(doc["tags"])
    code_t = tokenize(doc["code"])
    head_t = tokenize(doc.get("headings", ""))
    total = 0.0
    for t in q:
        total += (
            4 * min(id_t.count(t), FIELD_CAP)
            + 4 * min(title_t.count(t), FIELD_CAP)
            + 2 * min(tag_t.count(t), FIELD_CAP)
            + 2 * min(code_t.count(t), FIELD_CAP)
            + 1 * min(head_t.count(t), FIELD_CAP)
        )
        if any(t in n for n in id_t):
            total += 1
    return total


def collect(scope="all"):
    roots = {"docs": DOC_ROOTS, "wiki": WIKI_ROOTS}.get(scope, DOC_ROOTS + WIKI_ROOTS)
    docs = []
    for root in roots:
        if not root.is_dir():
            continue
        for md in sorted(root.rglob("*.md")):
            docs.append(parse_doc(md, archived="archive" in md.parts))
    return docs


def search(query, top=8, scope="all"):
    ranked = sorted(((score(query, d), d) for d in collect(scope)), key=lambda x: -x[0])
    return [(sc, d) for sc, d in ranked if sc > 0][:top]


def main():
    ap = argparse.ArgumentParser(description="구현 문서·위키 검색")
    ap.add_argument("query", nargs="+")
    ap.add_argument("--top", type=int, default=8)
    ap.add_argument("--scope", choices=("all", "docs", "wiki"), default="all")
    a = ap.parse_args()
    q = " ".join(a.query)
    res = search(q, a.top, a.scope)
    if not res:
        print(f"매칭 문서 없음: {q}")
        return
    for sc, d in res:
        mark = " [archived]" if d["archived"] else ""
        rel = Path(d["path"]).relative_to(REPO_ROOT)
        print(f"{d['id']}  (score {sc:.0f}){mark} — {d['title'][:80]}\n    {rel}")


if __name__ == "__main__":
    main()
