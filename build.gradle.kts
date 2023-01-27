plugins {
    alias(commonlibs.plugins.multiplatform).apply(false)
    alias(commonlibs.plugins.kotlin.android).apply(false)
    alias(commonlibs.plugins.kotlin.serialization).apply(false)
    alias(commonlibs.plugins.android.lib).apply(false)
    alias(commonlibs.plugins.android.app).apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

subprojects {
    afterEvaluate {
        project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.let { ext ->
            ext.sourceSets.removeAll { sourceSet ->
                setOf(
                    "androidAndroidTestRelease",
                    "androidTestFixtures",
                    "androidTestFixturesDebug",
                    "androidTestFixturesRelease",
                ).contains(sourceSet.name)
            }
        }
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
            }
        }
    }
}
