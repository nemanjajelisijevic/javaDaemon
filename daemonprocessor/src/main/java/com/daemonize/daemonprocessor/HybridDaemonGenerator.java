package com.daemonize.daemonprocessor;


import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class HybridDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    protected MainQuestDaemonGenerator mainGenerator;
    protected SideQuestDaemonGenerator sideGenerator;

    {
        daemonPackage = DAEMON_ENGINE_IMPL_PACKAGE + ".hybriddaemon";
        daemonEngineSimpleName = "HybridDaemonEngine";
    }

    public HybridDaemonGenerator(TypeElement classElement) {
        super(classElement);
        this.mainGenerator = new MainQuestDaemonGenerator(
                classElement,
                false,
                classElement.getAnnotation(Daemonize.class).returnDaemonInstance(),
                classElement.getAnnotation(Daemonize.class).consumer()
        );
        this.sideGenerator = new SideQuestDaemonGenerator(classElement);

        if(!mainGenerator.getDedicatedThreadEngines().isEmpty())
            autoGenerateApiMethods = false;
    }

    @Override
    public TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods) {

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(
                        Modifier.PUBLIC
                ).addSuperinterface(daemonInterface);

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
        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : mainGenerator.getDedicatedThreadEngines().entrySet()) {
            daemonClassBuilder.addField(entry.getValue().getSecond());
            daemonConstructorBuilder.addStatement(
                    "this." + entry.getValue().getFirst() +
                            " = new $N(consumer).setName(this.getClass().getSimpleName() + \" - "
                            + entry.getValue().getFirst() + "\")",
                    daemonEngineSimpleName
            );
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

            if (method.getAnnotation(CallingThread.class) != null) {
                daemonClassBuilder.addMethod(mainGenerator.wrapMethod(method));
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

        if (autoGenerateApiMethods) {
            daemonApiMethods = sideGenerator.generateDaemonApiMethods();
        } else {
            daemonApiMethods = new ArrayList<>(9);

            daemonApiMethods.add(generateGetPrototypeDaemonApiMethod());
            daemonApiMethods.add(generateSetPrototypeDaemonApiMethod());
            daemonApiMethods.add(generateStartDaemonApiMethod());


            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesStopDaemonApiMethod());
            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesQueueStopDaemonApiMethod());

            daemonApiMethods.add(generateGetStateDaemonApiMethod());

            daemonApiMethods.add(mainGenerator.generateDedicatedEnginesSetNameDaemonApiMethod());
            daemonApiMethods.add(generateGetNameDaemonApiMethod());
            daemonApiMethods.add(generateSetConsumerDaemonApiMethod());

        }

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
}
