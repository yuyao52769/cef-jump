package cn.yuyao.cefjump.handler;

import cn.yuyao.cefjump.constant.AnnoConstant;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yuyao
 * @create 2025/2/12
 */
public class ExtensionHandler extends AbstractAnnoHandler{
    public ExtensionHandler() {
        super(AnnoConstant.EXTENSION_ANNOTATION, Arrays.asList(AnnoConstant.OPEN_AFTER_ANNOTATION,
                AnnoConstant.OPEN_BEFORE_ANNOTATION,
                AnnoConstant.OPEN_REPLACE_ANNOTATION));
    }
    @Override
    Icon createIcon(PsiAnnotation annotation, String extensionKey) {
        return IconLoader.getIcon("/images/extend16.svg", getClass());
    }

    @Override
    public Boolean support(PsiMethod method) {
        PsiAnnotation annotation = method.getAnnotation(this.originalAnno);
        return annotation != null;
    }

    @Override
    protected LineMarkerInfo<?> createLineMarkerInfo(PsiAnnotation annotation, PsiMethod psiMethod, String extensionKey) {
        List<PsiElement> results = new ArrayList<>();
        Project project = psiMethod.getProject();
        // 创建一个包含项目和库的搜索范围
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        // 使用 JavaPsiFacade 查找特定的方法
        PsiMethod method = findMethodByQualifiedName(project, extensionKey, scope);
        if (method != null) {
            results.add(method);
        }

        if (results != null && results.size() > 0) {
            NavigationGutterIconBuilder<PsiElement> reBuilder = NavigationGutterIconBuilder.create(createIcon(annotation, extensionKey))
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTooltipText("Navigate to calling location");
            reBuilder.setTargets(results);
            return reBuilder.createLineMarkerInfo(annotation);
        }
        return null;
    }

    private static PsiMethod findMethodByQualifiedName(Project project, String qualifiedMethodName, GlobalSearchScope scope) {
        int lastDotIndex = qualifiedMethodName.lastIndexOf('.');

        if (lastDotIndex == -1) {
            return null;
        }
        String fullMethodName = qualifiedMethodName.substring(0, lastDotIndex);
        int secondDotIndex = fullMethodName.lastIndexOf('.');

        String className = fullMethodName.substring(0, secondDotIndex);
        String methodName = fullMethodName.substring(secondDotIndex + 1);

        // 查找类
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(className, scope);
        if (psiClass == null) {
            return null;
        }

        // 查找方法
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
        for (PsiMethod method : methods) {
            return method;
        }
        return null;
    }
}
