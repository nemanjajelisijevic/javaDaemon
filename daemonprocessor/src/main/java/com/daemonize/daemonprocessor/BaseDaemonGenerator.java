package com.daemonize.daemonprocessor;


import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.LogExecutionTime;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public abstract class BaseDaemonGenerator implements DaemonGenerator {

    @FunctionalInterface
    public static interface Printer {
        void print(String string);
    }

    @Override
    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    //apt logger
    protected Printer printer;

    //package constants and literals
    //closures
    protected static final String CLOSURE_PACKAGE = "com.daemonize.daemonengine.closure";
    protected static final String CLOSURE_STRING = "Closure";
    protected static final String CLOSURE_WAITER_STRING = "ClosureWaiter";

    //engine
    protected static final String DAEMON_ENGINE_PACKAGE_ROOT = "com.daemonize.daemonengine";
    protected static final String DAEMON_ENGINE_IMPL_PACKAGE = DAEMON_ENGINE_PACKAGE_ROOT + ".implementations";

    //quests
    protected static final String QUEST_PACKAGE = "com.daemonize.daemonengine.quests";
    protected static final String STOP_QUEST_TYPE_NAME = "StopMainQuest";
    protected final String VOID_QUEST_TYPE_NAME = "VoidMainQuest";
    protected final String RETURN_VOID_QUEST_TYPE_NAME = "ReturnVoidMainQuest";
    protected static final String CONSUME_QUEST_TYPE_NAME = "ConsumeQuest";
    protected static final String CONSUMER_PACKAGE_ROOT = "com.daemonize.daemonengine.consumer";

    //consumer
    protected static final String CONSUMER_PACKAGE = "com.daemonize.daemonengine.consumer";
    protected static final String CONSUMER_INTERFACE_STRING = "Consumer";

    //utils
    private static final String DAEMONUTILS_PACKAGE = "com.daemonize.daemonengine.utils";
    protected static final ClassName DAEMON_UTILS_CLASSNAME = ClassName.get(DAEMONUTILS_PACKAGE, "DaemonUtils");
    private static final ClassName TIMEUNITS_CLASSNAME = ClassName.get(DAEMONUTILS_PACKAGE, "TimeUnits");

    protected static final String PROTOTYPE_STRING = "prototype";

    //variants
    protected String daemonEngineString = "daemonEngine";
    protected String daemonConcatEngineString = "DaemonEngine";

    protected ClassName consumer = ClassName.get(CONSUMER_PACKAGE, CONSUMER_INTERFACE_STRING);
    protected ClassName daemonClassName;

    protected String questTypeName;

    //prtotype and generated daemon class params
    protected String prototypeClassQualifiedName;
    protected String prototypeClassSimpleName;

    protected String daemonEngineSimpleName;


    public BaseDaemonGenerator setDaemonEngineString(String daemonEngineString) {
        this.daemonEngineString = daemonEngineString;
        return this;
    }

    public String getDaemonEngineString() {
        return daemonEngineString;
    }








    protected TypeElement classElement;



    protected String packageName;
    protected String daemonSimpleName;

    protected String returnRunnableType;





    protected ClassName consumerInterface = ClassName.get(CONSUMER_PACKAGE_ROOT, "Consumer");



    public String getDaemonEngineSimpleName() {
        return daemonEngineSimpleName;
    }

    protected ClassName daemonInterface = ClassName.get(
            DAEMON_ENGINE_PACKAGE_ROOT,
            "Daemon"
            );

    protected ClassName daemonStateClassName = ClassName.get(
            DAEMON_ENGINE_PACKAGE_ROOT,
            "DaemonState"
    );

    private static String baseClassBound = "java.lang.Object";
    private Set<String> overloadedPrototypeMethods = new TreeSet<>();

    protected List<TypeElement> interfaces;
    protected HashSet<PrototypeMethodData> overriddenMethods;

    public String getPackageName() {
        return packageName;
    }

    public BaseDaemonGenerator(TypeElement classElement) {
        this.classElement = classElement;
        this.prototypeClassQualifiedName = classElement.getQualifiedName().toString();
        this.prototypeClassSimpleName = classElement.getSimpleName().toString();
        this.packageName = prototypeClassQualifiedName.substring(0, prototypeClassQualifiedName.lastIndexOf("."));

        if (classElement.getNestingKind().equals(NestingKind.MEMBER)) {
            this.packageName = this.packageName.substring(0, packageName.lastIndexOf("."));
        }

        interfaces = new ArrayList<>();
        overriddenMethods = new HashSet<>();

        if (classElement.getAnnotation(Daemon.class).implementPrototypeInterfaces())
            for (TypeMirror intf : classElement.getInterfaces()) {
                TypeElement intfElement = (TypeElement) ((DeclaredType) intf).asElement();
                interfaces.add(intfElement);
                populateOverridenMethods(intf);
            }

        String name = classElement.getAnnotation(Daemon.class).className();
        this.daemonSimpleName = name.isEmpty() ? prototypeClassSimpleName + "Daemon" : name;

    }

    private void populateOverridenMethods(TypeMirror intf) {
        TypeElement intfElement = (TypeElement) ((DeclaredType) intf).asElement();

        for (Element intfMethod : getPublicClassMethods(intfElement)) {
            overriddenMethods.add(new PrototypeMethodData((ExecutableElement) intfMethod));
        }

        for (TypeMirror intfp : intfElement.getInterfaces()) {
            populateOverridenMethods(intfp);
        }
    }

    public static List<ExecutableElement> getPublicClassMethods(Element annotatedClass) {

        List<ExecutableElement> publicMethods = new ArrayList<>(10);
        for(Element innerElement :  annotatedClass.getEnclosedElements()) {
            if ((innerElement instanceof ExecutableElement) &&
                    !innerElement.getSimpleName().toString().equals("<init>") &&
                    innerElement.getModifiers().contains(Modifier.PUBLIC)) {
                publicMethods.add(((ExecutableElement) innerElement));
            }
        }

        return publicMethods;
    }


    public static List<ExecutableElement> getPublicClassMethodsWithBaseClasses(Element annotatedClass) {

        Map<PrototypeMethodData, ExecutableElement> publicMethodsMap = new HashMap<>();

        TypeElement annClass = (TypeElement) annotatedClass;

        for(Element innerElement :  annClass.getEnclosedElements()) {
            if ((innerElement instanceof ExecutableElement) &&
                    !innerElement.getSimpleName().toString().equals("<init>") &&
                    innerElement.getModifiers().contains(Modifier.PUBLIC)) {

                PrototypeMethodData innerElMethodData = new PrototypeMethodData((ExecutableElement) innerElement);
                publicMethodsMap.put(innerElMethodData, (ExecutableElement) innerElement);
            }
        }

        if (annClass.getKind().equals(ElementKind.CLASS)) {

            //while (!(annClass.getSuperclass() instanceof NoType || annClass.getSuperclass().getKind().equals(NONE))) {

            while (!(((TypeElement) ((DeclaredType) annClass.getSuperclass()).asElement())).getQualifiedName().toString().equals(baseClassBound)) {

                annClass = (TypeElement) ((DeclaredType) annClass.getSuperclass()).asElement();
                for (Element innerElement : annClass.getEnclosedElements()) {
                    if ((innerElement instanceof ExecutableElement) &&
                            !innerElement.getSimpleName().toString().equals("<init>") &&
                            innerElement.getModifiers().contains(Modifier.PUBLIC)) {

                        PrototypeMethodData innerElMethodData = new PrototypeMethodData((ExecutableElement) innerElement);

                        if (!publicMethodsMap.containsKey(innerElMethodData)) {
                            publicMethodsMap.put(innerElMethodData, (ExecutableElement) innerElement);
                        }
                    }
                }
            }
        }

        List<ExecutableElement> ret = new ArrayList<>(publicMethodsMap.size());
        for (ExecutableElement method : publicMethodsMap.values()) {
            ret.add(method);
        }

        return ret;
    }

    public void implementInterfaces(TypeSpec.Builder daemonClassBuilder, TypeName specialization) {
        for (TypeElement intf : interfaces) {
            TypeMirror intfMirror = intf.asType();
            List<? extends TypeParameterElement> typeParams = intf.getTypeParameters();

            boolean builder = false;

            if (!typeParams.isEmpty()) {

                for (TypeParameterElement type : typeParams) {
                    if (!type.getBounds().isEmpty() && type.getBounds().get(0).getClass().equals(intfMirror.getClass())) {// builder
                        builder = true;
                    } else {
                        daemonClassBuilder.addTypeVariable(TypeVariableName.get(type));
                    }
                }
            }

            daemonClassBuilder.addSuperinterface(
                    builder ?
                            ParameterizedTypeName.get(
                                    ClassName.get(intf),
                                    specialization
                            ) :
                            TypeName.get(intf.asType()));
        }
    }

    public static List<Pair<ExecutableElement, SideQuest>> getSideQuestMethods(List<ExecutableElement> publicMethods) {
        List<Pair<ExecutableElement, SideQuest>> ret = new ArrayList<>();
        for (ExecutableElement method : publicMethods) {
            if (method.getAnnotation(SideQuest.class) != null)
                ret.add(Pair.create(method, method.getAnnotation(SideQuest.class)));
        }
        return ret;
    }

    public static List<Pair<ExecutableElement, String>> getDedicatedThreadMethods(List<ExecutableElement> publicMethods) {
        List<Pair<ExecutableElement, String>> ret = new ArrayList<>();
        for (ExecutableElement method : publicMethods) {
            if (method.getAnnotation(DedicatedThread.class) != null)
                ret.add(Pair.create(method, method.getAnnotation(DedicatedThread.class).name()));
            else {
                Daemonize annotation = method.getAnnotation(Daemonize.class);
                if (annotation != null && annotation.dedicatedThread())
                    ret.add(Pair.create(method, annotation.name()));
            }
        }
        return ret;
    }

    public MethodSpec wrapMethod(ExecutableElement prototypeMethod, boolean implementIntf){

        PrototypeMethodData methodData = new PrototypeMethodData(prototypeMethod);
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(
                prototypeMethod.getSimpleName().toString()
        ).addModifiers(Modifier.PUBLIC);

        if(implementIntf)
            methodBuilder.addAnnotation(Override.class);

        methodBuilder = BaseDaemonGenerator.addTypeParameters(prototypeMethod, methodBuilder);

        for(Pair<TypeName, String> param : methodData.getParameters()) {
            methodBuilder.addParameter(param.getFirst(), param.getSecond());
        }

        for ( TypeMirror exception : prototypeMethod.getThrownTypes()) {
            methodBuilder.addException(TypeName.get(exception));
        }

        if (methodData.getMethodRetTypeName().equals(TypeName.get(prototypeMethod.getEnclosingElement().asType()))) {
            methodBuilder.returns(ClassName.get(packageName, daemonSimpleName)).addStatement("prototype."
                    + prototypeMethod.getSimpleName().toString()
                    + "(" + methodData.getArguments()
                    + ")").addStatement("return this");
        } else if (methodData.isVoid()) {
            if (implementIntf) {
                methodBuilder.returns(void.class).addStatement("prototype."
                        + prototypeMethod.getSimpleName().toString()
                        + "(" + methodData.getArguments()
                        + ")");
            } else {
                methodBuilder.returns(ClassName.get(packageName, daemonSimpleName)).addStatement("prototype."
                        + prototypeMethod.getSimpleName().toString()
                        + "(" + methodData.getArguments()
                        + ")").addStatement("return this");
            }
        } else
            methodBuilder.returns(TypeName.get(prototypeMethod.getReturnType())).addStatement(
                    "return prototype."
                            + prototypeMethod.getSimpleName().toString()
                            + "(" + methodData.getArguments()
                            + ")");

        return methodBuilder.build();
    }


    public List<MethodSpec> generateDaemonApiMethods() {

        List<MethodSpec> ret = new ArrayList<>();

        ret.add(generateGetPrototypeDaemonApiMethod());
        ret.add(generateSetPrototypeDaemonApiMethod());
        ret.add(generateStartDaemonApiMethod());
        ret.add(generateStopDaemonApiMethod());
        ret.add(generateClearDaemonApiMethod());
        ret.add(generateQueueStopDaemonApiMethod());
        ret.add(generateGetEnginesStateDaemonApiMethod());
        ret.add(generateGetEnginesQueueSizeDaemonApiMethod());
        ret.add(generateSetNameDaemonApiMethod());
        ret.add(generateGetNameDaemonApiMethod());
        ret.add(generateSetConsumerDaemonApiMethod());
        ret.add(generateGetConsumerDaemonApiMethod());
        ret.add(generateSetUncaughtExceptionHandler());

        return ret;
    }

    public MethodSpec generateGetPrototypeDaemonApiMethod() {
        return MethodSpec.methodBuilder("getPrototype")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(classElement.asType()))
                .addStatement("return prototype")
                .build();
    }

    public MethodSpec generateSetPrototypeDaemonApiMethod() {
        return MethodSpec.methodBuilder("setPrototype")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(classElement.asType()), "prototype")
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement("this.prototype = prototype")
                .addStatement("return this")
                .build();
    }

    public MethodSpec generateStartDaemonApiMethod() {
        return MethodSpec.methodBuilder("start")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".start()")
                .addStatement("return this")
                .build();
    }

    public MethodSpec generateStopDaemonApiMethod() {
        return MethodSpec.methodBuilder("stop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement(daemonEngineString + ".stop()")
                .build();
    }

    public MethodSpec generateQueueStopDaemonApiMethod() {
        return MethodSpec.methodBuilder("queueStop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                //.returns(void.class)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".queueStop(this)")
                .addStatement("return this")
                .build();
    }
//
//    public MethodSpec generateGetStateDaemonApiMethod() {
//        return MethodSpec.methodBuilder("getState")
//                .addAnnotation(Override.class)
//                .addModifiers(Modifier.PUBLIC)
//                .returns(daemonStateClassName)
//                .addStatement("return " + daemonEngineString + ".getState()")
//                .build();
//    }

    public MethodSpec generateSetNameDaemonApiMethod() {
        return MethodSpec.methodBuilder("setName")
                .addParameter(String.class, "name")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".setName(name)")
                .addStatement("return this")
                .build();
    }

    public MethodSpec generateGetNameDaemonApiMethod() {
        return MethodSpec.methodBuilder("getName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return " + daemonEngineString + ".getName()")
                .build();
    }

    public MethodSpec generateSetConsumerDaemonApiMethod() {
        return MethodSpec.methodBuilder("setConsumer")
                .addParameter(consumer, "consumer")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement(daemonEngineString + ".setConsumer(consumer)")
                .addStatement("return this")
                .build();
    }


    public MethodSpec generateGetConsumerDaemonApiMethod() {
        return MethodSpec.methodBuilder("getConsumer")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(consumerInterface)
                .addStatement("return " + daemonEngineString + ".getConsumer()")
                .build();
    }

    //setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler)

    public MethodSpec generateSetUncaughtExceptionHandler() {
        return MethodSpec.methodBuilder("setUncaughtExceptionHandler")
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(Thread.UncaughtExceptionHandler.class), "handler")
                .addModifiers(Modifier.PUBLIC)
                .addStatement(daemonEngineString + ".setUncaughtExceptionHandler(handler)")
                .returns(ClassName.get(packageName, daemonSimpleName))
                .addStatement("return this")
                .build();
    }

    public abstract TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods);

    protected MethodSpec createPursue(
            String methodName,
            TypeName methodRetTypeName,
            String arguments,
            boolean isVoid,
            boolean isStatic,
            LogExecutionTime logExecutionTime
    ) {

        MethodSpec.Builder pursueImplementation = MethodSpec.methodBuilder("pursue")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        pursueImplementation.returns(methodRetTypeName);
        pursueImplementation.addException(Exception.class);//TODO check this shit

        String instanceOrClass = isStatic ? prototypeClassSimpleName : PROTOTYPE_STRING;


        if (!isVoid) {

            if (logExecutionTime != null) {
                addTimeMeasureCode(
                        pursueImplementation,
                        logExecutionTime.daemonName(),
                        logExecutionTime.timeUnits(),
                        methodRetTypeName.toString() + " ret = " + instanceOrClass + "." + methodName + "(" + arguments + ")"//TODO fix ret
                );
            } else {
                pursueImplementation.addStatement("return " + instanceOrClass + "."
                        + methodName + "(" + arguments + ")");
            }

        } else {
            pursueImplementation.addStatement(instanceOrClass + "."
                    + methodName + "(" + arguments + ")");
            pursueImplementation.addStatement("return null");
        }

        return pursueImplementation.build();
    }


    protected static TypeSpec.Builder addTypeParameters(
            TypeElement classElement,
            TypeSpec.Builder builder
    ) {

        List<? extends TypeParameterElement> typeVariables = classElement.getTypeParameters();
        if (!typeVariables.isEmpty()){
            for (TypeParameterElement type: typeVariables) {
                builder.addTypeVariable(TypeVariableName.get(type));
            }
        }

        return builder;
    }

    protected static MethodSpec.Builder addTypeParameters(
            ExecutableElement methodElement,
            MethodSpec.Builder builder
    ) {

        List<? extends TypeParameterElement> typeVariables = methodElement.getTypeParameters();
        if (!typeVariables.isEmpty()){
            for (TypeParameterElement type: typeVariables) {
                builder.addTypeVariable(TypeVariableName.get(type));
            }
        }

        return builder;
    }

    protected static TypeSpec.Builder addTypeParameters(
            ExecutableElement classElement,
            TypeSpec.Builder builder
    ) {

        List<? extends TypeParameterElement> typeVariables = classElement.getTypeParameters();
        if (!typeVariables.isEmpty()){
            for (TypeParameterElement type: typeVariables) {
                builder.addTypeVariable(TypeVariableName.get(type));
            }
        }

        return builder;
    }

    private void addTimeMeasureCode(MethodSpec.Builder builder, String daemonName, TimeUnits units, String usefulCode) {

        if (!daemonName.isEmpty()) {

            builder.addCode("if (Thread.currentThread().getName().equals($S)) {\n", daemonName);
            builder.addStatement("  long begin = System.nanoTime()");
            builder.addStatement("  " + usefulCode);
            builder.addStatement("  long end = System.nanoTime()");
            builder.addStatement(
                    "  System.out.println($T.tag() + \"Method '\" + this.description + \"' execution lasted: \" + $T.convertNanoTimeUnits(end - begin, $T.$N))",
                    DAEMON_UTILS_CLASSNAME,
                    DAEMON_UTILS_CLASSNAME,
                    TIMEUNITS_CLASSNAME,
                    units.name()
            );
            builder.addStatement("  return ret");
            builder.addCode("} else {\n");
            builder.addStatement("  " + usefulCode);
            builder.addStatement("  return ret");
            builder.addCode("}\n");

        } else {
            builder.addStatement("long begin = System.nanoTime()");
            builder.addStatement(usefulCode);
            builder.addStatement("long end = System.nanoTime()");
            builder.addStatement("System.out.println($T.convertNanoTimeUnits(end - begin, $T.$N))", DAEMON_UTILS_CLASSNAME, TIMEUNITS_CLASSNAME, units.name());
            builder.addStatement("return ret");
        }



    }


    public abstract MethodSpec generateGetEnginesStateDaemonApiMethod();

    public abstract MethodSpec generateGetEnginesQueueSizeDaemonApiMethod();

    public abstract MethodSpec generateClearDaemonApiMethod();

}
