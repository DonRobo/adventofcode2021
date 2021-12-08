plugins {
    kotlin("jvm") version "1.5.10"
}

group = "at.robbert"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.google.ortools:ortools-java:9.1.9490")
}
