---
name: git-flow
description: Use when starting, shipping, or releasing work in the Pushlog repo — "이슈 만들고 작업해줘", "워크트리 파서 작업", "develop에 PR 올려줘", "머지해줘", "릴리스해줘", "release 브랜치 만들어줘", "main에 배포". Covers issue, worktree, branch names, PR targets, merge methods, version bump, and tags.
---

# git-flow

Pushlog branches: `develop` collects work, `main` holds released code only.

- Work: issue → worktree from `origin/develop` → PR to `develop` → **merge commit**.
- Release: issue → `release/<versionName>` from `origin/develop` → PR to `main` → **squash merge** → tag on `main` → merge `main` back into `develop`.

Never commit or push directly to `main` or `develop`. Every change reaches them through a PR.

## Quick reference

| Item | Work | Release |
|---|---|---|
| Issue template / label | matching `.github/ISSUE_TEMPLATE/*.yml` | `Deploy.yml` / `배포` |
| Branch | `<type>/<kebab-slug>` | `release/<versionName>` |
| Base | `origin/develop` | `origin/develop` |
| PR target | `develop` | `main` |
| PR title | `<type>: <English summary>` | `release: <versionName>` |
| Merge | `gh pr merge --merge --delete-branch` | `gh pr merge --squash` |
| After merge | close issue, remove worktree | tag `main`, back-merge `main` → `develop`, close issue |

`<type>`: `feat`, `fix`, `refactor`, `docs`, `chore`, `build`, `ci`, `test`.
Commit messages: `<type>: 한국어 설명`. Issue and PR bodies: Korean.

## Work flow

### 1. Issue

Pick the template matching the work (`Feature`, `Bug`, `Refactor`, `Docs`, `Build`, `Deploy`). Use its `title` prefix and `labels`, and write the body with its textarea labels as `### ` headings.

```bash
gh issue create --title "[Docs]: <한국어 제목>" --label "문서화" --body-file <body.md>
```

Keep the issue number `N`.

### 2. Worktree from develop

```bash
git fetch origin
```

Inside Orca (`$ORCA_WORKTREE_ID` or `$ORCA_WORKSPACE_ID` set), create an Orca-managed worktree as a child of the current workspace. The parent link groups it under this workspace in the app and lets an agent launched there report back to this session:

```bash
orca worktree create --name <slug> --base-branch origin/develop \
  --branch <type>/<slug> --issue N --parent-worktree active --setup skip --json
```

If a worktree was already created without a parent, attach it: `orca worktree set --worktree <id> --parent-worktree active --json`.

Outside Orca:

```bash
git worktree add ../Pushlog-worktrees/<slug> -b <type>/<slug> origin/develop
```

Run every later command inside the worktree path. Leave the original checkout and its uncommitted files alone.

### 3. Change and verify

Follow `AGENTS.md`. Before committing, run `./gradlew verifyStarter` and confirm it passes. A failing run blocks the PR.

### 4. Commit, push, PR

```bash
git add <files>
git commit -m "<type>: 한국어 설명"
git push -u origin <type>/<slug>
gh pr create --base develop --head <type>/<slug> \
  --title "<type>: <English summary>" --body-file <pr.md>
```

Always pass `--base develop`. The repo default branch is `main`, so omitting it targets `main`.

The PR body follows `.github/PULL_REQUEST_TEMPLATE.md`: fill `Summary`, tick the checklist items you actually did, and put `Closes #N` under `Related Issue`.

### 5. Merge and clean up

```bash
gh pr checks <PR> --watch          # wait for CI; stop if any check fails
gh pr merge <PR> --merge --delete-branch
gh issue close N --comment "#<PR>로 develop에 반영"
```

`Closes #N` closes issues only on merges into the default branch (`main`), so close the issue yourself.

Remove the worktree after the merge: `orca worktree rm --worktree <id> --json` inside Orca, `git worktree remove <path>` outside.

## Release flow

### 1. Version

Both values live in `gradle/libs.versions.toml` `[versions]`.

- `versionName` = `yyyy.MM.ddvNN`: release date plus the same-day sequence, from `01`. Count existing tags for today: `git tag -l "$(date +%Y.%m.%d)v*"`. None → `v01`; `v01` exists → `v02`.
- `versionCode` = current value + 1.

### 2. Issue

`Deploy.yml` template: title `[Deploy]: <versionName> 배포`, label `배포`, kind `신규 배포`.

### 3. Release branch

Create the worktree as in the work flow with branch `release/<versionName>` from `origin/develop`. Then:

```bash
# edit versionCode and versionName in gradle/libs.versions.toml
./gradlew verifyStarter
git commit -am "chore: 버전 <versionName>로 올림"
git push -u origin release/<versionName>
```

### 4. Check main has nothing develop lacks

```bash
git log --oneline origin/develop..origin/main
```

Allowed: `release: ...` squash commits and `Merge pull request ... release/...` commits. Anything else is a commit that never went through `develop`. Stop and ask the user; the release would drop or conflict with it.

### 5. PR to main and squash merge

List the PRs merged into `develop` since the last release:

```bash
git log --merges --first-parent --format='%s' <lastTag>..origin/develop
```

```bash
gh pr create --base main --head release/<versionName> \
  --title "release: <versionName>" --body-file <pr.md>   # Summary lists the PRs; Closes #<deploy issue>
gh pr checks <PR> --watch
gh pr merge <PR> --squash --subject "release: <versionName> (#<PR>)"
```

Only `--squash` for release PRs. `main` history holds one commit per release.

### 6. Tag main

Tag the squash commit on `main`, not the release branch tip. The squash commit is the only one `main` contains.

```bash
git fetch origin
git tag <versionName> origin/main
git push origin <versionName>
```

### 7. Back-merge main into develop

A squash commit shares no history with `develop`. Without this step the next release PR conflicts on every line touched since this release. This step also brings the version bump into `develop`.

```bash
gh pr create --base develop --head main \
  --title "chore: merge main into develop after <versionName>" \
  --body "<versionName> 릴리스 squash 커밋을 develop 이력에 넣는다."
gh pr merge <PR> --merge
```

Use `--merge` here. Never pass `--delete-branch`: the head is `main`.

Check: `git fetch origin && git log --oneline origin/develop..origin/main` prints nothing.

## Common mistakes

| Mistake | Result | Fix |
|---|---|---|
| `gh pr create` without `--base develop` | PR targets `main` | Always pass `--base` |
| Squash-merging a work PR into `develop` | Issue links and per-commit history lost | `--merge` for `develop` |
| Merge-committing a release PR into `main` | `main` gets every develop commit | `--squash` for `main` |
| Tagging the release branch tip | Tag points at a commit outside `main` | Tag `origin/main` after merge |
| Skipping the back-merge | Next release PR conflicts; `develop` keeps the old version | Step 7 every release |
| Branching from local `develop` | Stale base | `git fetch` then use `origin/develop` |
| Working in the original checkout | Mixes with other branches' uncommitted files | Use the worktree |
