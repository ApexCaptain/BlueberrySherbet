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
        NOTIFY::class
    )

    private val supportedAnnotationTypesQualifiedNames = supportedAnnotationKotlinClasses.map { eachAnnotationKotlinClass -> eachAnnotationKotlinClass.qualifiedName }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            BlueberryService::class.java.name
        ).apply { addAll(supportedAnnotationKotlinClasses.map { eachAnnotationKotlinClass -> eachAnnotationKotlinClass.java.name }) }
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val readMethodElements = roundEnv.getElementsAnnotatedWith(READ::class.java).filter { it.kind == ElementKind.METHOD }.map { it as ExecutableElement }
        val writeMethodElements = roundEnv.getElementsAnnotatedWith(WRITE::class.java).filter { it.kind == ElementKind.METHOD }.map { it as ExecutableElement }
        val notifyMethodElements = roundEnv.getElementsAnnotatedWith(NOTIFY::class.java).filter { it.kind== ElementKind.METHOD }.map { it as ExecutableElement }

        roundEnv.getElementsAnnotatedWith(BlueberryService::class.java)
            .forEach { eachBlueberryElement ->
                if(eachBlueberryElement.kind != ElementKind.INTERFACE) {
                    errorLog("The Class '${(eachBlueberryElement as TypeElement).asClassName()}' annotated with '${BlueberryService::class.java}' is not an interface")
                    return true
                }
                eachBlueberryElement.enclosedElements.find { eachEnclosedElement ->
                    eachEnclosedElement.annotationMirrors.map { it.annotationType.toString() }.intersect(supportedAnnotationTypes).isEmpty()
                }?.let { unsupportedEnclosedElement ->
                    val unsupportedElementTypeName = unsupportedEnclosedElement.kind.name.let { "${it.substring(0,1).toUpperCase(Locale.US)}${it.substring(1).toLowerCase(Locale.US)}" }
                    errorLog("The $unsupportedElementTypeName '${unsupportedEnclosedElement.simpleName}' is not Annotated with Blueberry Method Annotation")
                    return true
                }
                if(generateDeviceServiceImplements(
                    eachBlueberryElement,
                    readMethodElements.filter { it.enclosingElement == eachBlueberryElement },
                    writeMethodElements.filter { it.enclosingElement == eachBlueberryElement },
                    notifyMethodElements.filter { it.enclosingElement == eachBlueberryElement })) {
                    errorLog("Compilation Failed while Processing $eachBlueberryElement")
                    return true
                }
            }
        return false
    }

    // Refer : https://github.com/square/kotlinpoet
    private val blueberryDeviceMemeberProrpertyName = "mBlueberryDevice"
    private val moshiClass = ClassName("com.squareup.moshi", "Moshi")
    private val moshiMemberPropertyName = "mMoshi"

    private fun generateDeviceServiceImplements
                (blueberryElement : Element,
                 readMethods : List<ExecutableElement>,
                 writeMethods : List<ExecutableElement>,
                 notifyMethods : List<ExecutableElement>) : Boolean {
        val className = blueberryElement.simpleName.toString()
        val packageName = processingEnv.elementUtils.getPackageOf(blueberryElement).toString()
        val fileName = "Blueberry${className}Impl"
        val fileBuilder = FileSpec.builder(packageName,fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        val blueberryDeviceClass = ClassName("$BLUEBERRY_SHERBET_CORE_PACKAGE_NAME.device", "BlueberryDevice").parameterizedBy(blueberryElement.asType().asTypeName())

        classBuilder.apply {

            // Super Interface
            addSuperinterface(blueberryElement.asType().asTypeName())

            // Blueberry Device Member
            addProperty(
                PropertySpec.builder(blueberryDeviceMemeberProrpertyName, blueberryDeviceClass)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

            // Moshi Member
            addProperty(
                PropertySpec.builder(moshiMemberPropertyName, moshiClass)
                    .mutable()
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

            // Construcotr
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(blueberryDeviceMemeberProrpertyName, blueberryDeviceClass)
                    .addStatement("this.$blueberryDeviceMemeberProrpertyName = $blueberryDeviceMemeberProrpertyName")
                    .addStatement("this.$moshiMemberPropertyName = ${moshiClass}.Builder().build()")
                    .build()
            )

            // Add Moshi Adapters Function
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
            //this.mBlueberryDevice.setBlueberryServiceImpl(this)
            if(generateDeviceServiceMethodImplements(this, readMethods, READ::class.java)
                || generateDeviceServiceMethodImplements(this, writeMethods, WRITE::class.java)
                || generateDeviceServiceMethodImplements(this, notifyMethods, NOTIFY::class.java)) return true
        }

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir!!))
        return false
    }

    private val uuidRegexWithHyphen = Regex("""[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}""")
    private val uuidRegexWithoutHyphen = Regex("""[0-9a-fA-F]{32}""")
    private val uuidRegex16bit = Regex("""[0-9a-fA-F]{4}""")
    private fun generateDeviceServiceMethodImplements(classBuilder : TypeSpec.Builder, methods : List<ExecutableElement>, requestType : Class<out Annotation>) : Boolean {
        classBuilder.apply {
            methods.forEach { eachMethod ->
                if(!eachMethod.returnType.asTypeName().toString().contains("${BLUEBERRY_SHERBET_CORE_PACKAGE_NAME}.request.BlueberryRequest")) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Return Type of Method '${eachMethod.simpleName}' Must be '${BLUEBERRY_SHERBET_CORE_PACKAGE_NAME}.request.BlueberryRequest'")
                    return true
                }

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

                val embededdGenericReturnType = (eachMethod.returnType as DeclaredType).typeArguments[0].asTypeName().javaToKotlinType().let {
                        if(it.toString() == "*") Any::class.asTypeName()
                        else it
                    }
                val returnTypeBlueberryRequestClass = ClassName("$BLUEBERRY_SHERBET_CORE_PACKAGE_NAME.request", "BlueberryRequest").parameterizedBy(embededdGenericReturnType)

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
                            """
                                return ${returnTypeBlueberryRequestClass}(
                                    $blueberryDeviceMemeberProrpertyName,
                                    "$uuidString",
                                    ${requestType.name}::class.java,
                                    $moshiMemberPropertyName,
                                    ${eachMethod.getAnnotation(Priority::class.java)?.priority?:Priority.defaultPriority},
                                    ${parameterName?:"null"},
                                    $embededdGenericReturnType::class.java
                                )
                            """.trimIndent()
                        )
                        .build()
                )


            }
        }
        return false
    }

    private fun errorLog(msg : String) = processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)

    private fun TypeName.javaToKotlinType() : TypeName {
        return when(this) {
            is ParameterizedTypeName -> {
                (rawType.javaToKotlinType() as ClassName).parameterizedBy(
                    *typeArguments.map {
                        it.javaToKotlinType()
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
        private const val BLUEBERRY_SHERBET_CORE_PACKAGE_NAME = "com.gmail.ayteneve93.blueberrysherbetcore"
        private const val BLUEBERRY_SHERBET_ANNOTATIONS_PACKABE_NAME = "com.gmail.ayteneve93.blueberrysherbetannotations"
    }

}