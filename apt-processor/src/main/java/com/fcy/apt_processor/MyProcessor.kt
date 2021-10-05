package com.fcy.apt_processor

import com.fcy.apt_annotation.BindClick
import com.fcy.apt_annotation.BindLayout
import com.fcy.apt_annotation.BindView
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

//_ViewBinding
private const val SUFFIX = "_ViewBinding"
private const val FUN_BIND = "bind"
private const val FUN_UNBIND = "unbind"

class MyProcessor : AbstractProcessor() {
    private var mMessager: Messager? = null

    //        get() = processingEnv.messager
    private var mElementUtils: Elements? = null

    //        get() = processingEnv.elementUtils
    private var mFiler: Filer? = null

    //        get() = processingEnv.filer
    private val mAnnotationType: List<Class<out Annotation>> =
        listOf(BindView::class.java, BindLayout::class.java, BindClick::class.java)
    private val supportType =
        setOf<Class<out Any>>(BindView::class.java, BindLayout::class.java, BindClick::class.java)

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        processingEnv?.apply {
            mMessager = messager
            mElementUtils = elementUtils
            mFiler = filer

        }
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.let {
//            mMessager.printMessage(Diagnostic.Kind.ERROR, "process")
            val annotationInfo = mutableMapOf<Element, MutableList<BindElementInfo<out Any>>>()
            //todo save the activity annotation info
            //process the annotation
            fillTheElementContainer(annotationInfo, roundEnv)
            // create the java file by javapoet
            createJavaFile(annotationInfo)
        }
        return false
    }

    private fun createJavaFile(map: MutableMap<Element, MutableList<BindElementInfo<out Any>>>) {
        for (element in map.keys) {
            val packageName = mElementUtils?.getPackageOf(element)?.qualifiedName?.toString()
            val bindMB = MethodSpec.methodBuilder(FUN_BIND)
                .returns(TypeName.VOID)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(element.asType()), "activity")
            val infoList = map[element]
            if (infoList == null || infoList.size == 0)
                return
            if (infoList[0].annotation is BindLayout) {
                bindMB.addStatement("activity.setContentView(${infoList[0].id});")
            }
            infoList.forEach {
                when (it.annotation) {
                    is BindView -> {
                        bindMB.addStatement(
                            """
                            activity.${it.propertyName} = activity.findViewById(${it.id});
                        """.trimIndent()
                        )
                    }
                    is BindClick -> {
                        (it.id as IntArray).forEach { id ->
                            bindMB.addStatement(
                                """
                            activity.findViewById($id).setOnClickListener(new android.view.View.OnClickListener() { 
                                                   @Override 
                                                    public void onClick(android.view.View v) { activity.${it.propertyName}(v); } 
                                                });
                        """.trimIndent()
                            )
                        }

                    }
                    else -> {
                    }
                }
            }
            val methodSpec = bindMB.build()
            val typeSpec = TypeSpec.classBuilder(element.simpleName.toString() + SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build()
            val javaFile = JavaFile.builder(packageName, typeSpec).build()

            runCatching {
                javaFile.writeTo(mFiler)
            }.exceptionOrNull()?.printStackTrace()
        }
    }

    private fun fillTheElementContainer(
        annotationInfo: MutableMap<Element, MutableList<BindElementInfo<out Any>>>,
        roundEnv: RoundEnvironment
    ) {
        for (clazz in mAnnotationType) {
            roundEnv.getElementsAnnotatedWith(clazz).forEach { element ->
                when (val annotation = element.getAnnotation(clazz)) {
                    is BindView -> {
                        if (!annotationInfo.containsKey(element.enclosingElement)) {
                            annotationInfo[element.enclosingElement] = mutableListOf()
                        }
                        annotationInfo[element.enclosingElement]?.add(
                            BindElementInfo(
                                annotation.id,
                                element.simpleName.toString(),
                                annotation
                            )
                        )
                    }
                    is BindLayout -> {
                        if (!annotationInfo.containsKey(element)) {
                            annotationInfo[element] = mutableListOf()
                        }
                        annotationInfo[element]?.add(
                            0,
                            BindElementInfo(
                                annotation.id,
                                element.simpleName.toString(),
                                annotation
                            )
                        )
                    }
                    is BindClick -> {
                        if (!annotationInfo.containsKey(element.enclosingElement)) {
                            annotationInfo[element.enclosingElement] = mutableListOf()
                        }
                        annotationInfo[element.enclosingElement]?.add(
                            BindElementInfo(
                                annotation.ids,
                                element.simpleName.toString(),
                                annotation
                            )
                        )
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val it = HashSet<String>()
//        it.add(BindView::class.java.canonicalName)
        it.add(BindLayout::class.java.canonicalName)
//        it.add(BindClick::class.java.canonicalName)
        return it
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }


}
//
//
//internal inline fun <reified T> bind(activity: T) = runCatching {
//    activity?.apply {
//        // todo activityViewBinding bindMethod 可以尝试做一个缓存 -> 反射效率低问题
//        val processedName = this::class.java.canonicalName + SUFFIX
//        val clazz = Class.forName(processedName)
//        val activityViewBinding = clazz.getConstructor().newInstance()
//        val bindMethod = clazz.getDeclaredMethod(FUN_BIND, this::class.java)
//        bindMethod.invoke(activityViewBinding, this)
//    }
//}.exceptionOrNull()?.printStackTrace()
