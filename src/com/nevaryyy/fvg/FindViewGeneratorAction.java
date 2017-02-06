package com.nevaryyy.fvg;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by waly6 on 2017/1/10.
 */
public class FindViewGeneratorAction extends BaseGenerateAction {

    private String layoutName;

    public FindViewGeneratorAction() {
        super(null);
    }

    public FindViewGeneratorAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    private void showMessage(String message) {
        Messages.showMessageDialog(message, "Message", Messages.getInformationIcon());
    }

    private boolean isClassValid(PsiClass psiClass) {
        String className = psiClass.getName();

        if (className != null && (className.endsWith("Activity") || className.endsWith("Fragment"))) {
            return true;
        }

        return false;
    }

    private String getRightLayoutName(PsiClass psiClass) {
        String className = psiClass.getName();

        if (className == null) {
            return null;
        }

        String[] pageWords = StringUtil.splitCamelCase(className);
        String pageNameWithUnderline = StringUtil.concatStringsWithUnderline(pageWords, pageWords.length - 1);
        String layoutName;

        if (className.endsWith("Activity")) {
            layoutName = "activity_";
        }
        else if (className.endsWith("Fragment")) {
            layoutName = "fragment_";
        }
        else {
            return null;
        }

        layoutName += pageNameWithUnderline;

        return layoutName;
    }

    private boolean isClassAndLayoutMatch(PsiClass psiClass, VirtualFile virtualFile) {
        String layoutName = getRightLayoutName(psiClass);

        if (layoutName == null) {
            return false;
        }

        return layoutName.equals(virtualFile.getNameWithoutExtension());
    }

    private List<ViewElement> getViewsFromLayout(PsiFile file) {
        List<ViewElement> elementList = new ArrayList<>();

        return getViewsFromLayout(file, elementList);
    }

    private List<ViewElement> getViewsFromLayout(PsiFile file, List<ViewElement> elementList) {
        file.accept(new XmlRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);

                if (element instanceof XmlTag) {
                    XmlTag tag = (XmlTag) element;

                    if (tag.getName().equalsIgnoreCase("include")) {
                        return;
                    }

                    XmlAttribute id = tag.getAttribute("android:id", null);

                    if (id == null) {
                        return;
                    }

                    String value = id.getValue();

                    if (value == null) {
                        return;
                    }

                    String name = tag.getName();
                    XmlAttribute clazz = tag.getAttribute("class", null);

                    if (clazz != null) {
                        name = clazz.getValue();
                    }

                    elementList.add(new ViewElement(name, value, layoutName));
                }
            }
        });

        return elementList;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass targetClass = getTargetClass(editor, file);

        if (!isClassValid(targetClass)) {
            showMessage("Current class is not an activity or a fragment.");
            return;
        }

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        String layoutDirPath = project.getBasePath() + "/app/src/main/res/layout";
        VirtualFile layoutDir = LocalFileSystem.getInstance().findFileByIoFile(new File(layoutDirPath));

        if (layoutDir == null) {
            showMessage("Cannot find directory: " + layoutDirPath);
            return;
        }

        VirtualFile[] layoutFiles = layoutDir.getChildren();
        VirtualFile layoutVirtualFile = null;

        for (VirtualFile virtualFile : layoutFiles) {
            if (isClassAndLayoutMatch(targetClass, virtualFile)) {
                layoutVirtualFile = virtualFile;
                break;
            }
        }

        if (layoutVirtualFile == null) {
            showMessage("Cannot find layout file: " + getRightLayoutName(targetClass) + ".xml");
            return;
        }

        layoutName = layoutVirtualFile.getNameWithoutExtension();

        PsiFile layoutFile = PsiManager.getInstance(project).findFile(layoutVirtualFile);
        List<ViewElement> elementList = getViewsFromLayout(layoutFile);

        new CodeCreator(project, targetClass, factory, elementList, file).execute();
    }
}
