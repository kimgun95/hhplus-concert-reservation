## 마일스톤

---
![image](https://github.com/user-attachments/assets/7a9a076a-092d-4648-a7f4-020f0eda259b)

일정 산출 이유
- 단위/통합 테스트 or 리팩토링 같은 일정은 기능 개발과 함께 항상 진행된다는 가정하에 항목에서 표현하지 않았습니다.
- 개발 초기에는 db 세팅을 함께 진행하여 일정을 길게 잡았습니다.
- 핵심 API 개발 단계에서는 실패 케이스를 생각하고 길게 잡았습니다.
- 일부 항목은 추후 db erd 설계에 따라 변경/삭제 될 수 있습니다. 

## 플로우 차트

---
![image](https://github.com/user-attachments/assets/83e903a0-a58e-40b1-9ad1-bd83c294057b)

- 대기열의 범위를 고민했습니다.   
콘서트 좌석 예매를 시작하는 첫 단계인 '좌석 조회'에서는 대기열을 확인합니다.   
그 이후는 고려하지 않고 다른 상태를 확인하도록 설계했습니다.   
ex) 좌석을 예약할 때는 '좌석을 차지한 상태'를 이용해 예약을 진행했습니다.


## 시퀀스 다이어그램

---

### 대기열 토큰 발급
```mermaid
sequenceDiagram
actor User
User->> 대기열 토큰 발급 API: 대기열 조회
대기열 토큰 발급 API->>DB: 대기열 토큰 조회
alt 토큰이 있다면
    DB->>대기열 토큰 발급 API: 기존 대기열 토큰 반환
else 토큰이 없다면
    DB->>대기열 토큰 발급 API: 신규 대기열 토큰 생성 및 반환
end
대기열 토큰 발급 API->>User: 대기열 확인
```

### 좌석 조회
```mermaid
sequenceDiagram
actor User
User->>좌석 조회 API: 좌석 조회
좌석 조회 API->>DB: 대기열 순번 조회
alt 대기열 순번이 아니라면
    DB->>DB: 10초 간격으로 대기열 순번 조회
else 대기열 순번이 되었다면
    DB->>좌석 조회 API: 좌석 반환
end
좌석 조회 API->>User: 좌석 확인
```

### 좌석 예약
```mermaid
sequenceDiagram
actor User
User->>좌석 예약 API: 좌석 예약
좌석 예약 API->>DB: 좌석 상태 조회
alt 예약 불가능한 좌석이라면
    DB->>좌석 예약 API: 에러 발생(이미 예약된 좌석입니다)
else 예약 가능한 좌석이라면
    DB->>좌석 예약 API: 좌석 상태 변환 및 성공 응답
end
좌석 예약 API->>User: 좌석 예약 확인
```

### 포인트 조회
```mermaid
sequenceDiagram
actor User
User->>포인트 조회 API: 포인트 조회
포인트 조회 API->>DB: 포인트 잔액 조회
DB->>포인트 조회 API: 포인트 잔액 반환
포인트 조회 API->>User: 포인트 확인
```

### 포인트 충전
```mermaid
sequenceDiagram
actor User
User->>포인트 충전 API: 포인트 충전
포인트 충전 API->>DB: 포인트 변환 및 충전 기록 저장
DB->>포인트 충전 API: 포인트 잔액 반환
포인트 충전 API->>User: 포인트 확인
```

### 결제
```mermaid
sequenceDiagram
actor User
User->>결제 API: 결제
결제 API->>DB: 포인트 상태 확인
alt 포인트가 부족하다면
    DB->>결제 API: 에러 발생(포인트가 부족합니다)
else 포인트가 충분하다면
    DB->>결제 API: 포인트 변환 및 사용 기록 저장, 예약 상태 변환, 성공 응답
end
결제 API->>User: 결제 확인
```