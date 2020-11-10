plugins {
    kotlin("jvm") version "1.4.10"
    maven
}

group = "dev.mk2481"
version = "0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    targetCompatibility = JavaVersion.VERSION_1_6
    sourceCompatibility = JavaVersion.VERSION_1_6
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    testImplementation("junit:junit:4.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.1")
    testImplementation("org.assertj:assertj-core:3.18.0")
    testImplementation("io.mockk:mockk:1.10.2")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.9")
}

val repo = File(rootDir, "repository")
tasks {
    "uploadArchives"(Upload::class) {
        repositories.withGroovyBuilder {
            "mavenDeployer" {
                "repository"("url" to "file://${repo.absolutePath}")
            }
        }
    }
}