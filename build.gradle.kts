plugins {
    id("java")
    application
}

group = "fr.xStagg"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.13.1")
}

application {
    mainClass.set("fr.xStagg.GraphFloydWarshall.Main") // or "com.example.Main" if in a package
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "fr.xStagg.GraphFloydWarshall.Main" // or "com.example.Main" if in a package
        )
    }
}