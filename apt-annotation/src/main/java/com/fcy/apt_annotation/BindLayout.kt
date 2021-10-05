package com.fcy.apt_annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BindLayout(val id: Int = -1)
