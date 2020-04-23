plugins {
    kotlin("jvm") version "1.3.71"
    application
    distribution
    id("net.nemerosa.versioning") version "2.12.1"
}

group = "com.wire.bots.polls"
version = versioning.info?.tag ?: versioning.info?.lastTag ?: "development"

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
    implementation("ai.blindspot.ktoolz", "ktoolz", "1.0.6")

    // Ktor server dependencies
    val ktorVersion = "1.3.2"
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-jackson", ktorVersion)
    implementation("io.ktor", "ktor-websockets", ktorVersion)

    // Ktor client dependencies
    implementation("io.ktor", "ktor-client-json", ktorVersion)
    implementation("io.ktor", "ktor-client-jackson", ktorVersion)
    implementation("io.ktor", "ktor-client-apache", ktorVersion)
    implementation("io.ktor", "ktor-client-jetty", ktorVersion)
    implementation("io.ktor", "ktor-client-logging-jvm", ktorVersion)

    // Prometheus metrics
    implementation("io.ktor", "ktor-metrics-micrometer", ktorVersion)
    implementation("io.micrometer", "micrometer-registry-prometheus", "1.4.1")

    // logging
    implementation("io.github.microutils", "kotlin-logging", "1.7.9")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")

    // DI
    val kodeinVersion = "6.5.4"
    implementation("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-ktor-server-jvm", kodeinVersion)

    // database
    implementation("org.postgresql", "postgresql", "42.2.2")

    val exposedVersion = "0.22.1"
    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)
    implementation("pw.forst", "exposed-upsert", "1.0")

    // database migrations from the code
    implementation("org.flywaydb", "flyway-core", "6.3.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    distTar {
        archiveFileName.set("app.tar")
    }

    withType<Test> {
        useJUnitPlatform()
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

    register("resolveDependencies") {
        doLast {
            project.allprojects.forEach { subProject ->
                with(subProject) {
                    buildscript.configurations.forEach { if (it.isCanBeResolved) it.resolve() }
                    configurations.compileClasspath.get().resolve()
                    configurations.testCompileClasspath.get().resolve()
                }
            }
        }
    }
}
