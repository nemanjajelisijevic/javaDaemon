package com.daemonize.daemonprocessor;


import com.squareup.javapoet.TypeSpec;


import java.util.List;

import javax.lang.model.element.ExecutableElement;


public interface DaemonGenerator {
    TypeSpec generateDaemon(List<ExecutableElement> publicPrototypeMethods);
    String getPackageName();
}
