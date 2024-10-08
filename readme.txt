

user 로그인 기능 재편성 예정 









### JPA LocalDateTime 자동 생성하는 방법

- @EnableJpaAuditing (Main 클래스 )
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스)
```java
    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
```

### validation 처리

controller 에서 validation 검증해야 함
-> controller 의 post/put(특정 메서드)에서 validation 검증을 위한 예외 처리를 AOP 에 등록 시킴

1. AOP : CustomValidation 이라는 클래스 만듦
 - @Component, @Aspect 붙임 ( 스프링에 등록 )
 - @Pointcut : 어떤 메서드에 적용할지 ? 알림
 - @Around / @Before / @After 등으로 적용 범위(구현할 코드) 알림
    - 2번에서 만든 커스터마이징한 예외처리를 validation 발생시 적용하도록 설계

 2. Advice : validation 을 위한 customizing 예외처리를 만듦
 - 이 프로젝트에서 만든 CustomException(@RestControllerAdvice) 에
   customizing 예외처리를 등록함


### 이메일 가입 후 로그인 - 토큰 발급 / 토큰 인증
1. Refresh Token의 만료 기간이 지나면 Refresh Token을 재발급하는 로직
2. 새로운 Refresh Token이 생성되면, 이를 저장소에 저장하고 검증하는 로직


---

### 레디스 설정

1. 레디스 의존성 추가
2. docker-compose.yml 내용 설정 + application.yml 내용 추가
3. 터미널 창 : 명령어 실행 =>  docker-compose up -d

