FROM eclipse-temurin:21-jre

WORKDIR /auth

COPY target/auth.jar /auth/auth.jar

EXPOSE 8090

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# Run the entrypoint script
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]