package com.daemonize.daemonprocessor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.type.TypeKind.VOID;

class PrototypeMethodData {

    private String methodName;
    private TypeMirror methodReturn;
    private TypeName methodRetTypeName;
    private boolean isVoid;
    private TypeName closureOfRet;
    private String arguments = "";
    private List<TypeName> parametersType = new ArrayList<>();
    private List<String> parametersName = new ArrayList<>();

    PrototypeMethodData(ExecutableElement prototypeMethod) {
        methodName = prototypeMethod.getSimpleName().toString();
        methodReturn = prototypeMethod.getReturnType();
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
                        BaseDaemonGenerator.CLOSURE_PACKAGE,
                        BaseDaemonGenerator.CLOSURE_STRING
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

    String getMethodName() {
        return methodName;
    }

    TypeMirror getMethodReturn() {
        return methodReturn;
    }

    TypeName getMethodRetTypeName() {
        return methodRetTypeName;
    }

    boolean isVoid() { return isVoid; }

    String getArguments() {
        return arguments;
    }

    List<Pair<TypeName, String>> getParameters() {
        List<Pair<TypeName, String>> ret  = new ArrayList<>(parametersType.size());
        for(int i = 0; i < parametersType.size(); ++i) {
            ret.add(Pair.create(parametersType.get(i), parametersName.get(i)));
        }
        return ret;
    }

    TypeName getClosureOfRet() {
        return closureOfRet;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(this instanceof PrototypeMethodData))
            return false;
        else {
            PrototypeMethodData rhs = (PrototypeMethodData) obj;
            if (this.methodName.equals(rhs.methodName) &&
//                        this.methodRetTypeName.equals(rhs.methodRetTypeName) &&
//                        this.closureOfRet.equals(rhs.closureOfRet) &&
                    this.parametersType.equals(rhs.parametersType))
                return true;
            else
                return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, /*methodRetTypeName, closureOfRet,*/ parametersType);
    }
}
