package com.fcy.apt_annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class BindView(val id:Int = 10)


