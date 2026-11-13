from conftest import page

import check_status
import lint


def test_run_all_has_no_violations_for_clean_repo(make_repo):
    root = make_repo({
        "wiki/pages/index.md": page(body="[[purpose]] [[overview]] [[src-정책]]"),
        "wiki/pages/purpose.md": page(),
        "wiki/pages/overview.md": page(),
        "wiki/pages/sources/src-정책.md": page(sources="[정책.md]"),
        "wiki/raw/정책.md": "원본",
    })
    found = lint.run_all(root)
    assert [f for f in found if f.level == "violation"] == []
    # 구조 파일은 인바운드 1건이 정상이라 약연결에서 면제된다.
    # 콘텐츠 페이지는 면제되지 않으므로 src-정책만 경고로 남는다.
    assert {f.path for f in found if f.code == "약연결"} == {
        "wiki/pages/sources/src-정책.md"}


def test_run_all_collects_violations_and_warnings(make_repo):
    root = make_repo({
        "wiki/pages/concepts/외톨이.md": page(
            sources="[]", body="someone@example.com"),
    })
    found = lint.run_all(root)
    levels = {f.level for f in found}
    assert "violation" in levels and "warning" in levels


def test_main_exit_code_reflects_violations_only(make_repo, monkeypatch, capsys):
    root = make_repo({
        "wiki/conventions.md": "계약",
        "wiki/pages/index.md": page(body="[[purpose]] [[overview]] someone@example.com"),
        "wiki/pages/purpose.md": page(),
        "wiki/pages/overview.md": page(),
    })
    monkeypatch.chdir(root)
    code = lint.main()
    out = capsys.readouterr().out
    assert code == 0            # 경고만 있으면 통과
    assert "위반 0건" in out
    assert "경고 1건" in out


def test_lint_main_exits_2_outside_repo_root(make_repo, monkeypatch, capsys):
    """루트가 아니면 '검사 대상 0건'을 통과로 오인하지 않고 2로 죽는다."""
    root = make_repo({
        "wiki/conventions.md": "계약",
        "wiki/pages/concepts/외톨이.md": page(sources="[]"),
    })
    monkeypatch.chdir(root / "wiki" / "pages")
    code = lint.main()
    err = capsys.readouterr().err
    assert code == 2
    assert "저장소 루트" in err


def test_lint_main_does_not_shell_out_to_check_status():
    import inspect
    import lint
    src = inspect.getsource(lint.main)
    assert "subprocess" not in src, "check_status는 import해서 직접 부른다"


def test_lint_reports_status_violations_inline(make_repo, monkeypatch, capsys):
    root = make_repo({
        "wiki/conventions.md": "계약",
        "wiki/pages/index.md": page(body="[[purpose]] [[overview]] [[src-x]]"),
        "wiki/pages/purpose.md": page(),
        "wiki/pages/overview.md": page(),
        # status 누락 = check_status 위반
        "wiki/pages/sources/src-x.md": page(sources="[x.md]"),
        "wiki/raw/x.md": "원본",
    })
    monkeypatch.chdir(root)
    code = lint.main()
    out = capsys.readouterr().out
    assert code == 1
    assert "status 누락" in out
