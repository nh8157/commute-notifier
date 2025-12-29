# ---- build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /commuter_notifier
COPY . .
RUN ./gradlew clean shadowJar

# ---- runtime stage ----
FROM public.ecr.aws/lambda/java:17
COPY --from=build /commuter_notifier/app/build/libs/*.jar ${LAMBDA_TASK_ROOT}/lib/

# Lambda handler
CMD ["org.commuter_notifier.LambdaHandler::handleRequest"]
