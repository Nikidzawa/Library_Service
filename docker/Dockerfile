FROM openjdk:21

ARG LIBRARY=*.jar

COPY ${LIBRARY} library.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "library.jar"]