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
        daemonInterface = ClassName.get(
                DAEMON_ENGINE_IMPL_PACKAGE + ".sidequestdaemon",
                "SideQuestDaemon"
        );
    }

    public HybridDaemonGenerator(TypeElement classElement) {
        super(classElement);
        this.mainGenerator = new MainQuestDaemonGenerator(
                classElement,
                false,
                classElement.getAnnotation(Daemonize.class).returnDaemonInstance()
        );
        this.sideGenerator = new SideQuestDaemonGenerator(classElement);
    }

    @Override
    public TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods) {

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(
                        Modifier.PUBLIC
                ).addSuperinterface(daemonInterface);

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

//        ClassName consumer = ClassName.get(
//                CONSUMER_PACKAGE + "." + platform.getImplementationPackage(),
//                platform.getPlatformConsumer()
//        );


        ClassName consumer = ClassName.get(
                CONSUMER_PACKAGE,
                CONSUMER_INTERFACE_STRING
        );

        FieldSpec daemonEngine = FieldSpec.builder(
                daemonEngineClass,
                daemonEngineString
        )
        .addModifiers(Modifier.PROTECTED)
//        .initializer(
//                "new $N(new $T()).setName(this.getClass().getSimpleName())",
//                daemonEngineSimpleName,
//                consumer
//        )
        .build();

        daemonClassBuilder.addField(prototype);
        daemonClassBuilder.addField(daemonEngine);

        //daemon construct
        MethodSpec daemonConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(consumer, "consumer")
                .addParameter(ClassName.get(classElement.asType()), PROTOTYPE_STRING)
                .addStatement("this.daemonEngine = new $N(consumer).setName(this.getClass().getSimpleName())", daemonEngineSimpleName)
                .addStatement("this.$N = $N", PROTOTYPE_STRING, PROTOTYPE_STRING)
                .build();

        daemonClassBuilder.addMethod(daemonConstructor);

        List<Pair<ExecutableElement, SideQuest>> sideQuests
                = getSideQuestMethods(publicPrototypeMethods);

        List<Pair<TypeSpec, MethodSpec>> sideQuestFields = new ArrayList<>();

        for (Pair<ExecutableElement, SideQuest> sideQuestPair : sideQuests) {
            sideQuestFields.add(sideGenerator.createSideQuest(sideQuestPair));
        }

        //add side quest setters
        for (Pair<TypeSpec, MethodSpec> sideQuestField : sideQuestFields) {
            daemonClassBuilder.addMethod(sideQuestField.getSecond());
        }

        Map<TypeSpec, MethodSpec> mainQuestsAndApiMethods = new LinkedHashMap<>();

        for (ExecutableElement method : publicPrototypeMethods) {

            if (method.getAnnotation(CallingThread.class) != null) {
                daemonClassBuilder.addMethod(mainGenerator.copyMethod(method));
                continue;
            }

            mainQuestsAndApiMethods.put(
                    mainGenerator.createMainQuest(method),
                    mainGenerator.createApiMethod(method)
            );
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
        if (autoGenerateApiMethods) {
            List<MethodSpec> apiMethods = sideGenerator.generateDaemonApiMethods();
            for (MethodSpec apiMethod : apiMethods) {
                daemonClassBuilder.addMethod(apiMethod);
            }
        }

        //add main quests
        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addType(entry.getKey());
        }

        return daemonClassBuilder.build();
    }
}
