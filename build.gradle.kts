plugins {
    kotlin("jvm") version "1.3.70"
    application
    distribution
    id("net.nemerosa.versioning") version "2.8.2"
}

group = "com.wire.bots.polls"
version = versioning.info?.tag ?: versioning.info?.lastTag ?: "0.0"

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
    val ktorVersion = "1.3.1"
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
    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    // DI
    val kodeinVersion = "6.5.0"
    implementation("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-ktor-server-jvm", kodeinVersion)

    // database
    implementation("org.postgresql", "postgresql", "42.2.2")

    val exposedVersion = "0.21.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("pw.forst", "exposed-upsert", "1.0")

    // database migrations from the code
    implementation("org.flywaydb", "flyway-core", "6.2.4")
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
