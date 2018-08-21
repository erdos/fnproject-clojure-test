FROM clojure:lein-2.7.1-alpine

WORKDIR /app/

COPY project.clj .
RUN lein deps

COPY . .
RUN lein uberjar

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=2", "-jar", "/app/target/fnproject-app.jar"]
