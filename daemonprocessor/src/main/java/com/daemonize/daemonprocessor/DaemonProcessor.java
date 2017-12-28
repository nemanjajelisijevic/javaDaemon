package com.daemonize.daemonprocessor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.daemonize.daemonprocessor.Daemonize", "com.daemonize.daemonprocessor.SideQuest"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DaemonProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //TODO annotation-config: update on runtime errors
        //TODO add the STOP_PENDING daemon state
        //TODO toString, equals?, hashcode?, copy construct/clone
        //TODO omit accessors
        //TODO annotation-config:daemon methods return daemon instance for chaining         CHECK!!!!
        //TODO get prototype method                                                         CHECK!!!!
        //TODO Generate exceptions!
        //TODO annotation-config: configure for singleton
        //TODO type parameters                                                              CHECK!!!!
        //TODO daemon parameter names all lowercase ???
        //TODO Daemonize superclass ???


        messager.printMessage(Diagnostic.Kind.NOTE, "Starting DaemonProcessor...");

        Collection<? extends Element> annotatedElements =
                roundEnvironment.getElementsAnnotatedWith(Daemonize.class);

        if (annotatedElements.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "No annotated classes");
            return true;
        }

        for (Element classElement : annotatedElements) {

            if (!(classElement.getKind().equals(ElementKind.CLASS) || classElement.getKind().equals(ElementKind.INTERFACE))) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Error processing element: "
                                + classElement.getSimpleName()
                                + " - @Daemonize can only be applied to a class or interface."
                );
                return true;
            } else {
                messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "Annotated type found: " + classElement.asType().toString()
                );

                List<ExecutableElement> publicPrototypeMethods =
                        BaseDaemonGenerator.getAnnotatedClassMethods(classElement);

                List<Pair<ExecutableElement, SideQuest>> sideQuestMethods =
                        BaseDaemonGenerator.getSideQuestMethods(publicPrototypeMethods);

                DaemonGenerator generator;

                if(sideQuestMethods.isEmpty()) {
                    generator = new MainQuestDaemonGenerator(((TypeElement) classElement));
                } else {

                    if(publicPrototypeMethods.size() == sideQuestMethods.size()) {
                        generator = new SideQuestDaemonGenerator(((TypeElement) classElement));
                    } else if (publicPrototypeMethods.size() > sideQuestMethods.size()) {
                        generator = new HybridDaemonGenerator(((TypeElement) classElement));
                    } else {
                        throw new IllegalStateException(
                                classElement.toString()
                                        + " has more side quests than methods."
                                        + " That is impossible. Fuck me...");
                    }
                }

                TypeSpec daemonClass = generator.generateDaemon(publicPrototypeMethods);
                JavaFile javaFile = JavaFile.builder(generator.getPackageName(), daemonClass).build();

                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        return true;
    }

}
