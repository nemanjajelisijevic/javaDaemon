package com.daemonize.daemonprocessor;


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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;


import static javax.lang.model.type.TypeKind.VOID;

public abstract class BaseDaemonGenerator implements DaemonGenerator {

    protected ClassName daemonClassName;

    protected boolean autoGenerateApiMethods = true;

    public BaseDaemonGenerator setAutoGenerateApiMethods(boolean autoGenerateApiMethods) {
        this.autoGenerateApiMethods = autoGenerateApiMethods;
        return this;
    }

    protected final String PROTOTYPE_STRING = "prototype";

    protected String daemonEngineString = "daemonEngine";
    protected String daemonConcatEngineString = "DaemonEngine";

    public BaseDaemonGenerator setDaemonEngineString(String daemonEngineString) {
        this.daemonEngineString = daemonEngineString;
        return this;
    }

    public String getDaemonEngineString() {
        return daemonEngineString;
    }

    protected final String DAEMON_ENGINE_PACKAGE_ROOT = "com.daemonize.daemonengine";
    protected final String DAEMON_ENGINE_IMPL_PACKAGE = DAEMON_ENGINE_PACKAGE_ROOT + ".implementations";

    protected static final String CLOSURE_PACKAGE = "com.daemonize.daemonengine.closure";
    protected static final String CLOSURE_STRING = "Closure";

    protected final String QUEST_PACKAGE = "com.daemonize.daemonengine.quests";
    protected String QUEST_TYPE_NAME;
    protected final String STOP_QUEST_TYPE_NAME = "StopMainQuest";

    protected final String CONSUMER_PACKAGE = "com.daemonize.daemonengine.consumer";
    protected final String CONSUMER_INTERFACE_STRING = "Consumer";

    protected ClassName consumer = ClassName.get(CONSUMER_PACKAGE, CONSUMER_INTERFACE_STRING);

    private final String DAEMONUTILS_PACKAGE = "com.daemonize.daemonengine.utils";
    protected final ClassName DAEMON_UTILS_CLASSNAME = ClassName.get(DAEMONUTILS_PACKAGE, "DaemonUtils");
    private final ClassName TIMEUNITS_CLASSNAME = ClassName.get(DAEMONUTILS_PACKAGE, "TimeUnits");

    protected TypeElement classElement;

    protected String prototypeClassQualifiedName;
    protected String prototypeClassSimpleName;

    protected String packageName;
    protected String daemonSimpleName;

    protected String returnRunnableType;

    protected String daemonPackage;
    protected String daemonEngineSimpleName;

    protected final String CONSUME_QUEST_TYPE_NAME = "ConsumeQuest";
    protected final String CONSUMER_PACKAGE_ROOT = "com.daemonize.daemonengine.consumer";

    protected ClassName consumerInterface = ClassName.get(CONSUMER_PACKAGE_ROOT, "Consumer");

    public String getDaemonPackage() {
        return daemonPackage;
    }

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

    //protected

    private Set<String> overloadedPrototypeMethods = new TreeSet<>();

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

        String name = classElement.getAnnotation(Daemonize.class).className();
        this.daemonSimpleName = name.isEmpty() ? prototypeClassSimpleName + "Daemon" : name;

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

        System.err.println("Annotated class: " + annClass.getQualifiedName());

        for(Element innerElement :  annClass.getEnclosedElements()) {
            if ((innerElement instanceof ExecutableElement) &&
                    !innerElement.getSimpleName().toString().equals("<init>") &&
                    innerElement.getModifiers().contains(Modifier.PUBLIC)) {

                PrototypeMethodData innerElMethodData = new PrototypeMethodData((ExecutableElement) innerElement);
                publicMethodsMap.put(innerElMethodData, (ExecutableElement) innerElement);
            }
        }

        if (annClass.getKind().equals(ElementKind.CLASS)) {
            while (!(((TypeElement) ((DeclaredType) annClass.getSuperclass()).asElement())).getQualifiedName().toString().equals("java.lang.Object")) {

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

    public static List<Pair<ExecutableElement, SideQuest>> getSideQuestMethods(List<ExecutableElement> publicMethods) {

        List<Pair<ExecutableElement, SideQuest>> ret = new ArrayList<>();

        for (ExecutableElement method : publicMethods) {
            if (method.getAnnotation(SideQuest.class) != null) {
                ret.add(Pair.create(method, method.getAnnotation(SideQuest.class)));
            }
        }

        return ret;
    }

    public static List<Pair<ExecutableElement, DedicatedThread>> getDedicatedThreadMethods(List<ExecutableElement> publicMethods) {

        List<Pair<ExecutableElement, DedicatedThread>> ret = new ArrayList<>();

        for (ExecutableElement method : publicMethods) {
            if (method.getAnnotation(DedicatedThread.class) != null) {
                ret.add(Pair.create(method, method.getAnnotation(DedicatedThread.class)));
            }
        }

        return ret;
    }


    public List<MethodSpec> generateDaemonApiMethods() {

        List<MethodSpec> ret = new ArrayList<>(9);

        ret.add(generateGetPrototypeDaemonApiMethod());
        ret.add(generateSetPrototypeDaemonApiMethod());
        ret.add(generateStartDaemonApiMethod());
        ret.add(generateStopDaemonApiMethod());
        ret.add(generateClearDaemonApiMethod());
        ret.add(generateQueueStopDaemonApiMethod());
        ret.add(generateGetEnginesStateDaemonApiMethod());
        ret.add(generateSetNameDaemonApiMethod());
        ret.add(generateGetNameDaemonApiMethod());
        ret.add(generateSetConsumerDaemonApiMethod());
        ret.add(generateGetConsumerDaemonApiMethod());

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


    protected static class PrototypeMethodData {

        private String methodName;
        private TypeName methodRetTypeName;
        private boolean isVoid;
        private TypeName closureOfRet;
        private String arguments = "";
        private List<TypeName> parametersType = new ArrayList<>();
        private List<String> parametersName = new ArrayList<>();

        protected PrototypeMethodData(ExecutableElement prototypeMethod) {
            methodName = prototypeMethod.getSimpleName().toString();
            TypeMirror methodReturn = prototypeMethod.getReturnType();
            methodRetTypeName = TypeName.get(methodReturn);
            isVoid = methodReturn.getKind().equals(VOID);

            if (methodReturn.getKind().isPrimitive()) {
                methodRetTypeName = methodRetTypeName.box();
            } else if (isVoid) {
                methodRetTypeName = TypeName.get(Void.class);
            }

            List<? extends VariableElement> methodParameters = prototypeMethod.getParameters();

            closureOfRet = ParameterizedTypeName.get(
                    ClassName.get(
                            CLOSURE_PACKAGE,
                            CLOSURE_STRING
                    ),
                    methodRetTypeName
            );

            StringBuilder argumentBuilder = new StringBuilder();

            for (int i = 0; i < methodParameters.size(); ++i) {

                VariableElement fieldElement = methodParameters.get(i);

                TypeMirror fieldType = fieldElement.asType();
                String fieldName = fieldElement.getSimpleName().toString().toLowerCase();

                parametersType.add(ClassName.get(fieldType));
                parametersName.add(fieldName);

                argumentBuilder.append(fieldName);
                if (i != methodParameters.size() - 1) {
                    argumentBuilder.append(", ");
                }
            }

            arguments = argumentBuilder.toString();

        }

        protected String getMethodName() {
            return methodName;
        }

        protected TypeName getMethodRetTypeName() {
            return methodRetTypeName;
        }

        protected boolean isVoid() { return isVoid; }

        protected String getArguments() {
            return arguments;
        }

        protected List<Pair<TypeName, String>> getParameters() {
            List<Pair<TypeName, String>> ret  = new ArrayList<>(parametersType.size());
            for(int i = 0; i < parametersType.size(); ++i) {
                ret.add(Pair.create(parametersType.get(i), parametersName.get(i)));
            }
            return ret;
        }

        protected TypeName getClosureOfRet() {
            return closureOfRet;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(this instanceof PrototypeMethodData))
                return false;
            else {
                PrototypeMethodData rhs = (PrototypeMethodData) obj;
                if (this.methodName.equals(rhs.methodName) &&
                        this.methodRetTypeName.equals(rhs.methodRetTypeName) &&
                        this.closureOfRet.equals(rhs.closureOfRet) &&
                        this.parametersType.equals(rhs.parametersType))
                    return true;
                else
                    return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, methodRetTypeName, closureOfRet, parametersType);
        }
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
