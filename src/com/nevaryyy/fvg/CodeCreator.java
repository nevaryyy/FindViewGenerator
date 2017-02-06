package com.nevaryyy.fvg;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.PsiUtilBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by waly6 on 2017/1/10.
 */
public class CodeCreator extends WriteCommandAction.Simple {

    private Project project;
    private PsiFile file;
    private PsiClass targetClass;
    private PsiElementFactory factory;
    private List<ViewElement> elementList;
    private Map<String, String> map;

    public CodeCreator(Project project, PsiClass targetClass, PsiElementFactory factory, List<ViewElement> elementList, PsiFile... files) {
        super(project, files);
        this.project = project;
        this.targetClass = targetClass;
        this.factory = factory;
        this.file = files[0];
        this.elementList = elementList;
        this.map = createMap();
    }

    private Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        map.put("WebView", "android.webkit.WebView");
        map.put("View", "android.view.View");
        map.put("TextureView", "android.view.TextureView");
        map.put("fragment", "android.support.v4.app.Fragment");
        map.put("SurfaceView", "android.view.SurfaceView");
        return map;
    }

    @Override
    protected void run() throws Throwable {
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, "FindView.java", new EverythingGlobalScope(project));
        String annotationName = null;

        if (psiFiles.length > 0) {
            if (psiFiles[0] instanceof PsiJavaFile) {
                annotationName = ((PsiJavaFile) psiFiles[0]).getPackageName() + ".FindView";
            }
        }

        if (annotationName == null) {
            System.out.println("Cannot find annotation: FindView");
            return;
        }

        PsiField[] psiFields = targetClass.getFields();
        PsiField firstNotStaticField = null;

        for (PsiField psiField : psiFields) {
            PsiAnnotation psiAnnotation = AnnotationUtil.findAnnotation(psiField, annotationName);
            if (psiAnnotation != null) {
                psiField.delete();
            }
            else {
                PsiModifierList modifierList = psiField.getModifierList();
                if (firstNotStaticField == null && !(modifierList != null && modifierList.hasModifierProperty("static"))) {
                    firstNotStaticField = psiField;
                }
            }
        }

        for (ViewElement element : elementList) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("@");
            stringBuilder.append(annotationName);
            stringBuilder.append("\n");
            stringBuilder.append("private ");

            if (element.getClassNameFull() != null) {
                stringBuilder.append(element.getClassNameFull());
            }
            else if (map.containsKey(element.getClassName())) {
                stringBuilder.append(map.get(element.getClassName()));
            }
            else {
                stringBuilder.append("android.widget.");
                stringBuilder.append(element.getClassName());
            }

            stringBuilder.append(" ");
            stringBuilder.append(element.getFieldName());
            stringBuilder.append(";\n");

            if (firstNotStaticField == null) {
                targetClass.add(factory.createFieldFromText(stringBuilder.toString(), targetClass));
            }
            else {
                targetClass.addBefore(factory.createFieldFromText(stringBuilder.toString(), targetClass), firstNotStaticField);
            }
        }

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
        styleManager.optimizeImports(file);
        styleManager.shortenClassReferences(targetClass);
    }
}
