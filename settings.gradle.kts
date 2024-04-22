pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()  // https://stackoverflow.com/questions/60119465/android-gradle-project-sync-failed-due-to-unresolved-dependency
    }
}

rootProject.name = "Tuneder3"
include(":app")
 