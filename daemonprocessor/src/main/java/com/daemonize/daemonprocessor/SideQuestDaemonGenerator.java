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
    private final String SIDE_QUEST_SLEEP = "SleepSideQuest";
    private final String INTERRUPTIBLE_SIDE_QUEST = "InterruptibleSideQuest";
    private final String SIDE_QUEST_SLEEP_INTERRUPTIBLE = "InterruptibleSleepSideQuest";

    {
        questTypeName = "SideQuest";
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
                DAEMON_ENGINE_IMPL_PACKAGE,
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
                .addParameter(ClassName.get(classElement.asType()), PROTOTYPE_STRING)
                .addStatement("this.daemonEngine = new $N().setName(this.getClass().getSimpleName())", daemonEngineSimpleName)
                .addStatement("this.$N = $N", PROTOTYPE_STRING, PROTOTYPE_STRING)
                .build();

        daemonClassBuilder.addMethod(daemonConstructor);

        for (Pair<TypeSpec, MethodSpec> initField : sideQuestInitializedFields) {
            daemonClassBuilder.addMethod(initField.getSecond());
            daemonClassBuilder.addType(initField.getFirst());
        }


        List<MethodSpec> daemonApiMethods = new ArrayList<>(9);

        daemonApiMethods.add(generateGetPrototypeDaemonApiMethod());
        daemonApiMethods.add(generateSetPrototypeDaemonApiMethod());

        daemonApiMethods.add(generateStartDaemonApiMethod());
        daemonApiMethods.add(generateStopDaemonApiMethod());
        daemonApiMethods.add(generateClearDaemonApiMethod());
        daemonApiMethods.add(generateQueueStopDaemonApiMethod());

        daemonApiMethods.add(generateGetEnginesStateDaemonApiMethod());

        daemonApiMethods.add(generateSetNameDaemonApiMethod());
        daemonApiMethods.add(generateGetNameDaemonApiMethod());

        daemonApiMethods.add(generateSetConsumerDaemonApiMethod());
        daemonApiMethods.add(generateGetConsumerDaemonApiMethod());
        daemonApiMethods.add(generateSetUncaughtExceptionHandler());

        for (MethodSpec apiMethod : daemonApiMethods) {
                daemonClassBuilder.addMethod(apiMethod);
        }

        return daemonClassBuilder.build();
    }

    private TypeSpec createSideQuest(ExecutableElement prototypeSideQuestMethod, long sleep, boolean interruptible) {

        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(prototypeSideQuestMethod);

        if (!prototypeMethodData.getParameters().isEmpty()) {
            throw new IllegalStateException(
                    prototypeSideQuestMethod.getEnclosingElement().toString()
                            + " - " + prototypeMethodData.getMethodName()
                            + " SideQuest cannot have any method parameters"
            );
        }

        //build sideQuestQuest
        ClassName sideQuestClassName = sleep > 0 ?( interruptible ? ClassName.get(QUEST_PACKAGE, SIDE_QUEST_SLEEP_INTERRUPTIBLE) : ClassName.get(QUEST_PACKAGE, SIDE_QUEST_SLEEP)) : (interruptible ? ClassName.get(QUEST_PACKAGE, INTERRUPTIBLE_SIDE_QUEST) : ClassName.get(QUEST_PACKAGE, questTypeName));
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

        TypeSpec.Builder sideQuestBuilder = TypeSpec.classBuilder(sideQuestName + questTypeName)
                .superclass(sideQuestOfRet)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL);

        //SideQuest construct
        MethodSpec.Builder sideQuestConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(
                        ClassName.get(
                                BaseDaemonGenerator.CLOSURE_PACKAGE,
                                BaseDaemonGenerator.CLOSURE_WAITER_STRING
                        ), "closureAwaiter")
                .addStatement("super(closureAwaiter)")
                .addStatement("this.description = \"$N\"", prototypeMethodData.getMethodName());

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

        long sleep = sideQuestMethod.getSecond().SLEEP();
        boolean interruptible = sideQuestMethod.getSecond().interruptible();
        boolean blockingClosure = sideQuestMethod.getSecond().blockingClosure();

        if (sleep < 0)
            throw new IllegalStateException("Sleep annotation parameter on method: " + methodName + " can not be less than 0 ms.");

        TypeSpec sideQuest = createSideQuest(sideQuestMethod.getFirst(), sleep, interruptible);

        //TODO DRY
        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(sideQuestMethod.getFirst());
        TypeName sideQuestOfRet = sleep == 0 ?

                interruptible ?
                        ParameterizedTypeName.get(
                                ClassName.get(QUEST_PACKAGE, INTERRUPTIBLE_SIDE_QUEST),
                                prototypeMethodData.getMethodRetTypeName()
                        )
                        :
                        ParameterizedTypeName.get(
                                ClassName.get(QUEST_PACKAGE, questTypeName),
                                prototypeMethodData.getMethodRetTypeName()
                        )

        :
                interruptible ?
                        ParameterizedTypeName.get(
                                ClassName.get(QUEST_PACKAGE, SIDE_QUEST_SLEEP_INTERRUPTIBLE),
                                prototypeMethodData.getMethodRetTypeName()
                        )
                        :
                        ParameterizedTypeName.get(
                                ClassName.get(QUEST_PACKAGE, SIDE_QUEST_SLEEP),
                                prototypeMethodData.getMethodRetTypeName()
                        );

        MethodSpec.Builder sideQuestSetter = MethodSpec.methodBuilder("set" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1) + "SideQuest")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Prototype method {@link $N#$N}", sideQuestMethod.getFirst().getEnclosingElement().getSimpleName(), sideQuestMethod.getFirst().getSimpleName())
                .addParameter(consumer, "consumer")
                .returns(sideQuestOfRet)
                .addStatement("$T sideQuest = new $N(" + (blockingClosure ? daemonEngineString + ".getClosureAwaiter()" : "null" ) +  ")", sideQuestOfRet, sideQuest);

        if (sleep > 0)
            sideQuestSetter.addStatement(daemonEngineString + ".setSideQuest(sideQuest.setSleepInterval($L).setConsumer(consumer))", sleep);
        else
            sideQuestSetter.addStatement(daemonEngineString + ".setSideQuest(sideQuest.setConsumer(consumer))");

        sideQuestSetter.addStatement("return sideQuest");

        return Pair.create(sideQuest, sideQuestSetter.build());
    }

    public MethodSpec generateCurrentSideQuestGetter() {
        return  MethodSpec.methodBuilder("getCurrentSideQuest")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(QUEST_PACKAGE, questTypeName))
                .addStatement("return this.$N.getSideQuest()", daemonEngineString)
                .build();
    }


    @Override
    public MethodSpec generateGetEnginesStateDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEnginesState")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName))
                .addStatement("$T ret = new $T()", ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName), ParameterizedTypeName.get(ClassName.get(ArrayList.class), daemonStateClassName))
                .addStatement("ret.add(" + getDaemonEngineString() + ".getState())");

        return builder.addStatement("return ret").build();
    }

    @Override
    public MethodSpec generateGetEnginesQueueSizeDaemonApiMethod() {
        return null;
    }

    public MethodSpec generateClearDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("clear")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName));

        return builder.addStatement("return this")
                .build();
    }

    @Override
    public MethodSpec generateQueueStopDaemonApiMethod() {
        return MethodSpec.methodBuilder("queueStop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                //.returns(void.class)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement("stop()")
                .addStatement("return this")
                .build();
    }
}
