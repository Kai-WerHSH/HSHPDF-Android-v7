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
        // O MuPDF (com.artifex.mupdf:fitz) fica no repositório da própria Artifex
        maven {
            url = uri("https://maven.ghostscript.com/")
            content { includeGroup("com.artifex.mupdf") }
        }
    }
}
rootProject.name = "HSHPDF"
include(":app")
