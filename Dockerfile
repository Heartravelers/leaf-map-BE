# 1. Build stage
FROM openjdk:17-jdk-slim as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle Wrapper와 의존성 파일 복사
COPY gradle/ gradle/               # Gradle Wrapper 설정 파일 복사
COPY gradlew gradlew               # Gradle Wrapper 스크립트 복사
COPY build.gradle .                # 빌드 스크립트 복사
COPY settings.gradle .             # 프로젝트 설정 복사
COPY src/ src/                     # 소스 코드 복사

# 4. Gradle 빌드 실행
RUN chmod +x gradlew               # Gradlew 실행 권한 추가
RUN ./gradlew build --no-daemon    # Gradle 빌드 실행

# 5. 실행할 JAR 파일 복사
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 6. 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
