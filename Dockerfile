# 1. Build stage
FROM openjdk:17-jdk-slim as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle Wrapper와 의존성 파일 복사
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 4. Gradle 빌드 (로컬에서 빌드 시 -jvm 옵션을 통해 환경 변수 설정 가능)
RUN ./gradlew build --no-daemon --stacktrace

# 5. 실행할 JAR 파일 복사 (빌드된 JAR 파일)
FROM openjdk:17-jdk-slim

# 6. 빌드 단계에서 생성된 JAR 파일을 컨테이너로 복사
COPY --from=build /app/build/libs/*.jar /app/spring-app.jar

# 7. 포트 열기
EXPOSE 8080

# 8. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app/spring-app.jar"]