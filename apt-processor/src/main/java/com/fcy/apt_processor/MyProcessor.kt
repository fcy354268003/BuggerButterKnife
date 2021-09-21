package com.fcy.apt_processor

import com.fcy.apt_annotation.BindView
import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class MyProcessor : AbstractProcessor() {
    private val mMessager: Messager
        get() = processingEnv.messager
    private val mElementUtils: Elements
        get() = processingEnv.elementUtils
    private val filer: Filer
        get() = processingEnv.filer
    private val typeUtil
        get() = processingEnv.typeUtils

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        //TODO create the java file
        roundEnv?.getElementsAnnotatedWith(BindView::class.java)?.forEach { it ->
            val filedName = it.simpleName
            val id = it.getAnnotation(BindView::class.java).value
            mMessager.printMessage(Diagnostic.Kind.NOTE, "filedName = $filedName \t id = $id ")
        }
        return false
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> =
        LinkedHashSet<String>().apply { add(MyProcessor::class.java.canonicalName) }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()


}
private const val SUFFIX = "_viewBinding"
private const val FUN_BIND = "bind"
private const val FUN_UNBIND = "unbind"
internal inline fun <reified T> bind(activity: T) {
    runCatching {
        activity?.apply {
            val processedName = this::class.java.canonicalName + SUFFIX
            val clazz = Class.forName(processedName)
            val activityViewBinding = clazz.getConstructor().newInstance()
            val bindMethod = clazz.getDeclaredMethod(FUN_BIND, this::class.java)
            bindMethod.invoke(activityViewBinding, this)
        }
    }
}