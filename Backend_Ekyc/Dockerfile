#FROM adoptopenjdk:16_36-jre-hotspot as builder
#WORKDIR extracted
#ADD ./target/*.jar app.jar
#RUN java -Djarmode=layertools -jar app.jar extract
#
#FROM adoptopenjdk:16_36-jre-hotspot
#WORKDIR application
#COPY --from=builder extracted/dependencies/ ./
#COPY --from=builder extracted/spring-boot-loader/ ./
#COPY --from=builder extracted/snapshot-dependencies/ ./
#COPY --from=builder extracted/application/ ./
#
#EXPOSE 8005
#
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

FROM eclipse-temurin:21-jre-alpine AS builder

WORKDIR /workspace/extracted
COPY ./target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /workspace/extracted/dependencies/ ./
COPY --from=builder /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/extracted/application/ ./

EXPOSE 8005
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]