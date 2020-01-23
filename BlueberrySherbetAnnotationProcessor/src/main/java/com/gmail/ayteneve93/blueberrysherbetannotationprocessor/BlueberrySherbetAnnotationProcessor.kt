package com.gmail.ayteneve93.blueberrysherbetannotationprocessor

import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryRequestType
import com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryService
import com.gmail.ayteneve93.blueberrysherbetannotations.WRITE


import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.Nullable
import java.io.File
import java.lang.reflect.Type
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic
import kotlin.collections.ArrayList
import kotlin.reflect.jvm.internal.impl.name.FqName
import kotlin.reflect.jvm.internal.impl.platform.JavaToKotlinClassMap
import kotlin.reflect.typeOf

@AutoService(Processor::class)
@Suppress("spellCheckingInspection")
class BlueberrySherbetAnnotationProcessor : AbstractProcessor() {
    companion object {
        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private const val BLUEBERRY_SHERBE_CORE_PACKAGE_NAME = "com.gmail.ayteneve93.blueberrysherbetcore"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BlueberryService::class.java.name,
            WRITE::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val writeMethodElements = roundEnv.getElementsAnnotatedWith(WRITE::class.java).filter { it.kind == ElementKind.METHOD }.map { it as ExecutableElement }
        roundEnv.getElementsAnnotatedWith(BlueberryService::class.java)
            .forEach { eachBlueberryElement ->
                if(eachBlueberryElement.kind != ElementKind.INTERFACE) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The Class '${(eachBlueberryElement as TypeElement).asClassName()}' annotated with '${BlueberryService::class.java}' is not an interface")
                    return true
                }
                eachBlueberryElement.enclosedElements.find {
                    it.getAnnotation(WRITE::class.java) == null
                }?.let { unsupportedElement ->
                    val unsupportedElementTypeName = unsupportedElement.kind.name.let { "${it.substring(0,1).toUpperCase(Locale.US)}${it.substring(1).toLowerCase(Locale.US)}" }
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "The $unsupportedElementTypeName '${unsupportedElement.simpleName}' is not Annotated with Blueberry Method Annotation")
                    return true
                }
                return generateDeviceServiceImplement(
                    eachBlueberryElement,
                    writeMethodElements.filter { it.enclosingElement == eachBlueberryElement })
            }
        return false
    }

    private val blueberryDeviceClass = Class.forName("$BLUEBERRY_SHERBE_CORE_PACKAGE_NAME.device.BlueberryDevice")
    private val blueberryDevicePropertyName = "mBlueberryDevice"

    private val blueberryRequestTypePackageString = "com.gmail.ayteneve93.blueberrysherbetannotations.BlueberryRequestType"

    private fun generateDeviceServiceImplement(blueberryElement : Element, writeMethods : List<ExecutableElement>) : Boolean {
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
            writeMethods.forEach { eachWriteMethod ->
                if(!eachWriteMethod.returnType.asTypeName().toString().contains("com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryRequest")) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Return Type of Method '${eachWriteMethod.simpleName}' Must be 'com.gmail.ayteneve93.blueberrysherbetcore.device.BlueberryRequest'")
                    return true
                }
                addFunction(
                    FunSpec.builder("${eachWriteMethod.simpleName}")
                        .addModifiers(KModifier.OVERRIDE)
                        .apply {
                            eachWriteMethod.parameters.forEach { eachVariableElement ->
                                addParameter(eachVariableElement.simpleName.toString(), eachVariableElement.javaToKotlinType())
                            }
                        }
                        .returns(eachWriteMethod.returnType.javaToKotlinType())
                        .addCode(
                            """
                                return ${eachWriteMethod.returnType.javaToKotlinType()}(mBlueberryDevice, "${eachWriteMethod.getAnnotation(WRITE::class.java).uuidString}", $blueberryRequestTypePackageString.WRITE, 10)
                            """.trimIndent()
                        )
                        .build()
                )
            }
        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir!!))
        return false
    }

    private fun VariableElement.javaToKotlinType() : TypeName {
        return asType().javaToKotlinType().let {
            if(getAnnotation(Nullable::class.java) == null) it.asNonNullable()
            else it.asNullable()
        }
    }
    private fun TypeMirror.javaToKotlinType() : TypeName = asTypeName().javaToKotlinType()
    private fun TypeName.javaToKotlinType() : TypeName {
        return if(this is ParameterizedTypeName) {
            ParameterizedTypeName.get(
                rawType.javaToKotlinType() as ClassName,
                *typeArguments.map { it.javaToKotlinType() }.toTypedArray()
            )
        } else {
            val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
            if(className == null) this
            else ClassName.bestGuess(className)
        }
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