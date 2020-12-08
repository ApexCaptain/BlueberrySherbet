package com.gmail.ayteneve93.blueberrysherbetannotationprocessor

import com.gmail.ayteneve93.blueberrysherbetannotations.*


import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.tools.Diagnostic
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.annotations.Nullable
import java.io.File
import javax.lang.model.type.DeclaredType
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName


@AutoService(Processor::class)
@Suppress("spellCheckingInspection")
class BlueberrySherbetAnnotationProcessor : AbstractProcessor() {

    private val supportedAnnotationKotlinClasses = arrayOf(
        READ::class,
        WRITE::class,
        WRITE_WITHOUT_RESPONSE::class,
        NOTIFY::class,
        INDICATE::class
    )

    private val supportedAnnotationTypesQualifiedNames = supportedAnnotationKotlinClasses.map { eachAnnotationKotlinClass -> eachAnnotationKotlinClass.qualifiedName }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BlueberryService::class.java.name,
            Priority::class.java.name
        ).apply { addAll(supportedAnnotationKotlinClasses.map { eachAnnotationKotlinClass -> eachAnnotationKotlinClass.java.name }) }
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {

        var doesProcessingErrorExists = false
        var blueberryElementWithProcessingError : Element? = null

        roundEnv.getElementsAnnotatedWith(BlueberryService::class.java)
            .forEach { eachBlueberryElement ->
                blueberryElementWithProcessingError = eachBlueberryElement
                if(eachBlueberryElement.kind != ElementKind.INTERFACE) {
                    errorLog("The Class '${(eachBlueberryElement as TypeElement).asClassName()}' annotated with '${BlueberryService::class.java}' is not an interface")
                    doesProcessingErrorExists = true
                    return@forEach
                }

                eachBlueberryElement.enclosedElements.also { enclosedElements ->

                    enclosedElements
                        .find { eachEnclosedElement -> eachEnclosedElement.kind != ElementKind.METHOD }
                        ?.let { unsupportedEnclosedElement ->
                            val unsupportedElementTypeName = unsupportedEnclosedElement.kind.name.let { "${it.substring(0,1).toUpperCase(Locale.US)}${it.substring(1).toLowerCase(Locale.US)}" }
                            errorLog("The $unsupportedElementTypeName '${unsupportedEnclosedElement.simpleName}' is not a Method")
                            doesProcessingErrorExists = true
                            return@forEach
                        }

                    val blueberryMethods = enclosedElements.filter { eachEnclosedElement ->
                        when(eachEnclosedElement.annotationMirrors
                            .map { eachAnnotationMirror -> eachAnnotationMirror.annotationType.toString() }
                            .intersect(supportedAnnotationTypes).size) {
                            1 -> true
                            0 -> {
                                errorLog("The Method '${eachEnclosedElement.simpleName}' is not Annotated with Blueberry Method Annotation")
                                doesProcessingErrorExists = true
                                return@forEach
                            }
                            else -> {
                                errorLog("The Method '${eachEnclosedElement.simpleName}' has too many Blueberry Annotations. it should have only one of $supportedAnnotationTypesQualifiedNames")
                                doesProcessingErrorExists = true
                                return@forEach
                            }
                        }
                    }.map { it as ExecutableElement }
                    if(generateDeviceServiceImplements(eachBlueberryElement, blueberryMethods)) {
                        doesProcessingErrorExists = true
                        return@forEach
                    }
                }
            }
        if(doesProcessingErrorExists) errorLog("Compilation Failed while Processing Service Interface : '$blueberryElementWithProcessingError'")
        return doesProcessingErrorExists
    }

    private val blueberryDeviceMemeberPropertyName = "mBlueberryDevice"
    //private val moshiClass = ClassName("com.squareup.moshi", "Moshi")
    //private val moshiMemberPropertyName = "mMoshi"

    private fun generateDeviceServiceImplements
                (blueberryElement : Element,
                 blueberryMethods : List<ExecutableElement>) : Boolean {

        val className = blueberryElement.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(blueberryElement).toString()
        val fileName = "Blueberry${className}Impl"
        val fileBuilder = FileSpec.builder(packageName,fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        val blueberryDeviceClass = ClassName("${BLUEBERRY_SHERBET.CORE}.device", "BlueberryDevice").parameterizedBy(blueberryElement.asType().asTypeName())

        classBuilder.apply {

            // Super Interface
            addSuperinterface(blueberryElement.asType().asTypeName())

            // Blueberry Device Member
            addProperty(
                PropertySpec.builder(blueberryDeviceMemeberPropertyName, blueberryDeviceClass)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

            // Moshi Member
            /*
            addProperty(
                PropertySpec.builder(moshiMemberPropertyName, moshiClass)
                    .mutable()
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
             */

            // Construcotr
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(blueberryDeviceMemeberPropertyName, blueberryDeviceClass)
                    .addStatement("this.$blueberryDeviceMemeberPropertyName = $blueberryDeviceMemeberPropertyName")
                    //.addStatement("this.$moshiMemberPropertyName = ${moshiClass}.Builder().build()")
                    .build()
            )

            // Add Moshi Adapters Function
            /*
            addFunction(
                FunSpec.builder("addMoshiAdapters")
                    .addParameter(ParameterSpec.builder("adapters", Any::class, KModifier.VARARG).build())
                    .addCode("""
                        this.$moshiMemberPropertyName = this.${moshiMemberPropertyName}.newBuilder().apply {
                            adapters.forEach { add(it) }
                        }.build()
                    """.trimIndent())
                    .build()
            )
            */
            if(generateDeviceServiceMethodImplements(this, blueberryMethods)) return true
        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir!!))
        return false
    }

    private val uuidRegexWithHyphen = Regex("""[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}""")
    private val uuidRegexWithoutHyphen = Regex("""[0-9a-fA-F]{32}""")
    private val uuidRegex16bit = Regex("""[0-9a-fA-F]{4}""")
    private fun generateDeviceServiceMethodImplements(classBuilder : TypeSpec.Builder, methods : List<ExecutableElement>) : Boolean {
        classBuilder.apply {
            methods.forEach { eachMethod ->

                // Checking Parameter Validation
                val requestType = supportedAnnotationKotlinClasses.find { eachMethod.getAnnotation(it.java) != null }!!.java
                val parameterCount = eachMethod.parameters.size
                if(requestType in arrayOf(WRITE::class.java, WRITE_WITHOUT_RESPONSE::class.java)) {
                    if(parameterCount != 1) {
                        errorLog("Method named '${eachMethod.simpleName}' is '${requestType.simpleName}' characteristic request function. It must have only one parameter.")
                        return true
                    }
                } else if(parameterCount != 0) {
                    errorLog("Method named '${eachMethod.simpleName}' is '${requestType.simpleName}' characteristic request function. It must have no parameter.")
                    return true
                }

                // Checking Return Type Validation
                val originalReturnTypeNameString = eachMethod.returnType.asTypeName().toString()
                var originalReturnTypeArgumentTypeName : TypeName? = null
                val returnTypeBlueberryRequestClass = when(requestType) {

                    WRITE::class.java -> if(!originalReturnTypeNameString.contains("${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST}")) {
                        errorLog("Return Type of Method '${eachMethod.simpleName}' Must be ${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST}")
                        return@generateDeviceServiceMethodImplements true
                    } else {
                        ClassName("${BLUEBERRY_SHERBET.CORE.REQUEST}", BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST.simpleName)
                    }

                    WRITE_WITHOUT_RESPONSE::class.java -> if(!originalReturnTypeNameString.contains("${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST_WITHOUT_RESPONSE}")) {
                        errorLog("Return Type of Method '${eachMethod.simpleName}' Must be ${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST_WITHOUT_RESPONSE}")
                        return@generateDeviceServiceMethodImplements true
                    } else ClassName("${BLUEBERRY_SHERBET.CORE.REQUEST}", BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_WRITE_REQUEST_WITHOUT_RESPONSE.simpleName)

                    READ::class.java -> if(!originalReturnTypeNameString.contains("${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_READ_REQUEST}")) {
                        errorLog("Return Type of Method '${eachMethod.simpleName}' Must be ${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_READ_REQUEST}")
                        return@generateDeviceServiceMethodImplements true
                    } else {
                        originalReturnTypeArgumentTypeName = (eachMethod.returnType as DeclaredType).typeArguments[0].asTypeName().javaToKotlinType().let {
                            if(it.toString() == "*") Any::class.asTypeName()
                            else it
                        }
                        ClassName("${BLUEBERRY_SHERBET.CORE.REQUEST}", BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_READ_REQUEST.simpleName).parameterizedBy(originalReturnTypeArgumentTypeName)
                    }

                    NOTIFY::class.java, INDICATE::class.java -> if(!originalReturnTypeNameString.contains("${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_NOTIFY_OR_INDICATE_REQUEST}")) {
                        errorLog("Return Type of Method '${eachMethod.simpleName}' Must be ${BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_NOTIFY_OR_INDICATE_REQUEST}")
                        return@generateDeviceServiceMethodImplements true
                    } else {
                        originalReturnTypeArgumentTypeName = (eachMethod.returnType as DeclaredType).typeArguments[0].asTypeName().javaToKotlinType().let {
                            if(it.toString() == "*") Any::class.java.asTypeName()
                            else it
                        }
                        ClassName("${BLUEBERRY_SHERBET.CORE.REQUEST}", BLUEBERRY_SHERBET.CORE.REQUEST.BLUEBERRY_NOTIFY_OR_INDICATE_REQUEST.simpleName).parameterizedBy(originalReturnTypeArgumentTypeName)
                    }

                    else -> return@generateDeviceServiceMethodImplements true

                }


                val uuidString : String = eachMethod.annotationMirrors.filter { eachAnnotationMirror ->
                    eachAnnotationMirror.annotationType.toString() in supportedAnnotationTypesQualifiedNames
                }.let { filteredAnnotations ->
                    if(filteredAnnotations.size > 1) {
                        errorLog("Method '${eachMethod.simpleName}' has too many request type arguments : $filteredAnnotations")
                        return true
                    }
                    filteredAnnotations[0].elementValues.entries.first().value.toString().replace("\"", "")
                }.let { sourceUuidString ->
                    when {
                        uuidRegexWithHyphen.matchEntire(sourceUuidString) != null -> sourceUuidString
                        uuidRegexWithoutHyphen.matchEntire(sourceUuidString) != null -> {
                            arrayOf(
                                sourceUuidString.slice(0..7),
                                sourceUuidString.slice(8..11),
                                sourceUuidString.slice(12..15),
                                sourceUuidString.slice(16..19),
                                sourceUuidString.slice(20..31)
                            ).joinToString("-")
                        }
                        uuidRegex16bit.matchEntire(sourceUuidString) != null -> "00009999-$sourceUuidString-1000-8000-00805F9B34FB"
                        else -> {
                            errorLog("UUID String '$sourceUuidString' is not valid. It must be matched with either one of ${arrayListOf(uuidRegexWithHyphen, uuidRegexWithoutHyphen, uuidRegex16bit)}")
                            return true
                        }
                    }.toLowerCase(Locale.US)
                }
                var parameterName : String? = null
                addFunction(
                    FunSpec.builder("${eachMethod.simpleName}")
                        .addModifiers(KModifier.OVERRIDE, KModifier.FINAL)
                        .apply {
                            if(eachMethod.parameters.size == 1) {
                                eachMethod.parameters[0].let {
                                    parameterName = it.simpleName.toString()
                                    addParameter(
                                        it.simpleName.toString(),
                                        it.asType().asTypeName().javaToKotlinType().copy(nullable = it.getAnnotation(Nullable::class.java) != null))
                                }
                            }
                        }

                        .returns(returnTypeBlueberryRequestClass)
                        .addCode(
                            when(requestType) {
                                WRITE::class.java -> {
                                    """
                                        return ${returnTypeBlueberryRequestClass}(
                                            $blueberryDeviceMemeberPropertyName,
                                            ${eachMethod.getAnnotation(Priority::class.java)?.priority?:Priority.defaultPriority},
                                            "$uuidString",
                                            ${parameterName?:"null"},
                                            ${eachMethod.getAnnotation(WRITE::class.java).checkIsReliable}
                                        )
                                    """.trimIndent()
                                }
                                WRITE_WITHOUT_RESPONSE::class.java -> {
                                    var endSignal = eachMethod.getAnnotation(WRITE_WITHOUT_RESPONSE::class.java)!!.endSignal
                                    if(endSignal.startsWith("\$")) endSignal = "\\$endSignal"
                                    if(endSignal.length > 20) errorLog("End Signal cannot be larger than 20 bytes!")
                                    """
                                        return ${returnTypeBlueberryRequestClass}(
                                            $blueberryDeviceMemeberPropertyName,
                                            ${eachMethod.getAnnotation(Priority::class.java)?.priority?:Priority.defaultPriority},
                                            "$uuidString",
                                            ${parameterName?:"null"},
                                            ${eachMethod.getAnnotation(WRITE_WITHOUT_RESPONSE::class.java).checkIsReliable},
                                            "$endSignal"
                                        )
                                    """.trimIndent()
                                }
                                READ::class.java -> {
                                    """
                                        return ${returnTypeBlueberryRequestClass}(
                                            $originalReturnTypeArgumentTypeName::class.java,
                                            $blueberryDeviceMemeberPropertyName,
                                            ${eachMethod.getAnnotation(Priority::class.java)?.priority?:Priority.defaultPriority},
                                            "$uuidString"
                                        )
                                    """.trimIndent()
                                }
                                NOTIFY::class.java, INDICATE::class.java -> {
                                    var endSignal =
                                        if(requestType == NOTIFY::class.java) eachMethod.getAnnotation(NOTIFY::class.java)!!.endSignal
                                        else eachMethod.getAnnotation(INDICATE::class.java)!!.endSignal
                                    if(endSignal.startsWith("\$")) endSignal = "\\$endSignal"
                                    if(endSignal.length > 20) errorLog("End Signal cannot be larger than 20 bytes!")
                                    """
                                        return ${returnTypeBlueberryRequestClass}(
                                            $originalReturnTypeArgumentTypeName::class.java,
                                            $blueberryDeviceMemeberPropertyName,
                                            ${eachMethod.getAnnotation(Priority::class.java)?.priority?:Priority.defaultPriority},
                                            "$uuidString",
                                            ${requestType.asTypeName()}::class.java,
                                            "$endSignal"
                                        )
                                    """.trimIndent()
                                }
                                else -> ""
                            }
                        )
                        .build()
                )


            }
        }
        return false
    }

    private fun errorLog(msg : String) = processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    private fun verboseLog(msg : String) = processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg)

    private fun TypeName.javaToKotlinType() : TypeName {
        return when(this) {
            is ParameterizedTypeName -> {
                (rawType.javaToKotlinType() as ClassName).parameterizedBy(
                    *typeArguments.map { eachTypeArgument ->
                        eachTypeArgument.javaToKotlinType()
                    }.toTypedArray()
                )
            }
            is WildcardTypeName -> {
                if (inTypes.isNotEmpty()) WildcardTypeName.consumerOf(inTypes[0].javaToKotlinType())
                else WildcardTypeName.producerOf(outTypes[0].javaToKotlinType())
            }
            else -> {
                val className = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(toString()))?.asSingleFqName()?.asString()
                if(className == null) this
                else ClassName.bestGuess(className)
            }
        }
    }

    companion object {
        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

        private object BLUEBERRY_SHERBET { override fun toString(): String = "com.gmail.ayteneve93"
            object CORE { const val simpleName = "blueberrysherbetcore"; override fun toString(): String = "${BLUEBERRY_SHERBET}.$simpleName"
                object DEVICE { const val simpleName = "device"; override fun toString(): String = "${CORE}.$simpleName" }
                object REQUEST { const val simpleName = "request"; override fun toString(): String = "${CORE}.$simpleName"
                    object BLUEBERRY_NOTIFY_OR_INDICATE_REQUEST { const val simpleName = "BlueberryNotifyOrIndicateRequestInfo"; override fun toString(): String = "${REQUEST}.$simpleName"}
                    object BLUEBERRY_READ_REQUEST { const val simpleName = "BlueberryReadRequestInfo"; override fun toString(): String = "${REQUEST}.$simpleName"}
                    object BLUEBERRY_WRITE_REQUEST { const val simpleName = "BlueberryWriteRequestInfo"; override fun toString(): String = "${REQUEST}.$simpleName"}
                    object BLUEBERRY_WRITE_REQUEST_WITHOUT_RESPONSE { const val simpleName = "BlueberryWriteRequestInfoWithoutResponse"; override fun toString(): String = "${REQUEST}.$simpleName"}
                }
            }
        }
    }

}