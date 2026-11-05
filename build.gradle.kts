plugins {

    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
}
repositories { mavenCentral() }
springBoot { mainClass.set("ru.nto.individual.ApplicationKt") }
java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }
kotlin { compilerOptions { freeCompilerArgs.add("-Xjsr305=strict") } }
dependencies {
    implementation(files("libs/verifier-obf.jar"))
    implementation(files("libs/encoder-obf.jar"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.liquibase:liquibase-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test { useJUnitPlatform() }
