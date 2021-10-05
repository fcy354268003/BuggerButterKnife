package com.fcy.apt_processor


data class BindElementInfo<T>(
    val id: T,
    val propertyName: String,
    val annotation: Annotation
)

