
plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
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
  //  implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
   // implementation("org.springframework.boot:spring-boot-starter-actuator:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.3.2")

    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // https://mvnrepository.com/artifact/org.springframework/spring-webmvc
    implementation("org.springframework:spring-webmvc:6.1.11")


    // Springdoc OpenAPI for Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Testing
  //  testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

//tasks.withType<Jar>() {
//    manifest {
//        attributes["Main-Class"] = "org.example.rest.StartRestServices"
//    }
//}

tasks.jar {
//    manifest {
//        attributes["Main-Class"] = "org.example.rest.StartRestServices"
//    }
    enabled = false
}

//jar {
//    enabled = false
//}