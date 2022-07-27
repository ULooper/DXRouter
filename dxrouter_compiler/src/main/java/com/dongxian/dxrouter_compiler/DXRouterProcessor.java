package com.dongxian.dxrouter_compiler;

import static com.dongxian.dxrouter_compiler.DXRouterConstants.DXROUTER_API_PATH;
import static com.dongxian.dxrouter_compiler.DXRouterConstants.DX_ROUTER_ANNOTATION_PACKAGE_NAME;
import static com.dongxian.dxrouter_compiler.DXRouterConstants.MODULE_MAPS;
import static com.dongxian.dxrouter_compiler.DXRouterConstants.MODULE_MAPS_FILE_NAME;
import static com.dongxian.dxrouter_compiler.DXRouterConstants.MODULE_MAPS_FILE_PACKAGE_NAME;
import static com.dongxian.dxrouter_compiler.DXRouterConstants.OPTIONS;

import com.dongxian.dxrouter_annotation.DXRouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 路由注解解析器，用于解析自定义DXRouter
 *
 * @author DongXian
 * on 2022/7/26
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes(DX_ROUTER_ANNOTATION_PACKAGE_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DXRouterProcessor extends AbstractProcessor {
    /**
     * 路由管理类基类
     */
    protected final String packageName = "com.dongxian.dxrouter_api.BaseModule";

    private final String TAG = DXRouterProcessor.class.getSimpleName();
    /**
     * 操作Element的工具类(类、函数、属性等对应的都是element)
     */
    private Elements elements;
    /**
     * type（类信息）的工具类，包含用于操作TypeMirror
     */
    private Types types;
    /**
     * 日志打印工具类
     */
    private Messager messager;
    /**
     * 文件写操作
     */
    private Filer filer;

    /**
     * module集合map
     */
    private Map<String, List<String>> moduleMaps = new HashMap<>();


    /**
     * 各个模块传递过来的模块名，如app、order、personal
     */
    private String options;

    /**
     * 初始化准备工作
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        options = processingEnv.getOptions().get(OPTIONS);
        messager.printMessage(Diagnostic.Kind.NOTE, TAG + " init");
    }

    /**
     * 注解处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            return false;
        }
        //获取DXRouter标注的类
        Set<? extends TypeElement> routesElements = (Set<? extends TypeElement>) roundEnvironment.getElementsAnnotatedWith(DXRouter.class);
        for (Element element : routesElements) {
            TypeElement typeElement = (TypeElement) element;
            String clazzName = typeElement.getQualifiedName().toString();
            if (isExtendsParentClass(typeElement, packageName)) {
                createModulesFile(typeElement, clazzName);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "The class:" + clazzName + "should extends " + packageName);
            }
        }

        return false;
    }

    /**
     * 生成modules map集合文件
     *
     * @param typeElement
     * @param clazzName
     */
    private void createModulesFile(TypeElement typeElement, String clazzName) {
        List<String> paths = new ArrayList<>();
        DXRouter dxRouter = typeElement.getAnnotation(DXRouter.class);
        String[] value = dxRouter.value();
        for (String path : value) {
            if (isLegalPath(path)) {
                paths.add(path.trim());
            }
        }
        moduleMaps.put(clazzName, paths);
        //返回类型Map<String,String>
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class)
        );
        //方法名
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getModuleMaps")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturn);

        //代码块 Map<String,String> moduleMaps= new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T> $N= new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(String.class),
                MODULE_MAPS,
                ClassName.get(HashMap.class));
        for (Map.Entry<String, List<String>> entry : moduleMaps.entrySet()) {
            for (String moduleName : entry.getValue()) {
                methodBuilder.addStatement("$N.put($S,$S)",
                        MODULE_MAPS,
                        moduleName,
                        entry.getKey()
                );
            }

        }
        //return modulesMaps
        methodBuilder.addStatement("return $N", MODULE_MAPS);
        TypeElement pathTypeElement = elements.getTypeElement(DXROUTER_API_PATH);
        //生成文件
        try {
            //生成文件路径固定写死为 com.dongxian.dxrouter_api.DXRouter$$[模块名称]$$ModuleMaps
            JavaFile.builder(MODULE_MAPS_FILE_PACKAGE_NAME,
                    TypeSpec.classBuilder(MODULE_MAPS_FILE_NAME + options + "$$ModuleMaps")
                            .addSuperinterface(ClassName.get(pathTypeElement))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build()
            ).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, "file write exception:" + e.getMessage());
        }
    }


    /**
     * 查看element是否继承或实现指定的parent
     *
     * @param element
     * @param parent
     * @return
     */
    private boolean isExtendsParentClass(TypeElement element, String parent) {
        TypeElement parentType = elements.getTypeElement(parent);
        if (parentType == null) {
            messager.printMessage(Diagnostic.Kind.WARNING, TAG + element.getSimpleName() + "don't find parent" + parent);
            return false;
        }
        return types.isAssignable(element.asType(), parentType.asType());
    }

    /**
     * 检查path路径是否符合/app/activity格式要求
     *
     * @param path
     * @return
     */
    private boolean isLegalPath(String path) {
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "The" + path + "is illegal,should be like /app/activity");
            return false;
        }

        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "The" + path + "is illegal,should be like /app/activity");
            return false;
        }
        //截取出group字段
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (StringUtils.isEmpty(finalGroup) || !finalGroup.equals(options)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "The " + finalGroup + " should be equals " + options);
            return false;
        }
        return true;

    }
}
