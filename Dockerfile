FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Install curl for health checks (optional, can be removed for production)
RUN apk add --no-cache curl

# Copy the built JAR file
# Note: Build with: ./gradlew bootJar
COPY build/libs/*.jar app.jar

# Create non-root user for security
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

# Expose the default Spring Boot port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
