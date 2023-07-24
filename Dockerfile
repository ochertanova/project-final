FROM maven:3.8-openjdk-17
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY resources ./resources
COPY lombok.config ./lombok.config
COPY README.md .
ARG JAR_FILE=target/jira-1.0.jar
COPY ${JAR_FILE} jira-1.0.jar
RUN mvn clean package -DskipTests
RUN mv ./target/*.jar ./jira-1.0.jar
RUN rm -rf ./target
RUN rm -rf ./src
EXPOSE 8080
ENTRYPOINT ["sh","-c","cd /app && java -jar /app/jira-1.0.jar --spring.profiles.active=prod"]
