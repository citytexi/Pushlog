# 디자인 시스템 foundation 설계

- 작성일: 2026-11-13
- 상태: 승인됨 (2026-11-13)
- 이슈: #18 (상위 #17)
- 범위: `core:designsystem`의 foundation(색, 글자, 간격, 모서리, 그림자, 모션)과 `PushlogTheme`. 컴포넌트(#19~#22)와 패턴(#23)은 이 문서 범위 밖이다.

## 1. 의도

Pushlog의 모든 화면이 올라갈 디자인 시스템을 당근 SEED Design 체계로 세운다. foundation → 컴포넌트 → 패턴 순서로 만들고, 이 문서는 첫 단계인 foundation을 다룬다.

- SEED를 참고하는 것은 토큰 이름 체계만이 아니라 foundation·컴포넌트·패턴 전체 구조다. 이 문서의 토큰이 이후 컴포넌트·패턴의 유일한 값 출처가 된다.
- 브랜드 색은 초록 계열로 한다. 초록은 안정감을 주고, "알림이 안전하게 보관된다"는 Pushlog의 약속([`privacy-promise`](../../../wiki/pages/concepts/privacy-promise.md))을 상징한다.
- 경쟁 앱 3개와 브랜드 색이 겹치지 않아야 한다. Play 스토어 아이콘 기준(2026-11-13): NotiFold는 파랑 바탕에 민트·청록 점, Notisave는 보라~파랑, Notistory는 분홍·코랄. 그래서 청록에 가까운 SEED green(에메랄드, `#079171`)은 브랜드로 쓰지 않는다.
- 폰트는 Pretendard를 쓴다.

## 2. 목표 · 비목표 · 반목표

목표

- SEED rootage 구조를 따른 토큰: 팔레트, 용도별 색(`bg`/`fg`/`stroke`, 눌림 상태 포함, 라이트/다크), 글자(`t1`~`t14` × regular/medium/bold, `screenTitle`/`articleBody`/`articleNote`), 간격(`x0_5`~`x16`과 용도별 간격), 모서리(`r0_5`~`r6`, `full`), 그림자(`s1`~`s3`), 모션(`d1`~`d6`, easing).
- 브랜드 색 포레스트 그린 `#20874A`와 이를 중심으로 한 `forest` 단계.
- `PushlogTheme`이 토큰을 제공하고, Material3 `MaterialTheme`에도 같은 값을 연결한다.
- 토큰을 눈으로 확인하는 카탈로그 스크린샷 테스트(라이트/다크).
- 화면 코드가 토큰 대신 값이나 Material3 테마를 직접 쓰지 못하게 하는 Konsist 규칙.

비목표

- 컴포넌트(#19~#22), 패턴(#23).
- 테마 선택(라이트/다크/시스템) 저장과 설정 화면. `PushlogTheme`은 `darkTheme` 인자만 받는다.
- SEED의 `-static` 글자 크기(시스템 글자 크기 설정을 무시하는 크기), viewport-width 모드, 그라데이션, 모션 reduced 모드. 컴포넌트 작업에서 필요해지면 그때 더한다.
- Material You 동적 색(dynamic color). 브랜드 색을 지켜야 한다.
- SEED YAML에서 Kotlin을 자동 생성하는 도구.
- 앱 아이콘 교체.

반목표 (기술적으로 돌아가도 실패로 본다)

- 브랜드 채운 버튼 위 흰 글자 대비가 4.5:1 미만인 것(라이트·다크 모두).
- 다크 모드에서 브랜드 버튼이 밝은 초록 + 검은 글자로 바뀌는 것. 두 모드 모두 "초록 바탕 + 흰 글자"다.
- 화면이 SEED와 같은 당근 주황으로 보이는 것. carrot 단계는 브랜드에 연결하지 않는다.
- 화면 코드가 `Color(0x…)`나 `MaterialTheme.colorScheme`/`MaterialTheme.typography`를 직접 써서 토큰을 우회하는 것.

## 3. 제약

- `core:designsystem`은 `core:ui`, `core:navigation`, `core:common`만 의존할 수 있다(`ModuleRules`).
- minSdk 26, Compose BOM `2026.08.00`.
- 비밀값 없음. 폰트 파일은 저장소에 커밋한다(Pretendard는 SIL OFL 1.1, 재배포 허용·라이선스 동봉 필요).
- SEED는 Apache-2.0이고 `packages/rootage/NOTICE`가 있다. 재배포 때 라이선스 사본과 NOTICE 귀속 고지를 함께 전달해야 한다(Apache-2.0 §4(d)). 값을 옮긴 파일 KDoc에는 출처를 남긴다.
- 참조 기준: `daangn/seed-design` 커밋 `5f25880e93fe6f6eedba6f3842805cd8f6f352d1`(기본 브랜치 `dev`, `color.yaml` lastUpdated 26-06-15). 값 검토는 이 커밋으로 재현한다. 같은 커밋을 `design-system.md`와 KDoc에도 적는다.
- 검사 우회 금지(`@Suppress`, baseline). 기준 스크린샷은 커밋하지 않는다.

## 4. 접근 방식

| 방식 | 내용 | 대가 | 결정 |
|---|---|---|---|
| **1. 자체 토큰 + Material3 연결** | SEED 구조의 자체 토큰을 `CompositionLocal`로 제공하고, `MaterialTheme`의 `colorScheme`/`typography`/`shapes`에도 같은 값을 연결 | 연결 코드 한 겹 | **선택** |
| 2. Material3만 확장 | `ColorScheme`·`Typography` 칸에 SEED 값을 끼워 넣음 | M3 칸(약 30개)에 SEED 용도별 색(약 80개)·눌림 상태·`t1`~`t14`·간격·모서리를 담을 수 없음 | 탈락 |
| 3. 자체 토큰만, Material3 제거 | M3 테마 없이 자체 토큰만 | 컴포넌트(#19~#22)가 나오기 전까지 M3 컴포넌트가 기본 보라색으로 보이고, ripple·텍스트 선택 색을 직접 챙겨야 함 | 탈락 |

사용자는 1번을 골랐다. 우리 컴포넌트가 생기기 전에도 기존 M3 컴포넌트(`PushlogButton`이 감싼 `Button` 등)가 브랜드 색과 Pretendard로 보이기 때문이다.

브랜드 색 후보도 비교했다(흰 글자 대비): A 포레스트 그린 `#21884A`(4.48), B 딥 그린 `#1B7A43`(5.4), C 파인 그린 `#17694A`(6.7), 밝은 연두 `#a3e635`(1.5, 검은 글자 필요). 사용자는 A를 골랐다. 차분하면서 가장 밝고, 경쟁 앱과 색상이 겹치지 않는다. A는 4.5에 조금 못 미쳐 RGB를 1씩 낮춘 `#20874A`(4.54)로 확정했다. 눈으로는 같은 색이다.

다크 모드 브랜드 버튼도 비교했다(예시 화면으로 확인).

| 방식 | 다크 버튼 | 다크 브랜드 글자 | 결정 |
|---|---|---|---|
| **A. 어두운 초록 + 흰 글자** | 라이트와 같은 짙은 초록, 흰 글자 ≥ 4.5 | 더 밝은 초록 단계 | **선택** |
| B. 밝은 초록 + 검은 글자 | 밝은 초록(민트 쪽), 검은 글자 | 버튼과 같은 색 | 탈락 |

사용자는 A를 골랐다. 라이트·다크 모두 "초록 바탕 + 흰 글자"라 인상이 같고, 초록을 고른 이유(차분함·안정감)가 다크에서도 유지되며, SEED 다크(주황 + 흰 글자)와 생김새가 같다. 대가로 다크에서 버튼과 배경의 대비가 B보다 작다(약 3.7:1, UI 요소 기준 3:1은 넘음).

토큰 값은 Kotlin에 직접 적는다. Pushlog는 브랜드 단계를 새로 만들고 브랜드 연결을 바꾸므로 SEED와 갈라지는 부분이 많아, 생성기를 유지할 이득이 작다.

## 5. 설계

### 5.1 패키지와 공개 API

`com.citytexi.pushlog.core.designsystem` 아래:

```
theme/
  PushlogTheme.kt        // @Composable fun PushlogTheme(darkTheme, content) + object PushlogTheme
  color/                 // 팔레트, 용도별 색, 라이트/다크 값
  typography/            // Pretendard FontFamily, 글자 스타일
  dimension/             // 간격, 모서리
  shadow/
  motion/
  MaterialMapping.kt     // 토큰 → M3 ColorScheme/Typography/Shapes
```

화면은 `MaterialTheme`처럼 객체로 읽는다(함수와 객체가 같은 이름을 쓰는 M3 방식).

```kotlin
PushlogTheme.color.bg.brandSolid
PushlogTheme.color.fg.neutralMuted
PushlogTheme.color.stroke.focusRing
PushlogTheme.typography.t5Bold
PushlogTheme.dimension.x4
PushlogTheme.dimension.spacingX.globalGutter
PushlogTheme.radius.r2
PushlogTheme.shadow.s1
PushlogTheme.motion.duration.d3
PushlogTheme.motion.timingFunction.enter
```

- 이름은 SEED 토큰 이름을 camelCase로 옮긴다(`bg.brand-solid-pressed` → `bg.brandSolidPressed`). ktlint `property-naming`이 프로퍼티 이름의 밑줄을 막으므로 `_5`는 `Half`로 쓴다(`x0_5` → `x0Half`, `r1_5` → `r1Half`). 그 밖에는 다른 이름을 짓지 않는다. SEED 문서를 보고 바로 찾을 수 있어야 한다.
- 각 묶음은 `@Immutable data class`다. 일반 클래스로 두면 생성자 인자(용도별 색 `bg`만 약 40개)가 detekt `LongParameterList`(기본 6개)에 걸리는데, detekt는 data class를 검사하지 않는다(`ignoreDataClasses`). detekt 설정은 바꾸지 않는다.
- 묶음은 `staticCompositionLocalOf`로 제공하고 기본값은 라이트 인스턴스다(M3와 같은 방식). `PushlogTheme`으로 감싸지 않은 Preview·테스트도 라이트 값으로 그려진다. 라이트/다크 인스턴스는 미리 만든 값을 고른다.
- `object PushlogTheme`의 접근자는 `@Composable @ReadOnlyComposable get() = LocalPushlog….current` 형태다.
- 팔레트(`palette.gray100` 등)는 `internal`이다. 모듈 밖(feature)은 용도별 색만 쓴다.

### 5.2 색

- 팔레트: SEED의 gray, gray-alpha, blue, red, green, yellow, purple, static(흰·검정·알파) 단계를 라이트/다크 값 그대로 옮긴다. `gray-alpha`는 `bg.neutralWeakAlpha`, `stroke.neutralWeak` 등이 참조하므로 빠지면 안 된다. carrot은 옮기지 않는다.
- 새 단계 `forest`(100~1000, 라이트/다크): 색상(hue)은 `#20874A`의 약 144°로 고정한다. `forest-600`의 라이트 값은 정확히 `#20874A`다(SEED가 `bg.brand-solid`에 600번을 쓰기 때문). 단계가 올라갈수록 라이트는 어두워지고 다크는 밝아지는 SEED 방향을 따른다. 구체 값을 만드는 방법은 builder 몫이고, 아래 대비 조건을 지켜야 한다. (SEED green의 같은 단계 밝기에 맞추는 방식은 쓰지 않는다. green-600은 흰 글자 대비가 2.94라 조건을 지킬 수 없다.)
- 브랜드 연결: SEED가 `carrot-*`을 쓰는 자리(`bg.brand-*`, `fg.brand*`, `stroke.brand-*`)를 같은 단계 번호의 `forest-*`로 바꾼다. 라이트 `bg.brandSolid` = `#20874A`.
- 다크 예외 하나: SEED 다크는 `bg.brand-solid`와 `fg.brand`에 같은 단계(700)를 쓴다. 초록 하나로는 흰 글자 대비(어두워야 함)와 어두운 배경 위 글자 대비(밝아야 함)를 함께 지킬 수 없다(필요 휘도 ≤ 0.1833 대 ≥ 0.2138, 다크 `bg.layerDefault` `#16171b` 기준). 그래서 다크에서는 `bg.brandSolid`를 흰 글자 조건을 지키는 어두운 단계에, `fg.brand`·`fg.brandContrast`를 배경 위 조건을 지키는 밝은 단계에 연결한다. 어느 단계 번호에 둘지는 builder 몫이다.
- 다크 눌림 상태도 예외다. SEED 다크는 눌림(`bg.brand-solid-pressed`)을 한 단계 위(700→800)에 두는데, 다크 단계는 번호가 오를수록 밝아지므로 눌린 버튼의 흰 글자 대비가 4.5 아래로 떨어진다. 다크 `bg.brandSolidPressed`는 `bg.brandSolid`보다 **어두운**(낮은 번호) 단계에 연결한다. `fg.onBrandSolid`는 SEED대로 두 모드 모두 흰색이다.
- 예시 근사값(확정 아님): 다크 `bg.brandSolid` ≈ `#20874A`, 다크 `fg.brand` ≈ `#3FB26C`.
- 대비 조건(라이트·다크 모두, WCAG 상대 휘도 기준):
  - `fg.onBrandSolid` on `bg.brandSolid` ≥ 4.5
  - `fg.onBrandSolid` on `bg.brandSolidPressed` ≥ 4.5
  - `fg.brand` on `bg.layerDefault` ≥ 4.5
  - `fg.brandContrast` on `bg.brandWeak` ≥ 4.5
  - `fg.neutral` on `bg.layerDefault` ≥ 4.5
  - `stroke.brandSolid` on `bg.layerDefault` ≥ 3.0 (UI 요소 기준)
  - 이 조건은 JVM 단위 테스트로 검사한다(§5.7).
- 성공(positive)은 SEED green(에메랄드)을 그대로 쓴다. 브랜드(약 144°)와 색상이 약 20° 떨어져 있다. (내 판단: 성공 상태와 브랜드가 비슷해 보이면 컴포넌트 작업에서 다시 본다.)
- `fg.brand`는 기본 배경(`bg.layerDefault`) 위 전용이다. 라이트 `#20874A`는 흰색 위에서만 4.54이고, `bg.layerBasement`(`#f3f4f5`) 위 4.12, 연한 초록(`bg.brandWeak`) 위 약 4.1이다. 연한 초록·회색 배경 위 브랜드 글자는 `fg.brandContrast`를 쓴다.
- 나머지 용도별 색은 SEED 연결을 그대로 옮긴다. 리터럴 값 토큰(`bg.magicWeak`, `bg.transparent` 등)도 포함한다. 당근 전용인 `manner-temp.*`와 `banner.*` 묶음은 옮기지 않는다.

### 5.3 글자

- Pretendard 정적 OTF 세 파일을 넣는다: `res/font/pretendard_regular.otf`(400), `pretendard_medium.otf`(500), `pretendard_bold.otf`(700). 출처는 `orioncactus/pretendard`의 `dist/public/static/`. 합계 약 4.7MB로 가변 TTF 한 파일(6.7MB)보다 작고, `FontVariation`이 Robolectric에서 렌더링되는지에 기대지 않아도 된다.
- 라이선스: 저장소에 `core/designsystem/licenses/`를 두고 `pretendard-OFL.txt`, `seed-design-LICENSE`, `seed-design-NOTICE`를 넣는다. 이 폴더는 APK에 들어가지 않는다. OFL과 Apache-2.0 §4(d)는 배포본(APK)에도 고지를 요구하므로, 앱 안 오픈소스 라이선스 고지 방법은 미결 사항으로 `docs/synthesis/open-questions.md`에 올린다(이번 범위 밖).
- 글자 스타일: `t1`~`t14` 각각 `Regular`/`Medium`/`Bold`(42개)와 `screenTitle`, `articleBody`, `articleNote`. 크기·줄 높이는 SEED `font-size.yaml`·`line-height.yaml`·`components/typography.yaml` 값을 `sp`로 옮긴다(1rem = 16sp).
- `includeFontPadding = false`, 줄 높이는 `LineHeightStyle(Alignment.Center, Trim.None)`. (내 판단: SEED 웹 줄 높이와 같은 간격을 내기 위함.)

### 5.4 간격 · 모서리

- `dimension.x0Half`(SEED `x0_5`, 2dp) ~ `x16`(64dp): SEED `dimension.yaml` 값, 1px = 1dp.
- 용도별 간격: `spacingX.globalGutter`, `spacingX.betweenChips`, `spacingY.betweenText`, `spacingY.componentDefault`, `spacingY.navToTitle`, `spacingY.screenBottom`. SEED 연결 그대로.
- 모서리: `radius.r0Half`(SEED `r0_5`) ~ `r6`, `full`. `Dp`로 제공하고, `full`은 `RoundedCornerShape(50)`처럼 쓰도록 별도 `Shape`도 둔다.

### 5.5 그림자 · 모션

- 그림자 `s1`~`s3`: SEED 값(offset, blur, 색, 라이트/다크)을 그대로 데이터로 둔다. 그리기는 Compose ui 1.12.0(BOM `2026.08.00`)의 `Modifier.dropShadow(shape, Shadow)`(`androidx.compose.ui.draw`, `Shadow`는 `androidx.compose.ui.graphics.shadow`)를 쓰는 `Modifier.pushlogShadow(token, shape)` 하나로 감싼다. 대응: SEED `blur` → `Shadow.radius`, `offsetX/Y` → `offset`, `spread` → `spread`, `color` → `color`. `BlurMaskFilter` 기반이라 CSS blur보다 조금 퍼져 보이는 것은 허용한다.
- 모션: `motion.duration.d1`~`d6`, `colorTransition`, `pressedScale`은 ms `Int`. 곡선은 SEED 이름 그대로 `motion.timingFunction.*`이고 cubic-bezier 값으로 `CubicBezierEasing`(`linear`, `easing`, `enter`, `exit`, `enterExpressive`, `exitExpressive`, `pressedScale`).
- SEED `scale.s95`/`s97`/`s98`은 옮기지 않는다. SEED yaml에 "TODO: remove all three"로 삭제 예정이고 참조하는 컴포넌트가 없다.

### 5.6 Material3 연결

`PushlogTheme`이 `MaterialTheme(colorScheme, typography, shapes)`을 함께 감싼다.

- `lightColorScheme()`/`darkColorScheme()`은 지정하지 않은 칸을 M3 기본 보라 계열로 채운다. 그래서 **`ColorScheme`의 모든 인자를 명시한다**(기본값에 기대지 않음). 특히 `secondary*`·`tertiary*`(FilledTonalButton, NavigationBar 표시), `surfaceContainer*`(Card, Dialog, BottomSheet, NavigationBar), `inverseSurface`/`inverseOnSurface`/`inversePrimary`(Snackbar), `scrim`.
- `surfaceTint`는 `Color.Transparent`다. 기본값(primary)이면 tonal elevation이 초록으로 물든다.
- **`Typography` 15개 칸을 모두** Pretendard 스타일로 채운다. 빠진 칸은 기본 폰트로 나온다.

주요 연결:

| M3 | 토큰 |
|---|---|
| `primary` / `onPrimary` | `bg.brandSolid` / `fg.onBrandSolid` |
| `primaryContainer` / `onPrimaryContainer` | `bg.brandWeak` / `fg.brandContrast` |
| `background`, `surface` / `onBackground`, `onSurface` | `bg.layerDefault` / `fg.neutral` |
| `surfaceVariant` / `onSurfaceVariant` | `bg.neutralWeak` / `fg.neutralMuted` |
| `error` / `onError` | `bg.criticalSolid` / `fg.onCriticalSolid` |
| `outline` / `outlineVariant` | `stroke.neutralWeak` / `stroke.neutralMuted` |
| `surfaceTint` | `Color.Transparent` |
| `bodyLarge` / `bodyMedium` / `labelLarge` / `titleLarge` / `headlineSmall` | `t5Regular` / `t4Regular` / `t4Medium` / `t7Bold` / `t9Bold` |

`outline`을 `stroke.neutralWeak`로 두는 것은 SEED `text-input.yaml`이 입력창 테두리에 이 토큰을 쓰기 때문이다(`stroke.neutralMuted`는 라이트에서 거의 투명한 알파라 OutlinedTextField 테두리가 안 보인다). 표에 없는 칸에 어떤 토큰을 둘지는 builder가 가장 가까운 토큰으로 정하되, 빈칸으로 두지 않는다. 연결은 컴포넌트가 생기기 전 M3 컴포넌트가 브랜드 색으로 보이게 하는 임시 다리다. 화면 코드는 M3 테마를 읽지 않는다(§5.7).

### 5.7 검사

- 단위 테스트(JVM, `core:designsystem`): §5.2 대비 조건을 라이트·다크 인스턴스에서 계산해 검사한다.
- 카탈로그 스크린샷(Roborazzi, 라이트/다크): 용도별 색 견본, 글자 스타일 목록, 간격·모서리·그림자 견본. 기존 `PushlogButtonScreenshotTest`는 새 테마에서 그대로 돈다.
- Konsist 규칙 추가(약화가 아니라 추가). 대상은 `feature`와 `app` 소스다(test 소스 제외).
  - `androidx.compose.material3.MaterialTheme` import 금지. 기존 `LayerImportRulesTest`처럼 import 기반으로 검사해 `import … as M` 별칭도 잡는다.
  - `Color(0x…)` 리터럴과 `Color(red = …)` 생성 금지(텍스트 검사).
  - 기존 Konsist 테스트처럼 검사 대상이 비어 있지 않은지(`isNotEmpty`) 먼저 확인한다.
- 기존 `feature:sample`의 `MaterialTheme.typography.*` 사용은 `PushlogTheme.typography.*`로 바꾼다. `app`에 같은 사용이 있으면 함께 바꾼다.

### 5.8 문서

- `docs/architecture/design-system.md` 신규: 토큰 구조, 이름 규칙(SEED 이름 camelCase), 팔레트 `internal` 이유, 브랜드 단계 생성 방법과 대비 조건, M3 연결이 임시 다리라는 점, SEED 참조 커밋.
- `docs/index.md`와 `AGENTS.md` 문서 라우팅 표에 "디자인 시스템 토큰·컴포넌트" 행을 추가한다.
- 브랜드 색 결정(초록 = 안정감·안전 보관, 경쟁 앱과 비겹침, 후보 비교)은 제품 판단이므로 위키에 남긴다(`wiki-ingest` 흐름).

## 6. builder에게 맡기는 것

- `forest` 단계의 구체 값 생성 방법(§5.2 조건 안에서).
- §5.6 표에 없는 M3 칸에 둘 토큰(모든 칸을 채운다는 조건 안에서).
- 파일 분할과 내부 클래스 구조(§5.1 공개 API 형태는 유지).
- 카탈로그 스크린샷의 배치.
