package com.fcy.apt_annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Test(val id: Int = 0)
