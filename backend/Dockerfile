FROM eclipse-temurin:25-jdk-jammy

WORKDIR /app

# Copier le fichier JAR (ou adapter selon votre build)
COPY target/capsule_temporelle-*.jar app.jar

# Exposer le port
EXPOSE 9005

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod

# Démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]