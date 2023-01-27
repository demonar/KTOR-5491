pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("commonlibs") {
            from(files("./catalogs/common.versions.toml"))
        }
    }
}

rootProject.name = "KTOR-5491"
include(":shared")