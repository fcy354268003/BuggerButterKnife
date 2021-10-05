package com.fcy.apt_annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(value = AnnotationRetention.BINARY)
annotation class BindClick(val ids:IntArray)