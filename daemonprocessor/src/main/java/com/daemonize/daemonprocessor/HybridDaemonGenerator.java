package com.daemonize.daemonprocessor;


import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class HybridDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    protected MainQuestDaemonGenerator mainGenerator;
    protected SideQuestDaemonGenerator sideGenerator;

    {
        daemonPackage = DAEMON_ENGINE_IMPL_PACKAGE;
        daemonEngineSimpleName = "HybridDaemonEngine";
    }

    public HybridDaemonGenerator(TypeElement classElement) {
        super(classElement);
        this.mainGenerator = new MainQuestDaemonGenerator(
                classElement,
                false,
                classElement.getAnnotation(Daemonize.class).consumer(),
                classElement.getAnnotation(Daemonize.class).markDaemonMethods()
        );
        this.sideGenerator = new SideQuestDaemonGenerator(classElement);

        if(!mainGenerator.getDedicatedThreadEngines().isEmpty())
            autoGenerateApiMethods = false;
    }

    @Override
    public TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods) {

        daemonClassName = ClassName.get(packageName, daemonSimpleName);

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(
                        Modifier.PUBLIC
                ).addSuperinterface(ParameterizedTypeName.get(daemonInterface, daemonClassName));

        implementInterfaces(daemonClassBuilder, daemonClassName.box());

//        for (TypeElement intf : interfaces) {
//            TypeMirror intfMirror = intf.asType();
//
// //           List<? extends TypeParameterElement> typeParams = intf.getTypeParameters();
//
////            if (!typeParams.isEmpty()) {
////
////                for (TypeParameterElement type : typeParams) {
////                    daemonClassBuilder.addTypeVariable(TypeVariableName.get(type));
////                }
////            }
//            daemonClassBuilder.addSuperinterface(TypeName.get(intf.asType()));
//        }

//        for (TypeElement intf : interfaces)
//            daemonClassBuilder.addSuperinterface(TypeName.get(intf.asType()));

        //daemonClassBuilder.addSuperinterfaces(interfaces);

        if (mainGenerator.isConsumer())
            daemonClassBuilder.addSuperinterface(consumerInterface);

        daemonClassBuilder = addTypeParameters(classElement, daemonClassBuilder);

        //private fields for DaemonEngine and prototype
        FieldSpec prototype = FieldSpec.builder(
                ClassName.get(classElement.asType()),
                PROTOTYPE_STRING
        ).addModifiers(Modifier.PRIVATE).build();

        ClassName daemonEngineClass = ClassName.get(
                daemonPackage,
                daemonEngineSimpleName
        );

        //private fields for main daemon engine
        FieldSpec daemonEngine = FieldSpec.builder(daemonEngineClass, daemonEngineString)
                .addModifiers(Modifier.PROTECTED)
                .build();

        daemonClassBuilder.addField(prototype);
        daemonClassBuilder.addField(daemonEngine);

        //daemon construct
        MethodSpec.Builder daemonConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(consumer, "consumer")
                .addParameter(ClassName.get(classElement.asType()), PROTOTYPE_STRING)
                .addStatement("this.daemonEngine = new $N(consumer).setName(this.getClass().getSimpleName())", daemonEngineSimpleName);

        //add dedicated daemon engines
        Set<String> dedNameSet = new HashSet<>(mainGenerator.dedicatedEnginesNameSet);

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : mainGenerator.getDedicatedThreadEngines().entrySet()) {
            if (dedNameSet.contains(entry.getValue().getFirst())) {
                daemonClassBuilder.addField(entry.getValue().getSecond());
                daemonConstructorBuilder.addStatement(
                        "this." + entry.getValue().getFirst() +
                                " = new $N(consumer).setName(this.getClass().getSimpleName() + \" - "
                                + entry.getValue().getFirst() + "\")",
                        daemonEngineSimpleName
                );
                dedNameSet.remove(entry.getValue().getFirst());
            }
        }

        MethodSpec daemonConstructor = daemonConstructorBuilder
                .addStatement("this.$N = $N", PROTOTYPE_STRING, PROTOTYPE_STRING)
                .build();

        daemonClassBuilder.addMethod(daemonConstructor);

        List<Pair<ExecutableElement, SideQuest>> sideQuests
                = getSideQuestMethods(publicPrototypeMethods);

        List<Pair<TypeSpec, MethodSpec>> sideQuestFields = new ArrayList<>();

        for (Pair<ExecutableElement, SideQuest> sideQuestPair : sideQuests) {
            sideQuestFields.add(sideGenerator.createSideQuest(sideQuestPair));
        }

        if (!sideQuestFields.isEmpty()) {
            daemonClassBuilder.addMethod(sideGenerator.generateCurrentSideQuestGetter());
        }

        //add side quest setters
        for (Pair<TypeSpec, MethodSpec> sideQuestField : sideQuestFields) {
            daemonClassBuilder.addMethod(sideQuestField.getSecond());
        }

        Map<TypeSpec, MethodSpec> mainQuestsAndApiMethods = new LinkedHashMap<>();

        for (ExecutableElement method : publicPrototypeMethods) {

            PrototypeMethodData overridenMethodData = new PrototypeMethodData(method);

            if (method.getAnnotation(CallingThread.class) != null || overriddenMethods.contains(overridenMethodData)) {
                if (method.getAnnotation(CallingThread.class) != null || overriddenMethods.contains(overridenMethodData)) {
                    daemonClassBuilder.addMethod(overriddenMethods.contains(overridenMethodData) ? mainGenerator.wrapIntfMethod(method) : mainGenerator.wrapMethod(method));
                    continue;
                }
                continue;
            }

            if (mainGenerator.getDedicatedThreadEngines().containsKey(method)) {
                mainQuestsAndApiMethods.put(
                        mainGenerator.createMainQuest(method),
                        mainGenerator.createApiMethod(
                                method,
                                mainGenerator.getDedicatedThreadEngines().get(method).getFirst()
                        )
                );
            } else {
                mainQuestsAndApiMethods.put(
                        mainGenerator.createMainQuest(method),
                        mainGenerator.createApiMethod(method, daemonEngineString)
                );
            }
        }

        //add side quests
        for (Pair<TypeSpec, MethodSpec> sideQuestField : sideQuestFields) {
            daemonClassBuilder.addType(sideQuestField.getFirst());
        }

        //add main quest methods
        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addMethod(entry.getValue());
        }

        //Add API METHODS
        List<MethodSpec> daemonApiMethods;

//        if (autoGenerateApiMethods) {
//            daemonApiMethods = sideGenerator.generateDaemonApiMethods();
//        } else {
            daemonApiMethods = new ArrayList<>(9);

            daemonApiMethods.add(generateGetPrototypeDaemonApiMethod());
            daemonApiMethods.add(generateSetPrototypeDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateStartDaemonApiMethod());

            daemonApiMethods.add(generateClearDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesStopDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesQueueStopDaemonApiMethod());

            daemonApiMethods.add(generateGetEnginesStateDaemonApiMethod());
            daemonApiMethods.add(generateGetEnginesQueueSizeDaemonApiMethod());

            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesSetNameDaemonApiMethod());
            daemonApiMethods.add(generateGetNameDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateSetConsumerDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateGetConsumerDaemonApiMethod());
            daemonApiMethods.add(generateSetUncaughtExceptionHandler());

//        }

        if (mainGenerator.isConsumer())
            daemonApiMethods.add(mainGenerator.generateConsumeMethod());

        for (MethodSpec apiMethod : daemonApiMethods) {
            daemonClassBuilder.addMethod(apiMethod);
        }

        //add main quests
        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addType(entry.getKey());
        }

        return daemonClassBuilder.build();
    }

    @Override
    public MethodSpec generateGetEnginesStateDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEnginesState")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName))
                .addStatement("$T ret = new $T()", ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName), ParameterizedTypeName.get(ClassName.get(ArrayList.class), daemonStateClassName))
                .addStatement("ret.add(" + mainGenerator.getDaemonEngineString() + ".getState())");

        for (String dedEngine : mainGenerator.dedicatedEnginesNameSet)
            builder.addStatement("ret.add(" + dedEngine + ".getState())");

        return builder.addStatement("return ret").build();
    }

    @Override
    public MethodSpec generateGetEnginesQueueSizeDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEnginesQueueSizes")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Integer.class)))
                .addStatement("$T ret = new $T()", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Integer.class)), ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(Integer.class)))
                .addStatement("ret.add(" + mainGenerator.getDaemonEngineString() + ".queueSize())");

        for (String dedEngine : mainGenerator.dedicatedEnginesNameSet)
            builder.addStatement("ret.add(" + dedEngine + ".queueSize())");

        return builder.addStatement("return ret").build();
    }

    @Override
    public MethodSpec generateSetUncaughtExceptionHandler() {
        MethodSpec.Builder builder =  MethodSpec.methodBuilder("setUncaughtExceptionHandler")
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(Thread.UncaughtExceptionHandler.class), "handler")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(mainGenerator.getDaemonEngineString()  + ".setUncaughtExceptionHandler(handler)");

        for (String dedicatedEngine : mainGenerator.dedicatedEnginesNameSet)
            builder.addStatement(dedicatedEngine + ".setUncaughtExceptionHandler(handler)");

        return builder.returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement("return this")
                .build();
    }

    @Override
    public MethodSpec generateClearDaemonApiMethod() {
        return mainGenerator.generateClearDaemonApiMethod();
    }

    @Override
    public MethodSpec generateQueueStopDaemonApiMethod() {
        return mainGenerator.generateDedicatedEnginesQueueStopDaemonApiMethod();
    }
}
