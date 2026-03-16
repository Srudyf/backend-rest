FROM public.ecr.aws/docker/library/openjdk:22-ea-17-slim-bookworm

ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom" \
    SPRING_PROFILES_ACTIVE=prod

RUN apt-get update && apt-get install -y --no-install-recommends curl=7.88.1-10+deb12u14 tzdata=2025b-0+deb12u2 && rm -rf /var/lib/apt/lists/*

USER nobody:nogroup
WORKDIR /app

COPY --chown=nobody:nogroup target/*.jar /app/app.jar

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
