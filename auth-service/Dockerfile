# 1️⃣ Base image with Java
FROM eclipse-temurin:17-jdk-alpine

# 2️⃣ Create app directory inside container
WORKDIR /app

# 3️⃣ Copy Maven build (jar) into container
COPY target/*.jar app.jar

# 4️⃣ Expose port used by auth-service
EXPOSE 8081

# 5️⃣ Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
