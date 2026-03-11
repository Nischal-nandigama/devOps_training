FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY backend /app

RUN mvn clean package

EXPOSE 3000

CMD ["java","-jar","target/userapp-1.0-jar-with-dependencies.jar"]