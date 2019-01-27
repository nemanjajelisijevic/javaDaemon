package com.daemonize.daemonprocessor;

import com.daemonize.daemonprocessor.annotations.LogExecutionTime;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
    }

    //construct
    public SideQuestDaemonGenerator(TypeElement classElement) {
        super(classElement);
    }

    public TypeSpec generateDaemon(List<ExecutableElement> sideQuestPrototypeMethods) {

        daemonClassName = ClassName.get(packageName, daemonSimpleName);

        List<Pair<TypeSpec, MethodSpec>> sideQuestInitializedFields = new ArrayList<>();

        for (ExecutableElement method : sideQuestPrototypeMethods) {
            sideQuestInitializedFields.add(
                    createSideQuest(
                            Pair.create(
                                    method,
                                    method.getAnnotation(SideQuest.class)
                            )
                    )
            );
        }

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(
                        Modifier.PUBLIC
                ).addSuperinterface(ParameterizedTypeName.get(daemonInterface, daemonClassName));

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

        FieldSpec daemonEngine = FieldSpec.builder(
                daemonEngineClass,
                daemonEngineString
        ).addModifiers(Modifier.PROTECTED)
        .build();

        daemonClassBuilder.addField(prototype);
        daemonClassBuilder.addField(daemonEngine);

        daemonClassBuilder.addMethod(generateCurrentSideQuestGetter());

        //daemon construct
        MethodSpec daemonConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(consumer, "consumer")
                .addParameter(ClassName.get(classElement.asType()), PROTOTYPE_STRING)
                .addStatement("this.daemonEngine = new $N(consumer).setName(this.getClass().getSimpleName())", daemonEngineSimpleName)
                .addStatement("this.$N = $N", PROTOTYPE_STRING, PROTOTYPE_STRING)
                .build();

        daemonClassBuilder.addMethod(daemonConstructor);

        for (Pair<TypeSpec, MethodSpec> initField : sideQuestInitializedFields) {
            daemonClassBuilder.addMethod(initField.getSecond());
            daemonClassBuilder.addType(initField.getFirst());
        }

        if (autoGenerateApiMethods) {
            List<MethodSpec> daemonApiMethods = generateDaemonApiMethods();
            for (MethodSpec apiMethod : daemonApiMethods) {
                daemonClassBuilder.addMethod(apiMethod);
            }
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
        MethodSpec.Builder sideQuestConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("this.description = \"$N\"", prototypeMethodData.getMethodName());

        if(prototypeMethodData.isVoid()) {
            sideQuestConstructorBuilder.addStatement("setVoid()");
        }

        MethodSpec sideQuestConstructor = sideQuestConstructorBuilder.build();

        MethodSpec pursue =  createPursue(
                prototypeMethodData.getMethodName(),
                prototypeMethodData.getMethodRetTypeName(),
                prototypeMethodData.getArguments(),
                prototypeMethodData.isVoid(),
                prototypeSideQuestMethod.getModifiers().contains(Modifier.STATIC),
                prototypeSideQuestMethod.getAnnotation(LogExecutionTime.class)
        );

        sideQuestBuilder.addMethod(pursue);
        sideQuestBuilder.addMethod(sideQuestConstructor);

        return sideQuestBuilder.build();
    }

    public Pair<TypeSpec, MethodSpec> createSideQuest(Pair<ExecutableElement, SideQuest> sideQuestMethod) {

        String methodName = sideQuestMethod.getFirst().getSimpleName().toString();
        int sleep = sideQuestMethod.getSecond().SLEEP();
        TypeSpec sideQuest = createSideQuest(sideQuestMethod.getFirst());

        //TODO DRY
        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(sideQuestMethod.getFirst());
        TypeName sideQuestOfRet = ParameterizedTypeName.get(
                ClassName.get(QUEST_PACKAGE, QUEST_TYPE_NAME),
                prototypeMethodData.getMethodRetTypeName()
        );

        MethodSpec sideQuestSetter = MethodSpec.methodBuilder("set" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + "SideQuest")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Prototype method {@link $N#$N}", sideQuestMethod.getFirst().getEnclosingElement().getSimpleName(), sideQuestMethod.getFirst().getSimpleName())
                .returns(sideQuestOfRet)
                .addStatement("$T sideQuest = new $N()", sideQuestOfRet, sideQuest)
                .addStatement(daemonEngineString + ".setSideQuest(sideQuest.setSleepInterval($L))", sleep)
                .addStatement("return sideQuest")
                .build();

        return Pair.create(sideQuest, sideQuestSetter);
    }

    public MethodSpec generateCurrentSideQuestGetter() {
        return  MethodSpec.methodBuilder("getCurrentSideQuest")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(QUEST_PACKAGE, QUEST_TYPE_NAME))
                .addStatement("return this.$N.getSideQuest()", daemonEngineString)
                .build();
    }

}
