---
id: data-layer
title: 데이터 계층: 데이터 소스 추가·SecureStore
category: architecture
status: living
platforms: android
verified: 2026-11-13
related_spec:
related_adr:
related_architecture:
related_code: [SecureStore, DataStoreModule, PushlogDatabase, SettingsDataSource]
tags: [architecture, data]
---

# 데이터 계층

## 데이터 소스 추가

`data`의 `RepositoryImpl`이 아래 core 모듈을 조합한다. feature는 이 모듈을 쓰지 못한다.

- `core:network`: `model/`에 `@Serializable` DTO(`SampleItemResponse` 참고), `SampleApi` 같은 Retrofit 인터페이스를 만들고 `NetworkModule`에서 `@Provides`한다. 경로를 정할 때 base URL은 `/`로 끝나므로 `@GET`은 `/` 없이 시작한다.
- `core:database`: `model/`에 entity, `dao/`에 DAO를 만들고 `@Database(entities = [...])`에 entity를 추가한 뒤 `DatabaseModule`에서 DAO를 제공한다.
  - **스키마를 바꾸면 `@Database`의 `version`을 올리고 마이그레이션을 반드시 추가한다.** 버전을 안 올리면 기존 설치에서 크래시한다. 방법(AutoMigration, `AutoMigrationSpec`, 수동 `Migration`)은 `PushlogDatabase` KDoc에 있다.
  - 새 스키마 json(`core/database/schemas/`)을 커밋한다. 이미 배포한 버전의 json은 고치지 않는다.
  - `PushlogDatabaseMigrationTest`에 이전 버전에서 현재 버전으로 여는 케이스를 추가한다.
- `core:datastore`: 일반 설정은 `SettingsDataSource`(키를 추가), 민감 값은 `SecureStore`(아래 절)를 쓴다.
- 변환 위치: DTO, entity는 `data`에서 `domain` 모델로 바꾼다. DTO나 entity가 `domain`, `feature`로 새지 않게 한다.

## SecureStore

`core:datastore`의 `SecureStore`는 값을 AndroidKeyStore 키로 암호화해 저장하는 key-value 저장소다.

```kotlin
interface SecureStore {
    suspend fun get(key: String): String?   // 없거나 지금 읽을 수 없으면 null. 예외를 던지지 않음
    suspend fun put(key: String, value: String)   // 쓰기/암호화 실패 시 예외
    suspend fun remove(key: String)
}
```

- `DataStoreModule`이 Hilt `SingletonComponent`에 바인딩한다. 생성자 주입으로 받는다.
- `feature`는 `core:datastore`를 쓸 수 없다. `data`의 `RepositoryImpl`에서 주입받고, `domain`의 Repository 인터페이스로 감싸 노출한다.
- 키가 사라졌거나(재설치, 백업 복원) 값이 손상되면 값을 지우고 `null`을 돌려준다. 호출부는 `null`을 "로그인 필요" 같은 재입력 상태로 처리한다.
- Keystore가 잠깐 실패하거나(`KeyStoreException`, `ProviderException`) 파일을 읽지 못하면 값은 지우지 않고 `null`을 돌려준다. 다음 호출에서 다시 읽힐 수 있다.
- `put`은 파일 쓰기(`IOException`)나 암호화(`GeneralSecurityException`, `ProviderException`)가 실패하면 예외를 던진다. `data`의 RepositoryImpl은 이 예외를 그대로 두고, 그 Repository를 부르는 UseCase가 `runSuspendCatching`으로 감싼다.
- DataStore 주입은 `@SecurePreferences`/`@SettingsPreferences` qualifier로 구분한다.
- 모든 호출은 `suspend`다. 메인 스레드 블로킹을 하지 않는다.
