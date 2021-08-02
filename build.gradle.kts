plugins {
    id("org.springframework.boot") version "2.4.9"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("org.springframework.boot:spring-boot-starter-amqp:2.4.9")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.4.9")

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.3")
    testImplementation("io.projectreactor:reactor-test:3.4.8")
    testImplementation("org.springframework.amqp:spring-rabbit-test:2.3.10")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
