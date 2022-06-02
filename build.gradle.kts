plugins {
    kotlin("jvm") version "1.5.32"
    application
}

val scenariosClassPath = "testscenarios.impl"
val scenariosServiceInterface = "testscenarios.TestScenario"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

repositories {
    mavenCentral()

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
        resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
}


application {
    mainClass.set("Main")
}

tasks.classes {
    // generate service file for autoloading Scenarios via ServiceLoader
    doLast {
        val classDir = file("$buildDir/classes/kotlin/main/" + scenariosClassPath.replace(".", "/"))
        val scenarioFilesList = layout.files(classDir.listFiles())
            .filter { !it.name.contains("$") && !it.name.endsWith("Kt.class")}
            .joinToString("\n") { scenariosClassPath + "." + it.name.slice(0..it.name.length - 7) }
        val serviceDir = file("$buildDir/resources/main/META-INF/services/")
        serviceDir.mkdirs()
        val output = File(serviceDir, scenariosServiceInterface)
        output.writeText(scenarioFilesList)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "Main"
    }
}
