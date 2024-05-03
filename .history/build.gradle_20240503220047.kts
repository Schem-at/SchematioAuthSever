plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven (url = "https://jitpack.io" )
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("com.github.Minestom:Minestom:fed512eaf6")
    implementation("com.google.code.gson:gson:2.8.8")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "schemat.Main"
    }
}