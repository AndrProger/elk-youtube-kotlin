plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.prometeo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback:logback-classic:1.4.12")

    implementation("org.apache.kafka:kafka-clients:3.4.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
