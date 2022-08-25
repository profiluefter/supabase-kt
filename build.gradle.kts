import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    `maven-publish`
}

group = "me.profiluefter"
version = "1.0-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("supabase-kt") {
            from(components["kotlin"])
        }
    }
}

repositories {
    mavenCentral()
    jcenter() // Required by postgrest-kt, reported as #11
}

dependencies {
    api("io.supabase:postgrest-kt:0.2.0")

    // for realtime
    // having this as "api" shouldn't be necessary but is currently needed for RealtimeChannel error handling
    api("com.github.dsrees:JavaPhoenixClient:1.0.0")

    // deserialize realtime payloads
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // http client used for gotrue client
    val ktorVersion = "2.0.2"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}