package com.daemonize.daemonprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class SideQuestDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    private Set<String> overloadedSideQuestPrototypeMethods = new TreeSet<>();

    {
        QUEST_TYPE_NAME = "SideQuest";
        daemonPackage = DAEMON_ENGINE_IMPL_PACKAGE + ".sidequestdaemon";
        daemonEngineSimpleName = "SideQuestDaemonEngine";
        daemonInterface = ClassName.get(daemonPackage, "SideQuestDaemon");
    }

    //construct
    public SideQuestDaemonGenerator(TypeElement classElement) {
        super(classElement);
    }

    public TypeSpec generateDaemon(List<ExecutableElement> sideQuestPrototypeMethods) {
        List<Pair<TypeSpec, FieldSpec>> sideQuestInitializedFields = new ArrayList<>();

        for (ExecutableElement method : sideQuestPrototypeMethods) {
            sideQuestInitializedFields.add(
                    createSideQuestField(
                            Pair.create(
                                    method,
                                    method.getAnnotation(SideQuest.class)
                            )
                    )
            );
        }

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

        for (Pair<TypeSpec, FieldSpec> initField : sideQuestInitializedFields) {
            daemonClassBuilder.addField(initField.getSecond());
            daemonClassBuilder.addType(initField.getFirst());
        }

        List<MethodSpec> daemonApiMethods = generateDaemonApiMethods();

        for (MethodSpec apiMethod : daemonApiMethods) {
            daemonClassBuilder.addMethod(apiMethod);
        }


        return daemonClassBuilder.build();
    }

    private TypeSpec createSideQuest(ExecutableElement prototypeSideQuestMethod) {

        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(prototypeSideQuestMethod);

        if (!prototypeMethodData.getParameters().isEmpty()) {
            throw new IllegalStateException(
                    prototypeSideQuestMethod.getEnclosingElement().toString()
                            + " - " + prototypeMethodData.getMethodName()
                            + " SideQuest cannot have any method parameters"
            );
        }

        //build sideQuestQuest
        ClassName sideQuestClassName = ClassName.get(QUEST_PACKAGE, QUEST_TYPE_NAME);
        TypeName sideQuestOfRet = ParameterizedTypeName.get(
                sideQuestClassName,
                prototypeMethodData.getMethodRetTypeName()
        );

        String sideQuestName = Character.valueOf(
                prototypeMethodData.getMethodName().charAt(0)
        ).toString().toUpperCase() + prototypeMethodData.getMethodName().substring(1);

        //check for overloaded methods
        while (overloadedSideQuestPrototypeMethods.contains(sideQuestName)) {
            sideQuestName += "I";
        }
        overloadedSideQuestPrototypeMethods.add(sideQuestName);

        TypeSpec.Builder sideQuestBuilder = TypeSpec.classBuilder(sideQuestName + QUEST_TYPE_NAME)
                .superclass(sideQuestOfRet)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL);

        //SideQuest construct
        MethodSpec sideQuestConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("this.description = \"$N\"", prototypeMethodData.getMethodName())
                .build();

        MethodSpec pursue =  createPursue(
                prototypeMethodData.getMethodName(),
                prototypeMethodData.getMethodRetTypeName(),
                prototypeMethodData.getArguments(),
                prototypeMethodData.isVoid(),
                prototypeSideQuestMethod.getModifiers().contains(Modifier.STATIC)
        );

        sideQuestBuilder.addMethod(pursue);
        sideQuestBuilder.addMethod(sideQuestConstructor);

        return sideQuestBuilder.build();
    }

    public Pair<TypeSpec, FieldSpec> createSideQuestField(Pair<ExecutableElement, SideQuest> sideQuestMethod) {

        String methodName = sideQuestMethod.getFirst().getSimpleName().toString();
        int sleep = sideQuestMethod.getSecond().SLEEP();
        TypeSpec sideQuest = createSideQuest(sideQuestMethod.getFirst());

        FieldSpec sideQuestField = FieldSpec.builder(sideQuest.superclass, methodName + QUEST_TYPE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .initializer("new $N().setSleepInterval($L)", sideQuest, sleep)
                .build();

        return Pair.create(sideQuest, sideQuestField);
    }

    @Override
    public  List<MethodSpec> generateDaemonApiMethods() {

        List<MethodSpec> ret = super.generateDaemonApiMethods();

        ret.add(
                MethodSpec.methodBuilder("setSideQuest")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addParameter(ClassName.get(QUEST_PACKAGE, "SideQuest"), "sideQuest")
                        .addStatement(DAEMON_ENGINE_STRING + ".setSideQuest($N)", "sideQuest")
                        .build()
        );

        ret.add(
                MethodSpec.methodBuilder("getSideQuest")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(QUEST_PACKAGE, "SideQuest"))
                        .addStatement("return " + DAEMON_ENGINE_STRING + ".getSideQuest()")
                        .build()
        );

        return ret;
    }


}
