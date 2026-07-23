FROM amazoncorretto:17-alpine AS build
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -B -Dmaven.test.skip=true

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/delivery-app-2.0.jar ./app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
