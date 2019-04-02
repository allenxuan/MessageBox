package com.allenxuan.xuanyihuang.messagebox.compiler;

import com.allenxuan.xuanyihuang.messagebox.annotation.MessageReceive;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.*;

public class MessageBoxAnnotationProcessor extends AbstractProcessor {
    public static final String MESSAGE_RECEIVE_CLASS_SUFFUX = "$$$$MessageReceiver";

    /**
     * for writing .java file
     */
    private Filer mFiler;
    /**
     * for printing error message when annotation processing encounters an error
     */
    private Messager mMessager;

    private Map<String, ArrayList<ExecutableElement>> mGeneratedClassesInfo;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mGeneratedClassesInfo = new HashMap<String, ArrayList<ExecutableElement>>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<String>();
        set.add(MessageReceive.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * process() may be invoked more than once, which leads to an exception throwing("Attempt to recreate a file for type XXXX")
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //get all element annotated with MessageReceive
        Set<? extends Element> messageReceiveAnnotatedElements = roundEnvironment.getElementsAnnotatedWith(MessageReceive.class);
        //filter out non-method elements
        Set<ExecutableElement> messageReceiveAnnotatedMethods = ElementFilter.methodsIn(messageReceiveAnnotatedElements);

        for (ExecutableElement method : messageReceiveAnnotatedMethods) {
            updateGeneratedClassedInfo(method);
        }

        generateJavaFiles();

        return true;
    }

    private boolean isMessageReceiveAnnotatedMethodValid(ExecutableElement method) {
        if (method.getKind() != ElementKind.METHOD) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + " -> only method can be annotated with MessageReceive");
            return false;
        }
        if (method.getParameters().size() > 1) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + " -> method annotated with MessageReceive must contain only one parameter which extends MessageCarrier");
            return false;
        }
        if (method.getModifiers().contains(Modifier.ABSTRACT)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + "-> method annotated with MessageReceive cannot be abstract");
            return false;
        }
        if (method.getModifiers().contains(Modifier.STATIC)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + "-> method annotated with MessageReceive should not be static");
            return false;
        }
        if (!method.getModifiers().contains(Modifier.PUBLIC)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + "-> method annotated with MessageReceive should be public");
            return false;
        }
        Element typeElement = method.getEnclosingElement();
        if (typeElement.getKind() != ElementKind.CLASS) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + "-> enclosing element must be a class");
            return false;
        }

        Element packageElement = typeElement.getEnclosingElement();
        if (packageElement.getKind() != ElementKind.PACKAGE) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, method.getSimpleName() + "-> enclosing element cannot be a nested inner class");
            return false;
        }

        return true;
    }

    private void updateGeneratedClassedInfo(ExecutableElement method) {
        if (isMessageReceiveAnnotatedMethodValid(method)) {
            Element typeElement = method.getEnclosingElement();
            Element packageElement = typeElement.getEnclosingElement();
            String classFullName = packageElement.toString() + "." + typeElement.getSimpleName() + MESSAGE_RECEIVE_CLASS_SUFFUX;
            ArrayList<ExecutableElement> executableElements = mGeneratedClassesInfo.get(classFullName);
            if (executableElements == null) {
                executableElements = new ArrayList<ExecutableElement>();
                mGeneratedClassesInfo.put(classFullName, executableElements);
            }
            executableElements.add(method);
        }
    }

    private void generateSingleJavaFile(String packageName, String className, List<ExecutableElement> methods) {
        String targetName = className.split("\\$\\$\\$\\$")[0];
        ClassName target = ClassName.get(packageName, targetName);
        ClassName iMessageReceiver = ClassName.get("com.allenxuan.xuanyihuang.messagebox.core", "IMessageReceiver");
        ClassName messageCarrier = ClassName.get("com.allenxuan.xuanyihuang.messagebox.core", "MessageCarrier");
        ClassName messageInfo = ClassName.get("com.allenxuan.xuanyihuang.messagebox.core", "MessageInfo");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfMessageInfo = ParameterizedTypeName.get(list, messageInfo);
        TypeName arrayListOfMessageInfo = ParameterizedTypeName.get(arrayList, messageInfo);

        HashSet<String> methodParameterClassNames = new HashSet<String>();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(target, "target")
                .addStatement("this.$N = $N", "target", "target")
                .build();

        MethodSpec.Builder dispatchMessageMethodSpec = MethodSpec.methodBuilder("dispatchMessage")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(messageCarrier, "message")
                .beginControlFlow("if(target == null)")
                .addStatement("return")
                .endControlFlow();

        MethodSpec.Builder messageInfosMethodSpec = MethodSpec.methodBuilder("messageInfos")
                .addModifiers(Modifier.PUBLIC)
                .returns(listOfMessageInfo)
                .addStatement("$T messageInfos = new $T()", listOfMessageInfo, arrayListOfMessageInfo);

        boolean firstControlFlow = true;
        for (ExecutableElement method : methods) {
            //every method have only one parameter here
            String methodName = method.getSimpleName().toString();
            String methodParameterClassName = method.getParameters().get(0).asType().toString();
            int lastIndexOfDot = methodParameterClassName.lastIndexOf(".");
            String methodParameterClassPackage = methodParameterClassName.substring(0, lastIndexOfDot);
            String methodParameterClassSimpleName = methodParameterClassName.substring(lastIndexOfDot + 1, methodParameterClassName.length());
            if (methodParameterClassNames.contains(methodParameterClassName)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "Within class " + packageName + "." + targetName + ", more than one method annotated with MessageReceive contain parameters whose class types are the same.");
            } else {
                methodParameterClassNames.add(methodParameterClassName);
            }

            MessageReceive messageReceive = method.getAnnotation(MessageReceive.class);
            int executeThread = messageReceive.executeThread();
            int executeDelay = messageReceive.executeDelay();

            ClassName specificMessageType = ClassName.get(methodParameterClassPackage, methodParameterClassSimpleName);

            if(firstControlFlow) {
                firstControlFlow = false;
                dispatchMessageMethodSpec.beginControlFlow("if(message instanceof $T)", specificMessageType);
                dispatchMessageMethodSpec.addStatement("target.$L(($T)message)", methodName, specificMessageType);
            }else {
                dispatchMessageMethodSpec.nextControlFlow("if(message instanceof $T)", specificMessageType);
                dispatchMessageMethodSpec.addStatement("target.$L(($T)message)", methodName, specificMessageType);
            }

            messageInfosMethodSpec.addStatement("messageInfos.add(new MessageInfo($L, $L, $L))", methodParameterClassName + ".class", executeThread, executeDelay);
        }
        methodParameterClassNames.clear();

        if(methods.size() > 0) {
            dispatchMessageMethodSpec.endControlFlow();
        }
        messageInfosMethodSpec.addStatement("return messageInfos");

        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(iMessageReceiver)
                .addField(target, "target", Modifier.PRIVATE)
                .addMethod(constructor)
                .addMethod(dispatchMessageMethodSpec.build())
                .addMethod(messageInfosMethodSpec.build())
                .build();

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();


        try {
            javaFile.writeTo(mFiler);
        } catch (Throwable throwable) {
            //process() may be invoked more than once, which leads to an exception throwing("Attempt to recreate a file for type XXXX")
//            mMessager.printMessage(Diagnostic.Kind.WARNING, "generate java file error, cause: " + throwable.getCause() + ", message: " + throwable.getMessage());
        }
    }

    private void generateJavaFiles() {
        Iterator<Map.Entry<String, ArrayList<ExecutableElement>>> iterator = mGeneratedClassesInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<ExecutableElement>> entry = iterator.next();
            int lastDotIndex = entry.getKey().lastIndexOf(".");
            String packageName = entry.getKey().substring(0, lastDotIndex);
            String className = entry.getKey().substring(lastDotIndex + 1, entry.getKey().length());
            generateSingleJavaFile(packageName, className, entry.getValue());
        }
    }


}
