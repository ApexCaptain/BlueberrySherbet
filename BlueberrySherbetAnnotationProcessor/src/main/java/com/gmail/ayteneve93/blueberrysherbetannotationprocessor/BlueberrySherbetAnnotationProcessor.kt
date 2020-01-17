package com.gmail.ayteneve93.blueberrysherbetannotationprocessor

import com.gmail.ayteneve93.blueberrysherbetannotations.Blueberry
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE


import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class BlueberrySherbetAnnotationProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            Blueberry::class.java.name,
            WRITE::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val writeMethodElements = roundEnv.getElementsAnnotatedWith(WRITE::class.java).filter { it.kind == ElementKind.METHOD }.map { it as ExecutableElement }
        roundEnv.getElementsAnnotatedWith(Blueberry::class.java)
            .forEach { eachBlueberryElement ->
                if(eachBlueberryElement.kind != ElementKind.INTERFACE) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The Class '${(eachBlueberryElement as TypeElement).asClassName()}' annotated with '${Blueberry::class.java}' is not an interface")
                    return true
                }
                generateDeviceServiceImplement(
                    eachBlueberryElement,
                    writeMethodElements.filter { it.enclosingElement == eachBlueberryElement })
            }
        return false
    }

    private val blueberryDeviceClass = Class.forName("com.gmail.ayteneve93.blueberrysherbet.device.BlueberryDevice")
    private val blueberryDevicePropertyName = "mBlueberryDevice"
    private fun generateDeviceServiceImplement(blueberryElement : Element, writeMethods : List<ExecutableElement>) {
        val className = blueberryElement.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(blueberryElement).toString()
        val fileName = "Blueberry${className}Impl"
        val fileBuilder = FileSpec.builder(packageName, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        classBuilder.apply {
            addSuperinterface(blueberryElement.asType().asTypeName())
            addProperty(
                PropertySpec.builder(blueberryDevicePropertyName, blueberryDeviceClass)
                    .build()
            )
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(blueberryDevicePropertyName, blueberryDeviceClass)
                    .addStatement("this.${blueberryDevicePropertyName} = $blueberryDevicePropertyName")
                    .build()
            )

        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun log(msg : String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "READ$className"
        val fileBuilder= FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        for (enclosed in element.enclosedElements) {
            if (enclosed.kind == ElementKind.FIELD) {
                classBuilder.addProperty(
                    PropertySpec.varBuilder(enclosed.simpleName.toString(), enclosed.asType().asTypeName().asNullable(), KModifier.PRIVATE)
                        .initializer("null")
                        .build()
                )
                classBuilder.addFunction(
                    FunSpec.builder("get${enclosed.simpleName}")
                        .returns(enclosed.asType().asTypeName().asNullable())
                        .addStatement("return ${enclosed.simpleName}")
                        .build()
                )
                classBuilder.addFunction(
                    FunSpec.builder("set${enclosed.simpleName}")
                        .addParameter(ParameterSpec.builder("${enclosed.simpleName}", enclosed.asType().asTypeName().asNullable()).build())
                        .addStatement("this.${enclosed.simpleName} = ${enclosed.simpleName}")
                        .build()
                )
            }
        }
        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir))
    }
}