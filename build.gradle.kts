import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val springBootVersion = "2.1.7.RELEASE"

    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
        classpath("org.jetbrains.kotlin:kotlin-allopen:1.4.21")
    }
}

plugins {
    java
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.jpa") version "1.3.61"
    kotlin("plugin.allopen") version "1.4.21"
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

group = "org.navi"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin-spring")
    plugin("org.springframework.boot")
    plugin("io.spring.dependency-management")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
    
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation(kotlin("test-junit"))
    //compile("com.h2database:h2")

    // Mongo DB
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // Tika
    implementation("org.apache.tika:tika-parsers:1.25")
}

tasks.test {
    systemProperty("navi.isTesting", "test")
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}