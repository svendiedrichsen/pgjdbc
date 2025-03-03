import org.gradle.kotlin.dsl.dependencies

plugins {
    id("build-logic.build-params")
    id("org.checkerframework")
}

dependencies {
    providers.gradleProperty("checkerframework.version")
        .takeIf { it.isPresent }
        ?.let {
            val checkerframeworkVersion = it.get()
            "checkerFramework"("org.checkerframework:checker:$checkerframeworkVersion")
            if (buildParameters.buildJdkVersion == 8) {
                // only needed for JDK 8
                "checkerFrameworkAnnotatedJDK"("org.checkerframework:jdk8:$checkerframeworkVersion")
            }
        } ?: run {
            val checkerframeworkVersion = "3.49.0"
            "checkerFramework"("org.checkerframework:checker:$checkerframeworkVersion")
            if (buildParameters.buildJdkVersion == 8) {
                // only needed for JDK 8
                "checkerFrameworkAnnotatedJDK"("org.checkerframework:jdk8:$checkerframeworkVersion")
            }
        }
}

checkerFramework {
    skipVersionCheck = true
    excludeTests = true
    // See https://checkerframework.org/manual/#introduction
    checkers.add("org.checkerframework.checker.nullness.NullnessChecker")
    checkers.add("org.checkerframework.checker.optional.OptionalChecker")
    // checkers.add("org.checkerframework.checker.index.IndexChecker")
    checkers.add("org.checkerframework.checker.regex.RegexChecker")
    extraJavacArgs.add("-Astubs=" +
            fileTree("$rootDir/config/checkerframework") {
                include("*.astub")
            }.asPath
    )
    // Translation classes are autogenerated, and they
    extraJavacArgs.add("-AskipDefs=^org\\.postgresql\\.translation\\.")
    // The below produces too many warnings :(
    // extraJavacArgs.add("-Alint=redundantNullComparison")
}
