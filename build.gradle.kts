import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "me.profiluefter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter() // Required by postgrest-kt, reported as #11
}

dependencies {
    api("io.supabase:postgrest-kt:0.2.0")

    // very handy for e.g. id columns so imho it should be included (already a dependency of postgrest-kt)
    api("com.fasterxml.jackson.core:jackson-annotations:2.13.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}