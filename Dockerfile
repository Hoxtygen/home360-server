# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build

# Create the /project directory
RUN mkdir /project

# Copy everything into the project directory
COPY . /project

# Set working directory
WORKDIR /project

# Build the application
RUN mvn clean package

# Stage 2: Run
FROM eclipse-temurin:17-jdk-alpine

RUN apk add dumb-init

# Create the /app directory
RUN mkdir /app

# Create a group and a user, then add the user to the group
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser

# Copy the generated JAR file
COPY --from=build /project/target/home360-*.jar /app/home360.jar

# Set working directory
WORKDIR /app

RUN chown -R javauser:javauser /app

USER javauser

# Expose the port
EXPOSE 8080

# Run the application
CMD [ "dumb-init","java", "-jar", "home360.jar"]
