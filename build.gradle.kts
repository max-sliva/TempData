import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

group = "me.mxslv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
dependencies{
//    implementation("com.opencsv:opencsv:5.7.1")
    implementation("com.couchbase.lite:couchbase-lite-java:3.0.5")
    implementation("org.controlsfx:controlsfx:8.40.18")
}
