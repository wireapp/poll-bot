plugins {
    kotlin("jvm") version "1.3.61"
    application
    distribution
}

group = "com.wire.bots.polls"
version = "0.1"

val mainClass = "com.wire.bots.polls.PollBotKt"

application {
    mainClassName = mainClass
}

repositories {
    jcenter()
}

dependencies {
    // stdlib
    implementation(kotlin("stdlib-jdk8"))
    // extension functions
    implementation("ai.blindspot.ktoolz", "ktoolz", "1.0.3")

    // Ktor server dependencies
    val ktorVersion = "1.3.0"
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-jackson", ktorVersion)
    implementation("io.ktor", "ktor-websockets", ktorVersion)

    // Ktor client dependencies
    implementation("io.ktor", "ktor-client-json", ktorVersion)
    implementation("io.ktor", "ktor-client-jackson", ktorVersion)
    implementation("io.ktor", "ktor-client-websockets", ktorVersion)
    implementation("io.ktor", "ktor-client-cio", ktorVersion)

    // logging
    implementation("io.github.microutils", "kotlin-logging", "1.7.8")
    implementation("org.slf4j", "slf4j-simple", "1.6.1")

    // DI
    val kodeinVersion = "6.5.0"
    implementation("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-ktor-server-jvm", kodeinVersion)

    // database
    implementation("org.postgresql", "postgresql", "42.2.2")

    val exposedVersion = "0.20.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = mainClass
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveFileName.set("polls.jar")
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}
