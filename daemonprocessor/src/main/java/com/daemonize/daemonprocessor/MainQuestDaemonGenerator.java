package com.daemonize.daemonprocessor;


import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.ConsumerArg;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.LogExecutionTime;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;


public class MainQuestDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    private Set<String> overloadedPrototypeMethods = new TreeSet<>();
    private String currentMainQuestName = "";
    private boolean eager;

    public boolean isEager() {
        return eager;
    }

    private final String VOID_QUEST_TYPE_NAME = "VoidMainQuest";

    private ClassName daemonEngineClass;

    private boolean consumerDaemon = false;

    private String CONSUME_QUEST_TYPE_NAME = "ConsumeQuest";

    public boolean isConsumer() {
        return consumerDaemon;
    }

    private Map<ExecutableElement, Pair<String, FieldSpec>> dedicatedThreadEngines;

    public Map<ExecutableElement, Pair<String, FieldSpec>> getDedicatedThreadEngines() {
        return dedicatedThreadEngines;
    }

    {
        QUEST_TYPE_NAME = "MainQuest";
        daemonPackage = DAEMON_ENGINE_IMPL_PACKAGE + ".mainquestdaemon";
        daemonEngineSimpleName = "MainQuestDaemonEngine";
    }

    public MainQuestDaemonGenerator(TypeElement classElement) {
        this(
                classElement,
                classElement.getAnnotation(Daemonize.class).eager(),
                classElement.getAnnotation(Daemonize.class).consumer()
        );
    }

    public MainQuestDaemonGenerator(
            TypeElement classElement,
            boolean eager,
            boolean consumer
    ) {
        super(classElement);

        this.eager = eager;

        if(this.eager) {
            daemonEngineSimpleName = "EagerMainQuestDaemonEngine";
            daemonInterface = ClassName.get(
                    DAEMON_ENGINE_PACKAGE_ROOT,
                    "EagerDaemon"
            );
        }

        this.daemonEngineClass = ClassName.get(daemonPackage, daemonEngineSimpleName);
        this.dedicatedThreadEngines = new HashMap<>();

        List<Pair<ExecutableElement, DedicatedThread>> dedicatedThreadMethods =
                BaseDaemonGenerator.getDedicatedThreadMethods(BaseDaemonGenerator.getAnnotatedClassMethods(classElement));

        for (Pair<ExecutableElement, DedicatedThread> dedicatedThreadMethod : dedicatedThreadMethods) {

            String daemonEngineDedicatedString =
                    dedicatedThreadMethod.getFirst().getSimpleName().toString() + daemonConcatEngineString;

//            String daemonEngineDedicatedString = dedicatedThreadMethod.getSecond().NAME().isEmpty() ?
//                    dedicatedThreadMethod.getFirst().getSimpleName().toString() + daemonConcatEngineString :
//                    dedicatedThreadMethod.getSecond().NAME() + daemonConcatEngineString;

            dedicatedThreadEngines.put(
                    dedicatedThreadMethod.getFirst(),
                    Pair.create(
                            daemonEngineDedicatedString,
                            FieldSpec.builder(daemonEngineClass, daemonEngineDedicatedString).addModifiers(Modifier.PROTECTED).build()
                    )
            );
        }

        if (!dedicatedThreadMethods.isEmpty())
            autoGenerateApiMethods = false;

        this.consumerDaemon = consumer;
    }

    public TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods) {

        daemonClassName = ClassName.get(packageName, daemonSimpleName);

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(daemonInterface, daemonClassName));

        if (consumerDaemon)
            daemonClassBuilder.addSuperinterface(consumerInterface);

        daemonClassBuilder = addTypeParameters(classElement, daemonClassBuilder);

        Map<TypeSpec, MethodSpec> mainQuestsAndApiMethods = new LinkedHashMap<>();

        for (ExecutableElement method : publicPrototypeMethods) {
            if (method.getAnnotation(CallingThread.class) != null) {
                daemonClassBuilder.addMethod(wrapMethod(method));
                continue;
            }

            if (dedicatedThreadEngines.containsKey(method)) {
                mainQuestsAndApiMethods.put(
                        createMainQuest(method),
                        createApiMethod(
                                method,
                                dedicatedThreadEngines.get(method).getFirst()
                        )
                );
            } else {
                mainQuestsAndApiMethods.put(
                        createMainQuest(method),
                        createApiMethod(method, daemonEngineString)
                );
            }
        }

        //private fields for prototype
        FieldSpec prototype = FieldSpec.builder(
                ClassName.get(classElement.asType()),
                PROTOTYPE_STRING
        ).addModifiers(Modifier.PRIVATE).build();

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
                .addStatement("this." + daemonEngineString + " = new $N(consumer).setName(this.getClass().getSimpleName())", daemonEngineSimpleName);

        //add dedicated daemon engines
        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : dedicatedThreadEngines.entrySet()) {
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

        for (Map.Entry<TypeSpec, MethodSpec> entry : mainQuestsAndApiMethods.entrySet()) {
            daemonClassBuilder.addMethod(entry.getValue());
            daemonClassBuilder.addType(entry.getKey());
        }

        List<MethodSpec> daemonApiMethods;

        if (autoGenerateApiMethods) {
             daemonApiMethods = generateDaemonApiMethods();
        } else {

            daemonApiMethods = new ArrayList<>(9);

            daemonApiMethods.add(generateGetPrototypeDaemonApiMethod());
            daemonApiMethods.add(generateSetPrototypeDaemonApiMethod());
            daemonApiMethods.add(generateStartDaemonApiMethod());
            daemonApiMethods.add(generateDedicatedEnginesStopDaemonApiMethod());
            daemonApiMethods.add(generateDedicatedEnginesQueueStopDaemonApiMethod());
            daemonApiMethods.add(generateDedicatedEnginesSetNameDaemonApiMethod());
            daemonApiMethods.add(generateGetNameDaemonApiMethod());
            daemonApiMethods.add(generateSetConsumerDaemonApiMethod());

        }

        if (consumerDaemon)
            daemonApiMethods.add(generateConsumeMethod());

        if (eager) {
            daemonApiMethods.add(generateInterruptMethod());
            daemonApiMethods.add(generateClearAndInterruptMethod());
        }

        for (MethodSpec apiMethod : daemonApiMethods) {
            daemonClassBuilder.addMethod(apiMethod);
        }

        return daemonClassBuilder.build();
    }


    public TypeSpec createMainQuest(ExecutableElement prototypeMethod){

        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(prototypeMethod);

        boolean voidWithRunnable = prototypeMethodData.isVoid() && prototypeMethod.getAnnotation(GenerateRunnable.class) != null;

        ClassName className = voidWithRunnable ? ClassName.get(QUEST_PACKAGE, VOID_QUEST_TYPE_NAME) : ClassName.get(QUEST_PACKAGE, QUEST_TYPE_NAME);

        TypeName mainQuestOfRet = ParameterizedTypeName.get(
                className,
                prototypeMethodData.getMethodRetTypeName()
        );

        String mainQuestName = Character.valueOf(
                prototypeMethodData.getMethodName().charAt(0)
        ).toString().toUpperCase() + prototypeMethodData.getMethodName().substring(1);

        //check for overloaded methods
        while (overloadedPrototypeMethods.contains(mainQuestName)) {
            mainQuestName += "I";
        }

        currentMainQuestName = mainQuestName;
        overloadedPrototypeMethods.add(mainQuestName);

        TypeSpec.Builder mainQuestBuilder = TypeSpec.classBuilder(
                mainQuestName + QUEST_TYPE_NAME
        ).superclass(mainQuestOfRet).addModifiers(Modifier.PRIVATE, Modifier.FINAL);

        mainQuestBuilder = addTypeParameters(prototypeMethod, mainQuestBuilder);

        //MainQuest construct
        MethodSpec.Builder mainQuestConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);

        //add parameters
        for (Pair<TypeName, String> parameter : prototypeMethodData.getParameters()){
            mainQuestBuilder.addField(parameter.getFirst(), parameter.getSecond(),Modifier.PRIVATE);
            mainQuestConstructorBuilder.addParameter(parameter.getFirst(), parameter.getSecond());

        }

        if(!prototypeMethodData.isVoid()) {
            mainQuestConstructorBuilder.addParameter(
                    prototypeMethodData.getClosureOfRet(),
                    "closure"
            );
            mainQuestConstructorBuilder.addStatement("super(closure)");
        } else {
            if (voidWithRunnable) {
                mainQuestConstructorBuilder.addParameter(TypeName.get(Runnable.class), "retRun");
                mainQuestConstructorBuilder.addStatement("super(retRun)");
            } else
                mainQuestConstructorBuilder.addStatement("setVoid()");
        }

        for (Pair<TypeName, String> parameter : prototypeMethodData.getParameters()){
            mainQuestConstructorBuilder.addStatement("this.$N = $N", parameter.getSecond(), parameter.getSecond());
        }

        mainQuestConstructorBuilder.addStatement(
                "this.description = \"$N\"",
                prototypeMethodData.getMethodName()
        );

        MethodSpec pursue = createPursue(
                prototypeMethodData.getMethodName(),
                prototypeMethodData.getMethodRetTypeName(),
                prototypeMethodData.getArguments(),
                prototypeMethodData.isVoid(),
                prototypeMethod.getModifiers().contains(Modifier.STATIC),
                prototypeMethod.getAnnotation(LogExecutionTime.class)
        );

        MethodSpec mainQuestConstructor = mainQuestConstructorBuilder.build();

        mainQuestBuilder.addMethod(mainQuestConstructor);
        mainQuestBuilder.addMethod(pursue);

        return mainQuestBuilder.build();
    }

    public MethodSpec createApiMethod(ExecutableElement prototypeMethod, String daemonEngineString) {

        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(prototypeMethod);
        MethodSpec.Builder apiMethodBuilder = MethodSpec.methodBuilder(prototypeMethodData.getMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(
                        "Prototype method {@link $N#$N}",
                        classElement.getNestingKind().equals(NestingKind.MEMBER)
                                ? prototypeMethod.getEnclosingElement().toString()
                                : prototypeMethod.getEnclosingElement().getSimpleName(),
                        prototypeMethod.getSimpleName()
                );

        boolean voidWithRunnable = prototypeMethodData.isVoid() && prototypeMethod.getAnnotation(GenerateRunnable.class) != null;
        boolean consumerArg = prototypeMethod.getAnnotation(ConsumerArg.class) != null;

        apiMethodBuilder = addTypeParameters(prototypeMethod, apiMethodBuilder);

        for (Pair<TypeName, String> field : prototypeMethodData.getParameters()) {
            apiMethodBuilder.addParameter(field.getFirst(), field.getSecond());
        }

        //add consumer argument
        if (consumerArg) {
            apiMethodBuilder.addParameter(consumer,"consumer");
        }

        if (!prototypeMethodData.isVoid()) {

            apiMethodBuilder.addParameter(prototypeMethodData.getClosureOfRet(),"closure");
            apiMethodBuilder.addStatement(
                    daemonEngineString + ".pursueQuest(new "
                            + currentMainQuestName + QUEST_TYPE_NAME + "("
                            + (prototypeMethodData.getArguments().isEmpty() ? "" :  prototypeMethodData.getArguments() + ", ")
                            + "closure)"
                            + (consumerArg ? ".setConsumer(consumer))" : ".setConsumer(" + daemonEngineString + ".getConsumer()))")

            );
        } else {

            if (voidWithRunnable) {

                apiMethodBuilder.addParameter(TypeName.get(Runnable.class), "retRun");

                apiMethodBuilder.addStatement(
                        daemonEngineString + ".pursueQuest(new "
                                + currentMainQuestName + QUEST_TYPE_NAME + "("
                                + (prototypeMethodData.getArguments().isEmpty() ? "" :  prototypeMethodData.getArguments() + ", ") + "retRun)"
                                + (consumerArg ? ".setConsumer(consumer))" : ".setConsumer(" + daemonEngineString + ".getConsumer()))")
                );
            } else
                apiMethodBuilder.addStatement(
                        daemonEngineString + ".pursueQuest(new "
                                + currentMainQuestName + QUEST_TYPE_NAME + "("
                                + prototypeMethodData.getArguments() + ")"
                                + (consumerArg ? ".setConsumer(consumer))" : ".setConsumer(" + daemonEngineString + ".getConsumer()))")
                );
        }

        apiMethodBuilder.addStatement("return this");
        apiMethodBuilder.returns(ClassName.get(packageName, daemonSimpleName));

        return apiMethodBuilder.build();
    }

    public MethodSpec wrapMethod(ExecutableElement prototypeMethod){

        PrototypeMethodData methodData = new PrototypeMethodData(prototypeMethod);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(
                prototypeMethod.getSimpleName().toString()
        ).addModifiers(Modifier.PUBLIC);

        methodBuilder = BaseDaemonGenerator.addTypeParameters(prototypeMethod, methodBuilder);

        for(Pair<TypeName, String> param : methodData.getParameters()) {
            methodBuilder.addParameter(param.getFirst(), param.getSecond());
        }

        for ( TypeMirror exception : prototypeMethod.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(exception));
        }


        if (methodData.isVoid())
            methodBuilder.returns(ClassName.get(packageName, daemonSimpleName)).addStatement("prototype."
                    + prototypeMethod.getSimpleName().toString()
                    + "(" + methodData.getArguments()
                    + ")").addStatement("return this");
        else
            methodBuilder.returns(TypeName.get(prototypeMethod.getReturnType())).addStatement(
                    "return prototype."
                        + prototypeMethod.getSimpleName().toString()
                        + "(" + methodData.getArguments()
                        + ")");

        return methodBuilder.build();
    }

    @Override
    public MethodSpec generateStartDaemonApiMethod() {
        MethodSpec.Builder methodSpecBuilder =  MethodSpec.methodBuilder("start")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".start()");

        for (Pair<String, FieldSpec> dedicatedEngine : getDedicatedThreadEngines().values())
            methodSpecBuilder.addStatement(dedicatedEngine.getFirst() + ".start()");

        return methodSpecBuilder.addStatement("return this").build();
    }

    public MethodSpec generateDedicatedEnginesStopDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("stop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement(daemonEngineString + ".stop()");

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : dedicatedThreadEngines.entrySet()) {
            builder.addStatement( entry.getValue().getFirst() + ".stop()");

        }

        return builder.build();
    }

    public MethodSpec generateDedicatedEnginesQueueStopDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("queueStop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".queueStop(this)");

//        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : dedicatedThreadEngines.entrySet()) {
//            builder.addStatement( entry.getValue().getFirst() + ".stop()");
//
//        }

        builder.addStatement("return this");

        return builder.build();
    }

    public MethodSpec generateDedicatedEnginesSetNameDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setName")
                .addParameter(String.class, "name")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".setName(name)");

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : dedicatedThreadEngines.entrySet()) {
            builder.addStatement(entry.getValue().getFirst() + ".setName(name +\" - " + entry.getValue().getFirst() + "\")");
        }

        return  builder.addStatement("return this")
               .build();
    }


    public MethodSpec generateConsumeMethod(){
        return MethodSpec.methodBuilder("consume")
                .addParameter(TypeName.get(Runnable.class), "runnable")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return " + daemonEngineString + ".pursueQuest(new $T(runnable))", ClassName.get(QUEST_PACKAGE, CONSUME_QUEST_TYPE_NAME))
                .build();
    }

    public MethodSpec generateInterruptMethod(){
        MethodSpec.Builder builder = MethodSpec.methodBuilder("interrupt")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".interrupt()");

        for (Pair<String, FieldSpec> dedicatedEngine : getDedicatedThreadEngines().values())
            builder.addStatement(dedicatedEngine.getFirst() + ".interrupt()");

        return builder.addStatement("return this")
                .build();
    }

    public MethodSpec generateClearAndInterruptMethod(){
        MethodSpec.Builder builder = MethodSpec.methodBuilder("clearAndInterrupt")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".clearAndInterrupt()");

        for (Pair<String, FieldSpec> dedicatedEngine : getDedicatedThreadEngines().values())
            builder.addStatement(dedicatedEngine.getFirst() + ".clearAndInterrupt()");

        return builder.addStatement("return this")
                .build();
    }

    public MethodSpec generateGetEnginesStateDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEnginesState")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName))
                .addStatement("$T ret = new $T()", ParameterizedTypeName.get(ClassName.get(List.class), daemonStateClassName), ParameterizedTypeName.get(ClassName.get(ArrayList.class), daemonStateClassName))
                .addStatement("ret.add(" + getDaemonEngineString() + ".getState())");

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : getDedicatedThreadEngines().entrySet())
            builder.addStatement("ret.add(" + entry.getValue().getFirst() + ".getState())");

        return builder.addStatement("return ret").build();
    }

    public MethodSpec generateGetEnginesQueueSizeDaemonApiMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getEnginesQueueSizes")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Integer.class)))
                .addStatement("$T ret = new $T()", ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(Integer.class)), ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(Integer.class)))
                .addStatement("ret.add(" + getDaemonEngineString() + ".queueSize())");

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : getDedicatedThreadEngines().entrySet())
            builder.addStatement("ret.add(" + entry.getValue().getFirst() + ".queueSize())");

        return builder.addStatement("return ret")
                .build();
    }

}
