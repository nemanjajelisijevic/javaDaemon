package com.daemonize.daemonprocessor;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;

public class HybridDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    private MainQuestDaemonGenerator mainGenerator;
    private SideQuestDaemonGenerator sideGenerator;

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
                        Modifier.PUBLIC,
                        Modifier.FINAL
                ).addSuperinterface(daemonInterface);

        daemonClassBuilder = addTypeParameters(classElement, daemonClassBuilder);

        //private fields for DaemonEngine and prototype
        FieldSpec prototype = FieldSpec.builder(
                ClassName.get(classElement.asType()),
                PROTOYPE_STRING
        ).addModifiers(Modifier.PRIVATE).build();

        ClassName daemonEngineClass = ClassName.get(
                daemonPackage,
                daemonEngineSimpleName
        );

        FieldSpec daemonEngine = FieldSpec.builder(
                daemonEngineClass,
                DAEMON_ENGINE_STRING
        ).addModifiers(Modifier.PRIVATE).initializer(
                "new $N().setName(this.getClass().getSimpleName())",
                daemonEngineSimpleName
        ).build();

        daemonClassBuilder.addField(prototype);
        daemonClassBuilder.addField(daemonEngine);

        //daemon construct
        MethodSpec daemonConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(classElement.asType()), PROTOYPE_STRING)
                .addStatement("this.$N = $N", PROTOYPE_STRING, PROTOYPE_STRING)
                .build();

        daemonClassBuilder.addMethod(daemonConstructor);

        List<Pair<ExecutableElement, SideQuest>> sideQuests
                = getSideQuestMethods(publicPrototypeMethods);

        List<Pair<TypeSpec, FieldSpec>> sideQuestFields = new ArrayList<>();

        for (Pair<ExecutableElement, SideQuest> sideQuestPair : sideQuests) {
            sideQuestFields.add(sideGenerator.createSideQuestField(sideQuestPair));
        }

        Map<TypeSpec, MethodSpec> mainQuestsAndApiMethods = new LinkedHashMap<>();

        for (ExecutableElement method : publicPrototypeMethods) {

            if (method.getAnnotation(CallingThread.class) != null) {
                System.out.println("@CallingThread - PROTOTYPE: " + method.getEnclosingElement().asType().toString() + ", METHOD: " + method.toString());
                daemonClassBuilder.addMethod(mainGenerator.copyMethod(method));
                continue;
            }

            mainQuestsAndApiMethods.put(
                    mainGenerator.createMainQuest(method),
                    mainGenerator.createApiMethod(method)
            );
        }

        //add side quest fields
        for (Pair<TypeSpec, FieldSpec> sideQuestField : sideQuestFields) {
            daemonClassBuilder.addField(sideQuestField.getSecond());
        }

        //add main quest methods
        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addMethod(entry.getValue());
        }

        //Add API METHODS
        List<MethodSpec> apiMethods = sideGenerator.generateDaemonApiMethods();

        for(MethodSpec apiMethod : apiMethods) {
            daemonClassBuilder.addMethod(apiMethod);
        }

        //add side quests
        for (Pair<TypeSpec, FieldSpec> sideQuestField : sideQuestFields) {
            daemonClassBuilder.addType(sideQuestField.getFirst());
        }

        //add main quests
        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addType(entry.getKey());
        }

        return daemonClassBuilder.build();
    }
}
