# 아이템 도감 API 체크리스트

12주 계획 1단계-1 "아이템 도감 API 백지 반복"의 진행 기록이다.
완료 기준은 "안 보고 타이핑할 수 있다"가 아니라 "흐름을 설명할 수 있다"이다.

## 1회차 (2026-10-07 ~ 10-08)

범위: `Item` 엔티티 1개(name, description, grade) + CRUD API 5개

### 완료 기준

- [x] Docker로 PostgreSQL 컨테이너를 띄우고 Spring이 연결됨
- [x] Item 테이블이 JPA로 생성됨
- [ ] 5개 API가 각각 정상 응답함 (Postman) — 등록만 확인
- [ ] 없는 id로 조회·수정·삭제했을 때 응답 확인
- [ ] 요청 하나가 Controller → Service → Repository → DB를 지나는 흐름을 말로 설명

### Postman 실행 검증

보내기 전에 예상 상태 코드를 먼저 적고, 실제 결과와 비교한다.

| 순서 | 요청 | 예상 | 실제 | 확인할 것 |
|---|---|---|---|---|
| 1 | `POST /items` `{"name":"강철 검","description":"잘 벼린 검","grade":"RARE"}` | 200 | 200 | 응답에 `id`, `grade: RARE` |
| 2 | `POST /items` `grade: "전썰"` | | | 어느 단계에서 막히는가 (Jackson 변환) |
| 3 | `POST /items` `grade` 생략 | | | 어느 단계에서 막히는가 (`@Valid`) |
| 4 | `GET /items` | | | 등록한 아이템이 배열에 있음 |
| 5 | `GET /items/1` | | | 등록 응답과 같은 모양 |
| 6 | `PUT /items/1` 이름·등급을 **다른 값**으로 | | | 응답에 새 값, `updatedAt` 채워짐, 로그에 `update item set ...` |
| 7 | `GET /items/999` | | | 404 + 메시지 |
| 8 | `PUT /items/999` | | | 404 |
| 9 | `DELETE /items/1` | | | 204, 본문 없음 |
| 10 | `DELETE /items/1` (한 번 더) | | | 404 |
| 11 | `GET /items` | | | 빈 배열 `[]` |

### 흐름 설명 과제

`PUT /items/1` 요청 하나가 응답이 되어 돌아오기까지를 말로 설명한다.

- 어떤 클래스의 어떤 메서드를 어떤 순서로 지나는가
- 요청 JSON은 어디서 `ItemRequest`가 되는가
- `save()`를 부르지 않는데 DB가 바뀌는 이유는 무엇인가 (변경 감지)
- `@Transactional`이 없으면 무엇이 달라지는가
- 없는 id일 때는 어디서 예외가 나고, 누가 404로 바꾸는가

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

1. 아이템 도감 API 백지 반복 ← 지금
2. 게임 코어 (Spring 의존 없는 순수 Java + JUnit)
   - 캐릭터 상태 기계: 대기 → 탐험 → 전투 → 귀환
   - 방치 보상 계산: 경과 시간 × 성장 속도, 최대 누적 시간 상한
   - 텍스트 턴제 전투 판정
3. Spring 통합: 캐릭터·인벤토리 영속화, 방치 보상 수령 API(중복 수령 방지·멱등성·동시 요청), 레시피 재귀 CTE, 랭킹 윈도우 함수

## 로컬 환경 (2회차 때는 보지 않기)

| 항목 | 값 |
|---|---|
| 컨테이너 | `postgres-game` (`postgres:16`) |
| 포트 | 호스트 5433 → 컨테이너 5432 |
| DB | `game_test_db` |
| 테이블 확인 | `docker exec postgres-game psql -U admin -d game_test_db -c "\d item"` |
| 재시작 | `docker start postgres-game` (`run`은 처음 한 번만) |
