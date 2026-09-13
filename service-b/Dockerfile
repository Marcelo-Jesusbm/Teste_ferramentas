# Build context deve ser a raiz do repositorio (build multi-modulo Maven):
#   docker build -f service-b/Dockerfile -t service-b:latest .

# ---- Stage 1: build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copia primeiro somente os poms para aproveitar o cache de dependencias do Docker
COPY pom.xml .
COPY service-a/pom.xml service-a/pom.xml
COPY service-b/pom.xml service-b/pom.xml
RUN mvn -pl service-b -am -B -q dependency:go-offline

# Agora copia o codigo-fonte e builda apenas o service-b (-am builda modulos dos quais ele depende)
COPY service-b/src service-b/src
RUN mvn -pl service-b -am -B -q -DskipTests package

# ---- Stage 2: runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app:app

COPY --from=build /workspace/service-b/target/service-b.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:+UseSerialGC", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
