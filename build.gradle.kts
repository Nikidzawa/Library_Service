plugins {
    id("java")
    id ("io.spring.dependency-management") version "1.1.4"
    id ("org.springframework.boot") version "3.2.0"
}
group = "ru.nikidzawa"

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot", "spring-boot-starter-web")
    implementation("org.springframework.boot" , "spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.7.1")
    compileOnly ("org.projectlombok:lombok:1.18.30")
    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.30")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

}
