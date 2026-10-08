# 아이템 도감 API 체크리스트

12주 계획 1단계-1 "아이템 도감 API 백지 반복"의 진행 기록이다.
완료 기준은 "안 보고 타이핑할 수 있다"가 아니라 "흐름을 설명할 수 있다"이다.

## 1회차 (2026-10-07 ~ 10-08)

범위: `Item` 엔티티 1개(name, description, grade) + CRUD API 5개

### 완료 기준

- [x] Docker로 PostgreSQL 컨테이너를 띄우고 Spring이 연결됨
- [x] Item 테이블이 JPA로 생성됨
- [x] 5개 API가 각각 정상 응답함 — 10-08 실행 확인
- [x] 없는 id로 조회·수정·삭제했을 때 응답 확인 — 10-08 실행 확인 (모두 404)
- [x] 요청 하나가 Controller → Service → Repository → DB를 지나는 흐름을 말로 설명 — 10-08 `PUT` 기준으로 설명

**1회차 완료 (2026-10-08)**

### 실행 검증

보내기 전에 예상 상태 코드를 먼저 적고, 실제 결과와 비교한다.

- 1번: 10-07 S가 Postman으로 확인
- 나머지: 10-08 Claude가 curl로 확인 (다른 세션이 8080을 써서 jar를 8081로 실행). 예상 칸은 이번에 건너뜀
- 1번 아이템(강철 검)을 남기려고 수정·삭제는 새로 등록한 2번(`시험용 단검`)으로 시험함

| 순서 | 요청 | 예상 | 실제 | 확인할 것 | 결과 |
|---|---|---|---|---|---|
| 1 | `POST /items` `{"name":"강철 검","description":"잘 벼린 검","grade":"RARE"}` | 200 | 200 | 응답에 `id`, `grade: RARE` | `id: 1`, `grade: RARE` |
| 1-2 | `POST /items` `{"name":"시험용 단검",...,"grade":"NORMAL"}` | | 200 | 수정·삭제 시험용 | `id: 2` |
| 2 | `POST /items` `grade: "전썰"` | | 400 | 어느 단계에서 막히는가 | JSON 변환 단계 (`HttpMessageNotReadableException`) |
| 3 | `POST /items` `grade` 생략 | | 400 | 어느 단계에서 막히는가 | `@Valid` 검사 (`MethodArgumentNotValidException`) |
| 3-2 | `POST /items` `name: ""` | | 400 | | `@NotBlank` |
| 4 | `GET /items` | | 200 | 등록한 아이템이 배열에 있음 | 확인 |
| 5 | `GET /items/1` | | 200 | 등록 응답과 같은 모양 | 확인 |
| 6 | `PUT /items/2` → `시험용 대검`, `LEGEND` | | 200 | 응답에 새 값, `updatedAt` 채워짐, 로그에 `update item set ...` | 모두 확인. `save()` 없이 `select` → `update` |
| 7 | `GET /items/999` | | 404 | 404 + 메시지 | `아이템을 찾을 수 없습니다. id=999` |
| 8 | `PUT /items/999` | | 404 | 404 | 확인 |
| 9 | `DELETE /items/2` | | 204 | 204, 본문 없음 | 확인. SQL은 `select` 1번 + `delete` |
| 10 | `DELETE /items/2` (한 번 더) | | 404 | 404 | 확인 |
| 11 | `GET /items` | | 200 | 2번이 사라짐 | 1번만 남음 |
| 12 | `GET /items/abc` | | 400 | | 경로 값 → `Long` 변환 실패 (`MethodArgumentTypeMismatchException`) |

### 실행하면서 알게 된 것

- **400은 원인을 말해 주지 않는다.** 처리기가 없는 400은 본문이 모두 `{"status":400,"error":"Bad Request"}`로 같다. 원인은 서버 로그(`Resolved [...]`)를 봐야 알 수 있다. → 확장 과제 "오류 응답 형식 통일"
- **Windows에서 curl 명령 인자에 한글 JSON을 넣으면 CP949로 전송된다.** 로그에 `Invalid UTF-8 start byte`가 찍히고 400이 난다. 본문은 UTF-8 파일로 만들어 `--data-binary @파일`로 보낸다. Postman은 해당 없음.
- **Hibernate는 기본적으로 모든 컬럼을 UPDATE한다.** 바뀌지 않은 `created_at`도 `set` 절에 들어간다.
- **시각 정밀도:** `LocalDateTime.now()`는 소수점 7자리(`...07.8695867`), PostgreSQL `timestamp(6)`은 6자리라서 저장할 때 반올림된다(`...07.869587`). 같은 아이템이라도 등록 응답과 조회 응답의 `createdAt`이 다르게 보인다. 저장했다 읽은 시각끼리 비교할 때 주의 (게임 코어의 수령 시각).

### 흐름 설명 과제

`PUT /items/1` 요청 하나가 응답이 되어 돌아오기까지를 말로 설명한다.

- 어떤 클래스의 어떤 메서드를 어떤 순서로 지나는가
- 요청 JSON은 어디서 `ItemRequest`가 되는가
- `save()`를 부르지 않는데 DB가 바뀌는 이유는 무엇인가 (변경 감지)
- `@Transactional`이 없으면 무엇이 달라지는가
- 없는 id일 때는 어디서 예외가 나고, 누가 404로 바꾸는가

### 흐름 설명에서 헷갈렸던 것

| 처음 생각 | 실제 |
|---|---|
| 수정은 "새 객체로 update" | 새 `Item`은 없다. `findById`로 읽어 온 **그 객체**의 필드를 바꾼다. `ItemRequest`는 바꿀 값을 담은 재료 |
| `PUT` 응답은 200 또는 204 | `PUT`은 200 + 수정된 아이템. 204는 `DELETE` |
| 잘못된 값은 `@Transactional`에 막혀 400, 롤백 | `@Valid`(누락·빈 값)와 Jackson 변환(`"전썰"`)이 **Controller 입구**에서 막는다. Service 호출도 트랜잭션 시작도 없으니 롤백할 것도 없다 |
| UPDATE가 실행되는 이유는 "lazy" | **변경 감지(dirty checking)**: `findById` 때 찍은 스냅샷과 커밋 직전의 객체를 비교. 지연 로딩(lazy loading)은 연관 엔티티를 나중에 읽는 별개 개념 |
| `ItemNotFoundException`이 404로 바꾼다 | 예외는 "없다"는 사실만 알린다. 404로 번역하는 것은 `GlobalExceptionHandler` |
| 데이터가 JSON으로 바뀌어 응답 | 바꾸는 주체는 Jackson. 그 전에 `toResponse`로 엔티티 → `ItemResponse` |

### 1회차 설계 결정

| 항목 | 결정 | 이유 |
|---|---|---|
| `grade` 타입 | Enum (`NORMAL < RARE < UNIQUE < LEGEND < HIDDEN`) | 등급은 정해진 값만 있어야 함 |
| Enum 저장 방식 | `EnumType.STRING` | 상수 순서가 바뀌어도 안전 |
| `ddl-auto` | `update` | 컬럼을 추가할 예정 |
| 테이블 이름 | 단수 (`item`) | 엔티티 이름과 일치. 예약어(`user` 등)는 `player` 같은 이름으로 회피 |
| 수정 방식 | `PUT` (통째로 교체, `ItemRequest` 재사용) | |
| `updatedAt` | `Item.updateItem`의 파라미터로 Service가 현재 시각을 넘김 | |
| 없는 id 처리 | `ItemNotFoundException` + `GlobalExceptionHandler` → 404 | Service는 HTTP를 모르게 둠 |
| 없는 id 삭제 | 404 (먼저 조회 후 `delete(item)`) | 조회와 일관성. `deleteById`는 없는 id를 조용히 통과시킴 |
| 삭제 성공 응답 | 204 No Content | |

## 2회차 (예정)

같은 범위를 **빈 프로젝트에서 처음부터** 다시 만든다. `text-game-app`은 열지 않는다.

### 진행 방식

- [ ] start.spring.io에서 의존성 고르기부터 시작
- [ ] Docker 명령도 안 보고 작성
- [ ] 막혀서 찾아본 곳을 아래 "막힌 곳" 칸에 기록

### 1회차에서 걸린 함정: 이번에는 피했는가?

- [ ] `@Enumerated`에 `EnumType.STRING`을 명시했는가? (생략하면 ORDINAL)
- [ ] 엔티티에 `protected` 기본 생성자를 넣었는가? (JPA 필수, 컴파일러는 안 잡아 줌)
- [ ] 요청 → 엔티티(builder) → 응답(`toResponse`)까지 모든 필드가 이어졌는가?
- [ ] DTO에 getter·setter가 있는가? (없으면 JSON이 드나들지 못함)
- [ ] Docker 포트를 `호스트:컨테이너` 순서로, 컨테이너 쪽은 5432로 썼는가?
- [ ] 클래스 경로(`/items`)와 메서드 경로를 겹쳐 쓰지 않았는가?
- [ ] `@PathVariable`을 쓰는 메서드의 경로에 `{id}`가 있는가?
- [ ] 수정에서 엔티티(대상)와 요청 DTO(재료)를 구분했는가?
- [ ] 수정 Service에 `readOnly` 없는 `@Transactional`을 붙였는가?
- [ ] 수정에 불필요한 `save()`를 쓰지 않았는가?
- [ ] 없는 id 삭제가 404인가? (`deleteById`만 쓰면 조용히 성공)
- [ ] 데이터를 바꾸는 Service 메서드에 모두 `@Transactional`이 있는가?
- [ ] `@Valid`를 등록·수정의 `@RequestBody` 앞에 붙였는가?

### 흐름 설명 다시 확인할 질문

- [ ] `grade` 누락 요청은 어디서 막히고, 그때 Service와 트랜잭션은 어떤 상태인가?
- [ ] `"전썰"`은 `@Valid`보다 앞에서 막힌다. 어디서, 왜?
- [ ] 스냅샷은 **언제** 찍히고, 비교는 **언제** 일어나는가?
- [ ] 롤백이 일어나는 경우와 일어나지 않는 경우를 각각 하나씩 들 수 있는가?
- [ ] 지연 로딩과 변경 감지의 차이를 말할 수 있는가?

### 막힌 곳

| 단계 | 막힌 내용 | 찾아본 것 |
|---|---|---|
| | | |

## 확장 과제 (기록만, 백지 반복이 끝난 뒤)

새 아이디어는 "방치형 RPG 안에 넣을 수 있는가"로만 판단한다.

- [ ] 목록 검색 필터 (`grade`, `name`) + 동적 쿼리 — 검색 조건은 `ItemRequest`가 아닌 별도 DTO로
- [ ] 오류 응답 형식 통일 (Jackson 변환 실패, `@Valid` 실패, 경로 타입 오류의 400 본문 모양)
- [ ] Enum에 등급을 추가할 때 CHECK 제약(`item_grade_check`)을 SQL로 직접 교체 (`ALTER TABLE ... DROP/ADD CONSTRAINT`)
- [ ] 중복 정리: "찾거나 없으면 예외" 3곳, `createItem`의 응답 생성 vs `toResponse`, `getItems`의 인덱스 for 루프
- [ ] 쓰이지 않는 `NoSuchElementException` import 정리
- [ ] `id`가 0·음수일 때 처리 (`@Positive`)
- [ ] 시각을 바깥에서 넣어 주는 구조 — 게임 코어의 방치 보상 테스트에서 필요
- [ ] 기능별 패키지 구조 검토 — 도메인(캐릭터, 인벤토리, 전투)이 늘어날 때
- [ ] DB 비밀번호를 `application.yaml` 밖(환경 변수)으로 — 미니PC 배포 전
- [ ] Swagger 도입 (Spring Boot 4에 맞는 springdoc 버전 확인)

## 이후 로드맵 (1단계)

게임 방향은 [game-design-v0.1.md](game-design-v0.1.md)를 따른다. 12주 범위는 그 문서의 부록에 정리했다.

1. 아이템 도감 API 백지 반복 ← 지금 (1회차 완료, 2회차는 확인용)
2. 게임 코어 (Spring 의존 없는 순수 Java + JUnit)
   - 탐험 상태 기계: 대기 → 탐험 중 → 완료 → 결과 수령
   - AP 계산: 활동량(걸음 수, 활동 에너지) 입력 → AP, 하루 상한 (공식은 임시값)
   - **탐험 적합도 판정** (10-08 결정, 인수인계서의 턴제 전투를 대체): 지역·몬스터 태그·레벨·스킬·장비 → 적합도 → 랜덤 → S/A/B/C
   - 제작: 레시피, 재료 소비, 재료 부족 시 거부
   - 시간·랜덤은 바깥에서 넣어 테스트에서 고정할 수 있게
3. Spring 통합: 캐릭터·인벤토리 영속화, 활동량 수신 API(하루 단위, 멱등), 탐험 시작·결과 수령 API(중복 수령 방지·동시 요청), 지역 해금 API(좌표·반경), 레시피 재귀 CTE, 길드 점수 랭킹 윈도우 함수
4. 미니PC 배포
   - 1단계 완료 기준: Swagger로 활동량 입력 → AP → 탐험 → 결과 → 제작 → 장비 → 다음 탐험 한 바퀴
   - Apple Watch·HealthKit은 3단계 (12주 동안 Swift·워치 개발 없음)

## 로컬 환경 (2회차 때는 보지 않기)

| 항목 | 값 |
|---|---|
| 컨테이너 | `postgres-game` (`postgres:16`) |
| 포트 | 호스트 5433 → 컨테이너 5432 |
| DB | `game_test_db` |
| 테이블 확인 | `docker exec postgres-game psql -U admin -d game_test_db -c "\d item"` |
| 재시작 | `docker start postgres-game` (`run`은 처음 한 번만) |
