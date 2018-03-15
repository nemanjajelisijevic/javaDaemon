package com.daemonize.daemonprocessor;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;


import static javax.lang.model.type.TypeKind.VOID;

public abstract class BaseDaemonGenerator implements DaemonGenerator {


    protected final String PROTOYPE_STRING = "prototype";
    protected final String DAEMON_ENGINE_STRING = "daemonEngine";

    protected final String DAEMON_ENGINE_PACKAGE_ROOT = "com.daemonize.daemonengine";
    protected final String DAEMON_ENGINE_IMPL_PACKAGE = DAEMON_ENGINE_PACKAGE_ROOT + ".implementations";

    protected final String CLOSURE_PACKAGE = "com.daemonize.daemonengine.closure";
    protected final String CLOSURE_STRING = "Closure";

    protected final String QUEST_PACKAGE = "com.daemonize.daemonengine.quests";
    protected String QUEST_TYPE_NAME;

    protected final String CONSUMER_PACKAGE = "com.daemonize.daemonengine.consumer";


    protected TypeElement classElement;

    protected String prototypeClassQualifiedName;
    protected String prototypeClassSimpleName;
    protected String packageName;
    protected String daemonSimpleName;

    protected Platform platform;

    protected String daemonPackage;
    protected String daemonEngineSimpleName;

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
        this.platform = classElement.getAnnotation(Daemonize.class).platform();
        String name = classElement.getAnnotation(Daemonize.class).className();
        this.daemonSimpleName = name.isEmpty() ? prototypeClassSimpleName + "Daemon" : name;

    }

    public static List<ExecutableElement> getAnnotatedClassMethods(Element annotatedClass) {

        //TODO ignore accessors

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

    public static List<Pair<ExecutableElement, SideQuest>> getSideQuestMethods(List<ExecutableElement> publicMethods) {

        List<Pair<ExecutableElement, SideQuest>> ret = new ArrayList<>();

        for (ExecutableElement method : publicMethods) {
            if (method.getAnnotation(SideQuest.class) != null) {
                ret.add(Pair.create(method, method.getAnnotation(SideQuest.class)));
            }
        }

        return ret;
    }

    public abstract TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods);

    public List<MethodSpec> generateDaemonApiMethods() {

        List<MethodSpec> ret = new ArrayList<>(7);

        ret.add(
                MethodSpec.methodBuilder("getPrototype")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(packageName, prototypeClassSimpleName))
                        .addStatement("return prototype")
                        .build()
        );

        ret.add(
                MethodSpec.methodBuilder("start")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                //.returns(ClassName.get(packageName, daemonSimpleName))
                .returns(void.class)
                .addStatement(DAEMON_ENGINE_STRING + ".start()")
                //.addStatement("return this")
                .build()
        );

        ret.add(
                MethodSpec.methodBuilder("stop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement(DAEMON_ENGINE_STRING + ".stop()")
                .build()
        );

        ret.add(
                MethodSpec.methodBuilder("getState")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(daemonStateClassName)
                .addStatement("return " + DAEMON_ENGINE_STRING + ".getState()")
                .build()
        );

        ret.add(
                MethodSpec.methodBuilder("setName")
                        .addParameter(String.class, "name")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.get(packageName, daemonSimpleName))
                        .addStatement("daemonEngine.setName(name)")
                        .addStatement("return this")
                        //.addStatement("return " + DAEMON_ENGINE_STRING + ".getState()")
                        .build()
        );

        ret.add(
                MethodSpec.methodBuilder("getName")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return daemonEngine.getName()")
                        .build()
        );

        ClassName consumer = ClassName.get(CONSUMER_PACKAGE, "Consumer");

        ret.add(
                MethodSpec.methodBuilder("setConsumer")
                        .addParameter(consumer, "consumer")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addStatement("daemonEngine.setConsumer(consumer)")
                        .build()
        );

        return ret;
    }

    protected MethodSpec createPursue(
            String methodName,
            TypeName methodRetTypeName,
            String arguments,
            boolean isVoid,
            boolean isStatic
    ) {

        MethodSpec.Builder pursueImplementation = MethodSpec.methodBuilder("pursue")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED, Modifier.FINAL);

        pursueImplementation.returns(methodRetTypeName);
        pursueImplementation.addException(Exception.class);//TODO check this shit

        String instanceOrClass = isStatic ? prototypeClassSimpleName : PROTOYPE_STRING;

        if (!isVoid) {
            pursueImplementation.addStatement("return " + instanceOrClass + "."
                    + methodName + "(" + arguments + ")");
        } else {
            pursueImplementation.addStatement(instanceOrClass + "."
                    + methodName + "(" + arguments + ")");
            pursueImplementation.addStatement("return null");
        }

        return pursueImplementation.build();
    }


    protected class PrototypeMethodData {

        private String methodName;
        private TypeName methodRetTypeName;
        private boolean isVoid;
        private TypeName closureOfRet;
        private String arguments = "";
        private List<Pair<TypeName, String>> parameters = new ArrayList<>();

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

                parameters.add(Pair.create(ClassName.get(fieldType), fieldName));

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
        protected boolean isVoid() {
            return isVoid;
        }
        protected String getArguments() {
            return arguments;
        }
        protected List<Pair<TypeName, String>> getParameters() {
            return parameters;
        }
        protected TypeName getClosureOfRet() {
            return closureOfRet;
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

}
