package com.daemonize.daemonprocessor;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;
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

@SupportedAnnotationTypes(
        {
                "com.daemonize.daemonprocessor.annotations.Daemon",
                "com.daemonize.daemonprocessor.annotations.SideQuest",
                "com.daemonize.daemonprocessor.annotations.DedicatedThread"
        }
)
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

        messager.printMessage(Diagnostic.Kind.NOTE, "Starting DaemonProcessor...");

        Collection<? extends Element> annotatedElements =
                roundEnvironment.getElementsAnnotatedWith(Daemon.class);

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
                                + " - @Daemon can only be applied to a class or an interface."
                );
                return true;

            } else {

                messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "Annotated type found: " + classElement.asType().toString()
                );

                List<ExecutableElement> publicPrototypeMethods =
                        classElement.getAnnotation(Daemon.class).daemonizeBaseMethods() ?
                                BaseDaemonGenerator.getPublicClassMethodsWithBaseClasses(classElement) :
                                BaseDaemonGenerator.getPublicClassMethods(classElement);

                int daemonized = 0;

                for (ExecutableElement method : publicPrototypeMethods)
                    if(method.getAnnotation(Daemonize.class) != null)
                        daemonized++;

                List<Pair<ExecutableElement, SideQuest>> sideQuestMethods =
                        BaseDaemonGenerator.getSideQuestMethods(publicPrototypeMethods);

                for (Pair<ExecutableElement, SideQuest> sideQuestMethod : sideQuestMethods) {
                    if (sideQuestMethod.getFirst().getAnnotation(Daemonize.class) != null || sideQuestMethod.getFirst().getAnnotation(DedicatedThread.class) != null)
                        throw new IllegalStateException(
                                "Error daemonizing class: "
                                        + classElement.getSimpleName().toString()
                                        + " - @SideQuest method cant have other annotations."
                        );
                }

                DaemonGenerator generator;

                if (publicPrototypeMethods.size() < 1) {
                    messager.printMessage(
                            Diagnostic.Kind.NOTE,
                            "No public methods in: " + classElement.asType().toString()
                    );
                    continue;
                }

                if(sideQuestMethods.isEmpty()) {
                    generator = new MainQuestDaemonGenerator(((TypeElement) classElement));
                } else {

                    if(publicPrototypeMethods.size() == sideQuestMethods.size()) {
                    //if(daemonized > 0) {
                        generator = new SideQuestDaemonGenerator(((TypeElement) classElement));

                    } else if (publicPrototypeMethods.size() > sideQuestMethods.size()) {

                        // double threaded daemon
                        if (classElement.getAnnotation(Daemon.class).doubleDaemonize()) {

                            generator = new DoubleDaemonGenerator(((TypeElement) classElement));

                        } else {

                            for(ExecutableElement method : publicPrototypeMethods) {
                                if(method.getAnnotation(DedicatedThread.class) != null
                                        || (method.getAnnotation(Daemonize.class) != null
                                        && method.getAnnotation(Daemonize.class).dedicatedThread()))
                                    throw new IllegalStateException(
                                            "Error daemonizing class: "
                                            + classElement.getSimpleName().toString()
                                            + " - To use @DedicatedThread daemon must be marked with 'doubleDaemonize = true' annotation arg."
                                    );
                            }

                            //single threaded daemon
                            generator = new HybridDaemonGenerator(((TypeElement) classElement));
                        }

                    } else {
                        throw new IllegalStateException(
                                classElement.toString()
                                        + " has more side quests than methods."
                                        + " That cant be right...");
                    }
                }

                generator.setPrinter(new BaseDaemonGenerator.Printer() {
                    @Override
                    public void print(String string) {
                        messager.printMessage(Diagnostic.Kind.NOTE, string);
                    }
                });

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
