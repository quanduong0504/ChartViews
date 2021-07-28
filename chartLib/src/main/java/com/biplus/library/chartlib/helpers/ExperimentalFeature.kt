package com.biplus.library.chartlib.helpers

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This feature is experimental and it can change in future."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class ExperimentalFeature