plugins {
    kotlin("jvm") version "1.3.61"
    application
    distribution
}

group = "com.wire.bots.polls"
version = "0.1"

application {
    mainClassName = "com.wire.bots.polls.PollBotKt"
}

repositories {
    jcenter()
}

val ktorVersion: String by project
val ktoolzVersion: String by project

dependencies {
    // stdlib
    implementation(kotlin("stdlib-jdk8"))
    // extension functions
    implementation("ai.blindspot.ktoolz:ktoolz:$ktoolzVersion")

    // KTor server dependencies
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    // KTor client dependencies
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    // logging
    implementation("io.github.microutils:kotlin-logging:1.7.8")
    implementation("org.slf4j:slf4j-simple:1.6.1")

    // DI
    implementation("org.kodein.di:kodein-di-generic-jvm:6.5.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:6.5.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
