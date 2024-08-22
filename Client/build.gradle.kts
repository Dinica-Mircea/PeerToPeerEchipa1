plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // JSON Processing
    implementation("com.googlecode.json-simple:json-simple:1.1")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.2")


    // Springdoc OpenAPI for Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

//    //OAuth2
//    // https://mvnrepository.com/artifact/org.springframework.security/spring-security-oauth2-client
//    implementation("org.springframework.security:spring-security-oauth2-client:6.3.1")
//
//    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security
//    implementation("org.springframework.boot:spring-boot-starter-security:3.2.4")
//
//    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-oauth2-resource-server
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.2.4")

}

tasks.test {
    useJUnitPlatform()
}
