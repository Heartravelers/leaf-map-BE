# 1. Build stage
FROM openjdk:17-jdk-slim as build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle Wrapper와 의존성 파일 복사
COPY gradle/ gradle/
COPY gradlew gradlew
COPY build.gradle .
COPY settings.gradle .
COPY src/ .

# 4. Gradle 빌드 실행
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon

# 5. 실행할 JAR 파일 복사
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 6. 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
