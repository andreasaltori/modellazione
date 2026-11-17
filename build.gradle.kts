plugins {
    application
}

group = "it.unicam.cs.mpgc.rpg122627"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 17
    options.encoding = "UTF-8"
}

application {
    mainClass = "it.unicam.cs.mpgc.rpg122627.Main"
}

tasks.test {
    useJUnitPlatform()
}