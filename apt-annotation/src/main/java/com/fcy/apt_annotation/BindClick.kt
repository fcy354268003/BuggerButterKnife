package com.fcy.apt_annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(value = AnnotationRetention.RUNTIME)
annotation class BindClick(val ids:IntArray)