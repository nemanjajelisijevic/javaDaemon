package com.daemonize.daemonprocessor;


import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;


public class MainQuestDaemonGenerator extends BaseDaemonGenerator implements DaemonGenerator {

    private Set<String> overloadedPrototypeMethods = new TreeSet<>();
    private String currentMainQuestName = "";
    private boolean returnInstance;

    private ClassName daemonEngineClass;
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
                classElement.getAnnotation(Daemonize.class).returnDaemonInstance()
        );
    }

    public MainQuestDaemonGenerator(
            TypeElement classElement,
            boolean eager,
            boolean returnInstance
    ) {
        super(classElement);
        if(eager) {
            daemonEngineSimpleName = "EagerMainQuestDaemonEngine";
        }
        this.returnInstance = returnInstance;

        this.daemonEngineClass = ClassName.get(daemonPackage, daemonEngineSimpleName);
        this.dedicatedThreadEngines = new HashMap<>();

        List<Pair<ExecutableElement, DedicatedThread>> dedicatedThreadMethods =
                BaseDaemonGenerator.getDedicatedThreadMethods(BaseDaemonGenerator.getAnnotatedClassMethods(classElement));

        for (Pair<ExecutableElement, DedicatedThread> dedicatedThreadMethod : dedicatedThreadMethods) {

            String daemonEngineDedicatedString = dedicatedThreadMethod.getFirst().getSimpleName().toString() + daemonConcatEngineString;

            dedicatedThreadEngines.put(
                    dedicatedThreadMethod.getFirst(),
                    Pair.create(
                            daemonEngineDedicatedString,
                            FieldSpec.builder(daemonEngineClass, daemonEngineDedicatedString).addModifiers(Modifier.PROTECTED).build()
                    )
            );
        }

        if(!dedicatedThreadMethods.isEmpty())
            autoGenerateApiMethods = false;

    }

    public TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods) {

        TypeSpec.Builder daemonClassBuilder = TypeSpec.classBuilder(daemonSimpleName)
                .addModifiers(
                        Modifier.PUBLIC
                ).addSuperinterface(daemonInterface);

        daemonClassBuilder = addTypeParameters(classElement, daemonClassBuilder);

        Map<TypeSpec, MethodSpec> mainQuestsAndApiMethods = new LinkedHashMap<>();

        for (ExecutableElement method : publicPrototypeMethods) {
            if (method.getAnnotation(CallingThread.class) != null) {
                daemonClassBuilder.addMethod(copyMethod(method));
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

            daemonApiMethods.add(generateGetStateDaemonApiMethod());

            daemonApiMethods.add(generateDedicatedEnginesSetNameDaemonApiMethod());
            daemonApiMethods.add(generateGetNameDaemonApiMethod());
            daemonApiMethods.add(generateSetConsumerDaemonApiMethod());

        }

        for (MethodSpec apiMethod : daemonApiMethods) {
            daemonClassBuilder.addMethod(apiMethod);
        }

        return daemonClassBuilder.build();
    }


    public TypeSpec createMainQuest(ExecutableElement prototypeMethod){

        PrototypeMethodData prototypeMethodData = new PrototypeMethodData(prototypeMethod);

        TypeName mainQuestOfRet = ParameterizedTypeName.get(
                ClassName.get(QUEST_PACKAGE, QUEST_TYPE_NAME),
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
                .addModifiers(Modifier.PUBLIC);

        apiMethodBuilder = addTypeParameters(prototypeMethod, apiMethodBuilder);

        for (Pair<TypeName, String> field : prototypeMethodData.getParameters()) {
            apiMethodBuilder.addParameter(field.getFirst(), field.getSecond());
        }

        if (!prototypeMethodData.isVoid()) {

            //add consumer argument
            if (dedicatedThreadEngines.containsKey(prototypeMethod) && prototypeMethod.getAnnotation(DedicatedThread.class).consumerArg()) {
                apiMethodBuilder.addParameter(consumer,"consumer");
                apiMethodBuilder.addStatement(dedicatedThreadEngines.get(prototypeMethod).getFirst() + ".setConsumer(consumer)");
            }

            apiMethodBuilder.addParameter(prototypeMethodData.getClosureOfRet(),"closure");
            apiMethodBuilder.addStatement(
                    daemonEngineString + ".pursueQuest(new "
                            + currentMainQuestName + "MainQuest("
                            + (prototypeMethodData.getArguments().isEmpty() ? "" :  prototypeMethodData.getArguments() + ", ")
                            + "closure))"
            );
        } else {
            apiMethodBuilder.addStatement(
                    daemonEngineString + ".pursueQuest(new "
                            + currentMainQuestName + "MainQuest("
                            + prototypeMethodData.getArguments() + "))"
            );
        }

        if (returnInstance) {
            apiMethodBuilder.addStatement("return this");
            apiMethodBuilder.returns(ClassName.get(packageName, daemonSimpleName));
        } else {
            apiMethodBuilder.returns(void.class);
        }

        return apiMethodBuilder.build();
    }

    public MethodSpec copyMethod(ExecutableElement prototypeMethod){

        PrototypeMethodData methodData = new PrototypeMethodData(prototypeMethod);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(
                prototypeMethod.getSimpleName().toString()
        ).addModifiers(Modifier.PUBLIC).returns(TypeName.get(prototypeMethod.getReturnType()));

        methodBuilder = BaseDaemonGenerator.addTypeParameters(prototypeMethod, methodBuilder);

        for(Pair<TypeName, String> param : methodData.getParameters()) {
            methodBuilder.addParameter(param.getFirst(), param.getSecond());
        }

        for ( TypeMirror exception : prototypeMethod.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(exception));
        }

        return methodBuilder.addStatement(
                (methodData.isVoid() ? "" : "return ")
                                + "prototype."
                                + prototypeMethod.getSimpleName().toString()
                                + "(" + methodData.getArguments()
                                + ")"
        ).build();
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
                .returns(void.class)
                .addStatement(daemonEngineString + ".queueStop()");

        for (Map.Entry<ExecutableElement, Pair<String, FieldSpec>> entry : dedicatedThreadEngines.entrySet()) {
            builder.addStatement( entry.getValue().getFirst() + ".queueStop()");

        }

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

}
