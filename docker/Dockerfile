FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY backend /app

RUN mvn package

EXPOSE 3000

CMD ["java","-cp","target/backend-1.0.jar","Main"]