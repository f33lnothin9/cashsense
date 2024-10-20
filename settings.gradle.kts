pluginManagement {
    includeBuild("build-logic")
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
    }
}

rootProject.name = "cashsense"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":baselineprofile")
include(":mobile")

include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:data")
include(":core:designsystem")
include(":core:locales")
include(":core:model")
include(":core:notifications")
include(":core:shortcuts")
include(":core:ui")

include(":feature:home")
include(":feature:category:list")
include(":feature:category:dialog")
include(":feature:glancewidget")
include(":feature:subscription:list")
include(":feature:subscription:dialog")
include(":feature:transaction")
include(":feature:wallet:detail")
include(":feature:wallet:dialog")
include(":feature:settings")